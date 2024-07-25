package kiul.kiulsmputilitiesv3.config;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class ClaimData {

        private static File file;
        private static FileConfiguration userDataFile;

        public static void setup() {
            file = new File(Bukkit.getServer().getPluginManager().getPlugin("Kiul-SMP-Utilities-V3").getDataFolder(), "claimdata.yml");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {

                }
            }
            userDataFile = YamlConfiguration.loadConfiguration(file);
        }

        public static FileConfiguration get() {
            return userDataFile;
        }



        public static void save(){
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        userDataFile.save(file);
                    } catch (IOException e) {
                        System.out.println("Failed to save persistentData File.");
                    }
                }
            }.runTaskAsynchronously(C.plugin);
        }

        public static void reload(){
            userDataFile = YamlConfiguration.loadConfiguration(file);
        }


        
}
