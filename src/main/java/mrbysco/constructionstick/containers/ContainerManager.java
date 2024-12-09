package mrbysco.constructionstick.containers;

import mrbysco.constructionstick.api.IContainerHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ContainerManager {
	private final List<IContainerHandler> handlers;

	public ContainerManager() {
		handlers = new ArrayList<IContainerHandler>();
	}

	public boolean register(IContainerHandler handler) {
		return handlers.add(handler);
	}

	public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
		for (IContainerHandler handler : handlers) {
			if (handler.matches(player, itemStack, inventoryStack)) {
				return handler.countItems(player, itemStack, inventoryStack);
			}
		}
		return 0;
	}

	public int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count) {
		for (IContainerHandler handler : handlers) {
			if (handler.matches(player, itemStack, inventoryStack)) {
				return handler.useItems(player, itemStack, inventoryStack, count);
			}
		}
		return count;
	}
}