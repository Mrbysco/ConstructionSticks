package mrbysco.constructionstick.api;

import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.stick.undo.ISnapshot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IStickAction {
	int getLimit(ItemStack stick);

	@NotNull
	List<ISnapshot> getSnapshots(Level level, Player player, BlockHitResult blockHitResult,
	                             ItemStack stick, StickOptions options, IStickSupplier supplier, int limit);

	@NotNull
	List<ISnapshot> getSnapshotsFromAir(Level level, Player player, BlockHitResult blockHitResult,
	                                    ItemStack stick, StickOptions options, IStickSupplier supplier, int limit);
}
