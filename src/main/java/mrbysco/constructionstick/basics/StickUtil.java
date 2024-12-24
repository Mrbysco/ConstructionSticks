package mrbysco.constructionstick.basics;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickTemplate;
import mrbysco.constructionstick.api.IStickUpgrade;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.containers.ContainerManager;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.stick.StickItemUseContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class StickUtil {
	public static boolean stackEquals(ItemStack stackA, ItemStack stackB) {
		return ItemStack.isSameItemSameComponents(stackA, stackB);
	}

	public static boolean stackEquals(ItemStack stackA, Item item) {
		ItemStack stackB = new ItemStack(item);
		return stackEquals(stackA, stackB);
	}

	public static ItemStack holdingStick(Player player) {
		if (player.getItemInHand(InteractionHand.MAIN_HAND) != ItemStack.EMPTY && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemStick) {
			return player.getItemInHand(InteractionHand.MAIN_HAND);
		} else if (player.getItemInHand(InteractionHand.OFF_HAND) != ItemStack.EMPTY && player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ItemStick) {
			return player.getItemInHand(InteractionHand.OFF_HAND);
		}
		return null;
	}

	public static BlockPos posFromVec(Vec3 vec) {
		return new BlockPos(
				(int) Math.round(vec.x), (int) Math.round(vec.y), (int) Math.round(vec.z));
	}

	public static Vec3 entityPositionVec(Entity entity) {
		return new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ());
	}

	public static Vec3 blockPosVec(BlockPos pos) {
		return new Vec3(pos.getX(), pos.getY(), pos.getZ());
	}

	public static List<ItemStack> getHotbar(Player player) {
		return player.getInventory().items.subList(0, 9);
	}

	public static List<ItemStack> getHotbarWithOffhand(Player player) {
		List<ItemStack> inventory = new ArrayList<>(player.getInventory().items.subList(0, 9));
		inventory.addAll(player.getInventory().offhand);
		return inventory;
	}

	public static List<ItemStack> getMainInv(Player player) {
		return player.getInventory().items.subList(9, player.getInventory().items.size());
	}

	public static List<ItemStack> getFullInv(Player player) {
		List<ItemStack> inventory = new ArrayList<>(player.getInventory().offhand);
		inventory.addAll(player.getInventory().items);
		return inventory;
	}

	public static int blockDistance(BlockPos p1, BlockPos p2) {
		return Math.max(Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getZ() - p2.getZ()));
	}

	public static boolean isTEAllowed(BlockState state) {
		if (!state.hasBlockEntity()) return true;

		ResourceLocation name = BuiltInRegistries.BLOCK.getKey(state.getBlock());
		if (name == null) return false;

		String fullId = name.toString();
		String modId = name.getNamespace();

		boolean inList = ConstructionConfig.BE_LIST.get().contains(fullId) || ConstructionConfig.BE_LIST.get().contains(modId);
		boolean isWhitelist = ConstructionConfig.BE_WHITELIST.get();

		return isWhitelist == inList;
	}

	public static boolean placeBlock(Level level, Player player, BlockState block, BlockPos pos, @Nullable BlockItem item) {
		if (!level.setBlockAndUpdate(pos, block)) {
			ConstructionStick.LOGGER.info("Block could not be placed");
			return false;
		}

		// Remove block if placeEvent is canceled
		BlockSnapshot snapshot = BlockSnapshot.create(level.dimension(), level, pos);
		BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(snapshot, block, player);
		NeoForge.EVENT_BUS.post(placeEvent);
		if (placeEvent.isCanceled()) {
			level.removeBlock(pos, false);
			return false;
		}

		ItemStack stack;
		if (item == null) stack = new ItemStack(block.getBlock().asItem());
		else {
			stack = new ItemStack(item);
			player.awardStat(Stats.ITEM_USED.get(item));
		}

		// Call OnBlockPlaced method
		block.getBlock().setPlacedBy(level, pos, block, player, stack);

		return true;
	}

	public static boolean removeBlock(Level level, Player player, @Nullable BlockState block, BlockPos pos) {
		BlockState currentBlock = level.getBlockState(pos);

		if (!level.mayInteract(player, pos)) return false;

		if (!player.isCreative()) {
			if (currentBlock.getDestroySpeed(level, pos) <= -1 || level.getBlockEntity(pos) != null) return false;

			if (block != null)
				if (!ReplacementRegistry.matchBlocks(currentBlock.getBlock(), block.getBlock())) return false;
		}

		BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(level, pos, currentBlock, player);
		NeoForge.EVENT_BUS.post(breakEvent);
		if (breakEvent.isCanceled()) return false;

		level.removeBlock(pos, false);
		return true;
	}

	public static boolean replaceBlock(Level level, Player player, BlockState oldBlock, BlockState newBlock, BlockPos pos, BlockItem item) {
		// Check if the block can be replaced
		if (!isBlockReplaceable(level, player, pos)) {
			return false;
		}

		// Remove the old block
		if (!removeBlock(level, player, oldBlock, pos)) {
			return false;
		}

		// Place the new block
		if (!placeBlock(level, player, newBlock, pos, item)) {
			// If placing the new block fails, restore the old block
			placeBlock(level, player, oldBlock, pos, null);
			return false;
		}

		return true;
	}

	public static int countItem(Player player, Item item) {
		if (player.getInventory().items == null) return 0;
		if (player.isCreative()) return Integer.MAX_VALUE;

		int total = 0;
		ContainerManager containerManager = ConstructionStick.containerManager;
		List<ItemStack> inventory = StickUtil.getFullInv(player);

		for (ItemStack stack : inventory) {
			if (stack == null || stack.isEmpty()) continue;

			if (StickUtil.stackEquals(stack, item)) {
				total += stack.getCount();
			} else {
				int amount = containerManager.countItems(player, new ItemStack(item), stack);
				if (amount == Integer.MAX_VALUE) return Integer.MAX_VALUE;
				total += amount;
			}
		}
		return total;
	}

	private static boolean isPositionModifiable(Level level, Player player, BlockPos pos) {
		// Is position out of level?
		if (!level.isInWorldBounds(pos)) return false;

		// Is block modifiable?
		if (!level.mayInteract(player, pos)) return false;

		// Limit range
		if (ConstructionConfig.MAX_RANGE.get() > 0 &&
				StickUtil.blockDistance(player.blockPosition(), pos) > ConstructionConfig.MAX_RANGE.get()) return false;

		return true;
	}

	/**
	 * Tests if a stick can place a block at a certain position.
	 * This check is independent of the used block.
	 */
	public static boolean isPositionPlaceable(Level level, Player player, BlockPos pos, boolean replace) {
		if (!isPositionModifiable(level, player, pos)) return false;

		// If replace mode is off, target has to be air
		if (level.isEmptyBlock(pos)) return true;

		// Otherwise, check if the block can be replaced by a generic block
		return replace && level.getBlockState(pos).canBeReplaced(
				new StickItemUseContext(level, player,
						new BlockHitResult(new Vec3(0, 0, 0), Direction.DOWN, pos, false),
						pos, (BlockItem) Items.STONE));
	}

	public static boolean isBlockRemovable(Level level, Player player, BlockPos pos) {
		if (!isPositionModifiable(level, player, pos)) return false;

		if (!player.isCreative()) {
			return !(level.getBlockState(pos).getDestroySpeed(level, pos) <= -1) && level.getBlockEntity(pos) == null;
		}
		return true;
	}

	public static boolean isBlockReplaceable(Level level, Player player, BlockPos pos) {
		if (!isPositionModifiable(level, player, pos)) return false;

		if (level.getBlockState(pos).is(ModTags.NON_REPLACEABLE)) return false;

		if (!player.isCreative()) {
			return !(level.getBlockState(pos).getDestroySpeed(level, pos) <= -1) && level.getBlockEntity(pos) == null;
		}
		return true;
	}

	public static boolean isBlockPermeable(Level level, BlockPos pos) {
		return level.isEmptyBlock(pos) || level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
	}

	public static boolean entitiesCollidingWithBlock(Level level, BlockState blockState, BlockPos pos) {
		VoxelShape shape = blockState.getCollisionShape(level, pos);
		if (!shape.isEmpty()) {
			AABB blockBB = shape.bounds().move(pos);
			return !level.getEntitiesOfClass(LivingEntity.class, blockBB, Predicate.not(Entity::isSpectator)).isEmpty();
		}
		return false;
	}

	public static Direction fromVector(Vec3 vector) {
		return Direction.getNearest(vector.x, vector.y, vector.z);
	}

	private static final List<IStickUpgrade> upgradeList = new ArrayList<>();

	public static List<IStickUpgrade> getAllUpgrades() {
		if (upgradeList.isEmpty()) {
			for (Item item : BuiltInRegistries.ITEM) {
				if (item instanceof IStickUpgrade upgrade) {
					upgradeList.add(upgrade);
				}
			}
		}
		return new ArrayList<>(upgradeList);
	}

	public static Optional<IStickTemplate> getUpgrade(ResourceLocation id) {
		for (IStickUpgrade upgrade : getAllUpgrades()) {
			if (upgrade instanceof IStickTemplate template && template.getRegistryName().equals(id)) {
				return Optional.of(template);
			}
		}
		return Optional.empty();
	}
}
