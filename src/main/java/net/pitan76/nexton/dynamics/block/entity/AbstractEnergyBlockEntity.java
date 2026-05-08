package net.pitan76.nexton.dynamics.block.entity;

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;
import net.pitan76.nexton.core.api.block.entity.MachineBlockEntity;
import net.pitan76.nexton.core.api.energy.EnergyStorageProvider;
import net.pitan76.nexton.core.api.energy.IEnergyStorage;
import net.pitan76.nexton.core.api.energy.SimpleEnergyStorage;

public abstract class AbstractEnergyBlockEntity extends MachineBlockEntity implements EnergyStorageProvider {
    public final IEnergyStorage energyStorage;

    public AbstractEnergyBlockEntity(BlockEntityTypeWrapper type, TileCreateEvent e) {
        super(type.get(), e);
        energyStorage = new SimpleEnergyStorage.Builder().capacity(getUsableCapacity()).maxInput(getMaxInputEnergy()).maxOutput(getMaxOutputEnergy()).canInsert(canInsertEnergy()).canExtract(canExtractEnergy()).build();
    }

    @Override
    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public boolean addEnergy(long energy) {
        if (canAddEnergy(energy)) {
            this.setEnergyStored(getEnergyStored() + energy);
            return true;
        }
        return false;
    }

    public boolean removeEnergy(long energy) {
        return addEnergy(-energy);
    }

    public boolean canAddEnergy(long energy) {
        return this.getCapacityEnergy() >= this.getEnergyStored() + energy && this.getEnergyStored() + energy >= 0;
    }

//    public long insertEnergy(long amount) {
//        long usableCapacity = this.getUsableCapacity();
//        if (amount > usableCapacity) {
//            this.energy += usableCapacity;
//            return usableCapacity;
//        }
//        this.energy += amount;
//        return amount;
//    }
//
//    public long extractEnergy(long amount) {
//        if (amount > this.energy) {
//            long energy = this.energy;
//            this.energy = 0;
//            return energy;
//        }
//        this.energy -= amount;
//        return amount;
//    }
}