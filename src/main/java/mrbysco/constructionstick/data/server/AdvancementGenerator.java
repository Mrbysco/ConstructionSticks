package mrbysco.constructionstick.data.server;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.basics.ModTags;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementGenerator extends ForgeAdvancementProvider {

	public AdvancementGenerator(PackOutput output, CompletableFuture<Provider> registries, ExistingFileHelper existingFileHelper) {
		super(output, registries, existingFileHelper, List.of(new StickAdvancementGenerator()));
	}

	public static class StickAdvancementGenerator implements AdvancementGenerator {
		@Override
		public void generate(Provider registries, Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper) {
			Advancement root = Advancement.Builder.advancement()
					.display(rootDisplay(ModItems.STICK_WOODEN.get(), advancementPrefix("root.title"),
							advancementPrefix("root.desc"), mcLoc("textures/block/oak_planks.png")))
					.addCriterion("stick", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(Tags.Items.RODS_WOODEN).build()))
					.save(consumer, rootID("root"));

			Advancement sticks = onHasItems(consumer, ModItems.STICK_IRON, ModTags.CONSTRUCTION_STICKS, FrameType.TASK, root);

			Advancement angelTemplate = onHasItems(consumer, ModItems.TEMPLATE_ANGEL, FrameType.TASK, sticks);
			Advancement destructionTemplate = onHasItems(consumer, ModItems.TEMPLATE_DESTRUCTION, FrameType.TASK, sticks);
			Advancement replacementTemplate = onHasItems(consumer, ModItems.TEMPLATE_REPLACEMENT, FrameType.TASK, sticks);
			Advancement unbreakableTemplate = onHasItems(consumer, ModItems.TEMPLATE_UNBREAKABLE, FrameType.TASK, sticks);
			Advancement batteryTemplate = onHasItems(consumer, ModItems.TEMPLATE_BATTERY, FrameType.TASK, sticks);
		}

		/**
		 * Adds an advancement for holding a given item.
		 *
		 * @param consumer The consumer to add to.
		 * @param iconItem The block registry object.
		 * @param type     The frame type.
		 * @param root     The root advancement.
		 */
		protected static Advancement onHasItems(Consumer<Advancement> consumer, RegistryObject<? extends Item> iconItem,
		                                        FrameType type, Advancement root) {
			String path = iconItem.getId().getPath();
			ResourceLocation registryLocation = modLoc(path);

			DisplayInfo info = simpleDisplay(iconItem.get(), path, type);
			return Advancement.Builder.advancement()
					.display(info)
					.parent(root)
					.addCriterion(path, hasItemsTrigger(iconItem.get()))
					.save(consumer, rootID(registryLocation.getPath()));
		}

		/**
		 * Get a trigger instance for holding items.
		 *
		 * @param items The items.
		 * @return The trigger instance.
		 */
		protected static TriggerInstance hasItemsTrigger(ItemLike... items) {
			return InventoryChangeTrigger.TriggerInstance.hasItems(items);
		}

		/**
		 * Adds an advancement for holding a given item.
		 *
		 * @param consumer The consumer to add to.
		 * @param iconItem The icon item.
		 * @param itemTag  The item tag.
		 * @param type     The frame type.
		 * @param root     The root advancement.
		 */
		protected static Advancement onHasItems(Consumer<Advancement> consumer, RegistryObject<? extends Item> iconItem, TagKey<Item> itemTag,
		                                        FrameType type, Advancement root) {
			String path = iconItem.getId().getPath();
			ResourceLocation registryLocation = modLoc(path);

			DisplayInfo info = simpleDisplay(iconItem.get(), path, type);
			return Advancement.Builder.advancement()
					.display(info)
					.parent(root)
					.addCriterion(path, hasItemsTrigger(itemTag))
					.save(consumer, rootID(registryLocation.getPath()));
		}

		/**
		 * Get a trigger instance for holding items.
		 *
		 * @param itemTag The item tag.
		 * @return The trigger instance.
		 */
		protected static TriggerInstance hasItemsTrigger(TagKey<Item> itemTag) {
			return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(itemTag).build());
		}

		/**
		 * Generate a root DisplayInfo object.
		 *
		 * @param icon       The icon to use.
		 * @param titleKey   The title key.
		 * @param descKey    The description key.
		 * @param background The background texture.
		 * @return The DisplayInfo object.
		 */
		protected static DisplayInfo rootDisplay(ItemLike icon, String titleKey, String descKey, ResourceLocation background) {
			return new DisplayInfo(new ItemStack(icon),
					Component.translatable(titleKey),
					Component.translatable(descKey),
					background, FrameType.TASK, false, false, false);
		}

		/**
		 * Generate a simple DisplayInfo object.
		 *
		 * @param icon The icon to use.
		 * @param name The name of the advancement.
		 * @return The DisplayInfo object.
		 */
		protected static DisplayInfo simpleDisplay(ItemLike icon, String name, FrameType type) {
			return new DisplayInfo(new ItemStack(icon),
					Component.translatable(advancementPrefix(name + ".title")),
					Component.translatable(advancementPrefix(name + ".desc")),
					null, type, true, true, false);
		}

		/**
		 * Generate a ResourceLocation that has the mod ID as the namespace.
		 *
		 * @param path The path.
		 * @return The ResourceLocation.
		 */
		private static ResourceLocation modLoc(String path) {
			return ConstructionStick.modLoc(path);
		}

		/**
		 * Generate a ResourceLocation that has the Minecraft namespace.
		 *
		 * @param path The path.
		 * @return The ResourceLocation.
		 */
		private static ResourceLocation mcLoc(String path) {
			return ResourceLocation.withDefaultNamespace(path);
		}

		/**
		 * Generate an advancement prefix.
		 *
		 * @param name The name of the advancement.
		 * @return The prefix.
		 */
		private static String advancementPrefix(String name) {
			return "advancement." + ConstructionStick.MOD_ID + "." + name;
		}

		/**
		 * Generate a root advancement ID.
		 *
		 * @param name The name of the advancement.
		 * @return The advancement ID.
		 */
		private static String rootID(String name) {
			return modLoc(name).toString();
		}
	}
}
