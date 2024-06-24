package kiul.kiulsmputilitiesv3.accessories;

import io.papermc.paper.event.player.PlayerPurchaseEvent;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;

public class RingAccessory implements Listener {

    @EventHandler
    public void baseEffect (PlayerInteractEntityEvent e) {
        if (AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).contains("ring") && !AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).contains("ruby")) {
            if (e.getRightClicked() instanceof Villager) {
                if (e.getPlayer().hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)) {
                    return;
                }
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 600, 0, true, false));
            }
        }
    }
    @EventHandler
    public void rubyEffect (PlayerTradeEvent e) {
        if (AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).equalsIgnoreCase("ring_ruby")) {
            e.getTrade().getIngredients().get(0).setAmount(e.getTrade().getIngredients().get(0).getAmount()*4);
            e.getTrade().getResult().setAmount(e.getTrade().getResult().getAmount()*2);
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
            ((Villager) e.getVillager()).setVillagerExperience(((Villager) e.getVillager()).getVillagerExperience()+e.getTrade().getVillagerExperience()*2);
            e.getPlayer().openMerchant((Villager) e.getVillager(),true);
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
