package mrbysco.constructionstick.data.server.recipe;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public record Inp(String name, Ingredient ingredient, ItemPredicate predicate) {

	public static Inp fromItem(ItemLike in) {
		return new Inp(BuiltInRegistries.ITEM.getKey(in.asItem()).getPath(), Ingredient.of(in), ItemPredicate.Builder.item().of(in).build());
	}

	public static Inp fromTag(TagKey<Item> in) {
		return new Inp(in.location().getPath(), Ingredient.of(in), ItemPredicate.Builder.item().of(in).build());
	}
}