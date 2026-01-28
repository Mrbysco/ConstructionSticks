package mrbysco.constructionstick.items.stick;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickTemplate;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.IOption;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.basics.option.StickUpgradesSelectable;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.stick.StickJob;
import mrbysco.constructionstick.util.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.List;

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
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (!player.isCrouching()) {
			if (level.isClientSide) return InteractionResultHolder.fail(stack);

			// Right click: Place angel block
			StickJob job = getStickJob(player, level, BlockHitResult.miss(player.getLookAngle(),
					StickUtil.fromVector(player.getLookAngle()), player.blockPosition()), stack);
			return job.doIt() ? InteractionResultHolder.success(stack) : InteractionResultHolder.fail(stack);
		}
		return InteractionResultHolder.fail(stack);
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

	@Override
	public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
		return false;
	}

	public int remainingDurability(ItemStack stack) {
		return Integer.MAX_VALUE;
	}

	public void hurtItem(ItemStack stack, int amount, LivingEntity entity, EquipmentSlot slot) {
		if (NBTHelper.hasKey(stack, ConstructionStick.BATTERY_KEY)) {
			if (entity instanceof Player player && player.getAbilities().instabuild) return;
			int usage = ConstructionConfig.getStickProperties(this).getBatteryUsage();
			stack.getCapability(ForgeCapabilities.ENERGY)
					.map(cap ->
							cap.extractEnergy(usage * amount, false)
					);
		} else {
			stack.hurtAndBreak(amount, entity, (livingEntity) -> {
				livingEntity.broadcastBreakEvent(slot);
			});
		}
	}

	@Nullable
	@Override
	public CompoundTag getShareTag(ItemStack stack) {
		CompoundTag tag = super.getShareTag(stack);
		IEnergyStorage cap = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
		if (cap instanceof EnergyStorage storage) {
			if (tag == null) {
				tag = new CompoundTag();
			}
			tag.put("EnergyStorage", storage.serializeNBT());
		}
		return tag;
	}

	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
		if (nbt != null && nbt.contains("EnergyStorage")) {
			IEnergyStorage cap = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
			if (cap instanceof EnergyStorage storage) {
				storage.deserializeNBT(nbt.getCompound("EnergyStorage"));
			}
		}
		super.readShareTag(stack, nbt);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);
		if (NBTHelper.hasKey(stack, ConstructionStick.UNBREAKABLE_KEY) && !NBTHelper.hasKey(stack, "Unbreakable")) {
			NBTHelper.setKey(stack, "Unbreakable", false);
		}
	}

	@Override
	public void appendHoverText(ItemStack itemstack, @Nullable Level level, List<Component> lines, TooltipFlag isAdvanced) {
		StickOptions options = new StickOptions(itemstack);
		int limit = options.upgrades.get().getStickAction().getLimit(itemstack);

		String langTooltip = ConstructionStick.MOD_ID + ".tooltip.";

		// +SHIFT tooltip: show all options + installed upgrades
		if (Screen.hasShiftDown()) {
			for (int i = 1; i < options.allOptions.length; i++) {
				IOption<?> opt = options.allOptions[i];
				lines.add(Component.translatable(opt.getKeyTranslation()).withStyle(ChatFormatting.AQUA)
						.append(Component.translatable(opt.getValueTranslation()).withStyle(ChatFormatting.GRAY))
				);
			}
			if (!options.upgrades.getUpgrades().isEmpty()) {
				lines.add(Component.literal(""));
				lines.add(Component.translatable(langTooltip + "upgrades").withStyle(ChatFormatting.GRAY));

				for (IStickTemplate upgrades : options.upgrades.getUpgrades()) {
					lines.add(Component.translatable(options.upgrades.getKeyTranslation() + "." + upgrades.getRegistryName().toString()));
				}
				for (IStickTemplate specialUpgrades : options.upgrades.getSpecialUpgrades()) {
					lines.add(Component.translatable(options.upgrades.getKeyTranslation() + "." + specialUpgrades.getRegistryName().toString()));
				}
			}
		}
		// Default tooltip: show block limit + active stick upgrade
		else {
			StickUpgradesSelectable<IStickTemplate> upgrades = options.upgrades;
			lines.add(Component.translatable(langTooltip + "blocks", limit).withStyle(ChatFormatting.GRAY));

			if (NBTHelper.hasKey(itemstack, ConstructionStick.BATTERY_KEY)) {
				IEnergyStorage storage = itemstack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
				if (storage != null) {
					int energy = storage.getEnergyStored();
					NumberFormat format = NumberFormat.getInstance();
					lines.add(Component.translatable("constructionstick.tooltip.storage", format.format(energy), format.format(storage.getMaxEnergyStored()))
							.withStyle(ChatFormatting.RED));
				}
			}

			lines.add(Component.translatable(upgrades.getKeyTranslation()).withStyle(ChatFormatting.AQUA)
					.append(Component.translatable(upgrades.getValueTranslation()).withStyle(ChatFormatting.WHITE)));
			lines.add(Component.translatable(langTooltip + "shift").withStyle(ChatFormatting.AQUA));
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
