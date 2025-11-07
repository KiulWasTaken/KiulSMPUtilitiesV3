package kiul.kiulsmputilitiesv3;

import com.sun.jna.platform.unix.solaris.LibKstat;
import kiul.kiulsmputilitiesv3.accessories.*;
import kiul.kiulsmputilitiesv3.banneditems.BannedItemListener;
import kiul.kiulsmputilitiesv3.combatlog.LogoutListeners;
import kiul.kiulsmputilitiesv3.combatlog.MovementListeners;
import kiul.kiulsmputilitiesv3.combattag.FightLogicListeners;
import kiul.kiulsmputilitiesv3.combattag.RecapInventory;
import kiul.kiulsmputilitiesv3.config.*;
import kiul.kiulsmputilitiesv3.crates.CrateListeners;
import kiul.kiulsmputilitiesv3.crates.CrateMethods;
import kiul.kiulsmputilitiesv3.featuretoggle.FeatureInventory;
import kiul.kiulsmputilitiesv3.itemhistory.ItemMethods;
import kiul.kiulsmputilitiesv3.itemhistory.listeners.ItemCraft;
import kiul.kiulsmputilitiesv3.itemhistory.listeners.ItemPickupAfterDeath;
import kiul.kiulsmputilitiesv3.scheduler.SMPScheduler;
import kiul.kiulsmputilitiesv3.server_events.CloseEndDimension;
import kiul.kiulsmputilitiesv3.server_events.FinalFight;
import kiul.kiulsmputilitiesv3.stats.StatDB;
import kiul.kiulsmputilitiesv3.stats.StatDBListeners;
import kiul.kiulsmputilitiesv3.teamcure.CureListener;
import kiul.kiulsmputilitiesv3.towns.Town;
import kiul.kiulsmputilitiesv3.towns.listeners.ProtectedBlocks;
import kiul.kiulsmputilitiesv3.towns.listeners.ProtectedEntities;
import kiul.kiulsmputilitiesv3.towns.listeners.TownBlock;
import kiul.kiulsmputilitiesv3.towns.listeners.TownGUI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
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
        getServer().getPluginManager().registerEvents(new FinalFight(),this);
        getServer().getPluginManager().registerEvents(new BannedItemListener(),this);
        getServer().getPluginManager().registerEvents(new CureListener(),this);
        getServer().getPluginManager().registerEvents(new ProtectedEntities(),this);

        getServer().getPluginManager().registerEvents(new TownGUI(),this);
        getServer().getPluginManager().registerEvents(new TownBlock(),this);
        getServer().getPluginManager().registerEvents(new ProtectedBlocks(),this);
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

        ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE);
        ShapelessRecipe sr = new ShapelessRecipe(new NamespacedKey(C.plugin, "light_apple"), goldenApple);
        sr.addIngredient(Material.APPLE);
        sr.addIngredient(Material.GOLD_INGOT);
        sr.addIngredient(Material.GOLD_INGOT);
        sr.addIngredient(Material.GOLD_INGOT);
        sr.addIngredient(Material.GOLD_INGOT);
        Bukkit.addRecipe(sr);

        List<String> lore = new ArrayList<>();
        ItemStack townCore = new ItemStack(Material.RESPAWN_ANCHOR);
        ItemMeta townCoreMeta = townCore.getItemMeta();
        lore.add(ChatColor.GRAY+"Can be placed to create a safe zone for your team.");
        townCoreMeta.setLore(lore);
        townCoreMeta.setDisplayName(C.t("&eTown Core"));
        townCoreMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING,"towncore");
        townCore.setItemMeta(townCoreMeta);
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(C.plugin,"town_core"),townCore);
        shapedRecipe.shape("XXX","XAX","XXX");
        shapedRecipe.setIngredient('X',Material.CRYING_OBSIDIAN);
        shapedRecipe.setIngredient('A',Material.GOLD_BLOCK);
        Bukkit.addRecipe(shapedRecipe);

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
        getCommand("debug-event").setExecutor(new Commands());
        getCommand("debug-event").setTabCompleter(new Commands());

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
            ConfigData.get().addDefault("ban_item", false);
            ConfigData.get().addDefault("curing", true);
        }
        PersistentData.setup();
        AccessoryData.setup();
        ClaimData.setup();
        WorldData.setup();
        if (WorldData.get().get("final_fight") == null) {
            WorldData.get().set("final_fight", false);
            WorldData.get().set("border_closed",false);
            WorldData.save();
        }
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

        new BukkitRunnable() {
            @Override
            public void run() {
                Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
                for (Team team : sb.getTeams()) {
                    C.plugin.getLogger().warning(team.getName());
                    continue;
                }
                if (C.plugin.getConfig().getConfigurationSection("towns") != null) {
                    for (String key : C.plugin.getConfig().getConfigurationSection("towns").getKeys(false)) {
                        Town.townsList.add(Town.loadFromConfig(key));
                    }
                }
            }
        }.runTaskLater(C.plugin,10);

        // Database
//        StatDB.connect();
    }

    @Override
    public void onDisable() {
        for (Town town : Town.townsList) {
            Town.saveToConfig(town);
        }
        for (Entity disabledEntity : ProtectedEntities.disabledEntities) {
            if (disabledEntity instanceof LivingEntity livingEntity) {
                livingEntity.setAI(true);
            }
            disabledEntity.setNoPhysics(false);
            disabledEntity.setGravity(true);
        }
        for (Block regeneratingBlock : ProtectedBlocks.regeneratingBlocks) {
            regeneratingBlock.setType(ProtectedBlocks.regeneratingBlockFinalTypeHash.get(regeneratingBlock));
        }

        // Plugin shutdown logic
        AccessoryData.save();
    }
}
