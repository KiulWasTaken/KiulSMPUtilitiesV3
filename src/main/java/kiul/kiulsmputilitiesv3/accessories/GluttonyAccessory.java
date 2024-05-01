package kiul.kiulsmputilitiesv3.accessories;

import kiul.kiulsmputilitiesv3.InventoryToBase64;
import kiul.kiulsmputilitiesv3.config.AccessoryData;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;

public class GluttonyAccessory implements Listener {

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        if (AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).equalsIgnoreCase("gluttony")) {


            int goldenCarrotSaturation = (int)Math.floor(AccessoryMethods.getActiveAccessoryModifier(e.getPlayer()));
            int goldenCarrotSatiation = 6;

            if (e.getItem().getType() != Material.POTION && e.getItem().getType() != Material.GOLDEN_APPLE && e.getItem().getType() != Material.ENCHANTED_GOLDEN_APPLE) {
                e.setCancelled(true);
                e.getPlayer().setSaturation(e.getPlayer().getSaturation() + goldenCarrotSaturation);
                e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel() + goldenCarrotSatiation);
                e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_BURP,1,1);
            }
        }
    }

    @EventHandler
    public void preventEat(PlayerInteractEvent e) {
        if (e.getItem() != null) {
            if (e.getItem().getItemMeta() != null) {
                if (e.getItem().getItemMeta().getLocalizedName().equalsIgnoreCase("gluttony")) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
