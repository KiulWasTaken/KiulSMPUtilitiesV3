package kiul.kiulsmputilitiesv3.end_fight;

import io.papermc.paper.event.block.DragonEggFormEvent;
import jdk.jfr.Enabled;
import kiul.kiulsmputilitiesv3.config.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DragonFightListeners implements Listener {

    @EventHandler
    public void detectEnderDragonFirstTimeDeath (EntityDeathEvent e) {
        if (e.getEntity() instanceof EnderDragon enderDragon) {
            if (WorldData.get().get("end.dragon.isdead") != null && !WorldData.get().getBoolean("end.dragon.isdead"))
                WorldData.get().set("end.dragon.isdead",true);

        }
    }



}
