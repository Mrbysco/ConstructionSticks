package mrbysco.constructionstick.registry;

import mrbysco.constructionstick.ConstructionStick;
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
import net.minecraft.world.item.Tiers;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ConstructionStick.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ConstructionStick.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ConstructionStick.MOD_ID);

	// Sticks
	public static final RegistryObject<ItemStick> STICK_WOODEN = ITEMS.register("wooden_stick", () -> new ItemStickBasic(propStick(), Tiers.WOOD));
	public static final RegistryObject<ItemStick> STICK_COPPER = ITEMS.register("copper_stick", () -> new ItemStickBasic(propStick(), Tiers.STONE));
	public static final RegistryObject<ItemStick> STICK_IRON = ITEMS.register("iron_stick", () -> new ItemStickBasic(propStick(), Tiers.IRON));
	public static final RegistryObject<ItemStick> STICK_DIAMOND = ITEMS.register("diamond_stick", () -> new ItemStickBasic(propStick(), Tiers.DIAMOND));
	public static final RegistryObject<ItemStick> STICK_NETHERITE = ITEMS.register("netherite_stick", () -> new ItemStickBasic(propStick().fireResistant(), Tiers.NETHERITE));

	// Upgrade Templates
	public static final RegistryObject<ItemUpgradeTemplate> TEMPLATE_ANGEL = ITEMS.register("template_angel", () -> new ItemAngelTemplate(propUpgrade()));
	public static final RegistryObject<ItemUpgradeTemplate> TEMPLATE_DESTRUCTION = ITEMS.register("template_destruction", () -> new ItemDestructionTemplate(propUpgrade()));
	public static final RegistryObject<ItemUpgradeTemplate> TEMPLATE_REPLACEMENT = ITEMS.register("template_replacement", () -> new ItemReplacementTemplate(propUpgrade()));
	public static final RegistryObject<ItemUpgradeTemplate> TEMPLATE_UNBREAKABLE = ITEMS.register("template_unbreakable", () -> new ItemUnbreakableTemplate(propUpgrade()));
	public static final RegistryObject<ItemUpgradeTemplate> TEMPLATE_BATTERY = ITEMS.register("template_battery", () -> new ItemBatteryTemplate(propUpgrade()));

	// Collections
	public static final List<RegistryObject<ItemStick>> STICKS = List.of(STICK_WOODEN, STICK_COPPER, STICK_IRON, STICK_DIAMOND, STICK_NETHERITE);
	public static final List<RegistryObject<ItemUpgradeTemplate>> TEMPLATES = List.of(TEMPLATE_ANGEL, TEMPLATE_DESTRUCTION, TEMPLATE_REPLACEMENT, TEMPLATE_UNBREAKABLE, TEMPLATE_BATTERY);

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
			for (RegistryObject<ItemStick> itemSupplier : STICKS) {
				event.accept(itemSupplier);
			}
		} else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			for (RegistryObject<ItemUpgradeTemplate> itemSupplier : TEMPLATES) {
				event.accept(itemSupplier);
			}
		}
	}
}
