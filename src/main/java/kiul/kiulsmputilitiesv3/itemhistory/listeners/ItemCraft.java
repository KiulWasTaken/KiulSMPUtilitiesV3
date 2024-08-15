package kiul.kiulsmputilitiesv3.itemhistory.listeners;


import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.itemhistory.ItemMethods;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ItemCraft implements Listener {

    ArrayList<Material> historyItems = new ArrayList<>() {{
        add(Material.NETHERITE_HELMET);
        add(Material.NETHERITE_CHESTPLATE);
        add(Material.NETHERITE_LEGGINGS);
        add(Material.NETHERITE_BOOTS);

        add(Material.NETHERITE_HOE);
        add(Material.NETHERITE_PICKAXE);
        add(Material.NETHERITE_AXE);
        add(Material.NETHERITE_SHOVEL);
        add(Material.NETHERITE_SWORD);

        add(Material.DIAMOND_HELMET);
        add(Material.DIAMOND_CHESTPLATE);
        add(Material.DIAMOND_LEGGINGS);
        add(Material.DIAMOND_BOOTS);

        add(Material.DIAMOND_HOE);
        add(Material.DIAMOND_PICKAXE);
        add(Material.DIAMOND_AXE);
        add(Material.DIAMOND_SHOVEL);
        add(Material.DIAMOND_SWORD);

        add(Material.SHIELD);
        add(Material.MACE);
        add(Material.TRIDENT);
    }};

    @EventHandler
    public void craftItemEvent (CraftItemEvent e) {
        if (!C.ITEM_HISTORY_ENABLED) {return;}
        if (historyItems.contains(e.getRecipe().getResult().getType())) {
            if ((e.getView().getBottomInventory().firstEmpty() != -1)) {
                LocalDate currentDate = LocalDate.now();
                ItemMethods.addLore(e.getInventory().getResult(),ChatColor.GRAY + "\uD83D\uDEE0 - " + ((Player)e.getView().getPlayer()).getDisplayName() + ChatColor.DARK_GRAY + " (" + C.dtf.format(currentDate) + ")");
            }
        }
    }

    @EventHandler
    public void smithDate (SmithItemEvent e) {
        if (!C.ITEM_HISTORY_ENABLED) {return;}
        if (historyItems.contains(e.getInventory().getRecipe().getResult().getType())) {
            LocalDate currentDate = LocalDate.now();
            ItemMethods.addLore(e.getInventory().getResult(),ChatColor.GRAY + "↑ - " + ((Player)e.getView().getPlayer()).getDisplayName() + ChatColor.DARK_GRAY + " (" + C.dtf.format(currentDate) + ")");
        }
    }

    @EventHandler
    public void combineDate (InventoryClickEvent e) {
        if (!C.ITEM_HISTORY_ENABLED) {return;}
        if (e.getInventory() instanceof AnvilInventory && e.getSlot() == 2) {
            if (historyItems.contains(e.getCurrentItem().getType())) {
                LocalDate currentDate = LocalDate.now();
                ItemMethods.addLore(e.getCurrentItem(), ChatColor.GRAY + "\uD83D\uDD27 - " + ((Player) e.getView().getPlayer()).getDisplayName() + ChatColor.DARK_GRAY + " (" + C.dtf.format(currentDate) + ")");
            }
        }
    }
    @EventHandler
    public void enchantDate (EnchantItemEvent e) {
        if (!C.ITEM_HISTORY_ENABLED) {return;}
        if (historyItems.contains(e.getInventory().getItem(0).getType())) {
            LocalDate currentDate = LocalDate.now();
            ItemMethods.addLore(e.getInventory().getItem(0),ChatColor.GRAY + "" + ChatColor.BOLD+ "\uD83D\uDCD6" + ChatColor.RESET+ChatColor.GRAY+ " - " + ((Player)e.getView().getPlayer()).getDisplayName() + ChatColor.DARK_GRAY + " (" + C.dtf.format(currentDate) + ")");
        }
    }

    @EventHandler
    public void grindstoneClear (InventoryClickEvent e) {
        if (!C.ITEM_HISTORY_ENABLED) {return;}
        if (e.getInventory() instanceof GrindstoneInventory && e.getSlot() == 2) {
            if (historyItems.contains(e.getCurrentItem().getType())) {
                List<String> lore = new ArrayList<>();
                e.getCurrentItem().setLore(lore);
            }
        }
    }
}
