package mrbysco.constructionstick;

import mrbysco.constructionstick.basics.ModStats;
import mrbysco.constructionstick.client.ClientHandler;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.containers.ContainerManager;
import mrbysco.constructionstick.containers.ContainerRegistrar;
import mrbysco.constructionstick.network.ModMessages;
import mrbysco.constructionstick.registry.ModDataComponents;
import mrbysco.constructionstick.registry.ModItems;
import mrbysco.constructionstick.registry.ModRecipes;
import mrbysco.constructionstick.stick.undo.UndoHistory;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(ConstructionStick.MOD_ID)
public class ConstructionStick {
	public static final String MOD_ID = "constructionstick";
	public static final String MODNAME = "ConstructionSticks";

	public static final Logger LOGGER = LogManager.getLogger();

	public static ContainerManager containerManager;
	public static UndoHistory undoHistory;

	public ConstructionStick(IEventBus eventBus, ModContainer container, Dist dist) {
		containerManager = new ContainerManager();
		undoHistory = new UndoHistory();

		// Register setup methods for modloading
		eventBus.addListener(this::commonSetup);
		// Register packets
		eventBus.addListener(ModMessages::registerPayloads);
		eventBus.addListener(ModItems::registerCapabilities);

		// Register Item DeferredRegister
		ModDataComponents.DATA_COMPONENT_TYPES.register(eventBus);
		ModItems.ITEMS.register(eventBus);
		ModItems.CREATIVE_MODE_TABS.register(eventBus);
		ModStats.CUSTOM_STATS.register(eventBus);
		ModRecipes.RECIPE_SERIALIZERS.register(eventBus);

		// Config setup
		container.registerConfig(ModConfig.Type.SERVER, ConstructionConfig.SPEC);

		if (dist.isClient()) {
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
			eventBus.addListener(ClientHandler::onClientSetup);
			eventBus.addListener(ClientHandler::registerModelProperties);
			eventBus.addListener(ClientHandler::registerKeymapping);
		}
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		// Container registry
		ContainerRegistrar.register();
	}

	public static ResourceLocation modLoc(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}
}
