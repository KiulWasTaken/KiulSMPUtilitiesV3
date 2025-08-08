package kiul.kiulsmputilitiesv3.towns.listeners;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.InventoryToBase64;
import kiul.kiulsmputilitiesv3.towns.Town;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Set;

public class ProtectedBlocks implements Listener {


    @EventHandler
    public void protectedBlockBreakEvent (BlockBreakEvent e) {
        if (!e.getBlock().hasMetadata("unauth")) { // if the broken block is an unauthorised placed block, cancel this method so that the block broken will drop and not regenerate.
            Block block = e.getBlock();
            Player p = e.getPlayer();
            boolean isInsideProtectedZone = false;
            boolean isOnOwningTeam = false;

            String containerInventoryContents = null;
            if (e.getBlock().getState() instanceof Container container) {
                containerInventoryContents = InventoryToBase64.itemStackArrayToBase64(container.getInventory().getContents().clone());
            }
            Town town = null;
            for (Town allTowns : Town.townsList) {
                if (allTowns.protectedAreaContains(block.getLocation())) {
                    town = allTowns;
                    isInsideProtectedZone = true;
                    if (allTowns.getOwningTeam() != null) {
                        isOnOwningTeam = allTowns.getOwningTeam().equals(C.getPlayerTeam(p));
                    }
                    break;
                }
            }
            if (town == null) {return;}

            if (isOnOwningTeam) {
                if (block.hasMetadata("unbreakable")) {
                    e.setDropItems(false);
                    scheduleBlockRespawn(e.getBlock(), System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS), e.getBlock().getType(),false,containerInventoryContents,e.getBlock().getBlockData(),town);
                }
                return;
            } // return early if the player is on the owning team, because if this check is run it means they are inside a protected zone they own and therefore no more resources should be wasted.

            if (isInsideProtectedZone) {
                e.setDropItems(false);

                scheduleBlockRespawn(e.getBlock(), System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS), e.getBlock().getType(),false,containerInventoryContents,e.getBlock().getBlockData(),town);
            }
        }
    }

    @EventHandler
    public void protectedBlockPlaceEvent (BlockPlaceEvent e) {
        Block block = e.getBlock();
        Player p = e.getPlayer();

        boolean isInsideProtectedZone = false;
        boolean isOnOwningTeam = false;

        Town town = null;
        for (Town allTowns : Town.townsList) {
            if (allTowns.protectedAreaContains(block.getLocation())) {
                town = allTowns;
                isInsideProtectedZone = true;
                if (allTowns.getOwningTeam() != null) {
                    isOnOwningTeam = allTowns.getOwningTeam().equals(C.getPlayerTeam(p));
                }
                break;
            }
        }
        if (town == null) {return;}
        if (isOnOwningTeam) {
            if (block.hasMetadata("unbreakable")) {
                e.getBlock().setMetadata("unauth",new FixedMetadataValue(C.plugin,"block"));
                BlockState oldBlock = e.getBlockReplacedState();
                scheduleBlockRespawn(e.getBlock(), System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS), oldBlock.getType(),false,null,oldBlock.getBlockData(),town);
            }
            return;
        } // return early if the player is on the owning team, because if this check is run it means they are inside a protected zone they own and therefore no more resources should be wasted.


        if (isInsideProtectedZone) {
            e.getBlock().setMetadata("unauth",new FixedMetadataValue(C.plugin,"block"));
            BlockState oldBlock = e.getBlockReplacedState();
            scheduleBlockRespawn(e.getBlock(),System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS),oldBlock.getType(),true,null,oldBlock.getBlockData(),town);
        }
    }

    @EventHandler
    public void protectedBlockExplodeByBlockEvent (BlockExplodeEvent e) {
        Block block = e.getBlock();

        boolean isInsideProtectedZone = false;
        Town town = null;
        for (Town allTowns : Town.townsList) {
            if (allTowns.protectedAreaContains(block.getLocation())) {
                isInsideProtectedZone = true;
                town = allTowns;
                break;
            }
        }



        if (town == null) {return;}
        if (isInsideProtectedZone) {
            e.setYield(0);
            for (Block explodedBlock : e.blockList()) {
                if (explodedBlock.hasMetadata("unauth")) { // if the broken block is an unauthorised placed block, cancel this method so that the block broken will not regenerate.
                    continue;
                }
                String containerInventoryContents = null;
                if (explodedBlock.getState() instanceof Container container) {
                    containerInventoryContents = InventoryToBase64.itemStackArrayToBase64(container.getInventory().getContents().clone());
                }
                scheduleBlockRespawn(explodedBlock, System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS), explodedBlock.getType(), false, containerInventoryContents, explodedBlock.getBlockData(),town);
            }
        }
    }

    @EventHandler
    public void protectedBlockExplodeByEntityEvent (EntityExplodeEvent e) {

        boolean isInsideProtectedZone = false;

        Town town = null;
        for (Town allTowns : Town.townsList) {
            if (allTowns.protectedAreaContains(e.getLocation())) {
                isInsideProtectedZone = true;
                town = allTowns;
                break;
            }
        }



        if (town == null) {return;}

        if (isInsideProtectedZone) {
            e.setYield(0);
            for (Block explodedBlock : e.blockList()) {
                if (explodedBlock.hasMetadata("unauth")) { // if the broken block is an unauthorised placed block, cancel this method so that the block broken will not regenerate.
                    continue;
                }
                String containerInventoryContents = null;
                if (explodedBlock.getState() instanceof Container container) {
                    containerInventoryContents = InventoryToBase64.itemStackArrayToBase64(container.getInventory().getContents().clone());
                }
                scheduleBlockRespawn(explodedBlock, System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS), explodedBlock.getType(), false, containerInventoryContents, explodedBlock.getBlockData(),town);
            }
        }
    }

    private static final Set<Material> RESTRICTED_BLOCKS = Set.of(
            Material.STONE_BUTTON,
            Material.OAK_BUTTON,
            Material.ACACIA_BUTTON,
            Material.CHERRY_BUTTON,
            Material.BAMBOO_BUTTON,
            Material.BIRCH_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.WARPED_BUTTON,
            Material.CRIMSON_BUTTON,

            Material.TNT,
            Material.LEVER,
            Material.CHEST,
            Material.BARREL,
            Material.DISPENSER,
            Material.DROPPER,
            Material.HOPPER,
            Material.LECTERN,
            Material.BREWING_STAND,
            Material.CRAFTER,
            Material.FURNACE,
            Material.SHULKER_BOX,
            Material.SMOKER,
            Material.BLAST_FURNACE,

            Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
            Material.STONE_PRESSURE_PLATE,
            Material.OAK_PRESSURE_PLATE,
            Material.SPRUCE_PRESSURE_PLATE,
            Material.BIRCH_PRESSURE_PLATE,
            Material.ACACIA_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE,
            Material.CRIMSON_PRESSURE_PLATE,
            Material.WARPED_PRESSURE_PLATE
    );

    @EventHandler
    public void protectedBlockMaliciousInteractEvent (PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (block == null) {
            return;
        }
        if (RESTRICTED_BLOCKS.contains(block.getType())) {
            if (!block.hasMetadata("unauth")) { // if the broken block is an unauthorised placed block, cancel this method so that the block can be interacted with as normal (placed tnt, buttons ,etc.)
                Player p = e.getPlayer();
                boolean isInsideProtectedZone = false;
                boolean isOnOwningTeam = false;

                for (Town allTowns : Town.townsList) {
                    if (allTowns.protectedAreaContains(block.getLocation())) {
                        isInsideProtectedZone = true;
                        if (allTowns.getOwningTeam() != null) {
                            isOnOwningTeam = allTowns.getOwningTeam().equals(C.getPlayerTeam(p));
                        }
                        break;
                    }
                }
                if (isOnOwningTeam) {
                    if (block.hasMetadata("unbreakable")) {
                        p.sendMessage(C.fMsg("Shrine structure blocks are completely indestructible and cannot be interacted with even if you are the owner."));
                        e.setCancelled(true);
                    }
                    return;
                } // return early if the player is on the owning team, because if this check is run it means they are inside a protected zone they own and therefore no more resources should be wasted.


                if (isInsideProtectedZone) {
                    p.sendMessage(C.fMsg("You cannot interact with blocks inside a shrine you do not own"));
                    e.setCancelled(true);

                }
            }
        }
    }



    public static void scheduleBlockRespawn(Block block, long timeUntilRegenerate, Material finalType, boolean isPlacedBlock, String containerInventoryBase64, BlockData blockData, Town town) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isPlacedBlock && block.getType().equals(Material.AIR)) {
                    cancel();
                }
                if (System.currentTimeMillis() >= timeUntilRegenerate) {
                    boolean nearbyPlayer = false;
                    for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(), 5, 5, 5)) {
                        if (entity instanceof Player) {
                            nearbyPlayer = true;
                            break;
                        }
                    }

                    if (!nearbyPlayer) {
                        block.setType(finalType);
                        block.setBlockData(blockData);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                BlockState state = block.getState();

                                if (state instanceof Container container) {

                                    ItemStack[] containerInventoryContents = new ItemStack[0];
                                    try {
                                        containerInventoryContents = InventoryToBase64.itemStackArrayFromBase64(containerInventoryBase64);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                    Inventory inventory = container.getInventory();
                                    InventoryHolder holder = inventory.getHolder();

                                    if (holder instanceof DoubleChest doubleChest) {
                                        Chest leftChest = (Chest) doubleChest.getLeftSide();
                                        Chest rightChest = (Chest) doubleChest.getRightSide();

                                        boolean leftBroken = leftChest.getBlock().equals(block);
                                        boolean rightBroken = rightChest.getBlock().equals(block);

                                        if (leftBroken) {
                                            Inventory leftInventory = leftChest.getInventory();
                                            for (int i = 0; i < containerInventoryContents.length / 2; i++) {
                                                leftInventory.setItem(i, containerInventoryContents[i]);
                                            }
                                        }

                                        if (rightBroken) {
                                            Inventory rightInventory = rightChest.getInventory();
                                            for (int i = containerInventoryContents.length / 2; i < containerInventoryContents.length; i++) {
                                                rightInventory.setItem(i, containerInventoryContents[i-27]);
                                            }
                                        }

                                    } else {
                                        // Single chest restoration
                                        if (containerInventoryContents != null) {
                                            for (int i = 0; i < Math.min(containerInventoryContents.length, inventory.getSize()); i++) {
                                                inventory.setItem(i, containerInventoryContents[i]);
                                            }
                                        }
                                    }
                                }

                                // deal damage to town hp


                            }
                        }.runTaskLater(C.plugin, 1);

                        cancel();
                    }
                }
            }
        }.runTaskTimer(C.plugin, 10, 20);
    }
}
