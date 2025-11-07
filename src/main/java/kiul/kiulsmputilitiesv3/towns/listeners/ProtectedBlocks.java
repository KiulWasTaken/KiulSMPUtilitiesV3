package kiul.kiulsmputilitiesv3.towns.listeners;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.InventoryToBase64;
import kiul.kiulsmputilitiesv3.towns.Town;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.*;

public class ProtectedBlocks implements Listener {

    private final HashMap<Entity, Player> entityOwner = new HashMap<>();

    @EventHandler
    public void onTNTPrime(TNTPrimeEvent event) {
        // Check if TNT was primed by a player
        if (event.getPrimingEntity() instanceof Player player) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            player.playSound(event.getBlock().getLocation(), Sound.ENTITY_TNT_PRIMED,1f,1f);
            event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0, 0.5), TNTPrimed.class, tnt -> {
                // Store in the map when the TNT entity is actually created
                entityOwner.put(tnt, player);
            });// No extra action needed here, TNT spawning handled inline
        }
    }

    @EventHandler
    public void protectedBlockBreakEvent (BlockBreakEvent e) {
        if (!e.getBlock().hasMetadata("unauth")) { // if the broken block is an unauthorised placed block, cancel this method so that the block broken will drop and not regenerate.
            Block block = e.getBlock();
            if (block.getType().equals(Material.RESPAWN_ANCHOR)) {
                e.setCancelled(true);
                return;
            }
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
            if (town.isDisabled()) {
                town.breakBlockInDisabledTownArea(e.getBlock().getLocation(),e.getBlock().getType());
                return;
            }
            if (isOnOwningTeam) {
                if (block.hasMetadata("unbreakable")) {
                    e.setDropItems(false);
                    scheduleBlockRespawn(e.getBlock(), System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS), e.getBlock().getType(),false,containerInventoryContents,e.getBlock().getBlockData(),town,false,e.getPlayer());
                }
                return;
            } // return early if the player is on the owning team, because if this check is run it means they are inside a protected zone they own and therefore no more resources should be wasted.

            if (isInsideProtectedZone) {
                e.setDropItems(false);
                scheduleBlockRespawn(e.getBlock(), System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS), e.getBlock().getType(),false,containerInventoryContents,e.getBlock().getBlockData(),town,false,e.getPlayer());
            }
            town.damageTownShield(1, true,e.getPlayer(),e.getBlock().getLocation());
        }
    }


    static final Set<Material> BANNED_PLACE_IN_TOWN = new HashSet<>() {{
       add(Material.PISTON);
       add(Material.STICKY_PISTON);
    }};

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
        if (town.isDisabled()) {
            BlockState oldBlock = e.getBlockReplacedState();
            town.breakBlockInDisabledTownArea(e.getBlock().getLocation(),oldBlock.getType());
            return;
        }
        if (isOnOwningTeam) {
            if (block.hasMetadata("unbreakable")) {
                e.getBlock().setMetadata("unauth",new FixedMetadataValue(C.plugin,"block"));
                BlockState oldBlock = e.getBlockReplacedState();
                scheduleBlockRespawn(e.getBlock(), System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS), oldBlock.getType(),false,null,oldBlock.getBlockData(),town,false, e.getPlayer());
            }
            return;
        } // return early if the player is on the owning team, because if this check is run it means they are inside a protected zone they own and therefore no more resources should be wasted.

        if (BANNED_PLACE_IN_TOWN.contains(e.getBlock().getType())) {
            e.setCancelled(true);
            return;
        }

        if (isInsideProtectedZone) {
            e.getBlock().setMetadata("unauth",new FixedMetadataValue(C.plugin,"block"));
            BlockState oldBlock = e.getBlockReplacedState();
            scheduleBlockRespawn(e.getBlock(),System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS),oldBlock.getType(),true,null,oldBlock.getBlockData(),town,false, e.getPlayer());
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
        if (town.isDisabled()) {
            for (Block explodedBlock : e.blockList()) {
                town.breakBlockInDisabledTownArea(explodedBlock.getLocation(), explodedBlock.getType());
            }
            return;
        }
        if (isInsideProtectedZone) {
            e.setYield(0);
            for (Block explodedBlock : e.blockList()) {
                if (town.isDisabled()) {
                    town.breakBlockInDisabledTownArea(e.getBlock().getLocation(),e.getBlock().getType());
                    continue;
                }
                if (explodedBlock.hasMetadata("unauth")) { // if the broken block is an unauthorised placed block, cancel this method so that the block broken will not regenerate.
                    continue;
                }
                String containerInventoryContents = null;
                if (explodedBlock.getState() instanceof Container container) {
                    containerInventoryContents = InventoryToBase64.itemStackArrayToBase64(container.getInventory().getContents().clone());
                }
                scheduleBlockRespawn(explodedBlock, System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS), explodedBlock.getType(), false, containerInventoryContents, explodedBlock.getBlockData(),town,true,null);
            }
            // deal damage to town hp
            town.damageTownShield(e.blockList().size(), true,null,e.getBlock().getLocation());
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
        if ((entityOwner.get(e.getEntity()) != null &&  C.getPlayerTeam(entityOwner.get(e.getEntity())) == town.getOwningTeam())) {
            return;
        }
        if (town.isDisabled()) {
            for (Block explodedBlock : e.blockList()) {
                town.breakBlockInDisabledTownArea(explodedBlock.getLocation(), explodedBlock.getType());
            }
            return;
        }
        if (isInsideProtectedZone) {
            e.setYield(0);

            // Explicitly stop XP drops from blocks

            for (Block explodedBlock : e.blockList()) {
                if (town.isDisabled()) {
                    town.breakBlockInDisabledTownArea(explodedBlock.getLocation(),explodedBlock.getType());
                    continue;
                }
                if (explodedBlock.hasMetadata("unauth")) { // if the broken block is an unauthorised placed block, cancel this method so that the block broken will not regenerate.
                    continue;
                }
                String containerInventoryContents = null;
                if (explodedBlock.getState() instanceof Container container) {
                    containerInventoryContents = InventoryToBase64.itemStackArrayToBase64(container.getInventory().getContents().clone());
                }
                scheduleBlockRespawn(explodedBlock, System.currentTimeMillis() + (1000L * C.BLOCK_REGEN_SECONDS), explodedBlock.getType(), false,
                        containerInventoryContents, explodedBlock.getBlockData(),town,true, entityOwner.get(e.getEntity()));
            }


            e.blockList().forEach(block -> {
                if (block.getType().equals(Material.TNT) && block.hasMetadata("unauth")) {
                    TNTPrimed tntPrimed = (TNTPrimed) block.getLocation().getWorld().spawnEntity(block.getLocation().add(0.5,0,0.5), EntityType.TNT);
                    tntPrimed.setFuseTicks(new Random().nextInt(10,31));
                }
                block.setType(Material.AIR, false); // break without XP
            });
            // deal damage to town hp
            town.damageTownShield(e.blockList().size(), true,entityOwner.get(e.getEntity()),e.getLocation());
            e.setCancelled(true);
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
                Town town = null;
                for (Town allTowns : Town.townsList) {
                    if (allTowns.protectedAreaContains(block.getLocation())) {
                        isInsideProtectedZone = true;
                        town = allTowns;
                        if (allTowns.getOwningTeam() != null) {
                            isOnOwningTeam = allTowns.getOwningTeam().equals(C.getPlayerTeam(p));
                        }
                        break;
                    }
                }
                if (town == null) return;
                if (town.isDisabled()) return;
                if (isOnOwningTeam) {
                    if (block.hasMetadata("unbreakable")) {
                        p.sendMessage(C.fMsg("Shrine structure blocks are completely indestructible and cannot be interacted with even if you are the owner."));
                        e.setCancelled(true);
                    }
                    return;
                } // return early if the player is on the owning team, because if this check is run it means they are inside a protected zone they own and therefore no more resources should be wasted.


                if (isInsideProtectedZone) {

                    p.sendMessage(C.fMsg("You cannot interact with blocks inside a town you are not a member of!"));
                    e.setCancelled(true);

                }
            }
        }
    }

    public static ArrayList<Block> regeneratingBlocks = new ArrayList<>();

    public static Location getNextAirPocket(Location location) {
        double x = location.x();
        int y = (int)location.y();
        double z = location.z();

        int air = 0;
        for (int i = y; i < 320; i++) {

            Location newloc = new Location(location.getWorld(),x,i,z);
            if (newloc.getBlock().getType().equals(Material.AIR)) {
                air++;
            } else {
                air = 0;
            }

            if (air > 1) {
                y = i-1;
                break;
            }
        }

        return new Location(location.getWorld(),x,y,z);
    }

    public static HashMap<Block,Material> regeneratingBlockFinalTypeHash = new HashMap<>();

    public static void scheduleBlockRespawn(Block block, long timeUntilRegenerate, Material finalType, boolean isPlacedBlock, String containerInventoryBase64, BlockData blockData, Town town, boolean isExplosion,Player attacker) {
        regeneratingBlocks.add(block);
        regeneratingBlockFinalTypeHash.put(block,finalType);
        ProtectedEntities.disableNearbyEntities(block);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isPlacedBlock && block.getType().equals(Material.AIR)) {
                    regeneratingBlocks.remove(block);
                    regeneratingBlockFinalTypeHash.remove(block);
                    cancel();
                }

                if (System.currentTimeMillis() >= timeUntilRegenerate) {
                    boolean nearbyPlayer = false;
                    Collection<Entity> nearbyEntities = block.getWorld().getNearbyEntities(block.getLocation(), 5, 5, 5);
                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof Player) {
                            nearbyPlayer = true;
                            for (Entity nonPlayer : block.getLocation().getNearbyEntities(1,1,1)) {
                                nonPlayer.teleport(getNextAirPocket(nonPlayer.getLocation()));
                            }
                            break;
                        }
                    }

                    if (!nearbyPlayer) {
                        block.setType(finalType);
                        block.setBlockData(blockData);
                        regeneratingBlocks.remove(block);
                        regeneratingBlockFinalTypeHash.remove(block);

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



                            }
                        }.runTaskLater(C.plugin, 1);

                        cancel();
                    }
                }
            }
        }.runTaskTimer(C.plugin, 10, 20);
    }
}
