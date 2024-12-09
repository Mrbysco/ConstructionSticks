package mrbysco.constructionstick.stick.undo;

import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.stick.StickItemUseContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class PlaceSnapshot implements ISnapshot {
	private BlockState block;
	private final BlockPos pos;
	private final BlockItem item;
	private final BlockState supportingBlock;
	private final boolean targetMode;

	public PlaceSnapshot(BlockState block, BlockPos pos, BlockItem item, BlockState supportingBlock, boolean targetMode) {
		this.block = block;
		this.pos = pos;
		this.item = item;
		this.supportingBlock = supportingBlock;
		this.targetMode = targetMode;
	}

	public static PlaceSnapshot get(Level level, Player player, BlockHitResult blockHitResult,
	                                BlockPos pos, BlockItem item,
	                                @Nullable BlockState supportingBlock, @Nullable StickOptions options) {
		boolean targetMode = options != null && supportingBlock != null && options.direction.get() == StickOptions.DIRECTION.TARGET;
		BlockState blockState = getPlaceBlockstate(level, player, blockHitResult, pos, item, supportingBlock, targetMode);
		if (blockState == null) return null;

		return new PlaceSnapshot(blockState, pos, item, supportingBlock, targetMode);
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}

	@Override
	public BlockState getBlockState() {
		return block;
	}

	@Override
	public ItemStack getRequiredItems() {
		return new ItemStack(item);
	}

	@Override
	public boolean execute(Level level, Player player, BlockHitResult blockHitResult) {
		// Recalculate PlaceBlockState, because other blocks might be placed nearby
		// Not doing this may cause game crashes (StackOverflowException) when placing lots of blocks
		// with changing orientation like panes, iron bars or redstone.
		block = getPlaceBlockstate(level, player, blockHitResult, pos, item, supportingBlock, targetMode);
		if (block == null) return false;
		return StickUtil.placeBlock(level, player, block, pos, item);
	}

	@Override
	public boolean canRestore(Level level, Player player) {
		return true;
	}

	@Override
	public boolean restore(Level level, Player player) {
		return StickUtil.removeBlock(level, player, block, pos);
	}

	@Override
	public void forceRestore(Level level) {
		level.removeBlock(pos, false);
	}

	/**
	 * Tests if a certain block can be placed by the stick.
	 * If it can, returns the blockstate to be placed.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Nullable
	private static BlockState getPlaceBlockstate(Level level, Player player, BlockHitResult blockHitResult,
	                                             BlockPos pos, BlockItem item,
	                                             @Nullable BlockState supportingBlock, boolean targetMode) {
		// Is block at pos replaceable?
		BlockPlaceContext ctx = new StickItemUseContext(level, player, blockHitResult, pos, item);
		if (!ctx.canPlace()) return null;

		// Can block be placed?
		BlockState blockState = item.getBlock().getStateForPlacement(ctx);
		if (blockState == null || !blockState.canSurvive(level, pos)) return null;

		// Forbidden Tile Entity?
		if (!StickUtil.isTEAllowed(blockState)) return null;

		// No entities colliding?
		if (StickUtil.entitiesCollidingWithBlock(level, blockState, pos)) return null;

		// Copy block properties from supporting block
		if (targetMode && supportingBlock != null) {
			// Block properties to be copied (alignment/rotation properties)

			for (Property property : new Property[]{
					BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.FACING, BlockStateProperties.FACING_HOPPER,
					BlockStateProperties.ROTATION_16, BlockStateProperties.AXIS, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE}) {
				if (supportingBlock.hasProperty(property) && blockState.hasProperty(property)) {
					blockState = blockState.setValue(property, supportingBlock.getValue(property));
				}
			}

			// Dont dupe double slabs
			if (supportingBlock.hasProperty(BlockStateProperties.SLAB_TYPE) && blockState.hasProperty(BlockStateProperties.SLAB_TYPE)) {
				SlabType slabType = supportingBlock.getValue(BlockStateProperties.SLAB_TYPE);
				if (slabType != SlabType.DOUBLE)
					blockState = blockState.setValue(BlockStateProperties.SLAB_TYPE, slabType);
			}
		}
		return blockState;
	}
}
