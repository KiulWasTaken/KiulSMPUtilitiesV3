package kiul.kiulsmputilitiesv3.accessories;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class CraftAccessory implements Listener {

    public ItemStack matchItem (AccessoryItemEnum accessoryItem,IngredientItemEnum ingredientItem) {
        ItemStack result = new ItemStack(Material.AIR);
        String[] stringParts = accessoryItem.getLocalName().split("\\_");
        String typePart = stringParts[0];
        String accessoryType = typePart+"_"+ingredientItem.getLocalName();
        for (AccessoryItemEnum accessoryItems : AccessoryItemEnum.values()) {
            if (accessoryItems.getLocalName().equalsIgnoreCase(accessoryType)) {
                result = accessoryItems.getAccessory();
            }
        }
        return result;
    }

    @EventHandler
    public void craftEvent (PrepareItemCraftEvent e) {
        if (!C.accessoriesEnabled) {return;}
        List<ItemStack> items = Arrays.stream(e.getInventory().getMatrix()).toList();
        AccessoryItemEnum accessoryItemEnum = null;

        for (ItemStack item : items) {

            if (item != null) {
                if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING)) {
                    for (AccessoryItemEnum accessoryItems : AccessoryItemEnum.values()) {
                        if (accessoryItems.getLocalName().contains("base")) {
                            if (item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(C.plugin, "local"), PersistentDataType.STRING).equalsIgnoreCase(accessoryItems.getLocalName())) {
                                accessoryItemEnum = accessoryItems;
                            }
                        }
                    }
                    for (IngredientItemEnum ingredientItems : IngredientItemEnum.values()) {
                        if (item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(C.plugin, "local"), PersistentDataType.STRING).equalsIgnoreCase(ingredientItems.getLocalName()) && accessoryItemEnum != null) {
                            ItemStack result = matchItem(accessoryItemEnum, ingredientItems);
                            e.getInventory().setResult(result);
                        }
                    }
                }
            }
        }
    }
}
