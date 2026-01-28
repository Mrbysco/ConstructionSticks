package mrbysco.constructionstick.api;

import net.minecraft.resources.ResourceLocation;

public interface IStickUpgrade {
	ResourceLocation getRegistryName();

	String getUpgradeKey();

	default boolean specialUpgrade() {
		return false;
	}

	default boolean incompatibleWith(IStickUpgrade other) {
		return false;
	}
}
