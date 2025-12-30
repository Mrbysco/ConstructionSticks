package mrbysco.constructionstick.items.template;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.registry.ModDataComponents;
import mrbysco.constructionstick.stick.action.ActionReplace;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;

public class ItemReplacementTemplate extends ItemUpgradeTemplate {
	public static final Identifier UPGRADE_ID = ConstructionStick.modLoc("upgrade_replacement");

	public ItemReplacementTemplate(Properties properties) {
		super(properties);
	}

	@Override
	public IStickAction getStickAction() {
		return new ActionReplace();
	}

	@Override
	public DataComponentType<Boolean> getStickComponent() {
		return ModDataComponents.REPLACEMENT.get();
	}

	@Override
	public Identifier getRegistryName() {
		return UPGRADE_ID;
	}
}
