package net.pitan76.nexton.dynamics.block;

import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.block.v3.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;

abstract class AbstractCable extends CompatBlock implements ExtendBlockEntityProvider {
    public AbstractCable(CompatibleBlockSettings settings) {
        super(settings);
    }
}
