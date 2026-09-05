package mrbysco.constructionstick.api;

import mrbysco.constructionstick.stick.undo.PlaceSnapshot;
import mrbysco.constructionstick.stick.undo.ReplaceSnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public interface IStickSupplier {
	void getSupply(@Nullable BlockItem target);

	/**
	 * Tries to create a new PlaceSnapshot at the specified position.
	 * Returns null if there aren't any blocks available that can be placed
	 * in that position.
	 */
	@Nullable
	PlaceSnapshot getPlaceSnapshot(Level level, BlockPos pos, BlockHitResult blockHitResult,
	                               @Nullable BlockState supportingBlock);

	/**
	 * Tries to create a new ReplaceSnapshot at the specified position,
	 * drawing the replacement block from this supplier's pool.
	 * Returns null if the position cannot be replaced or no suitable block remains.
	 */
	@Nullable
	default ReplaceSnapshot getReplaceSnapshot(Level level, Player player, BlockPos pos, BlockState existing) {
		return null;
	}

	/**
	 * Consumes an item stack if the placement was successful
	 */
	int takeItemStack(ItemStack stack);
}
