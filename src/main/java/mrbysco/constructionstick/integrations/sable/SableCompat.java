package mrbysco.constructionstick.integrations.sable;

import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Mod support for the Sable mod
 */
public class SableCompat {

	/**
	 * Finds an item handler at the given position, searching through sublevels if necessary.
	 *
	 * @param level The level
	 * @param pos   The position
	 * @return The item handler, or null if none was found
	 */
	public static double getRange(Level level, BlockPos playerPos, BlockPos pos) {
		return SableCompanion.INSTANCE.rectilinearDistanceWithSubLevels(
				level,
				playerPos.getX() + 0.5,
				playerPos.getY() + 0.5,
				playerPos.getZ() + 0.5,
				pos.getX() + 0.5,
				pos.getY() + 0.5,
				pos.getZ() + 0.5);
	}
}
