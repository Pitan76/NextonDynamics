package net.pitan76.nexton.dynamics;

import net.pitan76.mcpitanlib.api.event.v0.EventRegistry;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.PlatformUtil;
import net.pitan76.mcpitanlib.fabric.ExtendModInitializer;
import net.pitan76.mcpitanlib.midohra.registry.MidohraRegistry;
import net.pitan76.mcpitanlib.midohra.world.World;
import net.pitan76.nexton.dynamics.block.Blocks;
import net.pitan76.nexton.dynamics.block.entity.BlockEntities;
import net.pitan76.nexton.dynamics.compat.RebornEnergyRegister;
import net.pitan76.nexton.dynamics.item.Items;

public class NextonDynamics extends ExtendModInitializer {

    public static final String MOD_ID = "nextondynamics";
    public static final String MOD_NAME = "Nexton Dynamics";

    public static MidohraRegistry registry;

    @Override
    public void init() {
        registry = MidohraRegistry.of(super.registry);

        Blocks.init();
        Items.init();
        BlockEntities.init();

        Config.init(PlatformUtil.getConfigFolderAsFile());

        registerEnergyStorage();

        // Clear cache when world unloads to prevent invalid cache access (rejoin)
        EventRegistry.ServerLifecycle.serverWorldUnload(world ->
                CableNetworkManager.clearCache(World.of(world)));
    }

    public static void registerEnergyStorage() {
        if (PlatformUtil.isModLoaded("team_reborn_energy")) {
            RebornEnergyRegister.init();
        }
    }

    // ----

    @Override
    public String getId() {
        return MOD_ID;
    }

    @Override
    public String getName() {
        return MOD_NAME;
    }

    public static CompatIdentifier _id(String path) {
        return CompatIdentifier.of(MOD_ID, path);
    }
}
