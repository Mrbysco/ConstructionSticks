package mrbysco.constructionstick.stick.undo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface ISnapshot {
	BlockPos getPos();

	BlockState getBlockState();

	ItemStack getRequiredItems();

	boolean execute(Level level, Player player, BlockHitResult blockHitResult);

	boolean canRestore(Level level, Player player);

	boolean restore(Level level, Player player);

	void forceRestore(Level level);

	default void onSuccess(Level level, Player player) {

	}
}
