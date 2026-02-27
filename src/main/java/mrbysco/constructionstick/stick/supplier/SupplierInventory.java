package mrbysco.constructionstick.stick.supplier;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickSupplier;
import mrbysco.constructionstick.basics.ReplacementRegistry;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.basics.pool.IPool;
import mrbysco.constructionstick.basics.pool.OrderedPool;
import mrbysco.constructionstick.containers.ContainerManager;
import mrbysco.constructionstick.containers.ContainerTrace;
import mrbysco.constructionstick.stick.undo.PlaceSnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Default StickSupplier. Takes items from player inventory.
 */
public class SupplierInventory implements IStickSupplier {
	protected final Player player;
	protected final StickOptions options;

	protected HashMap<BlockItem, Integer> itemCounts;
	protected IPool<BlockItem> itemPool;

	public SupplierInventory(Player player, StickOptions options) {
		this.player = player;
		this.options = options;
	}

	public void getSupply(@Nullable BlockItem target) {
		itemCounts = new LinkedHashMap<>();
		ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);

		itemPool = new OrderedPool<>();

		// Block in offhand -> override
		if (!offhandStack.isEmpty() && offhandStack.getItem() instanceof BlockItem) {
			addBlockItem((BlockItem) offhandStack.getItem());
		}
		// Otherwise use target block
		else if (target != null && target != Items.AIR) {
			addBlockItem(target);

			// Add replacement items
			if (options.match.get() != StickOptions.MATCH.EXACT) {
				for (Item it : ReplacementRegistry.getMatchingSet(target)) {
					if (it instanceof BlockItem) addBlockItem((BlockItem) it);
				}
			}
		}
	}

	protected void addBlockItem(BlockItem item) {
		int count = StickUtil.countItem(player, item);
		if (count > 0) {
			itemCounts.put(item, count);
			itemPool.add(item);
		}
	}

	@Override
	@Nullable
	public PlaceSnapshot getPlaceSnapshot(Level level, BlockPos pos, BlockHitResult blockHitResult,
	                                      @Nullable BlockState supportingBlock) {
		if (!StickUtil.isPositionPlaceable(level, player, pos, options.replace.get())) return null;
		itemPool.reset();

		while (true) {
			// Draw item from pool (returns null if none are left)
			BlockItem item = itemPool.draw();
			if (item == null) return null;

			int count = itemCounts.get(item);
			if (count == 0) continue;

			PlaceSnapshot placeSnapshot = PlaceSnapshot.get(level, player, blockHitResult, pos, item, supportingBlock, options);
			if (placeSnapshot != null) {
				int ncount = count - 1;
				itemCounts.put(item, ncount);

				// Remove item from pool if there are no items left
				if (ncount == 0) itemPool.remove(item);

				return placeSnapshot;
			}
		}
	}

	@Override
	public int takeItemStack(ItemStack stack) {
		int count = stack.getCount();
		Item item = stack.getItem();

		if (player.getInventory().items == null) return count;
		if (player.isCreative()) return 0;

		List<ItemStack> hotbar = StickUtil.getHotbarWithOffhand(player);
		List<ItemStack> mainInv = StickUtil.getMainInv(player);

		// Take items from main inv, loose items first
		count = takeItemsInvList(count, item, mainInv, false);
		count = takeItemsInvList(count, item, mainInv, true);

		// Take items from hotbar, containers first
		count = takeItemsInvList(count, item, hotbar, true);
		count = takeItemsInvList(count, item, hotbar, false);

		return count;
	}

	private int takeItemsInvList(int count, Item item, List<ItemStack> inv, boolean container) {
		if (count == 0) return 0;
		if (player instanceof ServerPlayer serverPlayer) {
			ContainerManager containerManager = ConstructionStick.containerManager;
			// In use, ContainerTrace is just a placeholder
			ContainerTrace trace = new ContainerTrace(serverPlayer);

			for (ItemStack stack : inv) {
				if (count == 0) break;

				if (container) {
					count = containerManager.useItems(serverPlayer, trace, new ItemStack(item), stack, count);
				}

				if (!container && StickUtil.stackEquals(stack, item)) {
					int toTake = Math.min(count, stack.getCount());
					stack.shrink(toTake);
					count -= toTake;
					serverPlayer.getInventory().setChanged();
				}
			}
		}
		return count;
	}
}
