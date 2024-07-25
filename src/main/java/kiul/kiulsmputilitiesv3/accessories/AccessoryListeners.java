package kiul.kiulsmputilitiesv3.accessories;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.InventoryToBase64;
import kiul.kiulsmputilitiesv3.advancements.AdvancementEnum;
import kiul.kiulsmputilitiesv3.advancements.AdvancementMethods;
import kiul.kiulsmputilitiesv3.config.AccessoryData;
import kiul.kiulsmputilitiesv3.stats.StatDB;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static kiul.kiulsmputilitiesv3.accessories.AccessoryMethods.getActiveAccessoryIdentifier;
import static kiul.kiulsmputilitiesv3.accessories.AccessoryMethods.trackingSignalTask;

public class AccessoryListeners implements Listener {

    @EventHandler
    public void accessoryConfigSetup (PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        p.sendMessage(Component.text("§f§a§i§r§x§a§e§r§o")); // disables worldmap caves
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
        if (!C.accessoriesEnabled) {return;}
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
                    if (accessoryItemEnum.getAttribute() != null) {
                        for (AttributeModifier attributeModifiers : p.getAttribute(accessoryItemEnum.getAttribute()).getModifiers()) {
                            p.getAttribute(accessoryItemEnum.getAttribute()).removeModifier(attributeModifiers);
                        }
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
        if (!C.accessoriesEnabled) {return;}
        Player p = (Player) e.getWhoClicked();
        if (e.getInventory() instanceof CraftingInventory) {
            if (e.getCurrentItem() == null) {return;}
            if (e.getCurrentItem().equals(e.getInventory().getItem(0))) {
                if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING)) {
                    for (AccessoryItemEnum accessoryItemEnum : AccessoryItemEnum.values()) {
                        if (accessoryItemEnum.getLocalName().equalsIgnoreCase(e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING))) {
                            AdvancementMethods.grantAdvancement(p, AdvancementEnum.CRAFT_ACCESSORY.getIdentifier());

//                            NamespacedKey key = new NamespacedKey(C.plugin, ChatColor.stripColor(itemStack.getItemMeta().getLore().get(itemStack.getItemMeta().getLore().size() - 1)));
//                            ItemMeta itemMeta = itemStack.getItemMeta();
//                            CustomItemTagContainer tagContainer = itemMeta.getCustomTagContainer();
//
//                            double foundValue = tagContainer.getCustomTag(key, ItemTagType.DOUBLE);
//
//                            if (foundValue > accessoryItemEnum.getMean() + (accessoryItemEnum.getStandardDeviation() * 2)) {
//                                AdvancementMethods.grantAdvancement(p, AdvancementEnum.CRAFT_PERFECT_ACCESSORY.getIdentifier());
//                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void preventPlacement (BlockPlaceEvent e) {
        if (e.getBlock().getType().equals(Material.PLAYER_HEAD)) {
            if (e.getItemInHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void makeItemInvulnerable (PlayerDropItemEvent e) {
        if (!C.accessoriesEnabled) {return;}
        if (e.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING)) {
            e.getItemDrop().setInvulnerable(true);
            e.getItemDrop().setUnlimitedLifetime(true);
            e.getItemDrop().setVisualFire(false);
            if (e.getItemDrop().getItemStack().getType() == Material.DRAGON_EGG) {
                e.getItemDrop().setPickupDelay(12000);
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "EVENT" + ChatColor.RESET + ChatColor.GRAY + " » " + ChatColor.WHITE + "The " + ChatColor.LIGHT_PURPLE + "Dragon Egg" + ChatColor.WHITE + " Has been dropped at the coordinates: " + e.getItemDrop().getLocation().getBlockX() + ", " + e.getItemDrop().getLocation().getBlockY() + ", " + e.getItemDrop().getLocation().getBlockZ());
                Bukkit.broadcastMessage("");
                new BukkitRunnable() {
                    int tick = 0;
                    long dropTime = System.currentTimeMillis();
                    long unlockTime = dropTime + (10 * 1000 * 60);
                    ArmorStand stand = (ArmorStand) e.getPlayer().getWorld().spawnEntity(e.getItemDrop().getLocation(), EntityType.ARMOR_STAND);

                    @Override
                    public void run() {
                        if (!e.getItemDrop().isDead()) {
                            tick++;
                            if (System.currentTimeMillis() < unlockTime && !stand.isDead()) {
                                stand.teleport(e.getItemDrop().getLocation().add(0, 1, 0));
                                stand.setMarker(true);
                                stand.setVisible(false);
                                stand.setCustomNameVisible(true);
                                stand.setInvulnerable(true);
                                stand.setCustomName(ChatColor.GRAY + String.format("%02d : %02d",
                                        TimeUnit.MILLISECONDS.toMinutes(unlockTime - System.currentTimeMillis()),
                                        TimeUnit.MILLISECONDS.toSeconds(unlockTime - System.currentTimeMillis()) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(unlockTime - System.currentTimeMillis()))
                                ));
                            } else {
                                stand.remove();
                                e.getItemDrop().setPickupDelay(20);
                            }


                            if (tick >= 3600) {
                                Bukkit.broadcastMessage(C.eventPrefix + ChatColor.LIGHT_PURPLE + " Dragon egg" + ChatColor.GRAY + " rests at the coordinates " + e.getItemDrop().getLocation().toString());
                                tick = 0;
                            }
                        } else {
                            if (!stand.isDead()) {
                                stand.remove();
                            }
                            cancel();
                        }
                    }
                }.runTaskTimer(C.plugin, 0, 20);
            }
        }
    }
    @EventHandler
    public void preventDeath (EntityDamageEvent e) {
        if (!C.accessoriesEnabled) {return;}
        if (e.getEntity() instanceof Item i && ((Item) e.getEntity()).getItemStack().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING)) {
            e.setCancelled(true);
            if (e.getDamageSource().getDamageType().equals(DamageType.OUT_OF_WORLD)) {
                if (i.getItemStack().getType().equals(Material.DRAGON_EGG)) {
                    i.teleport(Bukkit.getWorld("world").getSpawnLocation());
                }
            }
        }
    }

    @EventHandler
    public void preventDie (ItemSpawnEvent e) {
        if (!C.accessoriesEnabled) {return;}
        if (e.getEntity().getItemStack().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING)) {
            e.getEntity().setInvulnerable(true);
            e.getEntity().setUnlimitedLifetime(true);
            e.getEntity().setVisualFire(false);
        }
    }
}
