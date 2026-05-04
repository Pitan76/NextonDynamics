package net.pitan76.nexton.dynamics.block.entity;

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.nexton.dynamics.Config;

public class IronCableBlockEntity extends EnergyCableBlockEntity {

    public IronCableBlockEntity(TileCreateEvent e, int speed) {
        super(BlockEntities.IRON_CABLE, e, speed);
    }

    public IronCableBlockEntity(TileCreateEvent e) {
        super(BlockEntities.IRON_CABLE, e, Config.getIronCableTransferRate());
    }
}
