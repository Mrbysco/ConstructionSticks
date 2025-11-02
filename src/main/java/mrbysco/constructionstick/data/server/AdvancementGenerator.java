package mrbysco.constructionstick.data.server;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.basics.ModTags;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementGenerator extends AdvancementProvider {

	public AdvancementGenerator(PackOutput output, CompletableFuture<Provider> registries) {
		super(output, registries, List.of(new StickAdvancementGenerator()));
	}

	public static class StickAdvancementGenerator implements AdvancementSubProvider {

		@Override
		public void generate(Provider registries, Consumer<AdvancementHolder> consumer) {
			AdvancementHolder root = Advancement.Builder.advancement()
					.display(rootDisplay(ModItems.STICK_WOODEN, advancementPrefix("root.title"),
							advancementPrefix("root.desc"), mcLoc("textures/block/oak_planks.png")))
					.addCriterion("stick", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
							.of(registries.lookupOrThrow(Registries.ITEM), Tags.Items.RODS_WOODEN)))
					.save(consumer, rootID("root"));

			AdvancementHolder sticks = onHasItems(registries, consumer, ModItems.STICK_IRON, ModTags.CONSTRUCTION_STICKS, AdvancementType.TASK, root);

			AdvancementHolder angelTemplate = onHasItems(consumer, ModItems.TEMPLATE_ANGEL, AdvancementType.TASK, sticks);
			AdvancementHolder destructionTemplate = onHasItems(consumer, ModItems.TEMPLATE_DESTRUCTION, AdvancementType.TASK, sticks);
			AdvancementHolder replacementTemplate = onHasItems(consumer, ModItems.TEMPLATE_REPLACEMENT, AdvancementType.TASK, sticks);
			AdvancementHolder unbreakableTemplate = onHasItems(consumer, ModItems.TEMPLATE_UNBREAKABLE, AdvancementType.TASK, sticks);
			AdvancementHolder batteryTemplate = onHasItems(consumer, ModItems.TEMPLATE_BATTERY, AdvancementType.TASK, sticks);
		}

		/**
		 * Adds an advancement for holding a given item.
		 *
		 * @param consumer The consumer to add to.
		 * @param iconItem The block registry object.
		 * @param type     The frame type.
		 * @param root     The root advancement.
		 */
		protected static AdvancementHolder onHasItems(Consumer<AdvancementHolder> consumer, DeferredHolder<Item, ? extends Item> iconItem,
		                                              AdvancementType type, AdvancementHolder root) {
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
		protected static Criterion<TriggerInstance> hasItemsTrigger(ItemLike... items) {
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
		protected static AdvancementHolder onHasItems(Provider registries, Consumer<AdvancementHolder> consumer, DeferredHolder<Item, ? extends Item> iconItem, TagKey<Item> itemTag,
		                                              AdvancementType type, AdvancementHolder root) {
			String path = iconItem.getId().getPath();
			ResourceLocation registryLocation = modLoc(path);

			DisplayInfo info = simpleDisplay(iconItem.get(), path, type);
			return Advancement.Builder.advancement()
					.display(info)
					.parent(root)
					.addCriterion(path, hasItemsTrigger(registries, itemTag))
					.save(consumer, rootID(registryLocation.getPath()));
		}

		/**
		 * Get a trigger instance for holding items.
		 *
		 * @param registries The registries provider.
		 * @param itemTag The item tag.
		 * @return The trigger instance.
		 */
		protected static Criterion<TriggerInstance> hasItemsTrigger(Provider registries, TagKey<Item> itemTag) {
			return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
					.of(registries.lookupOrThrow(Registries.ITEM), itemTag));
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
					Optional.of(new ClientAsset.ResourceTexture(background)), AdvancementType.TASK, false, false, false);
		}

		/**
		 * Generate a simple DisplayInfo object.
		 *
		 * @param icon The icon to use.
		 * @param name The name of the advancement.
		 * @return The DisplayInfo object.
		 */
		protected static DisplayInfo simpleDisplay(ItemLike icon, String name, AdvancementType type) {
			return new DisplayInfo(new ItemStack(icon),
					Component.translatable(advancementPrefix(name + ".title")),
					Component.translatable(advancementPrefix(name + ".desc")),
					Optional.empty(), type, true, true, false);
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
