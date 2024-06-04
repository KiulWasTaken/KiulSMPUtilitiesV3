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
import java.util.HashMap;
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
                Fight p1Fight = C.fightManager.findFightForMember(p1);
                Fight p2Fight = C.fightManager.findFightForMember(p2);
                if (p1Fight != null && p1Fight.getParticipants().contains(p2.getUniqueId())) {return;}
                if (p2Fight != null && p2Fight.getParticipants().contains(p1.getUniqueId())) {return;}

                    if (!p1inFight && !p2inFight) {
                        ArrayList<UUID> participants = new ArrayList<>();
                        participants.add(p1.getUniqueId());
                        participants.add(p2.getUniqueId());
                        Fight newFight = C.fightManager.createFight(participants);
                        FightMethods.startDistanceCheck(p1, newFight);
                        FightMethods.startDistanceCheck(p2, newFight);
                        return;
                    }

                    if (p1inFight ^ p2inFight) {
                        if (p1inFight) {
                            p1Fight.addParticipant(p2);
                            FightMethods.startDistanceCheck(p2, p1Fight);
                            return;
                        }
                        if (p2inFight) {
                            p2Fight.addParticipant(p1);
                            FightMethods.startDistanceCheck(p1, p2Fight);
                            return;
                        }
                    }

                    if (p1inFight && p2inFight) {
                        if (p1Fight.getDuration() > p2Fight.getDuration()) {
                            for (UUID p2FightUUIDs : p2Fight.getParticipants()) {
                                p1Fight.addParticipant(Bukkit.getPlayer(p2FightUUIDs));
                                FightMethods.startDistanceCheck(Bukkit.getPlayer(p2FightUUIDs), p1Fight);
                            }
                            C.fightManager.disbandFight(p2Fight);
                        } else {
                            for (UUID p1FightUUIDs : p1Fight.getParticipants()) {
                                p2Fight.addParticipant(Bukkit.getPlayer(p1FightUUIDs));
                                FightMethods.startDistanceCheck(Bukkit.getPlayer(p1FightUUIDs), p2Fight);
                            }
                            C.fightManager.disbandFight(p1Fight);
                        }
                    }
            }
        }
    }

    @EventHandler
    public void removeFromFightOnDeath (PlayerDeathEvent e) {
        Player p = e.getEntity();
        Fight fight = C.fightManager.findFightForMember(p);
        if (fight != null) {
            fight.removeParticipant(p);
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
            Fight fight = C.fightManager.findFightForMember(p);
            p.setCooldown(Material.TRIDENT,300+(int)((10 * (fight.getDuration() / 1000 / 60))));
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
