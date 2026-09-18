package mrbysco.constructionstick.data;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.data.client.LanguageGenerator;
import mrbysco.constructionstick.data.client.ModelGenerator;
import mrbysco.constructionstick.data.server.AdvancementGenerator;
import mrbysco.constructionstick.data.server.BlockTagsGenerator;
import mrbysco.constructionstick.data.server.ItemTagsGenerator;
import mrbysco.constructionstick.data.server.RecipeGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

@EventBusSubscriber
public class ModData {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider(BlockTagsGenerator::new);
		event.createProvider(ItemTagsGenerator::new);

		event.createReloadableRegistryObjects(
				new RegistrySetBuilder()
						.add(RecipeProvider.asBootstrap(RecipeGenerator::new))
						.add(Registries.ADVANCEMENT, AdvancementGenerator.create()),
				Set.of(ConstructionStick.MOD_ID));

		event.createProvider(LanguageGenerator::new);
		event.createProvider(ModelGenerator::new);
	}
}
