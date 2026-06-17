package mrbysco.constructionstick.data.server;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.basics.ModTags;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ItemTagsGenerator extends ItemTagsProvider {
	public ItemTagsGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, ConstructionStick.MOD_ID);
	}

	@Override
	protected void addTags(Provider provider) {
		this.tag(ModTags.CONSTRUCTION_STICKS).add(ModItems.STICK_WOODEN.getKey(), ModItems.STICK_COPPER.getKey(), ModItems.STICK_IRON.getKey(),
				ModItems.STICK_DIAMOND.getKey(), ModItems.STICK_NETHERITE.getKey());
	}
}
