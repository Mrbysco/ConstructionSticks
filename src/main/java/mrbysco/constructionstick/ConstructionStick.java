package mrbysco.constructionstick;

import mrbysco.constructionstick.containers.ContainerManager;
import mrbysco.constructionstick.network.ModMessages;
import mrbysco.constructionstick.registry.ModItems;
import mrbysco.constructionstick.stick.undo.UndoHistory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(ConstructionStick.MOD_ID)
public class ConstructionStick {
	public static final String MOD_ID = "constructionstick";
	public static final String MODNAME = "ConstructionSticks";
	public static final Logger LOGGER = LogManager.getLogger();

	// All keys for compatibility
	public static final String OPTIONS_KEY = "constructionstick:options";
	public static final String SELECTED_KEY = "SelectedUpgrade";
	public static final String ANGEL_KEY = "angel";
	public static final String BATTERY_KEY = "battery";
	public static final String DESTRUCTION_KEY = "destruction";
	public static final String REPLACEMENT_KEY = "replacement";
	public static final String UNBREAKABLE_KEY = "unbreakable";

	public static ContainerManager containerManager;
	public static UndoHistory undoHistory;

	@SuppressWarnings("removal")
	public ConstructionStick() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		containerManager = new ContainerManager();
		undoHistory = new UndoHistory();

		// Register common setup (network init)
		eventBus.addListener(this::onCommonSetup);

		// Register client setup only on client side
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSetup.register(eventBus));

		ModItems.ITEMS.register(eventBus);
		ModItems.CREATIVE_MODE_TABS.register(eventBus);

		LOGGER.info("ConstructionStick initialized");
	}

	private void onCommonSetup(final FMLCommonSetupEvent event) {
		ModMessages.init();
		LOGGER.info("ConstructionStick network initialized");
	}

	public static ResourceLocation modLoc(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}
}
