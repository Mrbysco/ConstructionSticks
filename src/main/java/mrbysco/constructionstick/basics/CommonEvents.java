package mrbysco.constructionstick.basics;

import mrbysco.constructionstick.ConstructionStick;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@EventBusSubscriber(modid = ConstructionStick.MOD_ID)
public class CommonEvents {
	@SubscribeEvent
	public static void serverStarting(ServerStartingEvent e) {
		ReplacementRegistry.init();
	}

	@SubscribeEvent
	public static void logOut(PlayerEvent.PlayerLoggedOutEvent e) {
		Player player = e.getEntity();
		if (player.level().isClientSide) return;
		ConstructionStick.undoHistory.removePlayer(player);
	}
}
