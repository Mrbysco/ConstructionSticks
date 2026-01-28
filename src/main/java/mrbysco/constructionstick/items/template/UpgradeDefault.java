package mrbysco.constructionstick.items.template;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.api.IStickTemplate;
import mrbysco.constructionstick.stick.action.ActionConstruction;
import net.minecraft.resources.ResourceLocation;

public class UpgradeDefault implements IStickTemplate {
	@Override
	public IStickAction getStickAction() {
		return new ActionConstruction();
	}

	@Override
	public ResourceLocation getRegistryName() {
		return ConstructionStick.modLoc("default");
	}

	@Override
	public String getUpgradeKey() {
		return "";
	}
}
