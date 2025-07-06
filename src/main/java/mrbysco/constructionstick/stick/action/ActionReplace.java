package mrbysco.constructionstick.stick.action;

import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.api.IStickSupplier;
import mrbysco.constructionstick.basics.ModTags;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.stick.undo.ISnapshot;
import mrbysco.constructionstick.stick.undo.ReplaceSnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ActionReplace implements IStickAction {
	@Override
	public int getLimit(ItemStack stick) {
		return ConstructionConfig.getStickProperties(stick.getItem()).getLimit();
	}

	@NotNull
	@Override
	public List<ISnapshot> getSnapshots(Level level, Player player, BlockHitResult blockHitResult,
	                                    ItemStack stick, StickOptions options, IStickSupplier supplier, int limit) {
		LinkedList<ISnapshot> replaceSnapshots = new LinkedList<>();
		LinkedList<BlockPos> candidates = new LinkedList<>();
		HashSet<BlockPos> allCandidates = new HashSet<>();

		ItemStack offHand = player.getOffhandItem();
		if (offHand.isEmpty() || !(offHand.getItem() instanceof BlockItem blockItem)) {
			return replaceSnapshots;
		}
		BlockState newBlock = blockItem.getBlock().defaultBlockState();

		Direction placeDirection = blockHitResult.getDirection();
		BlockState targetBlock = level.getBlockState(blockHitResult.getBlockPos());
		BlockPos startingPoint = blockHitResult.getBlockPos();

		// Is place direction allowed by lock?
		if (placeDirection == Direction.UP || placeDirection == Direction.DOWN) {
			if (options.testLock(StickOptions.LOCK.NORTHSOUTH) || options.testLock(StickOptions.LOCK.EASTWEST))
				candidates.add(startingPoint);
		} else if (options.testLock(StickOptions.LOCK.HORIZONTAL) || options.testLock(StickOptions.LOCK.VERTICAL))
			candidates.add(startingPoint);

		while (!candidates.isEmpty() && replaceSnapshots.size() < limit) {
			BlockPos currentCandidate = candidates.removeFirst();
			try {
				BlockState candidateBlock = level.getBlockState(currentCandidate);
				if (candidateBlock.is(newBlock.getBlock()) || candidateBlock.isAir()) continue;

				if (!targetBlock.is(ModTags.NON_REPLACEABLE) && allCandidates.add(currentCandidate)) {
					ReplaceSnapshot snapshot = ReplaceSnapshot.get(level, player, currentCandidate, newBlock, blockItem);
					if (snapshot == null) continue;
					replaceSnapshots.add(snapshot);

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
				// Skip if anything goes wrong.
			}
		}
		return replaceSnapshots;
	}

	@NotNull
	@Override
	public List<ISnapshot> getSnapshotsFromAir(Level level, Player player, BlockHitResult blockHitResult, ItemStack stick, StickOptions options, IStickSupplier supplier, int limit) {
		return List.of();
	}
}
