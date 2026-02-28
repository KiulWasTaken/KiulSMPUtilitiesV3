package kiul.kiulsmputilitiesv3.towns.listeners;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.towns.Town;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static kiul.kiulsmputilitiesv3.C.getHighestBlockY;

public class TownBlock implements Listener {


    @EventHandler
    public void placeTown (BlockPlaceEvent e) {
        if (!e.getItemInHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING)) return;
        if (e.getItemInHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING).equalsIgnoreCase("towncore")) {
            if (C.getPlayerTeam(e.getPlayer()) == null || C.getPlayerTeam(e.getPlayer()).getEntries().size() < 1) {
                e.getPlayer().sendMessage(C.failPrefix+" cannot place a town core without a team that has at least 1 members!");
                e.setCancelled(true);
                return;
            }
            if (e.getBlockPlaced().getLocation().y() > 100 || e.getBlockPlaced().getLocation().y() < 60) {
                e.getPlayer().sendMessage(C.failPrefix+" town cores can only be placed between y60-100");
                e.setCancelled(true);
                return;
            }
            for (Town town : Town.townsList) {
                if (town.getTownCenter().distance(e.getBlockPlaced().getLocation()) < 500) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(C.failPrefix+" you cannot place town cores within 500 blocks of the claim of another town!");
                    return;
                }
            }
            e.getPlayer().sendMessage(C.GOLD+C.t("&oSneak click to change the name of the town"));
            Town town = new Town(e.getBlockPlaced().getLocation(), e.getPlayer());
            Town.townsList.add(town);
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(e.getPlayer().getWorld()))) {
                    int radius = town.getTownProtectedRadius();

                    Location cornerA = town.getTownCenter().clone().add(-radius, 0, -radius);

                    for (int x = 0; x <= radius * 2; x++) {
                        Location loc1 = cornerA.clone().add(x, 0, 0);
                        Location loc2 = loc1.clone().add(0, 0, radius * 2);
                        loc1.setY(getHighestBlockY(loc1));
                        loc2.setY(getHighestBlockY(loc2));
                        editSession.setBlock(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(), new BaseBlock(BlockTypes.BEDROCK.getDefaultState()));
                        editSession.setBlock(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ(), new BaseBlock(BlockTypes.BEDROCK.getDefaultState()));
                        editSession.setBlock(loc1.getBlockX(), loc1.getBlockY() + 1, loc1.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
                        editSession.setBlock(loc2.getBlockX(), loc2.getBlockY() + 1, loc2.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
                    }
                    for (int z = 0; z <= radius * 2; z++) {
                        Location loc1 = cornerA.clone().add(0, 0, z);
                        Location loc2 = loc1.clone().add(radius * 2, 0, 0);
                        loc1.setY(getHighestBlockY(loc1));
                        loc2.setY(getHighestBlockY(loc2));
                        editSession.setBlock(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(), new BaseBlock(BlockTypes.BEDROCK.getDefaultState()));
                        editSession.setBlock(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ(), new BaseBlock(BlockTypes.BEDROCK.getDefaultState()));
                        editSession.setBlock(loc1.getBlockX(), loc1.getBlockY() + 1, loc1.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
                        editSession.setBlock(loc2.getBlockX(), loc2.getBlockY() + 1, loc2.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
                    }
                }
        }
    }

    @EventHandler
    public void breakTown (BlockBreakEvent e) {
        if (e.getBlock().getType().equals(Material.RESPAWN_ANCHOR)) {
            for (Town town : Town.townsList) {
                if (town.getTownCenter().distance(e.getBlock().getLocation()) <= 1) {
                    if (town.getOwningTeam().equals(C.getPlayerTeam(e.getPlayer()))) {
                        // gui to confirm destroy town
                        confirmDestroyTown(e.getPlayer());
                        e.setCancelled(true);
                    } else {
                        e.setCancelled(true);
                        town.damageTownShield(5, false,e.getPlayer(),e.getBlock().getLocation().clone().add(0,1,0));
                    }

                    return;
                }
            }
        }
    }

    public void confirmDestroyTown(Player p) {
        Inventory hopperInv = Bukkit.createInventory(p, InventoryType.HOPPER, "Destroy Town Core?");

        List<String> emptylore = new ArrayList<>();
        emptylore.add("");

        for (int i = 0; i < hopperInv.getSize(); i++) {
            hopperInv.setItem(i, C.createItemStack("", Material.GRAY_STAINED_GLASS_PANE, 1, new String[]{""}, null, null, null,null));
        }

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"This will remove your town and all");
        lore.add(ChatColor.GRAY+"protections will be immediately voided");
        ItemStack yesItem = C.createItemStack(C.successPrefix+"Confirm Destroy",Material.LIME_WOOL,1,lore.toArray(String[]::new),null,null,"yes",null);





        lore.clear();
        lore.add(ChatColor.GRAY+"Cancel destroy town");
        ItemStack noItem = C.createItemStack(C.failPrefix+"Cancel",Material.RED_WOOL,1,lore.toArray(String[]::new),null,null,"no",null);

        // Slot 1 for Yes, Slot 3 for No
        hopperInv.setItem(3, yesItem);
        hopperInv.setItem(1, noItem);

        p.openInventory(hopperInv);
    }

    @EventHandler
    public void clickConfirmGui (InventoryClickEvent e) {
        Player p = (Player) e.getView().getPlayer();
        if (e.getView().getTitle().equalsIgnoreCase("Destroy Town Core?")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"),PersistentDataType.STRING)) {
                String localName = e.getCurrentItem().getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"),PersistentDataType.STRING);
                switch (localName) {
                    case "yes":
                        if (Town.getTownForPlayer(p) == null) {
                            p.closeInventory();
                            return;
                        }
                        Town.getTownForPlayer(p).destroy();
                        p.closeInventory();
                        break;
                    case "no":
                        p.closeInventory();
                        break;
                }
            }
        }
    }
}
