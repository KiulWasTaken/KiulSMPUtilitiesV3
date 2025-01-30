package kiul.kiulsmputilitiesv3.scheduler;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.ConfigData;
import org.bukkit.scheduler.BukkitRunnable;

public class SMPScheduler {


    public static void initializeScheduler () {
        if (ConfigData.get().getBoolean("scheduler")) {
            // hardcode some events with times in schedule config
            new BukkitRunnable() {
                @Override
                public void run() {
                    // go through all listed events in schedule config
                    // if they are within x time then show the time remaining until event to the entire server with a bossbar
                    // up to 2 bossbars showing server events at once - crates can spawn a 3rd bossbar if 2 are already on.
                }
            }.runTaskTimer(C.plugin,0,20);
        }
    }
}
