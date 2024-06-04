package kiul.kiulsmputilitiesv3.itemhistory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemMethods {

    public static ArrayList<Material> historyItems = new ArrayList<>() {{
        add(Material.NETHERITE_HELMET);
        add(Material.NETHERITE_CHESTPLATE);
        add(Material.NETHERITE_LEGGINGS);
        add(Material.NETHERITE_BOOTS);

        add(Material.NETHERITE_HOE);
        add(Material.NETHERITE_PICKAXE);
        add(Material.NETHERITE_AXE);
        add(Material.NETHERITE_SHOVEL);
        add(Material.NETHERITE_SWORD);

        add(Material.DIAMOND_HELMET);
        add(Material.DIAMOND_CHESTPLATE);
        add(Material.DIAMOND_LEGGINGS);
        add(Material.DIAMOND_BOOTS);

        add(Material.DIAMOND_HOE);
        add(Material.DIAMOND_PICKAXE);
        add(Material.DIAMOND_AXE);
        add(Material.DIAMOND_SHOVEL);
        add(Material.DIAMOND_SWORD);
    }};

    public static void addLore (ItemStack item, String itemLore) {
        ItemStack resultItem = item;
        ItemMeta resultItemMeta = resultItem.getItemMeta();
        List<String> lore;
        if (resultItemMeta.getLore() != null) {
            lore = resultItemMeta.getLore();
        } else {
            lore = new ArrayList<>() {{
                add("");
            }};
        }
        lore.add(itemLore);
        resultItemMeta.setLore(lore);
        resultItem.setItemMeta(resultItemMeta);
    }
}
