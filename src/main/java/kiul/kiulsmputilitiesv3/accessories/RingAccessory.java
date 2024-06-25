package kiul.kiulsmputilitiesv3.accessories;

import io.papermc.paper.event.player.PlayerPurchaseEvent;
import io.papermc.paper.event.player.PlayerTradeEvent;
import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RingAccessory implements Listener {
    // 10
    // 70
    // 150
    // 250

    @EventHandler
    public void baseEffect (PlayerInteractEntityEvent e) {
        if (AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).contains("ring") && !AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).contains("ruby")) {
            if (e.getRightClicked() instanceof Villager v) {
                e.getPlayer().sendMessage(v.getVillagerExperience() + "");
                if (e.getPlayer().hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)) {
                    return;
                }

                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 600, 0, true, false));
            }
        }
    }

    HashMap<MerchantRecipe,Villager> customVillagerGUI = new HashMap<>();
    @EventHandler
    public void rubyCreateGUI (PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Villager) {
            if (AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).equalsIgnoreCase("ring_ruby")) {
                e.setCancelled(true);
                Villager v = (Villager) e.getRightClicked();
                Merchant merchant = Bukkit.createMerchant(v.getName());
                merchant.setRecipes(v.getRecipes());
                for (int i = 0; i < v.getRecipeCount(); i++) {
                    ItemStack result = v.getRecipe(i).getResult();
                    result.setAmount(2);
                    ItemStack price = new ItemStack(Material.EMERALD);
                    price.setAmount(v.getRecipe(i).getIngredients().get(0).getAmount()*3);
                    MerchantRecipe recipe = new MerchantRecipe(v.getRecipe(i).getResult(),v.getRecipe(i).getUses(),12,true,v.getRecipe(i).getVillagerExperience(),v.getRecipe(i).getPriceMultiplier());
                    List<ItemStack> ingredients = new ArrayList<>();
                    ingredients.add(price);
                    if (v.getRecipe(i).getIngredients().get(1) != null) {
                        ingredients.add(v.getRecipe(i).getIngredients().get(1));
                    }
                    recipe.setIngredients(ingredients);
                    recipe.adjust(price);
                    merchant.setRecipe(i,recipe);
                    customVillagerGUI.put(merchant.getRecipe(i),v);
                    e.getPlayer().sendMessage(recipe +"");
                }



                e.getPlayer().openMerchant(merchant,true);
            }
        }
    }
    @EventHandler
    public void rubyTrade (PlayerPurchaseEvent e) {

            if (AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).equalsIgnoreCase("ring_ruby")) {
                if (customVillagerGUI.containsKey(e.getTrade())) {
                    Villager v = customVillagerGUI.get(e.getTrade());
                    v.setVillagerExperience(v.getVillagerExperience()+e.getTrade().getVillagerExperience());
                    for (MerchantRecipe recipes : v.getRecipes()) {
                        if (e.getTrade().getResult().equals(recipes.getResult())) {
                            recipes.setUses(recipes.getUses()+1);
                        }
                    }
                }

            }
    }
    @EventHandler
    public void peridotEffect (EntitySpawnEvent e) {
        if (e.getEntity() instanceof ExperienceOrb exp) {
            if (exp.getSpawnReason().equals(ExperienceOrb.SpawnReason.VILLAGER_TRADE)) {
                for (Entity nearbyEntity : exp.getNearbyEntities(5,5,5)) {
                    if (nearbyEntity instanceof Player p) {
                        if (AccessoryMethods.getActiveAccessoryIdentifier(p).equalsIgnoreCase("ring_peridot")) {
                            exp.setExperience((int)(exp.getExperience()*1.5));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void tanzaniteEffect (PlayerTradeEvent e) {
        if (AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).equalsIgnoreCase("ring_tanzanite")) {

            Villager villager = (Villager) e.getVillager();
            Player p = e.getPlayer();
            villager.setVillagerExperience(villager.getVillagerExperience()+e.getTrade().getVillagerExperience());
        }
    }



    @EventHandler
    public void opalEffect (PlayerPurchaseEvent e) {
        if (AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).equalsIgnoreCase("ring_opal")) {
            if (Math.random() < 0.3) {
                e.setIncreaseTradeUses(false);
            }
        }
    }
}
