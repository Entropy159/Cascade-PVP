package dev.entropy159.cascadepvp.network.toClient;

import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.cascadepvp.client.ClientData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record WorldSeedPacket(long seed) implements CustomPacketPayload {
    public static final Type<WorldSeedPacket> TYPE = new Type<>(CascadePVP.id("seed"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WorldSeedPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_LONG, WorldSeedPacket::seed, WorldSeedPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ClientData.SEED = seed;
    }
}
