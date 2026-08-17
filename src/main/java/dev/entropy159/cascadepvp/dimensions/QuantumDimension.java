package dev.entropy159.cascadepvp.dimensions;

import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.cascadepvp.client.ClientData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import org.joml.Vector2i;

import java.util.Optional;
import java.util.function.Function;

public class QuantumDimension {
    public static final ResourceKey<Level> QUANTUM = ResourceKey.create(Registries.DIMENSION, CascadePVP.id("quantum"));

    public static boolean teleportTo(ServerPlayer player, BlockPos overworld, boolean platform) {
        ServerLevel quantum = player.serverLevel().getServer().getLevel(QUANTUM);
        if (quantum == null) {
            CascadePVP.LOGGER.error("No quantum dimension found!");
            return false;
        }
        var pos = toQuantum(player.serverLevel(), overworld).getBottomCenter();
        if (platform) {
            BlockPos block = BlockPos.containing(pos);
            if (quantum.getBlockState(block.below()).isAir()) {
                quantum.setBlock(block.below(), Blocks.CRYING_OBSIDIAN.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            }
            if (quantum.getBlockState(block.above()).is(Blocks.OBSIDIAN)) {
                quantum.setBlock(block.above(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL_IMMEDIATE);
            }
        }
        player.teleportTo(quantum, pos.x, pos.y, pos.z, player.getYRot(), player.getXRot());
        return true;
    }

    public static boolean teleportTo(ServerPlayer player, boolean platform) {
        return teleportTo(player, player.blockPosition(), platform);
    }

    public static boolean teleportFrom(ServerPlayer player, BlockPos quantum) {
        var pos = fromQuantum(player.serverLevel(), quantum).getBottomCenter();
        player.teleportTo(player.server.overworld(), pos.x, pos.y, pos.z, player.getYRot(), player.getXRot());
        return true;
    }

    public static boolean teleportFrom(ServerPlayer player) {
        return teleportFrom(player, player.blockPosition());
    }

    public static BlockPos toQuantum(Level level, BlockPos pos) {
        var converter = new CoordinateTransformer(level);
        return converter.toQuantum(pos);
    }

    public static BlockPos fromQuantum(Level level, BlockPos pos) {
        var converter = new CoordinateTransformer(level);
        return converter.fromQuantum(pos);
    }

    public static BlockPos convert(Level level, BlockPos pos) {
        if (level.dimension().equals(Level.OVERWORLD)) {
            return toQuantum(level, pos);
        }
        if (level.dimension().equals(QUANTUM)) {
            return fromQuantum(level, pos);
        }
        return pos;
    }

    public static GlobalPos convert(Level level, GlobalPos pos) {
        if (pos.dimension().equals(Level.OVERWORLD)) {
            return new GlobalPos(QUANTUM, toQuantum(level, pos.pos()));
        }
        if (pos.dimension().equals(QUANTUM)) {
            return new GlobalPos(Level.OVERWORLD, fromQuantum(level, pos.pos()));
        }
        return pos;
    }

    private record CoordinateTransformer(CoordinateDomain coordDomain, long seed) {
        private static final int FEISTEL_ROUNDS = 8;
        private static final int MAX_CYCLE_WALK_ITERATIONS = 256;
        private static final long GOLDEN_RATIO = 0x9E3779B97F4A7C15L;
        private static final long SEED_MIX = 0xD1B54A32D192ED03L;

        public CoordinateTransformer(Level source) {
            this(source, deriveSeed(source));
        }

        public CoordinateTransformer(Level source, long seed) {
            this(CoordinateDomain.fromWorldBorder(source.getWorldBorder()), seed);
        }

        public BlockPos toQuantum(BlockPos sourcePos) {
            var point = toQuantum(new Vector2i(sourcePos.getX(), sourcePos.getZ()));
            return new BlockPos(point.x, sourcePos.getY(), point.y);
        }

        public Vector2i toQuantum(Vector2i sourcePos) {
            long sourceIndex = coordDomain.indexOf(sourcePos.x, sourcePos.y);
            long targetIndex = permute(sourceIndex, coordDomain.positionCount(), seed);
            int targetX = coordDomain.xFromIndex(targetIndex);
            int targetZ = coordDomain.zFromIndex(targetIndex);
            return new Vector2i(targetX, targetZ);
        }

        public BlockPos fromQuantum(BlockPos targetPos) {
            var point = fromQuantum(new Vector2i(targetPos.getX(), targetPos.getZ()));
            return new BlockPos(point.x, targetPos.getY(), point.y);
        }

        public Vector2i fromQuantum(Vector2i targetPos) {
            long targetIndex = coordDomain.indexOf(targetPos.x, targetPos.y);
            long sourceIndex = inversePermute(targetIndex, coordDomain.positionCount(), seed);

            int sourceX = coordDomain.xFromIndex(sourceIndex);
            int sourceZ = coordDomain.zFromIndex(sourceIndex);

            return new Vector2i(sourceX, sourceZ);
        }

        private static long deriveSeed(Level source) {
            long result = source instanceof ServerLevel level ? level.getSeed() : Optional.ofNullable(ClientData.SEED).orElse(0L);

            result ^= (long) Level.OVERWORLD.location().hashCode() * GOLDEN_RATIO;
            result ^= (long) QUANTUM.location().hashCode() * SEED_MIX;

            return mix64(result);
        }

        private static long permute(long value, long domainSize, long seed) {
            int bits = requiredFeistelBits(domainSize);

            long result = value;

            for (int i = 0; i < MAX_CYCLE_WALK_ITERATIONS; i++) {
                result = feistel(result, seed, bits);

                if (result < domainSize) {
                    return result;
                }
            }

            throw new IllegalStateException("Feistel cycle walking exceeded " + MAX_CYCLE_WALK_ITERATIONS + " iterations. Domain size=" + domainSize + ", bits=" + bits);
        }

        private static long inversePermute(long value, long domainSize, long seed) {
            int bits = requiredFeistelBits(domainSize);

            long result = value;

            for (int i = 0; i < MAX_CYCLE_WALK_ITERATIONS; i++) {
                result = inverseFeistel(result, seed, bits);

                if (result < domainSize) {
                    return result;
                }
            }

            throw new IllegalStateException("Inverse Feistel cycle walking exceeded " + MAX_CYCLE_WALK_ITERATIONS + " iterations. Domain size=" + domainSize + ", bits=" + bits);
        }

        private static int requiredFeistelBits(long domainSize) {
            if (domainSize <= 1) {
                return 2;
            }

            int bits = 64 - Long.numberOfLeadingZeros(domainSize - 1);

            if ((bits & 1) != 0) {
                bits++;
            }

            if (bits > 62) {
                throw new IllegalArgumentException("Coordinate domain is too large for this transformer: " + domainSize);
            }

            return bits;
        }

        private static long feistel(long value, long seed, int bits) {
            int halfBits = bits / 2;

            long mask = (1L << halfBits) - 1L;

            long left = (value >>> halfBits) & mask;
            long right = value & mask;

            for (int round = 0; round < FEISTEL_ROUNDS; round++) {
                long roundKey = seed + GOLDEN_RATIO * (round + 1L) + SEED_MIX;

                long function = mix64(right ^ roundKey ^ ((long) round * SEED_MIX)) & mask;

                long newLeft = right;
                long newRight = (left ^ function) & mask;

                left = newLeft;
                right = newRight;
            }

            return (left << halfBits) | right;
        }

        private static long inverseFeistel(long value, long seed, int bits) {
            int halfBits = bits / 2;
            long mask = (1L << halfBits) - 1L;

            long left = (value >>> halfBits) & mask;
            long right = value & mask;

            for (int round = FEISTEL_ROUNDS - 1; round >= 0; round--) {
                long roundKey = seed + GOLDEN_RATIO * (round + 1L) + SEED_MIX;

                long function = mix64(left ^ roundKey ^ ((long) round * SEED_MIX));

                long previousRight = left;

                left = (right ^ function) & mask;
                right = previousRight;
            }

            return (left << halfBits) | right;
        }

        private static long mix64(long value) {
            value ^= value >>> 30;
            value *= 0xBF58476D1CE4E5B9L;

            value ^= value >>> 27;
            value *= 0x94D049BB133111EBL;

            value ^= value >>> 31;

            return value;
        }

        public record CoordinateDomain(int minX, int maxX, int minZ, int maxZ) {
            public static CoordinateDomain fromWorldBorder(WorldBorder border) {
                double centerX = border.getCenterX();
                double centerZ = border.getCenterZ();
                double size = border.getSize();

                if (!Double.isFinite(centerX) || !Double.isFinite(centerZ) || !Double.isFinite(size) || size <= 0.0) {
                    throw new IllegalArgumentException("Invalid WorldBorder: center=(" + centerX + ", " + centerZ + "), size=" + size);
                }

                double halfSize = size / 2.0;

                long minX = (long) Math.ceil(centerX - halfSize);
                long maxX = (long) Math.ceil(centerX + halfSize) - 1L;
                long minZ = (long) Math.ceil(centerZ - halfSize);
                long maxZ = (long) Math.ceil(centerZ + halfSize) - 1L;

                if (minX < Integer.MIN_VALUE || maxX > Integer.MAX_VALUE || minZ < Integer.MIN_VALUE || maxZ > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("WorldBorder exceeds Minecraft's " + "integer block-coordinate range");
                }

                if (minX > maxX || minZ > maxZ) {
                    throw new IllegalArgumentException("WorldBorder contains no integer coordinates");
                }

                return new CoordinateDomain((int) minX, (int) maxX, (int) minZ, (int) maxZ);
            }

            /**
             * Number of valid X coordinates.
             */
            public long width() {
                return (long) maxX - (long) minX + 1L;
            }

            /**
             * Number of valid Z coordinates.
             */
            public long depth() {
                return (long) maxZ - (long) minZ + 1L;
            }

            /**
             * Total number of valid X/Z coordinate pairs.
             */
            public long positionCount() {
                if (width() > Long.MAX_VALUE / depth()) {
                    throw new IllegalStateException("WorldBorder coordinate domain is too large");
                }

                return width() * depth();
            }

            /**
             * Converts an X/Z coordinate into a contiguous domain index.
             *
             * <p>Index 0 corresponds to (minX, minZ).
             */
            public long indexOf(int x, int z) {
                if (!contains(x, z)) {
                    throw new IllegalArgumentException("Coordinate (" + x + ", " + z + ") is outside WorldBorder domain " + this);
                }

                long normalizedX = (long) x - minX;
                long normalizedZ = (long) z - minZ;
                return normalizedX * depth() + normalizedZ;
            }

            /**
             * Gets the X coordinate represented by an index.
             */
            public int xFromIndex(long index) {
                long x = index / depth();

                return Math.toIntExact((long) minX + x);
            }

            /**
             * Gets the Z coordinate represented by an index.
             */
            public int zFromIndex(long index) {
                long z = index % depth();

                return Math.toIntExact((long) minZ + z);
            }

            /**
             * Tests whether a coordinate is inside the domain.
             */
            public boolean contains(int x, int z) {
                return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
            }
        }
    }

    public static class Route {
        public final Vector2i overworldStart;
        public final Vector2i overworldEnd;
        private final Function<Vector2i, Vector2i> mapper;

        public Route(Vector2i start, Vector2i end, Function<Vector2i, Vector2i> mapper) {
            overworldStart = start;
            overworldEnd = end;
            this.mapper = mapper;
        }

        public long totalDistance(Vector2i start, Vector2i end) {
            return Math.round(start.distance(overworldStart) + quantumStart().distance(quantumEnd()) + overworldEnd.distance(end));
        }

        public long totalDistance(ColumnPos start, ColumnPos end) {
            return totalDistance(new Vector2i(start.x(), start.z()), new Vector2i(end.x(), end.z()));
        }

        public long totalDistance(BlockPos start, BlockPos end) {
            return totalDistance(new Vector2i(start.getX(), start.getZ()), new Vector2i(end.getX(), end.getZ()));
        }

        public Vector2i quantumStart() {
            return mapper.apply(overworldStart);
        }

        public Vector2i quantumEnd() {
            return mapper.apply(overworldEnd);
        }

        public Component toComponent(Vector2i start, Vector2i end) {
            var text = coords(start, ChatFormatting.AQUA);
            var middle = Component.literal(" -> ").withStyle(ChatFormatting.GRAY);
            text.append(middle);
            text.append(coords(overworldStart, ChatFormatting.GREEN));
            text.append(middle);
            text.append(coords(quantumStart(), ChatFormatting.DARK_PURPLE));
            text.append(middle);
            text.append(coords(quantumEnd(), ChatFormatting.DARK_PURPLE));
            text.append(middle);
            text.append(coords(overworldEnd, ChatFormatting.GREEN));
            text.append(middle);
            text.append(coords(end, ChatFormatting.AQUA));
            return text;
        }

        public Component toComponent(ColumnPos start, ColumnPos end) {
            return toComponent(new Vector2i(start.x(), start.z()), new Vector2i(end.x(), end.z()));
        }

        private MutableComponent coords(Vector2i pos, ChatFormatting color) {
            return Component.literal(pos.x + ", " + pos.y).withStyle(color);
        }
    }

    public static Route findBestRoute(MinecraftServer server, BlockPos start, BlockPos end, int radius) {
        return findBestRoute(server, new Vector2i(start.getX(), start.getZ()), new Vector2i(end.getX(), end.getZ()), radius);
    }

    public static Route findBestRoute(MinecraftServer server, ColumnPos start, ColumnPos end, int radius) {
        return findBestRoute(server, new Vector2i(start.x(), start.z()), new Vector2i(end.x(), end.z()), radius);
    }

    public static Route findBestRoute(MinecraftServer server, Vector2i startPos, Vector2i endPos, int radius) {
        var overworld = server.overworld();
        var mapper = new CoordinateTransformer(overworld);
        Route bestRoute = new Route(startPos, endPos, mapper::toQuantum);
        var bestDistance = bestRoute.totalDistance(startPos, endPos);
        for (int startdx = -radius; startdx <= radius; startdx++) {
            for (int startdz = -radius; startdz <= radius; startdz++) {
                for (int enddx = -radius; enddx <= radius; enddx++) {
                    for (int enddz = -radius; enddz <= radius; enddz++) {
                        Vector2i start = new Vector2i(startPos.x + startdx, startPos.y + startdz);
                        Vector2i end = new Vector2i(endPos.x + enddx, endPos.y + enddz);
                        Route route = new Route(start, end, mapper::toQuantum);
                        var distance = route.totalDistance(startPos, endPos);
                        if (distance < bestDistance) {
                            bestDistance = distance;
                            bestRoute = route;
                        }
                    }
                }
            }
        }
        return bestRoute;
    }
}
