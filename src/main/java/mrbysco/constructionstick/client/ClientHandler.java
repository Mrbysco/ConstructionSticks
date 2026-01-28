package mrbysco.constructionstick.client;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.items.template.ItemAngelTemplate;
import mrbysco.constructionstick.items.template.ItemDestructionTemplate;
import mrbysco.constructionstick.registry.ModItems;
import mrbysco.constructionstick.util.NBTHelper;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

public class ClientHandler {
	public static RenderBlockPreview renderBlockPreview;

	public static void onClientSetup(final FMLClientSetupEvent event) {
		renderBlockPreview = new RenderBlockPreview();
		MinecraftForge.EVENT_BUS.register(renderBlockPreview);
		MinecraftForge.EVENT_BUS.register(new KeybindHandler());

		event.enqueueWork(ClientHandler::registerModelProperties);
	}

	public static void registerKeymapping(final RegisterKeyMappingsEvent event) {
		event.register(KeybindHandler.KEY_CHANGE_RESTRICTION);
		event.register(KeybindHandler.KEY_CHANGE_UPGRADE);
		event.register(KeybindHandler.KEY_CHANGE_DIRECTION);
		event.register(KeybindHandler.KEY_OPEN_GUI);
		event.register(KeybindHandler.KEY_UNDO);
		event.register(KeybindHandler.KEY_SHOW_PREVIOUS);
	}

	public static void registerModelProperties() {
		for (RegistryObject<ItemStick> itemSupplier : ModItems.STICKS) {
			Item item = itemSupplier.get();
			ItemProperties.register(
					item, ConstructionStick.modLoc("angel_selected"),
					(stack, world, entity, n) ->
							stack.getItem() instanceof ItemStick &&
									NBTHelper.hasKey(stack, ConstructionStick.SELECTED_KEY) &&
									NBTHelper.getSelectedUpgrade(stack) != null && NBTHelper.getSelectedUpgrade(stack).equals(ItemAngelTemplate.UPGRADE_ID) ? 1 : 0
			);
			ItemProperties.register(
					item, ConstructionStick.modLoc("destruction_selected"),
					(stack, world, entity, n) ->
							stack.getItem() instanceof ItemStick &&
									NBTHelper.hasKey(stack, ConstructionStick.SELECTED_KEY) &&
									NBTHelper.getSelectedUpgrade(stack) != null && NBTHelper.getSelectedUpgrade(stack).equals(ItemDestructionTemplate.UPGRADE_ID) ? 1 : 0
			);
		}
	}
}
