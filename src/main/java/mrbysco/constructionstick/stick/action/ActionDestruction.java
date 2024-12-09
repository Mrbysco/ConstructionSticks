package mrbysco.constructionstick.stick.action;

import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.api.IStickSupplier;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.stick.undo.DestroySnapshot;
import mrbysco.constructionstick.stick.undo.ISnapshot;
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

public class ActionDestruction implements IStickAction {
	@Override
	public int getLimit(ItemStack stick) {
		return ConstructionConfig.getStickProperties(stick.getItem()).getDestruction();
	}

	@NotNull
	@Override
	public List<ISnapshot> getSnapshots(Level level, Player player, BlockHitResult blockHitResult,
	                                    ItemStack stick, StickOptions options, IStickSupplier supplier, int limit) {
		LinkedList<ISnapshot> destroySnapshots = new LinkedList<>();
		// Current list of block positions to process
		LinkedList<BlockPos> candidates = new LinkedList<>();
		// All positions that were processed (dont process blocks multiple times)
		HashSet<BlockPos> allCandidates = new HashSet<>();

		// Block face the stick was pointed at
		Direction breakFace = blockHitResult.getDirection();
		// Block the stick was pointed at
		BlockPos startingPoint = blockHitResult.getBlockPos();
		BlockState targetBlock = level.getBlockState(blockHitResult.getBlockPos());

		// Is break direction allowed by lock?
		// Tried to break blocks from top/bottom face, so the stick should allow breaking in NS/EW direction
		if (breakFace == Direction.UP || breakFace == Direction.DOWN) {
			if (options.testLock(StickOptions.LOCK.NORTHSOUTH) || options.testLock(StickOptions.LOCK.EASTWEST))
				candidates.add(startingPoint);
		}
		// Tried to break blocks from side face, so the stick should allow breaking in horizontal/vertical direction
		else if (options.testLock(StickOptions.LOCK.HORIZONTAL) || options.testLock(StickOptions.LOCK.VERTICAL))
			candidates.add(startingPoint);

		// Process current candidates, stop when none are avaiable or block limit is reached
		while (!candidates.isEmpty() && destroySnapshots.size() < limit) {
			BlockPos currentCandidate = candidates.removeFirst();

			// Only break blocks facing the player, with no collidable blocks in between
			if (!StickUtil.isBlockPermeable(level, currentCandidate.offset(breakFace.getNormal()))) continue;

			try {
				BlockState candidateBlock = level.getBlockState(currentCandidate);

				// If target and candidate blocks match and the current candidate has not been processed
				if (options.matchBlocks(targetBlock.getBlock(), candidateBlock.getBlock()) &&
						allCandidates.add(currentCandidate)) {
					DestroySnapshot snapshot = DestroySnapshot.get(level, player, currentCandidate);
					if (snapshot == null) continue;
					destroySnapshots.add(snapshot);

					switch (breakFace) {
						case DOWN:
						case UP:
							if (options.testLock(StickOptions.LOCK.NORTHSOUTH)) {
								candidates.add(currentCandidate.offset(Direction.NORTH.getNormal()));
								candidates.add(currentCandidate.offset(Direction.SOUTH.getNormal()));
							}
							if (options.testLock(StickOptions.LOCK.EASTWEST)) {
								candidates.add(currentCandidate.offset(Direction.EAST.getNormal()));
								candidates.add(currentCandidate.offset(Direction.WEST.getNormal()));
							}
							if (options.testLock(StickOptions.LOCK.NORTHSOUTH) && options.testLock(StickOptions.LOCK.EASTWEST)) {
								candidates.add(currentCandidate.offset(Direction.NORTH.getNormal()).offset(Direction.EAST.getNormal()));
								candidates.add(currentCandidate.offset(Direction.NORTH.getNormal()).offset(Direction.WEST.getNormal()));
								candidates.add(currentCandidate.offset(Direction.SOUTH.getNormal()).offset(Direction.EAST.getNormal()));
								candidates.add(currentCandidate.offset(Direction.SOUTH.getNormal()).offset(Direction.WEST.getNormal()));
							}
							break;
						case NORTH:
						case SOUTH:
							if (options.testLock(StickOptions.LOCK.HORIZONTAL)) {
								candidates.add(currentCandidate.offset(Direction.EAST.getNormal()));
								candidates.add(currentCandidate.offset(Direction.WEST.getNormal()));
							}
							if (options.testLock(StickOptions.LOCK.VERTICAL)) {
								candidates.add(currentCandidate.offset(Direction.UP.getNormal()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getNormal()));
							}
							if (options.testLock(StickOptions.LOCK.HORIZONTAL) && options.testLock(StickOptions.LOCK.VERTICAL)) {
								candidates.add(currentCandidate.offset(Direction.UP.getNormal()).offset(Direction.EAST.getNormal()));
								candidates.add(currentCandidate.offset(Direction.UP.getNormal()).offset(Direction.WEST.getNormal()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getNormal()).offset(Direction.EAST.getNormal()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getNormal()).offset(Direction.WEST.getNormal()));
							}
							break;
						case EAST:
						case WEST:
							if (options.testLock(StickOptions.LOCK.HORIZONTAL)) {
								candidates.add(currentCandidate.offset(Direction.NORTH.getNormal()));
								candidates.add(currentCandidate.offset(Direction.SOUTH.getNormal()));
							}
							if (options.testLock(StickOptions.LOCK.VERTICAL)) {
								candidates.add(currentCandidate.offset(Direction.UP.getNormal()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getNormal()));
							}
							if (options.testLock(StickOptions.LOCK.HORIZONTAL) && options.testLock(StickOptions.LOCK.VERTICAL)) {
								candidates.add(currentCandidate.offset(Direction.UP.getNormal()).offset(Direction.NORTH.getNormal()));
								candidates.add(currentCandidate.offset(Direction.UP.getNormal()).offset(Direction.SOUTH.getNormal()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getNormal()).offset(Direction.NORTH.getNormal()));
								candidates.add(currentCandidate.offset(Direction.DOWN.getNormal()).offset(Direction.SOUTH.getNormal()));
							}
							break;
					}
				}
			} catch (Exception e) {
				// Can't do anything, could be anything.
				// Skip if anything goes wrong.
			}
		}
		return destroySnapshots;
	}

	@NotNull
	@Override
	public List<ISnapshot> getSnapshotsFromAir(Level level, Player player, BlockHitResult blockHitResult,
	                                           ItemStack stick, StickOptions options, IStickSupplier supplier, int limit) {
		return new ArrayList<>();
	}
}
