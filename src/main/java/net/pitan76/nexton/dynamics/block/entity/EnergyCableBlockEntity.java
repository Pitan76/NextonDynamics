package net.pitan76.nexton.dynamics.block.entity;

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.core.datafixer.Pair;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;
import net.pitan76.nexton.dynamics.CableNetworkManager;
import net.pitan76.nexton.dynamics.Config;
import java.util.*;

public class EnergyCableBlockEntity extends AbstractEnergyBlockEntity implements ExtendBlockEntityTicker<EnergyCableBlockEntity> {

    public EnergyCableBlockEntity(BlockEntityTypeWrapper type, TileCreateEvent e) {
        super(type, e);
        this.speed = Config.getEnergyCableTransferRate();
    }

    public EnergyCableBlockEntity(BlockEntityTypeWrapper type, TileCreateEvent e, int speed) {
        super(type, e);
        this.speed = speed;
    }

    public EnergyCableBlockEntity(TileCreateEvent e) {
        this(BlockEntities.ENERGY_CABLE, e);
    }

    public EnergyCableBlockEntity(TileCreateEvent e, int speed) {
        this(BlockEntities.ENERGY_CABLE, e, speed);
    }

    public final int speed;

    @Override
    public long getCapacityEnergy() {
        return (long) speed * 4;
    }

    @Override
    public long getMaxOutputEnergy() {
        return speed;
    }

    @Override
    public long getMaxInputEnergy() {
        return speed;
    }

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

    @Override
    public void writeNbt(WriteNbtArgs args) {
        super.writeNbt(args);
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        super.readNbt(args);
    }
}
