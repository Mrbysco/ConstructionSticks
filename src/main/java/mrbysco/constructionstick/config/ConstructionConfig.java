package mrbysco.constructionstick.config;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;

public class ConstructionConfig {
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.IntValue MAX_RANGE;
	public static final ForgeConfigSpec.IntValue UNDO_HISTORY;
	public static final ForgeConfigSpec.BooleanValue ANGEL_FALLING;

	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SIMILAR_BLOCKS;
	private static final String[] SIMILAR_BLOCKS_DEFAULT = {
			"minecraft:dirt;minecraft:grass_block;minecraft:coarse_dirt;minecraft:podzol;minecraft:mycelium;minecraft:farmland;minecraft:dirt_path;minecraft:rooted_dirt"
	};

	public static final ForgeConfigSpec.BooleanValue BE_WHITELIST;
	public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BE_LIST;
	private static final String[] BE_LIST_DEFAULT = {"chiselsandbits", "mekanism", "waystones"};

	private static final HashMap<ResourceLocation, StickProperties> stickProperties = new HashMap<>();

	public static StickProperties getStickProperties(Item stick) {
		return stickProperties.getOrDefault(BuiltInRegistries.ITEM.getKey(stick), StickProperties.DEFAULT);
	}

	public static class StickProperties {
		public static final StickProperties DEFAULT = new StickProperties(null, null, null, null, null, null, null);

		private final ForgeConfigSpec.IntValue durability;
		private final ForgeConfigSpec.IntValue batteryStorage;
		private final ForgeConfigSpec.IntValue batteryUsage;
		private final ForgeConfigSpec.IntValue limit;
		private final ForgeConfigSpec.IntValue angel;
		private final ForgeConfigSpec.IntValue destruction;
		private final ForgeConfigSpec.BooleanValue upgradeable;

		private StickProperties(ForgeConfigSpec.IntValue durability,
		                        ForgeConfigSpec.IntValue storage, ForgeConfigSpec.IntValue usage,
		                        ForgeConfigSpec.IntValue limit,
		                        ForgeConfigSpec.IntValue angel, ForgeConfigSpec.IntValue destruction,
		                        ForgeConfigSpec.BooleanValue upgradeable) {
			this.durability = durability;
			this.batteryStorage = storage;
			this.batteryUsage = usage;
			this.limit = limit;
			this.angel = angel;
			this.destruction = destruction;
			this.upgradeable = upgradeable;
		}

		public StickProperties(ForgeConfigSpec.Builder builder, RegistryObject<? extends Item> stick, int defDurability,
		                       int defStorage, int defUsage, int defLimit,
		                       int defAngel, int defDestruction, boolean defUpgradeable) {
			ResourceLocation registryName = stick.getId();
			String stickName = registryName.getPath();
			builder.push(stickName);

			if (defDurability > 0) {
				builder.comment("Stick durability");
				durability = builder.defineInRange("durability", defDurability, 1, Integer.MAX_VALUE);
			} else durability = null;
			batteryStorage = builder.defineInRange("batteryStorage", defStorage, 1, Integer.MAX_VALUE);
			builder.comment("Battery power storage");
			batteryUsage = builder.defineInRange("batteryUsage", defUsage, 1, Integer.MAX_VALUE);
			builder.comment("Battery power usage per block");
			limit = builder.defineInRange("limit", defLimit, 1, Integer.MAX_VALUE);
			builder.comment("Max placement distance with angel upgrade (0 to disable angel upgrade)");
			angel = builder.defineInRange("angel", defAngel, 0, Integer.MAX_VALUE);
			builder.comment("Stick destruction block limit (0 to disable destruction upgrade)");
			destruction = builder.defineInRange("destruction", defDestruction, 0, Integer.MAX_VALUE);
			builder.comment("Allow stick upgrading by putting the stick together with a stick upgrade in a smithing table.");
			upgradeable = builder.define("upgradeable", defUpgradeable);
			builder.pop();

			stickProperties.put(registryName, this);
		}

		public int getDurability() {
			return durability == null ? -1 : durability.get();
		}

		public int getBatteryStorage() {
			return batteryStorage == null ? 0 : batteryStorage.get();
		}

		public int getBatteryUsage() {
			return batteryUsage == null ? 0 : batteryUsage.get();
		}

		public int getLimit() {
			return limit == null ? 0 : limit.get();
		}

		public int getAngel() {
			return angel == null ? 0 : angel.get();
		}

		public int getDestruction() {
			return destruction == null ? 0 : destruction.get();
		}

		public boolean isUpgradeable() {
			return upgradeable != null && upgradeable.get();
		}
	}

	static {
		final var builder = new ForgeConfigSpec.Builder();

		new StickProperties(builder, ModItems.STICK_WOODEN, Tiers.WOOD.getUses(), 5000, 10, 3, 1, 1, true);
		new StickProperties(builder, ModItems.STICK_COPPER, Tiers.STONE.getUses(), 10000, 10, 9, 2, 3, true);
		new StickProperties(builder, ModItems.STICK_IRON, Tiers.IRON.getUses(), 25000, 10, 27, 4, 9, true);
		new StickProperties(builder, ModItems.STICK_DIAMOND, Tiers.DIAMOND.getUses(), 100000, 10, 128, 8, 25, true);
		new StickProperties(builder, ModItems.STICK_NETHERITE, Tiers.NETHERITE.getUses(), 200000, 10, 1024, 16, 81, true);

		builder.push("misc");
		builder.comment("Maximum placement range (0: unlimited). Affects all sticks and is meant for lag prevention, not game balancing.");
		MAX_RANGE = builder.defineInRange("MaxRange", 100, 0, Integer.MAX_VALUE);
		builder.comment("Number of operations that can be undone");
		UNDO_HISTORY = builder.defineInRange("UndoHistory", 3, 0, Integer.MAX_VALUE);
		builder.comment("Place blocks below you while falling > 10 blocks with angel upgrade (Can be used to save you from drops/the void)");
		ANGEL_FALLING = builder.define("AngelFalling", false);
		builder.comment("Blocks to treat equally when in Similar mode. Enter block IDs separated by ;");
		SIMILAR_BLOCKS = builder.defineListAllowEmpty("SimilarBlocks", List.of(SIMILAR_BLOCKS_DEFAULT), o -> (o instanceof String));
		builder.pop();

		builder.push("blockentity");
		builder.comment("White/Blacklist for Block Entities. Allow/Prevent blocks with BEs from being placed by stick.",
				"You can either add block ids like minecraft:chest or mod ids like minecraft");
		BE_LIST = builder.defineListAllowEmpty("BEList", List.of(BE_LIST_DEFAULT), o -> (o instanceof String));
		builder.comment("If set to TRUE, treat BEList as a whitelist, otherwise blacklist");
		BE_WHITELIST = builder.define("BEWhitelist", false);
		builder.pop();

		SPEC = builder.build();
	}
}
