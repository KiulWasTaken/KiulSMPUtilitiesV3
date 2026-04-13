package kiul.kiulsmputilitiesv3.towns.bounty;

import kiul.kiulsmputilitiesv3.config.BountyData;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ItemConsumeListener implements Listener {

    @EventHandler
    public void consumeEnderPearl (ProjectileLaunchEvent e) { // when the player throws an enderpearl, i'm going to add emeralds to their bounty since thats the main way people obtain them.
        if (e.getEntity().getShooter() instanceof Player player && e.getEntity() instanceof EnderPearl) {
            if (BountyData.get().get(player.getUniqueId() + ".emeralds") == null) {
                BountyData.get().set(player.getUniqueId() + ".emeralds",0.5);
            } else {
                BountyData.get().set(player.getUniqueId() + ".emeralds",BountyData.get().getDouble(player.getUniqueId() + ".emeralds")+0.5);
            }
            BountyData.save();
        }
    }
    @EventHandler
    public void consumeBottleOEnchanting (ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player player && e.getEntity() instanceof ThrownExpBottle) {
            if (BountyData.get().get(player.getUniqueId() + ".emeralds") == null) {
                BountyData.get().set(player.getUniqueId() + ".emeralds",0.5);
            } else {
                BountyData.get().set(player.getUniqueId() + ".emeralds",BountyData.get().getDouble(player.getUniqueId() + ".emeralds")+0.5);
            }
            BountyData.save();
        }
    }

    @EventHandler
    public void consumeGoldenApple (PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        if (e.getItem().getType() == Material.GOLDEN_APPLE) { // golden apples give 2 gold back to the bounty counter when consumed, since they take 8 to craft.
            if (BountyData.get().get(player.getUniqueId() + ".gold") == null) {
                BountyData.get().set(player.getUniqueId() + ".gold",2);
            } else {
                BountyData.get().set(player.getUniqueId() + ".gold",BountyData.get().getDouble(player.getUniqueId() + ".gold")+2);
            }
            BountyData.save();
        }
    }

    @EventHandler
    public void craftNetheriteIngot (CraftItemEvent e) {
        Player player = (Player) e.getView().getPlayer();
        if (e.getRecipe().getResult().getType().equals(Material.NETHERITE_INGOT)) {
            if (BountyData.get().get(player.getUniqueId() + ".scrap") == null) {
                BountyData.get().set(player.getUniqueId() + ".scrap",1);
            } else {
                BountyData.get().set(player.getUniqueId() + ".scrap",BountyData.get().getDouble(player.getUniqueId() + ".scrap")+1);
            }
        }
    }
}
