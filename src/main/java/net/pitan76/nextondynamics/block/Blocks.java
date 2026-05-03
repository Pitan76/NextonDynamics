package net.pitan76.nextondynamics.block;

import net.pitan76.mcpitanlib.api.block.CompatibleMaterial;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.midohra.block.TypedBlockWrapper;
import net.pitan76.nextondynamics.Config;

import static net.pitan76.nextondynamics.NextonDynamics._id;
import static net.pitan76.nextondynamics.NextonDynamics.registry;

public class Blocks {
    public static TypedBlockWrapper<EnergyCable> ENERGY_CABLE;
    public static TypedBlockWrapper<CopperCable> COPPER_CABLE;
    public static TypedBlockWrapper<IronCable> IRON_CABLE;
    public static TypedBlockWrapper<GoldCable> GOLD_CABLE;

    public static void init() {
        ENERGY_CABLE = registry.registerBlock(_id("energy_cable"),
            () -> new EnergyCable(
                CompatibleBlockSettings.of(_id("energy_cable"), CompatibleMaterial.STONE)
                    .strength(0.5f, 6.0f),
                Config.getEnergyCableTransferRate()
            )
        );

        COPPER_CABLE = registry.registerBlock(_id("copper_cable"),
            () -> new CopperCable(
                CompatibleBlockSettings.of(_id("copper_cable"), CompatibleMaterial.METAL)
                    .strength(0.5f, 6.0f),
                Config.getCopperCableTransferRate()
            )
        );

        IRON_CABLE = registry.registerBlock(_id("iron_cable"),
            () -> new IronCable(
                CompatibleBlockSettings.of(_id("iron_cable"), CompatibleMaterial.METAL)
                    .strength(0.5f, 6.0f),
                Config.getIronCableTransferRate()
            )
        );

        GOLD_CABLE = registry.registerBlock(_id("gold_cable"),
            () -> new GoldCable(
                CompatibleBlockSettings.of(_id("gold_cable"), CompatibleMaterial.METAL)
                    .strength(0.5f, 6.0f),
                Config.getGoldCableTransferRate()
            )
        );
    }
}