package kiul.kiulsmputilitiesv3.combatlog;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.InventoryToBase64;
import kiul.kiulsmputilitiesv3.config.ConfigData;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LogoutListeners implements Listener {

    public static HashMap<Villager, UUID> NPCOwner = new HashMap<>();


    @EventHandler
    public void spawnDummy(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        int combatMultiplier = 1;
        if (!ConfigData.get().getBoolean("combatlog")) {return;}
        if (C.fightManager.playerIsInFight(e.getPlayer())) {combatMultiplier = 2;}
        if (e.getReason() == PlayerQuitEvent.QuitReason.KICKED) {return;}
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) {return;}
        // Spawn NPC and update its displayname to communicate the time until it despawns.
        Location location = e.getPlayer().getLocation();
        World world = location.getWorld();
        int delay = 0;
        if (e.getReason() != PlayerQuitEvent.QuitReason.DISCONNECTED) {
            return;
            // if the player disconnected by ALT+F4 or connection issue, allow them time to reconnect.
            /*
            delay = 20*C.CONNECTION_ISSUE_PROTECTION_SECONDS;

            ArmorStand timer = (ArmorStand) world.spawnEntity(location.add(0,2,0),EntityType.ARMOR_STAND);
            timer.setMarker(true);
            timer.setCustomNameVisible(true);
            timer.setInvulnerable(true);
            timer.setInvisible(true);

            ArmorStand description = (ArmorStand) world.spawnEntity(location.add(0,1.8,0),EntityType.ARMOR_STAND);
            description.setMarker(true);
            description.setCustomNameVisible(true);
            description.setInvulnerable(true);
            description.setInvisible(true);
            description.setCustomName(ChatColor.GRAY+"Allowing Time To Reconnect (Connection Issue)");
            long initialTime = System.currentTimeMillis();
            new BukkitRunnable() {
                int tick = 0;
                @Override
                public void run() {
                    if (tick >= C.CONNECTION_ISSUE_PROTECTION_SECONDS) {
                        description.remove();
                        timer.remove();
                        cancel();
                        return;
                    }
                    int[] timestamps = C.splitTimestamp(initialTime+C.CONNECTION_ISSUE_PROTECTION_SECONDS*1000L);
                    timer.setCustomName(ChatColor.RED+String.format("%02d:%02d:%02d", timestamps[0], timestamps[1], timestamps[2]));

                    tick ++;
                }
            }.runTaskTimer(C.plugin,0,20);*/
        }

        int finalCombatMultiplier = combatMultiplier;
        new BukkitRunnable() {
            @Override
            public void run() {


                Villager npc = (Villager) world.spawnEntity(location, EntityType.VILLAGER);
                npc.setMetadata(e.getPlayer().getDisplayName(), new FixedMetadataValue(C.plugin, "rat"));
                npc.setAI(false);
                npc.setSilent(true);
                npc.setProfession(Villager.Profession.NITWIT);
                npc.setBreed(false);
                npc.setAdult();
                for (AttributeModifier attributeModifier : p.getAttribute(Attribute.MAX_HEALTH).getModifiers()) {
                    npc.getAttribute(Attribute.MAX_HEALTH).addModifier(attributeModifier);
                }
                npc.getAttribute(Attribute.MAX_HEALTH).setBaseValue(e.getPlayer().getAttribute(Attribute.MAX_HEALTH).getBaseValue());
                npc.setHealth(e.getPlayer().getHealth());
                npc.setCanPickupItems(false);
                npc.setRemoveWhenFarAway(false);
                npc.setCustomNameVisible(true);
                npc.getLocation().getChunk().setForceLoaded(true);
                npc.setCustomName(ChatColor.RED + e.getPlayer().getDisplayName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "1 : 30");

                npc.getEquipment().setArmorContents(e.getPlayer().getEquipment().getArmorContents());
                npc.getEquipment().setItemInMainHand(e.getPlayer().getEquipment().getItemInMainHand());
                npc.getEquipment().setItemInOffHand(e.getPlayer().getEquipment().getItemInOffHand());
                npc.getEquipment().setBootsDropChance(0);
                npc.getEquipment().setLeggingsDropChance(0);
                npc.getEquipment().setChestplateDropChance(0);
                npc.getEquipment().setHelmetDropChance(0);
                npc.getEquipment().setItemInMainHandDropChance(0);
                npc.getEquipment().setItemInOffHandDropChance(0);

                NPCOwner.put(npc, e.getPlayer().getUniqueId());

                long npcSpawnTime = System.currentTimeMillis();
                new BukkitRunnable() {
                    long despawnTime = npcSpawnTime + (C.NPC_DESPAWN_SECONDS * 1000 * finalCombatMultiplier);

                    @Override
                    public void run() {
                        if (!npc.isDead()) {
                            if (System.currentTimeMillis() < despawnTime) {
                                npc.setCustomName(ChatColor.RED + e.getPlayer().getDisplayName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + String.format("%02d : %02d",
                                        TimeUnit.MILLISECONDS.toMinutes(despawnTime - System.currentTimeMillis()),
                                        TimeUnit.MILLISECONDS.toSeconds(despawnTime - System.currentTimeMillis()) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(despawnTime - System.currentTimeMillis()))
                                ));
                            } else {
                                npc.getLocation().getChunk().setForceLoaded(false);
                                NPCOwner.remove(npc);
                                npc.remove();
                                cancel();
                            }
                        }
                    }
                }.runTaskTimer(C.plugin, 0, 20);


                // Save the player's inventory before they log out so that you can drop it later if/when their NPC dies.
                PersistentData.get().set(e.getPlayer().getUniqueId() + ".inventory", InventoryToBase64.itemStackArrayToBase64(e.getPlayer().getInventory().getContents()));
                PersistentData.get().set(e.getPlayer().getUniqueId() + ".armour", InventoryToBase64.itemStackArrayToBase64(e.getPlayer().getInventory().getArmorContents()));
                PersistentData.get().set(e.getPlayer().getUniqueId() + ".npc", npc.getUniqueId().toString());
                PersistentData.get().set(e.getPlayer().getUniqueId() + ".flagged", false);
                PersistentData.save();
                if (Bukkit.getEntity(UUID.fromString(PersistentData.get().getString(e.getPlayer().getUniqueId() + ".npc"))) == null) {
                    npc.remove();
                }
            }
        }.runTaskLater(C.plugin,delay);


    }

    @EventHandler
    public void killNPC(EntityDamageByEntityEvent e) {
        if (!ConfigData.get().getBoolean("combatlog")) {return;}
        if (e.getEntity() instanceof Villager) {
            if ((e.getDamager().getType() == EntityType.ARROW || e.getDamager().getType() == EntityType.SPECTRAL_ARROW || e.getDamager().getType() == EntityType.PLAYER || e.getDamager().getType() == EntityType.TNT_MINECART)) {
                boolean allowDamage = false;
                if ((e.getDamager() instanceof Projectile arrow)) {
                    if (arrow.getShooter() instanceof Player) {
                        allowDamage = true;
                    }
                }
                if (e.getDamager() instanceof Player) {
                    allowDamage = true;
                }
                if (e.getDamager() instanceof ExplosiveMinecart) {
                    allowDamage = true;
                }
                if (!allowDamage) {
                    if (NPCOwner.get(e.getEntity()) != null) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
        }
            if (e.getDamager() instanceof Player) {
                if (e.getEntity() instanceof Villager) {
                    if (((Villager) e.getEntity()).getHealth() <= e.getFinalDamage() && ((Villager) e.getEntity()).getEquipment().getItemInMainHand().getType() != Material.TOTEM_OF_UNDYING && ((Villager) e.getEntity()).getEquipment().getItemInOffHand().getType() != Material.TOTEM_OF_UNDYING ) {
                        if (NPCOwner.get(e.getEntity()) != null) {
                            PersistentData.get().set(NPCOwner.get(e.getEntity()) + ".flagged", true);
                            PersistentData.save();

                            // Drop Logger's Inventory
                            World world = e.getDamager().getWorld();
                            Location location = e.getEntity().getLocation();

                            try {
                                for (ItemStack itemStack : InventoryToBase64.itemStackArrayFromBase64(PersistentData.get().getString(NPCOwner.get(e.getEntity()) + ".inventory"))) {
                                    if (itemStack != null) {
                                        world.dropItemNaturally(location, itemStack);
                                    }
                                }

                            } catch (IOException err) {
                                err.printStackTrace();
                            }

                            Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
                            String damagerteamName = "";
                            String entityteamName = "";
                            for (Team team : sb.getTeams()) {
                                if (team.hasEntry(((Player) e.getDamager()).getDisplayName())) {
                                    damagerteamName = team.getPrefix();
                                }
                                if (team.hasEntry(Bukkit.getOfflinePlayer(NPCOwner.get(e.getEntity())).getName())) {
                                    entityteamName = team.getPrefix();
                                }
                            }
                            if (((Player) e.getDamager()).getInventory().getItemInMainHand() != null) {
                                if (((Player) e.getDamager()).getInventory().getItemInMainHand().getItemMeta() != null) {
                                    if (((Player) e.getDamager()).getInventory().getItemInMainHand().getItemMeta().getDisplayName() != null) {
                                        if (((Player) e.getDamager()).getInventory().getItemInMainHand().getItemMeta().getDisplayName().length() > 0) {
                                            Bukkit.broadcastMessage(entityteamName + ChatColor.RESET + Bukkit.getOfflinePlayer(NPCOwner.get(e.getEntity())).getName() + " was slain by " + damagerteamName + ChatColor.RESET + ((Player) e.getDamager()).getDisplayName() + " using " + ChatColor.AQUA + "[" + ChatColor.RESET + ((Player) e.getDamager()).getInventory().getItemInMainHand().getItemMeta().getDisplayName() + ChatColor.AQUA + "]" + ChatColor.RESET);
                                            return;
                                        }
                                    }
                                }
                            }
                            Bukkit.broadcastMessage(entityteamName + ChatColor.RESET + Bukkit.getOfflinePlayer(NPCOwner.get(e.getEntity())).getName() + " was slain by " + damagerteamName + ChatColor.RESET + ((Player) e.getDamager()).getDisplayName());
                        }
                    }
                }
            }
        }

    @EventHandler
    public void punishCombatLogger (PlayerJoinEvent e) {
        if (!ConfigData.get().getBoolean("combatlog")) {
            PersistentData.get().set(e.getPlayer().getUniqueId() + ".flagged",false);
            PersistentData.save();
            return;
        }
        if (PersistentData.get().getBoolean(e.getPlayer().getUniqueId() + ".flagged")) {
            e.getPlayer().getInventory().clear();
            e.getPlayer().setHealth(0);
            PersistentData.get().set(e.getPlayer().getUniqueId() + ".flagged",false);
            PersistentData.save();
        }
        if (PersistentData.get().getString(e.getPlayer().getUniqueId() + ".npc") != null) {
            if (Bukkit.getEntity(UUID.fromString(PersistentData.get().getString(e.getPlayer().getUniqueId() + ".npc"))) != null) {
                if (((LivingEntity)Bukkit.getEntity(UUID.fromString(PersistentData.get().getString(e.getPlayer().getUniqueId() + ".npc")))).getHealth() < e.getPlayer().getAttribute(Attribute.MAX_HEALTH).getValue()) {
                    e.getPlayer().setHealth(((LivingEntity) Bukkit.getEntity(UUID.fromString(PersistentData.get().getString(e.getPlayer().getUniqueId() + ".npc")))).getHealth());
                }
                Bukkit.getEntity(UUID.fromString(PersistentData.get().getString(e.getPlayer().getUniqueId() + ".npc"))).remove();
                PersistentData.get().set(e.getPlayer().getUniqueId() + ".npc", null);
                PersistentData.save();

            }
        }
    }


    @EventHandler
    public void clearNPCDrops (EntityDeathEvent e) {
        if (NPCOwner.get(e.getEntity()) != null) {
            e.getDrops().clear();
            e.setDroppedExp(0);
        }
    }
    @EventHandler
    public void preventNPCCombust (EntityCombustEvent e) {
        if (NPCOwner.get(e.getEntity()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void sendOfflineKillMessage (PlayerDeathEvent e) {
        if (e.getDeathMessage().equalsIgnoreCase(e.getEntity().getDisplayName() + " died")) {
            e.setDeathMessage(e.getEntity().getDisplayName() + " realised their fate");
        }
    }
}


