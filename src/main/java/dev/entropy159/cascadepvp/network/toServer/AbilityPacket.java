package dev.entropy159.cascadepvp.network.toServer;

import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.cascadepvp.items.CascadeItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AbilityPacket(int entityID) implements CustomPacketPayload {
    public static final Type<AbilityPacket> TYPE = new Type<>(CascadePVP.id("ability"));
    public static final StreamCodec<ByteBuf, AbilityPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, AbilityPacket::entityID, AbilityPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer player) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof CascadeItem item) {
                item.onUseAbility(player, entityID);
            }
        }
    }
}
