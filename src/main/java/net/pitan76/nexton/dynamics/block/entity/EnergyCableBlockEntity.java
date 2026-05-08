package net.pitan76.nexton.dynamics.block.entity;

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.core.datafixer.Pair;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;
import net.pitan76.nexton.core.api.energy.IEnergyStorage;
import net.pitan76.nexton.core.api.energy.SimpleEnergyStorage;
import net.pitan76.nexton.dynamics.CableNetworkManager;
import net.pitan76.nexton.dynamics.Config;
import java.util.*;

public class EnergyCableBlockEntity extends AbstractEnergyBlockEntity implements ExtendBlockEntityTicker<EnergyCableBlockEntity> {

    public final IEnergyStorage energyStorage;

    public EnergyCableBlockEntity(BlockEntityTypeWrapper type, TileCreateEvent e, int speed) {
        super(type, e);
        this.speed = speed;
        energyStorage = new SimpleEnergyStorage.Builder().capacity(speed * 4L).maxInput(speed).maxOutput(speed).canInsert(true).canExtract(true).build();
    }

    public EnergyCableBlockEntity(BlockEntityTypeWrapper type, TileCreateEvent e) {
        this(type, e, Config.getEnergyCableTransferRate());
    }

    @Override
    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public EnergyCableBlockEntity(TileCreateEvent e) {
        this(BlockEntities.ENERGY_CABLE, e);
    }

    public EnergyCableBlockEntity(TileCreateEvent e, int speed) {
        this(BlockEntities.ENERGY_CABLE, e, speed);
    }

    public final int speed;

    public UUID networkId = UUID.randomUUID();

    @Override
    public void tick(TileTickEvent<EnergyCableBlockEntity> e) {
        if (e.isClient()) return;

        var world = e.getMidohraWorld();
        var pos = e.getMidohraPos();

        var network = CableNetworkManager.getOrCreateNetwork(world, pos);

        var isMaster = network.cables.stream().findFirst().map(Pair::getA).orElse(null) == this;
        if (!isMaster) return;

        network.tick();
    }
}
