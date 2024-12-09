package mrbysco.constructionstick.stick;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.api.IStickAction;
import mrbysco.constructionstick.api.IStickSupplier;
import mrbysco.constructionstick.basics.ModStats;
import mrbysco.constructionstick.basics.option.StickOptions;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.stick.supplier.SupplierInventory;
import mrbysco.constructionstick.stick.supplier.SupplierRandom;
import mrbysco.constructionstick.stick.undo.ISnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StickJob {
	public final Player player;
	public final Level level;
	public final BlockHitResult blockHitResult;
	public final StickOptions options;
	public final ItemStack stick;
	public final ItemStick stickItem;

	private final IStickAction stickAction;
	private final IStickSupplier stickSupplier;

	private List<ISnapshot> placeSnapshots;

	public StickJob(Player player, Level level, BlockHitResult blockHitResult, ItemStack stick) {
		this.player = player;
		this.level = level;
		this.blockHitResult = blockHitResult;
		this.placeSnapshots = new ArrayList<>();

		// Get stick
		this.stick = stick;
		this.stickItem = (ItemStick) stick.getItem();
		options = new StickOptions(stick);

		// Select stick action and supplier based on options
		stickSupplier = options.random.get() ?
				new SupplierRandom(player, options) : new SupplierInventory(player, options);
		stickAction = options.upgrades.get().getStickAction();

		stickSupplier.getSupply(getTargetItem(level, blockHitResult));
	}

	@Nullable
	private static BlockItem getTargetItem(Level level, BlockHitResult rayTraceResult) {
		// Get target item
		Item tgitem = level.getBlockState(rayTraceResult.getBlockPos()).getBlock().asItem();
		if (!(tgitem instanceof BlockItem)) return null;
		return (BlockItem) tgitem;
	}

	public void getSnapshots() {
		int limit = Math.min(stickItem.remainingDurability(stick), stickAction.getLimit(stick));

		if (blockHitResult.getType() == HitResult.Type.BLOCK)
			placeSnapshots = stickAction.getSnapshots(level, player, blockHitResult, stick, options, stickSupplier, limit);
		else
			placeSnapshots = stickAction.getSnapshotsFromAir(level, player, blockHitResult, stick, options, stickSupplier, limit);
	}

	public Set<BlockPos> getBlockPositions() {
		return placeSnapshots.stream().map(ISnapshot::getPos).collect(Collectors.toSet());
	}

	public int blockCount() {
		return placeSnapshots.size();
	}

	@SuppressWarnings("deprecation")
	public boolean doIt() {
		List<ISnapshot> executed = new ArrayList<>();

		for (ISnapshot snapshot : placeSnapshots) {
			if (stick.isEmpty() || stickItem.remainingDurability(stick) == 0) break;

			if (snapshot.execute(level, player, blockHitResult)) {
				if (player.isCreative()) executed.add(snapshot);
				else {
					// If the item cant be taken, undo the placement
					if (stickSupplier.takeItemStack(snapshot.getRequiredItems()) == 0) {
						executed.add(snapshot);
						stickItem.hurtItem(stick, 1, player, EquipmentSlot.MAINHAND);
					} else {
						ConstructionStick.LOGGER.info("Item could not be taken. Remove block: {}",
								snapshot.getBlockState().getBlock().toString());
						snapshot.forceRestore(level);
					}
				}
				player.awardStat(ModStats.USE_STICK_STAT.get());
			}
		}
		placeSnapshots = executed;

		// Play place sound
		if (!placeSnapshots.isEmpty()) {
			SoundType sound = placeSnapshots.getFirst().getBlockState().getSoundType();
			level.playSound(null, player.blockPosition(), sound.getPlaceSound(), SoundSource.BLOCKS, sound.volume, sound.pitch);

			// Add to job history for undo
			ConstructionStick.undoHistory.add(player, level, placeSnapshots);
		}

		return !placeSnapshots.isEmpty();
	}
}