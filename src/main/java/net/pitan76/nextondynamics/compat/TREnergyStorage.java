package net.pitan76.nextondynamics.compat;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.pitan76.nextondynamics.Config;
import net.pitan76.nextondynamics.block.entity.AbstractEnergyBlockEntity;
import team.reborn.energy.api.EnergyStorage;

public class TREnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage, IEnergyStorage {

    public final AbstractEnergyBlockEntity tile;

    public static final double CONVERSION_RATE = Config.getRebornEnergyConversionRate();

    public TREnergyStorage(AbstractEnergyBlockEntity tile) {
        this.tile = tile;
    }

    public long getUsableCapacity() {
        return tile.getUsableCapacity() / (long) CONVERSION_RATE;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        if (maxAmount < getUsableCapacity()) {
            updateSnapshots(transaction);
            return tile.insertEnergy((long) (maxAmount * CONVERSION_RATE)) / (long) CONVERSION_RATE;
        }
        if (maxAmount > 0) {
            updateSnapshots(transaction);
            return tile.insertEnergy((long) (getUsableCapacity() * CONVERSION_RATE)) / (long) CONVERSION_RATE;
        }
        return 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        if (maxAmount < getAmount()) {
            updateSnapshots(transaction);
            return tile.extractEnergy((long) (maxAmount * CONVERSION_RATE)) / (long) CONVERSION_RATE;
        }
        if (getAmount() > 0) {
            updateSnapshots(transaction);
            return tile.extractEnergy(tile.energy) / (long) CONVERSION_RATE;
        }

        return 0;
    }

    @Override
    public long getAmount() {
        return (long) (tile.energy / CONVERSION_RATE);
    }

    @Override
    public long getCapacity() {
        return (long) (tile.getMaxEnergy() / CONVERSION_RATE);
    }

    @Override
    protected Long createSnapshot() {
        return tile.energy;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        tile.energy = snapshot;
    }

    @Override
    public long getMaxOutput() {
        return (long) (tile.getMaxOutput() / CONVERSION_RATE);
    }

    @Override
    public long getMaxInput() {
        return (long) (tile.getMaxInput() / CONVERSION_RATE);
    }

    @Override
    public long getMaxEnergy() {
        return getCapacity();
    }

    @Override
    public boolean canInput() {
        return tile.canInput();
    }

    @Override
    public boolean canOutput() {
        return tile.canOutput();
    }

    @Override
    public long insert(long maxAmount) {
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = insert(maxAmount, transaction);
            if (inserted > 0)
                transaction.commit();

            return inserted;
        }
    }

    @Override
    public long extract(long maxAmount) {
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = extract(maxAmount, transaction);
            if (extracted > 0)
                transaction.commit();

            return extracted;
        }
    }

    @Override
    public long getEnergy() {
        return tile.energy;
    }

    @Override
    public void setEnergy(long amount) {
        tile.energy = amount;
    }
}
