package mrbysco.constructionstick.stick.action;

import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.api.IStickSupplier;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Default StickAction. Extends your building on the side you're facing.
 */
public class ActionConstruction implements IStickAction {
	@Override
	public int getLimit(ItemStack stick) {
		return ConstructionConfig.getStickProperties(stick.getItem()).getLimit();
	}

	@NotNull
	@Override
	public List<ISnapshot> getSnapshots(Level level, Player player, BlockHitResult blockHitResult,
	                                    ItemStack stick, StickOptions options, IStickSupplier supplier, int limit) {
		LinkedList<ISnapshot> placeSnapshots = new LinkedList<>();
		LinkedList<BlockPos> candidates = new LinkedList<>();
		HashSet<BlockPos> allCandidates = new HashSet<>();

		Direction placeDirection = blockHitResult.getDirection();
		BlockState targetBlock = level.getBlockState(blockHitResult.getBlockPos());
		BlockPos startingPoint = blockHitResult.getBlockPos().offset(placeDirection.getUnitVec3i());

		// Is place direction allowed by lock?
		if (placeDirection == Direction.UP || placeDirection == Direction.DOWN) {
			if (options.testLock(StickOptions.LOCK.NORTHSOUTH) || options.testLock(StickOptions.LOCK.EASTWEST))
				candidates.add(startingPoint);
		} else if (options.testLock(StickOptions.LOCK.HORIZONTAL) || options.testLock(StickOptions.LOCK.VERTICAL))
			candidates.add(startingPoint);

		while (!candidates.isEmpty() && placeSnapshots.size() < limit) {
			BlockPos currentCandidate = candidates.removeFirst();
			try {
				BlockPos supportingPoint = currentCandidate.offset(placeDirection.getOpposite().getUnitVec3i());
				BlockState candidateSupportingBlock = level.getBlockState(supportingPoint);

				if (options.matchBlocks(targetBlock.getBlock(), candidateSupportingBlock.getBlock()) &&
						allCandidates.add(currentCandidate)) {
					PlaceSnapshot snapshot = supplier.getPlaceSnapshot(level, currentCandidate, blockHitResult, candidateSupportingBlock);
					if (snapshot == null) continue;
					placeSnapshots.add(snapshot);

					switch (placeDirection) {
						case DOWN:
						case UP:
							if (options.testLock(StickOptions.LOCK.NORTHSOUTH)) {
								candidates.add(currentCandidate.offset(Direction.NORTH.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.SOUTH.getUnitVec3i()));
							}
							if (options.testLock(StickOptions.LOCK.EASTWEST)) {
								candidates.add(currentCandidate.offset(Direction.EAST.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.WEST.getUnitVec3i()));
							}
							if (options.testLock(StickOptions.LOCK.NORTHSOUTH) && options.testLock(StickOptions.LOCK.EASTWEST)) {
								candidates.add(currentCandidate.offset(Direction.NORTH.getUnitVec3i()).offset(Direction.EAST.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.NORTH.getUnitVec3i()).offset(Direction.WEST.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.SOUTH.getUnitVec3i()).offset(Direction.EAST.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.SOUTH.getUnitVec3i()).offset(Direction.WEST.getUnitVec3i()));
							}
							break;
						case NORTH:
						case SOUTH:
							if (options.testLock(StickOptions.LOCK.HORIZONTAL)) {
								candidates.add(currentCandidate.offset(Direction.EAST.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.WEST.getUnitVec3i()));
							}
							if (options.testLock(StickOptions.LOCK.VERTICAL)) {
								candidates.add(currentCandidate.offset(Direction.UP.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getUnitVec3i()));
							}
							if (options.testLock(StickOptions.LOCK.HORIZONTAL) && options.testLock(StickOptions.LOCK.VERTICAL)) {
								candidates.add(currentCandidate.offset(Direction.UP.getUnitVec3i()).offset(Direction.EAST.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.UP.getUnitVec3i()).offset(Direction.WEST.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getUnitVec3i()).offset(Direction.EAST.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getUnitVec3i()).offset(Direction.WEST.getUnitVec3i()));
							}
							break;
						case EAST:
						case WEST:
							if (options.testLock(StickOptions.LOCK.HORIZONTAL)) {
								candidates.add(currentCandidate.offset(Direction.NORTH.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.SOUTH.getUnitVec3i()));
							}
							if (options.testLock(StickOptions.LOCK.VERTICAL)) {
								candidates.add(currentCandidate.offset(Direction.UP.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getUnitVec3i()));
							}
							if (options.testLock(StickOptions.LOCK.HORIZONTAL) && options.testLock(StickOptions.LOCK.VERTICAL)) {
								candidates.add(currentCandidate.offset(Direction.UP.getUnitVec3i()).offset(Direction.NORTH.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.UP.getUnitVec3i()).offset(Direction.SOUTH.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getUnitVec3i()).offset(Direction.NORTH.getUnitVec3i()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getUnitVec3i()).offset(Direction.SOUTH.getUnitVec3i()));
							}
							break;
					}
				}
			} catch (Exception e) {
				// Can't do anything, could be anything.
				// Skip if anything goes wrong.
			}
		}
		return placeSnapshots;
	}

	@NotNull
	@Override
	public List<ISnapshot> getSnapshotsFromAir(Level level, Player player, BlockHitResult blockHitResult,
	                                           ItemStack stick, StickOptions options, IStickSupplier supplier, int limit) {
		return new ArrayList<>();
	}
}
