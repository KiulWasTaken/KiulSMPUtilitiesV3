package kiul.kiulsmputilitiesv3.combattag;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.stats.StatDB;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FightMethods {

    static HashMap<FightObject,Long> timeAllSneaking = new HashMap<>();
    static long waitingUntil;
    public static void startDistanceCheck (Player p,FightObject fight) {


        new BukkitRunnable() {
            @Override
            public void run() {

                boolean nearby = false;
                if (fight != null && fight.isPartaking(p.getUniqueId())) {
                    for (int i = 0; i < fight.getParticipants().size(); i++) {

                        Player fightParticipant = Bukkit.getPlayer(fight.getParticipants().get(i));
                        UUID fightParticipantUUID = fight.getParticipants().get(i);

                        if (fightParticipant == null ) {
                            fight.getParticipants().remove(fightParticipantUUID);
                            fight.getOfflineParticipants().add(fightParticipantUUID);
                            continue;
                        }
                        if (fightParticipant != p) {
                            if (p.getWorld() != fightParticipant.getWorld()) {
                                switch (p.getWorld().getEnvironment()) {
                                    case NETHER:
                                        switch (fightParticipant.getWorld().getEnvironment()) {
                                            case NORMAL:
                                                Location pLocation = new Location(p.getWorld(),p.getX(),0,p.getZ());
                                                Location fLocation = new Location(p.getWorld(),fightParticipant.getX()/8,0,fightParticipant.getZ()/8);
                                                if (pLocation.distance(fLocation) < 500) {
                                                    nearby = true;
                                                }
                                                break;
                                            case THE_END:
                                                nearby = false;
                                                break;

                                        }
                                        break;
                                    case THE_END:
                                        break;
                                    default:
                                        switch (fightParticipant.getWorld().getEnvironment()) {
                                            case NETHER:
                                                Location pLocation = new Location(p.getWorld(),p.getX()/8,0,p.getZ()/8);
                                                Location fLocation = new Location(p.getWorld(),fightParticipant.getX(),0,fightParticipant.getZ());
                                                if (pLocation.distance(fLocation) < 500) {
                                                    nearby = true;
                                                }
                                                break;
                                            case THE_END:
                                                nearby = false;
                                                break;

                                        }
                                        break;
                                }
                            } else {
                                if (p.getLocation().distance(fightParticipant.getLocation()) < 500) {
                                    nearby = true;
                                }
                            }

                            if (!nearby) {
                                if (fight.getParticipants().size() <= 1 && !fight.getOfflineParticipants().isEmpty()) {
                                        boolean participantsStillActive = true;
                                        for (UUID offlineFightParticipant : fight.getOfflineParticipants()) {
                                            if (System.currentTimeMillis() - FightLogicListeners.relogCooldown.get(offlineFightParticipant) > 90*1000) {
                                                participantsStillActive = false;
                                            }
                                        }
                                        if (participantsStillActive) return;
                                }
                                fight.removeParticipant(p.getUniqueId(),false);
                                cancel();
                                return;
                            }
                        }

                    }





                } else {
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(C.plugin,0,30);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (fight != null && fight.isPartaking(p.getUniqueId()) && fight.getParticipants().size() > 1) {
                    String hex = ColorInterpolator.getColorBasedOnAverageDistance(p, fight.getParticipants().stream()
                            .filter(player -> !player.equals(p.getUniqueId())) // Exclude Player1
                            .toList());
                    p.sendActionBar(MiniMessage.miniMessage().deserialize("<" + hex + ">⚔"));

                    boolean allPlayersSneaking = true;
                    for (UUID uuid : fight.getParticipants()) {
                        Player fightPlayer = Bukkit.getPlayer(uuid);
                        if (fightPlayer != null) {

                            if (!fightPlayer.isSneaking() || fightPlayer.getVelocity().getX() != 0 || fightPlayer.getVelocity().getZ() != 0) {
                                allPlayersSneaking = false;
                                timeAllSneaking.remove(fight);
                            }
                        }
                    }
                    if (allPlayersSneaking) {
                        if (!timeAllSneaking.containsKey(fight)) {
                            timeAllSneaking.put(fight, System.currentTimeMillis());
                            waitingUntil = System.currentTimeMillis() + 3500;
                        } else {
                            p.sendActionBar(MiniMessage.miniMessage().deserialize("<yellow>" + (waitingUntil - System.currentTimeMillis()) / 1000));
                            if (System.currentTimeMillis() - timeAllSneaking.get(fight) > 4000) {
                                p.sendActionBar(MiniMessage.miniMessage().deserialize("<yellow>fight disbanded"));
                                fight.disband();
                                cancel();
                                return;
                            }
                        }
                    }
                } else {
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(C.plugin,0,2);
    }
}
