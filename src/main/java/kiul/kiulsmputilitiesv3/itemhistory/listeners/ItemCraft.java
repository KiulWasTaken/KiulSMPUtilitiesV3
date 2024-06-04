package kiul.kiulsmputilitiesv3.itemhistory.listeners;


import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.itemhistory.ItemMethods;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.HashMap;

public class ItemCraft implements Listener {



    @EventHandler
    public void craftItemEvent (CraftItemEvent e) {
        if (ItemMethods.historyItems.contains(e.getRecipe().getResult().getType())) {
            if ((e.getView().getBottomInventory().firstEmpty() != -1)) {
                LocalDate currentDate = LocalDate.now();
                ItemMethods.addLore(e.getInventory().getResult(),ChatColor.GRAY + "\uD83D\uDEE0 - " + ((Player)e.getView().getPlayer()).getDisplayName() + ChatColor.DARK_GRAY + " (" + C.dtf.format(currentDate) + ")");
            }
        }
    }

    @EventHandler
    public void smithDate (SmithItemEvent e) {
        if (ItemMethods.historyItems.contains(e.getInventory().getRecipe().getResult().getType())) {
            LocalDate currentDate = LocalDate.now();
            ItemMethods.addLore(e.getInventory().getResult(),ChatColor.GRAY + "↑ - " + ((Player)e.getView().getPlayer()).getDisplayName() + ChatColor.DARK_GRAY + " (" + C.dtf.format(currentDate) + ")");
        }
    }

    @EventHandler
    public void combineDate (Anvil e) {
        if (ItemMethods.historyItems.contains(e.getInventory().getRecipe().getResult().getType())) {
            LocalDate currentDate = LocalDate.now();
            ItemMethods.addLore(e.getInventory().getResult(),ChatColor.GRAY + "↑ - " + ((Player)e.getView().getPlayer()).getDisplayName() + ChatColor.DARK_GRAY + " (" + C.dtf.format(currentDate) + ")");
        }
    }
}
