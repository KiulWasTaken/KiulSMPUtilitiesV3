package kiul.kiulsmputilitiesv3.featuretoggle;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class FeatureInventory implements Listener {

    public static void open(Player p,boolean update) {


        int invSize = 45;
        Inventory inventory = Bukkit.createInventory(p, invSize, "Feature Toggle");


        for (int i = 1; i <= 9; i++) {
            inventory.setItem(invSize - i, C.createItemStack(" ", Material.BLACK_STAINED_GLASS_PANE, 1, new String[]{""}, null, null, null, null));
        }


        for (FeatureEnum item : FeatureEnum.values()) {
            List<String> lore = new ArrayList<>();
            for (String itemLore : item.getLore()) {
                lore.add((itemLore));
            }
            lore.add("");


            String itemName = C.t(item.getDisplayName());
            switch (item.getlocalName()) {
                case "potions":
                    lore.add(ChatColor.DARK_GRAY+"Status" + ChatColor.GRAY+ " » "+ChatColor.BOLD+(C.potionsEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                    break;
                case "combattag":
                    lore.add(ChatColor.DARK_GRAY+"Status" + ChatColor.GRAY+ " » "+ChatColor.BOLD+(C.combatTagEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                    break;
                case "combatlog":
                    lore.add(ChatColor.DARK_GRAY+"Status" + ChatColor.GRAY+ " » "+ChatColor.BOLD+(C.combatLogEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                    break;
                case "crates":
                    lore.add(ChatColor.DARK_GRAY+"Status" + ChatColor.GRAY+ " » "+ChatColor.BOLD+(C.cratesEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                    break;
                case "accessories":
                    lore.add(ChatColor.DARK_GRAY+"Status" + ChatColor.GRAY+ " » "+ChatColor.BOLD+(C.accessoriesEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                    break;
                case "itemhistory":
                    lore.add(ChatColor.DARK_GRAY+"Status" + ChatColor.GRAY+ " » "+ChatColor.BOLD+(C.itemHistoryEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                    break;
            }
            String[] arr = new String[lore.size()];

            // Converting ArrayList to Array
            // using get() method
            for (int i = 0; i < lore.size(); i++) {
                arr[i] = lore.get(i);
            }

            inventory.addItem(C.createItemStack(itemName, item.getMaterial(), 1, arr, null, null,item.getlocalName(),null));
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
        if (e.getView().getTitle().equalsIgnoreCase("Feature Toggle")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {return;}
            if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING)) {
                switch (e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"),PersistentDataType.STRING)) {
                    case "potions":
                        C.potionsEnabled = !C.potionsEnabled;
                        p.sendMessage(C.pluginPrefix+e.getCurrentItem().getItemMeta().getDisplayName()+ChatColor.WHITE+" has been " + (C.potionsEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                        break;
                    case "combattag":
                        C.combatTagEnabled = !C.combatTagEnabled;
                        p.sendMessage(C.pluginPrefix+e.getCurrentItem().getItemMeta().getDisplayName()+ChatColor.WHITE+" has been " + (C.combatTagEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                        break;
                    case "combatlog":
                        C.combatLogEnabled = !C.combatLogEnabled;
                        p.sendMessage(C.pluginPrefix+e.getCurrentItem().getItemMeta().getDisplayName()+ChatColor.WHITE+" has been " + (C.combatLogEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                        break;
                    case "crates":
                        C.cratesEnabled = !C.cratesEnabled;
                        p.sendMessage(C.pluginPrefix+e.getCurrentItem().getItemMeta().getDisplayName()+ChatColor.WHITE+" has been " + (C.cratesEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                        break;
                    case "accessories":
                        C.accessoriesEnabled = !C.accessoriesEnabled;
                        p.sendMessage(C.pluginPrefix+e.getCurrentItem().getItemMeta().getDisplayName()+ChatColor.WHITE+" has been " + (C.accessoriesEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                        break;
                    case "itemhistory":
                        C.itemHistoryEnabled = !C.itemHistoryEnabled;
                        p.sendMessage(C.pluginPrefix+e.getCurrentItem().getItemMeta().getDisplayName()+ChatColor.WHITE+" has been " + (C.itemHistoryEnabled ? ChatColor.GREEN+"Enabled":ChatColor.RED+"Disabled"));
                        break;
                }
                open(p,true);
            }
        }
    }
}
