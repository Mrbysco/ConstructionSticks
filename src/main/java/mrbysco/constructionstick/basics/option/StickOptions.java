package mrbysco.constructionstick.basics.option;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickTemplate;
import mrbysco.constructionstick.basics.ReplacementRegistry;
import mrbysco.constructionstick.items.template.UpgradeDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class StickOptions {
	public enum LOCK implements StringRepresentable {
		HORIZONTAL(0),
		VERTICAL(1),
		NORTHSOUTH(2),
		EASTWEST(3),
		NOLOCK(4);

		private final int id;

		LOCK(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		@Override
		@NotNull
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

	public enum DIRECTION implements StringRepresentable {
		TARGET(0),
		PLAYER(1);

		private final int id;

		DIRECTION(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		@Override
		@NotNull
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

	public enum MATCH implements StringRepresentable {
		EXACT(0),
		SIMILAR(1),
		ANY(2);

		private final int id;

		MATCH(int id) {
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		@Override
		@NotNull
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

	public final CompoundTag tag;
	public final StickUpgradesSelectable<IStickTemplate> upgrades;

	public final OptionEnum<LOCK> lock;
	public final OptionEnum<DIRECTION> direction;
	public final OptionBoolean replace;
	public final OptionEnum<MATCH> match;
	public final OptionBoolean random;

	public final IOption<?>[] allOptions;

	public StickOptions(ItemStack stickStack) {
		tag = stickStack.getOrCreateTagElement(ConstructionStick.OPTIONS_KEY);

		upgrades = new StickUpgradesSelectable<>(tag, "upgrades", new UpgradeDefault());

		lock = new OptionEnum<>(tag, "lock", LOCK.class, LOCK.NOLOCK);
		direction = new OptionEnum<>(tag, "direction", DIRECTION.class, DIRECTION.TARGET);
		replace = new OptionBoolean(tag, "replace", true);
		match = new OptionEnum<>(tag, "match", MATCH.class, MATCH.SIMILAR);
		random = new OptionBoolean(tag, "random", false);

		allOptions = new IOption[]{upgrades, lock, direction, replace, match, random};
	}

	@Nullable
	public IOption<?> get(String key) {
		for (IOption<?> option : allOptions) {
			if (option.getKey().equals(key)) return option;
		}
		return null;
	}

	public boolean testLock(LOCK l) {
		if (lock.get() == LOCK.NOLOCK) return true;
		return lock.get() == l;
	}

	public boolean matchBlocks(Block b1, Block b2) {
		switch (match.get()) {
			case EXACT:
				return b1 == b2;
			case SIMILAR:
				return ReplacementRegistry.matchBlocks(b1, b2);
			case ANY:
				return b1 != Blocks.AIR && b2 != Blocks.AIR;
		}
		return false;
	}
}
