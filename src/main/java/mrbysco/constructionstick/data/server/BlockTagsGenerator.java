package mrbysco.constructionstick.data.server;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.basics.ModTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BlockTagsGenerator extends BlockTagsProvider {
	public BlockTagsGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider) {
		super(output, lookupProvider, ConstructionStick.MOD_ID);
	}

	@Override
	protected void addTags(Provider provider) {
		this.tag(ModTags.NON_REPLACEABLE).addTag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).addTag(ModTags.NON_PLACABLE);

		var tagAppender = this.tag(ModTags.NON_PLACABLE);
		List<ResourceLocation> reactors = List.of(
				modLoc("powah", "reactor_starter"),
				modLoc("powah", "reactor_basic"),
				modLoc("powah", "reactor_hardened"),
				modLoc("powah", "reactor_blazing"),
				modLoc("powah", "reactor_niotic"),
				modLoc("powah", "reactor_spirited"),
				modLoc("powah", "reactor_nitro")
		);
		reactors.forEach(tagAppender::addOptional);
	}

	private ResourceLocation modLoc(String modID, String path) {
		return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(modID, path);
	}
}
