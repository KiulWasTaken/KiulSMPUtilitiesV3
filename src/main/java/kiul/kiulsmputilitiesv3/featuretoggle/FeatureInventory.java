package kiul.kiulsmputilitiesv3.featuretoggle;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.banneditems.BannedItemListener;
import kiul.kiulsmputilitiesv3.config.ConfigData;
import kiul.kiulsmputilitiesv3.config.ScheduleConfig;
import kiul.kiulsmputilitiesv3.scheduler.SMPScheduler;
import kiul.kiulsmputilitiesv3.server_events.CloseEndDimension;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeatureInventory implements Listener {

    public static void open(Player p,boolean update) {


        int invSize = 45 + (9);
        Material border = Material.GREEN_STAINED_GLASS_PANE;
        Inventory inventory = Bukkit.createInventory(p, invSize, "Kiul SMP Utilities V3 | Menu");
        List<String> emptylore = new ArrayList<>();
        emptylore.add("");

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, C.createItemStack(" ", Material.GRAY_STAINED_GLASS_PANE, 1, new String[]{}, null, null, null,null));
        }

        int[] slots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 38, 39, 40, 41, 42};
        for (int slot : slots) {
            if (slot < 38) {
                inventory.setItem(slot, C.createItemStack(ChatColor.GRAY + "?", Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1, new String[]{}, null, null, null,null));
            } else {
                inventory.setItem(slot, C.createItemStack(ChatColor.DARK_AQUA + "?", Material.CYAN_STAINED_GLASS_PANE, 1, new String[]{}, null, null, null,null));
            }
        }

        for (int i = 0; i <= invSize/9; i++) {
            if (i*9 < invSize && i*9 >= 0) {
                inventory.setItem(i * 9, C.createItemStack(" ", border, 1, new String[]{}, null, null, null,null));
            }
            if ((i*9)-1 < invSize && (i*9)-1 >= 0) {
                inventory.setItem((i * 9) - 1, C.createItemStack(" ", border, 1, new String[]{}, null, null, null,null));
            }
        }


        for (FeatureEnum item : FeatureEnum.values()) {
            List<String> lore = new ArrayList<>();
            for (String itemLore : item.getLore()) {
                lore.add((itemLore));
            }
            lore.add("");


            String itemName = C.t(item.getDisplayName());
            ItemStack inventoryItem = C.createItemStack(itemName, item.getMaterial(), 1, new String[]{}, null, null,item.getlocalName(),null);
            ItemMeta itemMeta = inventoryItem.getItemMeta();

            switch (item.getlocalName()) {
                case "close_end":
                itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "hours"), PersistentDataType.INTEGER, 1);
                itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "minutes"), PersistentDataType.INTEGER, 0);
                itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "incrementHours"), PersistentDataType.BOOLEAN, true);

                int hours = itemMeta.getPersistentDataContainer().get(new NamespacedKey(C.plugin, "hours"), PersistentDataType.INTEGER);
                int minutes = itemMeta.getPersistentDataContainer().get(new NamespacedKey(C.plugin, "minutes"), PersistentDataType.INTEGER);
                lore.add(C.t("&#89adafLeft-Click &8→ &fIncrement Time &#27a33a↑"));
                lore.add(C.t("&#89adafRight-Click &8→ &fDecrement Time &#e33630↓"));
                lore.add(C.t("&#89adafShift-Right-Click &8→ &fToggle Increment Minutes/Hours ⌛"));
                lore.add(C.t("&#89adafShift-Left-Click &8→ &fStart Event ⛈"));
                lore.add(ChatColor.GRAY+"");
                lore.add(C.GRAY_BLUE + "Schedule end closure to occur in " + hours + " hour(s) and " + minutes + " minute(s)");


                break;
                case "scheduler":
                    lore.add("");
                    if (ScheduleConfig.get().get("start_time") != null) {
                        HashMap<String, int[]> eventTimes = SMPScheduler.getTimeUntilEvents();
                        for (String event : eventTimes.keySet()) {
                            String[] properName = event.split("\\_");
                            String[] finalName = new String[2];
                            finalName[0] = properName[0].substring(0, 1).toUpperCase() + properName[0].substring(1);
                            finalName[1] = properName[1].substring(0, 1).toUpperCase() + properName[1].substring(1);
                            int[] timestamps = eventTimes.get(event);
                            String times = timestamps[0] + " Hours And " + timestamps[1] + " Minutes";
                            lore.add(C.t("&#89adaf" + finalName[0] + " " + finalName[1] + "&f In " + times));

                        }
                    } else {
                        ConfigData.get().set("scheduler",false);
                        ConfigData.save();
                        lore.add(C.t("&#89adafscheduler has not been initialized"));
                        lore.add(C.t("&#89adafmore info will appear when you enable it."));
                    }
                    lore.add("");
                    break;
            }

            if (item.isToggleable()) {
                lore.add(C.t("&#849ea1Status") + ChatColor.DARK_GRAY + " → " + ChatColor.BOLD + (ConfigData.get().getBoolean(item.getlocalName()) ? C.GREEN + "✔" : C.RED + "❌"));
            }

            itemMeta.setLore(lore);
            inventoryItem.setItemMeta(itemMeta);
            inventory.setItem(item.getInventorySlot(),inventoryItem);


        }
        if (update) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1F, 1F);
            p.getOpenInventory().getTopInventory().setContents(inventory.getContents());
        } else {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.2F, 0.5F);
            p.openInventory(inventory);
        }
    }

    @EventHandler
    public void clickToggle (InventoryClickEvent e) {
        Player p = (Player) e.getView().getPlayer();
        if (e.getInventory().getHolder() instanceof BlockInventoryHolder) {return;}
        if (e.getView().getTitle().equalsIgnoreCase("Kiul SMP Utilities V3 | Menu")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {return;}
            if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING)) {
                String localName = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING);
                ConfigData.get().set(localName,!ConfigData.get().getBoolean(localName));
                ConfigData.save();
                switch (localName) {
                    case "close_end":
                        ItemStack itemStack = e.getCurrentItem();
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (itemMeta.getPersistentDataContainer().has(new NamespacedKey(C.plugin, "hours"))) {

                            int hours = itemMeta.getPersistentDataContainer().get(new NamespacedKey(C.plugin, "hours"), PersistentDataType.INTEGER);
                            int minutes = itemMeta.getPersistentDataContainer().get(new NamespacedKey(C.plugin, "minutes"), PersistentDataType.INTEGER);
                            boolean incrementHours = itemMeta.getPersistentDataContainer().get(new NamespacedKey(C.plugin, "incrementHours"), PersistentDataType.BOOLEAN);

                            if (e.getClick() == ClickType.SHIFT_LEFT) {
                                CloseEndDimension.scheduleEndClosure(hours, minutes);
                                long closeTime = System.currentTimeMillis()+(1000L*60*60*hours)+(1000L*60*minutes);
                                int[] timestamps = C.splitTimestamp(closeTime);
                                Bukkit.broadcastMessage("");
                                Bukkit.broadcastMessage(C.eventPrefix + C.GRAY_PINK+"End Dimension is Closing " + ChatColor.WHITE+"in " + C.GRAY_PURPLE + timestamps[0] + ChatColor.WHITE+ " hour(s) and " + C.GRAY_PURPLE + timestamps[1] + ChatColor.WHITE + " minute(s)");
                                Bukkit.broadcastMessage("");
                                p.closeInventory();
                                return;
                            }
                            if (e.getClick() == ClickType.SHIFT_RIGHT) {
                                itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "incrementHours"), PersistentDataType.BOOLEAN, !incrementHours);
                                itemStack.setItemMeta(itemMeta);
                                return;
                            }
                            if (e.getClick() == ClickType.LEFT) {

                                if (incrementHours) {
                                    hours++;
                                    if (hours >= 48) {
                                        hours = 0;
                                        minutes = 0;
                                    }
                                    itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "minutes"), PersistentDataType.INTEGER, minutes);
                                    itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "hours"), PersistentDataType.INTEGER, hours);
                                } else {
                                    if (hours < 48) {
                                        minutes++;
                                        if (minutes > 59) {
                                            minutes = 0;
                                        }
                                        itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "minutes"), PersistentDataType.INTEGER, minutes);
                                    } else {
                                        minutes = 0;
                                        itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "minutes"), PersistentDataType.INTEGER, minutes);
                                    }
                                }

                                List<String> itemMetaLore = itemMeta.getLore();
                                itemMetaLore.set(itemStack.getLore().size() - 1, C.GRAY_BLUE + "Schedule end closure to occur in " + hours + " hour(s) and " + minutes + " minute(s)");
                                itemMeta.setLore(itemMetaLore);
                                itemStack.setItemMeta(itemMeta);
                            } else if (e.getClick() == ClickType.RIGHT) {

                                if (incrementHours) {
                                    hours--;
                                    if (hours < 0) {
                                        hours = 47;
                                        minutes = 59;
                                    }
                                    itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "hours"), PersistentDataType.INTEGER, hours);
                                    itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "minutes"), PersistentDataType.INTEGER, minutes);
                                } else {
                                    if (hours < 48) {
                                        minutes--;
                                        if (minutes < 0) {
                                            minutes = 59;
                                        }
                                        itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "minutes"), PersistentDataType.INTEGER, minutes);
                                    } else {
                                        minutes = 0;
                                        itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin, "minutes"), PersistentDataType.INTEGER, minutes);
                                    }
                                }

                                List<String> itemMetaLore = itemMeta.getLore();
                                itemMetaLore.set(itemStack.getLore().size() - 1, C.GRAY_BLUE + "Schedule end closure to occur in " + hours + " hour(s) and " + minutes + " minute(s)");
                                itemMeta.setLore(itemMetaLore);
                                itemStack.setItemMeta(itemMeta);
                            }
                            return;
                        }
                        break;
                    case "scheduler":
                        if (!ConfigData.get().getBoolean("scheduler")) {
                            // save timestamp
                            ScheduleConfig.get().set("toggle_time",System.currentTimeMillis());
                            ScheduleConfig.save();
                            if (C.smpScheduler != null) {
                                C.smpScheduler.cancel();
                            }
                        } else {
                            if (ScheduleConfig.get().get("toggle_time") != null && ScheduleConfig.get().get("start_time") != null) {
                                long timestamp = ScheduleConfig.get().getLong("toggle_time");
                                long startTime = ScheduleConfig.get().getLong("start_time");
                                long currentTime = System.currentTimeMillis();
                                startTime += currentTime-timestamp;
                                ScheduleConfig.get().set("start_time",startTime);
                                ScheduleConfig.save();
                                SMPScheduler.initializeScheduler();
                                // add currentTime-timestamp to start_time
                            } else {
                                ScheduleConfig.get().set("start_time",System.currentTimeMillis());
                                ScheduleConfig.save();
                                SMPScheduler.initializeScheduler();
                            }
                        }
                        break;
                    case "ban_item":
                        if (e.getClick() == ClickType.SHIFT_LEFT) {
                            Inventory inventory = Bukkit.createInventory(null, 27, "Banned Items");
                            for (Material material : BannedItemListener.bannedItems) {
                                inventory.addItem(new ItemStack(material));
                            }
                            p.openInventory(inventory);
                            return;
                        }
                        break;
                }


                Bukkit.broadcastMessage(C.pluginPrefix+e.getCurrentItem().getItemMeta().getDisplayName()+ChatColor.WHITE+" has been " + (ConfigData.get().getBoolean(localName) ? C.GREEN+"Enabled":C.RED+"Disabled"));

                open(p,true);
            }
        }
    }

    @EventHandler
    public void saveBannedItemsGUI (InventoryCloseEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("banned items")) {
            Player p = (Player) e.getPlayer();
            BannedItemListener.bannedItems.clear();
            for (ItemStack bannedItem : p.getOpenInventory().getTopInventory()) {
                if (bannedItem != null && bannedItem.getType() != Material.AIR) {
                    BannedItemListener.bannedItems.add(bannedItem.getType());
                }
            }
        }
    }
}
