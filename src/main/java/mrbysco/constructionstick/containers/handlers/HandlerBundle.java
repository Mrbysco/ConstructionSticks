package mrbysco.constructionstick.containers.handlers;

import mrbysco.constructionstick.api.IContainerHandler;
import mrbysco.constructionstick.basics.StickUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class HandlerBundle implements IContainerHandler {
	@Override
	public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
		return inventoryStack != null && inventoryStack.getCount() == 1 && inventoryStack.getItem() == Items.BUNDLE;
	}

	@Override
	public int countItems(Player player, ItemStack itemStack, ItemStack inventoryStack) {
		return getContents(inventoryStack).filter((stack) -> StickUtil.stackEquals(stack, itemStack))
				.map(ItemStack::getCount).reduce(0, Integer::sum);
	}

	@Override
	public int useItems(Player player, ItemStack itemStack, ItemStack inventoryStack, int count) {
		AtomicInteger newCount = new AtomicInteger(count);

		List<ItemStack> itemStacks = getContents(inventoryStack).filter((stack -> {
			if (StickUtil.stackEquals(stack, itemStack)) {
				int toTake = Math.min(newCount.get(), stack.getCount());
				stack.shrink(toTake);
				newCount.set(newCount.get() - toTake);
			}
			return !stack.isEmpty();
		})).toList();

		setItemList(inventoryStack, itemStacks);

		return newCount.get();
	}

	private Stream<ItemStack> getContents(ItemStack bundleStack) {
		BundleContents contents = bundleStack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
		return contents.itemCopyStream();
	}

	private void setItemList(ItemStack itemStack, List<ItemStack> itemStacks) {
		BundleContents contents = new BundleContents(itemStacks);
		itemStack.set(DataComponents.BUNDLE_CONTENTS, contents);
	}
}
