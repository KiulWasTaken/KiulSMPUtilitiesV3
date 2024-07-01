package kiul.kiulsmputilitiesv3.combattag;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDate;
import java.util.*;

public class RecapInventory implements Listener {



    public static void open(Player p, int page) {

        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.2F, 0.5F);
        int invSize = 54;
        Inventory inventory = Bukkit.createInventory(p, invSize, "Fight Recaps");
        List<ItemStack> items = new ArrayList<>();
        for (String fightUUID : PersistentData.get().getConfigurationSection("recaps").getKeys(false)) {
            ArrayList<String> lore = new ArrayList<>();
            String path = "recaps."+fightUUID;
            if (PersistentData.get().getList(path+".participants").contains(p.getUniqueId())) {
                ArrayList<UUID> participants = (ArrayList<UUID>) PersistentData.get().getList(path+".participants");
                HashMap<UUID,Double> damageTaken = (HashMap<UUID, java.lang.Double>) PersistentData.get().get(path+".damagetaken");
                HashMap<UUID,Double> damageDealt = (HashMap<UUID, java.lang.Double>) PersistentData.get().get(path+".damagedealt");
                TreeMap<UUID,Long> joinTime = (TreeMap<UUID, Long>) PersistentData.get().get(path+".jointime");
                TreeMap<UUID,Long> leaveTime = (TreeMap<UUID, Long>) PersistentData.get().get(path+".leavetime");
                TreeMap<UUID,Long> dieTime = (TreeMap<UUID, Long>) PersistentData.get().get(path+".dietime");
                ArrayList<Long> timestamps = new ArrayList<>();
                timestamps.addAll(joinTime.values());
                timestamps.addAll(dieTime.values());
                timestamps.addAll(leaveTime.values());
                Collections.sort(timestamps);
                Long startTime = PersistentData.get().getLong(path+".starttime");
                Long endTime = PersistentData.get().getLong(path+".endtime");
                LocalDate cal = LocalDate.ofEpochDay(startTime);

                Double damageDealtDelta = damageDealt.get(p.getUniqueId())-damageTaken.get(p.getUniqueId());
                int[] times = C.splitTimestampManual(startTime,endTime);
                String displayName = C.t("&f"+C.dtf.format(cal));
                lore.add(C.t("&fParticipants &6▸ " + participants.toString()));
                lore.add(C.t("&fDD &6▸ " + damageDealt.get(p.getUniqueId())));
                lore.add(C.t("&fDDΔ &6▸ " + damageDealtDelta));
                lore.add(C.t("&fDuration &6▸ " + String.format("%02d:%02d:%02d",times[0],times[1],times[2])));
                lore.add("");
                lore.add(C.t("&7⎯⎯⎯⎯⎯ &fTimeline &7⎯⎯⎯⎯⎯"));
                for (int i = 0; i <= timestamps.size(); i++) {
                    Long timestamp = timestamps.get(i);
                    if (joinTime.containsValue(timestamp)) {
                        UUID playerUUID = C.getKeyByValue(joinTime,timestamp);
                        int[] joinTimes = C.splitTimestampManual(startTime,timestamp);
                        lore.add(C.t("&f"+String.format("%02d:%02d:%02d",joinTimes[0],joinTimes[1],joinTimes[2]) + " &7▸&f " + Bukkit.getPlayer(playerUUID).getDisplayName() + "&a ↓"));
                    }
                    if (dieTime.containsValue(timestamp)) {
                        UUID playerUUID = C.getKeyByValue(dieTime,timestamp);
                        int[] joinTimes = C.splitTimestampManual(startTime,timestamp);
                        lore.add(C.t("&f"+String.format("%02d:%02d:%02d",joinTimes[0],joinTimes[1],joinTimes[2]) + " &7▸&f " + Bukkit.getPlayer(playerUUID).getDisplayName() + "&c ☠"));
                    }
                    if (leaveTime.containsValue(timestamp)) {
                        UUID playerUUID = C.getKeyByValue(leaveTime,timestamp);
                        int[] joinTimes = C.splitTimestampManual(startTime,timestamp);
                        lore.add(C.t("&f"+String.format("%02d:%02d:%02d",joinTimes[0],joinTimes[1],joinTimes[2]) + " &7▸&f " + Bukkit.getPlayer(playerUUID).getDisplayName() + "&e ↑"));
                    }
                }
                C.createItemStack(displayName,Material.PLAYER_HEAD,1,(String[]) lore.stream().toArray(),null,null,fightUUID,null);

            }
        }

        for (int i = 1; i <= 9; i++) {
            inventory.setItem(invSize - i, C.createItemStack(" ", Material.BLACK_STAINED_GLASS_PANE, 1,new String[]{""}, null, null,null,null));
        }
        ItemStack pagePlus = C.createItemStack(C.t("&6Page " + (page+2)),Material.SPECTRAL_ARROW,1,new String[]{""},null,null,"arrow",null);
        ItemStack pageMinus = C.createItemStack(C.t("&6Page " + (page)),Material.SPECTRAL_ARROW,1,new String[]{""},null,null,"arrow",null);
        inventory.setItem(invSize-1,pagePlus);
        if (page > 0) {
            inventory.setItem(invSize-9,pageMinus);
        }

        int slot = 0+(page*45);
        for (int i = slot; i < items.size(); i++) {
                inventory.setItem(slot, items.get(slot));
                slot++;
                if (slot >= 45) {
                    p.openInventory(inventory);
                    return;
                }
        }
        p.openInventory(inventory);
    }


    @EventHandler
    public void tagInventoryClickEvent (InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory().getHolder() instanceof BlockInventoryHolder) {return;}
        if (e.getView().getTitle().equalsIgnoreCase("Fight Recaps")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {return;}
            if (e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)) {
                open(p, Integer.parseInt(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("Page ", ""))) - 1);
            }
        }
    }
}
