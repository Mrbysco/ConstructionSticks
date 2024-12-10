package mrbysco.constructionstick.data.server.recipe;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public record IngredientPredicate(Ingredient ingredient, ItemPredicate predicate) {

	public static IngredientPredicate fromItem(ItemLike in) {
		return new IngredientPredicate(Ingredient.of(in), ItemPredicate.Builder.item().of(in).build());
	}

	public static IngredientPredicate fromTag(TagKey<Item> in) {
		return new IngredientPredicate(Ingredient.of(in), ItemPredicate.Builder.item().of(in).build());
	}
}