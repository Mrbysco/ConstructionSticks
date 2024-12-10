package mrbysco.constructionstick.items.stick;

import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.registry.ModDataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class ItemStickBasic extends ItemStick {
	private final Tier tier;

	public ItemStickBasic(Properties properties, Tier tier) {
		super(properties.durability(tier.getUses()));
		this.tier = tier;
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return !stack.has(ModDataComponents.UNBREAKABLE) && super.isDamageable(stack);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return super.isBarVisible(stack) || stack.has(ModDataComponents.BATTERY);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		if (stack.has(ModDataComponents.BATTERY_ENABLED)) {
			IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
			if (storage != null) {
				return Math.round((13.0F / storage.getMaxEnergyStored() * storage.getEnergyStored()));
			}
		}
		return super.getBarWidth(stack);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		if (stack.has(ModDataComponents.BATTERY_ENABLED)) {
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
		if (stack.has(ModDataComponents.UNBREAKABLE)) {
			return Integer.MAX_VALUE;
		}
		if (stack.has(ModDataComponents.BATTERY_ENABLED)) {
			IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
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
}
