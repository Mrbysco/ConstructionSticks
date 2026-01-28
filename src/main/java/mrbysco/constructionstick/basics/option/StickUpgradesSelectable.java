package mrbysco.constructionstick.basics.option;

import mrbysco.constructionstick.api.IStickUpgrade;
import mrbysco.constructionstick.basics.StickUtil;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class StickUpgradesSelectable<T extends IStickUpgrade> implements IOption<T> {
	private final CompoundTag tag;
	private final String key;
	protected final List<T> upgrades;
	protected final List<T> specialUpgrades;
	private byte selector;

	public StickUpgradesSelectable(CompoundTag tag, String key, T dVal) {
		this.tag = tag;
		this.key = key;
		this.upgrades = new ArrayList<>();
		this.specialUpgrades = new ArrayList<>();
		this.populateList(dVal);
	}

	@SuppressWarnings("unchecked")
	private void populateList(T dVal) {
		if (dVal != null) upgrades.add(dVal);
		for (IStickUpgrade upgrade : StickUtil.getAllUpgrades()) {
			if (!this.tag.contains(upgrade.getUpgradeKey())) continue;
			if (upgrade.specialUpgrade())
				specialUpgrades.add((T) upgrade);
			else
				upgrades.add((T) upgrade);
		}
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
				serializeSelector();
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
		serializeSelector();
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
		serializeSelector();
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

	private void serializeSelector() {
		tag.putByte(key + "_sel", selector);
	}

	public boolean isCompatible(T upgrade) {
		List<T> allUpgrades = new ArrayList<>(upgrades);
		allUpgrades.addAll(specialUpgrades);
		return allUpgrades.stream().noneMatch(up -> up.incompatibleWith(upgrade));
	}
}
