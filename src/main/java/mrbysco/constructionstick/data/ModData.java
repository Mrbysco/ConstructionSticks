package mrbysco.constructionstick.data;

import mrbysco.constructionstick.data.client.LanguageGenerator;
import mrbysco.constructionstick.data.client.ModelGenerator;
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
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModData {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		BlockTagsProvider blockTags = new BlockTagsGenerator(packOutput, lookupProvider);
		generator.addProvider(true, blockTags);
		generator.addProvider(true, new ItemTagsGenerator(packOutput, lookupProvider, blockTags));
		generator.addProvider(true, new RecipeGenerator.Runner(packOutput, lookupProvider));
		generator.addProvider(true, new AdvancementGenerator(packOutput, lookupProvider));

		generator.addProvider(true, new LanguageGenerator(packOutput));
		generator.addProvider(true, new ModelGenerator(packOutput));
	}
}
