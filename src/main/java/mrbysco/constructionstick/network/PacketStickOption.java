package mrbysco.constructionstick.network;

import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.IOption;
import mrbysco.constructionstick.basics.option.StickOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
		// consumerMainThread already runs on main thread
		if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
			ServerPlayer player = ctx.getSender();
			ItemStack stick = StickUtil.holdingStick(player);
			if (stick == null) return;
			StickOptions options = new StickOptions(stick);

			IOption<?> option = options.get(key);
			if (option == null) return;
			option.setValueString(value);

			if (notifyMessage) sendOptionMessage(player, option);
			player.getInventory().setChanged();
		}
		ctx.setPacketHandled(true);
	}

	/**
	 * Send option change message to player.
	 * Inlined here to avoid importing ItemStick which causes class loading issues.
	 */
	private static void sendOptionMessage(Player player, IOption<?> option) {
		player.displayClientMessage(
				Component.translatable(option.getKeyTranslation()).withStyle(ChatFormatting.AQUA)
						.append(Component.translatable(option.getValueTranslation()).withStyle(ChatFormatting.WHITE))
						.append(Component.literal(" - ").withStyle(ChatFormatting.GRAY))
						.append(Component.translatable(option.getDescTranslation()).withStyle(ChatFormatting.WHITE))
				, true);
	}
}
