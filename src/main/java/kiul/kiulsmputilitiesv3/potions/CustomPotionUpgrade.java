package kiul.kiulsmputilitiesv3.potions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class CustomPotionUpgrade extends BrewAction {
    @Override
    public void brew(BrewerInventory inventory, ItemStack item, ItemStack ingredient) {

        if (!(item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION)) {
            return;
        }
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        if (!(potionMeta.getCustomEffects().get(0).getType() == PotionEffectType.FAST_DIGGING ||
                potionMeta.getCustomEffects().get(0).getType() == PotionEffectType.LUCK)) {
            return;
        }
        switch (ingredient.getType()) {
            case REDSTONE:
                if (potionMeta.getCustomEffects().get(0).getAmplifier() < 1) {
                    PotionEffectType customEffectType = potionMeta.getCustomEffects().get(0).getType();
                    int duration = 9600;
                    if (customEffectType.equals(PotionEffectType.LUCK)) {
                        duration = 4800;
                        potionMeta.lore().clear();
                        potionMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                        List<Component> lore = new ArrayList<>();
                        lore.add(Component.text("Purity (4:00)").color(NamedTextColor.BLUE));
                        potionMeta.lore();
                    }
                    potionMeta.clearCustomEffects();
                    potionMeta.addCustomEffect(new PotionEffect(customEffectType,duration,0,false,true),true);
                }
                break;
            case GLOWSTONE_DUST:
                if (potionMeta.getCustomEffects().get(0).getDuration() <= 4800) {
                    PotionEffectType customEffectType = potionMeta.getCustomEffects().get(0).getType();
                    int duration = 1800;
                    if (customEffectType.equals(PotionEffectType.LUCK)) {
                        return;
                    }
                    potionMeta.clearCustomEffects();
                    potionMeta.addCustomEffect(new PotionEffect(customEffectType,duration,1,false,true),true);
                }
                break;

            case GUNPOWDER:
                item.setType(Material.SPLASH_POTION);
                break;
        }

        item.setItemMeta(potionMeta);

    }
}
