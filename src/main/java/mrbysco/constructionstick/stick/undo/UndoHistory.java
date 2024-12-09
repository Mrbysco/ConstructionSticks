package mrbysco.constructionstick.stick.undo;

import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.network.ModMessages;
import mrbysco.constructionstick.network.PacketUndoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UndoHistory {
	private final HashMap<UUID, PlayerEntry> history;

	public UndoHistory() {
		history = new HashMap<>();
	}

	private PlayerEntry getEntryFromPlayer(Player player) {
		return history.computeIfAbsent(player.getUUID(), k -> new PlayerEntry());
	}

	public void add(Player player, Level level, List<ISnapshot> placeSnapshots) {
		LinkedList<HistoryEntry> list = getEntryFromPlayer(player).entries;
		list.add(new HistoryEntry(placeSnapshots, level));
		while (list.size() > ConstructionConfig.UNDO_HISTORY.get()) list.removeFirst();
	}

	public void removePlayer(Player player) {
		history.remove(player.getUUID());
	}

	public void updateClient(Player player, boolean ctrlDown) {
		Level level = player.level();
		if (level.isClientSide) return;

		// Set state of CTRL key
		PlayerEntry playerEntry = getEntryFromPlayer(player);
		playerEntry.undoActive = ctrlDown;

		LinkedList<HistoryEntry> historyEntries = playerEntry.entries;
		Set<BlockPos> positions;

		// Send block positions of most recent entry to client
		if (historyEntries.isEmpty()) positions = Collections.emptySet();
		else {
			HistoryEntry entry = historyEntries.getLast();

			if (entry == null || !entry.level.equals(level)) positions = Collections.emptySet();
			else positions = entry.getBlockPositions();
		}

		PacketUndoBlocks packet = new PacketUndoBlocks(positions);
		ModMessages.sendToPlayer(packet, (ServerPlayer) player);
	}

	public boolean isUndoActive(Player player) {
		return getEntryFromPlayer(player).undoActive;
	}

	public boolean undo(Player player, Level level, BlockPos pos) {
		// If CTRL key is not pressed, return
		PlayerEntry playerEntry = getEntryFromPlayer(player);
		if (!playerEntry.undoActive) return false;

		// Get the most recent entry for undo
		LinkedList<HistoryEntry> historyEntries = playerEntry.entries;
		if (historyEntries.isEmpty()) return false;
		HistoryEntry entry = historyEntries.getLast();

		// Player has to be in the same level and near the blocks
		if (!entry.level.equals(level) || !entry.withinRange(pos)) return false;

		if (entry.undo(player)) {
			historyEntries.remove(entry);
			updateClient(player, true);
			return true;
		}
		return false;
	}

	private static class PlayerEntry {
		public final LinkedList<HistoryEntry> entries;
		public boolean undoActive;

		public PlayerEntry() {
			entries = new LinkedList<>();
			undoActive = false;
		}
	}

	private static class HistoryEntry {
		public final List<ISnapshot> placeSnapshots;
		public final Level level;

		public HistoryEntry(List<ISnapshot> placeSnapshots, Level level) {
			this.placeSnapshots = placeSnapshots;
			this.level = level;
		}

		public Set<BlockPos> getBlockPositions() {
			return placeSnapshots.stream().map(ISnapshot::getPos).collect(Collectors.toSet());
		}

		public boolean withinRange(BlockPos pos) {
			Set<BlockPos> positions = getBlockPositions();

			if (positions.contains(pos)) return true;

			for (BlockPos p : positions) {
				if (pos.closerThan(p, 3)) return true;
			}
			return false;
		}

		public boolean undo(Player player) {
			// Check first if all snapshots can be restored
			for (ISnapshot snapshot : placeSnapshots) {
				if (!snapshot.canRestore(level, player)) return false;
			}
			for (ISnapshot snapshot : placeSnapshots) {
				if (snapshot.restore(level, player) && !player.isCreative()) {
					ItemStack stack = snapshot.getRequiredItems();

					if (!player.getInventory().add(stack)) {
						player.drop(stack, false);
					}
				}
			}
			player.getInventory().setChanged();

			// Play teleport sound
			SoundEvent sound = SoundEvents.CHORUS_FRUIT_TELEPORT;
			level.playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);

			return true;
		}
	}
}
