package kiul.kiulsmputilitiesv3.combattag;

import com.ibm.icu.impl.locale.LocaleValidityChecker;
import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.locatorbar.LocatorBar;
import kiul.kiulsmputilitiesv3.locatorbar.Waypoint;
import kiul.kiulsmputilitiesv3.stats.StatDB;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;

public class FightMethods {

    static HashMap<FightObject,Long> timeAllSneaking = new HashMap<>();
    static long waitingUntil;
    public static void startDistanceCheck (Player p,FightObject fight) {


        new BukkitRunnable() {
            @Override
            public void run() {

                boolean nearby = false;
                if (fight != null && fight.isPartaking(p.getUniqueId()) && !fight.getParticipants().isEmpty()) {
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
                    for (UUID uuid : fight.getParticipants()) {
                        if (uuid == p.getUniqueId()) continue;
                        if (LocatorBar.playerLocatorBar.get(p.getUniqueId()) != null) {
                            if (!LocatorBar.playerLocatorBar.get(p.getUniqueId()).barContains(Waypoint.waypoints.get(Bukkit.getOfflinePlayer(uuid).getName()))) {
                                LocatorBar.playerLocatorBar.get(p.getUniqueId()).addWaypoint(Waypoint.waypoints.get(Bukkit.getOfflinePlayer(uuid).getName()));
                            }
                        }
                    }
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
                            waitingUntil = System.currentTimeMillis() + 2990;
                        } else {
                            p.sendActionBar(MiniMessage.miniMessage().deserialize("<yellow>" + (1+(waitingUntil - System.currentTimeMillis()) / 1000)));
                            if (System.currentTimeMillis() - timeAllSneaking.get(fight) > 3000) {
                                p.sendActionBar(MiniMessage.miniMessage().deserialize("<yellow>fight disbanded"));
                                fight.disband();
                                cancel();
                                return;
                            }
                            return;
                        }
                    }

                } else {
                    new LocatorBar(45,p);
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(C.plugin,0,2);
    }




    public static int totalArmourDurability(Player p) {

        int totalDamage = 0;

        for (ItemStack armour : p.getInventory().getArmorContents()) {
            if (armour != null) {
                totalDamage += armour.getDurability();
            }
        }

        return totalDamage;
    }

    private static String getArrowTo(Player from, Player to) {
        if (to == null || to.getWorld() != from.getWorld() || to.getLocation().distance(from.getLocation()) > to.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE).getValue()) return "⚔";

        Vector facing = from.getLocation().getDirection().setY(0).normalize();

        Vector toTarget = to.getLocation().toVector().subtract(from.getLocation().toVector());
        toTarget.setY(0);

        if (toTarget.lengthSquared() == 0) return "⚔"; // player is at same spot

        toTarget.normalize();

        // signed angle between facing and target
        double dot = facing.dot(toTarget);
        double det = facing.getX() * toTarget.getZ() - facing.getZ() * toTarget.getX();
        double angle = Math.toDegrees(Math.atan2(det, dot));

        return getArrowFromAngle(angle);
    }

    private static String getArrowFromAngle(double diff) {
        if (diff >= -22.5 && diff < 22.5) return "\uD83E\uDC69";   // Forward
        if (diff >= 22.5 && diff < 67.5) return "\uD83E\uDC6D";   // Slight Right
        if (diff >= 67.5 && diff < 112.5) return "\uD83E\uDC6A";  // Right
        if (diff >= 112.5 && diff < 157.5) return "\uD83E\uDC6E"; // Far Right
        if (diff >= 157.5 || diff < -157.5) return "\uD83E\uDC6B"; // Behind
        if (diff >= -157.5 && diff < -112.5) return "\uD83E\uDC6F"; // Far Left
        if (diff >= -112.5 && diff < -67.5) return "\uD83E\uDC68"; // Left
        if (diff >= -67.5 && diff < -22.5) return "\uD83E\uDC6C";  // Slight Left
        return "⬆";
    }
}
