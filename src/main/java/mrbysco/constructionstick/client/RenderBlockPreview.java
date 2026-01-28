package mrbysco.constructionstick.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.stick.StickJob;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

public class RenderBlockPreview {
	private StickJob stickJob;
	public Set<BlockPos> undoBlocks;

	@SubscribeEvent
	public void renderBlockHighlight(RenderHighlightEvent.Block event) {
		if (event.getTarget().getType() != HitResult.Type.BLOCK) return;

		BlockHitResult target = event.getTarget();
		Entity entity = event.getCamera().getEntity();
		if (!(entity instanceof Player player)) return;
		Set<BlockPos> blocks;
		float colorR = 0, colorG = 0, colorB = 0;

		ItemStack stick = StickUtil.holdingStick(player);
		if (stick == null) return;

		if (!KeybindHandler.KEY_SHOW_PREVIOUS.isDown()) {
			// Use cached stickJob for previews of the same target pos/dir
			// Exception: always update if blockCount < 2 to prevent 1-block previews when block updates
			// from the last placement are lagging
			if (stickJob == null || !compareRTR(stickJob.blockHitResult, target) || !(stickJob.stick.equals(stick))
					|| stickJob.blockCount() < 2) {
				stickJob = ItemStick.getStickJob(player, player.level(), target, stick);
			}
			blocks = stickJob.getBlockPositions();
		} else {
			blocks = undoBlocks;
			colorG = 1;
		}

		if (blocks == null || blocks.isEmpty()) return;

		PoseStack ms = event.getPoseStack();
		MultiBufferSource buffer = event.getMultiBufferSource();
		VertexConsumer lineBuilder = buffer.getBuffer(RenderType.LINES);

		Camera info = event.getCamera();
		double d0 = info.getPosition().x();
		double d1 = info.getPosition().y();
		double d2 = info.getPosition().z();

		for (BlockPos block : blocks) {
			AABB aabb = new AABB(block).move(-d0, -d1, -d2);
			LevelRenderer.renderLineBox(ms, lineBuilder, aabb, colorR, colorG, colorB, 0.4F);
		}

		event.setCanceled(true);
	}

	public void reset() {
		stickJob = null;
	}

	private static boolean compareRTR(BlockHitResult rtr1, BlockHitResult rtr2) {
		return rtr1.getBlockPos().equals(rtr2.getBlockPos()) && rtr1.getDirection().equals(rtr2.getDirection());
	}
}
