package mrbysco.constructionstick.items.template;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.api.IStickUpgrade;
import mrbysco.constructionstick.stick.action.ActionConstruction;
import net.minecraft.resources.ResourceLocation;

public class ItemBatteryTemplate extends ItemUpgradeTemplate {
	public static final ResourceLocation UPGRADE_ID = ConstructionStick.modLoc("upgrade_battery");

	public ItemBatteryTemplate(Properties properties) {
		super(properties);
	}

	@Override
	public IStickAction getStickAction() {
		return new ActionConstruction();
	}

	@Override
	public String getUpgradeKey() {
		return ConstructionStick.BATTERY_KEY;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return UPGRADE_ID;
	}

	@Override
	public boolean specialUpgrade() {
		return true;
	}

	@Override
	public boolean incompatibleWith(IStickUpgrade other) {
		return other.getRegistryName().equals(ItemUnbreakableTemplate.UPGRADE_ID);
	}
}
