package mrbysco.constructionstick.stick.action;

import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.api.IStickSupplier;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.stick.undo.ISnapshot;
import mrbysco.constructionstick.stick.undo.PlaceSnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class ActionAngel implements IStickAction {
	@Override
	public int getLimit(ItemStack stick) {
		return ConstructionConfig.getStickProperties(stick.getItem()).getAngel();
	}

	@NotNull
	@Override
	public List<ISnapshot> getSnapshots(Level level, Player player, BlockHitResult blockHitResult,
	                                    ItemStack stick, StickOptions options, IStickSupplier supplier, int limit) {
		LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();

		Direction placeDirection = blockHitResult.getDirection();
		BlockPos currentPos = blockHitResult.getBlockPos();
		BlockState supportingBlock = level.getBlockState(currentPos);

		for (int i = 0; i < limit; i++) {
			currentPos = currentPos.offset(placeDirection.getOpposite().getUnitVec3i());

			PlaceSnapshot snapshot = supplier.getPlaceSnapshot(level, currentPos, blockHitResult, supportingBlock);
			if (snapshot != null) {
				placeSnapshots.add(snapshot);
				break;
			}
		}
		return placeSnapshots;
	}

	@NotNull
	@Override
	public List<ISnapshot> getSnapshotsFromAir(Level level, Player player, BlockHitResult blockHitResult,
	                                           ItemStack stick, StickOptions options, IStickSupplier supplier, int limit) {
		LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();

		if (!player.isCreative() && !ConstructionConfig.ANGEL_FALLING.get() && player.fallDistance > 10)
			return placeSnapshots;

		Vec3 playerVec = StickUtil.entityPositionVec(player);
		Vec3 lookVec = player.getLookAngle().multiply(2, 2, 2);
		Vec3 placeVec = playerVec.add(lookVec);
		BlockPos currentPos = StickUtil.posFromVec(placeVec);

		PlaceSnapshot snapshot = supplier.getPlaceSnapshot(level, currentPos, blockHitResult, null);
		if (snapshot != null) placeSnapshots.add(snapshot);

		return placeSnapshots;
	}
}
