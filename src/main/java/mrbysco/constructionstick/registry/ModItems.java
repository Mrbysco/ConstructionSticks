package mrbysco.constructionstick.registry;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.config.ConstructionConfig.StickProperties;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.items.stick.ItemStickBasic;
import mrbysco.constructionstick.items.template.ItemAngelTemplate;
import mrbysco.constructionstick.items.template.ItemBatteryTemplate;
import mrbysco.constructionstick.items.template.ItemDestructionTemplate;
import mrbysco.constructionstick.items.template.ItemReplacementTemplate;
import mrbysco.constructionstick.items.template.ItemUnbreakableTemplate;
import mrbysco.constructionstick.items.template.ItemUpgradeTemplate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber(modid = ConstructionStick.MOD_ID)
public class ModItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ConstructionStick.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ConstructionStick.MOD_ID);

	// Sticks
	public static final DeferredItem<ItemStick> STICK_WOODEN = ITEMS.registerItem("wooden_stick", (properties) -> new ItemStickBasic(properties, ToolMaterial.WOOD), propStick());
	public static final DeferredItem<ItemStick> STICK_COPPER = ITEMS.registerItem("copper_stick", (properties) -> new ItemStickBasic(properties, ToolMaterial.STONE), propStick());
	public static final DeferredItem<ItemStick> STICK_IRON = ITEMS.registerItem("iron_stick", (properties) -> new ItemStickBasic(properties, ToolMaterial.IRON), propStick());
	public static final DeferredItem<ItemStick> STICK_DIAMOND = ITEMS.registerItem("diamond_stick", (properties) -> new ItemStickBasic(properties, ToolMaterial.DIAMOND), propStick());
	public static final DeferredItem<ItemStick> STICK_NETHERITE = ITEMS.registerItem("netherite_stick", (properties) -> new ItemStickBasic(properties, ToolMaterial.NETHERITE), propStick().fireResistant());

	// Upgrade Templates
	public static final DeferredItem<ItemUpgradeTemplate> TEMPLATE_ANGEL = ITEMS.registerItem("template_angel", ItemAngelTemplate::new, propUpgrade());
	public static final DeferredItem<ItemUpgradeTemplate> TEMPLATE_DESTRUCTION = ITEMS.registerItem("template_destruction", ItemDestructionTemplate::new, propUpgrade());
	public static final DeferredItem<ItemUpgradeTemplate> TEMPLATE_REPLACEMENT = ITEMS.registerItem("template_replacement", ItemReplacementTemplate::new, propUpgrade());
	public static final DeferredItem<ItemUpgradeTemplate> TEMPLATE_UNBREAKABLE = ITEMS.registerItem("template_unbreakable", ItemUnbreakableTemplate::new, propUpgrade());
	public static final DeferredItem<ItemUpgradeTemplate> TEMPLATE_BATTERY = ITEMS.registerItem("template_battery", ItemBatteryTemplate::new, propUpgrade());

	// Collections
	public static final List<DeferredItem<ItemStick>> STICKS = List.of(STICK_WOODEN, STICK_COPPER, STICK_IRON, STICK_DIAMOND, STICK_NETHERITE);
	public static final List<DeferredItem<ItemUpgradeTemplate>> TEMPLATES = List.of(TEMPLATE_ANGEL, TEMPLATE_DESTRUCTION, TEMPLATE_REPLACEMENT, TEMPLATE_UNBREAKABLE, TEMPLATE_BATTERY);

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
			event.registerItem(Capabilities.Energy.ITEM, (stack, access) -> {
						StickProperties properties = ConstructionConfig.getStickProperties(holder.get());
						return new ItemAccessEnergyHandler(access, ModDataComponents.BATTERY.get(),
								properties.getBatteryStorage(), 200, properties.getBatteryUsage()) {
							@Override
							public int extract(int amount, TransactionContext transaction) {
								if (stack.has(ModDataComponents.BATTERY_ENABLED)) return 0;
								return super.extract(amount, transaction);
							}

							@Override
							public int insert(int amount, TransactionContext transaction) {
								if (stack.has(ModDataComponents.BATTERY_ENABLED)) return 0;
								return super.insert(amount, transaction);
							}
						};
					},
					holder.get()
			);
		}
	}
}
