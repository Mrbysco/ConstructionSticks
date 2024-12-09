package mrbysco.constructionstick.data.client;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class ItemModelGenerator extends ItemModelProvider {
	public ItemModelGenerator(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
		super(packOutput, ConstructionStick.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		for (DeferredHolder<Item, ? extends Item> deferredHolder : ModItems.ITEMS.getEntries()) {
			Item item = deferredHolder.get();
			String name = deferredHolder.getId().getPath();

			if (item instanceof ItemStick)
				generateStick(name);
			else if (item instanceof BlockItem)
				withExistingParent(name, modLoc("block/" + name));
			else withExistingParent(name, "item/generated").texture("layer0", "item/" + name);
		}
	}

	private void generateStick(String name) {
		ModelFile stickAngel = withExistingParent(name + "_angel", "item/handheld")
				.texture("layer0", modLoc("item/" + name))
				.texture("layer1", modLoc("item/overlay_angel"));
		ModelFile stickDestruction = withExistingParent(name + "_destruction", "item/handheld")
				.texture("layer0", modLoc("item/" + name))
				.texture("layer1", modLoc("item/overlay_destruction"));

		withExistingParent(name, "item/handheld")
				.texture("layer0", modLoc("item/" + name))
				.override()
				.predicate(modLoc("angel_selected"), 1)
				.model(stickAngel)
				.end()
				.override()
				.predicate(modLoc("destruction_selected"), 1)
				.model(stickDestruction)
				.end();
	}

	@NotNull
	@Override
	public String getName() {
		return ConstructionStick.MODNAME + " item models";
	}
}
