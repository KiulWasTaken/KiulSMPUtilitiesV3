package kiul.kiulsmputilitiesv3;

import kiul.kiulsmputilitiesv3.accessories.*;
import kiul.kiulsmputilitiesv3.combatlog.LogoutListeners;
import kiul.kiulsmputilitiesv3.combatlog.MovementListeners;
import kiul.kiulsmputilitiesv3.combattag.FightLogicListeners;
import kiul.kiulsmputilitiesv3.combattag.RecapInventory;
import kiul.kiulsmputilitiesv3.config.*;
import kiul.kiulsmputilitiesv3.crates.CrateListeners;
import kiul.kiulsmputilitiesv3.crates.CrateMethods;
import kiul.kiulsmputilitiesv3.end_fight.CloseEndDimension;
import kiul.kiulsmputilitiesv3.featuretoggle.FeatureInventory;
import kiul.kiulsmputilitiesv3.itemhistory.listeners.ItemCraft;
import kiul.kiulsmputilitiesv3.itemhistory.listeners.ItemPickupAfterDeath;
import kiul.kiulsmputilitiesv3.scheduler.SMPScheduler;
import kiul.kiulsmputilitiesv3.stats.StatDB;
import kiul.kiulsmputilitiesv3.stats.StatDBListeners;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class KiulSMPUtilitiesV3 extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic


        // Listeners
        getServer().getPluginManager().registerEvents(new FightLogicListeners(),this);
        getServer().getPluginManager().registerEvents(new LogoutListeners(),this);
        getServer().getPluginManager().registerEvents(new MovementListeners(),this);
        getServer().getPluginManager().registerEvents(new AccessoryListeners(),this);
        getServer().getPluginManager().registerEvents(new CraftAccessory(),this);
        getServer().getPluginManager().registerEvents(new CrateListeners(),this);
        getServer().getPluginManager().registerEvents(new RingAccessory(),this);
        getServer().getPluginManager().registerEvents(new TomeAccessory(),this);
        getServer().getPluginManager().registerEvents(new ItemCraft(),this);
        getServer().getPluginManager().registerEvents(new ItemPickupAfterDeath(),this);
        getServer().getPluginManager().registerEvents(new StatDBListeners(),this);
        getServer().getPluginManager().registerEvents(new RecapInventory(),this);
        getServer().getPluginManager().registerEvents(new FeatureInventory(),this);
        getServer().getPluginManager().registerEvents(new EggAccessory(),this);
        getServer().getPluginManager().registerEvents(new CloseEndDimension(),this);
        // Recipes
        for (AccessoryItemEnum accessoryItem : AccessoryItemEnum.values()) {
            if ((accessoryItem.getLocalName().contains("ring") || accessoryItem.getLocalName().contains("tome")) && accessoryItem.getLocalName().contains("base")) {
                ItemStack is = accessoryItem.getAccessory();
                ShapelessRecipe sr = new ShapelessRecipe(new NamespacedKey(C.plugin, accessoryItem.getLocalName()), is);
                    switch (accessoryItem.getLocalName().substring(0, 4)) {
                        case "ring":
                            sr.addIngredient(Material.IRON_INGOT);
                            sr.addIngredient(Material.NETHERITE_SCRAP);
                            break;
                        case "tome":
                            sr.addIngredient(Material.BOOK);
                            sr.addIngredient(Material.NETHERITE_SCRAP);
                            break;
                    }
                Bukkit.addRecipe(sr);
            }
        }

        // Logger
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.WARNING);
        Logger mongoConnectionLogger = Logger.getLogger( "org.mongodb.driver.connection" );
        mongoConnectionLogger.setLevel(Level.SEVERE);

        // Dependencies

        // Commands
        getCommand("logout").setExecutor(new Commands());
        getCommand("accessory").setExecutor(new Commands());
        getCommand("give-accessory").setExecutor(new Commands());
        getCommand("give-ingredient").setExecutor(new Commands());
        getCommand("toggle-sounds").setExecutor(new Commands());
        getCommand("test-crate").setExecutor(new Commands());
        getCommand("spawn-crate").setExecutor(new Commands());
        getCommand("populate-crate").setExecutor(new Commands());
        getCommand("translate").setExecutor(new Commands());
        getCommand("recaps").setExecutor(new Commands());
        getCommand("kmenu").setExecutor(new Commands());
        getCommand("close-end").setExecutor(new Commands());

        // Config
        ConfigData.setup();
        if (ConfigData.get().get("scheduler") == null) {
            ConfigData.get().options().copyDefaults(true);
            ConfigData.get().addDefault("combatlog", true);
            ConfigData.get().addDefault("combattag", true);
            ConfigData.get().addDefault("potions", true);
            ConfigData.get().addDefault("itemhistory", true);
            ConfigData.get().addDefault("accessories", true);
            ConfigData.get().addDefault("crates", true);
            ConfigData.get().addDefault("scheduler", true);
        }
        PersistentData.setup();
        AccessoryData.setup();
        ClaimData.setup();
        WorldData.setup();
        RecapData.setup();
        ScheduleConfig.setup();
        SMPScheduler.initializeScheduleConfig();
        if (ConfigData.get().getBoolean("scheduler")) {
            SMPScheduler.initializeScheduler();
        }
        // Plugin Methods
        if (ConfigData.get().getBoolean("crates")) {
            CrateMethods.startRandomCrates(getServer().getWorld("world"));
        }

        // Database
//        StatDB.connect();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        AccessoryData.save();
    }
}
