package kiul.kiulsmputilitiesv3.claims;

import org.bukkit.Location;
import org.bukkit.util.Vector;


public class Region
{
    private final Vector minimum;
    private final Vector maximum;

    private final Vector center;
    private final String owningTeam;

    public Region(Vector firstPoint, Vector secondPoint,String teamName)
    {
        this.minimum = Vector.getMinimum(firstPoint, secondPoint);
        this.maximum = Vector.getMaximum(firstPoint, secondPoint);
        this.center = minimum.getMidpoint(maximum);
        this.owningTeam = teamName;
    }

    public Vector getCenter()
    {
        return center;
    }
    public String getOwningTeam()
    {
        return owningTeam;
    }

    public boolean contains(Location location)
    {
        return location.toVector().isInAABB(minimum, maximum);

    }
}
