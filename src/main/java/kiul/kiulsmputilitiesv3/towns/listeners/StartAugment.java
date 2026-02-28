package kiul.kiulsmputilitiesv3.towns.listeners;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.towns.AugmentEvent;
import kiul.kiulsmputilitiesv3.towns.Town;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class StartAugment implements Listener {

    @EventHandler
    public void startAugmentEvent (PlayerDropItemEvent e) {
        boolean isInsideProtectedZone = false;
        Town town = null;
        for (Town allTowns : Town.townsList) {
            if (allTowns.protectedAreaContains(e.getItemDrop().getLocation())) {
                isInsideProtectedZone = true;
                town = allTowns;
                break;
            }
        }
        Item itemEntity = e.getItemDrop();
        if (town == null) {return;}

        Town finalTown = town;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (itemEntity == null || itemEntity.isDead()) {
                    cancel();
                    return;
                }
                if (finalTown.getTownCenter().distance(e.getItemDrop().getLocation()) < 5 && itemEntity.getLocation().getBlock().getType() == Material.RESPAWN_ANCHOR || itemEntity.getLocation().add(0,-1,0).getBlock().getType() == Material.RESPAWN_ANCHOR) {
                    if (finalTown.getSelectedAugment() == null) {
                        finalTown.getTownChargeStand().setCustomName(C.RED+"Select Augment");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!finalTown.isAugmenting()) {
                                    finalTown.updateTownCharge();
                                }
                            }
                        }.runTaskLater(C.plugin,80);
                        cancel();
                        return;
                    }
                    if (finalTown.getTownHealth()/finalTown.getTownMaxHealth() < 0.9) {
                        if (finalTown.getSelectedAugment() == null) {
                            finalTown.getTownChargeStand().setCustomName(C.RED+"Regen to >90% first!");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!finalTown.isAugmenting()) {
                                        finalTown.updateTownCharge();
                                    }
                                }
                            }.runTaskLater(C.plugin,80);
                        }
                        cancel();
                        return;
                    }
                    if (e.getItemDrop().getItemStack().getType().equals(finalTown.getSelectedAugment().getRequiredType())) {
                        AugmentEvent augmentEvent = new AugmentEvent(finalTown, itemEntity, finalTown.getSelectedAugment());
                        augmentEvent.start();
                        cancel();
                    }
                }
            }
        }.runTaskTimer(C.plugin,0,20);


    }
}
