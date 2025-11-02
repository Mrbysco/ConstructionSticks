package mrbysco.constructionstick.items.stick;

import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.registry.ModDataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public class ItemStickBasic extends ItemStick {

	public ItemStickBasic(Properties properties, ToolMaterial tier) {
		super(properties.durability(tier.durability()).repairable(tier.repairItems()));
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
			EnergyHandler storage = stack.getCapability(Capabilities.Energy.ITEM, null);
			if (storage != null) {
				return Math.round((13.0F / storage.getCapacityAsInt() * storage.getAmountAsInt()));
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
			EnergyHandler storage = stack.getCapability(Capabilities.Energy.ITEM, null);
			if (storage != null) {
				int usage = ConstructionConfig.getStickProperties(this).getBatteryUsage();
				return storage.getAmountAsInt() / usage;
			}
		}
		return stack.getMaxDamage() - stack.getDamageValue();
	}
}
