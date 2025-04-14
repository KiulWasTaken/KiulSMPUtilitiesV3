package kiul.kiulsmputilitiesv3.crates;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AABBUtils {

    /**
     * Calculates the distance from the player's location to the closest edge of an AABB.
     *
     * @param player The player whose distance is being calculated.
     * @param aabbMin The minimum corner of the AABB (e.g., lower corner).
     * @param aabbMax The maximum corner of the AABB (e.g., upper corner).
     * @return The distance from the player to the closest edge of the AABB.
     */
    public static double getDistanceToClosestEdge(Player player, Vector aabbMin, Vector aabbMax) {
        // Get the player's location as a Vector
        Location playerLocation = player.getLocation();
        Vector playerPos = playerLocation.toVector();

        // Calculate the closest point on the AABB to the player's position
        double closestX = clamp(playerPos.getX(), aabbMin.getX(), aabbMax.getX());
        double closestY = clamp(playerPos.getY(), aabbMin.getY(), aabbMax.getY());
        double closestZ = clamp(playerPos.getZ(), aabbMin.getZ(), aabbMax.getZ());

        // Create a Vector for the closest point on the AABB
        Vector closestPoint = new Vector(closestX, closestY, closestZ);

        // Calculate the distance between the player's position and the closest point on the AABB
        return playerPos.distance(closestPoint);
    }

    /**
     * Clamps a value between a minimum and maximum.
     *
     * @param value The value to clamp.
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The clamped value.
     */
    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }
}
