package mrbysco.constructionstick.registry;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.config.ConstructionConfig.StickProperties;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.items.stick.ItemStickBasic;
import mrbysco.constructionstick.items.template.ItemAngelTemplate;
import mrbysco.constructionstick.items.template.ItemBatteryTemplate;
import mrbysco.constructionstick.items.template.ItemDestructionTemplate;
import mrbysco.constructionstick.items.template.ItemUnbreakableTemplate;
import mrbysco.constructionstick.items.template.ItemUpgradeTemplate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = ConstructionStick.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ConstructionStick.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ConstructionStick.MOD_ID);

	// Sticks
	public static final DeferredItem<ItemStick> STICK_WOODEN = ITEMS.register("wooden_stick", () -> new ItemStickBasic(propStick(), Tiers.WOOD));
	public static final DeferredItem<ItemStick> STICK_COPPER = ITEMS.register("copper_stick", () -> new ItemStickBasic(propStick(), Tiers.STONE));
	public static final DeferredItem<ItemStick> STICK_IRON = ITEMS.register("iron_stick", () -> new ItemStickBasic(propStick(), Tiers.IRON));
	public static final DeferredItem<ItemStick> STICK_DIAMOND = ITEMS.register("diamond_stick", () -> new ItemStickBasic(propStick(), Tiers.DIAMOND));
	public static final DeferredItem<ItemStick> STICK_NETHERITE = ITEMS.register("netherite_stick", () -> new ItemStickBasic(propStick().fireResistant(), Tiers.NETHERITE));

	// Upgrade Templates
	public static final DeferredItem<ItemUpgradeTemplate> TEMPLATE_ANGEL = ITEMS.register("template_angel", () -> new ItemAngelTemplate(propUpgrade()));
	public static final DeferredItem<ItemUpgradeTemplate> TEMPLATE_DESTRUCTION = ITEMS.register("template_destruction", () -> new ItemDestructionTemplate(propUpgrade()));
	public static final DeferredItem<ItemUpgradeTemplate> TEMPLATE_UNBREAKABLE = ITEMS.register("template_unbreakable", () -> new ItemUnbreakableTemplate(propUpgrade()));
	public static final DeferredItem<ItemUpgradeTemplate> TEMPLATE_BATTERY = ITEMS.register("template_battery", () -> new ItemBatteryTemplate(propUpgrade()));

	// Collections
	public static final List<DeferredItem<ItemStick>> STICKS = List.of(STICK_WOODEN, STICK_COPPER, STICK_IRON, STICK_DIAMOND, STICK_NETHERITE);
	public static final List<DeferredItem<ItemUpgradeTemplate>> TEMPLATES = List.of(TEMPLATE_ANGEL, TEMPLATE_DESTRUCTION, TEMPLATE_UNBREAKABLE, TEMPLATE_BATTERY);

	public static final Supplier<CreativeModeTab> CONSTRUCTION_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
			.icon(() -> new ItemStack(STICK_WOODEN.get()))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.title(Component.translatable("itemGroup.constructionstick.tab"))
			.displayItems((displayParameters, output) -> {
				List<ItemStack> stacks = ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
				output.acceptAll(stacks);
			}).build());

	public static Item.Properties propStick() {
		return new Item.Properties();
	}

	private static Item.Properties propUpgrade() {
		return new Item.Properties().stacksTo(1);
	}

	@SubscribeEvent
	public static void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			for (DeferredItem<ItemStick> itemSupplier : STICKS) {
				event.accept(itemSupplier);
			}
		} else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			for (DeferredItem<ItemUpgradeTemplate> itemSupplier : TEMPLATES) {
				event.accept(itemSupplier);
			}
		}
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		for (DeferredHolder<Item, ? extends Item> holder : STICKS) {
			event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, context) -> {
						StickProperties properties = ConstructionConfig.getStickProperties(holder.get());
						return new ComponentEnergyStorage(stack, ModDataComponents.BATTERY.get(), properties.getBatteryStorage(), properties.getBatteryUsage());
					},
					holder.get()
			);
		}
	}
}
