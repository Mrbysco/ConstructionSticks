package mrbysco.constructionstick.data.server;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.data.server.recipe.IngredientPredicate;
import mrbysco.constructionstick.data.server.recipe.SmithingApplyUpgradeRecipeBuilder;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.items.template.ItemUpgradeTemplate;
import mrbysco.constructionstick.registry.ModItems;
import mrbysco.constructionstick.util.NBTHelper;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider {
	public RecipeGenerator(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> output) {
		stickRecipe(output, ModItems.STICK_WOODEN.get(), IngredientPredicate.fromTag(Tags.Items.RODS_WOODEN));
		stickRecipe(output, ModItems.STICK_COPPER.get(), IngredientPredicate.fromTag(Tags.Items.INGOTS_COPPER));
		stickRecipe(output, ModItems.STICK_IRON.get(), IngredientPredicate.fromTag(Tags.Items.INGOTS_IRON));
		stickRecipe(output, ModItems.STICK_DIAMOND.get(), IngredientPredicate.fromTag(Tags.Items.GEMS_DIAMOND));
		stickRecipe(output, ModItems.STICK_NETHERITE.get(), IngredientPredicate.fromTag(Tags.Items.INGOTS_NETHERITE));

		templateRecipe(output, ModItems.TEMPLATE_ANGEL.get(), IngredientPredicate.fromTag(Tags.Items.FEATHERS), IngredientPredicate.fromTag(Tags.Items.INGOTS_GOLD));
		templateRecipe(output, ModItems.TEMPLATE_DESTRUCTION.get(), IngredientPredicate.fromItem(Items.TNT), IngredientPredicate.fromItem(Items.DIAMOND_PICKAXE));
		templateRecipe(output, ModItems.TEMPLATE_REPLACEMENT.get(), IngredientPredicate.fromTag(Tags.Items.ENDER_PEARLS), IngredientPredicate.fromItem(Items.SCULK));
		templateRecipe(output, ModItems.TEMPLATE_UNBREAKABLE.get(), IngredientPredicate.fromTag(Tags.Items.NETHER_STARS), IngredientPredicate.fromItem(Items.CRYING_OBSIDIAN));
		templateRecipe(output, ModItems.TEMPLATE_BATTERY.get(), IngredientPredicate.fromItem(Items.POTATO), IngredientPredicate.fromTag(Tags.Items.DUSTS_REDSTONE));

		templateUpgradeRecipe(output, ModItems.TEMPLATE_ANGEL, IngredientPredicate.fromTag(Tags.Items.FEATHERS), ConstructionStick.ANGEL_KEY, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_DESTRUCTION, IngredientPredicate.fromTag(Tags.Items.STORAGE_BLOCKS_REDSTONE), ConstructionStick.DESTRUCTION_KEY, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_REPLACEMENT, IngredientPredicate.fromTag(Tags.Items.ENDER_PEARLS), ConstructionStick.REPLACEMENT_KEY, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_UNBREAKABLE, IngredientPredicate.fromItem(Items.CRYING_OBSIDIAN), ConstructionStick.UNBREAKABLE_KEY, true);
		templateUpgradeRecipe(output, ModItems.TEMPLATE_BATTERY, IngredientPredicate.fromTag(Tags.Items.STORAGE_BLOCKS_COPPER), ConstructionStick.BATTERY_KEY, true);
	}

	private void stickRecipe(Consumer<FinishedRecipe> output, ItemLike stick, IngredientPredicate material) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, stick)
				.define('X', material.ingredient())
				.define('#', Tags.Items.RODS_WOODEN)
				.pattern("  X")
				.pattern(" # ")
				.pattern("#  ")
				.unlockedBy("has_item", inventoryTrigger(material.predicate()))
				.save(output);
	}

	private <T> void templateUpgradeRecipe(Consumer<FinishedRecipe> output, RegistryObject<? extends ItemUpgradeTemplate> template,
	                                       IngredientPredicate item1, String upgradeKey, boolean defaultValue) {
		for (RegistryObject<? extends ItemStick> stickHolder : ModItems.STICKS) {
			ItemStack stack = new ItemStack(stickHolder.get());
			NBTHelper.setKey(stack, upgradeKey, defaultValue);
			SmithingApplyUpgradeRecipeBuilder.smithing(Ingredient.of(template.get()), Ingredient.of(stickHolder.get()),
							item1.ingredient(), RecipeCategory.TOOLS, stack, template.get().getRegistryName())
					.unlocks("has_template", has(template.get()))
					.unlocks("has_stick", has(template.get()))
					.unlocks("has_addition", inventoryTrigger(item1.predicate()))
					.save(output, template.getId().withPrefix("smithing_upgrade/" + stickHolder.getId().getPath() + "_with_"));
		}
	}

	private void templateRecipe(Consumer<FinishedRecipe> output, ItemLike template, IngredientPredicate item1, IngredientPredicate item2) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, template)
				.define('O', item1.ingredient())
				.define('X', item2.ingredient())
				.define('#', Tags.Items.GLASS)
				.pattern(" #X")
				.pattern("#O#")
				.pattern("X# ")
				.unlockedBy("has_item", inventoryTrigger(item1.predicate()))
				.save(output);
	}
}