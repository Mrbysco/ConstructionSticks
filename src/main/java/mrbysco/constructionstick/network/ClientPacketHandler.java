package mrbysco.constructionstick.network;

import mrbysco.constructionstick.client.ClientHandler;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;

/**
 * Client-side packet handler to prevent loading client classes on the server.
 * This class should only be loaded on the client via DistExecutor.
 */
@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
	public static void handleUndoBlocks(HashSet<BlockPos> undoBlocks) {
		if (ClientHandler.renderBlockPreview != null) {
			ClientHandler.renderBlockPreview.undoBlocks = undoBlocks;
		}
	}
}
