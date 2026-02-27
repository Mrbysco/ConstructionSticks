package mrbysco.constructionstick.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static mrbysco.constructionstick.ConstructionStick.MOD_ID;

public final class ModMessages {

	private ModMessages() {
	}

	public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(MOD_ID);

		registrar.playToClient(PacketUndoBlocks.ID, PacketUndoBlocks.CODEC, PacketUndoBlocks.Handler::handle);
		registrar.playToServer(PacketQueryUndo.ID, PacketQueryUndo.CODEC, PacketQueryUndo.Handler::handle);
		registrar.playToServer(PacketStickOption.ID, PacketStickOption.CODEC, PacketStickOption.Handler::handle);
		registrar.playToServer(PacketRequestPreview.ID, PacketRequestPreview.CODEC, PacketRequestPreview.Handler::handle);
		registrar.playToClient(PacketPreviewResult.ID, PacketPreviewResult.CODEC, PacketPreviewResult.Handler::handle);
	}

	public static void sendToServer(CustomPacketPayload message) {
		PacketDistributor.sendToServer(message);
	}

	public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
		player.connection.send(message);
	}
}
