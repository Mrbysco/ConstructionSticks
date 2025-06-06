package mrbysco.constructionstick.items.template;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.registry.ModDataComponents;
import mrbysco.constructionstick.stick.action.ActionAngel;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

public class ItemAngelTemplate extends ItemUpgradeTemplate {
	public static final ResourceLocation UPGRADE_ID = ConstructionStick.modLoc("upgrade_angel");

	public ItemAngelTemplate(Properties properties) {
		super(properties);
	}

	@Override
	public IStickAction getStickAction() {
		return new ActionAngel();
	}

	@Override
	public DataComponentType<Boolean> getStickComponent() {
		return ModDataComponents.ANGEL.get();
	}

	@Override
	public ResourceLocation getRegistryName() {
		return UPGRADE_ID;
	}
}
