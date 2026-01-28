package mrbysco.constructionstick.recipe;

import com.google.gson.JsonObject;
import mrbysco.constructionstick.api.IStickTemplate;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.registry.ModRecipes;
import mrbysco.constructionstick.util.NBTHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

import java.util.stream.Stream;

/**
 * A copy of SmithingTransformRecipe specifically for applying upgrades to Construction Sticks
 */
public class SmithingApplyUpgradeRecipe implements SmithingRecipe {
	final ResourceLocation id;
	final Ingredient template;
	final Ingredient base;
	final Ingredient addition;
	final ItemStack result;
	final IStickTemplate upgrade;

	public SmithingApplyUpgradeRecipe(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition, ItemStack result, IStickTemplate upgrade) {
		this.id = id;
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
		this.upgrade = upgrade;
	}

	public SmithingApplyUpgradeRecipe(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition, ItemStack result, ResourceLocation upgrade) {
		this(id, template, base, addition, result,
				StickUtil.getUpgrade(upgrade).orElseThrow(
						() -> new IllegalArgumentException("Unknown upgrade: " + upgrade)
				)
		);
	}

	@Override
	public boolean matches(Container container, Level level) {
		if (container.getContainerSize() != 3) {
			return false;
		}
		ItemStack base = container.getItem(1);
		if (NBTHelper.hasKey(base, upgrade.getUpgradeKey()) ||
				!new StickOptions(base).upgrades.isCompatible(upgrade) ||
				!ConstructionConfig.getStickProperties(base.getItem()).isUpgradeable())
			return false;
		return this.template.test(container.getItem(0))
				&& this.base.test(base) &&
				this.addition.test(container.getItem(2));
	}

	@Override
	public ItemStack assemble(Container container, RegistryAccess registryAccess) {
		if (container.getContainerSize() != 3) {
			return ItemStack.EMPTY;
		}
		ItemStack base = container.getItem(1);

		ItemStack itemstack = base.copy();
		CompoundTag tag = itemstack.getOrCreateTag();
		tag = tag.merge(result.copy().getOrCreateTag());
		itemstack.setTag(tag);
		return itemstack;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return this.result;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public boolean isTemplateIngredient(ItemStack stack) {
		return this.template.test(stack);
	}

	@Override
	public boolean isBaseIngredient(ItemStack stack) {
		return this.base.test(stack);
	}

	@Override
	public boolean isAdditionIngredient(ItemStack stack) {
		return this.addition.test(stack);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipes.SMITHING_UPGRADE.get();
	}

	@Override
	public boolean isIncomplete() {
		return Stream.of(this.template, this.base, this.addition).anyMatch(Ingredient::isEmpty);
	}

	public Ingredient getBase() {
		return base;
	}

	public Ingredient getAddition() {
		return addition;
	}

	public Ingredient getTemplate() {
		return template;
	}

	public static class Serializer implements RecipeSerializer<SmithingApplyUpgradeRecipe> {
		public SmithingApplyUpgradeRecipe fromJson(ResourceLocation recipeId, JsonObject jsonObject) {
			Ingredient ingredient = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "template"));
			Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "base"));
			Ingredient ingredient2 = Ingredient.fromJson(GsonHelper.getNonNull(jsonObject, "addition"));
			ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
			ResourceLocation upgrade = new ResourceLocation(GsonHelper.getAsString(jsonObject, "upgrade"));
			return new SmithingApplyUpgradeRecipe(recipeId, ingredient, ingredient1, ingredient2, itemstack, upgrade);
		}

		public SmithingApplyUpgradeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf friendlyByteBuf) {
			Ingredient ingredient = Ingredient.fromNetwork(friendlyByteBuf);
			Ingredient ingredient1 = Ingredient.fromNetwork(friendlyByteBuf);
			Ingredient ingredient2 = Ingredient.fromNetwork(friendlyByteBuf);
			ItemStack itemstack = friendlyByteBuf.readItem();
			ResourceLocation upgrade = friendlyByteBuf.readResourceLocation();
			return new SmithingApplyUpgradeRecipe(recipeId, ingredient, ingredient1, ingredient2, itemstack, upgrade);
		}

		public void toNetwork(FriendlyByteBuf friendlyByteBuf, SmithingApplyUpgradeRecipe upgradeRecipe) {
			upgradeRecipe.template.toNetwork(friendlyByteBuf);
			upgradeRecipe.base.toNetwork(friendlyByteBuf);
			upgradeRecipe.addition.toNetwork(friendlyByteBuf);
			friendlyByteBuf.writeItem(upgradeRecipe.result);
			friendlyByteBuf.writeResourceLocation(upgradeRecipe.upgrade.getRegistryName());
		}
	}
}
