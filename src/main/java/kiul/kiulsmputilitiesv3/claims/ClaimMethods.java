package kiul.kiulsmputilitiesv3.claims;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.ClaimData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;

public class ClaimMethods {

    public static HashMap<String,Region> activeClaims = new HashMap<>();
    public static HashMap<Player,Region> playersInClaims = new HashMap<>();
    public static HashMap<String, BossBar> claimBars = new HashMap<>();


    public static void initializeClaims() {
        if (getClaimOwners() != null) {
            for (String claimOwner : getClaimOwners()) {
                Location claimCenter = (Location) ClaimData.get().get(claimOwner + ".core.location");
                int size = C.claimCoreRange;
                Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                Team team = mainScoreboard.getTeam(claimOwner);
                Location cornerOne = claimCenter.clone().add(-size, -size, -size);
                Location cornerTwo = claimCenter.clone().add(size, size, size);
                activeClaims.put(claimOwner, new Region(cornerOne.toVector(), cornerTwo.toVector(), claimOwner));
                BossBar bossBar = Bukkit.createBossBar(team.getPrefix() + ChatColor.RESET + "'s " + ClaimData.get().getString(claimOwner + ".core.name"), BarColor.WHITE, BarStyle.SEGMENTED_20);
                claimBars.put(claimOwner, bossBar);
            }
        }
    }

    public static Set<String> getClaimOwners() {
        Set<String> keys = ClaimData.get().getConfigurationSection("").getKeys(false);
        return keys;}

    public List<Region> getRegions() {
        List<Region> regions = new ArrayList<>();
        for (String claimOwner : getClaimOwners()) {
            Location claimCenter = (Location) ClaimData.get().get(claimOwner+".core.location");
            int size = C.claimCoreRange;
            Location cornerOne = claimCenter.clone().add(-size,-size,-size);
            Location cornerTwo = claimCenter.clone().add(size,size,size);
            regions.add(new Region(cornerOne.toVector(),cornerTwo.toVector(),claimOwner));
        }
    return regions;}

    public static void scheduleBlockRespawn (Block block, int timeUntilRegenerate, Material finalType, Location location) {
        Bukkit.broadcastMessage("regenerating in: " + timeUntilRegenerate);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage("regen attempt 1");
                boolean nearbyPlayer = false;
                for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(),5,5,5)) {
                    if (entity instanceof Player) {
                        nearbyPlayer = true;
                        break;
                    }
                }
                Bukkit.broadcastMessage("nearbyplayer = " + nearbyPlayer);
                    if (nearbyPlayer) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                boolean nearbyPlayer = false;
                                for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(),5,5,5)) {
                                    if (entity instanceof Player) {
                                        nearbyPlayer = true;
                                        break;
                                    }
                                }
                                if (!nearbyPlayer) {
                                    Bukkit.broadcastMessage("regenerated at: " + location);
                                    Bukkit.broadcastMessage("regenerated type: " + finalType);
                                    location.getBlock().setType(finalType);
                                    cancel();
                                }
                            }
                        }.runTaskTimer(C.plugin,20,20);
                    } else {
                        Bukkit.broadcastMessage("regenerated at: " + location);
                        Bukkit.broadcastMessage("regenerated type: " + finalType);
                        location.getBlock().setType(finalType);
                    }
                }
        }.runTaskLater(C.plugin,timeUntilRegenerate);
    }
 }
