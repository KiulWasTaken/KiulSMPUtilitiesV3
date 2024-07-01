package kiul.kiulsmputilitiesv3.combattag;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.scheduler.BukkitRunnable;

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
        if (e.getDamager() instanceof Player p1 && e.getEntity() instanceof Player p2) {
            if (C.getPlayerTeam(p1) != C.getPlayerTeam(p2)) {

                boolean p1inFight = C.fightManager.playerIsInFight(p1);
                boolean p2inFight = C.fightManager.playerIsInFight(p2);
                FightObject p1FightObject = C.fightManager.findFightForMember(p1);
                FightObject p2FightObject = C.fightManager.findFightForMember(p2);
                if (p1FightObject != null && p1FightObject.getParticipants().contains(p2.getUniqueId())) {
                    p1FightObject.getDamageDealt().put(p1.getUniqueId(),p1FightObject.getDamageDealt().get(p1.getUniqueId())+e.getFinalDamage());
                    p1FightObject.getDamageTaken().put(p2.getUniqueId(),p1FightObject.getDamageTaken().get(p2.getUniqueId())+e.getFinalDamage());
                    return;
                }
                if (p2FightObject != null && p2FightObject.getParticipants().contains(p1.getUniqueId())) {
                    p2FightObject.getDamageDealt().put(p1.getUniqueId(),p1FightObject.getDamageDealt().get(p1.getUniqueId())+e.getFinalDamage());
                    p2FightObject.getDamageTaken().put(p2.getUniqueId(),p1FightObject.getDamageTaken().get(p2.getUniqueId())+e.getFinalDamage());
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
                                p1FightObject.getDamageDealt().put(p2FightUUIDs,p1FightObject.getDamageDealt().get(p2FightUUIDs));
                                p1FightObject.getDamageTaken().put(p2FightUUIDs,p1FightObject.getDamageTaken().get(p2FightUUIDs));
                                p1FightObject.getJoinTimestamp().put(p2FightUUIDs,p1FightObject.getJoinTimestamp().get(p2FightUUIDs));
                                p1FightObject.getLeaveTimestamp().put(p2FightUUIDs,p1FightObject.getLeaveTimestamp().get(p2FightUUIDs));
                                p1FightObject.getDieTimestamp().put(p2FightUUIDs,p1FightObject.getDieTimestamp().get(p2FightUUIDs));
                            }
                            C.fightManager.disbandFight(p2FightObject);
                        } else {
                            for (UUID p1FightUUIDs : p1FightObject.getParticipants()) {
                                p2FightObject.addParticipant(Bukkit.getPlayer(p1FightUUIDs));
                                FightMethods.startDistanceCheck(Bukkit.getPlayer(p1FightUUIDs), p2FightObject);
                                p2FightObject.getDamageDealt().put(p1FightUUIDs,p1FightObject.getDamageDealt().get(p1FightUUIDs));
                                p2FightObject.getDamageTaken().put(p1FightUUIDs,p1FightObject.getDamageTaken().get(p1FightUUIDs));
                                p2FightObject.getJoinTimestamp().put(p1FightUUIDs,p1FightObject.getJoinTimestamp().get(p1FightUUIDs));
                                p2FightObject.getLeaveTimestamp().put(p1FightUUIDs,p1FightObject.getLeaveTimestamp().get(p1FightUUIDs));
                                p2FightObject.getDieTimestamp().put(p1FightUUIDs,p1FightObject.getDieTimestamp().get(p1FightUUIDs));
                            }
                            C.fightManager.disbandFight(p1FightObject);
                        }
                    }
            }
        }
    }

    @EventHandler
    public void removeFromFightOnDeath (PlayerDeathEvent e) {
        Player p = e.getEntity();
        FightObject fightObject = C.fightManager.findFightForMember(p);
        if (fightObject != null) {
            fightObject.removeParticipant(p,true);
        }
    }

    /**
     *
     *  COOLDOWNS
     *
     **/

    @EventHandler
    public void pearlSlowDown (ProjectileLaunchEvent e) {
        if (e.getEntity() instanceof EnderPearl && e.getEntity().getShooter() instanceof Player p) {
            Bukkit.broadcastMessage("event called");
            if (C.fightManager.playerIsInFight(p)) {
                Bukkit.broadcastMessage("playerIsInFight");
                if (lastPearlThrow.get(p.getUniqueId()) == null) {
                    lastPearlThrow.put(p.getUniqueId(), System.currentTimeMillis());
                    fastPearlThrow.put(p.getUniqueId(), 0);
                }

                    if ((System.currentTimeMillis() - lastPearlThrow.get(p.getUniqueId())) < 3000) {
                        fastPearlThrow.put(p.getUniqueId(), fastPearlThrow.get(p.getUniqueId()) + 1);
                        Bukkit.broadcastMessage(fastPearlThrow.get(p.getUniqueId()) + "");

                        new BukkitRunnable() {
                            int cooldown = (int)(2*(C.fightManager.findFightForMember(p).getDuration() / 1000 / 60 / 1)) * fastPearlThrow.get(p.getUniqueId());

                            @Override
                            public void run() {
                                if (cooldown > 60) {
                                    cooldown = 60;
                                }
                                p.setCooldown(Material.ENDER_PEARL,(20 + cooldown));
                            }
                        }.runTaskLater(C.plugin,0);
                    } else {
                        lastPearlThrow.put(p.getUniqueId(), null);
                        fastPearlThrow.put(p.getUniqueId(), 0);
                    }
                lastPearlThrow.put(p.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void useTridentCoolDown (PlayerRiptideEvent e) {
        Player p = e.getPlayer();
        if (C.fightManager.playerIsInFight(p)) {
            FightObject fightObject = C.fightManager.findFightForMember(p);
            p.setCooldown(Material.TRIDENT,300+(int)((10 * (fightObject.getDuration() / 1000 / 60))));
        }
    }

    @EventHandler
    public void damagedTridentCoolDown (EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player damaged) {
            if (C.fightManager.playerIsInFight(damaged)) {
                int cooldown = damaged.getCooldown(Material.TRIDENT)+100;
                if (cooldown > 300) {
                    cooldown = 300;
                }
                damaged.setCooldown(Material.TRIDENT,cooldown);
            }
        }
    }

    @EventHandler
    public void damagedRocketCoolDown (EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player damaged) {
            if (C.fightManager.playerIsInFight(damaged)) {
                if (damaged.getInventory().getChestplate() != null) {
                    if (damaged.getInventory().contains(Material.ELYTRA) || damaged.getInventory().getChestplate().getType() == Material.ELYTRA) {
                        int cooldown = damaged.getCooldown(Material.FIREWORK_ROCKET) + 200;
                        if (cooldown > 1200) {
                            cooldown = 1200;
                        }
                        damaged.setCooldown(Material.FIREWORK_ROCKET, cooldown);
                    }
                }
            }
        }
    }
}
