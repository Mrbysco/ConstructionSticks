package mrbysco.constructionstick.api;

import mrbysco.constructionstick.containers.ContainerTrace;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IContainerHandler {
	boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack);

	// Ender Chest is 0
	int getSignature(Player player, ItemStack inventoryStack);

	int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack);

	int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count);
}
