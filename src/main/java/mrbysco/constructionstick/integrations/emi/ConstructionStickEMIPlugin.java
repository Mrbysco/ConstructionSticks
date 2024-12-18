package mrbysco.constructionstick.integrations.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.integrations.emi.recipe.ApplyUpgradeEMIRecipe;
import mrbysco.constructionstick.recipe.SmithingApplyUpgradeRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;

import java.util.Objects;

@EmiEntrypoint
public class ConstructionStickEMIPlugin implements EmiPlugin {
	private static final ResourceLocation UID = ConstructionStick.modLoc("emi_plugin");

	@Override
	public void register(EmiRegistry registry) {
		RecipeManager manager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
		for (RecipeHolder<SmithingRecipe> holder : manager.getAllRecipesFor(RecipeType.SMITHING).stream().filter(holder ->
				holder.value() instanceof SmithingApplyUpgradeRecipe).toList()) {
			SmithingApplyUpgradeRecipe recipe = (SmithingApplyUpgradeRecipe) holder.value();
			registry.addRecipe(new ApplyUpgradeEMIRecipe(recipe, holder.id()));
		}
	}
}
