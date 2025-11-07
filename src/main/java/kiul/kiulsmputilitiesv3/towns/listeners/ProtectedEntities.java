package kiul.kiulsmputilitiesv3.towns.listeners;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.InventoryToBase64;
import kiul.kiulsmputilitiesv3.towns.Town;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ProtectedEntities implements Listener {

    static ArrayList<EntityType> protectedEntityTypes = new ArrayList<>() {{
        // passive mobs
        add(EntityType.VILLAGER);
       add(EntityType.ZOMBIE_VILLAGER);
       add(EntityType.COW);
       add(EntityType.SHEEP);
       add(EntityType.PIG);
       add(EntityType.TURTLE);
       add(EntityType.WOLF);
       add(EntityType.FOX);
       add(EntityType.BEE);
       add(EntityType.HORSE);
       add(EntityType.SKELETON_HORSE);
       add(EntityType.MULE);
       add(EntityType.ALLAY);
       add(EntityType.ARMADILLO);
       add(EntityType.AXOLOTL);
       add(EntityType.CHICKEN);
       add(EntityType.DONKEY);
       add(EntityType.HAPPY_GHAST);
       add(EntityType.LEASH_KNOT);
       add(EntityType.MOOSHROOM);
       // placeables
        add(EntityType.MINECART);
        add(EntityType.HOPPER_MINECART);
        add(EntityType.CHEST_MINECART);
        add(EntityType.FURNACE_MINECART);
       add(EntityType.ARMOR_STAND);
       add(EntityType.ITEM_FRAME);
       add(EntityType.GLOW_ITEM_FRAME);

    }};
    @EventHandler
    public void hurtEntityInsideTown (EntityDamageByEntityEvent e) {
        if (!protectedEntityTypes.contains(e.getEntity().getType())) return;
        if (e.getDamager() instanceof Player p) {
            boolean isInsideProtectedZone = false;
            boolean isOnOwningTeam = false;

            Town town = null;
            for (Town allTowns : Town.townsList) {
                if (allTowns.protectedAreaContains(e.getEntity().getLocation())) {
                    town = allTowns;
                    isInsideProtectedZone = true;
                    if (allTowns.getOwningTeam() != null) {
                        isOnOwningTeam = allTowns.getOwningTeam().equals(C.getPlayerTeam(p));
                    }
                    break;
                }
            }
            if (!isOnOwningTeam && isInsideProtectedZone) {
                e.setDamage(0);
                if (e.getEntity() instanceof LivingEntity livingEntity) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            livingEntity.setFireTicks(0);
                        }
                    }.runTaskLater(C.plugin,1);

                }
            }
        }

    }
    public static Set<Entity> disabledEntities = new HashSet<>();

    public static void disableNearbyEntities (Block brokenBlock) { // when blocks are broken near entities inside a town during a raid, we don't want the entities to escape.
        for (Entity nearbyEntity : brokenBlock.getLocation().getNearbyEntities(4,4,4)) {
            if (protectedEntityTypes.contains(nearbyEntity.getType())) {
                if (nearbyEntity instanceof LivingEntity livingEntity) {
                    livingEntity.setAI(false);
                }
                nearbyEntity.setNoPhysics(true);
                nearbyEntity.setGravity(false);
                disabledEntities.add(nearbyEntity);
                checkIfEntityCanBeReactivated(nearbyEntity);
            }
        }
    }

    public static void checkIfEntityCanBeReactivated (Entity disabledEntity) {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block regeneratingBlocks : ProtectedBlocks.regeneratingBlocks) {
                    if (regeneratingBlocks.getLocation().distance(disabledEntity.getLocation()) < 3) {
                        return;
                    }
                }

                if (disabledEntity instanceof LivingEntity livingEntity) {
                    livingEntity.setAI(true);
                }
                disabledEntity.setNoPhysics(false);
                disabledEntity.setGravity(true);
                disabledEntities.remove(disabledEntity);
                cancel();

            }
        }.runTaskTimer(C.plugin,20,100);
    }
}
