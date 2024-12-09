package mrbysco.constructionstick.stick.undo;

import mrbysco.constructionstick.basics.StickUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class DestroySnapshot implements ISnapshot {
	private final BlockState block;
	private final BlockPos pos;

	public DestroySnapshot(BlockState block, BlockPos pos) {
		this.pos = pos;
		this.block = block;
	}

	@Nullable
	public static DestroySnapshot get(Level level, Player player, BlockPos pos) {
		if (!StickUtil.isBlockRemovable(level, player, pos)) return null;

		return new DestroySnapshot(level.getBlockState(pos), pos);
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
		return ItemStack.EMPTY;
	}

	@Override
	public boolean execute(Level level, Player player, BlockHitResult blockHitResult) {
		return StickUtil.removeBlock(level, player, block, pos);
	}

	@Override
	public boolean canRestore(Level level, Player player) {
		// Is position out of level?
		if (!level.isInWorldBounds(pos)) return false;

		// Is block modifiable?
		if (!level.mayInteract(player, pos)) return false;

		// Ignore blocks and entities when in creative
		if (player.isCreative()) return true;

		// Is block empty or fluid?
		if (!level.isEmptyBlock(pos) && !level.getBlockState(pos).canBeReplaced(Fluids.EMPTY)) return false;

		return !StickUtil.entitiesCollidingWithBlock(level, block, pos);
	}

	@Override
	public boolean restore(Level level, Player player) {
		return StickUtil.placeBlock(level, player, block, pos, null);
	}

	@Override
	public void forceRestore(Level level) {
		level.setBlockAndUpdate(pos, block);
	}
}
