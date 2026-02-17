package mrbysco.constructionstick.network;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.stick.StickJob;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Set;

public record PacketRequestPreview(BlockHitResult rtr, ItemStack stick) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketRequestPreview> CODEC = CustomPacketPayload.codec(
			PacketRequestPreview::encode,
			PacketRequestPreview::new);
	public static final Type<PacketRequestPreview> ID = new Type<>(ConstructionStick.modLoc("request_preview"));

    public PacketRequestPreview(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockHitResult(), ItemStack.STREAM_CODEC.decode(buffer));
    }

    public static void encode(PacketRequestPreview msg, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockHitResult(msg.rtr);
        ItemStack.STREAM_CODEC.encode(buffer, msg.stick);
    }

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

    public static class Handler
    {
        public static void handle(final PacketRequestPreview msg, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
                    StickJob job = ItemStick.getStickJob(player, player.level(), msg.rtr, msg.stick);
                    if (job == null) return;
                    Set<BlockPos> blocks = job.getBlockPositions();

                    ModMessages.sendToPlayer(new PacketPreviewResult(blocks), player);
                }
            })
            .exceptionally(e -> {
                // Handle exception
                ctx.disconnect(Component.translatable("constructionstick.networking.preview_request.failed", e.getMessage()));
                return null;
            });
        }
    }
}
