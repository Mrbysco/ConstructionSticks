package mrbysco.constructionstick.items.template;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.stick.action.ActionReplace;
import net.minecraft.resources.ResourceLocation;

public class ItemReplacementTemplate extends ItemUpgradeTemplate {
	public static final ResourceLocation UPGRADE_ID = ConstructionStick.modLoc("upgrade_replacement");

	public ItemReplacementTemplate(Properties properties) {
		super(properties);
	}

	@Override
	public IStickAction getStickAction() {
		return new ActionReplace();
	}

	@Override
	public String getUpgradeKey() {
		return ConstructionStick.REPLACEMENT_KEY;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return UPGRADE_ID;
	}
}
