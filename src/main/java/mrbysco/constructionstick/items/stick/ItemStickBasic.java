package mrbysco.constructionstick.items.stick;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.util.NBTHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemStickBasic extends ItemStick {
	private final Tier tier;

	public ItemStickBasic(Properties properties, Tier tier) {
		super(properties.durability(tier.getUses()));
		this.tier = tier;
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return !NBTHelper.hasKey(stack, ConstructionStick.UNBREAKABLE_KEY) && super.isDamageable(stack);

	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return super.isBarVisible(stack) || NBTHelper.hasKey(stack, ConstructionStick.BATTERY_KEY);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		if (NBTHelper.hasKey(stack, ConstructionStick.BATTERY_KEY)) {
			IEnergyStorage storage = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
			if (storage != null) {
				return Math.round((13.0F / storage.getMaxEnergyStored() * storage.getEnergyStored()));
			}
		}
		return super.getBarWidth(stack);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		if (NBTHelper.hasKey(stack, ConstructionStick.BATTERY_KEY)) {
			return 0x971607;
		}
		return super.getBarColor(stack);
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return ConstructionConfig.getStickProperties(this).getDurability();
	}

	@Override
	public int remainingDurability(ItemStack stack) {
		if (NBTHelper.hasKey(stack, ConstructionStick.UNBREAKABLE_KEY)) {
			return Integer.MAX_VALUE;
		}
		if (NBTHelper.hasKey(stack, ConstructionStick.BATTERY_KEY)) {
			IEnergyStorage storage = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
			if (storage != null) {
				int usage = ConstructionConfig.getStickProperties(this).getBatteryUsage();
				return storage.getEnergyStored() / usage;
			}
		}
		return stack.getMaxDamage() - stack.getDamageValue();
	}

	@Override
	public boolean isValidRepairItem(@NotNull ItemStack toRepair, @NotNull ItemStack repair) {
		return this.tier.getRepairIngredient().test(repair);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag tag) {
		ConstructionConfig.StickProperties properties = ConstructionConfig.getStickProperties(stack.getItem());
		return new BatteryEnergyStorage(stack, properties.getBatteryStorage(), 200, properties.getBatteryUsage());
	}

	private static class BatteryEnergyStorage implements ICapabilityProvider, INBTSerializable<Tag> {
		private final EnergyStorage storage;
		private final LazyOptional<IEnergyStorage> optional;

		public BatteryEnergyStorage(ItemStack stack, int capacity, int maxReceive, int maxTransfer) {
			this.storage = new EnergyStorage(capacity, maxReceive, maxTransfer) {
				@Override
				public boolean canExtract() {
					return NBTHelper.hasKey(stack, ConstructionStick.BATTERY_KEY) && super.canExtract();
				}

				@Override
				public boolean canReceive() {
					return NBTHelper.hasKey(stack, ConstructionStick.BATTERY_KEY) && super.canReceive();
				}
			};
			this.optional = LazyOptional.of(() -> this.storage);
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
			if (cap == ForgeCapabilities.ENERGY) {
				return this.optional.cast();
			}
			return LazyOptional.empty();
		}

		@Override
		public Tag serializeNBT() {
			return this.storage.serializeNBT();
		}

		@Override
		public void deserializeNBT(Tag nbt) {
			this.storage.deserializeNBT(nbt);
		}
	}
}
