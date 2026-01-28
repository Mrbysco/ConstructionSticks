package mrbysco.constructionstick.network;

import mrbysco.constructionstick.ConstructionStick;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModMessages {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(ConstructionStick.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	private static int id = 0;

	public static void init() {
		CHANNEL.registerMessage(id++, PacketUndoBlocks.class, PacketUndoBlocks::encode, PacketUndoBlocks::decode, PacketUndoBlocks::handle);
		CHANNEL.registerMessage(id++, PacketQueryUndo.class, PacketQueryUndo::encode, PacketQueryUndo::decode, PacketQueryUndo::handle);
		CHANNEL.registerMessage(id++, PacketStickOption.class, PacketStickOption::encode, PacketStickOption::decode, PacketStickOption::handle);
	}

	public static <MSG> void sendToServer(MSG message) {
		CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
}
