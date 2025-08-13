package kiul.kiulsmputilitiesv3.towns.listeners;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.towns.Town;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TownGUI implements Listener {

    HashMap<Player,Long> interactCooldown = new HashMap<>();

    @EventHandler
    public void openTownGUI (PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getClickedBlock() == null) return;
        if (!e.getClickedBlock().getType().equals(Material.RESPAWN_ANCHOR)) return;
        if (e.getAction().isRightClick()) e.setCancelled(true);
        if (interactCooldown.get(p) != null && interactCooldown.get(p) > System.currentTimeMillis()) return;
        interactCooldown.put(p,System.currentTimeMillis()+500);
        for (Town town : Town.townsList) {
            if (town.getTownCenter().distance(e.getClickedBlock().getLocation()) <= 1) {
                if (town.getOwningTeam().equals(C.getPlayerTeam(e.getPlayer()))) {
                    if (town.isInvulnerable()) {
                        confirmReactivateTown(p);
                        return;
                    }
                    if (town.isDisabled() && town.getDisabledUntil() < System.currentTimeMillis()) {
                        confirmReinstateTown(p);
                        return;
                    }

                    if (e.getPlayer().isSneaking()) {
                        new AnvilGUI.Builder()
                                .onClickAsync((slot, stateSnapshot) -> CompletableFuture.supplyAsync(() -> {
                                    // this code is now running async
                                    if (slot != AnvilGUI.Slot.OUTPUT) {
                                        return Collections.emptyList();
                                    }
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            String name = C.t(stateSnapshot.getOutputItem().getItemMeta().getDisplayName());
                                            town.getTownNameStand().setCustomName(town.getOwningTeam().getPrefix()+name);
                                        }
                                    }.runTask(C.plugin);
                                    return AnvilGUI.Response.close();
                                }))
                                .text("To color use Hex: &#123456 Or &a,&b, etc.") // Default text
                                .title("Enter New Town Name") // Requires Minecraft 1.20+ for title support
                                .plugin(C.plugin) // Replace with your plugin instance
                                .open(p);
                        return;
                    }
                    if (p.getInventory().getItemInMainHand().getType().equals(Material.PLAYER_HEAD)) {
                        ItemStack playerHead = p.getInventory().getItemInMainHand();
                        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
                        if (!town.getCollectedSkulls().contains(skullMeta.getOwningPlayer().getUniqueId().toString())) {
                            town.increaseTownCharge(3);
                            town.setTownMaxHealth(town.getTownMaxHealth()+500);
                            town.getCollectedSkulls().add(skullMeta.getOwningPlayer().getUniqueId().toString());
                            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
                        }
                        return;
                    }


                    p.sendActionBar(C.t(C.PURPLE+"\uD83D\uDEE1 "+C.LIGHT_PURPLE+town.getTownHealth()+ ChatColor.GRAY+"/"+C.PURPLE+town.getTownMaxHealth()));
                    p.sendMessage("");
                    ArrayList<String> tipsList = new ArrayList<>();
                    tipsList.add(ChatColor.GRAY+C.t("&oSneak click to change the name of the town"));
                    tipsList.add(ChatColor.GRAY+C.t("&oWhen charge is full, right-click anchor to collect rewards"));
                    tipsList.add(ChatColor.GRAY+C.t("&oPut player skulls into the town core to increase its max health, and get &r"+C.GOLD+"charge"+C.YELLOW+" ⚡"));
                    tipsList.add(ChatColor.GRAY+C.t("&oEvery team member that logs in each day gives your anchor a small amount of &r"+C.GOLD+"charge"+C.YELLOW+" ⚡"));
                    tipsList.add(ChatColor.GRAY+C.t("&oWhen charge is full, right-click anchor to collect rewards"));
                    tipsList.add(ChatColor.GRAY+C.t("&oOnly &nunique &r&7&oplayer skulls will give you rewards when put into the town core"));
                    tipsList.add(ChatColor.GRAY+C.t("&oPlacing beacons or elder guardians inside your town will spread the effect to the entire protected radius"));
                    tipsList.add(ChatColor.GRAY+C.t("&oBlocks and containers broken inside a town by enemy players will regenerate after a short while"));
//                    tipsList.add(ChatColor.GRAY+C.t("&oYou can only get " +C.FUCHSIA+"&o★ Rare &r&7Enchantments from fully charged anchor loot, or from crates."));
//                    tipsList.add(ChatColor.GRAY+C.t("&oApply " +C.FUCHSIA+"&o★ Rare &r&7Enchantments by dropping them onto the anchor with the item you want to apply it to"));
//                    tipsList.add(ChatColor.GRAY+C.t("&oWhile applying a " +C.FUCHSIA+"&o★ Rare &r&7Enchantment to an item, enemies can steal it off the anchor if they can make it there"));
//                    tipsList.add(ChatColor.GRAY+C.t("&oApplying " +C.FUCHSIA+"&o★ Rare &r&7Enchantments announces it to the whole server, and takes longer depending on the amount of players online"));
                    p.sendMessage(tipsList.get(new Random().nextInt(0, tipsList.size())));

                }
            }
        }
    }


    public void confirmReactivateTown(Player p) {
        Inventory hopperInv = Bukkit.createInventory(p, InventoryType.HOPPER, "Begin Town Defense?");
        if (Town.getTownForPlayer(p) == null) {
            return;
        }
        Town town = Town.getTownForPlayer(p);
        List<String> emptylore = new ArrayList<>();
        emptylore.add("");

        for (int i = 0; i < hopperInv.getSize(); i++) {
            hopperInv.setItem(i, C.createItemStack("", Material.GRAY_STAINED_GLASS_PANE, 1, new String[]{""}, null, null, null,null));
        }

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"Town invulnerability ends immediately. Be ready to defend!");
        lore.add(ChatColor.GRAY+"Town HP is immediately regenerated to 40% and regeneration starts.");
        lore.add(ChatColor.GRAY+"invulnerability at 10% HP will not activate again unless town regenerates to >95% HP.");
        lore.add("");
        lore.add(C.GREEN+"Town health will take ~3 hours to regenerate to full from when you click this.");
        ItemStack yesItem = C.createItemStack(C.successPrefix+"Start Regenerating",Material.LIME_TERRACOTTA,1,lore.toArray(String[]::new),null,null,"yes",null);





        lore.clear();
        int[] timestamps = C.splitTimestamp(town.getInvulnerableUntil());
        lore.add(ChatColor.GRAY+"Your town will continue to be invulnerable for "+timestamps[0]+"h "+timestamps[1]+"m.");
        lore.add(ChatColor.GRAY+"Town health will not resume regenerating until invulnerability ends.");
        lore.add(ChatColor.GRAY+"When it begins regenerating, it will start at 10% HP and town");
        lore.add(ChatColor.GRAY+"invulnerability at 10% HP will not activate again unless town regenerates to >95% HP.");
        lore.add("");
        lore.add(C.RED+"Town health will take ~5 hours to regenerate to full from 10% after invulnerabilty ends");
        ItemStack noItem = C.createItemStack(C.failPrefix+"Delay Town Regeneration",Material.RED_TERRACOTTA,1,lore.toArray(String[]::new),null,null,"no",null);

        // Slot 1 for Yes, Slot 3 for No
        hopperInv.setItem(3, yesItem);
        hopperInv.setItem(1, noItem);

        p.openInventory(hopperInv);
    }

    public void confirmReinstateTown(Player p) {
        Inventory hopperInv = Bukkit.createInventory(p, InventoryType.HOPPER, "Reinstate Town?");
        if (Town.getTownForPlayer(p) == null) {
            return;
        }
        Town town = Town.getTownForPlayer(p);
        List<String> emptylore = new ArrayList<>();
        emptylore.add("");

        for (int i = 0; i < hopperInv.getSize(); i++) {
            hopperInv.setItem(i, C.createItemStack("", Material.GRAY_STAINED_GLASS_PANE, 1, new String[]{""}, null, null, null,null));
        }

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"All town blocks that were broken after shield failure will be replaced, and");
        lore.add(ChatColor.GRAY+"Town will be set back to 100% shield. Containers will not regenerate with their");
        lore.add(ChatColor.GRAY+"contents, and valuable blocks will not regenerate.");
        ItemStack yesItem = C.createItemStack(C.successPrefix+"Reinstate Town",Material.LIME_TERRACOTTA,1,lore.toArray(String[]::new),null,null,"yes",null);





        lore.clear();
        int[] timestamps = C.splitTimestamp(town.getInvulnerableUntil());
        lore.add(ChatColor.GRAY+"Town remains disabled and no blocks are regenerated.");
        lore.add(ChatColor.GRAY+"If you want to destroy your town, you can do so at any time by breaking the core");
        ItemStack noItem = C.createItemStack(C.failPrefix+"Do Not Reinstate",Material.RED_TERRACOTTA,1,lore.toArray(String[]::new),null,null,"no",null);

        // Slot 1 for Yes, Slot 3 for No
        hopperInv.setItem(3, yesItem);
        hopperInv.setItem(1, noItem);

        p.openInventory(hopperInv);
    }

    @EventHandler
    public void confirmRegenerateTown (InventoryClickEvent e) {
        Player p = (Player) e.getView().getPlayer();
        if (e.getView().getTitle().equalsIgnoreCase("Begin Town Defense?")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING)) {
                String localName = e.getCurrentItem().getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"),PersistentDataType.STRING);
                switch (localName) {
                    case "yes":
                        if (Town.getTownForPlayer(p) == null) {
                            p.closeInventory();
                            return;
                        }
                        Town town = Town.getTownForPlayer(p);
                        town.setInvulnerableUntil(System.currentTimeMillis());
                        town.setTownHealth((Town.DEFAULT_TOWN_MAX_HEALTH/10)*4);
                        p.playSound(town.getTownCenter(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,50f,1.2f);
                        town.updateTownStatus();
                        p.closeInventory();
                        break;
                    case "no":
                        p.closeInventory();
                        break;
                }
            }
        }
    }

    @EventHandler
    public void confirmReinstateTown (InventoryClickEvent e) {
        Player p = (Player) e.getView().getPlayer();
        if (e.getView().getTitle().equalsIgnoreCase("Reinstate Town?")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING)) {
                String localName = e.getCurrentItem().getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"),PersistentDataType.STRING);
                switch (localName) {
                    case "yes":
                        if (Town.getTownForPlayer(p) == null) {
                            p.closeInventory();
                            return;
                        }
                        Town town = Town.getTownForPlayer(p);
                        town.reinstate();
                        town.updateTownStatus();
                        p.playSound(town.getTownCenter(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,50f,1.2f);
                        p.closeInventory();
                        break;
                    case "no":
                        p.closeInventory();
                        break;
                }
            }
        }
    }
}
