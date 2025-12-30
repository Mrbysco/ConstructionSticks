package mrbysco.constructionstick.items.template;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.registry.ModDataComponents;
import mrbysco.constructionstick.stick.action.ActionDestruction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;

public class ItemDestructionTemplate extends ItemUpgradeTemplate {
	public static final Identifier UPGRADE_ID = ConstructionStick.modLoc("upgrade_destruction");

	public ItemDestructionTemplate(Properties properties) {
		super(properties);
	}

	@Override
	public IStickAction getStickAction() {
		return new ActionDestruction();
	}

	@Override
	public DataComponentType<Boolean> getStickComponent() {
		return ModDataComponents.DESTRUCTION.get();
	}

	@Override
	public Identifier getRegistryName() {
		return UPGRADE_ID;
	}
}
