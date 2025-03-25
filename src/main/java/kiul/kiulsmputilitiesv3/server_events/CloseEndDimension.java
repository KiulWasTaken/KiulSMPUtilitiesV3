package kiul.kiulsmputilitiesv3.server_events;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import kiul.kiulsmputilitiesv3.config.WorldData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.pat.pattyEssentialsV3.Enums.MenuEnum;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.bukkit.Bukkit.getServer;
import static org.pat.pattyEssentialsV3.Listeners.ClickInv.changeEventConfig;

public class CloseEndDimension implements Listener {

    @EventHandler
    public void portalFrameInteract (PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {return;}
        if (e.getClickedBlock().getType() == Material.END_PORTAL_FRAME) {
            EndPortalFrame endPortalFrame = ((EndPortalFrame) e.getClickedBlock().getBlockData());
            if (!endPortalFrame.hasEye() && e.getItem().getType().equals(Material.ENDER_EYE)) {
                if (WorldData.get().getConfigurationSection("end.") == null) {
                    C.plugin.getLogger().info("frame location write passed");
                    WorldData.get().set("end.frame0"+".x",e.getClickedBlock().getLocation().x());
                    WorldData.get().set("end.frame0"+".y",e.getClickedBlock().getLocation().y());
                    WorldData.get().set("end.frame0"+".z",e.getClickedBlock().getLocation().z());
                    WorldData.save();
                    return;
                }
                Set<String> keys = WorldData.get().getConfigurationSection("end.").getKeys(false);

                for (String key : keys) {
                    if (new Location(e.getClickedBlock().getWorld(), WorldData.get().getDouble("end." + key + ".x"), WorldData.get().getDouble("end." + key + ".y"), WorldData.get().getDouble("end." + key + ".z")).equals(e.getClickedBlock().getLocation())) {
                        C.plugin.getLogger().info("frame location write cancelled because of a duplicate");
                        return;
                    }
                }
                C.plugin.getLogger().info("frame location write passed");
                WorldData.get().set("end.frame"+keys.size()+".x",e.getClickedBlock().getLocation().x());
                WorldData.get().set("end.frame"+keys.size()+".y",e.getClickedBlock().getLocation().y());
                WorldData.get().set("end.frame"+keys.size()+".z",e.getClickedBlock().getLocation().z());


                WorldData.save();
            }
        }
    }

    @EventHandler
    public void teleportPlayerOutOfBounds (PlayerJoinEvent e) {
        if (PersistentData.get().getBoolean(e.getPlayer().getUniqueId()+"outofbounds")) {
            World overworld = Bukkit.getWorld("world");
            if (e.getPlayer().getRespawnLocation() != null) {
                e.getPlayer().teleport(e.getPlayer().getRespawnLocation());
            } else {
                e.getPlayer().teleport(overworld.getSpawnLocation());
            }
        }
    }

    public static void deleteEndPortalBlocks (World overworld) {
        if (WorldData.get().getConfigurationSection("end.") == null) return;
        Set<String> keys = WorldData.get().getConfigurationSection("end.").getKeys(false);

        for (String key : keys) {
            Location frameLocation = new Location(overworld, WorldData.get().getDouble("end." + key + ".x"), WorldData.get().getDouble("end." + key + ".y"), WorldData.get().getDouble("end." + key + ".z"));
            if (frameLocation.getBlock().getType().equals(Material.END_PORTAL_FRAME)) {
                EndPortalFrame endPortalFrame = ((EndPortalFrame) frameLocation.getBlock().getBlockData());
                endPortalFrame.setEye(false);
                frameLocation.getBlock().setBlockData(endPortalFrame);
            }
            for (int x = -3; x < 3; x++) {
                for (int z = -3; z < 3; z++) {
                    Block possiblePortalBlock = frameLocation.clone().add(x,0,z).getBlock();
                    if (possiblePortalBlock.getType().equals(Material.END_PORTAL)) {
                        possiblePortalBlock.setType(Material.AIR);
                    }
                }
            }
        }
    }

