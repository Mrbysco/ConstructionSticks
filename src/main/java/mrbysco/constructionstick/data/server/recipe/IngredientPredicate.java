package mrbysco.constructionstick.data.server.recipe;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public record IngredientPredicate(Ingredient ingredient, ItemPredicate predicate) {

	public static IngredientPredicate fromItem(Provider registries, ItemLike in) {
		return new IngredientPredicate(Ingredient.of(in), ItemPredicate.Builder.item()
				.of(registries.lookupOrThrow(Registries.ITEM), in).build());
	}

	public static IngredientPredicate fromTag(Provider registries, TagKey<Item> in) {
		return new IngredientPredicate(Ingredient
				.of(tagSet(registries, in)), ItemPredicate.Builder.item()
				.of(registries.lookupOrThrow(Registries.ITEM), in)
				.build());
	}

	private static HolderSet<Item> tagSet(Provider registries, TagKey<Item> tagKey) {
		return registries.lookupOrThrow(Registries.ITEM).getOrThrow(tagKey);
	}
}