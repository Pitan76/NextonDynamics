package net.pitan76.nexton.dynamics.block;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.pitan76.mcpitanlib.api.block.CompatBlockRenderType;
import net.pitan76.mcpitanlib.api.block.CompatWaterloggable;
import net.pitan76.mcpitanlib.api.block.args.RenderTypeArgs;
import net.pitan76.mcpitanlib.api.block.args.v2.OutlineShapeEvent;
import net.pitan76.mcpitanlib.api.block.args.v2.PlacementStateArgs;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.*;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.state.property.CompatProperties;
import net.pitan76.mcpitanlib.api.text.CompatFormatting;
import net.pitan76.mcpitanlib.api.text.CompatStyle;
import net.pitan76.mcpitanlib.api.text.TextComponent;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.DirectionBoolPropertyUtil;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.fluid.FluidState;
import net.pitan76.mcpitanlib.midohra.fluid.Fluids;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.util.shape.VoxelShape;
import net.pitan76.mcpitanlib.midohra.world.World;
import net.pitan76.nexton.core.api.util.EnergyUtil;
import net.pitan76.nexton.core.fabric.compat.RebornEnergyRegister;
import net.pitan76.nexton.dynamics.CableNetworkManager;
import net.pitan76.nexton.dynamics.Config;
import net.pitan76.nexton.dynamics.block.entity.AbstractEnergyBlockEntity;
import net.pitan76.nexton.dynamics.block.entity.EnergyCableBlockEntity;
import org.jetbrains.annotations.Nullable;

public class EnergyCable extends AbstractCable implements CompatWaterloggable {
    public int speed;

    protected final CompatMapCodec<EnergyCable> CODEC = CompatBlockMapCodecUtil.createCodec((settings ->
            new EnergyCable(settings, Config.getEnergyCableTransferRate())));

    @Override
    public CompatMapCodec<? extends EnergyCable> getCompatCodec() {
        return CODEC;
    }

    public EnergyCable(CompatibleBlockSettings settings, int speed) {
        super(settings);
        setDefaultState(DirectionBoolPropertyUtil.clearAll(getDefaultMidohraState()).with(CompatProperties.WATERLOGGED, false));
        this.speed = speed;
    }

    public EnergyCable(CompatibleBlockSettings settings) {
        this(settings, Config.getEnergyCableTransferRate());
    }

    @Override
    public VoxelShape getOutlineShapeM(OutlineShapeEvent e) {
        VoxelShape shape = getCenterShape();

        if (e.get(CompatProperties.UP)) {
            shape = shape.union(
                    VoxelShape.blockCuboid(5.0, 11.0, 5.0, 11.0, 16.0, 11.0));
        }
        if (e.get(CompatProperties.DOWN)) {
            shape = shape.union(
                    VoxelShape.blockCuboid(5.0, 0.0, 5.0, 11.0, 5.0, 11.0));
        }
        if (e.get(CompatProperties.NORTH)) {
            shape = shape.union(
                    VoxelShape.blockCuboid(5.0, 5.0, 0.0, 11.0, 11.0, 5.0));
        }
        if (e.get(CompatProperties.EAST)) {
            shape = shape.union(
                    VoxelShape.blockCuboid(11.0, 5.0, 5.0, 16.0, 11.0, 11.0));
        }
        if (e.get(CompatProperties.SOUTH)) {
            shape = shape.union(
                    VoxelShape.blockCuboid(5.0, 5.0, 11.0, 11.0, 11.0, 16.0));
        }
        if (e.get(CompatProperties.WEST)) {
            shape = shape.union(
                    VoxelShape.blockCuboid(0.0, 5.0, 5.0, 5.0, 11.0, 11.0));
        }

        return shape;
    }

