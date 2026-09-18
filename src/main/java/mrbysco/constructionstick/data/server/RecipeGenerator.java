package mrbysco.constructionstick.data.server;

import mrbysco.constructionstick.data.server.recipe.IngredientPredicate;
import mrbysco.constructionstick.data.server.recipe.SmithingApplyUpgradeRecipeBuilder;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.items.template.ItemUpgradeTemplate;
import mrbysco.constructionstick.registry.ModDataComponents;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

public class RecipeGenerator extends RecipeProvider {

	public RecipeGenerator(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
		super(recipeOutput, advancementOutput);
	}

	@Override
	protected void buildRecipes() {
		shaped(RecipeCategory.MISC, ModItems.STICK_TEMPLATE)
				.define('S', Tags.Items.RODS_WOODEN)
				.define('B', ItemTags.WOODEN_BUTTONS)
				.pattern(" S ")
				.pattern("SBS")
				.pattern(" S ")
				.unlockedBy("has_stick", has(Tags.Items.RODS_WOODEN))
				.unlockedBy("has_button", has(ItemTags.WOODEN_BUTTONS))
				.save(output);

		stickRecipe(output, ModItems.STICK_WOODEN, IngredientPredicate.fromTag(items, Tags.Items.RODS_WOODEN));
		shaped(RecipeCategory.TOOLS, ModItems.STICK_WOODEN)
				.define('X', tag(Tags.Items.RODS_WOODEN))
				.define('#', Tags.Items.RODS_WOODEN)
				.pattern("  X")
				.pattern(" # ")
				.pattern("#  ")
				.unlockedBy("has_item", has(Tags.Items.RODS_WOODEN))
				.save(output);

		stickRecipe(output, ModItems.STICK_COPPER, IngredientPredicate.fromTag(items, Tags.Items.INGOTS_COPPER));
		stickRecipe(output, ModItems.STICK_IRON, IngredientPredicate.fromTag(items, Tags.Items.INGOTS_IRON));
		stickRecipe(output, ModItems.STICK_DIAMOND, IngredientPredicate.fromTag(items, Tags.Items.GEMS_DIAMOND));
		stickRecipe(output, ModItems.STICK_NETHERITE, IngredientPredicate.fromTag(items, Tags.Items.INGOTS_NETHERITE));

		templateRecipe(output, ModItems.TEMPLATE_ANGEL.get(), IngredientPredicate.fromTag(items, Tags.Items.FEATHERS), IngredientPredicate.fromTag(items, Tags.Items.INGOTS_GOLD));
		templateRecipe(output, ModItems.TEMPLATE_DESTRUCTION.get(), IngredientPredicate.fromItem(items, Items.TNT), IngredientPredicate.fromItem(items, Items.DIAMOND_PICKAXE));
		templateRecipe(output, ModItems.TEMPLATE_REPLACEMENT.get(), IngredientPredicate.fromTag(items, Tags.Items.ENDER_PEARLS), IngredientPredicate.fromItem(items, Items.SCULK));
		templateRecipe(output, ModItems.TEMPLATE_UNBREAKABLE.get(), IngredientPredicate.fromTag(items, Tags.Items.NETHER_STARS), IngredientPredicate.fromTag(items, Tags.Items.OBSIDIANS_CRYING));
		templateRecipe(output, ModItems.TEMPLATE_BATTERY.get(), IngredientPredicate.fromItem(items, Items.POTATO), IngredientPredicate.fromTag(items, Tags.Items.DUSTS_REDSTONE));

		templateUpgradeRecipe(output, ModItems.TEMPLATE_ANGEL, IngredientPredicate.fromTag(items, Tags.Items.FEATHERS), ModDataComponents.ANGEL, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_DESTRUCTION, IngredientPredicate.fromTag(items, Tags.Items.STORAGE_BLOCKS_REDSTONE), ModDataComponents.DESTRUCTION, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_REPLACEMENT, IngredientPredicate.fromTag(items, Tags.Items.ENDER_PEARLS), ModDataComponents.REPLACEMENT, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_UNBREAKABLE, IngredientPredicate.fromTag(items, Tags.Items.OBSIDIANS_CRYING), ModDataComponents.UNBREAKABLE, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_BATTERY, IngredientPredicate.fromTag(items, Tags.Items.STORAGE_BLOCKS_COPPER), ModDataComponents.BATTERY_ENABLED, true);
	}

	private void stickRecipe(RecipeOutput output, DeferredItem<ItemStick> stick, IngredientPredicate material) {
		SmithingTransformRecipeBuilder.smithing(
						Ingredient.of(ModItems.STICK_TEMPLATE), tag(Tags.Items.RODS_WOODEN), material.ingredient(),
						RecipeCategory.TOOLS, stick.get()
				)
				.unlocks("has_item", inventoryTrigger(material.predicate()))
				.save(output, stick.getId().withPrefix("smithing_transform/").toString());
	}

	private <T> void templateUpgradeRecipe(RecipeOutput output, DeferredItem<? extends ItemUpgradeTemplate> template,
	                                       IngredientPredicate item1, Supplier<DataComponentType<T>> component, T defaultValue) {
		for (DeferredItem<? extends ItemStick> stickHolder : ModItems.STICKS) {
			ItemStackTemplate stack = new ItemStackTemplate(stickHolder,
					DataComponentPatch.builder().set(component.get(), defaultValue).build()
			);
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
}