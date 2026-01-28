package mrbysco.constructionstick;

import mrbysco.constructionstick.basics.ModStats;
import mrbysco.constructionstick.client.ClientHandler;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.containers.ContainerManager;
import mrbysco.constructionstick.containers.ContainerRegistrar;
import mrbysco.constructionstick.network.ModMessages;
import mrbysco.constructionstick.registry.ModItems;
import mrbysco.constructionstick.registry.ModRecipes;
import mrbysco.constructionstick.stick.undo.UndoHistory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(ConstructionStick.MOD_ID)
public class ConstructionStick {
	public static final String MOD_ID = "constructionstick";
	public static final String MODNAME = "ConstructionSticks";

	public static final Logger LOGGER = LogManager.getLogger();


	public static final String OPTIONS_KEY = "constructionstick:options";
	public static final String SELECTED_KEY = "SelectedUpgrade";
	public static final String ANGEL_KEY = "angel";
	public static final String BATTERY_KEY = "battery";
	public static final String DESTRUCTION_KEY = "destruction";
	public static final String REPLACEMENT_KEY = "replacement";
	public static final String UNBREAKABLE_KEY = "unbreakable";

	public static ContainerManager containerManager;
	public static UndoHistory undoHistory;

	public ConstructionStick() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		containerManager = new ContainerManager();
		undoHistory = new UndoHistory();

		// Register setup methods for modloading
		eventBus.addListener(this::commonSetup);
		// Register packets
		eventBus.addListener(this::setup);

		// Register Item DeferredRegister
//		ModDataComponents.DATA_COMPONENT_TYPES.register(eventBus);
		ModItems.ITEMS.register(eventBus);
		ModItems.CREATIVE_MODE_TABS.register(eventBus);
		ModStats.CUSTOM_STATS.register(eventBus);
		ModRecipes.RECIPE_SERIALIZERS.register(eventBus);

		// Config setup
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConstructionConfig.SPEC);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::onClientSetup);
			eventBus.addListener(ClientHandler::registerKeymapping);
		});
	}

	private void setup(final FMLCommonSetupEvent event) {
		ModMessages.init();
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		// Container registry
		ContainerRegistrar.register();
	}

	public static ResourceLocation modLoc(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}
}
