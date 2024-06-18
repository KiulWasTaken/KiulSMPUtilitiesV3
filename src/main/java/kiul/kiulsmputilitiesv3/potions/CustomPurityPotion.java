package kiul.kiulsmputilitiesv3.potions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
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
        potionMeta.setDisplayName(ChatColor.RESET+"Potion of Purity");
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.LUCK,1800,0,false,true),true);
        potionMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET+""+ChatColor.BLUE+"Purity (01:30)");
        lore.add("");
        lore.add(ChatColor.RESET+""+ChatColor.DARK_PURPLE+"When Applied:");
        lore.add(ChatColor.RESET+""+ChatColor.BLUE+"Cannot acquire negative potion effects");
        potionMeta.setLore(lore);

        item.setItemMeta(potionMeta);

    }
}
