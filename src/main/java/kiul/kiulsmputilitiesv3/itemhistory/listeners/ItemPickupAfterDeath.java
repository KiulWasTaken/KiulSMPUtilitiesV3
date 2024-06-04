package kiul.kiulsmputilitiesv3.itemhistory.listeners;


import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.itemhistory.ItemMethods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDate;
import java.util.ArrayList;

public class ItemPickupAfterDeath implements Listener {

    private ArrayList<ItemStack> historicItems = new ArrayList<>();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        for (ItemStack itemStack : event.getDrops()) {
            // Check if the item is something specific
            if (ItemMethods.historyItems.contains(itemStack.getType())) {
                historicItems.add(itemStack);
            }
        }
    }

    @EventHandler
    public void playerPickupItem (EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            if (historicItems.contains(e.getItem().getItemStack())) {
                ItemStack item = e.getItem().getItemStack();
                LocalDate currentDate = LocalDate.now();
                ItemMethods.addLore(item, ChatColor.GRAY + "⇄ - " + ((Player) e.getEntity()).getDisplayName() + ChatColor.DARK_GRAY + " (" + C.dtf.format(currentDate) + ")");
            }
        }
    }

    @EventHandler
    public void onItemDeath (EntityDeathEvent e) {
        if (e.getEntity() instanceof Item) {
            if (historicItems.contains(((Item) e.getEntity()).getItemStack())) {
                historicItems.remove(((Item) e.getEntity()).getItemStack());
            }
        }
    }
}
