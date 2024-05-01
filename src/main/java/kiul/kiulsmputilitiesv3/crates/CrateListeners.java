package kiul.kiulsmputilitiesv3.crates;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.ArrayList;

import static kiul.kiulsmputilitiesv3.crates.CrateMethods.*;

public class CrateListeners implements Listener {


    @EventHandler
    public void crateInventoryClick (InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase("crate")) {
            if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                CrateMethods.playersWhoGotLoot.add(p.getDisplayName());
            }
        }
    }

    @EventHandler
    public void OpenCrate (PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof ArmorStand crate) {
            if (!CrateMethods.unlocking.contains(crate) && !CrateMethods.locked.contains(crate) || crateInventoryMap.get(crate) != null) {
                e.getPlayer().openInventory(crateInventoryMap.get(crate));
            } else if (CrateMethods.locked.contains(crate)) {
                CrateMethods.locked.remove(crate);
                int unlockMinutes = 1;
                long unlockTime = System.currentTimeMillis()+(unlockMinutes*60*1000);
                crateUnlockTime.put(crate,unlockTime);
                CrateMethods.unlocking.add(crate);
            }
        }
    }

    @EventHandler
    public void privateItemPickup (EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (privateItemStacks.get(p) == null) {
                for (ArrayList<Item> privateItemLists : privateItemStacks.values()) {
                    if (privateItemLists.contains(e.getItem())) {
                        e.setCancelled(true);
                        return;
                    }
                }
            } else {
                if (privateItemStacks.get(p).contains(e.getItem())) {
                    e.getEntity().sendMessage(privateItemStacks.get(p).toString());
                    if (!privateItemStacks.get(p).contains(e.getItem())) {
                        e.setCancelled(true);
                    } else {
                        privateItemStacks.get(p).remove(e.getItem());
                        if (privateItemStacks.get(p).isEmpty()) {
                            privateItemStacks.remove(p);
                        }
                    }
                }
            }
        }
    }
}
