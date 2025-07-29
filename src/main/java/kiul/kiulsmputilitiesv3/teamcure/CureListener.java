package kiul.kiulsmputilitiesv3.teamcure;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.ConfigData;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CureListener implements Listener {

    @EventHandler
    public void fastCureDebug(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ZombieVillager zombieVillager)) return;
        Player player = event.getPlayer();

        // Is holding a golden apple (regular, not enchanted)
        if (player.getInventory().getItemInMainHand().getType() != Material.GOLDEN_APPLE) return;

        // Check if the zombie has weakness effect
        if (!zombieVillager.hasPotionEffect(PotionEffectType.WEAKNESS)) return;

        zombieVillager.setConversionTime(100,true);
        zombieVillager.setConversionPlayer(Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId())); // Should now be set
    }

    @EventHandler
    public void detectVillagerCure (EntityTransformEvent e) {
        if (!ConfigData.get().getBoolean("curing")) {return;}
        if (e.getEntity().hasMetadata("pat")) {return;}
        if (e.getTransformReason().equals(EntityTransformEvent.TransformReason.CURED)) {
            if (e.getEntity() instanceof ZombieVillager villager) {
                OfflinePlayer p = villager.getConversionPlayer();
                if (p == null || p.getPlayer() == null) return;
                if (C.getPlayerTeam(p.getPlayer()) != null && C.getPlayerTeam(p.getPlayer()).getEntries().size() > 1) {
                    ArmorStand pending = (ArmorStand) villager.getLocation().getWorld().spawnEntity(villager.getLocation().clone().add(0,0.5,0), EntityType.ARMOR_STAND);
                    pending.setCustomNameVisible(true);
                    pending.setVisible(false);
                    pending.setGravity(false);
                    pending.setCustomName(ChatColor.YELLOW+"Curing");
                    villager.setAI(false);
                    villager.setGravity(false);
                    villager.setSilent(true);
                    BukkitTask runnable = new BukkitRunnable() {
                        int i = 0;
                        int animation = 0;
                        int cure = 0;
                        Location lastLocation = villager.getLocation();
                        List<String> entries = new ArrayList<>(C.getPlayerTeam(p.getPlayer()).getEntries());
                        @Override
                        public void run() {
                            // Dynamically find the entity each tick
                            Entity foundEntity = null;
                            for (Entity entity : lastLocation.getNearbyEntities(0.3, 0.3, 0.3)) {
                                if (entity instanceof Villager || entity instanceof ZombieVillager) {
                                    foundEntity = entity;
                                    entity.setGravity(false);
                                    ((Ageable) entity).setAI(false);
                                    entity.setSilent(true);
                                    break;
                                }
                            }

                            if (foundEntity == null) {
                                System.out.println("No entity found in range.");
                                return;
                            }

                            if (i >= entries.size()) {
                                if (foundEntity instanceof LivingEntity living) {
                                    living.setAI(true);
                                    foundEntity.setGravity(true);
                                    foundEntity.setSilent(false);
                                }
                                pending.remove();
                                cancel();
                                return;
                            }

                            Player teammate = Bukkit.getPlayer(entries.get(i));
                            if (teammate == null) {
                                i++;
                                return;
                            }

                            if (cure == 0) {
                                if (foundEntity instanceof Villager villagerToCure) {
                                    villagerToCure.zombify();
                                    cure++;
                                } else if (foundEntity instanceof ZombieVillager) {
                                    cure++;
                                }
                            } else if (cure == 1) {
                                if (foundEntity instanceof ZombieVillager zombieToCure) {
                                    zombieToCure.setMetadata("pat",new FixedMetadataValue(C.plugin,"rat"));
                                    zombieToCure.setConversionTime(1, false);
                                    zombieToCure.setConversionPlayer(teammate);
                                    // optional: mark entity if you want
                                    cure++;
                                } else if (foundEntity instanceof Villager) {
                                    cure++;
                                }
                            }

                            if (cure > 1) {
                                i++;
                                cure = 0;
                            }

                            // Animate curing name
                            animation++;
                            String dots = ".".repeat(animation);
                            pending.setCustomName(ChatColor.YELLOW + "Curing" + dots);
                            if (animation >= 3) animation = 0;
                        }
                    }.runTaskTimer(C.plugin,3,3);
                }
            }
        }
    }
}
