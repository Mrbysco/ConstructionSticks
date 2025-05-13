package mrbysco.constructionstick.containers.handlers;

import mrbysco.constructionstick.api.IContainerHandler;
import mrbysco.constructionstick.basics.StickUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class HandlerShulkerbox implements IContainerHandler {
	private final int SLOTS = 27;

	@Override
	public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
		return inventoryStack != null && inventoryStack.getCount() == 1 && Block.byItem(inventoryStack.getItem()) instanceof ShulkerBoxBlock;
	}

	@Override
	public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
		int count = 0;

		for (ItemStack stack : getItemList(inventoryStack)) {
			if (StickUtil.stackEquals(stack, itemStack)) count += stack.getCount();
		}

		return count;
	}

	@Override
	public int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count) {
		NonNullList<ItemStack> itemList = getItemList(inventoryStack);
		boolean changed = false;

		for (ItemStack stack : itemList) {
			if (StickUtil.stackEquals(stack, itemStack)) {
				int toTake = Math.min(count, stack.getCount());
				stack.shrink(toTake);
				count -= toTake;
				changed = true;
				if (count == 0) break;
			}
		}
		if (changed) {
			setItemList(inventoryStack, itemList);
			player.getInventory().setChanged();
		}

		return count;
	}

	private NonNullList<ItemStack> getItemList(ItemStack itemStack) {
		NonNullList<ItemStack> itemStacks = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
		ItemContainerContents contents = itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
		contents.stream().forEach(stack -> {
			if (stack != null && !stack.isEmpty()) {
				itemStacks.add(stack);
			}
		});
		return itemStacks;
	}

	private void setItemList(ItemStack itemStack, NonNullList<ItemStack> itemStacks) {
		itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(itemStacks));
	}
}
