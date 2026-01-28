package mrbysco.constructionstick.data.server.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mrbysco.constructionstick.registry.ModRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SmithingApplyUpgradeRecipeBuilder {
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;
	private final RecipeCategory category;
	private final ItemStack result;
	private final ResourceLocation upgrade;
	private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();

	public SmithingApplyUpgradeRecipeBuilder(Ingredient template, Ingredient base, Ingredient addition,
	                                         RecipeCategory category, ItemStack result,
	                                         ResourceLocation upgradeComponent) {
		this.category = category;
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
		this.upgrade = upgradeComponent;
	}

	public static SmithingApplyUpgradeRecipeBuilder smithing(
			Ingredient template, Ingredient base, Ingredient addition, RecipeCategory category, ItemStack result, ResourceLocation upgradeComponent
	) {
		return new SmithingApplyUpgradeRecipeBuilder(template, base, addition, category, result, upgradeComponent);
	}

	public SmithingApplyUpgradeRecipeBuilder unlocks(String key, CriterionTriggerInstance criterion) {
		this.advancement.addCriterion(key, criterion);
		return this;
	}

	public void save(Consumer<FinishedRecipe> consumer, String recipeId) {
		this.save(consumer, ResourceLocation.parse(recipeId));
	}

	public void save(Consumer<FinishedRecipe> consumer, ResourceLocation recipeId) {
		this.ensureValid(recipeId);
		this.advancement.parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT)
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId))
				.requirements(RequirementsStrategy.OR);
		consumer.accept(new Result(recipeId, this.template, this.base,
				this.addition, this.result, this.upgrade, this.advancement, recipeId.withPrefix("recipes/" + this.category.getFolderName() + "/")));
	}

	public record Result(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition,
	                     ItemStack result,
	                     ResourceLocation upgrade, Advancement.Builder advancement,
	                     ResourceLocation advancementId) implements FinishedRecipe {
		public void serializeRecipeData(JsonObject json) {
			json.add("template", this.template.toJson());
			json.add("base", this.base.toJson());
			json.add("addition", this.addition.toJson());
			json.add("result", serializeItemStack(this.result));
			json.addProperty("upgrade", this.upgrade.toString());
		}

		public ResourceLocation getId() {
			return this.id;
		}

		public RecipeSerializer<?> getType() {
			return ModRecipes.SMITHING_UPGRADE.get();
		}

		@Nullable
		public JsonObject serializeAdvancement() {
			return this.advancement.serializeToJson();
		}

		@Nullable
		public ResourceLocation getAdvancementId() {
			return this.advancementId;
		}
	}

	static JsonElement serializeItemStack(@NotNull ItemStack stack) {
		JsonObject json = new JsonObject();
		json.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
		if (stack.getCount() > 1) {
			json.addProperty("count", stack.getCount());
		}
		if (stack.hasTag()) {
			assert stack.getTag() != null;
			stack.getTag().remove("Damage");
			json.addProperty("nbt", stack.getTag().toString());
		}
		return json;
	}

	private void ensureValid(ResourceLocation id) {
		if (this.advancement.getCriteria().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + id);
		}
	}
}
