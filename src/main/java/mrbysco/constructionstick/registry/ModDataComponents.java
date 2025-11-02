package mrbysco.constructionstick.registry;

import com.mojang.serialization.Codec;
import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.basics.option.StickOptions;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
	public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ConstructionStick.MOD_ID);

	public static final Supplier<DataComponentType<StickOptions.LOCK>> LOCK = DATA_COMPONENT_TYPES.registerComponentType("lock", builder ->
			builder
					.persistent(StickOptions.LOCK.CODEC)
					.networkSynchronized(StickOptions.LOCK.STREAM_CODEC)
	);

	public static final Supplier<DataComponentType<StickOptions.DIRECTION>> DIRECTION = DATA_COMPONENT_TYPES.registerComponentType("direction", builder ->
			builder
					.persistent(StickOptions.DIRECTION.CODEC)
					.networkSynchronized(StickOptions.DIRECTION.STREAM_CODEC)
	);

	public static final Supplier<DataComponentType<Boolean>> REPLACE = DATA_COMPONENT_TYPES.registerComponentType("replace", builder ->
			builder
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
	);

	public static final Supplier<DataComponentType<StickOptions.MATCH>> MATCH = DATA_COMPONENT_TYPES.registerComponentType("match", builder ->
			builder
					.persistent(StickOptions.MATCH.CODEC)
					.networkSynchronized(StickOptions.MATCH.STREAM_CODEC)
	);

	public static final Supplier<DataComponentType<Boolean>> RANDOM = DATA_COMPONENT_TYPES.registerComponentType("random", builder ->
			builder
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ANGEL = DATA_COMPONENT_TYPES.registerComponentType("angel", builder ->
			builder
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DESTRUCTION = DATA_COMPONENT_TYPES.registerComponentType("destruction", builder ->
			builder
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> UNBREAKABLE = DATA_COMPONENT_TYPES.registerComponentType("unbreakable", builder ->
			builder
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BATTERY_ENABLED = DATA_COMPONENT_TYPES.registerComponentType("battery_enabled", builder ->
			builder
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BATTERY = DATA_COMPONENT_TYPES.registerComponentType("battery", builder ->
			builder
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.INT)
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> SELECTED = DATA_COMPONENT_TYPES.registerComponentType("selected", builder ->
			builder
					.persistent(ResourceLocation.CODEC)
					.networkSynchronized(ResourceLocation.STREAM_CODEC)
	);

	public static final Supplier<DataComponentType<Boolean>> REPLACEMENT = DATA_COMPONENT_TYPES.registerComponentType("replacement", builder ->
			builder
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
	);
}
