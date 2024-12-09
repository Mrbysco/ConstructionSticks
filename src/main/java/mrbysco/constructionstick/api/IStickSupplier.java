package mrbysco.constructionstick.api;

import mrbysco.constructionstick.stick.undo.PlaceSnapshot;
import net.minecraft.core.BlockPos;
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
	 * Consumes an item stack if the placement was successful
	 */
	int takeItemStack(ItemStack stack);
}
