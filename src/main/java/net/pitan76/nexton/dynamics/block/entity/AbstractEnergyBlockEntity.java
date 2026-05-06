package net.pitan76.nexton.dynamics.block.entity;

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.util.nbt.v2.NbtRWUtil;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;
import net.pitan76.nexton.core.api.block.entity.MachineBlockEntity;
import net.pitan76.nexton.core.api.energy.IEnergyStorage;
import net.pitan76.nexton.core.api.energy.SimpleEnergyStorage;

public abstract class AbstractEnergyBlockEntity extends MachineBlockEntity {

    public AbstractEnergyBlockEntity(BlockEntityTypeWrapper type, TileCreateEvent e) {
        super(type.get(), e);
        if (!hasEnergyStorage())
            setEnergyStorage(new SimpleEnergyStorage.Builder().capacity(getMaxEnergy()).maxInput(getMaxInput()).maxOutput(getMaxOutput()).canInsert(canInput()).canExtract(canOutput()).build());
    }

    public abstract long getMaxEnergy();

    public long getUsableCapacity() {
        return this.getMaxEnergy() - this.energy;
    }

    public abstract long getMaxOutput();

    public abstract long getMaxInput();

    public long energy = 0;

    public boolean canInput() {
        return getMaxInput() > 0;
    }

    public boolean canOutput() {
        return getMaxOutput() > 0;
    }

    public IEnergyStorage energyStorage = null;

    public void setEnergyStorage(IEnergyStorage energyStorage) {
        this.energyStorage = energyStorage;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public boolean hasEnergyStorage() {
        return this.energyStorage != null;
    }

    @Override
    public void writeNbt(WriteNbtArgs args) {
        super.writeNbt(args);
        NbtRWUtil.putLong(args, "energy", this.energy);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        super.readNbt(args);
        this.energy = NbtRWUtil.getLongOrDefault(args, "energy", 0);
    }

    public boolean addEnergy(long energy) {
        if (canAddEnergy(energy)) {
            this.energy += energy;
            return true;
        }
        return false;
    }

    public boolean removeEnergy(long energy) {
        return addEnergy(-energy);
    }

    public boolean canAddEnergy(long energy) {
        return this.getMaxEnergy() > this.energy + energy && this.energy + energy >= 0;
    }

    public long insertEnergy(long amount) {
        long usableCapacity = this.getUsableCapacity();
        if (amount > usableCapacity) {
            this.energy += usableCapacity;
            return usableCapacity;
        }
        this.energy += amount;
        return amount;
    }

    public long extractEnergy(long amount) {
        if (amount > this.energy) {
            long energy = this.energy;
            this.energy = 0;
            return energy;
        }
        this.energy -= amount;
        return amount;
    }
}