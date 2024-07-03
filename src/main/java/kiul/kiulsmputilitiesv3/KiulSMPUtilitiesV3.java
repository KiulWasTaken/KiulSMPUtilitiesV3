package kiul.kiulsmputilitiesv3;

import kiul.kiulsmputilitiesv3.accessories.*;
import kiul.kiulsmputilitiesv3.claims.ClaimListeners;
import kiul.kiulsmputilitiesv3.claims.ClaimMethods;
import kiul.kiulsmputilitiesv3.combatlog.LogoutListeners;
import kiul.kiulsmputilitiesv3.combatlog.MovementListeners;
import kiul.kiulsmputilitiesv3.combattag.FightLogicListeners;
import kiul.kiulsmputilitiesv3.config.AccessoryData;
import kiul.kiulsmputilitiesv3.config.ClaimData;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import kiul.kiulsmputilitiesv3.crates.CrateListeners;
import kiul.kiulsmputilitiesv3.crates.CrateMethods;
import kiul.kiulsmputilitiesv3.itemhistory.listeners.ItemCraft;
import kiul.kiulsmputilitiesv3.itemhistory.listeners.ItemPickupAfterDeath;
import kiul.kiulsmputilitiesv3.potions.*;
import kiul.kiulsmputilitiesv3.stats.StatDBListeners;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

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
        getServer().getPluginManager().registerEvents(new PotionListeners(),this);
        getServer().getPluginManager().registerEvents(new ItemCraft(),this);
        getServer().getPluginManager().registerEvents(new ItemPickupAfterDeath(),this);
        getServer().getPluginManager().registerEvents(new StatDBListeners(),this);

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


        // Dependencies

        // Commands
        getCommand("logout").setExecutor(new Commands());
        getCommand("accessory").setExecutor(new Commands());
        getCommand("give-accessory").setExecutor(new Commands());
        getCommand("give-ingredient").setExecutor(new Commands());
        getCommand("toggle-sounds").setExecutor(new Commands());
        getCommand("test-crate").setExecutor(new Commands());
        getCommand("populate-crate").setExecutor(new Commands());
        getCommand("translate").setExecutor(new Commands());
        getCommand("recaps").setExecutor(new Commands());

        // Config
        PersistentData.setup();
        AccessoryData.setup();
        ClaimData.setup();

        // Plugin Methods
        CrateMethods.startRandomCrates(getServer().getWorld("world"));
        new BrewingRecipe(Material.GLOW_BERRIES, new CustomHastePotion());
        new BrewingRecipe(Material.NETHERITE_SCRAP, new CustomPurityPotion());
        new BrewingRecipe(Material.GUNPOWDER, new CustomPotionUpgrade());
        new BrewingRecipe(Material.GLOWSTONE_DUST, new CustomPotionUpgrade());
        new BrewingRecipe(Material.REDSTONE, new CustomPotionUpgrade());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
