package dev.entropy159.cascadepvp.network;

import dev.entropy159.cascadepvp.network.toClient.WorldSeedPacket;
import dev.entropy159.cascadepvp.network.toServer.AbilityPacket;
import dev.entropy159.cascadepvp.network.toServer.UtilityPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber
public class CascadeNetworking {
    @SubscribeEvent
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        final var registrar = event.registrar("1");

        registrar.playToClient(WorldSeedPacket.TYPE, WorldSeedPacket.STREAM_CODEC, WorldSeedPacket::handle);

        registrar.playToServer(AbilityPacket.TYPE, AbilityPacket.STREAM_CODEC, AbilityPacket::handle);
        registrar.playToServer(UtilityPacket.TYPE, UtilityPacket.STREAM_CODEC, UtilityPacket::handle);
    }
}
