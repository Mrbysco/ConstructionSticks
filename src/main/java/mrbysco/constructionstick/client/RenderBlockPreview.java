package mrbysco.constructionstick.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.stick.StickJob;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;

import java.util.Set;

@EventBusSubscriber(Dist.CLIENT)
public class RenderBlockPreview {
	private static StickJob stickJob;
	public static Set<BlockPos> undoBlocks;

	@SubscribeEvent
	public static void onExtractBlockOutlineRenderStateEvent(ExtractBlockOutlineRenderStateEvent event) {

		event.addCustomRenderer(new PreviewRender(event.getHitResult(), event.getCamera()));
	}

	private static class PreviewRender implements CustomBlockOutlineRenderer {
		private final BlockHitResult target;
		private final Camera camera;

		public PreviewRender(BlockHitResult hitResult, Camera camera) {
			this.target = hitResult;
			this.camera = camera;
		}

		@Override
		public boolean render(BlockOutlineRenderState renderState, MultiBufferSource.BufferSource buffer,
		                      PoseStack poseStack, boolean translucentPass, LevelRenderState levelRenderState) {
			Entity entity = camera.getEntity();
			if (!(entity instanceof Player player)) return false;
			Set<BlockPos> blocks;
			float colorR = 0, colorG = 0, colorB = 0;

			ItemStack stick = StickUtil.holdingStick(player);
			if (stick == null) return false;

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

			if (blocks == null || blocks.isEmpty()) return false;

			VertexConsumer lineBuilder = buffer.getBuffer(RenderType.LINES);

			double d0 = camera.getPosition().x();
			double d1 = camera.getPosition().y();
			double d2 = camera.getPosition().z();

			for (BlockPos block : blocks) {
				AABB aabb = new AABB(block).move(-d0, -d1, -d2);
				ShapeRenderer.renderLineBox(poseStack.last(), lineBuilder, aabb, colorR, colorG, colorB, 0.4F);
			}

			return true;
		}
	}

	public static void reset() {
		stickJob = null;
	}

	private static boolean compareRTR(BlockHitResult rtr1, BlockHitResult rtr2) {
		return rtr1.getBlockPos().equals(rtr2.getBlockPos()) && rtr1.getDirection().equals(rtr2.getDirection());
	}
}
