package kiul.kiulsmputilitiesv3.combattag;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

import static kiul.kiulsmputilitiesv3.combattag.FightMethods.fastPearlThrow;
import static kiul.kiulsmputilitiesv3.combattag.FightMethods.lastPearlThrow;

public class FightLogicListeners implements Listener {

    /**
     *
     * FIGHT BEHAVIOUR
     *
     **/
    @EventHandler
    public void startFight(EntityDamageByEntityEvent e) {
        if (!C.combatTagEnabled) {return;}
        if (e.getEntity() instanceof Player) {
            Player p1 = null;
            if (e.getDamager() instanceof ExplosiveMinecart cart) {
                if (cart.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamageCause) {
                    if ((lastDamageCause.getDamager().getType() == EntityType.ARROW || lastDamageCause.getDamager().getType() == EntityType.SPECTRAL_ARROW || lastDamageCause.getDamager().getType() == EntityType.PLAYER)) {
                        Player damager = null;
                        if ((lastDamageCause.getDamager() instanceof Projectile arrow)) {
                            damager = (Player) arrow.getShooter();
                        }
                        if (lastDamageCause.getDamager() instanceof Player) {
                            damager = (Player) lastDamageCause.getDamager();
                        }
                        p1 = damager;
                    }
                }
            }
            if (e.getDamager() instanceof AbstractArrow arrow) {
                if (arrow.getShooter() instanceof Player) {
                    p1 = (Player) arrow.getShooter();
                }
            }
            if (e.getDamager() instanceof Player) {
                p1 = (Player) e.getDamager();
            }

            Player p2 = (Player) e.getEntity();
            if (p1 == null) {
                return;
            }
            if (C.getPlayerTeam(p1) != null && C.getPlayerTeam(p2) != null) {
                if ((C.getPlayerTeam(p1).getName() == C.getPlayerTeam(p2).getName())) {
                    return;
                }
            }
            boolean p1inFight = C.fightManager.playerIsInFight(p1);
            boolean p2inFight = C.fightManager.playerIsInFight(p2);
            FightObject p1FightObject = C.fightManager.findFightForMember(p1);
            FightObject p2FightObject = C.fightManager.findFightForMember(p2);
            if (p1FightObject != null && p1FightObject.getParticipants().contains(p2.getUniqueId())) {
                p1FightObject.getDamageDealt().put(p1.getUniqueId().toString(), p1FightObject.getDamageDealt().get(p1.getUniqueId().toString()) + e.getFinalDamage());
                p1FightObject.getDamageTaken().put(p2.getUniqueId().toString(), p1FightObject.getDamageTaken().get(p2.getUniqueId().toString()) + e.getFinalDamage());
                p1FightObject.getHits().put(p1.getUniqueId().toString(), p1FightObject.getHits().get(p1.getUniqueId().toString()) + 1);
                return;
            }
            if (p2FightObject != null && p2FightObject.getParticipants().contains(p1.getUniqueId())) {
                p2FightObject.getDamageDealt().put(p1.getUniqueId().toString(), p1FightObject.getDamageDealt().get(p1.getUniqueId().toString()) + e.getFinalDamage());
                p2FightObject.getDamageTaken().put(p2.getUniqueId().toString(), p1FightObject.getDamageTaken().get(p2.getUniqueId().toString()) + e.getFinalDamage());
                p2FightObject.getHits().put(p1.getUniqueId().toString(), p2FightObject.getHits().get(p1.getUniqueId().toString()) + 1);
                return;
            }

            if (!p1inFight && !p2inFight) {
                ArrayList<UUID> participants = new ArrayList<>();
                participants.add(p1.getUniqueId());
                participants.add(p2.getUniqueId());
                FightObject newFightObject = C.fightManager.createFight(participants);
                FightMethods.startDistanceCheck(p1, newFightObject);
                FightMethods.startDistanceCheck(p2, newFightObject);
                return;
            }

            if (p1inFight ^ p2inFight) {
                if (p1inFight) {
                    p1FightObject.addParticipant(p2);
                    FightMethods.startDistanceCheck(p2, p1FightObject);
                    return;
                }
                if (p2inFight) {
                    p2FightObject.addParticipant(p1);
                    FightMethods.startDistanceCheck(p1, p2FightObject);
                    return;
                }
            }

            if (p1inFight && p2inFight) {
                if (p1FightObject.getDuration() > p2FightObject.getDuration()) {
                    for (UUID p2FightUUIDs : p2FightObject.getParticipants()) {
                        p1FightObject.addParticipant(Bukkit.getPlayer(p2FightUUIDs));
                        FightMethods.startDistanceCheck(Bukkit.getPlayer(p2FightUUIDs), p1FightObject);
                        p1FightObject.getDamageDealt().put(p2FightUUIDs.toString(), p1FightObject.getDamageDealt().get(p2FightUUIDs.toString()));
                        p1FightObject.getDamageTaken().put(p2FightUUIDs.toString(), p1FightObject.getDamageTaken().get(p2FightUUIDs.toString()));
                        p1FightObject.getJoinTimestamp().put(p2FightUUIDs.toString(), p1FightObject.getJoinTimestamp().get(p2FightUUIDs.toString()));
                        p1FightObject.getLeaveTimestamp().put(p2FightUUIDs.toString(), p1FightObject.getLeaveTimestamp().get(p2FightUUIDs.toString()));
                        p1FightObject.getDieTimestamp().put(p2FightUUIDs.toString(), p1FightObject.getDieTimestamp().get(p2FightUUIDs.toString()));
                    }
                    C.fightManager.disbandFight(p2FightObject);
                } else {
                    for (UUID p1FightUUIDs : p1FightObject.getParticipants()) {
                        p2FightObject.addParticipant(Bukkit.getPlayer(p1FightUUIDs));
                        FightMethods.startDistanceCheck(Bukkit.getPlayer(p1FightUUIDs), p2FightObject);
                        p2FightObject.getDamageDealt().put(p1FightUUIDs.toString(), p1FightObject.getDamageDealt().get(p1FightUUIDs.toString()));
                        p2FightObject.getDamageTaken().put(p1FightUUIDs.toString(), p1FightObject.getDamageTaken().get(p1FightUUIDs.toString()));
                        p2FightObject.getJoinTimestamp().put(p1FightUUIDs.toString(), p1FightObject.getJoinTimestamp().get(p1FightUUIDs.toString()));
                        p2FightObject.getLeaveTimestamp().put(p1FightUUIDs.toString(), p1FightObject.getLeaveTimestamp().get(p1FightUUIDs.toString()));
                        p2FightObject.getDieTimestamp().put(p1FightUUIDs.toString(), p1FightObject.getDieTimestamp().get(p1FightUUIDs.toString()));
                    }
                    C.fightManager.disbandFight(p1FightObject);
                }
            }
        }
    }