    public VoxelShape getCenterShape() {
        return VoxelShape.blockCuboid(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {
        var blockEntityWrapper = e.getBlockEntityWrapper();
        var stack = e.getStackM();

        if (blockEntityWrapper.instanceOf(AbstractEnergyBlockEntity.class) && (stack.isEmpty() || !stack.isBlockItem())) {
            var blockEntity = blockEntityWrapper.getCompatBlockEntity(AbstractEnergyBlockEntity.class);

            if (e.isClient()) return e.success();
            e.getPlayer().sendMessage(new TextComponent("Energy: " + blockEntity.energy + " / " + blockEntity.getCapacityEnergy()));
            CableNetworkManager.printLog(e.getMidohraWorld(), e.getMidohraPos());
        }

        return super.onRightClick(e);
    }

    public void updateConnections(World world, BlockPos pos, EnergyCableBlockEntity tile) {
        if (!DirectionBoolPropertyUtil.hasAll(world.getBlockState(pos))) return;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.offset(dir);
            var neighborTile = world.getBlockEntity(neighborPos);
            if (neighborTile.instanceOf(EnergyCableBlockEntity.class)) {
                DirectionBoolPropertyUtil.setProperty(world, pos, dir, true);
                continue;
            }

//            if (tile.getEnergyStorage() instanceof TREnergyStorage) {
            var rebornEnergyStorage = EnergyUtil.getEnergyStorage(world, neighborPos, dir.getOpposite());
            if (rebornEnergyStorage != null) {
                DirectionBoolPropertyUtil.setProperty(world, pos, dir, true);
                continue;
            }
//            }

            if (neighborTile.instanceOf(AbstractEnergyBlockEntity.class)) {
                DirectionBoolPropertyUtil.setProperty(world, pos, dir, true);
                continue;
            }

            DirectionBoolPropertyUtil.setProperty(world, pos, dir, false);
        }
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        if (!e.isClient()) {
            CableNetworkManager.onCableChanged(e.getMidohraWorld(), e.getMidohraPos());
//            CableNetworkManager.printLog(e.midohraWorld, e.midohraPos);
        }

        var cable = e.getBlockEntityWrapper().getCompatBlockEntity(EnergyCableBlockEntity.class);
        if (cable != null) {
            updateConnections(e.getMidohraWorld(), e.getMidohraPos(), cable);
        }

        super.onStateReplaced(e);
    }

    @Override
    public void onPlaced(BlockPlacedEvent e) {
        if (!e.isClient()) {
            CableNetworkManager.onCableChanged(e.getMidohraWorld(), e.getMidohraPos());
//            CableNetworkManager.printLog(e.midohraWorld, e.midohraPos);
        }

        var cable = e.getBlockEntityWrapper().getCompatBlockEntity(EnergyCableBlockEntity.class);
        if (cable != null) {
            updateConnections(e.getMidohraWorld(), e.getMidohraPos(), cable);
        }

        super.onPlaced(e);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(TileCreateEvent e) {
        return new EnergyCableBlockEntity(e, speed);
    }

    @Override
    public boolean isTick() {
        return true;
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        super.appendProperties(args);
        args.addAllDirectionBoolProperties();
        args.addProperty(CompatProperties.WATERLOGGED);
    }

    @Override
    public CompatBlockRenderType getRenderType(RenderTypeArgs args) {
        return CompatBlockRenderType.MODEL;
    }

    @Override
    public FluidState getFluidStateM(FluidStateArgs args) {
        if (CompatProperties.WATERLOGGED.get(args.state)) {
            return FluidState.water();
        }

        return super.getFluidStateM(args);
    }

    @Override
    public @Nullable BlockState getPlacementState(PlacementStateArgs args) {
        if (args != null) {
            return this.getDefaultMidohraState().with(CompatProperties.WATERLOGGED,
                args.getWorld().getFluid(args.getPos()).equals(Fluids.WATER));
        }

        return super.getPlacementState(args);
    }

    @Override
    public void neighborUpdate(NeighborUpdateEvent e) {
        super.neighborUpdate(e);
        if (e == null) return;

        if (!e.getBlockEntityWrapper().instanceOf(EnergyCableBlockEntity.class)) return;

        updateConnections(e.getMidohraWorld(), e.getMidohraPos(), e.getBlockEntityWrapper().getCompatBlockEntity(EnergyCableBlockEntity.class));
    }

    @Override
    public void appendTooltip(ItemAppendTooltipEvent event) {
        super.appendTooltip(event);
        var style = new CompatStyle().withColor(CompatFormatting.AQUA);
        event.addTooltip(TextComponent.translatable("tooltip.nexton.energy_cable", speed).setStyle(style));
    }
}
