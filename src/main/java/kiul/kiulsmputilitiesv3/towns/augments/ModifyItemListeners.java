package kiul.kiulsmputilitiesv3.towns.augments;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.ConfigData;
import kiul.kiulsmputilitiesv3.itemhistory.ItemMethods;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static kiul.kiulsmputilitiesv3.towns.augments.AugmentEnum.getRomanNumeral;

public class ModifyItemListeners implements Listener {

    // required to maintain the fake enchantment lore in augmented items.

    @EventHandler
    public void enchantDate (EnchantItemEvent e) {
        ItemStack preItemStack = e.getItem();
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack postItemStack = e.getItem();
                AugmentEnum.repairItemLore(preItemStack,postItemStack);
            }
        }.runTaskLater(C.plugin,1);
    }

    private static boolean hasLocalName(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(C.plugin, "local"), PersistentDataType.STRING);
    }

    @EventHandler
    public static void applyLoreRepairToAnvilResult(PrepareAnvilEvent e) {

        ItemStack slot0 = e.getInventory().getItem(0);
        ItemStack slot1 = e.getInventory().getItem(1);
        ItemStack result = e.getResult();

        if (slot0 == null || slot1 == null || result == null) return;

        boolean augmentInSlot1 = false;

        ItemStack preItemStack = null;    // augmented item
        ItemStack postSourceItem = result;

        // Check slot0 for augment
        if (hasLocalName(slot0)) {
            preItemStack = slot0;
        }
        // Check slot1 for augment
        else if (hasLocalName(slot1)) {
            preItemStack = slot1;
            augmentInSlot1 = true;  // <-- important
        }

        if (preItemStack == null) return;

        // -----------------------
        // FIND THE AUGMENT ENUM
        // -----------------------
        AugmentEnum augment = null;
        ItemMeta preMeta = preItemStack.getItemMeta();

        if (preMeta != null) {
            PersistentDataContainer pdc = preMeta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(C.plugin, "local");

            if (pdc.has(key, PersistentDataType.STRING)) {
                String localName = pdc.get(key, PersistentDataType.STRING);
                for (AugmentEnum a : AugmentEnum.values()) {
                    if (a.getLocalName().equalsIgnoreCase(localName)) {
                        augment = a;
                        break;
                    }
                }
            }
        }

        if (augment == null) return;


        // -----------------------
        // APPLY LORE RECONSTRUCTION
        // -----------------------
        ItemMeta postMeta = postSourceItem.getItemMeta();
        ItemMeta resultMeta = result.getItemMeta();
        if (resultMeta == null || postMeta == null) return;

        List<String> lore = resultMeta.hasLore()
                ? new ArrayList<>(resultMeta.getLore())
                : new ArrayList<>();

        // Remove enchant lines equal to pre-item’s enchant count
        int removalCount = preMeta.getEnchants().size();
        for (int i = 0; i < removalCount && i < lore.size(); i++) {
            lore.remove(0);
        }

        // Add augment title if no prerequisite
        if (augment.getPrerequisite() == null) {
            lore.add(0, augment.getTitle());
        }

        // Add enchants from post item (as gray text)
        for (Enchantment ench : postMeta.getEnchants().keySet()) {
            if (augment.getPrerequisite() == null && ench.equals(Enchantment.PROTECTION) && postMeta.getEnchants().get(ench) == 1) {
                continue;
            }
            if (ench.equals(augment.getPrerequisite())) {
                lore.add(0, augment.getTitle()+ ChatColor.DARK_GRAY + " (" + getRomanNumeral(postMeta.getEnchants().get(ench)) +")");
                continue;
            }

            PlainTextComponentSerializer plain = PlainTextComponentSerializer.plainText();
            String raw = plain.serialize(ench.displayName(postMeta.getEnchants().get(ench)));

            lore.add(0, ChatColor.GRAY + raw);
        }

        // --------------------------------------------
        // NEW RULES WHEN AUGMENT IS IN SLOT 1
        // --------------------------------------------
        if (augmentInSlot1) {
            // Hide enchants
            resultMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            // Append augment lore at the BOTTOM
            int squeeze = resultMeta.getEnchants().size();
            lore.add(squeeze,"");
            lore.addAll(squeeze,augment.getLore());
            lore.add(squeeze,augment.getTitle());
            lore.add(squeeze,"");
        }

        resultMeta.setLore(lore);
        result.setItemMeta(resultMeta);
        e.setResult(result);
    }

    @EventHandler
    public void grindstoneUse (PrepareGrindstoneEvent e) {

        ItemStack preItem = null;

        for (int i = 0; i < 2; i++) {
            ItemStack preItemStack = e.getInventory().getItem(i);
            if (preItemStack == null) continue;
            for (AugmentEnum augmentEnum : AugmentEnum.values()) {
                if (preItemStack.getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"))) {
                    String localName = preItemStack.getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING);
                    if (augmentEnum.getLocalName().equalsIgnoreCase(localName)) {
                        preItem = preItemStack;
                    }
                }
            }
        }
        if (preItem == null) return;
        ItemStack postItemStack = new ItemStack(preItem.getType());
        e.setResult(postItemStack);
    }
}
