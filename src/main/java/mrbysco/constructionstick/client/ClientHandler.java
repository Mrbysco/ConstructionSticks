package mrbysco.constructionstick.client;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.client.property.SelectStickUpgrade;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.world.item.Item;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredItem;

public class ClientHandler {
	public static RenderBlockPreview renderBlockPreview;

	public static void onClientSetup(final FMLClientSetupEvent event) {
		renderBlockPreview = new RenderBlockPreview();
		NeoForge.EVENT_BUS.register(renderBlockPreview);
		NeoForge.EVENT_BUS.register(new KeybindHandler());
	}

	public static void registerKeymapping(final RegisterKeyMappingsEvent event) {
		event.register(KeybindHandler.KEY_CHANGE_RESTRICTION);
		event.register(KeybindHandler.KEY_CHANGE_UPGRADE);
		event.register(KeybindHandler.KEY_CHANGE_DIRECTION);
		event.register(KeybindHandler.KEY_OPEN_GUI);
		event.register(KeybindHandler.KEY_UNDO);
		event.register(KeybindHandler.KEY_SHOW_PREVIOUS);
	}

	public static void registerModelProperties(RegisterSelectItemModelPropertyEvent event) {
		event.register(ConstructionStick.modLoc("stick_upgrade"), SelectStickUpgrade.TYPE);
		for (DeferredItem<ItemStick> itemSupplier : ModItems.STICKS) {
			Item item = itemSupplier.get();
//			ItemProperties.register(
//					item, ConstructionStick.modLoc("angel_selected"),
//					(stack, world, entity, n) ->
//							stack.getItem() instanceof ItemStick &&
//									stack.has(ModDataComponents.SELECTED) &&
//									stack.get(ModDataComponents.SELECTED).equals(ItemAngelTemplate.UPGRADE_ID) ? 1 : 0
//			);
//			ItemProperties.register(
//					item, ConstructionStick.modLoc("destruction_selected"),
//					(stack, world, entity, n) ->
//							stack.getItem() instanceof ItemStick &&
//									stack.has(ModDataComponents.SELECTED) &&
//									stack.get(ModDataComponents.SELECTED).equals(ItemDestructionTemplate.UPGRADE_ID) ? 1 : 0
//			);
		}
	}
}
