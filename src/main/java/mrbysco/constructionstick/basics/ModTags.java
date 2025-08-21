package mrbysco.constructionstick.basics;

import mrbysco.constructionstick.ConstructionStick;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
	public static final TagKey<Item> CONSTRUCTION_STICKS = ItemTags.create(ConstructionStick.modLoc("construction_sticks"));

	public static final TagKey<Block> NON_REPLACEABLE = BlockTags.create(ConstructionStick.modLoc("non_replaceable"));
	public static final TagKey<Block> NON_PLACABLE = BlockTags.create(ConstructionStick.modLoc("non_placable"));
}
