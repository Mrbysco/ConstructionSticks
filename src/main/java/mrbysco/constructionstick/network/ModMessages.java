package mrbysco.constructionstick.network;

import mrbysco.constructionstick.ConstructionStick;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class ModMessages {
	private static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel CHANNEL;

	private static int id = 0;

	private static int nextId() {
		return id++;
	}

	public static void init() {
		// Create channel during init to ensure proper timing
		CHANNEL = NetworkRegistry.ChannelBuilder
				.named(new ResourceLocation(ConstructionStick.MOD_ID, "network"))
				.networkProtocolVersion(() -> PROTOCOL_VERSION)
				.clientAcceptedVersions(PROTOCOL_VERSION::equals)
				.serverAcceptedVersions(PROTOCOL_VERSION::equals)
				.simpleChannel();

		// PacketUndoBlocks: Server -> Client (sends undo block positions for preview rendering)
		CHANNEL.messageBuilder(PacketUndoBlocks.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
				.encoder(PacketUndoBlocks::encode)
				.decoder(PacketUndoBlocks::decode)
				.consumerMainThread(PacketUndoBlocks::handle)
				.add();

		// PacketQueryUndo: Client -> Server (client queries undo state)
		CHANNEL.messageBuilder(PacketQueryUndo.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
				.encoder(PacketQueryUndo::encode)
				.decoder(PacketQueryUndo::decode)
				.consumerMainThread(PacketQueryUndo::handle)
				.add();

		// PacketStickOption: Client -> Server (client sends stick option changes)
		CHANNEL.messageBuilder(PacketStickOption.class, nextId(), NetworkDirection.PLAY_TO_SERVER)
				.encoder(PacketStickOption::encode)
				.decoder(PacketStickOption::decode)
				.consumerMainThread(PacketStickOption::handle)
				.add();
	}

	public static <MSG> void sendToServer(MSG message) {
		CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
	}

	public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
}
