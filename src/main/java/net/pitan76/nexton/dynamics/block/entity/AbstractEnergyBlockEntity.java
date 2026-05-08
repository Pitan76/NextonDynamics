package net.pitan76.nexton.dynamics.block.entity;

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;
import net.pitan76.nexton.core.api.block.entity.MachineBlockEntity;
import net.pitan76.nexton.core.api.energy.EnergyStorageProvider;

public abstract class AbstractEnergyBlockEntity extends MachineBlockEntity implements EnergyStorageProvider {

    public AbstractEnergyBlockEntity(BlockEntityTypeWrapper type, TileCreateEvent e) {
        super(type.get(), e);
    }
}