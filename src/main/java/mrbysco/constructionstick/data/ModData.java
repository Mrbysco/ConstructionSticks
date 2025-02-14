package mrbysco.constructionstick.data;

import mrbysco.constructionstick.data.client.ItemModelGenerator;
import mrbysco.constructionstick.data.client.LanguageGenerator;
import mrbysco.constructionstick.data.server.AdvancementGenerator;
import mrbysco.constructionstick.data.server.BlockTagsGenerator;
import mrbysco.constructionstick.data.server.ItemTagsGenerator;
import mrbysco.constructionstick.data.server.RecipeGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModData {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		ExistingFileHelper fileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		if (event.includeServer()) {
			BlockTagsProvider blockTags = new BlockTagsGenerator(packOutput, lookupProvider, fileHelper);
			generator.addProvider(true, blockTags);
			generator.addProvider(true, new ItemTagsGenerator(packOutput, lookupProvider, blockTags, fileHelper));
			generator.addProvider(true, new RecipeGenerator(packOutput, lookupProvider));
			generator.addProvider(true, new AdvancementGenerator(packOutput, lookupProvider, fileHelper));
		}

		if (event.includeClient()) {
			generator.addProvider(true, new LanguageGenerator(packOutput));
			generator.addProvider(true, new ItemModelGenerator(packOutput, fileHelper));
		}
	}
}
