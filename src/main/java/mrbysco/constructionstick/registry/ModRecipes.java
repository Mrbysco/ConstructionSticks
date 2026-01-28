package mrbysco.constructionstick.registry;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.recipe.SmithingApplyUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ConstructionStick.MOD_ID);

	public static final Supplier<RecipeSerializer<SmithingApplyUpgradeRecipe>> SMITHING_UPGRADE = RECIPE_SERIALIZERS.register("smithing_upgrade", SmithingApplyUpgradeRecipe.Serializer::new);

}
