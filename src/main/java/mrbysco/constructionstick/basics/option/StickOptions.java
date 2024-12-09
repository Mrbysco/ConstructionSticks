package mrbysco.constructionstick.basics.option;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import mrbysco.constructionstick.api.IStickTemplate;
import mrbysco.constructionstick.basics.ReplacementRegistry;
import mrbysco.constructionstick.items.template.UpgradeDefault;
import mrbysco.constructionstick.registry.ModDataComponents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.IntFunction;

public class StickOptions {
	public enum LOCK implements StringRepresentable {
		HORIZONTAL(0),
		VERTICAL(1),
		NORTHSOUTH(2),
		EASTWEST(3),
		NOLOCK(4);

		public static final Codec<LOCK> CODEC = StringRepresentable.fromEnum(LOCK::values);
		private static final IntFunction<LOCK> BY_ID = ByIdMap.continuous(LOCK::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		public static final StreamCodec<ByteBuf, LOCK> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, LOCK::getId);

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

		public static final Codec<DIRECTION> CODEC = StringRepresentable.fromEnum(DIRECTION::values);
		private static final IntFunction<DIRECTION> BY_ID = ByIdMap.continuous(DIRECTION::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		public static final StreamCodec<ByteBuf, DIRECTION> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, DIRECTION::getId);

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

		public static final Codec<MATCH> CODEC = StringRepresentable.fromEnum(MATCH::values);
		private static final IntFunction<MATCH> BY_ID = ByIdMap.continuous(MATCH::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		public static final StreamCodec<ByteBuf, MATCH> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, MATCH::getId);

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

	public final ItemStack stickStack;
	public final StickUpgradesSelectable<IStickTemplate> upgrades;

	public final OptionEnum<LOCK> lock;
	public final OptionEnum<DIRECTION> direction;
	public final OptionBoolean replace;
	public final OptionEnum<MATCH> match;
	public final OptionBoolean random;

	public final IOption<?>[] allOptions;

	public StickOptions(ItemStack stickStack) {
		this.stickStack = stickStack;

		upgrades = new StickUpgradesSelectable<>(stickStack, "upgrades", new UpgradeDefault());

		lock = new OptionEnum<>(stickStack, ModDataComponents.LOCK.get(), "lock", LOCK.class, LOCK.NOLOCK);
		direction = new OptionEnum<>(stickStack, ModDataComponents.DIRECTION.get(), "direction", DIRECTION.class, DIRECTION.TARGET);
		replace = new OptionBoolean(stickStack, ModDataComponents.REPLACE.get(), "replace", true);
		match = new OptionEnum<>(stickStack, ModDataComponents.MATCH.get(), "match", MATCH.class, MATCH.SIMILAR);
		random = new OptionBoolean(stickStack, ModDataComponents.RANDOM.get(), "random", false);

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
