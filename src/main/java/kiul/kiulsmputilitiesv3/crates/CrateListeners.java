package kiul.kiulsmputilitiesv3.crates;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static kiul.kiulsmputilitiesv3.crates.CrateMethods.*;

public class CrateListeners implements Listener {


    @EventHandler
    public void crateInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase("crate")) {
            if (e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                CrateMethods.playersWhoGotLoot.add(p.getDisplayName());
            }
        }
    }

    @EventHandler
    public void damageInCrateFight(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player p) {
            for (Location crateLocation : activeCratesLocation.keySet()) {
                Vector cornerA = crateLocation.clone().add(10,10,10).toVector();
                Vector cornerB = crateLocation.clone().add(-10,-10,-10).toVector();
                Vector squareCornerA = Vector.getMinimum(cornerA, cornerB);
                Vector squareCornerB = Vector.getMaximum(cornerA, cornerB);
                if (p.getLocation().toVector().isInAABB(squareCornerA,squareCornerB)) {
                    activeCratesLocation.put(crateLocation,activeCratesLocation.get(crateLocation)+e.getFinalDamage());
                }
            }
        }
    }

    @EventHandler
    public void OpenCrate(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        p.sendMessage("1");
        if (e.getRightClicked() instanceof ArmorStand crate) {
            p.sendMessage("2");
            p.sendMessage("unlocking.contains(crate): " + CrateMethods.unlocking.contains(crate));
            p.sendMessage("locked.contains(crate):" + CrateMethods.locked.contains(crate));
            if (crateInventoryMap.get(crate) != null) {
                p.sendMessage("crateInventoryMap.get(crate): " + crateInventoryMap.get(crate).toString());
            } else {
                p.sendMessage("crateInventoryMap.get(crate): null");
            }
            if (crateUnlockTime.get(crate) != null && System.currentTimeMillis() >= crateUnlockTime.get(crate) && crateInventoryMap.get(crate) != null) {
                e.getPlayer().openInventory(crateInventoryMap.get(crate));
            } else if (CrateMethods.locked.contains(crate)) {
                CrateMethods.locked.remove(crate);
                int unlockMinutes = 1;
                long unlockTime = System.currentTimeMillis() + (unlockMinutes * 60 * 1000);
                crateUnlockTime.put(crate, unlockTime);
                CrateMethods.unlocking.add(crate);
            }
        }
    }

    @EventHandler
    public void preventFireWorkDamage (EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework fw && fw.hasMetadata("pat")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void privateItemPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player p) {

            for (ArrayList<Item> privateItemLists : privateItemStacks.values()) {
                if (privateItemLists.contains(e.getItem())) {
                    if (privateItemStacks.get(p) == null) {
                        e.setCancelled(true);
                        return;
                    }
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
