package mrbysco.constructionstick.containers;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.containers.handlers.HandlerBundle;
import mrbysco.constructionstick.containers.handlers.HandlerCapability;
import mrbysco.constructionstick.containers.handlers.HandlerShulkerbox;

public class ContainerRegistrar {
	public static void register() {
		ConstructionStick.containerManager.register(new HandlerCapability());
		ConstructionStick.containerManager.register(new HandlerShulkerbox());
		ConstructionStick.containerManager.register(new HandlerBundle());

//        if(ModList.get().isLoaded("botania")) {
//            ConstructionStick.instance.containerManager.register(new HandlerBotania());
//            ConstructionStick.LOGGER.info("Botania integration added");
//        }
	}
}
