package net.pitan76.nextondynamics.block.entity;

import net.pitan76.mcpitanlib.api.tile.BlockEntityTypeBuilder;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.midohra.block.SupplierBlockWrapper;
import net.pitan76.mcpitanlib.midohra.block.entity.TypedBlockEntityTypeWrapper;
import net.pitan76.nextondynamics.NextonDynamics;
import net.pitan76.nextondynamics.block.Blocks;

import static net.pitan76.nextondynamics.NextonDynamics.registry;

public class BlockEntities {
    public static TypedBlockEntityTypeWrapper<EnergyCableBlockEntity> ENERGY_CABLE;
    public static TypedBlockEntityTypeWrapper<EnergyCableBlockEntity> COPPER_CABLE;
    public static TypedBlockEntityTypeWrapper<EnergyCableBlockEntity> IRON_CABLE;
    public static TypedBlockEntityTypeWrapper<EnergyCableBlockEntity> GOLD_CABLE;

    public static void init() {
        ENERGY_CABLE = registry.registerBlockEntityType(
            NextonDynamics._id("energy_cable"),
            create(EnergyCableBlockEntity::new, SupplierBlockWrapper.of(() -> Blocks.ENERGY_CABLE.get()))
        );

        COPPER_CABLE = registry.registerBlockEntityType(
                NextonDynamics._id("copper_cable"),
            create(CopperCableBlockEntity::new, SupplierBlockWrapper.of(() -> Blocks.COPPER_CABLE.get()))
        );

        IRON_CABLE = registry.registerBlockEntityType(
                NextonDynamics._id("iron_cable"),
            create(IronCableBlockEntity::new, SupplierBlockWrapper.of(() -> Blocks.IRON_CABLE.get()))
        );

        GOLD_CABLE = registry.registerBlockEntityType(
                NextonDynamics._id("gold_cable"),
            create(GoldCableBlockEntity::new, SupplierBlockWrapper.of(() -> Blocks.GOLD_CABLE.get()))
        );
    }

    public static <T extends CompatBlockEntity> BlockEntityTypeBuilder<T> create(BlockEntityTypeBuilder.Factory<? extends T> factory, SupplierBlockWrapper wrapper) {
        return net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder.create(factory, wrapper);
    }
}