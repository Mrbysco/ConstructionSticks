package mrbysco.constructionstick.basics;

import mrbysco.constructionstick.ConstructionStick;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ConstructionStick.MOD_ID)
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
