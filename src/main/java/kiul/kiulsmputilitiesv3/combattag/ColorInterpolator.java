package kiul.kiulsmputilitiesv3.combattag;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ColorInterpolator {

    public static String getColorBasedOnAverageDistance(Player player1, List<UUID> otherPlayers) {
        // Calculate the average distance from player1 to all other players
        double totalDistance = 0;
        for (UUID otherPlayer : otherPlayers) {

            totalDistance += calculateDistance(player1.getLocation(), Bukkit.getPlayer(otherPlayer).getLocation());
        }
        double averageDistance = totalDistance / otherPlayers.size();

        // Normalize the distance (Assume distance between 0 and some maximum distance)
        double normalizedDistance = normalizeDistance(averageDistance);

        // Get the color based on the normalized distance
        return getColorBasedOnDistance(normalizedDistance);
    }

    // Function to calculate the Euclidean distance between two Location objects
    public static double calculateDistance(Location loc1, Location loc2) {
        if (loc1.getWorld() != loc2.getWorld()) {
            if (loc1.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                loc1 = new Location(loc1.getWorld(),loc1.getX(),0,loc1.getZ());
                loc2 = new Location(loc1.getWorld(),loc2.getX()/8,0,loc2.getZ()/8);
            } else if (loc2.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                loc1 = new Location(loc1.getWorld(),loc1.getX()/8,0,loc1.getZ()/8);
                loc2 = new Location(loc1.getWorld(),loc2.getX(),0,loc2.getZ());
            } else {
                loc1 = new Location(loc1.getWorld(), loc1.getX(), 0, loc1.getZ());
                loc2 = new Location(loc1.getWorld(), loc2.getX(), 0, loc2.getZ());
            }
        }
        return loc1.distance(loc2);  // Spigot's Location.distance method
    }

    // Normalize the distance to be between 0 and 1
    public static double normalizeDistance(double distance) {
        // You can adjust the maxDistance based on your game's logic
        double maxDistance = 500.0;  // Example max distance between players
        return Math.min(1.0, distance / maxDistance);  // Clamp between 0 and 1
    }

    // Function to get the color based on normalized distance
    public static String getColorBasedOnDistance(double distance) {
        // Clamp the distance between 0 and 1
        distance = Math.max(0, Math.min(1, distance));

        // RGB values for the two colors
        int[] redColor = {227, 54, 48};   // #e33630 (red)
        int[] greenColor = {39, 163, 58}; // #27a33a (green)

        // Interpolate between red and green
        int r = (int) (redColor[0] * (1 - distance) + greenColor[0] * distance);
        int g = (int) (redColor[1] * (1 - distance) + greenColor[1] * distance);
        int b = (int) (redColor[2] * (1 - distance) + greenColor[2] * distance);

        // Return the resulting color in hex format
        return String.format("#%02X%02X%02X", r, g, b);
    }
}