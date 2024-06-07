package kiul.kiulsmputilitiesv3.potions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class CustomPurityPotion extends BrewAction {
    @Override
    public void brew(BrewerInventory inventory, ItemStack item, ItemStack ingredient) {

        if (!(item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION)) {
            return;
        }
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        if (potionMeta.getBasePotionType() != PotionType.AWKWARD) {
            return;
        }
        potionMeta.setColor(Color.WHITE);
        potionMeta.displayName(Component.text("Potion of Purity").color(NamedTextColor.AQUA));
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LUCK,20,0,false,false),true);
        potionMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Instant Purity").color(NamedTextColor.BLUE));
        potionMeta.lore(lore);
        item.setItemMeta(potionMeta);

    }
}
