package net.pitan76.nextondynamics.block.entity;

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.nextondynamics.Config;

public class CopperCableBlockEntity extends EnergyCableBlockEntity {

    public CopperCableBlockEntity(TileCreateEvent e, int speed) {
        super(BlockEntities.COPPER_CABLE, e, speed);
    }

    public CopperCableBlockEntity(TileCreateEvent e) {
        this(e, Config.getCopperCableTransferRate());
    }
}
