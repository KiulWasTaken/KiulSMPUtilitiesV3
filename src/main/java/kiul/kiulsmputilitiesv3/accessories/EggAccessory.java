package kiul.kiulsmputilitiesv3.accessories;

import kiul.kiulsmputilitiesv3.InventoryToBase64;
import kiul.kiulsmputilitiesv3.config.AccessoryData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class EggAccessory implements Listener {

    @EventHandler
    public void createAccessory(ItemSpawnEvent e) {
        if (e.getEntity().getItemStack().getType() == Material.DRAGON_EGG) {
            e.getEntity().setItemStack(AccessoryItemEnum.egg.getAccessory());
        }
    }

    @EventHandler
    public void preventExtremeDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (p.getHealth() > 10 && p.getHealth()-e.getFinalDamage() <= 0) {
                if (AccessoryMethods.getActiveAccessoryIdentifier(p).equalsIgnoreCase("egg")) {
                    e.setCancelled(true);
                    p.setHealth(2);
                }
            }
        }
    }
    @EventHandler
    public void shulker(PlayerInteractEvent e) {
        if (e.getItem() == null) {return;}
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (e.getItem().getType().equals(Material.DRAGON_EGG)) {
                e.getPlayer().openInventory(createEggInventory());
                AccessoryData.get().set(e.getPlayer().getUniqueId() + ".accessory.identifier", "egg");
                AccessoryData.get().set(e.getPlayer().getUniqueId() + ".accessory.range", AccessoryItemEnum.egg.getRange());
                AccessoryData.save();
                AccessoryMethods.instantiateTrackingSignalTask(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void updateShulker(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof BlockInventoryHolder) {return;}
        if (e.getView().getTitle().equalsIgnoreCase("Dragon Egg")) {
            AccessoryData.get().set("accessory.inventory", InventoryToBase64.itemStackArrayToBase64(e.getInventory().getContents()));
            AccessoryData.get().set(e.getPlayer().getUniqueId()+".accessory.identifier", null);
            AccessoryData.get().set(e.getPlayer().getUniqueId()+".accessory.range", null);
            AccessoryData.save();
            AccessoryMethods.trackingSignalTask.get(e.getPlayer()).cancel();
        }
    }


    public Inventory createEggInventory () {
       Inventory eggInventory = Bukkit.createInventory(null,27,"Dragon Egg");
       if (AccessoryData.get().get("accessory.inventory") != null) {
           try {
               eggInventory.setContents(InventoryToBase64.itemStackArrayFromBase64(AccessoryData.get().getString("accessory.inventory")));
           } catch (IOException err) {
               err.printStackTrace();
           }
       }
       return eggInventory;
    }
}
