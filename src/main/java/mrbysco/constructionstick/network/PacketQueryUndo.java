package mrbysco.constructionstick.network;

import mrbysco.constructionstick.ConstructionStick;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketQueryUndo(boolean undoPressed) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, PacketQueryUndo> CODEC = CustomPacketPayload.codec(
			PacketQueryUndo::encode,
			PacketQueryUndo::new);
	public static final Type<PacketQueryUndo> ID = new Type<>(ConstructionStick.modLoc("query_undo"));

	public PacketQueryUndo(FriendlyByteBuf buf) {
		this(buf.readBoolean());
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(undoPressed);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public static class Handler {
		public static void handle(final PacketQueryUndo msg, final IPayloadContext ctx) {
			ctx.enqueueWork(() -> {
						if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
							ConstructionStick.undoHistory.updateClient(player, msg.undoPressed);
						}
					})
					.exceptionally(e -> {
						// Handle exception
						ctx.disconnect(Component.translatable("constructionstick.networking.query_undo.failed", e.getMessage()));
						return null;
					});
		}
	}
}
