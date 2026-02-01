package mrbysco.constructionstick.items.stick;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * NBT-backed energy storage that stores energy directly in the item's NBT tag.
 * This syncs automatically via vanilla item sync - no getShareTag/readShareTag needed.
 */
public class NBTEnergyStorage implements IEnergyStorage {
	private static final String ENERGY_KEY = "Energy";

	private final ItemStack stack;
	private final int capacity;
	private final int maxReceive;
	private final int maxExtract;

	public NBTEnergyStorage(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
		this.stack = stack;
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	private int getEnergyStored_Internal() {
		return stack.getOrCreateTag().getInt(ENERGY_KEY);
	}

	private void setEnergyStored_Internal(int energy) {
		stack.getOrCreateTag().putInt(ENERGY_KEY, Math.max(0, Math.min(capacity, energy)));
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int energy = getEnergyStored_Internal();
		int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
		if (!simulate) {
			setEnergyStored_Internal(energy + energyReceived);
		}
		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int energy = getEnergyStored_Internal();
		int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
		if (!simulate) {
			setEnergyStored_Internal(energy - energyExtracted);
		}
		return energyExtracted;
	}

	@Override
	public int getEnergyStored() {
		return getEnergyStored_Internal();
	}

	@Override
	public int getMaxEnergyStored() {
		return capacity;
	}

	@Override
	public boolean canExtract() {
		return maxExtract > 0;
	}

	@Override
	public boolean canReceive() {
		return maxReceive > 0;
	}
}
