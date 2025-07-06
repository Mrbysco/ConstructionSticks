package mrbysco.constructionstick.data.client;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.client.property.SelectStickUpgrade;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModelGenerator extends ModelProvider {
	public ModelGenerator(PackOutput output) {
		super(output, ConstructionStick.MOD_ID);
	}

	@Override
	protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
		for (DeferredHolder<Item, ? extends Item> deferredHolder : ModItems.ITEMS.getEntries()) {
			Item item = deferredHolder.get();
			String name = deferredHolder.getId().getPath();

			if (item instanceof ItemStick)
				generateStick(itemModels, item);
			else if (item instanceof BlockItem)
				blockModels.registerSimpleItemModel(item, ConstructionStick.modLoc("block/" + name));
			else
				itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
		}
	}

	public static final ModelTemplate TWO_LAYERED_HANDHELD = ModelTemplates.createItem("handheld", TextureSlot.LAYER0, TextureSlot.LAYER1);

	private void generateStick(ItemModelGenerators itemModels, Item item) {
		ResourceLocation location = ModelLocationUtils.getModelLocation(item);
		ItemModel.Unbaked model = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, ModelTemplates.FLAT_HANDHELD_ITEM));
		ItemModel.Unbaked angelModel = ItemModelUtils.plainModel(
				generateLayeredItem(
						itemModels,
						location.withSuffix("_angel"),
						ModelLocationUtils.getModelLocation(item), ConstructionStick.modLoc("item/overlay_angel")
				)
		);
		ItemModel.Unbaked destructionModel = ItemModelUtils.plainModel(
				generateLayeredItem(
						itemModels,
						location.withSuffix("_destruction"),
						ModelLocationUtils.getModelLocation(item), ConstructionStick.modLoc("item/overlay_destruction")
				)
		);

		List<SelectItemModel.SwitchCase<String>> list = new ArrayList<>(1);
		list.add(ItemModelUtils.when("angel", angelModel));
		list.add(ItemModelUtils.when("destruction", destructionModel));

		itemModels.itemModelOutput.accept(item, ItemModelUtils.select(new SelectStickUpgrade(), model, list));
	}

	public ResourceLocation generateLayeredItem(ItemModelGenerators itemModels, ResourceLocation modelLocation, ResourceLocation layer0, ResourceLocation layer1) {
		return TWO_LAYERED_HANDHELD.create(modelLocation, TextureMapping.layered(layer0, layer1), itemModels.modelOutput);
	}
}
