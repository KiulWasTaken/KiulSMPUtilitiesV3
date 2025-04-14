package kiul.kiulsmputilitiesv3.server_events;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import kiul.kiulsmputilitiesv3.config.ScheduleConfig;
import kiul.kiulsmputilitiesv3.config.WorldData;
import org.bukkit.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class FinalFight implements Listener {

    int maximumTimeLoggedOutMinutes = 5;

    @EventHandler
    public void preventDimensionTravel (PlayerPortalEvent e) {
        if (WorldData.get().getBoolean("final_fight")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void finalFightSpectator (PlayerDeathEvent e) {
        if (WorldData.get().getBoolean("final_fight")) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
    }
    private HashMap<Entity, Player> entityOwner = new HashMap<>();

    @EventHandler
    public void playerPlaceEntity (EntityPlaceEvent e) {
        if (C.suddenDeath != null) {
            Player p = e.getPlayer();
            if (e.getEntity() instanceof ExplosiveMinecart cart) {
                entityOwner.put(cart, p);
            }
        }
    }

    @EventHandler
    public void suddenDeathListener (EntityDamageByEntityEvent e) {
        if (C.suddenDeath != null) {
            SuddenDeath suddenDeath = C.suddenDeath;
            if (e.getEntity() instanceof Player) {
                Player playerDamager = null;
                if (e.getDamager() instanceof ExplosiveMinecart cart) {
                    if (entityOwner.get(cart) != null) {
                        Player damager = entityOwner.get(cart);
                        playerDamager = damager;
                    }
                }
                if (e.getDamager() instanceof AbstractArrow arrow) {
                    if (arrow.getShooter() instanceof Player) {
                        playerDamager = (Player) arrow.getShooter();
                    }
                }
                if (e.getDamager() instanceof Player) {
                    playerDamager = (Player) e.getDamager();
                }

                Player playerHurt = (Player) e.getEntity();
                if (playerDamager == null) {
                    return;
                }
                if (C.getPlayerTeam(playerDamager) != null && C.getPlayerTeam(playerHurt) != null) {
                    if ((C.getPlayerTeam(playerDamager).getName() == C.getPlayerTeam(playerHurt).getName())) {
                        return;
                    }
                }

                suddenDeath.livingPlayersDamageMap.put(playerDamager,e.getDamage());
                if (playerHurt.getHealth() <= e.getFinalDamage() && playerHurt.getInventory().getItemInMainHand().getType() != Material.TOTEM_OF_UNDYING && playerHurt.getInventory().getItemInOffHand().getType() != Material.TOTEM_OF_UNDYING) {
                    // dead
                    suddenDeath.livingPlayersDamageMap.remove(playerHurt);
                    suddenDeath.graceTime = System.currentTimeMillis()+2*60*1000; // pause the suddenDeath for two minutes
                }
            }
        }
    }

    HashMap<Player,Long> outsideBorderDamageCooldown = new HashMap<>();

    @EventHandler
    public void worldBorderCheeseListener(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode().equals(GameMode.SURVIVAL)) {
            World world = p.getWorld();
            double size = world.getWorldBorder().getSize();
            Location center = new Location(world, world.getWorldBorder().getCenter().getX(), p.getLocation().getY() + 5, world.getWorldBorder().getCenter().getZ());

            // Define the border corners using the correct coordinates
            Location northWestCorner = new Location(world, world.getWorldBorder().getCenter().getX() - size / 2, p.getLocation().getY(), world.getWorldBorder().getCenter().getZ() - size / 2);
            Location southEastCorner = new Location(world, world.getWorldBorder().getCenter().getX() + size / 2, p.getLocation().getY(), world.getWorldBorder().getCenter().getZ() + size / 2);

            // Define the minimum and maximum vectors for the AABB
            Vector minimum = new Vector(Math.min(northWestCorner.getX(), southEastCorner.getX()), -64, Math.min(northWestCorner.getZ(), southEastCorner.getZ()));
            Vector maximum = new Vector(Math.max(northWestCorner.getX(), southEastCorner.getX()), 1000, Math.max(northWestCorner.getZ(), southEastCorner.getZ()));

            // Check if the player is outside the world border (in terms of X and Z, Y is excluded here)
            if (!p.getLocation().toVector().isInAABB(minimum, maximum)) {
                if (p.getVehicle() != null) p.getVehicle().removePassenger(p);
                // Check the cooldown before applying damage and velocity
                Long lastDamageTime = outsideBorderDamageCooldown.get(p);
                if (lastDamageTime == null || System.currentTimeMillis() - lastDamageTime > 500) {
                    // Apply damage to the player
                    p.damage(16, DamageSource.builder(DamageType.OUTSIDE_BORDER).build());

                    // Push the player back toward the center of the world border
                    Vector directionToCenter = center.toVector().subtract(p.getLocation().toVector()).normalize().multiply(0.5f);
                    p.setVelocity(directionToCenter);

                    // Set the cooldown for this player
                    outsideBorderDamageCooldown.put(p, System.currentTimeMillis());
                }
            }
        }
    }

    @EventHandler
    public void playerQuitDuringFinalFight (PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (WorldData.get().getBoolean("border_closed")) {
            PersistentData.get().set(p.getUniqueId().toString()+".final_fight.time_quit",System.currentTimeMillis());
            PersistentData.get().set(p.getUniqueId().toString()+".final_fight.total_time_quit",0);
            PersistentData.save();
        }
    }

    @EventHandler
    public void joinOutsideBorderWhilstStillClosing (PlayerJoinEvent e) {
        Player p = e.getPlayer();
        World world = Bukkit.getWorld("world");
        double size = world.getWorldBorder().getSize();
        Location northWestCorner = new Location(world,-(0.5*size),1000,-(0.5*size));
        Location southEastCorner = new Location(world,0.5*size,-64,0.5*size);
        Vector minimum = Vector.getMinimum(northWestCorner.toVector(),southEastCorner.toVector());
        Vector maximum = Vector.getMaximum(northWestCorner.toVector(),southEastCorner.toVector());
        if (!p.getLocation().toVector().isInAABB(minimum,maximum)) {
            p.playSound(p,Sound.ENTITY_ELDER_GUARDIAN_CURSE,1,0.8f);
            p.teleport(world.getSpawnLocation());
        }
        if (PersistentData.get().get(p.getUniqueId()+".outofbounds") != null) {
            if (PersistentData.get().getBoolean(p.getUniqueId()+".outofbounds")) {
                if (p.getRespawnLocation() != null) {
                    p.teleport(p.getRespawnLocation());
                } else {
                    p.teleport(world.getSpawnLocation());
                }
                PersistentData.get().set(p.getUniqueId()+".outofbounds",false);
                PersistentData.save();
            }
        }

        if (WorldData.get().getBoolean("border_closed")) {
            long timeQuit;
            long totalTimeQuit;
            if ((PersistentData.get().get(p.getUniqueId().toString() + ".final_fight.time_quit") != null)) {
                timeQuit = PersistentData.get().getLong(p.getUniqueId().toString() + ".final_fight.time_quit");
                totalTimeQuit = PersistentData.get().getLong(p.getUniqueId().toString() + ".final_fight.total_time_quit");
                totalTimeQuit += (System.currentTimeMillis() - timeQuit);
                PersistentData.get().set(p.getUniqueId().toString() + ".final_fight.total_time_quit", totalTimeQuit);
                PersistentData.save();
            } else return;


            if ((totalTimeQuit > (long) maximumTimeLoggedOutMinutes * 60 * 1000)
                    || ScheduleConfig.get().getLong("start_time") + (long) ScheduleConfig.get().getInt("event.sudden_death.time") * 60 * 60 * 1000 < System.currentTimeMillis()) {
                int[] loggedOutTimestamps = C.splitTimestampManual(System.currentTimeMillis(), System.currentTimeMillis() + totalTimeQuit);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendMessage(C.failPrefix + " you were logged out for " + loggedOutTimestamps[1] + " minute(s) and " + loggedOutTimestamps[2] + "second(s) and violated the maximum " +
                                "logout time of " + maximumTimeLoggedOutMinutes + " minute(s) during this event." + C.LIGHT_RED + "You have been disqualified and killed as a result of this.");
                    }
                }.runTaskLater(C.plugin,20);

                p.setGameMode(GameMode.SPECTATOR);
                p.teleport(world.getSpawnLocation());

                if (C.suddenDeath != null) {
                    if (C.suddenDeath.getLivingPlayersDamageMap().containsKey(p)) {
                        C.suddenDeath.getLivingPlayersDamageMap().remove(p);
                    }
                }
            } else {
                int[] loggedOutTimestamps = C.splitTimestampManual(timeQuit, System.currentTimeMillis());
                int[] timeRemainingTimestamps = C.splitTimestampManual(System.currentTimeMillis(), System.currentTimeMillis()+((long) maximumTimeLoggedOutMinutes * 60 * 1000 - totalTimeQuit));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.sendMessage(C.warnPrefix + "you were logged out for " + loggedOutTimestamps[1] + " minute(s) and " + loggedOutTimestamps[2] + " second(s). You can be logged out for another "
                                + timeRemainingTimestamps[1] + " minute(s) and " + timeRemainingTimestamps[2] + " second(s) during this event before you are" + C.LIGHT_RED + " disqualified and killed.");
                    }
                }.runTaskLater(C.plugin,20);

            }
        }
    }

    public static void beginShrinkWorldBorder (long borderClosedTime, int borderSize) {
        World overworld = Bukkit.getWorld("world");
        World nether = Bukkit.getWorld("world_nether");
        World end = Bukkit.getWorld("world_end");
        overworld.getWorldBorder().setSize(borderSize,borderClosedTime);

        overworld.setGameRule(GameRule.DO_MOB_SPAWNING,false);
        overworld.setGameRule(GameRule.DO_PATROL_SPAWNING,false);
        overworld.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        overworld.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        overworld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer, Sound.ENTITY_ELDER_GUARDIAN_CURSE,1,0.8f);
            if (onlinePlayer.getWorld().equals(nether) || onlinePlayer.getWorld().equals(end)) {
                if (onlinePlayer.getRespawnLocation() != null) {
                    onlinePlayer.teleport(onlinePlayer.getRespawnLocation());
                } else {
                    onlinePlayer.teleport(overworld.getSpawnLocation());
                }
            }
        }

        WorldData.get().set("final_fight",true);
        WorldData.save();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getLocation().getWorld().equals(end) || offlinePlayer.getLocation().getWorld().equals(nether)) {
                PersistentData.get().set(offlinePlayer.getUniqueId()+".outofbounds",true);
                PersistentData.save();
            }
        }
    }
}
