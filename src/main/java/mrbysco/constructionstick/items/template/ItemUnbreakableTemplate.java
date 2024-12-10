package mrbysco.constructionstick.items.template;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.api.IStickUpgrade;
import mrbysco.constructionstick.registry.ModDataComponents;
import mrbysco.constructionstick.stick.action.ActionAngel;
import mrbysco.constructionstick.stick.action.ActionConstruction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

public class ItemUnbreakableTemplate extends ItemUpgradeTemplate {
	public static final ResourceLocation UPGRADE_ID = ConstructionStick.modLoc("upgrade_unbreakable");

	public ItemUnbreakableTemplate(Properties properties) {
		super(properties);
	}

	@Override
	public IStickAction getStickAction() {
		return new ActionConstruction();
	}

	@Override
	public DataComponentType<Boolean> getStickComponent() {
		return ModDataComponents.UNBREAKABLE.get();
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
		return other.getRegistryName().equals(ItemBatteryTemplate.UPGRADE_ID);
	}
}
