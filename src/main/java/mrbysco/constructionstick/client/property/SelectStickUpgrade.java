package mrbysco.constructionstick.client.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import mrbysco.constructionstick.registry.ModDataComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectStickUpgrade implements SelectItemModelProperty<String> {
	public static final SelectItemModelProperty.Type<SelectStickUpgrade, String> TYPE = SelectItemModelProperty.Type.create(
			MapCodec.unit(new SelectStickUpgrade()), Codec.STRING
	);

	@Nullable
	@Override
	public String get(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed,
	                  @NotNull ItemDisplayContext displayContext) {
		ResourceLocation selected = stack.getOrDefault(ModDataComponents.SELECTED, null);
		if (selected != null) {
			String id = selected.getPath();
			if (id.equals("angel") || id.equals("destruction")) {
				return id;
			}
		}
		return null;
	}

	@NotNull
	@Override
	public Type<? extends SelectItemModelProperty<String>, String> type() {
		return TYPE;
	}
}
