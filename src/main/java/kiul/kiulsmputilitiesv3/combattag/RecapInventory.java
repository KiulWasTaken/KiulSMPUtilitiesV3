package kiul.kiulsmputilitiesv3.combattag;

import com.sun.source.tree.Tree;
import it.unimi.dsi.fastutil.Hash;
import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class RecapInventory implements Listener {

    public static String b(String playerUUID,String path) {
        Map<String,Object> map = PersistentData.get().getConfigurationSection(path).getValues(false);
        for (String uuidStrings : map.keySet()) {
            if (map.get(uuidStrings) instanceof Integer) {
                int value = (Integer) map.get(uuidStrings);
                double finalValue = value;
                map.put(uuidStrings,finalValue);
            }
        }
        TreeMap<String,Double> tree= new TreeMap<>((HashMap) map);
        if (tree.get(playerUUID) >= tree.lastEntry().getValue()) {
            return "&6";
        }
        return "&f";
    }

    public static String damageDealtDelta(String path,String pUUID) {
        ArrayList<String> participants = (ArrayList<String>) PersistentData.get().getList(path + ".participants");
        HashMap<String, Double> damageTaken = (HashMap) PersistentData.get().getConfigurationSection(path + ".damagetaken").getValues(false);
        HashMap<String, Double> damageDealt = (HashMap) PersistentData.get().getConfigurationSection(path + ".damagedealt").getValues(false);
        TreeMap<String,Double> damageDealtDelta = new TreeMap<>();
        for (String playerUUID : participants) {
            Double damageDealtDeltaValue = damageDealt.get(playerUUID) - damageTaken.get(playerUUID);
            damageDealtDelta.put(playerUUID,damageDealtDeltaValue);
        }
        if (damageDealtDelta.get(pUUID) >= damageDealtDelta.lastEntry().getValue()) {
            return "&6";
        }

        return "&f";
    }

    public static void open(Player p, int page) {

        int invSize = 54;
        String pattern = "dd/MM/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Inventory inventory = Bukkit.createInventory(p, invSize, "Fight Recaps");
        List<ItemStack> items = new ArrayList<>();
        p.sendMessage(Component.text("Reading data..").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));
        if (PersistentData.get().getConfigurationSection("recaps").getKeys(false) != null) {
            new BukkitRunnable() {
                List<String> configurationSection = new ArrayList<>(PersistentData.get().getConfigurationSection("recaps").getKeys(false));
                int i = 0;

                @Override
                public void run() {
                    if (i < configurationSection.size()) {
                        String fightUUID = configurationSection.get(i);
                        ArrayList<String> lore = new ArrayList<>();
                        String path = "recaps." + fightUUID;
                        Bukkit.broadcastMessage(path + ".participants");
                        if (PersistentData.get().getList(path + ".participants").contains(p.getUniqueId().toString())) {
                            ArrayList<String> participants = (ArrayList<String>) PersistentData.get().getList(path + ".participants");
                            HashMap<String, Double> hits = (HashMap) PersistentData.get().getConfigurationSection(path + ".hits").getValues(false);
                            HashMap<String, Double> damageTaken = (HashMap) PersistentData.get().getConfigurationSection(path + ".damagetaken").getValues(false);
                            HashMap<String, Double> damageDealt = (HashMap) PersistentData.get().getConfigurationSection(path + ".damagedealt").getValues(false);
                            TreeMap<String, Long> joinTime = new TreeMap<>((HashMap) PersistentData.get().getConfigurationSection(path + ".jointime").getValues(false));
                            TreeMap<String, Long> leaveTime = new TreeMap<>((HashMap) PersistentData.get().getConfigurationSection(path + ".leavetime").getValues(false));
                            TreeMap<String, Long> dieTime = new TreeMap<>((HashMap) PersistentData.get().getConfigurationSection(path + ".dietime").getValues(false));
                            ArrayList<Long> timestamps = new ArrayList<>();
                            timestamps.addAll(joinTime.values());
                            timestamps.addAll(dieTime.values());
                            timestamps.addAll(leaveTime.values());
                            Collections.sort(timestamps);
                            Long startTime = PersistentData.get().getLong(path + ".starttime");
                            Long endTime = PersistentData.get().getLong(path + ".endtime");
                            java.util.Date time = new java.util.Date(startTime);

                            Double damageDealtDelta = (double)damageDealt.get(p.getUniqueId().toString()) - (double)damageTaken.get(p.getUniqueId().toString());
                            int[] times = C.splitTimestampManual(startTime, endTime);
                            String displayName = C.t("&f" + df.format(time));

                            lore.add(C.t("&fParticipants &8▸ " + participants.toString()));
                            lore.add(C.t("&fDuration &8▸ " + String.format("%02d:%02d:%02d", times[0], times[1], times[2])));
                            lore.add(C.t("&fHits &8▸ " + b(p.getUniqueId().toString(), path + ".hits") + hits.get(p.getUniqueId())));
                            lore.add(C.t("&fDamage &8▸ " + b(p.getUniqueId().toString(), path + ".damagedealt") + C.twoPointDecimal.format((double)damageDealt.get(p.getUniqueId().toString()))));
                            lore.add(C.t("&fDDΔ &8▸ " + damageDealtDelta(path, p.getUniqueId().toString()) + C.twoPointDecimal.format(damageDealtDelta)));
                            lore.add("");
                            lore.add(C.t("&7⎯⎯⎯⎯⎯ &fTimeline &7⎯⎯⎯⎯⎯"));
                            for (int i = 0; i < timestamps.size(); i++) {
                                Long timestamp = timestamps.get(i);
                                if (joinTime.containsValue(timestamp)) {
                                    String playerUUID = C.getKeyByValue(joinTime, timestamp);
                                    int[] joinTimes = C.splitTimestampManual(startTime, timestamp);
                                    lore.add(C.t("&f" + String.format("%02d:%02d:%02d", joinTimes[0], joinTimes[1], joinTimes[2]) + " &7▸&f " + Bukkit.getPlayer(UUID.fromString(playerUUID)).getDisplayName() + "&a ↓"));
                                }
                                if (dieTime.containsValue(timestamp)) {
                                    String playerUUID = C.getKeyByValue(dieTime, timestamp);
                                    int[] joinTimes = C.splitTimestampManual(startTime, timestamp);
                                    lore.add(C.t("&f" + String.format("%02d:%02d:%02d", joinTimes[0], joinTimes[1], joinTimes[2]) + " &7▸&f " + Bukkit.getPlayer(UUID.fromString(playerUUID)).getDisplayName() + "&c ☠"));
                                }
                                if (leaveTime.containsValue(timestamp)) {
                                    String playerUUID = C.getKeyByValue(leaveTime, timestamp);
                                    int[] joinTimes = C.splitTimestampManual(startTime, timestamp);
                                    lore.add(C.t("&f" + String.format("%02d:%02d:%02d", joinTimes[0], joinTimes[1], joinTimes[2]) + " &7▸&f " + Bukkit.getPlayer(UUID.fromString(playerUUID)).getDisplayName() + "&e ↑"));
                                }
                            }
                            String participantName = p.getDisplayName();
                            for (int i = 0; i < participants.size(); i++) {
                                if (UUID.fromString(participants.get(i)) != p.getUniqueId()) {
                                    participantName = Bukkit.getPlayer(UUID.fromString(participants.get(i))).getDisplayName();
                                }
                            }
                            for (int i = 0; i < lore.size(); i++) {
                                String line = lore.get(i);
                                if (line.length() > 30) {
                                    String newLine = line.substring(30, line.length());
                                    lore.add(i + 1, newLine);
                                }
                            }
                            String[] arr = new String[lore.size()];

                            // Converting ArrayList to Array
                            // using get() method
                            for (int i = 0; i < lore.size(); i++) {
                                arr[i] = lore.get(i);
                            }

                        ItemStack fightItem = C.createHead(displayName, Material.PLAYER_HEAD, 1, arr, fightUUID, null, participantName);
                            items.add(fightItem);
                        }
                        i++;
                    } else {
                        if (p.hasPermission("kiulsmp.recaps")) {
                            for (int i = 1; i <= 9; i++) {
                                inventory.setItem(invSize - i, C.createItemStack(" ", Material.BLACK_STAINED_GLASS_PANE, 1, new String[]{""}, null, null, null, null));
                            }
                            ItemStack pagePlus = C.createItemStack(C.t("&6Page " + (page + 2)), Material.SPECTRAL_ARROW, 1, new String[]{""}, null, null, "arrow", null);
                            ItemStack pageMinus = C.createItemStack(C.t("&6Page " + (page)), Material.SPECTRAL_ARROW, 1, new String[]{""}, null, null, "arrow", null);
                            inventory.setItem(invSize - 1, pagePlus);
                            if (page > 0) {
                                inventory.setItem(invSize - 9, pageMinus);
                            }

                            int slot = 0;
                            for (int i = slot+(page*45); i < items.size(); i++) {
                                inventory.setItem(slot, items.get(slot));
                                slot++;
                                if (slot >= 45) {
                                    p.openInventory(inventory);
                                    cancel();
                                    return;
                                }
                            }
                        } else {
                            Inventory newInventory = Bukkit.createInventory(p, 9, "Fight Recaps");
                            for (int i = 1; i <= 9; i++) {
                                newInventory.setItem(9 - i, C.createItemStack(" ", Material.BLACK_STAINED_GLASS_PANE, 1, new String[]{""}, null, null, null, null));
                            }
                            if (items.size() > 0) {
                                newInventory.setItem(5, items.get(items.size() - 1));
                                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.2F, 0.5F);
                                p.openInventory(newInventory);
                            } else {
                                p.sendMessage(Component.text("No recaps to view!").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC));
                            }
                            cancel();
                            return;
                        }
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.2F, 0.5F);
                        p.openInventory(inventory);
                        cancel();
                    }
                }
            }.runTaskTimer(C.plugin, 0, 1);
        } else {
            p.sendMessage(Component.text("No recaps to view!").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC));
        }

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