    @EventHandler
    public void removeFromFightOnDeath (PlayerDeathEvent e) {
        if (!C.combatTagEnabled) {return;}
        Player p = e.getEntity();
        FightObject fightObject = C.fightManager.findFightForMember(p);
        if (fightObject != null) {
            if (p.getKiller() != null) {
                fightObject.getKiller().put(p.getUniqueId().toString(), p.getKiller().getUniqueId().toString());
            }
            fightObject.removeParticipant(p.getUniqueId(),true);
        }
    }

    @EventHandler
    public void addBackOnJoin (PlayerJoinEvent e) {
        FightObject fight = C.fightManager.findFightForMember(e.getPlayer());
        if (fight != null && fight.getOfflineParticipants().contains(e.getPlayer().getUniqueId())) {
            fight.getParticipants().add(e.getPlayer().getUniqueId());
            fight.getOfflineParticipants().remove(e.getPlayer().getUniqueId());
            FightMethods.startDistanceCheck(e.getPlayer(),fight);
        }
    }

    /**
     *
     *  COOLDOWNS
     *
     **/

//    @EventHandler
//    public void pearlSlowDown (ProjectileLaunchEvent e) {
//        if (!C.combatTagEnabled) {return;}
//        if (e.getEntity() instanceof EnderPearl && e.getEntity().getShooter() instanceof Player p) {
//            if (C.fightManager.playerIsInFight(p)) {
//                if (lastPearlThrow.get(p.getUniqueId()) == null) {
//                    lastPearlThrow.put(p.getUniqueId(), System.currentTimeMillis());
//                    fastPearlThrow.put(p.getUniqueId(), 0);
//                }
//
//                    if ((System.currentTimeMillis() - lastPearlThrow.get(p.getUniqueId())) < 6000) {
//                        fastPearlThrow.put(p.getUniqueId(), fastPearlThrow.get(p.getUniqueId()) + 1);
//
//                        new BukkitRunnable() {
//                            int cooldown = 10*fastPearlThrow.get(p.getUniqueId());
//
//                            @Override
//                            public void run() {
//                                if (cooldown > 120) {
//                                    cooldown = 120;
//                                }
//                                p.setCooldown(Material.ENDER_PEARL,(20 + cooldown));
//                            }
//                        }.runTaskLater(C.plugin,0);
//                    } else {
//                        lastPearlThrow.put(p.getUniqueId(), null);
//                        fastPearlThrow.put(p.getUniqueId(), 0);
//                    }
//                lastPearlThrow.put(p.getUniqueId(), System.currentTimeMillis());
//            }
//        }
//    }

