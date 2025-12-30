package mrbysco.constructionstick.basics.option;

import mrbysco.constructionstick.api.IStickUpgrade;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.registry.ModDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class StickUpgradesSelectable<T extends IStickUpgrade> implements IOption<T> {
	private final ItemStack stack;
	private final String key;
	protected final List<T> upgrades;
	protected final List<T> specialUpgrades;
	private byte selector;

	public StickUpgradesSelectable(ItemStack stack, String key, T dVal) {
		this.key = key;
		this.upgrades = new ArrayList<>();
		this.specialUpgrades = new ArrayList<>();
		this.stack = stack;
		this.populateList(dVal);

		// Update the selector to match the already selected upgrade if it exists
		if (this.stack.has(ModDataComponents.SELECTED.get())) {
			T upgrade = getUpgradeFromId(this.stack.get(ModDataComponents.SELECTED.get()));
			if (upgrade != null) {
				this.selector = (byte) this.upgrades.indexOf(upgrade);
			}
		}
	}

	private T getUpgradeFromId(Identifier id) {
		for (T upgrade : upgrades) {
			if (upgrade.getRegistryName().equals(id)) {
				return upgrade;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void populateList(T dVal) {
		if (dVal != null) upgrades.add(dVal);
		for (IStickUpgrade upgrade : StickUtil.getAllUpgrades()) {
			if (!this.stack.has(upgrade.getStickComponent())) continue;
			if (upgrade.specialUpgrade())
				specialUpgrades.add((T) upgrade);
			else
				upgrades.add((T) upgrade);
		}
	}

	@Override
	public DataComponentType<?> getComponentType() {
		return ModDataComponents.SELECTED.get();
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValueString() {
		return get().getRegistryName().toString();
	}

	@Override
	public void setValueString(String val) {
		for (byte i = 0; i < upgrades.size(); i++) {
			if (upgrades.get(i).getRegistryName().toString().equals(val)) {
				selector = i;
				set(upgrades.get(i));
				return;
			}
		}
	}

	@Override
	public boolean isEnabled() {
		return upgrades.size() > 1;
	}

	@Override
	public void set(T val) {
		selector = (byte) upgrades.indexOf(val);

		fixSelector();
		stack.set(ModDataComponents.SELECTED.get(), val.getRegistryName());
	}

	@Override
	public T get() {
		fixSelector();
		return upgrades.get(selector);
	}

	@Override
	public T next(boolean dir) {
		selector++;
		fixSelector();
		return get();
	}

	private void fixSelector() {
		if (selector < 0 || selector >= upgrades.size()) selector = 0;
	}

	public List<T> getUpgrades() {
		return upgrades;
	}

	public List<T> getSpecialUpgrades() {
		return specialUpgrades;
	}

	public boolean isCompatible(T upgrade) {
		List<T> allUpgrades = new ArrayList<>(upgrades);
		allUpgrades.addAll(specialUpgrades);
		return allUpgrades.stream().noneMatch(up -> up.incompatibleWith(upgrade));
	}
}
