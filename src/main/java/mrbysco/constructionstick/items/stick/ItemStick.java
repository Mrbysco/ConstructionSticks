package mrbysco.constructionstick.items.stick;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickTemplate;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.IOption;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.basics.option.StickUpgradesSelectable;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.registry.ModDataComponents;
import mrbysco.constructionstick.stick.StickJob;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.function.Consumer;

public abstract class ItemStick extends Item {
	public ItemStick(Properties properties) {
		super(properties);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();
		Level level = context.getLevel();

		if (level.isClientSide || player == null) return InteractionResult.FAIL;

		ItemStack stack = player.getItemInHand(hand);

		if (ConstructionStick.undoHistory.isUndoActive(player)) {
			return ConstructionStick.undoHistory.undo(player, level, context.getClickedPos()) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		} else {
			StickJob job = getStickJob(player, level, new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), false), stack);
			return job.doIt() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		}
	}

	@NotNull
	@Override
	public InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!player.isCrouching()) {
			if (level.isClientSide) return InteractionResult.FAIL;

			// Right click: Place angel block
			StickJob job = getStickJob(player, level, BlockHitResult.miss(player.getLookAngle(),
					StickUtil.fromVector(player.getLookAngle()), player.blockPosition()), stack);
			return job.doIt() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		}
		return InteractionResult.FAIL;
	}

	public static StickJob getStickJob(Player player, Level level, @Nullable BlockHitResult rayTraceResult, ItemStack stick) {
		StickJob stickJob = new StickJob(player, level, rayTraceResult, stick);
		stickJob.getSnapshots();

		return stickJob;
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return false;
	}

	public int remainingDurability(ItemStack stack) {
		return Integer.MAX_VALUE;
	}

	public void hurtItem(ItemStack stack, int amount, LivingEntity entity, EquipmentSlot slot) {
		if (stack.has(ModDataComponents.BATTERY_ENABLED)) {
			if (entity.hasInfiniteMaterials()) return;
			IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
			if (storage != null) {
				int usage = ConstructionConfig.getStickProperties(this).getBatteryUsage();
				storage.extractEnergy(usage, false);
			}
		} else {
			stack.hurtAndBreak(amount, entity, slot);
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);
		if (stack.has(ModDataComponents.UNBREAKABLE) && !stack.has(DataComponents.UNBREAKABLE)) {
			stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay,
	                            Consumer<Component> components, TooltipFlag flag) {
		StickOptions options = new StickOptions(stack);
		int limit = options.upgrades.get().getStickAction().getLimit(stack);

		String langTooltip = ConstructionStick.MOD_ID + ".tooltip.";

		// +SHIFT tooltip: show all options + installed upgrades
		if (Screen.hasShiftDown()) {
			for (int i = 1; i < options.allOptions.length; i++) {
				IOption<?> opt = options.allOptions[i];
				components.accept(Component.translatable(opt.getKeyTranslation()).withStyle(ChatFormatting.AQUA)
						.append(Component.translatable(opt.getValueTranslation()).withStyle(ChatFormatting.GRAY))
				);
			}
			if (!options.upgrades.getUpgrades().isEmpty()) {
				components.accept(Component.literal(""));
				components.accept(Component.translatable(langTooltip + "upgrades").withStyle(ChatFormatting.GRAY));

				for (IStickTemplate upgrades : options.upgrades.getUpgrades()) {
					components.accept(Component.translatable(options.upgrades.getKeyTranslation() + "." + upgrades.getRegistryName().toString()));
				}
				for (IStickTemplate specialUpgrades : options.upgrades.getSpecialUpgrades()) {
					components.accept(Component.translatable(options.upgrades.getKeyTranslation() + "." + specialUpgrades.getRegistryName().toString()));
				}
			}
		}
		// Default tooltip: show block limit + active stick upgrade
		else {
			StickUpgradesSelectable<IStickTemplate> upgrades = options.upgrades;
			components.accept(Component.translatable(langTooltip + "blocks", limit).withStyle(ChatFormatting.GRAY));

			if (stack.has(ModDataComponents.BATTERY_ENABLED)) {
				IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
				if (storage != null) {
					int energy = storage.getEnergyStored();
					NumberFormat format = NumberFormat.getInstance();
					components.accept(Component.translatable("constructionstick.tooltip.storage", format.format(energy), format.format(storage.getMaxEnergyStored()))
							.withStyle(ChatFormatting.RED));
				}
			}

			components.accept(Component.translatable(upgrades.getKeyTranslation()).withStyle(ChatFormatting.AQUA)
					.append(Component.translatable(upgrades.getValueTranslation()).withStyle(ChatFormatting.WHITE)));
			components.accept(Component.translatable(langTooltip + "shift").withStyle(ChatFormatting.AQUA));
		}
	}

	public static void optionMessage(Player player, IOption<?> option) {
		player.displayClientMessage(
				Component.translatable(option.getKeyTranslation()).withStyle(ChatFormatting.AQUA)
						.append(Component.translatable(option.getValueTranslation()).withStyle(ChatFormatting.WHITE))
						.append(Component.literal(" - ").withStyle(ChatFormatting.GRAY))
						.append(Component.translatable(option.getDescTranslation()).withStyle(ChatFormatting.WHITE))
				, true);
	}
}
