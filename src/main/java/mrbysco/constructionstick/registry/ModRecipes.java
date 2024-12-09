package mrbysco.constructionstick.registry;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.recipe.SmithingApplyUpgradeRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ConstructionStick.MOD_ID);

	public static final Supplier<RecipeSerializer<SmithingApplyUpgradeRecipe>> SMITHING_UPGRADE = RECIPE_SERIALIZERS.register("smithing_upgrade", SmithingApplyUpgradeRecipe.Serializer::new);

}
