package mrbysco.constructionstick.network;

import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.IOption;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.items.stick.ItemStick;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PacketStickOption(String key, String value, boolean notifyMessage) {
	public static PacketStickOption decode(FriendlyByteBuf buffer) {
		return new PacketStickOption(buffer.readUtf(100), buffer.readUtf(100), buffer.readBoolean());
	}

	public PacketStickOption(IOption<?> option, boolean notify) {
		this(option.getKey(), option.getValueString(), notify);
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(key);
		buffer.writeUtf(value);
		buffer.writeBoolean(notifyMessage);
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
				ServerPlayer player = ctx.getSender();
				ItemStack stick = StickUtil.holdingStick(player);
				if (stick == null) return;
				StickOptions options = new StickOptions(stick);

				IOption<?> option = options.get(key);
				if (option == null) return;
				option.setValueString(value);

				if (notifyMessage) ItemStick.optionMessage(player, option);
				player.getInventory().setChanged();
			}
		});
		ctx.setPacketHandled(true);
	}
}
