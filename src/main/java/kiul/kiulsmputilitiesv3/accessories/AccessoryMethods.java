package kiul.kiulsmputilitiesv3.accessories;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.InventoryToBase64;
import kiul.kiulsmputilitiesv3.advancements.AdvancementEnum;
import kiul.kiulsmputilitiesv3.advancements.AdvancementMethods;
import kiul.kiulsmputilitiesv3.config.AccessoryData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AccessoryMethods {
    public static HashMap<Player,BukkitTask> trackingSignalTask = new HashMap<>();

    public static void giveAccessory (Player p,String identifier) {
        for (AccessoryItemEnum item : AccessoryItemEnum.values()) {
            if (item.getLocalName().equals(identifier)) {
                p.getInventory().setItemInMainHand(item.getAccessory());
                return;
            }
        }
    }
    public static void giveIngredient (Player p,String identifier) {
        for (IngredientItemEnum item : IngredientItemEnum.values()) {
            if (item.getLocalName().equals(identifier)) {
                p.getInventory().setItemInMainHand(item.getIngredient());
                return;
            }
        }
    }


    public static void equipAccessory (Player p) {
        if (AccessoryData.get().getLong(p.getUniqueId() + ".accessory.cooldown") < System.currentTimeMillis()) {
            if (((ArrayList<String>)AccessoryData.get().get(p.getUniqueId() + ".accessory.equipped")).size() < 5 && !((ArrayList<String>)AccessoryData.get().get(p.getUniqueId() + ".accessory.equipped")).contains(p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING))) {
                // substitute 5 out for an individual maxAmount var later.
                if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
                    for (AccessoryItemEnum item : AccessoryItemEnum.values()) {
                        if (item.getLocalName().equalsIgnoreCase(p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING))) {
                            ArrayList<String> equippedAccessoryIdentifiers = ((ArrayList<String>)AccessoryData.get().get(p.getUniqueId() + ".accessory.equipped"));
                            equippedAccessoryIdentifiers.add(item.getLocalName());
                            AccessoryData.get().set(p.getUniqueId() + ".accessory.equipped", equippedAccessoryIdentifiers);
                            AccessoryData.get().set(p.getUniqueId() + ".accessory.range", item.getRange());
                            AccessoryData.get().set(p.getUniqueId() + ".accessory.tracking-multiplier", item.getTrackingMultiplier());
                            AccessoryData.save();
                            ItemStack itemStack = p.getInventory().getItemInMainHand();
                            NamespacedKey key = new NamespacedKey(C.plugin, ChatColor.stripColor(itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size()-1)));
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();
                            double foundValue = tagContainer.getCustomTag(key, ItemTagType.DOUBLE);
                            if (item.getAttribute() != null) {
                                p.getAttribute(item.getAttribute()).addModifier(new AttributeModifier(item.getAttribute().name(),(foundValue/100), AttributeModifier.Operation.ADD_NUMBER));
                            }

                            if (item.getLocalName().equals("tome_peridot")) {
                                TomeAccessory.peridotEffect(p);
                            }

                            if (trackingSignalTask.get(p) == null) {
                                instantiateTrackingSignalTask(p);
                            }
                            AdvancementMethods.grantAdvancement(p, AdvancementEnum.ACTIVATE_ACCESSORY.getIdentifier());
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName() + ChatColor.GRAY + " - " + ChatColor.GREEN + "ACTIVE"));
                            itemMeta.setEnchantmentGlintOverride(true);
                            p.getInventory().getItemInMainHand().setItemMeta(itemMeta);
                            p.playSound(p, Sound.BLOCK_ANVIL_LAND, 0.1f, 2f);

                            return;
                        }
                    }
                }
            } else {
                ArrayList<String> equippedAccessoryIdentifiers = ((ArrayList<String>)AccessoryData.get().get(p.getUniqueId() + ".accessory.equipped"));
                if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    for (AccessoryItemEnum accessoryItemEnum : AccessoryItemEnum.values()) {
                        if (equippedAccessoryIdentifiers.contains(accessoryItemEnum.getLocalName())) {
                            if (accessoryItemEnum.getAttribute() != null) {
                                for (AttributeModifier attributeModifiers : p.getAttribute(accessoryItemEnum.getAttribute()).getModifiers()) {
                                    p.getAttribute(accessoryItemEnum.getAttribute()).removeModifier(attributeModifiers);
                                }
                            }
                        }
                    }
                    ItemStack itemStack = p.getInventory().getItemInMainHand();
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setEnchantmentGlintOverride(false);
                    p.getInventory().getItemInMainHand().setItemMeta(itemMeta);

                    equippedAccessoryIdentifiers.remove(p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING));
                    AccessoryData.get().set(p.getUniqueId() + ".accessory.equipped", equippedAccessoryIdentifiers);
                    AccessoryData.get().set(p.getUniqueId() + ".accessory.item", null);
                    AccessoryData.get().set(p.getUniqueId() + ".accessory.cooldown", System.currentTimeMillis()+C.ACCESSORY_COOLDOWN_MINUTES*1000*60);
                    AccessoryData.save();

                    p.playSound(p, Sound.BLOCK_ANVIL_LAND, 0.1f, 2f);
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName() + ChatColor.GRAY + " - " + ChatColor.RED + "DISABLED"));
                }
            }
        } else {
            long cooldownTime = AccessoryData.get().getLong(p.getUniqueId() + ".accessory.cooldown");
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Accessory Cooldown"+ ChatColor.GRAY + " - " + ChatColor.RED + ChatColor.UNDERLINE + String.format("%02d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(cooldownTime - System.currentTimeMillis()),
                    TimeUnit.MILLISECONDS.toSeconds(cooldownTime - System.currentTimeMillis()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(cooldownTime - System.currentTimeMillis())))));
        }
    }

    public static String getActiveAccessoryIdentifier (Player p) {
        try {
            if (InventoryToBase64.itemStackFromBase64(AccessoryData.get().getString(p.getUniqueId() + ".accessory.item")) != null) {
                return InventoryToBase64.itemStackFromBase64(AccessoryData.get().getString(p.getUniqueId() + ".accessory.item")).getItemMeta().getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING);
            }
        } catch (IOException err) {
            err.printStackTrace();
        }
    return "none";}

    public static Double getActiveAccessoryModifier (Player p) {
        try {
            if (InventoryToBase64.itemStackFromBase64(AccessoryData.get().getString(p.getUniqueId() + ".accessory.item")) != null) {
                ItemStack itemStack = InventoryToBase64.itemStackFromBase64(AccessoryData.get().getString(p.getUniqueId() + ".accessory.item"));
                NamespacedKey key = new NamespacedKey(C.plugin, ChatColor.stripColor(itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size()-1)));
                ItemMeta itemMeta = itemStack.getItemMeta();
                CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();

                if(tagContainer.hasCustomTag(key , ItemTagType.DOUBLE)) {
                    double foundValue = tagContainer.getCustomTag(key, ItemTagType.DOUBLE);
                    return foundValue;
                }
            }
        } catch (IOException err) {
            err.printStackTrace();
        }
        return 0.0;}

    public static void instantiateTrackingSignalTask(Player p) {

        int range = AccessoryData.get().getInt(p.getUniqueId() + ".accessory.range");

        final BukkitTask runnable = new BukkitRunnable() {

            @Override
            public void run() {
                if (p.isOnline() && AccessoryData.get().get(p.getUniqueId()+".accessory.identifier") != null && C.ACCESSORIES_ENABLED) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        Team onlinePlayerTeam = C.getPlayerTeam(onlinePlayer);
                        Team pTeam = C.getPlayerTeam(p);
                        boolean isMe = onlinePlayer.equals(p);
                        if (AccessoryData.get().getBoolean(onlinePlayer.getUniqueId()+".sounds.self")) {
                            isMe = false;
                        }
                        if (!isMe && AccessoryData.get().getBoolean(onlinePlayer.getUniqueId()+".sounds.all")) {
                            if (pTeam != null) {
                                if (onlinePlayerTeam != null) {
                                    if (C.getPlayerTeam(onlinePlayer).getName() == C.getPlayerTeam(p).getName()) {
                                        if (!AccessoryData.get().getBoolean(onlinePlayer.getUniqueId() + ".sounds.teammates")) {
                                            return;
                                        }
                                    }
                                }
                                if (C.getPlayerTeam(p) != C.getPlayerTeam(onlinePlayer)) {
                                    if (!AccessoryData.get().getBoolean(onlinePlayer.getUniqueId()+".sounds.enemies")) {
                                        return;
                                    }
                                }
                            }
                            if (onlinePlayer.getWorld() == p.getWorld()) {

                                int trackingMultiplier = AccessoryData.get().getInt(onlinePlayer.getUniqueId() + ".accessory.tracking-multiplier");
                                Location pLocation = new Location(p.getWorld(),p.getLocation().getX(),0,p.getLocation().getZ());
                                Location onlinePlayerLocation = new Location(onlinePlayer.getWorld(),onlinePlayer.getLocation().getX(),0,onlinePlayer.getLocation().getZ());
                                double distance = pLocation.distance(onlinePlayerLocation);

                                if (distance < range * trackingMultiplier) {
//                                Location soundDirection = onlinePlayer.getEyeLocation().add((onlinePlayer.getEyeLocation().getDirection().subtract(p.getEyeLocation().getDirection())).multiply(12 * (distance / range)).toLocation(onlinePlayer.getWorld()));
                                    Location initialLocation = onlinePlayer.getLocation();
                                    Location targetLocation = p.getLocation();

                                    // Calculate the direction from initial player to target player
                                    Vector direction = targetLocation.toVector().subtract(initialLocation.toVector()).normalize();

                                    // Move three blocks in the calculated direction
                                    Location soundDirection = initialLocation.add(direction.multiply(12 * (distance / range)));
                                    onlinePlayer.playSound(soundDirection, Sound.ENTITY_WARDEN_HEARTBEAT, (float) (0.7 + (range / distance) / 10), 1f);
                                }
                            }
                        }
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(C.plugin,0,80);
        trackingSignalTask.put(p,runnable);
    }
}
