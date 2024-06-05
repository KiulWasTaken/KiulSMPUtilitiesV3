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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
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
        getServer().getPluginManager().registerEvents(new ClaimListeners(),this);
        getServer().getPluginManager().registerEvents(new CrateListeners(),this);
        getServer().getPluginManager().registerEvents(new GluttonyAccessory(),this);
        getServer().getPluginManager().registerEvents(new NimbleAccessory(),this);

        // Recipes
        for (AccessoryItemEnum accessoryItem : AccessoryItemEnum.values()) {
            ItemStack is = new ItemStack(accessoryItem.getAccessory());

            ShapelessRecipe sr = new ShapelessRecipe(new NamespacedKey(C.plugin,accessoryItem.getLocalName()),is);

            RecipeChoice recipeChoice = new RecipeChoice.ExactChoice(IngredientItemEnum.Ruby.getIngredient());
            if (accessoryItem.getLocalName().contains("base")) {

            } else {
                sr.addIngredient(new RecipeChoice.ExactChoice(accessoryItem.getAccessory()));
            }
            if (accessoryItem.getLocalName().contains("ruby")) {
                recipeChoice = new RecipeChoice.ExactChoice(IngredientItemEnum.Ruby.getIngredient());
            }
            if (accessoryItem.getLocalName().contains("tanzanite")) {
                recipeChoice = new RecipeChoice.ExactChoice(IngredientItemEnum.Tanzanite.getIngredient());
            }
            if (accessoryItem.getLocalName().contains("peridot")) {
                recipeChoice = new RecipeChoice.ExactChoice(IngredientItemEnum.Peridot.getIngredient());
            }
            if (accessoryItem.getLocalName().contains("opal")) {
                recipeChoice = new RecipeChoice.ExactChoice(IngredientItemEnum.Opal.getIngredient());
            }

            sr.addIngredient(recipeChoice);
            Bukkit.addRecipe(sr);
        }


        // Dependencies

        // Commands
        getCommand("logout").setExecutor(new Commands());
        getCommand("accessory").setExecutor(new Commands());
        getCommand("give-accessory").setExecutor(new Commands());
        getCommand("toggle-sounds").setExecutor(new Commands());
        getCommand("test-crate").setExecutor(new Commands());
        getCommand("populate-crate").setExecutor(new Commands());
        getCommand("translate").setExecutor(new Commands());

        // Config
        PersistentData.setup();
        AccessoryData.setup();
        ClaimData.setup();

        // Plugin Methods
        ClaimMethods.initializeClaims();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
