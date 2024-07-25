//package kiul.kiulsmputilitiesv3.potions;
//
//import io.papermc.paper.event.player.AsyncChatCommandDecorateEvent;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.format.NamedTextColor;
//import net.kyori.adventure.text.minimessage.MiniMessage;
//import org.bukkit.ChatColor;
//import org.bukkit.Color;
//import org.bukkit.Material;
//import org.bukkit.inventory.BrewerInventory;
//import org.bukkit.inventory.ItemFlag;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.PotionMeta;
//import org.bukkit.potion.PotionEffect;
//import org.bukkit.potion.PotionEffectType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CustomPotionUpgrade extends BrewAction {
//    @Override
//    public void brew(BrewerInventory inventory, ItemStack item, ItemStack ingredient) {
//
//        if (!(item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION)) {
//            return;
//        }
//        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
//        if (!(potionMeta.getCustomEffects().get(0).getType() == PotionEffectType.HASTE ||
//                potionMeta.getCustomEffects().get(0).getType() == PotionEffectType.LUCK)) {
//            return;
//        }
//        switch (ingredient.getType()) {
//            case REDSTONE:
//                if (potionMeta.getCustomEffects().get(0).getAmplifier() < 1) {
//                    PotionEffectType customEffectType = potionMeta.getCustomEffects().get(0).getType();
//                    int duration = 9600;
//                    if (customEffectType.equals(PotionEffectType.LUCK)) {
//                        duration = 4800;
//                    }
//                    potionMeta.clearCustomEffects();
//                    potionMeta.addCustomEffect(new PotionEffect(customEffectType,duration,0,false,true),true);
//                }
//                break;
//            case GLOWSTONE_DUST:
//                PotionEffectType customEffectType = potionMeta.getCustomEffects().get(0).getType();
//                if (customEffectType.equals(PotionEffectType.LUCK) || item.getItemMeta().getDisplayName().contains("Purity")) {
//                    potionMeta.getLore().clear();
//                    List<String> lore = new ArrayList<>();
//                    lore.add(ChatColor.RESET + "" + ChatColor.BLUE + "Instant Purity");
//                    lore.add("");
//                    lore.add(ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "When Applied:");
//                    lore.add(ChatColor.RESET + "" + ChatColor.BLUE + "Clears active negative potion effects");
//                    potionMeta.setLore(lore);
//
//                    potionMeta.clearCustomEffects();
//                    potionMeta.addCustomEffect(new PotionEffect(customEffectType,200,1,false,true),true);
//                    item.setItemMeta(potionMeta);
//                    return;
//                }
//                if (potionMeta.getCustomEffects().get(0).getDuration() <= 4800) {
//                    int duration = 1800;
//                    potionMeta.clearCustomEffects();
//                    potionMeta.addCustomEffect(new PotionEffect(customEffectType,duration,1,false,true),true);
//                }
//                break;
//
//            case GUNPOWDER:
//                item.setType(Material.SPLASH_POTION);
//                potionMeta.setDisplayName(ChatColor.WHITE+"Splash "+item.getItemMeta().getDisplayName());
//                break;
//        }
//
//        item.setItemMeta(potionMeta);
//
//    }
//}
