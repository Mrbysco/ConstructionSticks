//package mrbysco.constructionstick.integrations.emi.recipe;
//
//import dev.emi.emi.api.recipe.EmiRecipe;
//import dev.emi.emi.api.recipe.EmiRecipeCategory;
//import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
//import dev.emi.emi.api.render.EmiTexture;
//import dev.emi.emi.api.stack.EmiIngredient;
//import dev.emi.emi.api.stack.EmiStack;
//import dev.emi.emi.api.widget.WidgetHolder;
//import mrbysco.constructionstick.recipe.SmithingApplyUpgradeRecipe;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.crafting.Ingredient;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.List;
//
//public class ApplyUpgradeEMIRecipe implements EmiRecipe {
//	Ingredient template;
//	Ingredient base;
//	Ingredient addition;
//
//	List<EmiIngredient> inputs;
//	EmiStack output;
//	ResourceLocation id;
//
//	public ApplyUpgradeEMIRecipe(SmithingApplyUpgradeRecipe recipe, ResourceLocation id) {
//		this.template = recipe.getTemplate();
//		this.base = recipe.getBase();
//		this.addition = recipe.getAddition();
//
//		this.inputs = List.of(EmiIngredient.of(template), EmiIngredient.of(base), EmiIngredient.of(addition));
//		this.output = EmiStack.of(recipe.getResultItem(null));
//		this.id = id;
//	}
//
//	@Override
//	public EmiRecipeCategory getCategory() {
//		return VanillaEmiRecipeCategories.SMITHING;
//	}
//
//	@Override
//	public @Nullable ResourceLocation getId() {
//		return this.id;
//	}
//
//	@Override
//	public List<EmiIngredient> getInputs() {
//		return inputs;
//	}
//
//	@Override
//	public List<EmiStack> getOutputs() {
//		return List.of(output);
//	}
//
//	@Override
//	public int getDisplayWidth() {
//		return 112;
//	}
//
//	@Override
//	public int getDisplayHeight() {
//		return 18;
//	}
//
//	@Override
//	public void addWidgets(WidgetHolder widgets) {
//		widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 1);
//		widgets.addSlot(inputs.get(0), 0, 0);
//		widgets.addSlot(inputs.get(1), 18, 0);
//		widgets.addSlot(inputs.get(2), 36, 0);
//		widgets.addSlot(output, 94, 0).recipeContext(this);
//	}
//}
