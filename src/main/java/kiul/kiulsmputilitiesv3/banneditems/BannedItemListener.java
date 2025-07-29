package kiul.kiulsmputilitiesv3.banneditems;

import kiul.kiulsmputilitiesv3.config.ConfigData;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.SmithItemEvent;

import java.util.ArrayList;

public class BannedItemListener implements Listener {

    public static ArrayList<Material> bannedItems = new ArrayList<>() {{
       add(Material.MACE);
       add(Material.SHIELD);
       add(Material.ELYTRA);
       add(Material.NETHERITE_HELMET);
       add(Material.NETHERITE_CHESTPLATE);
       add(Material.NETHERITE_LEGGINGS);
       add(Material.NETHERITE_BOOTS);
       add(Material.TNT_MINECART);
    }};

    @EventHandler
    public void pickupBannedItem (EntityPickupItemEvent e) {
        if (!ConfigData.get().getBoolean("ban_item")) {return;}
        if (bannedItems.contains(e.getItem().getItemStack().getType())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void clickBannedItem (InventoryClickEvent e) {
        if (!ConfigData.get().getBoolean("ban_item")) {return;}
        if (e.getCurrentItem() == null) return;
        if (bannedItems.contains(e.getCurrentItem().getType())) {
            e.getCurrentItem().setAmount(0);
            if (bannedItems.contains(e.getCursor().getType())) {
                e.getCursor().setAmount(0);
            }
        }
    }

    @EventHandler
    public void craftBannedItem (CraftItemEvent e) {
        if (!ConfigData.get().getBoolean("ban_item")) {return;}
        if (bannedItems.contains(e.getRecipe().getResult().getType())) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void smithBannedItem (SmithItemEvent e) {
        if (!ConfigData.get().getBoolean("ban_item")) {return;}
        if (bannedItems.contains(e.getInventory().getRecipe().getResult().getType())) {
            e.setCancelled(true);
        }
    }
}
