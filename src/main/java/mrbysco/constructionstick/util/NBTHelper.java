package mrbysco.constructionstick.util;

import mrbysco.constructionstick.ConstructionStick;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NBTHelper {
	public static boolean hasKey(ItemStack stack, String key) {
		if (stack.hasTag()) {
			assert stack.getTag() != null;
			CompoundTag root = stack.getTagElement(ConstructionStick.OPTIONS_KEY);
			return root != null && root.contains(key);
		}
		return false;
	}

	public static void setKey(ItemStack stack, String key, boolean value) {
		stack.getOrCreateTagElement(ConstructionStick.OPTIONS_KEY)
				.putBoolean(key, value);
	}

	@Nullable
	public static ResourceLocation getSelectedUpgrade(ItemStack stack) {
		if (hasKey(stack, "SelectedUpgrade")) {
			assert stack.getTag() != null;
			return new ResourceLocation(stack.getTag().getString("SelectedUpgrade"));
		}
		return null;
	}
}
