package mrbysco.constructionstick.network;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.client.ClientHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

public record PacketUndoBlocks(HashSet<BlockPos> undoBlocks) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, PacketUndoBlocks> CODEC = CustomPacketPayload.codec(
			PacketUndoBlocks::encode,
			PacketUndoBlocks::new);
	public static final Type<PacketUndoBlocks> ID = new Type<>(ConstructionStick.modLoc("undo_blocks"));

	public PacketUndoBlocks(FriendlyByteBuf buffer) {
		this(getSet(buffer));
	}

	public PacketUndoBlocks(Set<BlockPos> undoBlocks) {
		this(new HashSet<>(undoBlocks));
	}

	private static HashSet<BlockPos> getSet(FriendlyByteBuf buffer) {
		HashSet<BlockPos> undoBlocks = new HashSet<>();

		while (buffer.isReadable()) {
			undoBlocks.add(buffer.readBlockPos());
		}
		return undoBlocks;
	}

	public void encode(FriendlyByteBuf buffer) {
		for (BlockPos pos : undoBlocks) {
			buffer.writeBlockPos(pos);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public static class Handler {
		public static void handle(final PacketUndoBlocks msg, final IPayloadContext ctx) {
			ctx.enqueueWork(() -> {
						//ConstructionStick.LOGGER.debug("PacketUndoBlocks received, Blocks: " + msg.undoBlocks.size());
						ClientHandler.renderBlockPreview.undoBlocks = msg.undoBlocks;
					})
					.exceptionally(e -> {
						// Handle exception
						ctx.disconnect(Component.translatable("constructionstick.networking.undo_blocks.failed", e.getMessage()));
						return null;
					});

		}
	}
}
