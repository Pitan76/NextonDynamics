package net.pitan76.nextondynamics.block;

import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.nextondynamics.Config;
import net.pitan76.nextondynamics.block.entity.CopperCableBlockEntity;

public class CopperCable extends EnergyCable {

    public CopperCable(CompatibleBlockSettings settings, int speed) {
        super(settings, speed);
    }

    protected final CompatMapCodec<CopperCable> CODEC = CompatBlockMapCodecUtil.createCodec((settings ->
            new CopperCable(settings, Config.getEnergyCableTransferRate())));

    @Override
    public CompatMapCodec<? extends CopperCable> getCompatCodec() {
        return CODEC;
    }

    @Override
    public CompatBlockEntity createBlockEntity(TileCreateEvent e) {
        return new CopperCableBlockEntity(e, speed);
    }
}
