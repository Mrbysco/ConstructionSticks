package mrbysco.constructionstick.client;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.network.PacketQueryUndo;
import mrbysco.constructionstick.network.PacketStickOption;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
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


	public KeybindHandler() {
		undoPressed = false;
	}

	private boolean undoPressed;

	@SubscribeEvent
	public void KeyEvent(InputEvent.Key event) {
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
		if (StickUtil.holdingStick(player) == null) return;

		boolean undoDown = KEY_UNDO.isDown();
		if (undoPressed != undoDown) {
			undoPressed = undoDown;
			ClientPacketDistributor.sendToServer(new PacketQueryUndo(undoPressed));
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent.Post event) {
		Player player = Minecraft.getInstance().player;
		if (player == null) return;
		if (StickUtil.holdingStick(player) == null) return;
		ItemStack stick = player.getItemInHand(player.getUsedItemHand());

		if (KEY_CHANGE_UPGRADE.consumeClick()) {
			StickOptions stickOptions = new StickOptions(stick);
			stickOptions.upgrades.next();
			ClientPacketDistributor.sendToServer(new PacketStickOption(stickOptions.upgrades, true));
		}
		if (KEY_CHANGE_RESTRICTION.consumeClick()) {
			StickOptions stickOptions = new StickOptions(stick);
			stickOptions.lock.next();
			ClientPacketDistributor.sendToServer(new PacketStickOption(stickOptions.lock, true));
		}
		if (KEY_CHANGE_DIRECTION.consumeClick()) {
			StickOptions stickOptions = new StickOptions(stick);
			stickOptions.direction.next();
			ClientPacketDistributor.sendToServer(new PacketStickOption(stickOptions.direction, true));
		}

		if (KEY_OPEN_GUI.consumeClick()) {
			Minecraft.getInstance().setScreen(new StickScreen(stick));
		}
	}

}
