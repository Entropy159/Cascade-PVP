package dev.entropy159.cascadepvp.network.toServer;

import dev.entropy159.cascadepvp.CascadePVP;
import dev.entropy159.cascadepvp.items.CascadeItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UtilityPacket() implements CustomPacketPayload {
    public static final Type<UtilityPacket> TYPE = new Type<>(CascadePVP.id("utility"));
    public static final StreamCodec<ByteBuf, UtilityPacket> STREAM_CODEC = StreamCodec.unit(new UtilityPacket());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer player && player.getMainHandItem().getItem() instanceof CascadeItem item) {
            item.utilityServer(player, player.getMainHandItem());
        }
    }
}
