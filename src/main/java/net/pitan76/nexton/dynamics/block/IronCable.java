package net.pitan76.nexton.dynamics.block;

import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.nexton.dynamics.Config;
import net.pitan76.nexton.dynamics.block.entity.IronCableBlockEntity;

public class IronCable extends EnergyCable {

    public IronCable(CompatibleBlockSettings settings, int speed) {
        super(settings, speed);
    }

    protected final CompatMapCodec<IronCable> CODEC = CompatBlockMapCodecUtil.createCodec((settings ->
            new IronCable(settings, Config.getEnergyCableTransferRate())));

    @Override
    public CompatMapCodec<? extends IronCable> getCompatCodec() {
        return CODEC;
    }

    @Override
    public CompatBlockEntity createBlockEntity(TileCreateEvent e) {
        return new IronCableBlockEntity(e, speed);
    }
}
