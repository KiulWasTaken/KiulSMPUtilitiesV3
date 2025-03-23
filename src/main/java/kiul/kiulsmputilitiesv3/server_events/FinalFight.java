package kiul.kiulsmputilitiesv3.server_events;

import kiul.kiulsmputilitiesv3.config.PersistentData;
import kiul.kiulsmputilitiesv3.config.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class FinalFight implements Listener {


    @EventHandler
    public void preventDimensionTravel (PlayerPortalEvent e) {
        if (!WorldData.get().getBoolean("nether.isenabled")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void suddenDeathListener (PlayerDeathEvent e) {

    }

    public static void beginShrinkWorldBorder (long borderClosedTime, int borderSize) {
        World overworld = Bukkit.getWorld("world");
        World nether = Bukkit.getWorld("world_nether");
        World end = Bukkit.getWorld("world_end");
        overworld.getWorldBorder().setSize(borderSize,borderClosedTime);



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

        WorldData.get().set("nether.isenabled",false);
        WorldData.save();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getLocation().getWorld().equals(end)) {
                PersistentData.get().set(offlinePlayer.getUniqueId()+".outofbounds",true);
                PersistentData.save();
            }
        }
    }
}
