package net.pitan76.nexton.dynamics.compat;

import net.pitan76.mcpitanlib.api.lookup.block.BlockApiLookupWithDirection;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper;
import net.pitan76.nexton.dynamics.block.entity.AbstractEnergyBlockEntity;
import net.pitan76.nexton.dynamics.block.entity.BlockEntities;
import team.reborn.energy.api.EnergyStorage;

public class RebornEnergyRegister {

    public static BlockApiLookupWithDirection<EnergyStorage> ENERGY_LOOKUP = new BlockApiLookupWithDirection<>(EnergyStorage.SIDED);

    public static void init() {
        System.out.println("Registering Reborn Energy Storage for Energy Cable");

        for (BlockEntityTypeWrapper wrapper2 : new BlockEntityTypeWrapper[]{BlockEntities.ENERGY_CABLE, BlockEntities.COPPER_CABLE, BlockEntities.IRON_CABLE, BlockEntities.GOLD_CABLE}) {
                ENERGY_LOOKUP.registerForBlockEntityWrapperM((wrapper, dir) -> {
                    if (wrapper.instanceOf(AbstractEnergyBlockEntity.class)) {
                        AbstractEnergyBlockEntity blockEntity = wrapper.getCompatBlockEntity(AbstractEnergyBlockEntity.class);

                        if (blockEntity.getEnergyStorage() instanceof TREnergyStorage)
                            return (TREnergyStorage) blockEntity.getEnergyStorage();

                        if (!blockEntity.hasEnergyStorage())
                            blockEntity.setEnergyStorage(new TREnergyStorage(blockEntity));
                    }

                    return null;
                }, wrapper2);
        }
    }
}
