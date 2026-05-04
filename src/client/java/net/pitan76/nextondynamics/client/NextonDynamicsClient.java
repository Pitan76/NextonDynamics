package net.pitan76.nextondynamics.client;

import net.fabricmc.api.ClientModInitializer;
import net.pitan76.mcpitanlib.api.client.registry.CompatRegistryClient;
import net.pitan76.nextondynamics.block.Blocks;

public class NextonDynamicsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CompatRegistryClient.registerCutoutBlock(Blocks.ENERGY_CABLE.get());
    }
}
