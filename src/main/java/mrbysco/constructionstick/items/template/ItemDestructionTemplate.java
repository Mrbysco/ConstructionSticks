package mrbysco.constructionstick.items.template;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.stick.action.ActionDestruction;
import net.minecraft.resources.ResourceLocation;

public class ItemDestructionTemplate extends ItemUpgradeTemplate {
	public static final ResourceLocation UPGRADE_ID = ConstructionStick.modLoc("upgrade_destruction");

	public ItemDestructionTemplate(Properties properties) {
		super(properties);
	}

	@Override
	public IStickAction getStickAction() {
		return new ActionDestruction();
	}

	@Override
	public String getUpgradeKey() {
		return ConstructionStick.DESTRUCTION_KEY;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return UPGRADE_ID;
	}
}
