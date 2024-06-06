package kiul.kiulsmputilitiesv3.accessories;

import io.papermc.paper.event.player.PlayerPurchaseEvent;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

import java.util.HashMap;
import java.util.List;

public class RingAccessory implements Listener {

    HashMap<Villager,List<MerchantRecipe>> villagerRecipes = new HashMap<>();

    @EventHandler
    public void baseEffect (PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Villager villager) {
            if (villager.getRecipes() == null) {
                return;
            }

            if (AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).contains("ring") && !AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).contains("ruby")) {
                List<MerchantRecipe> villagerRecipeList = villager.getRecipes();
                villagerRecipes.put(villager,villagerRecipeList);
                e.getPlayer().sendMessage("passed");
                for (MerchantRecipe recipes : villager.getRecipes()) {
                    int total = recipes.getIngredients().get(0).getAmount();
                    recipes.setSpecialPrice(-(total / 3));
                    e.getPlayer().sendMessage("trade passed: " + recipes.getSpecialPrice());
                }
            }
        }
    }

    @EventHandler
    public void resetTradesOnExit (InventoryCloseEvent e) {
        if (e.getInventory() instanceof MerchantInventory merchantInventory) {
            merchantInventory.getMerchant().setRecipes(villagerRecipes.get((Villager)merchantInventory.getMerchant()));
        }
    }
}
