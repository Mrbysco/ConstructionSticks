package mrbysco.constructionstick.data.client;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.Nullable;

public class LanguageGenerator extends LanguageProvider {

	public LanguageGenerator(PackOutput packOutput) {
		super(packOutput, ConstructionStick.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		add("itemGroup.constructionstick.tab", "Construction Sticks");

		addItem(ModItems.STICK_WOODEN, "Stickiest Stick");
		addItem(ModItems.STICK_COPPER, "Copper Stick");
		addItem(ModItems.STICK_IRON, "Iron Stick");
		addItem(ModItems.STICK_DIAMOND, "Diamond Stick");
		addItem(ModItems.STICK_NETHERITE, "Netherite Stick");
		addItem(ModItems.TEMPLATE_ANGEL, "Angel Stick Template");
		addItem(ModItems.TEMPLATE_DESTRUCTION, "Destruction Stick Template");
		addItem(ModItems.TEMPLATE_REPLACEMENT, "Replacement Stick Template");
		addItem(ModItems.TEMPLATE_UNBREAKABLE, "Unbreakable Template");
		addItem(ModItems.TEMPLATE_BATTERY, "Battery Template");

		add("constructionstick.tooltip.storage", "%s/%s RF stored");
		add("constructionstick.tooltip.blocks", "Max. %d blocks");
		add("constructionstick.tooltip.shift", "Press [SHIFT]");
		add("constructionstick.tooltip.upgrades", "Stick upgrades:");
		add("constructionstick.tooltip.upgrades_tip", "Apply the template to your stick in the smithing table");

		add("constructionstick.option.upgrades", "");
		addUpgradeInfo("default", "Construction", "Extend your building on the side facing you");
		addUpgradeInfo("upgrade_angel", "§6Angel", "Place behind blocks and in mid air");
		addUpgradeInfo("upgrade_destruction", "§cDestruction", "Destroys blocks on the side facing you");
		addUpgradeInfo("upgrade_replacement", "§5Replacement", "Replaces blocks with the block in your offhand");
		addUpgradeInfo("upgrade_unbreakable", "§dUnbreakable", "Allows placing without using durability");
		addUpgradeInfo("upgrade_battery", "§4Battery", "Use energy instead of durability");

		add("constructionstick.option.lock", "Restriction: ");
		add("constructionstick.option.lock.horizontal", "§aLeft/Right");
		add("constructionstick.option.lock.horizontal.desc", "Build a horizontal column in front of the original block");
		add("constructionstick.option.lock.vertical", "§aUp/Down");
		add("constructionstick.option.lock.vertical.desc", "Build a vertical column in front of the original block");
		add("constructionstick.option.lock.northsouth", "§6North/South");
		add("constructionstick.option.lock.northsouth.desc", "Build a row in N/S direction on top of the original block");
		add("constructionstick.option.lock.eastwest", "§6East/West");
		add("constructionstick.option.lock.eastwest.desc", "Build a row in E/W direction on top of the original block");
		add("constructionstick.option.lock.nolock", "§cNone");
		add("constructionstick.option.lock.nolock.desc", "Extend from any side of the original block");

		add("constructionstick.option.direction", "Direction: ");
		add("constructionstick.option.direction.target", "§6Target");
		add("constructionstick.option.direction.target.desc", "Place blocks with same direction as target block");
		add("constructionstick.option.direction.player", "§aPlayer");
		add("constructionstick.option.direction.player.desc", "Place blocks facing the player");

		add("constructionstick.option.replace", "Replacement: ");
		add("constructionstick.option.replace.yes", "§aYes");
		add("constructionstick.option.replace.yes.desc", "Replace certain blocks like fluids, snow and tallgrass");
		add("constructionstick.option.replace.no", "§cNo");
		add("constructionstick.option.replace.no.desc", "Don't replace blocks");

		add("constructionstick.option.match", "Matching: ");
		add("constructionstick.option.match.exact", "§aExact");
		add("constructionstick.option.match.exact.desc", "Only extend blocks that are exactly the same");
		add("constructionstick.option.match.similar", "§6Similar");
		add("constructionstick.option.match.similar.desc", "Treat similar blocks (dirt/grass types) equally");
		add("constructionstick.option.match.any", "§cAny");
		add("constructionstick.option.match.any.desc", "Extend any block");

		add("constructionstick.option.random", "Random: ");
		add("constructionstick.option.random.yes", "§aYes");
		add("constructionstick.option.random.yes.desc", "Place random blocks present in your hotbar");
		add("constructionstick.option.random.no", "§cNo");
		add("constructionstick.option.random.no.desc", "Don't randomize placed blocks");

		add("constructionstick.description.stick", "The %s can place up to %s blocks at the side of a building facing you and lasts %s.\n\nPress the bound %s key to change placement restriction (Horizontal, Vertical, North/South, East/West, No lock).\n\nOpen the option screen with the bound %s key.\n\n§5§nUNDO§0§r\nHolding down the bound %s key while looking at a blocks will show you the last blocks you placed with a green border around them. Pressing the bound %s key while looking at any of them will undo the operation, giving you all the items back. If you used the Destruction upgrade, it will restore the blocks.\n\n§5§nCONTAINERS§0§r\nShulker boxes, bundles and many containers from other mods can provide building blocks for the stick.\n\n§5§nOFFHAND PRIORITY§0§r\nHaving blocks in your offhand will place them instead of the block you're looking at.");
		add("constructionstick.description.durability.limited", "for %d blocks");
		add("constructionstick.description.key.sneak", "Sneak");
		add("constructionstick.description.key.sneak_opt", "Sneak+%s");
		add("constructionstick.description.upgrade", "§5§nINSTALLATION§0§r\nPut your new upgrade template together with your stick and the required item in a Smithing Table to apply (Check the Smithing Table recipes to see the required item). To switch between upgrades, press the bound %s key while holding your stick or use the option screen.");
		add("constructionstick.description.template_angel", "The angel upgrade places a block on the opposite side of the block (or row of blocks) you are facing. Maximum distance depends on stick tier. Right click empty space to place a block in midair. To do that, you'll need to have the block you want to place in your offhand.");
		add("constructionstick.description.template_destruction", "The destruction upgrade destroys blocks (no block entities) on the side facing you. Maximum number of blocks depends on stick tier. Destroyed blocks disappear into the void, you can use the undo feature if you've made a mistake.");
		add("constructionstick.description.template_replacement", "The replacement upgrade allows you to exchange blocks with the block of the type you are holding in your offhand. This can be useful for replacing walls after placement.");
		add("constructionstick.description.template_unbreakable", "The unbreakable upgrade allows you to place blocks without using durability. The stick will never break.");
		add("constructionstick.description.template_battery", "The battery upgrade allows you to use energy instead of durability. The stick will have the ability to store energy and use it to place blocks. Warning, you can only charge the stick if you have a mod that provides a way to do so.");

		add("stat.constructionstick.use_stick", "Blocks placed using Stick");

		addKeybind("category", "Construction Sticks");
		addKeybind("change_restriction", "Toggle Restriction");
		addKeybind("change_upgrade", "Toggle Upgrade");
		addKeybind("change_direction", "Toggle Direction");
		addKeybind("open_gui", "Open Stick Options");
		addKeybind("undo", "Undo Operation");
		addKeybind("show_previous", "Show Previous");

		add("constructionstick.networking.query_undo.failed", "Failed to undo operation: %s");
		add("constructionstick.networking.stick_option.undo", "Failed to change stick option: %s");
		add("constructionstick.networking.undo_blocks.failed", "Failed to undo blocks: %s");

		add("constructionstick.placement.denied", "This block cannot be placed with the Construction Stick!");

		add("constructionstick.alias.emi.construction", "Construction");
		add("constructionstick.alias.emi.wand", "Wand");
		add("constructionstick.alias.emi.construction_wand", "Construction Wand");

		addAdvancement("root", "Construction Sticks", "Making building easier");
		addAdvancement("iron_stick", "Sticky Situation", "Craft any Construction Stick");
		addAdvancement("template_angel", "Angel Stick Template", "I'm placing in mid-air, wo-oah!");
		addAdvancement("template_destruction", "Destruction Stick Template", "Destroyer of blocks");
		addAdvancement("template_replacement", "Replacement Stick Template", "Out with the old, in with the new");
		addAdvancement("template_unbreakable", "Unbreakable Template", "There's no limit!");
		addAdvancement("template_battery", "Battery Template", "Power up your construction game");

		addConfig(ModItems.STICK_WOODEN.getId().getPath(), "Stickiest Stick", "Stickiest Stick Settings");
		addConfig(ModItems.STICK_COPPER.getId().getPath(), "Copper Stick", "Copper Stick Settings");
		addConfig(ModItems.STICK_IRON.getId().getPath(), "Iron Stick", "Iron Stick Settings");
		addConfig(ModItems.STICK_DIAMOND.getId().getPath(), "Diamond Stick", "Diamond Stick Settings");
		addConfig(ModItems.STICK_NETHERITE.getId().getPath(), "Netherite Stick", "Netherite Stick Settings");
		addConfig("durability", "Durability", "Stick durability");
		addConfig("batteryStorage", "Battery Storage", "Battery power storage");
		addConfig("batteryUsage", "Battery Usage", "Battery power usage per block");
		addConfig("limit", "Placement Limit", "Max placement distance with angel upgrade (0 to disable angel upgrade)");
		addConfig("angel", "Angel Upgrade", "Stick destruction block limit (0 to disable destruction upgrade)");
		addConfig("destruction", "Destruction Upgrade", "Stick destruction block limit (0 to disable destruction upgrade)");
		addConfig("upgradeable", "Upgradeable", "Allow stick upgrading by putting the stick together with a stick upgrade in a smithing table.");

		addConfig("misc", "Misc", "Miscellaneous Settings");
		addConfig("MaxRange", "Max Range", "Maximum placement range (0: unlimited). Affects all sticks and is meant for lag prevention, not game balancing.");
		addConfig("UndoHistory", "Undo History", "Number of operations that can be undone");
		addConfig("AngelFalling", "Angel Falling", "Place blocks below you while falling > 10 blocks with angel upgrade (Can be used to save you from drops/the void)");
		addConfig("SimilarBlocks", "Similar Blocks", "Blocks to treat equally when in Similar mode. Enter block IDs separated by ;");
		addConfig("blockentity", "Block entity", "Block Entity Settings");
		addConfig("BEList", "Block Entity List", "White/Blacklist for Block Entities. Allow/Prevent blocks with BEs from being placed by stick. You can either add block ids like minecraft:chest or mod ids like minecraft");
		addConfig("BEWhitelist", "Block Entity Whitelist", "If set to TRUE, treat BEList as a whitelist, otherwise blacklist");
	}

	private void addKeybind(String path, String translation) {
		add("key." + ConstructionStick.MOD_ID + "." + path, translation);
	}

	private void addUpgradeInfo(String upgrade, String name, String desc) {
		add("constructionstick.option.upgrades.constructionstick:" + upgrade, name);
		add("constructionstick.option.upgrades.constructionstick:" + upgrade + ".desc", desc);
	}


	/**
	 * Add the translation of an advancement
	 *
	 * @param id          The advancement id
	 * @param name        The name of the advancement
	 * @param description The description of the advancement
	 */
	private void addAdvancement(String id, String name, String description) {
		String prefix = "advancement.constructionstick.";
		add(prefix + id + ".title", name);
		add(prefix + id + ".desc", description);
	}

	/**
	 * Add the translation for a config entry
	 *
	 * @param path        The path of the config entry
	 * @param name        The name of the config entry
	 * @param description The description of the config entry (optional in case of targeting "title" or similar entries that have no tooltip)
	 */
	private void addConfig(String path, String name, @Nullable String description) {
		this.add("constructionstick.configuration." + path, name);
		if (description != null && !description.isEmpty())
			this.add("constructionstick.configuration." + path + ".tooltip", description);
	}
}
