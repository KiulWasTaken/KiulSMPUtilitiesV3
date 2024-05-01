package kiul.kiulsmputilitiesv3.claims;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.ClaimData;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

import static kiul.kiulsmputilitiesv3.claims.ClaimMethods.*;

public class ClaimListeners implements Listener {

    @EventHandler
    public void placeClaimCore (BlockPlaceEvent e) {
        if (e.getBlock().getType().equals(Material.LODESTONE)) {
            if (C.getPlayerTeam(e.getPlayer()) == null) {
                e.setCancelled(true);
                return;
            }
            if (ClaimData.get().get(C.getPlayerTeam(e.getPlayer()).getName()) == null) {
                Player p = e.getPlayer();
                FileConfiguration data = ClaimData.get();
                String teamName = C.getPlayerTeam(e.getPlayer()).getName();
                data.set(teamName+".core.location",e.getBlock().getLocation());
                data.set(teamName+".core.name","unnamed core");
                data.set(teamName+".core.health", 10.0);
                data.set(teamName+".core.size", C.claimCoreRange);
                ClaimData.save();
                Location claimCenter = (Location) ClaimData.get().get(teamName+".core.location");
                int size = C.claimCoreRange;
                Location cornerOne = claimCenter.clone().add(-size,-size,-size);
                Location cornerTwo = claimCenter.clone().add(size,size,size);
                activeClaims.put(teamName,new Region(cornerOne.toVector(),cornerTwo.toVector(),teamName));
                Team team = C.getPlayerTeam(p);
                BossBar bossBar = Bukkit.createBossBar(team.getPrefix()+ ChatColor.RESET+"'s " + ClaimData.get().getString(teamName+".core.name"), BarColor.WHITE, BarStyle.SEGMENTED_20);
                claimBars.put(teamName,bossBar);
                e.getBlock().getWorld().playSound(e.getBlock().getLocation(), Sound.BLOCK_BEACON_ACTIVATE,1,1);

            } else {
                e.setCancelled(true);
                e.getPlayer().sendMessage(C.chatColour+ "Your team already has a claim core placed somewhere");
            }
        }
    }

    @EventHandler
    public void breakClaimCore (BlockBreakEvent e) {
        if (e.getBlock().getType().equals(Material.LODESTONE)) {
                if (playersInClaims.containsKey(e.getPlayer())) {
                    Player p = e.getPlayer();
                    FileConfiguration data = ClaimData.get();
                    String teamName = playersInClaims.get(p).getOwningTeam();
                    data.set(teamName,null);
                    ClaimData.save();
                    if (claimBars.containsKey(teamName)) {
                        claimBars.get(teamName).removeAll();
                        claimBars.remove(teamName);
                    }
                    for (Player players : playersInClaims.keySet()) {
                        if (playersInClaims.get(players) == activeClaims.get(teamName)) {
                            playersInClaims.remove(players);
                        }
                    }
                    if (activeClaims.containsKey(teamName)) {
                        activeClaims.remove(teamName);
                    }
                } else {
                    e.setCancelled(true);
                }
            }
        }

    @EventHandler
    public void placeUnauthorisedBlock (BlockPlaceEvent e) {
        if (playersInClaims.containsKey(e.getPlayer())) {
            
            if (!C.playerIsOnTeam(playersInClaims.get(e.getPlayer()).getOwningTeam(),e.getPlayer())) {
                e.getBlock().setMetadata("unauth",new FixedMetadataValue(C.plugin,"block"));
                BlockState oldBlock = e.getBlockReplacedState();
                scheduleBlockRespawn(e.getBlock(),20 * C.blockRegenTimeSeconds,oldBlock.getType(),e.getBlock().getLocation());
            }
        } else {
            for (Region claimRegions : activeClaims.values()) {
                if (claimRegions.contains(e.getBlock().getLocation())) {
                    if (!C.playerIsOnTeam(claimRegions.getOwningTeam(), e.getPlayer())) {
                        e.getBlock().setMetadata("unauth",new FixedMetadataValue(C.plugin,"block"));
                        if (playersInClaims.containsValue(e.getPlayer())) {
                            BlockState oldBlock = e.getBlockReplacedState();
                            scheduleBlockRespawn(e.getBlock(),20 * C.blockRegenTimeSeconds,oldBlock.getType(),e.getBlock().getLocation());
                        } else {
                            playersInClaims.put(e.getPlayer(), claimRegions);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void breakUnauthorisedBlock (BlockBreakEvent e) {
        if (!e.getBlock().hasMetadata("unauth")) {
            if (playersInClaims.containsKey(e.getPlayer())) {
                if (!C.playerIsOnTeam(playersInClaims.get(e.getPlayer()).getOwningTeam(), e.getPlayer())) {
                    e.setDropItems(false);
                    scheduleBlockRespawn(e.getBlock(), 20 * C.blockRegenTimeSeconds, e.getBlock().getType(), e.getBlock().getLocation());
                }
            } else {
                for (Region claimRegions : activeClaims.values()) {
                    if (claimRegions.contains(e.getBlock().getLocation())) {
                        if (!C.playerIsOnTeam(claimRegions.getOwningTeam(), e.getPlayer())) {
                            if (playersInClaims.containsValue(e.getPlayer())) {
                                e.setDropItems(false);
                                scheduleBlockRespawn(e.getBlock(), 20 * C.blockRegenTimeSeconds, e.getBlock().getType(), e.getBlock().getLocation());
                            } else {
                                playersInClaims.put(e.getPlayer(), claimRegions);
                            }
                        }
                    }
                }
            }
        }
    }



    @EventHandler
    public void moveIntoOutOfClaimArea (PlayerMoveEvent e) {
        // give the player a boss bar showing the upkeep int
        if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
            if (!playersInClaims.containsKey(e.getPlayer())) {
                for (Region claimRegions : activeClaims.values()) {
                    if (claimRegions.contains(e.getTo())) {
                        playersInClaims.put(e.getPlayer(),claimRegions);
                        claimBars.get(claimRegions.getOwningTeam()).addPlayer(e.getPlayer());
                        return;
                    }
                }
            } else {
                if (!playersInClaims.get(e.getPlayer()).contains(e.getTo())) {
                    claimBars.get(playersInClaims.get(e.getPlayer()).getOwningTeam()).removePlayer(e.getPlayer());
                    playersInClaims.remove(e.getPlayer());
                }
            }
        }
    }
}
