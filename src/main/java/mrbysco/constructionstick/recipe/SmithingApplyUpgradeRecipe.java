package mrbysco.constructionstick.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mrbysco.constructionstick.api.IStickTemplate;
import mrbysco.constructionstick.basics.StickUtil;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleSmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SmithingRecipeDisplay;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * A copy of SmithingTransformRecipe specifically for applying upgrades to Construction Sticks
 */
public class SmithingApplyUpgradeRecipe extends SimpleSmithingRecipe {
	public static final MapCodec<SmithingApplyUpgradeRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
							Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
							Ingredient.CODEC.fieldOf("template").forGetter(o -> o.template),
							Ingredient.CODEC.fieldOf("base").forGetter(o -> o.base),
							Ingredient.CODEC.fieldOf("addition").forGetter(o -> o.addition),
							ItemStackTemplate.CODEC.fieldOf("result").forGetter(p_300935_ -> p_300935_.result),
							Identifier.CODEC.fieldOf("upgrade").forGetter(p_300935_ -> p_300935_.upgrade.getRegistryName())
					)
					.apply(i, SmithingApplyUpgradeRecipe::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, SmithingApplyUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
			Recipe.CommonInfo.STREAM_CODEC,
			o -> o.commonInfo,
			Ingredient.CONTENTS_STREAM_CODEC,
			o -> o.template,
			Ingredient.CONTENTS_STREAM_CODEC,
			o -> o.base,
			Ingredient.CONTENTS_STREAM_CODEC,
			o -> o.addition,
			ItemStackTemplate.STREAM_CODEC,
			o -> o.result,
			Identifier.STREAM_CODEC,
			o -> o.upgradeId,
			SmithingApplyUpgradeRecipe::new
	);
	public static final RecipeSerializer<SmithingApplyUpgradeRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);


	final Ingredient template;
	final Ingredient base;
	final Ingredient addition;
	final ItemStackTemplate result;
	final Identifier upgradeId;
	final IStickTemplate upgrade;
	@Nullable
	private PlacementInfo placementInfo;

	public SmithingApplyUpgradeRecipe(Recipe.CommonInfo commonInfo, Ingredient template, Ingredient base,
	                                  Ingredient addition, ItemStackTemplate result, Identifier upgrade) {
		super(commonInfo);
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
		this.upgradeId = upgrade;
		this.upgrade = StickUtil.getUpgrade(upgrade).orElseThrow(() ->
				new IllegalArgumentException("Unknown upgrade: " + upgrade));
	}

	@Override
	public boolean matches(SmithingRecipeInput input, Level level) {
		ItemStack base = input.base();
		if (base.has(upgrade.getStickComponent()) ||
				!new StickOptions(base).upgrades.isCompatible(upgrade) ||
				!ConstructionConfig.getStickProperties(base.getItem()).isUpgradeable())
			return false;
		return this.template.test(input.template()) && this.base.test(base) && this.addition.test(input.addition());
	}

	@Override
	public ItemStack assemble(SmithingRecipeInput input) {
		ItemStack resultStack = this.getResult();
		ItemStack itemstack = input.base().transmuteCopy(resultStack.getItem(), resultStack.getCount());
		itemstack.applyComponents(resultStack.getComponentsPatch());
		return itemstack;
	}

	public ItemStack getResult() {
		return this.result.create();
	}

	@Override
	public Optional<Ingredient> templateIngredient() {
		return Optional.of(this.template);
	}

	@Override
	public Ingredient baseIngredient() {
		return this.base;
	}

	@Override
	public Optional<Ingredient> additionIngredient() {
		return Optional.of(this.addition);
	}

	@Override
	public RecipeSerializer<SmithingApplyUpgradeRecipe> getSerializer() {
		return ModRecipes.SMITHING_UPGRADE.get();
	}

	@Override
	public PlacementInfo placementInfo() {
		if (this.placementInfo == null) {
			this.placementInfo = PlacementInfo.createFromOptionals(
					List.of(this.templateIngredient(), Optional.of(this.baseIngredient()), this.additionIngredient())
			);
		}

		return this.placementInfo;
	}

	@Override
	protected PlacementInfo createPlacementInfo() {
		return PlacementInfo.create(List.of(this.template, this.base, this.addition));
	}

	@Override
	public List<RecipeDisplay> display() {
		return List.of(
				new SmithingRecipeDisplay(
						Ingredient.optionalIngredientToDisplay(Optional.of(this.template)),
						Ingredient.optionalIngredientToDisplay(Optional.of(this.base)),
						Ingredient.optionalIngredientToDisplay(Optional.of(this.addition)),
						new SlotDisplay.ItemStackSlotDisplay(this.result),
						new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)
				)
		);
	}
}
