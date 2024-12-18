package mrbysco.constructionstick.stick.undo;

import mrbysco.constructionstick.basics.StickUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ReplaceSnapshot implements ISnapshot {
	private final BlockState oldBlock;
	private final BlockState newBlock;
	private final BlockPos pos;
	private final BlockItem item;

	public ReplaceSnapshot(BlockState oldBlock, BlockState newBlock, BlockPos pos, BlockItem item) {
		this.oldBlock = oldBlock;
		this.newBlock = newBlock;
		this.pos = pos;
		this.item = item;
	}

	@Nullable
	public static ReplaceSnapshot get(Level level, Player player, BlockPos pos, BlockState newBlock, BlockItem item) {
		BlockState oldBlock = level.getBlockState(pos);
		if (!StickUtil.isBlockReplaceable(level, player, pos)) return null;

		return new ReplaceSnapshot(oldBlock, newBlock, pos, item);
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}

	@Override
	public BlockState getBlockState() {
		return newBlock;
	}

	@Override
	public ItemStack getRequiredItems() {
		return new ItemStack(item);
	}

	@Override
	public boolean execute(Level level, Player player, BlockHitResult blockHitResult) {
		return StickUtil.replaceBlock(level, player, oldBlock, newBlock, pos, item);
	}

	@Override
	public boolean canRestore(Level level, Player player) {
		return false;
	}

	@Override
	public boolean restore(Level level, Player player) {
		return false;
	}

	@Override
	public void forceRestore(Level level) {
		level.setBlockAndUpdate(pos, oldBlock);
	}
}