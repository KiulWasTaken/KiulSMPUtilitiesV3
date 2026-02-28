package kiul.kiulsmputilitiesv3.locatorbar;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import kiul.kiulsmputilitiesv3.towns.Town;
import kiul.kiulsmputilitiesv3.towns.listeners.TownGUIEnum;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class LocatorConfigInventory implements Listener {

    public static void open (Player p) {

        Inventory inventory = Bukkit.createInventory(p, InventoryType.HOPPER, "Locator Bar Config");
        List<String> emptylore = new ArrayList<>();
        emptylore.add("");

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, C.createItemStack("", Material.GRAY_STAINED_GLASS_PANE, 1, new String[]{}, null, null, null, null));
        }

        for (LocatorConfigInventoryEnum item : LocatorConfigInventoryEnum.values()) {
            String suffix = C.failPrefix;
            if (PersistentData.get().getBoolean("locatorbar." + p.getUniqueId() + "." + item.getlocalName())) {
                suffix = C.successPrefix;
            }
            inventory.setItem(item.getInventorySlot(), C.createItemStack(item.getDisplayName()+" "+suffix, item.getMaterial(), 1, item.getLore(), null, null, item.getlocalName(), item.getSkullValue()));

        }
        p.openInventory(inventory);
    }

    @EventHandler
    public void clickLocatorConfigGUI (InventoryClickEvent e) {
        Player p = (Player) e.getView().getPlayer();
        if (e.getInventory().getHolder() instanceof BlockInventoryHolder) return;
        if (e.getView().getTitle().equalsIgnoreCase("Locator Bar Config")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getPersistentDataContainer().has(new NamespacedKey(C.plugin, "local"), PersistentDataType.STRING)) {
                String localName = e.getCurrentItem().getPersistentDataContainer().get(new NamespacedKey(C.plugin, "local"), PersistentDataType.STRING);
                switch (localName) {
                    case "show_towns":
                        if (PersistentData.get().getBoolean("locatorbar." + p.getUniqueId() + ".show_towns")) {
                            // disable
                            LocatorBar.disable_show_towns.add(p);
                            PersistentData.get().set("locatorbar." + p.getUniqueId() + ".show_towns",false);
                            p.sendMessage(C.GOLD+"LOCATOR_BAR "+C.YELLOW+localName.toUpperCase() + C.GOLD + " has been set to " + C.RED + "FALSE");
                        } else {
                            LocatorBar.disable_show_towns.remove(p);
                            PersistentData.get().set("locatorbar." + p.getUniqueId() + ".show_towns",true);
                            p.sendMessage(C.GOLD+"LOCATOR_BAR "+C.YELLOW+localName.toUpperCase() + C.GOLD + " has been set to " + C.GREEN + "TRUE");
                        }
                        PersistentData.saveAsync();
                        break;
                    case "share_self":
                        if (PersistentData.get().getBoolean("locatorbar." + p.getUniqueId() + ".share_self")) {
                            // disable
                            LocatorBar.disable_show_self.add(p);
                            PersistentData.get().set("locatorbar." + p.getUniqueId() + ".share_self",false);
                            p.sendMessage(C.GOLD+"LOCATOR_BAR "+C.YELLOW+localName.toUpperCase() + C.GOLD + " has been set to " + C.RED + "FALSE");
                        } else {
                            LocatorBar.disable_show_self.remove(p);
                            PersistentData.get().set("locatorbar." + p.getUniqueId() + ".share_self",true);
                            p.sendMessage(C.GOLD+"LOCATOR_BAR "+C.YELLOW+localName.toUpperCase() + C.GOLD + " has been set to " + C.GREEN + "TRUE");
                        }
                        PersistentData.saveAsync();
                        break;
                    case "show_teammates":
                        if (PersistentData.get().getBoolean("locatorbar." + p.getUniqueId() + ".show_teammates")) {
                            // disable
                            LocatorBar.disable_show_teammates.add(p);
                            PersistentData.get().set("locatorbar." + p.getUniqueId() + ".show_teammates",false);
                            p.sendMessage(C.GOLD+"LOCATOR_BAR "+C.YELLOW+localName.toUpperCase() + C.GOLD + " has been set to " + C.RED + "FALSE");
                        } else {
                            LocatorBar.disable_show_teammates.remove(p);
                            PersistentData.get().set("locatorbar." + p.getUniqueId() + ".show_teammates",true);
                            p.sendMessage(C.GOLD+"LOCATOR_BAR "+C.YELLOW+localName.toUpperCase() + C.GOLD + " has been set to " + C.GREEN + "TRUE");
                        }
                        PersistentData.saveAsync();
                        break;
                    case "enabled":
                        if (PersistentData.get().getBoolean("locatorbar." + p.getUniqueId() + ".enabled")) {
                            // disable
                            LocatorBar.playerLocatorBar.remove(p.getUniqueId());
                            PersistentData.get().set("locatorbar." + p.getUniqueId() + ".enabled",false);
                            p.sendMessage(C.GOLD+"LOCATOR_BAR "+C.YELLOW+localName.toUpperCase() + C.GOLD + " has been set to " + C.RED + "FALSE");
                        } else {
                            PersistentData.get().set("locatorbar." + p.getUniqueId() + ".enabled",true);
                            p.sendMessage(C.GOLD+"LOCATOR_BAR "+C.YELLOW+localName.toUpperCase() + C.GOLD + " has been set to " + C.GREEN + "TRUE");
                            new LocatorBar(45,p);
                        }
                        PersistentData.saveAsync();
                        break;
                }
                p.closeInventory();
            }
        }
    }
}
