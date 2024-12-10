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
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ConstructionStick.MOD_ID);

	public static final Supplier<DataComponentType<StickOptions.LOCK>> LOCK = DATA_COMPONENT_TYPES.register("lock", () ->
			DataComponentType.<StickOptions.LOCK>builder()
					.persistent(StickOptions.LOCK.CODEC)
					.networkSynchronized(StickOptions.LOCK.STREAM_CODEC)
					.build());

	public static final Supplier<DataComponentType<StickOptions.DIRECTION>> DIRECTION = DATA_COMPONENT_TYPES.register("direction", () ->
			DataComponentType.<StickOptions.DIRECTION>builder()
					.persistent(StickOptions.DIRECTION.CODEC)
					.networkSynchronized(StickOptions.DIRECTION.STREAM_CODEC)
					.build());

	public static final Supplier<DataComponentType<Boolean>> REPLACE = DATA_COMPONENT_TYPES.register("replace", () ->
			DataComponentType.<Boolean>builder()
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
					.build());

	public static final Supplier<DataComponentType<StickOptions.MATCH>> MATCH = DATA_COMPONENT_TYPES.register("match", () ->
			DataComponentType.<StickOptions.MATCH>builder()
					.persistent(StickOptions.MATCH.CODEC)
					.networkSynchronized(StickOptions.MATCH.STREAM_CODEC)
					.build());

	public static final Supplier<DataComponentType<Boolean>> RANDOM = DATA_COMPONENT_TYPES.register("random", () ->
			DataComponentType.<Boolean>builder()
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ANGEL = DATA_COMPONENT_TYPES.register("angel", () ->
			DataComponentType.<Boolean>builder()
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DESTRUCTION = DATA_COMPONENT_TYPES.register("destruction", () ->
			DataComponentType.<Boolean>builder()
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> UNBREAKABLE = DATA_COMPONENT_TYPES.register("unbreakable", () ->
			DataComponentType.<Boolean>builder()
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BATTERY_ENABLED = DATA_COMPONENT_TYPES.register("battery_enabled", () ->
			DataComponentType.<Boolean>builder()
					.persistent(Codec.BOOL)
					.networkSynchronized(ByteBufCodecs.BOOL)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BATTERY = DATA_COMPONENT_TYPES.register("battery", () ->
			DataComponentType.<Integer>builder()
					.persistent(Codec.INT)
					.networkSynchronized(ByteBufCodecs.INT)
					.build());

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> SELECTED = DATA_COMPONENT_TYPES.register("selected", () ->
			DataComponentType.<ResourceLocation>builder()
					.persistent(ResourceLocation.CODEC)
					.networkSynchronized(ResourceLocation.STREAM_CODEC)
					.build());
}
