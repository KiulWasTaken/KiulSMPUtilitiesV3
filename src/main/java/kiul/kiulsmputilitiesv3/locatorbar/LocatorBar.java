package kiul.kiulsmputilitiesv3.locatorbar;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.towns.Town;
import net.royawesome.jlibnoise.module.modifier.ScalePoint;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.eclipse.aether.util.listener.ChainedTransferListener;

import java.util.*;

public class LocatorBar {

    public static HashMap<UUID,LocatorBar> playerLocatorBar = new HashMap<>();

    public static ArrayList<Player> disable_show_self = new ArrayList<>();
    public static ArrayList<Player> disable_show_teammates = new ArrayList<>();
    public static ArrayList<Player> disable_show_towns = new ArrayList<>();

    private int barLength;
    private List<Waypoint> waypoints;
    private Player ownerPlayer;

    LocatorBar(List<Waypoint> waypoints, int barLength) {
        this.barLength = barLength;
        this.waypoints = waypoints;
        for (Town town : Town.townsList) {
            waypoints.add(town.getWaypoint());
        }
    }

    public LocatorBar(int barLength, Player ownerPlayer) {
        this.barLength = barLength;
        this.waypoints = new ArrayList<>();
        this.ownerPlayer = ownerPlayer;
        for (Town town : Town.townsList) {
            waypoints.add(town.getWaypoint());
        }
        if (C.PAT_MODE) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (Waypoint.waypoints.get(p.getName()) != null) {
                    waypoints.add(Waypoint.waypoints.get(p.getName()));
                }
            }
        }
        playerLocatorBar.put(ownerPlayer.getUniqueId(),this);
    }

    public void addWaypoint(Waypoint newWaypoint) {
        waypoints.add(newWaypoint);
    }
    public void removeWaypoint(Waypoint waypoint) {
            waypoints.remove(waypoint);
    }

    public String getBarString(Location barCenterLocation) {
        float playerYaw = (barCenterLocation.getYaw() + 360) % 360;
        float playerPitch = barCenterLocation.getPitch();
        String[] bar = new String[barLength];
        Arrays.fill(bar, " "); // Fill with String spaces, not char
        int offLeft = 0;
        int offRight = 0;
        String leftColour = ChatColor.WHITE+"";
        String rightColour = ChatColor.WHITE+"";
        for (Waypoint w : waypoints) {
            if (!w.isPlayer()) {
                if (disable_show_towns.contains(ownerPlayer)) continue;
            }
            if (Bukkit.getPlayer(w.getName()) != null) {
                Player playerWaypoint = Bukkit.getPlayer(w.getName());
                if (disable_show_self.contains(playerWaypoint) && C.getPlayerTeam(playerWaypoint) == C.getPlayerTeam(ownerPlayer)) continue;
                if (disable_show_teammates.contains(ownerPlayer) && C.getPlayerTeam(playerWaypoint) == C.getPlayerTeam(ownerPlayer)) continue;
                if (playerWaypoint == null || !playerWaypoint.isOnline()) continue;
                if (playerLocatorBar.get(playerWaypoint.getUniqueId()) == this) continue;
                if (playerWaypoint.getGameMode() != GameMode.SURVIVAL) continue;
            }
            double distance =  w.getLocation().distance(barCenterLocation.toVector());
            String dot = "";

            for (Integer textureDistances : w.getTextures().keySet()) {
                if (textureDistances < distance) {
                    dot = w.getTextures().get(textureDistances);
                }
            }

            Vector other = w.getLocation().clone();
            if (!w.getWorld().equals(barCenterLocation.getWorld())) continue;

            String color = ChatColor.WHITE+"";
            if (w.getTeam() != null) {
                color =  w.getTeam().getColor() + "";
            }



            Vector vec = other.subtract(barCenterLocation.toVector());
            float targetYaw = vectorToYaw(vec);
            float targetPitch = vectorToPitch(vec);

            float diff = (targetYaw - playerYaw + 360) % 360;
            if (diff > 180) diff -= 360;

            float pitchDiff = targetPitch - playerPitch;


            if (diff < -60F) {
                offLeft++;
                leftColour = color;
                continue;
            }

            if (diff > 60F) {
                offRight++;
                rightColour = color;
                continue;
            }

            if (distance < 100) {
                if (pitchDiff < -45F) {
                    dot = "↑";
                } else if (pitchDiff > 45F) {
                    dot = "↓";
                }
            }


            if (Math.abs(diff) <= 60F) {
                float normalized = (diff + 60F) / 120F;
                int index = (int) (normalized * barLength);
                index = Math.min(Math.max(index, 0), barLength - 1);
                bar[index] = color + dot;
            }
        }
        String leftPrefix;
        String rightSuffix;
        StringBuilder finalBar = new StringBuilder();
        if (offLeft > 0) {
            leftPrefix =  ChatColor.BOLD + "  "+ ChatColor.RESET + leftColour + "  ←";
            if (offLeft > 1) {
                leftPrefix =  ChatColor.GRAY +"[" + ChatColor.WHITE+ offLeft + ChatColor.GRAY + "]" + leftColour + " ←";
            }
        } else {
            leftPrefix = "    " + ChatColor.BOLD + " " + ChatColor.RESET;
        }
        finalBar.append(leftPrefix);
        for (String segment : bar) {
            finalBar.append(segment);
        }
        if (offRight > 0) {
            rightSuffix = rightColour + "→  "+ ChatColor.BOLD + "  " + ChatColor.RESET;
            if (offRight > 1) {
                rightSuffix = rightColour + "→ "+ ChatColor.GRAY +"[" + ChatColor.WHITE+ offRight + ChatColor.GRAY + "]";
            }
        } else {
            rightSuffix = "    " + ChatColor.BOLD + " " + ChatColor.RESET;;
        }
        finalBar.append(rightSuffix);
        return finalBar.toString();
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public boolean barContains(Waypoint waypoint) {
        if (waypoint == null) return false;
        for (Waypoint w : waypoints) {
            if (w.getName().equals(waypoint.getName())) {
                return true;
            }
        }
        return false;
    }

    private float vectorToPitch(Vector vec) {
        double x = vec.getX();
        double y = vec.getY();
        double z = vec.getZ();

        double horizontalDistance = Math.sqrt(x * x + z * z);
        return (float) Math.toDegrees(-Math.atan2(y, horizontalDistance));
    }

    public static float vectorToYaw(Vector vec) {
        // Minecraft uses X-Z plane for yaw, with Z forward and X sideways
        double x = vec.getX();
        double z = vec.getZ();

        // atan2 returns angle in radians from -π to π
        double yawRadians = Math.atan2(-x, z); // Negate X to match Minecraft's yaw convention
        double yawDegrees = Math.toDegrees(yawRadians);

        // Normalize to 0–360
        float yaw = (float) ((yawDegrees + 360) % 360);
        return yaw;
    }


    public static void sendLocatorBars() {
        TreeMap<Integer, String> textures = new TreeMap<>() {{
            put(0, "⏺");
            put(100, "●");
            put(500, "•");
            put(1000, "·");


        }};
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (playerLocatorBar.containsKey(onlinePlayer.getUniqueId())) {
                        String locatorBar = playerLocatorBar.get(onlinePlayer.getUniqueId()).getBarString(onlinePlayer.getLocation());
                        if (!locatorBar.isBlank()) {
                            onlinePlayer.sendActionBar(locatorBar);
                        }
                    }

                }
            }
        }.runTaskTimer(C.plugin, 0, 1);
    }
}
