package net.pitan76.nexton.dynamics;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.pitan76.mcpitanlib.api.util.BlockEntityUtil;
import net.pitan76.mcpitanlib.core.datafixer.Pair;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.world.World;
import net.pitan76.nexton.core.api.energy.IEnergyStorage;
import net.pitan76.nexton.core.fabric.compat.TREnergyStorage;
import net.pitan76.nexton.dynamics.block.entity.AbstractEnergyBlockEntity;
import net.pitan76.nexton.dynamics.block.entity.EnergyCableBlockEntity;
import net.pitan76.nexton.dynamics.compat.EnergyStorageWrapper;
import net.pitan76.nexton.dynamics.compat.RebornEnergyRegister;
import net.pitan76.nexton.dynamics.compat.*;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class CableNetworkManager {

    private static final Map<UUID, CableNetwork> networkMap = new HashMap<>();
    private static final Map<Pair<String, BlockPos>, UUID> cablePosToNetworkId = new HashMap<>();

    private static String getWorldId(World world) {
        return world.getId().toString();
    }

    public static class CableNetwork {
        public UUID id;
        public Set<Pair<EnergyCableBlockEntity, IEnergyStorage>> cables;
        public Set<Pair<BlockEntity, IEnergyStorage>> tiles;

        public CableNetwork(UUID id,
                            Set<Pair<EnergyCableBlockEntity, IEnergyStorage>> cables,
                            Set<Pair<BlockEntity, IEnergyStorage>> tiles) {
            this.id = id;
            this.cables = cables;
            this.tiles = tiles;
        }

        public void tick() {
            if (tiles.isEmpty()) return;

            // providers
            List<Pair<BlockEntity, IEnergyStorage>> providers = new ArrayList<>();
            for (Pair<BlockEntity, IEnergyStorage> p : tiles) {
                IEnergyStorage s = p.getB();
                if (s.getEnergyStored() > 0 && s.canExtractEnergy() && s.getMaxOutputEnergy() > 0) {
                    providers.add(p);
                }
            }

            for (Pair<BlockEntity, IEnergyStorage> p : providers) {
                IEnergyStorage storage = p.getB();

                long totalCapacity = 0;
                for (Pair<EnergyCableBlockEntity, IEnergyStorage> c : cables) {
                    IEnergyStorage s = c.getB();
                    totalCapacity += (s.getCapacityEnergy() - s.getEnergyStored());
                }

                if (totalCapacity <= 0) break;

                long takeAmount = Math.min(storage.getEnergyStored(), totalCapacity);
                if (takeAmount <= 0) continue;

                long extracted = storage.extractEnergy(takeAmount);
                if (extracted <= 0) continue;

                long remaining = extracted;

                for (Pair<EnergyCableBlockEntity, IEnergyStorage> c : cables) {
                    if (remaining <= 0) break;

                    IEnergyStorage cableStorage = c.getB();
                    long space = cableStorage.getCapacityEnergy() - cableStorage.getEnergyStored();
                    long give = Math.min(space, remaining);

                    cableStorage.setEnergyStored(cableStorage.getEnergyStored() + give);
                    remaining -= give;
                }
            }

            // consumers
            List<Pair<BlockEntity, IEnergyStorage>> consumers = new ArrayList<>();
            for (Pair<BlockEntity, IEnergyStorage> p : tiles) {
                IEnergyStorage s = p.getB();
                if (s.getEnergyStored() < s.getCapacityEnergy() && s.canInsertEnergy() && s.getMaxInputEnergy() > 0) {
                    consumers.add(p);
                }
            }

            for (Pair<BlockEntity, IEnergyStorage> p : consumers) {
                IEnergyStorage storage = p.getB();

                long capacity = storage.getUsableCapacity() - storage.getEnergyStored();
                if (capacity <= 0) continue;

                long available = 0;
                for (Pair<EnergyCableBlockEntity, IEnergyStorage> c : cables) {
                    EnergyCableBlockEntity cable = c.getA();
                    IEnergyStorage cableStorage = c.getB();
                    available += Math.min(cableStorage.getEnergyStored(), cable.getMaxOutput());
                }

                if (available <= 0) continue;

                long pushAmount = Math.min(capacity, available);

                long inserted = storage.insertEnergy(pushAmount);
                if (inserted <= 0) continue;

                long remaining = inserted;

                for (Pair<EnergyCableBlockEntity, IEnergyStorage> c : cables) {
                    if (remaining <= 0) break;

                    EnergyCableBlockEntity cable = c.getA();
                    IEnergyStorage cableStorage = c.getB();

                    long take = Math.min(Math.min(cableStorage.getEnergyStored(), cable.getMaxOutput()), remaining);
                    if (take > 0) {
                        cableStorage.setEnergyStored(cableStorage.getEnergyStored() - take);
                        remaining -= take;
                    }
                }
            }
        }
    }

    public static CableNetwork getOrCreateNetwork(World world, BlockPos pos) {
        Pair<String, BlockPos> key = new Pair<>(getWorldId(world), pos);
        UUID networkId = cablePosToNetworkId.get(key);

        if (networkId != null) {
            CableNetwork network = networkMap.get(networkId);
            if (network != null) return network;
        }

        return searchNetwork(world, pos);
    }

    public static void clearCache() {
        networkMap.clear();
        cablePosToNetworkId.clear();
    }

    public static void clearCache(World world) {
        String worldId = getWorldId(world);

        cablePosToNetworkId.entrySet().removeIf(e -> e.getKey().getA().equals(worldId));
        networkMap.clear();
    }

    public static CableNetwork searchNetwork(World world, BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();

        Set<Pair<EnergyCableBlockEntity, IEnergyStorage>> cables = new HashSet<>();
        Set<Pair<BlockEntity, IEnergyStorage>> tiles = new HashSet<>();

        queue.add(startPos);

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.removeFirst();
            if (!visited.add(currentPos)) continue;

            BlockEntity tile = world.getBlockEntity(currentPos).get();

            if (tile instanceof EnergyCableBlockEntity cable) {

                if (cable.getEnergyStorage() == null) {
                    cable.setEnergyStorage(new TREnergyStorage(cable.energyStorage));
                }

                cables.add(new Pair<>(cable, cable.getEnergyStorage()));

                for (Direction dir : Direction.values()) {
                    BlockPos neighborPos = currentPos.offset(dir);
                    BlockEntity neighborTile = world.getBlockEntity(neighborPos).get();

                    if (neighborTile instanceof EnergyCableBlockEntity) {
                        if (!visited.contains(neighborPos)) {
                            queue.add(neighborPos);
                        }
                    } else if (neighborTile != null) {

                        boolean alreadyAdded = false;
                        for (Pair<BlockEntity, IEnergyStorage> t : tiles) {
                            if (t.getA() == neighborTile) {
                                alreadyAdded = true;
                                break;
                            }
                        }
                        if (alreadyAdded) continue;

                        if (cable.getEnergyStorage() instanceof TREnergyStorage) {
                            EnergyStorage found =
                                    RebornEnergyRegister.ENERGY_LOOKUP
                                            .find(world, neighborPos, dir.getOpposite());

                            if (found != null) {
                                tiles.add(new Pair<>(neighborTile, new EnergyStorageWrapper(found)));
                            }
                        } else if (neighborTile instanceof AbstractEnergyBlockEntity be) {
                            if (be.getEnergyStorage() != null) {
                                tiles.add(new Pair<>(be, be.getEnergyStorage()));
                            }
                        }
                    }
                }
            }
        }

        UUID newId = UUID.randomUUID();

        for (Pair<EnergyCableBlockEntity, IEnergyStorage> p : cables) {
            EnergyCableBlockEntity cable = p.getA();
            IEnergyStorage storage = p.getB();

            UUID oldId = cable.networkId;
            if (!oldId.equals(newId)) {
                CableNetwork old = networkMap.get(oldId);
                if (old != null) {
                    old.cables.remove(new Pair<>(cable, storage));
                }
            }
        }

        for (Pair<EnergyCableBlockEntity, IEnergyStorage> p : cables) {
            EnergyCableBlockEntity cable = p.getA();
            cable.networkId = newId;

            BlockPos cablePos = cable.getMidohraPos();
            cablePosToNetworkId.put(new Pair<>(getWorldId(world), cablePos), newId);
        }

        CableNetwork network = new CableNetwork(newId, cables, tiles);
        networkMap.put(newId, network);

        return network;
    }

    public static void onCableChanged(World world, BlockPos pos) {
        List<BlockPos> positions = Arrays.asList(
                pos,
                pos.offset(Direction.UP),
                pos.offset(Direction.DOWN),
                pos.offset(Direction.NORTH),
                pos.offset(Direction.SOUTH),
                pos.offset(Direction.WEST),
                pos.offset(Direction.EAST)
        );

        for (BlockPos p : positions) {
            BlockEntity tile = world.getBlockEntity(p).get();
            if (tile instanceof EnergyCableBlockEntity) {
                searchNetwork(world, p);
            }
        }
    }

    public static void printLog(World world, BlockPos pos) {
        CableNetwork network = getOrCreateNetwork(world, pos);

        System.out.println("Cable Network ID: " + network.id);

        System.out.println("Cables (" + network.cables.size() + "):");
        for (Pair<EnergyCableBlockEntity, IEnergyStorage> p : network.cables) {
            EnergyCableBlockEntity cable = p.getA();
            IEnergyStorage storage = p.getB();

            System.out.println("- " + cable.getMidohraPos().toRaw() + ": " +
                    storage.getEnergy() + "/" + storage.getMaxEnergy());
        }

        System.out.println("Tiles (" + network.tiles.size() + "):");
        for (Pair<BlockEntity, IEnergyStorage> p : network.tiles) {
            BlockEntity tile = p.getA();
            IEnergyStorage storage = p.getB();

            System.out.println("- " + BlockEntityUtil.getPos(tile) + ": " +
                    storage.getEnergy() + "/" + storage.getMaxEnergy());
        }

        System.out.println();
    }

    public static CableNetwork getNetworkById(UUID id) {
        return networkMap.get(id);
    }
}