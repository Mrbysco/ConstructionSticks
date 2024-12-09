package mrbysco.constructionstick.stick;

import mrbysco.constructionstick.basics.StickUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class StickItemUseContext extends BlockPlaceContext {
	public StickItemUseContext(Level level, Player player, BlockHitResult blockHitResult, BlockPos pos, BlockItem item) {
		super(level, player, InteractionHand.MAIN_HAND, new ItemStack(item),
				new BlockHitResult(getBlockHitVec(blockHitResult, pos), blockHitResult.getDirection(), pos, false));
	}

	private static Vec3 getBlockHitVec(BlockHitResult blockHitResult, BlockPos pos) {
		Vec3 hitVec = blockHitResult.getLocation(); // Absolute coords of hit target

		Vec3 blockDelta = StickUtil.blockPosVec(blockHitResult.getBlockPos()).subtract(StickUtil.blockPosVec(pos)); // Vector between start and current block

		return blockDelta.add(hitVec); // Absolute coords of current block hit target
	}

	@Override
	public boolean canPlace() {
		return replaceClicked;
	}
}
