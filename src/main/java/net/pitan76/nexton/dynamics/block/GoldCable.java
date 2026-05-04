package net.pitan76.nexton.dynamics.block;

import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.nexton.dynamics.Config;
import net.pitan76.nexton.dynamics.block.entity.GoldCableBlockEntity;

public class GoldCable extends EnergyCable {

    public GoldCable(CompatibleBlockSettings settings, int speed) {
        super(settings, speed);
    }

    protected final CompatMapCodec<GoldCable> CODEC = CompatBlockMapCodecUtil.createCodec((settings ->
            new GoldCable(settings, Config.getEnergyCableTransferRate())));

    @Override
    public CompatMapCodec<? extends GoldCable> getCompatCodec() {
        return CODEC;
    }

    @Override
    public CompatBlockEntity createBlockEntity(TileCreateEvent e) {
        return new GoldCableBlockEntity(e, speed);
    }
}