    @EventHandler
    public void useTridentCoolDown (PlayerRiptideEvent e) {
        if (!C.combatTagEnabled) {return;}
        Player p = e.getPlayer();
        if (C.fightManager.playerIsInFight(p)) {
            p.setCooldown(Material.TRIDENT,600);
            if (p.getInventory().contains(Material.ELYTRA) || p.getInventory().getChestplate().getType() == Material.ELYTRA) {
                p.setCooldown(Material.TRIDENT, 900);
            }
        }
    }
    @EventHandler
    public void useWindChargeCoolDown (ProjectileLaunchEvent e) {
        if (!C.combatTagEnabled) {return;}
        if (e.getEntity() instanceof WindCharge && e.getEntity().getShooter() instanceof Player p) {
                if (C.fightManager.playerIsInFight(p)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.setCooldown(Material.WIND_CHARGE, 300);
                        }
                    }.runTaskLater(C.plugin, 0);

                }
            }
        }

    @EventHandler
    public void damagedTridentCoolDown (EntityDamageByEntityEvent e) {
        if (!C.combatTagEnabled) {return;}
        if (e.getEntity() instanceof Player) {
            Player p1 = null;
            if (e.getDamager() instanceof ExplosiveMinecart cart) {
                if (cart.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamageCause) {
                    if ((lastDamageCause.getDamager().getType() == EntityType.ARROW || lastDamageCause.getDamager().getType() == EntityType.SPECTRAL_ARROW || lastDamageCause.getDamager().getType() == EntityType.PLAYER)) {
                        Player damager = null;
                        if ((lastDamageCause.getDamager() instanceof Projectile arrow)) {
                            damager = (Player) arrow.getShooter();
                        }
                        if (lastDamageCause.getDamager() instanceof Player) {
                            damager = (Player) lastDamageCause.getDamager();
                        }
                        p1 = damager;
                    }
                }
            }
            if (e.getDamager() instanceof AbstractArrow arrow) {
                if (arrow.getShooter() instanceof Player) {
                    p1 = (Player) arrow.getShooter();
                }
            }
            if (e.getDamager() instanceof Player) {
                p1 = (Player) e.getDamager();
            }

            Player p2 = (Player) e.getEntity();
            if (C.getPlayerTeam(p1) != null && C.getPlayerTeam(p2) != null) {
                if ((C.getPlayerTeam(p1).getName() == C.getPlayerTeam(p2).getName())) {
                    return;
                }
            }
            if (p1 != null) {
                int p2cooldown = p2.getCooldown(Material.TRIDENT) + 100;
                if (p2cooldown > 300) {
                    p2cooldown = 300;
                }
                p2.setCooldown(Material.TRIDENT, p2cooldown);


                    int p1cooldown = p1.getCooldown(Material.TRIDENT) + 100;
                    if (p1cooldown > 300) {
                        p1cooldown = 300;
                    }
                    p1.setCooldown(Material.TRIDENT, p1cooldown);
                }

        }
    }
    ArrayList<Player> glidingPlayers = new ArrayList<>();
    public void updateElytraHitBox (Player p) {

        new BukkitRunnable() {

            @Override
            public void run() {
               for (Entity nearbyEntities : p.getNearbyEntities(2,2,2)) {
                   if (nearbyEntities instanceof Arrow || nearbyEntities instanceof SpectralArrow) {
                       if (((AbstractArrow) nearbyEntities).getShooter() instanceof Player attacker && attacker != p) {
                           ((AbstractArrow) nearbyEntities).hitEntity(p);
                       }

                   }
               }
                if (!p.isGliding()) {
                    p.setCollidable(true);
                    glidingPlayers.remove(p);
                    cancel();
                }
            }
        }.runTaskTimer(C.plugin,0,1);
    }
    @EventHandler
    public void elytraGlidingCheck (PlayerMoveEvent e) {
        if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
            if (!e.getPlayer().isGliding()) {return;}
            if (!glidingPlayers.contains(e.getPlayer())) {
                glidingPlayers.add(e.getPlayer());
                updateElytraHitBox(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void preventStartGlidingOnCooldown (EntityToggleGlideEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (p.getCooldown(Material.ELYTRA) > 0) {
                p.setGliding(false);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void elytraMidAirDisable (EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player p) {
            Player damager = null;
            if (e.getDamager() instanceof AbstractArrow arrow) {
                if (arrow.getShooter() instanceof Player) {
                    damager = (Player) arrow.getShooter();
                }
            }
            if (e.getDamager() instanceof Player) {
                damager = (Player) e.getDamager();
            }
            if (damager != null) {
                if (p.isGliding()) {
                    p.setGliding(false);

                    p.setCooldown(Material.ELYTRA, 600);
                }
            }
        }
    }

    @EventHandler
    public void damagedRocketCoolDown (EntityDamageByEntityEvent e) {
        if (!C.combatTagEnabled) {return;}
        if (e.getDamager() instanceof Player damager && e.getEntity() instanceof Player damaged) {
            if (C.fightManager.playerIsInFight(damaged)) {
                if (damaged.getInventory().getChestplate() != null) {
                    if (damaged.getInventory().contains(Material.ELYTRA) || damaged.getInventory().getChestplate().getType() == Material.ELYTRA) {
                        int cooldown = damaged.getCooldown(Material.FIREWORK_ROCKET) + 400;
                        if (cooldown > 1200) {
                            cooldown = 1200;
                        }
                        damaged.setCooldown(Material.FIREWORK_ROCKET, cooldown);
                    }
                }
            }
            if (C.fightManager.playerIsInFight(damager)) {
                if (damager.getInventory().getChestplate() != null) {
                    if (damager.getInventory().contains(Material.ELYTRA) || damager.getInventory().getChestplate().getType() == Material.ELYTRA) {
                        int cooldown = damaged.getCooldown(Material.FIREWORK_ROCKET) + 400;
                        if (cooldown > 1200) {
                            cooldown = 1200;
                        }
                        damager.setCooldown(Material.FIREWORK_ROCKET, cooldown);
                    }
                }
            }
        }
    }
}
