package kiul.kiulsmputilitiesv3.locatorbar;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.TreeMap;

public class LocatorBarJoinEvent implements Listener {


    @EventHandler
    public void locatorBarJoin (PlayerJoinEvent e) {
        if (PersistentData.get().get("locatorbar." + e.getPlayer().getUniqueId() + ".enabled") == null) {
            PersistentData.get().set("locatorbar." + e.getPlayer().getUniqueId() + ".enabled", true);
            PersistentData.get().set("locatorbar." + e.getPlayer().getUniqueId() + ".show_towns", true);
            PersistentData.get().set("locatorbar." + e.getPlayer().getUniqueId() + ".show_teammates", true);
            PersistentData.get().set("locatorbar." + e.getPlayer().getUniqueId() + ".share_self", true);
            PersistentData.saveAsync();
            new LocatorBar(45, e.getPlayer());
            return;
        }

        if (PersistentData.get().getBoolean("locatorbar." + e.getPlayer().getUniqueId() + ".enabled")) {
            new LocatorBar(45, e.getPlayer());
        }
        if (PersistentData.get().getBoolean("locatorbar." + e.getPlayer().getUniqueId() + ".show_towns")) {
            LocatorBar.disable_show_towns.add(e.getPlayer());
        }
        if (PersistentData.get().getBoolean("locatorbar." + e.getPlayer().getUniqueId() + ".show_teammates")) {
            LocatorBar.disable_show_teammates.add(e.getPlayer());
        }
        if (PersistentData.get().getBoolean("locatorbar." + e.getPlayer().getUniqueId() + ".share_self")) {
            LocatorBar.disable_show_self.add(e.getPlayer());
        }


    }

    @EventHandler
    public void locatorBarQuit (PlayerQuitEvent e) {
        Waypoint waypoint = Waypoint.waypoints.get(e.getPlayer().getName());
            if (waypoint != null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    LocatorBar.playerLocatorBar.get(p.getUniqueId()).removeWaypoint(waypoint);
                }
                waypoint.delete();
            }

    }
}
