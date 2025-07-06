package mrbysco.constructionstick.data.server;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.basics.ModTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class BlockTagsGenerator extends BlockTagsProvider {
	public BlockTagsGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider) {
		super(output, lookupProvider, ConstructionStick.MOD_ID);
	}

	@Override
	protected void addTags(Provider provider) {
		this.tag(ModTags.NON_REPLACEABLE).addTag(Tags.Blocks.RELOCATION_NOT_SUPPORTED);
	}
}
