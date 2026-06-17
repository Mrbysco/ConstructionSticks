package mrbysco.constructionstick.data.server.recipe;

import mrbysco.constructionstick.recipe.SmithingApplyUpgradeRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.RecipeUnlockedTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmithingApplyUpgradeRecipeBuilder {
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;
	private final RecipeCategory category;
	private final ItemStackTemplate result;
	private final Identifier upgrade;
	private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

	public SmithingApplyUpgradeRecipeBuilder(Ingredient template, Ingredient base, Ingredient addition,
	                                         RecipeCategory category, ItemStackTemplate result,
	                                         Identifier upgradeComponent) {
		this.category = category;
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
		this.upgrade = upgradeComponent;
	}

	public static SmithingApplyUpgradeRecipeBuilder smithing(
			Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, ItemStackTemplate result, Identifier upgradeComponent
	) {
		return new SmithingApplyUpgradeRecipeBuilder(template, base, addition, category, result, upgradeComponent);
	}

	public SmithingApplyUpgradeRecipeBuilder unlocks(String key, net.minecraft.advancements.triggers.Criterion<InventoryChangeTrigger.TriggerInstance> criterion) {
		this.criteria.put(key, criterion);
		return this;
	}

	public void save(RecipeOutput recipeOutput, String recipeId) {
		this.save(recipeOutput, Identifier.parse(recipeId));
	}

	public void save(RecipeOutput recipeOutput, Identifier recipeId) {
		this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, recipeId));
	}

	public void save(RecipeOutput output, ResourceKey<Recipe<?>> resourceKey) {
		this.ensureValid(resourceKey);
		Advancement.Builder advancement$builder = output.advancement()
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
				.rewards(AdvancementRewards.Builder.recipe(resourceKey))
				.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(advancement$builder::addCriterion);
		SmithingApplyUpgradeRecipe applyUpgradeRecipe = new SmithingApplyUpgradeRecipe(new Recipe.CommonInfo(true),
				this.template, this.base, this.addition, this.result, this.upgrade
		);
		output.accept(
				resourceKey, applyUpgradeRecipe, advancement$builder.build(resourceKey.identifier().withPrefix("recipes/" + this.category.getFolderName() + "/"))
		);
	}

	private void ensureValid(ResourceKey<Recipe<?>> recipe) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipe.identifier());
		}
	}
}
