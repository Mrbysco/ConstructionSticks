package mrbysco.constructionstick.network;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.IOption;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.items.stick.ItemStick;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketStickOption(String key, String value, boolean notifyMessage) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, PacketStickOption> CODEC = CustomPacketPayload.codec(
			PacketStickOption::encode,
			PacketStickOption::new);
	public static final Type<PacketStickOption> ID = new Type<>(ConstructionStick.modLoc("stick_option"));

	private PacketStickOption(FriendlyByteBuf buffer) {
		this(buffer.readUtf(100), buffer.readUtf(100), buffer.readBoolean());
	}

	public PacketStickOption(IOption<?> option, boolean notify) {
		this(option.getKey(), option.getValueString(), notify);
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(key);
		buffer.writeUtf(value);
		buffer.writeBoolean(notifyMessage);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	public static class Handler {
		public static void handle(final PacketStickOption msg, final IPayloadContext ctx) {
			ctx.enqueueWork(() -> {
						if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
							ItemStack stick = StickUtil.holdingStick(player);
							if (stick == null) return;
							StickOptions options = new StickOptions(stick);

							IOption<?> option = options.get(msg.key);
							if (option == null) return;
							option.setValueString(msg.value);

							if (msg.notifyMessage) ItemStick.optionMessage(player, option);
							player.getInventory().setChanged();
						}
					})
					.exceptionally(e -> {
						// Handle exception
						ctx.disconnect(Component.translatable("constructionstick.networking.stick_option.failed", e.getMessage()));
						return null;
					});
		}
	}
}
