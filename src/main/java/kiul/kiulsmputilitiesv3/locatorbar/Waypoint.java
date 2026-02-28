package kiul.kiulsmputilitiesv3.locatorbar;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.combattag.FightManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;

public class Waypoint {

    public static HashMap<String,Waypoint> waypoints = new HashMap<>();

    private String name;
    private Vector location;
    private TreeMap<Integer,String> textures; // distance, texture
    private Team team;
    private World world;
    private boolean isPlayer;

    public Waypoint(String name, Location location, TreeMap<Integer, String> textures, Team team, boolean isPlayer) {
        this.name = name;
        this.location = location.toVector();
        this.world = location.getWorld();
        this.textures = textures;
        this.team = team;
        this.isPlayer = isPlayer;
        waypoints.put(name,this);
    }

    public void delete() {
        waypoints.remove(name);
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public String getName() {
        return name;
    }

    public void updateLocation(Location location) {
        this.location = location.toVector();
        this.world = location.getWorld();
    }

    public void updateTeam(Team team) {
        this.team = team;
    }

    public Vector getLocation() {
        return location;
    }

    public TreeMap<Integer,String> getTextures() {
        return textures;
    }

    public World getWorld() {
        return world;
    }

    public Team getTeam() {
        return team;
    }

    public static void initializePlayerWaypointManager() {

        TreeMap<Integer,String> textures = new TreeMap<>() {{
            put(0,"⏺");
            put(100,"●");
            put(400,"•");
            put(1000,"·");



        }};
        new BukkitRunnable() {
            @Override
            public void run() {


                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    if (Waypoint.waypoints.get(onlinePlayer.getName()) == null) { // if the player has not got a waypoint assigned to their name , create one
                        Waypoint loginWaypoint = new Waypoint(onlinePlayer.getName(),onlinePlayer.getLocation(),textures,C.getPlayerTeam(onlinePlayer),true);
                        if (C.PAT_MODE) { // if pattyevent is installed, send the waypoint to all players
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (p != onlinePlayer) {
                                    LocatorBar.playerLocatorBar.get(p.getUniqueId()).addWaypoint(loginWaypoint);
                                }
                            }
                        } else if (C.getPlayerTeam(onlinePlayer) != null) { // if pattyevent is not installed, send the waypoint to their teammates.
                            for (String entry : C.getPlayerTeam(onlinePlayer).getEntries()) {
                                Player p = Bukkit.getPlayer(entry);
                                if (p != null && p != onlinePlayer) {
                                    LocatorBar.playerLocatorBar.get(p.getUniqueId()).addWaypoint(loginWaypoint);
                                }
                            }
                        }
                    } else { // if the player already has a waypoint assigned to their name, update its information every tick.
                        for (Waypoint w : Waypoint.waypoints.values()) {
                            if (w.getName().equals(onlinePlayer.getName())) {
                                w.updateLocation(onlinePlayer.getLocation());
                                w.updateTeam(C.getPlayerTeam(onlinePlayer));
                            }
                        }
                    }

                    if (!C.fightManager.playerIsInFight(onlinePlayer) && !C.PAT_MODE) { // if the player is not in a fight (and pattyevent isnt installed), they shouldn't have any player waypoints that aren't on their team.
                        // this is a safety check to make sure people can't exploit by changing teams and getting access to waypoints then changing back. that's why this updates every tick.
                        LocatorBar locatorBar = LocatorBar.playerLocatorBar.get(onlinePlayer.getUniqueId());
                        Iterator<Waypoint> iter = locatorBar.getWaypoints().iterator();
                        if (C.getPlayerTeam(onlinePlayer) == null) { // if they have no team, remove all player waypoints from their locator bar
                            while (iter.hasNext()) {
                                Waypoint waypoint = iter.next();
                                if (waypoint.isPlayer()) {
                                    locatorBar.removeWaypoint(waypoint);
                                }
                            }
                        } else {
                            while (iter.hasNext()) { // if they DO have a team, remove all player waypoints from their locator bar that aren't their teammates.
                                Waypoint waypoint = iter.next();
                                if (!waypoint.isPlayer()) continue;
                                if (!waypoint.getTeam().equals(C.getPlayerTeam(onlinePlayer))) {
                                    locatorBar.removeWaypoint(waypoint);
                                }
                            }
                            // whilst we're at it, might as well make it so they get all their teammates added if they're missing.
                            for (String entry : C.getPlayerTeam(onlinePlayer).getEntries()) {
                                Player p = Bukkit.getPlayer(entry);

                                if (locatorBar.barContains(Waypoint.waypoints.get(p.getName()))) continue;
                                if (p != null && p != onlinePlayer && p.isOnline()) {
                                    LocatorBar.playerLocatorBar.get(p.getUniqueId()).addWaypoint(Waypoint.waypoints.get(p.getName()));
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(C.plugin,0,1);
    }
}
