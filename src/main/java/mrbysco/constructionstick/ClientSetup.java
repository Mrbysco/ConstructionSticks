package mrbysco.constructionstick;

import mrbysco.constructionstick.client.ClientHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Client-side setup class. Only loaded on client via DistExecutor.
 */
@OnlyIn(Dist.CLIENT)
public class ClientSetup {
	public static void register(IEventBus eventBus) {
		eventBus.addListener(ClientHandler::onClientSetup);
		eventBus.addListener(ClientHandler::registerKeymapping);
	}
}
