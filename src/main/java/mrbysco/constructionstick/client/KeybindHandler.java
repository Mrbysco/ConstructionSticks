package mrbysco.constructionstick.client;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.network.ModMessages;
import mrbysco.constructionstick.network.PacketQueryUndo;
import mrbysco.constructionstick.network.PacketStickOption;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {
	public static final KeyMapping KEY_CHANGE_RESTRICTION = new KeyMapping(getKey("change_restriction"), GLFW.GLFW_KEY_UNKNOWN, getKey("category"));
	public static final KeyMapping KEY_CHANGE_UPGRADE = new KeyMapping(getKey("change_upgrade"), GLFW.GLFW_KEY_UNKNOWN, getKey("category"));
	public static final KeyMapping KEY_CHANGE_DIRECTION = new KeyMapping(getKey("change_direction"), GLFW.GLFW_KEY_UNKNOWN, getKey("category"));
	public static final KeyMapping KEY_OPEN_GUI = new KeyMapping(getKey("open_gui"), GLFW.GLFW_KEY_UNKNOWN, getKey("category"));
	public static final KeyMapping KEY_UNDO = new KeyMapping(getKey("undo"), GLFW.GLFW_KEY_UNKNOWN, getKey("category"));
	public static final KeyMapping KEY_SHOW_PREVIOUS = new KeyMapping(getKey("show_previous"), GLFW.GLFW_KEY_UNKNOWN, getKey("category"));

	private static String getKey(String name) {
		return String.join(".", "key", ConstructionStick.MOD_ID, name);
	}

	private boolean optPressed;

	public KeybindHandler() {
		optPressed = false;
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent.Post event) {
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
		ItemStack stick = player.getItemInHand(player.getUsedItemHand());
		if (!(stick.getItem() instanceof ItemStick)) return;

		boolean undoDown = KEY_UNDO.consumeClick();
		if (optPressed != undoDown) {
			optPressed = undoDown;
			ModMessages.sendToServer(new PacketQueryUndo(optPressed));
		}

		if (KEY_CHANGE_UPGRADE.consumeClick()) {
			StickOptions stickOptions = new StickOptions(stick);
			stickOptions.upgrades.next();
			ModMessages.sendToServer(new PacketStickOption(stickOptions.upgrades, true));
		}
		if (KEY_CHANGE_RESTRICTION.consumeClick()) {
			StickOptions stickOptions = new StickOptions(stick);
			stickOptions.lock.next();
			ModMessages.sendToServer(new PacketStickOption(stickOptions.lock, true));
		}
		if (KEY_CHANGE_DIRECTION.consumeClick()) {
			StickOptions stickOptions = new StickOptions(stick);
			stickOptions.direction.next();
			ModMessages.sendToServer(new PacketStickOption(stickOptions.direction, true));
		}

		if (KEY_OPEN_GUI.consumeClick()) {
			Minecraft.getInstance().setScreen(new StickScreen(stick));
		}
	}

}
