package mrbysco.constructionstick.integrations.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.IExtendableSmithingRecipeCategory;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.client.KeybindHandler;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.config.ConstructionConfig.StickProperties;
import mrbysco.constructionstick.integrations.jei.category.UpgradeCategory;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.items.template.ItemUpgradeTemplate;
import mrbysco.constructionstick.recipe.SmithingApplyUpgradeRecipe;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class ConstructionStickJeiPlugin implements IModPlugin {
	private static final ResourceLocation pluginId = ConstructionStick.modLoc(ConstructionStick.MOD_ID);
	private static final String baseKey = ConstructionStick.MOD_ID + ".description.";
	private static final String baseKeyItem = "item." + ConstructionStick.MOD_ID + ".";

	@NotNull
	@Override
	public ResourceLocation getPluginUid() {
		return pluginId;
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		IExtendableSmithingRecipeCategory smithingCategory = registration.getSmithingCategory();
		smithingCategory.addExtension(SmithingApplyUpgradeRecipe.class, new UpgradeCategory());
	}

	@Override
	public void registerRecipes(@NotNull IRecipeRegistration registration) {

		MutableComponent stickChangeUpgradeKey = Component.translatable(KeybindHandler.KEY_CHANGE_UPGRADE.getName()).withStyle(ChatFormatting.BLUE);
		MutableComponent stickOpenGuiKey = Component.translatable(KeybindHandler.KEY_OPEN_GUI.getName()).withStyle(ChatFormatting.BLUE);
		MutableComponent stickUndoKey = Component.translatable(KeybindHandler.KEY_UNDO.getName()).withStyle(ChatFormatting.BLUE);
		MutableComponent stickShowPrevious = Component.translatable(KeybindHandler.KEY_SHOW_PREVIOUS.getName()).withStyle(ChatFormatting.BLUE);

		for (RegistryObject<ItemStick> deferredStick : ModItems.STICKS) {
			Item stick = deferredStick.get();
			StickProperties stickProperties = ConstructionConfig.getStickProperties(stick);

			Component durabilityComponent = Component.translatable(baseKey + "durability.limited", stickProperties.getDurability());

			registration.addIngredientInfo(new ItemStack(stick), VanillaTypes.ITEM_STACK,
					Component.translatable(baseKey + "stick",
							Component.translatable(baseKeyItem + BuiltInRegistries.ITEM.getKey(stick).getPath()),
							stickProperties.getLimit(), durabilityComponent, stickChangeUpgradeKey, stickOpenGuiKey, stickShowPrevious, stickUndoKey, stickOpenGuiKey)
			);
		}

		for (RegistryObject<ItemUpgradeTemplate> templateSupplier : ModItems.TEMPLATES) {
			Item upgradeTemplate = templateSupplier.get();
			registration.addIngredientInfo(new ItemStack(upgradeTemplate), VanillaTypes.ITEM_STACK,
					Component.translatable(baseKey + BuiltInRegistries.ITEM.getKey(upgradeTemplate).getPath())
							.append("\n\n")
							.append(Component.translatable(baseKey + "upgrade", stickChangeUpgradeKey))
			);
		}
	}

	@Override
	public void registerIngredientAliases(IIngredientAliasRegistration registration) {
		for (RegistryObject<ItemStick> deferredStick : ModItems.STICKS) {
			registration.addAliases(VanillaTypes.ITEM_STACK, deferredStick.get().getDefaultInstance(), List.of("construction", "wand", "construction wand"));
		}
	}
}
