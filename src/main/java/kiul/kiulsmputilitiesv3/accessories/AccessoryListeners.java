package kiul.kiulsmputilitiesv3.accessories;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.InventoryToBase64;
import kiul.kiulsmputilitiesv3.advancements.AdvancementEnum;
import kiul.kiulsmputilitiesv3.advancements.AdvancementMethods;
import kiul.kiulsmputilitiesv3.config.AccessoryData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.io.IOException;
import java.util.UUID;

import static kiul.kiulsmputilitiesv3.accessories.AccessoryMethods.getActiveAccessoryIdentifier;
import static kiul.kiulsmputilitiesv3.accessories.AccessoryMethods.trackingSignalTask;

public class AccessoryListeners implements Listener {

    @EventHandler
    public void accessoryConfigSetup (PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        AccessoryData.get().options().copyDefaults(true);
        AccessoryData.get().addDefault(uuid+".accessory.identifier",null);
        AccessoryData.get().addDefault(uuid+".accessory.item",null);
        AccessoryData.get().addDefault(uuid+".accessory.range",0);
        AccessoryData.get().addDefault(uuid+".accessory.tracking-multiplier",1.0);
        AccessoryData.get().addDefault(uuid+".accessory.cooldown",null);
        AccessoryData.get().addDefault(uuid+".sounds.teammates",false);
        AccessoryData.get().addDefault(uuid+".sounds.enemies",true);
        AccessoryData.get().addDefault(uuid+".sounds.self",true);
        AccessoryData.get().addDefault(uuid+".sounds.all",true);
        AccessoryData.save();

        if (AccessoryData.get().get(uuid+".accessory.identifier") != null) {
            AccessoryMethods.instantiateTrackingSignalTask(p);
        }
    }

    @EventHandler
    public void cancelTaskOnQuit (PlayerQuitEvent e) {
        if (trackingSignalTask.containsKey(e.getPlayer())) {
            trackingSignalTask.get(e.getPlayer()).cancel();
            trackingSignalTask.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void dropAccessoriesOnDeath (PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (getActiveAccessoryIdentifier(p) != null) {
            for (AccessoryItemEnum accessoryItemEnum : AccessoryItemEnum.values()) {
                if (accessoryItemEnum.getLocalName().equalsIgnoreCase(getActiveAccessoryIdentifier(p))) {
                    for (AttributeModifier attributeModifiers : p.getAttribute(accessoryItemEnum.getAttribute()).getModifiers()) {
                        p.getAttribute(accessoryItemEnum.getAttribute()).removeModifier(attributeModifiers);
                    }
                }
            }
            try {
                ItemStack itemStack = InventoryToBase64.itemStackFromBase64(AccessoryData.get().getString(p.getUniqueId() + ".accessory.item"));
                e.getDrops().add(itemStack);
                AccessoryData.get().set(p.getUniqueId() + ".accessory.item",null);
                AccessoryData.get().set(p.getUniqueId() + ".accessory.identifier",null);
                AccessoryData.save();
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }

    @EventHandler
    public void reforgeAccessory (InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory() instanceof CraftingInventory) {
            if (e.getCurrentItem().equals(e.getInventory().getItem(0))) {
                if (e.getCurrentItem().getItemMeta().hasLocalizedName()) {
                    for (AccessoryItemEnum accessoryItemEnum : AccessoryItemEnum.values()) {
                        if (accessoryItemEnum.getLocalName().equalsIgnoreCase(e.getCurrentItem().getItemMeta().getLocalizedName())) {
                            ItemStack itemStack = accessoryItemEnum.getAccessory();
                            e.setCurrentItem(itemStack);
                            AdvancementMethods.grantAdvancement(p, AdvancementEnum.CRAFT_ACCESSORY.getIdentifier());

                            NamespacedKey key = new NamespacedKey(C.plugin, ChatColor.stripColor(itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1)));
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();

                            double foundValue = tagContainer.getCustomTag(key, ItemTagType.DOUBLE);

                            if (foundValue > accessoryItemEnum.getMean() + (accessoryItemEnum.getStandardDeviation() * 2)) {
                                AdvancementMethods.grantAdvancement(p, AdvancementEnum.CRAFT_PERFECT_ACCESSORY.getIdentifier());
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void preventPlacement (BlockPlaceEvent e) {
        if (e.getBlock().getType().equals(Material.PLAYER_HEAD)) {
            if (e.getItemInHand().getItemMeta().hasLocalizedName()) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void preventWearing (InventoryClickEvent e) {
        if (!e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {return;}
        if (!e.getCurrentItem().getItemMeta().hasLocalizedName()) {return;}
        if (e.getClick().isShiftClick()) {e.setCancelled(true);}
        if (e.getSlotType().equals(InventoryType.SlotType.ARMOR) && e.getCursor().equals(e.getCurrentItem())) {e.setCancelled(true);}
    }
}
