package net.pitan76.nexton.dynamics.block.entity;

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.nexton.dynamics.Config;

public class GoldCableBlockEntity extends EnergyCableBlockEntity {

    public GoldCableBlockEntity(TileCreateEvent e, int speed) {
        super(BlockEntities.GOLD_CABLE, e, speed);
    }

    public GoldCableBlockEntity(TileCreateEvent e) {
        this(e, Config.getGoldCableTransferRate());
    }
}
