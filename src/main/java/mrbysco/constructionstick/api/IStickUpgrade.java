package mrbysco.constructionstick.api;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;

public interface IStickUpgrade {
	Identifier getRegistryName();

	DataComponentType<?> getStickComponent();

	default boolean specialUpgrade() {
		return false;
	}

	default boolean incompatibleWith(IStickUpgrade other) {
		return false;
	}
}
