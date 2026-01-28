package mrbysco.constructionstick.network;

import mrbysco.constructionstick.ConstructionStick;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PacketQueryUndo(boolean undoPressed) {
	public static PacketQueryUndo decode(final FriendlyByteBuf packetBuffer) {
		return new PacketQueryUndo(packetBuffer.readBoolean());
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(undoPressed);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayer player = ctx.getSender();
				ConstructionStick.undoHistory.updateClient(player, undoPressed);
			}
		});
		ctx.setPacketHandled(true);
	}
}
