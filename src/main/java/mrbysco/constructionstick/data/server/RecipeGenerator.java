package mrbysco.constructionstick.data.server;

import mrbysco.constructionstick.data.server.recipe.IngredientPredicate;
import mrbysco.constructionstick.data.server.recipe.SmithingApplyUpgradeRecipeBuilder;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.items.template.ItemUpgradeTemplate;
import mrbysco.constructionstick.registry.ModDataComponents;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RecipeGenerator extends RecipeProvider {
	public RecipeGenerator(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
		super(provider, recipeOutput);
	}

	@Override
	protected void buildRecipes() {
		stickRecipe(output, ModItems.STICK_WOODEN.get(), IngredientPredicate.fromTag(registries, Tags.Items.RODS_WOODEN));
		stickRecipe(output, ModItems.STICK_COPPER.get(), IngredientPredicate.fromTag(registries, Tags.Items.INGOTS_COPPER));
		stickRecipe(output, ModItems.STICK_IRON.get(), IngredientPredicate.fromTag(registries, Tags.Items.INGOTS_IRON));
		stickRecipe(output, ModItems.STICK_DIAMOND.get(), IngredientPredicate.fromTag(registries, Tags.Items.GEMS_DIAMOND));
		stickRecipe(output, ModItems.STICK_NETHERITE.get(), IngredientPredicate.fromTag(registries, Tags.Items.INGOTS_NETHERITE));

		templateRecipe(output, ModItems.TEMPLATE_ANGEL.get(), IngredientPredicate.fromTag(registries, Tags.Items.FEATHERS), IngredientPredicate.fromTag(registries, Tags.Items.INGOTS_GOLD));
		templateRecipe(output, ModItems.TEMPLATE_DESTRUCTION.get(), IngredientPredicate.fromItem(registries, Items.TNT), IngredientPredicate.fromItem(registries, Items.DIAMOND_PICKAXE));
		templateRecipe(output, ModItems.TEMPLATE_REPLACEMENT.get(), IngredientPredicate.fromTag(registries, Tags.Items.ENDER_PEARLS), IngredientPredicate.fromItem(registries, Items.SCULK));
		templateRecipe(output, ModItems.TEMPLATE_UNBREAKABLE.get(), IngredientPredicate.fromTag(registries, Tags.Items.NETHER_STARS), IngredientPredicate.fromTag(registries, Tags.Items.OBSIDIANS_CRYING));
		templateRecipe(output, ModItems.TEMPLATE_BATTERY.get(), IngredientPredicate.fromItem(registries, Items.POTATO), IngredientPredicate.fromTag(registries, Tags.Items.DUSTS_REDSTONE));

		templateUpgradeRecipe(output, ModItems.TEMPLATE_ANGEL, IngredientPredicate.fromTag(registries, Tags.Items.FEATHERS), ModDataComponents.ANGEL, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_DESTRUCTION, IngredientPredicate.fromTag(registries, Tags.Items.STORAGE_BLOCKS_REDSTONE), ModDataComponents.DESTRUCTION, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_REPLACEMENT, IngredientPredicate.fromTag(registries, Tags.Items.ENDER_PEARLS), ModDataComponents.REPLACEMENT, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_UNBREAKABLE, IngredientPredicate.fromTag(registries, Tags.Items.OBSIDIANS_CRYING), ModDataComponents.UNBREAKABLE, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_BATTERY, IngredientPredicate.fromTag(registries, Tags.Items.STORAGE_BLOCKS_COPPER), ModDataComponents.BATTERY_ENABLED, true);
	}

	private void stickRecipe(RecipeOutput output, ItemLike stick, IngredientPredicate material) {
		shaped(RecipeCategory.TOOLS, stick)
				.define('X', material.ingredient())
				.define('#', Tags.Items.RODS_WOODEN)
				.pattern("  X")
				.pattern(" # ")
				.pattern("#  ")
				.unlockedBy("has_item", inventoryTrigger(material.predicate()))
				.save(output);
	}

	private <T> void templateUpgradeRecipe(RecipeOutput output, DeferredItem<? extends ItemUpgradeTemplate> template,
	                                       IngredientPredicate item1, Supplier<DataComponentType<T>> component, T defaultValue) {
		for (DeferredItem<? extends ItemStick> stickHolder : ModItems.STICKS) {
			ItemStack stack = stickHolder.toStack();
			stack.set(component, defaultValue);
			SmithingApplyUpgradeRecipeBuilder.smithing(Ingredient.of(template.get()), Ingredient.of(stickHolder),
							item1.ingredient(), RecipeCategory.TOOLS, stack, template.get().getRegistryName())
					.unlocks("has_template", has(template.get()))
					.unlocks("has_stick", has(template.get()))
					.unlocks("has_addition", inventoryTrigger(item1.predicate()))
					.save(output, template.getId().withPrefix("smithing_upgrade/" + stickHolder.getId().getPath() + "_with_"));
		}

	}

	private void templateRecipe(RecipeOutput output, ItemLike template, IngredientPredicate item1, IngredientPredicate item2) {
		shaped(RecipeCategory.MISC, template)
				.define('O', item1.ingredient())
				.define('X', item2.ingredient())
				.define('#', Tags.Items.GLASS_BLOCKS)
				.pattern(" #X")
				.pattern("#O#")
				.pattern("X# ")
				.unlockedBy("has_item", inventoryTrigger(item1.predicate()))
				.save(output);
	}

	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<Provider> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			return new RecipeGenerator(provider, recipeOutput);
		}

		@Override
		public String getName() {
			return "Construction Sticks Recipes";
		}
	}
}