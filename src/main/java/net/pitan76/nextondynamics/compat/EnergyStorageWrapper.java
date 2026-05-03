package net.pitan76.nextondynamics.compat;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.EnergyStorage;

public class EnergyStorageWrapper implements IEnergyStorage {

    private final EnergyStorage energyStorage;

    public EnergyStorageWrapper(EnergyStorage energyStorage) {
        this.energyStorage = energyStorage;
    }

    @Override
    public long getMaxOutput() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getMaxInput() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getMaxEnergy() {
        return energyStorage.getCapacity();
    }

    @Override
    public boolean canInput() {
        return energyStorage.supportsInsertion();
    }

    @Override
    public boolean canOutput() {
        return energyStorage.supportsExtraction();
    }

    @Override
    public long insert(long maxAmount) {
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = energyStorage.insert(maxAmount, transaction);
            if (inserted > 0) {
                transaction.commit();
            }
            return inserted;
        }
    }

    @Override
    public long extract(long maxAmount) {
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = energyStorage.extract(maxAmount, transaction);
            if (extracted > 0) {
                transaction.commit();
            }
            return extracted;
        }
    }

    @Override
    public long getEnergy() {
        return energyStorage.getAmount();
    }

    @Override
    public void setEnergy(long value) {
        long delta = value - energyStorage.getAmount();

        if (delta > 0) {
            try (Transaction transaction = Transaction.openOuter()) {
                long inserted = energyStorage.insert(delta, transaction);
                if (inserted > 0) {
                    transaction.commit();
                }
            }
        } else if (delta < 0) {
            try (Transaction transaction = Transaction.openOuter()) {
                long extracted = energyStorage.extract(-delta, transaction);
                if (extracted > 0) {
                    transaction.commit();
                }
            }
        }
    }
}