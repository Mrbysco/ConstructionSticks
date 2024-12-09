package mrbysco.constructionstick.basics.option;

import mrbysco.constructionstick.ConstructionStick;
import net.minecraft.core.component.DataComponentType;

public interface IOption<T> {
	DataComponentType<?> getComponentType();

	String getKey();

	String getValueString();

	void setValueString(String val);

	default String getKeyTranslation() {
		return ConstructionStick.MOD_ID + ".option." + getKey();
	}

	default String getValueTranslation() {
		return ConstructionStick.MOD_ID + ".option." + getKey() + "." + getValueString();
	}

	default String getDescTranslation() {
		return ConstructionStick.MOD_ID + ".option." + getKey() + "." + getValueString() + ".desc";
	}

	boolean isEnabled();

	void set(T val);

	T get();

	T next(boolean dir);

	default T next() {
		return next(true);
	}
}
