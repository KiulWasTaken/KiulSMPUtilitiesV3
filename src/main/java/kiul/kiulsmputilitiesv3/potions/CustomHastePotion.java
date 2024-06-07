package kiul.kiulsmputilitiesv3.potions;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class CustomHastePotion extends BrewAction {
    @Override
    public void brew(BrewerInventory inventory, ItemStack item, ItemStack ingredient) {

        if (!(item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION)) {
            return;
        }
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        if (potionMeta.getBasePotionType() != PotionType.AWKWARD) {
            return;
        }

        potionMeta.setColor(Color.OLIVE);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING,3600,0,false,true),true);
        item.setItemMeta(potionMeta);

    }
}
