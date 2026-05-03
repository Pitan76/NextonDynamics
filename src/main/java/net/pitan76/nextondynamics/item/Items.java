package net.pitan76.nextondynamics.item;

import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.midohra.item.ItemGroups;
import net.pitan76.mcpitanlib.midohra.item.ItemWrapper;
import net.pitan76.nextondynamics.block.Blocks;

import static net.pitan76.nextondynamics.NextonDynamics._id;
import static net.pitan76.nextondynamics.NextonDynamics.registry;

public class Items {
    public static ItemWrapper ENERGY_CABLE;
    public static ItemWrapper COPPER_CABLE;
    public static ItemWrapper IRON_CABLE;
    public static ItemWrapper GOLD_CABLE;

    public static void init() {
        ENERGY_CABLE = registry.registerBlockItem(_id("energy_cable"), Blocks.ENERGY_CABLE,
            CompatibleItemSettings.of(_id("energy_cable"))
            .addGroup(ItemGroups.TRANSPORTATION));

        COPPER_CABLE = registry.registerBlockItem(_id("copper_cable"), Blocks.COPPER_CABLE,
            CompatibleItemSettings.of(_id("copper_cable"))
                .addGroup(ItemGroups.TRANSPORTATION));

        IRON_CABLE = registry.registerBlockItem(_id("iron_cable"), Blocks.IRON_CABLE,
                CompatibleItemSettings.of(_id("iron_cable"))
                .addGroup(ItemGroups.TRANSPORTATION));

        GOLD_CABLE = registry.registerBlockItem(_id("gold_cable"), Blocks.GOLD_CABLE,
            CompatibleItemSettings.of(_id("gold_cable"))
                .addGroup(ItemGroups.TRANSPORTATION));
    }
}