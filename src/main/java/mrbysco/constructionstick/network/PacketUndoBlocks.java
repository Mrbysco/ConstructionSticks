package mrbysco.constructionstick.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public record PacketUndoBlocks(HashSet<BlockPos> undoBlocks) {
	public PacketUndoBlocks(Set<BlockPos> undoBlocks) {
		this(new HashSet<>(undoBlocks));
	}

	public static PacketUndoBlocks decode(final FriendlyByteBuf packetBuffer) {
		return new PacketUndoBlocks(getSet(packetBuffer));
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

	public void handle(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		if (ctx.getDirection().getReceptionSide().isClient()) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleUndoBlocks(undoBlocks));
		}
		ctx.setPacketHandled(true);
	}
}
