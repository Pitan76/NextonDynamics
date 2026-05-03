package net.pitan76.nextondynamics.compat;

public interface IEnergyStorage {
    long getMaxOutput();
    long getMaxInput();
    long getEnergy();
    void setEnergy(long amount);
    long getMaxEnergy();
    boolean canInput();
    boolean canOutput();

    long insert(long maxAmount);
    long extract(long maxAmount);
}
