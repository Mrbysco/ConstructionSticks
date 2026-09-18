package mrbysco.constructionstick.data.server.recipe;

import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public record IngredientPredicate(Ingredient ingredient, ItemPredicate predicate) {

	public static IngredientPredicate fromItem(HolderGetter<Item> itemHolderGetter, ItemLike in) {
		return new IngredientPredicate(Ingredient.of(in), ItemPredicate.Builder.item()
				.of(itemHolderGetter, in).build());
	}

	public static IngredientPredicate fromTag(HolderGetter<Item> itemHolderGetter, TagKey<Item> in) {
		return new IngredientPredicate(Ingredient
				.of(tagSet(itemHolderGetter, in)), ItemPredicate.Builder.item()
				.of(itemHolderGetter, in)
				.build());
	}

	private static HolderSet<Item> tagSet(HolderGetter<Item> itemHolderGetter, TagKey<Item> tagKey) {
		return itemHolderGetter.getOrThrow(tagKey);
	}
}