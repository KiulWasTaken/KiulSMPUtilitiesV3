package kiul.kiulsmputilitiesv3.config;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class RecapData {

        private static File file;
        private static FileConfiguration arenaDataFile;

        public static void setup() {
            file = new File(Bukkit.getServer().getPluginManager().getPlugin("Kiul-SMP-Utilities-V3").getDataFolder(), "recaps.yml");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {

                }
            }
            arenaDataFile = YamlConfiguration.loadConfiguration(file);
        }

        public static FileConfiguration get() {
            return arenaDataFile;
        }



    public static void saveAsync(){

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    arenaDataFile.save(file);
                } catch (IOException e) {
                    System.out.println("Failed to save persistentData File.");
                }
            }
        }.runTaskAsynchronously(C.plugin);
    }
    public static void save() {
        try {
            arenaDataFile.save(file);
        } catch (IOException e) {
            System.out.println("Failed to save persistentData File.");
        }
    }

        public static void reload(){
            arenaDataFile = YamlConfiguration.loadConfiguration(file);
        }


        
}