    public static void increaseEndBorder () {
        World overworld = Bukkit.getWorld("world");
        World end = Bukkit.getWorld("world_the_end");
        end.getWorldBorder().setSize(overworld.getWorldBorder().getSize()/1.5);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer,Sound.ENTITY_ELDER_GUARDIAN_CURSE,1,0.8f);
            if (onlinePlayer.getWorld().equals(end)) {
                if (onlinePlayer.getRespawnLocation() != null) {
                    onlinePlayer.teleport(onlinePlayer.getRespawnLocation());
                } else {
                    onlinePlayer.teleport(overworld.getSpawnLocation());
                }
            }
        }
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getLocation().getWorld().equals(end)) {
                PersistentData.get().set(offlinePlayer.getUniqueId()+".outofbounds",true);
                PersistentData.save();
            }
        }
    }

    public static void scheduleEndClosure (int hours, int minutes) {
        World overworld = Bukkit.getWorld("world");
        Set<String> keys = WorldData.get().getConfigurationSection("end.").getKeys(false);
        long closeTime = System.currentTimeMillis()+(1000L*60*60*hours)+(1000L*60*minutes);
        List<ArmorStand> endCloseTimerNameplates = new ArrayList<>();
        HashMap<ArmorStand,ArmorStand> endCloseTimerTitles = new HashMap<>();
        for (String key : keys) {



            Location frameLocation = new Location(overworld, WorldData.get().getDouble("end." + key + ".x"), WorldData.get().getDouble("end." + key + ".y"), WorldData.get().getDouble("end." + key + ".z"));

            boolean frameHasExistingNameplate = false;
            for (ArmorStand nameplate : endCloseTimerNameplates) {
                if (nameplate.getLocation().distance(frameLocation) < 20) {
                    frameHasExistingNameplate = true;
                }
            }
            if (frameHasExistingNameplate) {continue;}

            if (frameLocation.getBlock().getType().equals(Material.END_PORTAL_FRAME)) {
                for (int x = -3; x < 3; x++) {
                    for (int z = -3; z < 3; z++) {
                        Block possiblePortalBlock = frameLocation.clone().add(x, 0, z).getBlock();
                        if (possiblePortalBlock.getType().equals(Material.END_PORTAL)) {
                            Location centerLocation = possiblePortalBlock.getLocation();
                            boolean isSurrounded = true;
                            for (int x2 = -2; x2 < 1; x2++) {
                                for (int z2 = -2; z2 < 1; z2++) {
                                    if (!possiblePortalBlock.getLocation().clone().add(x2,0,z2).getBlock().getType().equals(Material.END_PORTAL)) {
                                        centerLocation = possiblePortalBlock.getLocation().clone().add(x2,0,z2);
                                        isSurrounded = false;
                                        break;
                                    }
                                }
                            }
                            if (!isSurrounded) {
                                continue;
                            }

                            possiblePortalBlock.getChunk().setForceLoaded(true);
                            ArmorStand nameplate = (ArmorStand) possiblePortalBlock.getWorld().spawnEntity(centerLocation.clone().add(-0.5,0.8,-0.5), EntityType.ARMOR_STAND);
                            endCloseTimerNameplates.add(nameplate);
                        }
                    }
                }
            }
        }

        for (ArmorStand nameplate : endCloseTimerNameplates) {
            nameplate.setInvulnerable(true);
            nameplate.setVisible(false);
            nameplate.setGravity(false);
            nameplate.setCustomNameVisible(true);
            nameplate.setPersistent(true);
            nameplate.setMarker(true);
            ArmorStand title = (ArmorStand) nameplate.getWorld().spawnEntity(nameplate.getLocation().clone().add(0,0.3,0), EntityType.ARMOR_STAND);
            title.setInvulnerable(true);
            title.setVisible(false);
            title.setGravity(false);
            title.setCustomNameVisible(true);
            title.setPersistent(true);
            title.setMarker(true);
            title.setCustomName(C.GRAY_PINK+"End Dimension Closing Soon");
            endCloseTimerTitles.put(nameplate,title);
        }
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {

                if (System.currentTimeMillis() < closeTime) {
                    tick++;
                    int[] timestamps = C.splitTimestamp(closeTime);


                    if (tick >= 300) {
                        tick = 0;
                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage(C.eventPrefix + C.GRAY_PINK+"End Dimension is Closing " + ChatColor.WHITE+"in " + C.GRAY_PURPLE + timestamps[0] + ChatColor.WHITE+ " hour(s) and " + C.GRAY_PURPLE + timestamps[1] + ChatColor.WHITE + " minute(s)");
                        Bukkit.broadcastMessage("");
                    }
                    if (closeTime - System.currentTimeMillis() <= 5 * 1000 * 60) {
                        if (closeTime - System.currentTimeMillis() <= 5 * 1000) {
                            tick = 0;
                            Bukkit.broadcastMessage("");
                            Bukkit.broadcastMessage(C.eventPrefix + C.GRAY_PINK+"End Dimension is Closing " + ChatColor.WHITE+"in " + C.GRAY_PURPLE + C.GRAY_PURPLE + timestamps[2] + ChatColor.WHITE + " second(s)");
                            Bukkit.broadcastMessage("");
                        }
                        if (tick >= 30) {
                            tick = 0;
                            Bukkit.broadcastMessage("");
                            Bukkit.broadcastMessage(C.eventPrefix + C.GRAY_PINK+"End Dimension is Closing " + ChatColor.WHITE+"in " + C.GRAY_PURPLE + timestamps[1] + ChatColor.WHITE+ " minute(s) and " + C.GRAY_PURPLE + timestamps[2] + ChatColor.WHITE + " second(s)");
                            Bukkit.broadcastMessage("");
                        }
                    }

                    for (ArmorStand nameplate : endCloseTimerNameplates) {
                        nameplate.setCustomName(String.format("%02d:%02d:%02d", timestamps[0], timestamps[1], timestamps[2]));
                    }
                } else {
                    for (ArmorStand nameplate : endCloseTimerNameplates) {
                        nameplate.remove();
                        endCloseTimerTitles.get(nameplate).remove();
                    }
                    deleteEndPortalBlocks(overworld);
                    increaseEndBorder();
                    if (getServer().getPluginManager().getPlugin("PattyEssentialsV3") != null) {
                        changeEventConfig(null, MenuEnum.endDimension.getPath(), false, 0, false, null, null);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(C.plugin,0,20);
    }

}
