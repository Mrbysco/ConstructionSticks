package mrbysco.constructionstick.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.network.PacketRequestPreview;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.Set;

public class RenderBlockPreview {
	public static Set<BlockPos> undoBlocks;
	public static Set<BlockPos> previewBlocks;

	@SubscribeEvent
	public static void onExtractBlockOutlineRenderStateEvent(ExtractBlockOutlineRenderStateEvent event) {
		event.addCustomRenderer(new PreviewRender(event.getHitResult(), event.getCamera()));
	}

	private static class PreviewRender implements CustomBlockOutlineRenderer {
		private final BlockHitResult target;
		private final Camera camera;

		private BlockHitResult lastRayTraceResult = null;
		private ItemStack lastStick = ItemStack.EMPTY;

		public PreviewRender(BlockHitResult hitResult, Camera camera) {
			this.target = hitResult;
			this.camera = camera;
		}

		@Override
		public boolean render(BlockOutlineRenderState renderState, MultiBufferSource.BufferSource buffer, PoseStack poseStack, boolean translucentPass, LevelRenderState levelRenderState) {
			Entity entity = camera.entity();
			if (!(entity instanceof Player player)) return false;
			Set<BlockPos> blocks;
			float colorR = 0, colorG = 0, colorB = 0;

			ItemStack stick = StickUtil.holdingStick(player);
			if (stick == null) return false;

			if (KeybindHandler.KEY_SHOW_PREVIOUS.isDown()) {
				blocks = undoBlocks;
				colorG = 1;
			} else {
				// Use cached stickJob for previews of the same target pos/dir
				// Exception: always update if blockCount < 2 to prevent 1-block previews when block updates
				// from the last placement are lagging
				if (lastRayTraceResult == null || !compareRTR(lastRayTraceResult, target) || !lastStick.equals(stick)
						|| previewBlocks == null || previewBlocks.size() < 2) {
					lastRayTraceResult = target;
					lastStick = stick;
					ClientPacketDistributor.sendToServer(new PacketRequestPreview(target, stick));
				}
				blocks = previewBlocks;
			}

			if (blocks == null || blocks.isEmpty()) return false;

			VertexConsumer lineBuilder = buffer.getBuffer(RenderTypes.lines());

			double d0 = camera.position().x();
			double d1 = camera.position().y();
			double d2 = camera.position().z();

			for (BlockPos block : blocks) {
				AABB aabb = new AABB(block).move(-d0, -d1, -d2);
				ShapeRenderer.renderShape(poseStack, lineBuilder, Shapes.create(aabb), 0, 0, 0,
						ARGB.colorFromFloat(0.4F, colorR, colorG, colorB), 2F);
			}

			return true;
		}
	}

	private static boolean compareRTR(BlockHitResult rtr1, BlockHitResult rtr2) {
		return rtr1.getBlockPos().equals(rtr2.getBlockPos()) && rtr1.getDirection().equals(rtr2.getDirection());
	}
}
