package kiul.kiulsmputilitiesv3.itemhistory.listeners;


import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.itemhistory.ItemMethods;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
    }};
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        try {
            for (ItemStack itemStack : event.getDrops()) {
                // Check if the item is something specific
                if (historyItems != null && historyItems.contains(itemStack.getType())) {
                    historicItems.add(itemStack);
                }
            }
        } catch(NullPointerException e) {}
    }

    @EventHandler
    public void playerPickupItem (EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            if (historicItems.contains(e.getItem().getItemStack())) {
                ItemStack item = e.getItem().getItemStack();
                LocalDate currentDate = LocalDate.now();
                ItemMethods.addLore(item, ChatColor.GRAY + "⇄ - " + ((Player) e.getEntity()).getDisplayName() + ChatColor.DARK_GRAY + " (" + C.dtf.format(currentDate) + ")");
                historicItems.remove(e.getItem().getItemStack());
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
