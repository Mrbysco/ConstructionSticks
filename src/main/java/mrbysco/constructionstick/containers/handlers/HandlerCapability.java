package mrbysco.constructionstick.containers.handlers;

import mrbysco.constructionstick.api.IContainerHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class HandlerCapability implements IContainerHandler {
	@Override
	public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
		return inventoryStack != null && inventoryStack.getCapability(Capabilities.Item.ITEM, null) != null;
	}

	@Override
	public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
		ResourceHandler<ItemResource> resourceHandler = inventoryStack.getCapability(Capabilities.Item.ITEM, null);
		if (resourceHandler == null) return 0;

		int total = 0;

		for (int i = 0; i < resourceHandler.size(); i++) {
			ItemResource containerResource = resourceHandler.getResource(i);
			if (!containerResource.isEmpty() && containerResource.matches(itemStack)) {
				total += Math.max(0, resourceHandler.getAmountAsInt(i));
			}
		}
		return total;
	}

	@Override
	public int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count) {
		ResourceHandler<ItemResource> resourceHandler = inventoryStack.getCapability(Capabilities.Item.ITEM, null);
		if (resourceHandler == null) return 0;

		try (var tx = Transaction.openRoot()) {
			int initialCount = count;
			for (int i = 0; i < resourceHandler.size(); i++) {
				ItemResource handlerResource = resourceHandler.getResource(i);
				if (!handlerResource.isEmpty() && handlerResource.matches(itemStack)) {
					int extracted = resourceHandler.extract(i, handlerResource, count, tx);
					if (extracted > 0) {
						count -= extracted;
						if (count <= 0) break;
					}
				}
			}
			if (initialCount != count) {
				tx.commit();
			}
			return count;
		}
	}
}
