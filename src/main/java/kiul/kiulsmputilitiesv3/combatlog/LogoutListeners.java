package kiul.kiulsmputilitiesv3.combatlog;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.InventoryToBase64;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

    private HashMap<Zombie, UUID> NPCOwner = new HashMap<>();

    private HashMap<Chunk,Player> markedChunks = new HashMap<>();


    @EventHandler
    public void spawnDummy(PlayerQuitEvent e) {


        if (!C.loggingOut.contains(e.getPlayer())) {
            // Spawn NPC and update its displayname to communicate the time until it despawns.
            Location location = e.getPlayer().getLocation();
            World world = location.getWorld();
            Zombie npc = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);
            npc.setMetadata(e.getPlayer().getDisplayName(), new FixedMetadataValue(C.plugin, "rat"));
            npc.setAI(false);
            npc.setAdult();
            npc.getAttribute(Attribute.GENERIC_ARMOR).addModifier(new AttributeModifier("GENERIC_ARMOR", e.getPlayer().getAttribute(Attribute.GENERIC_ARMOR).getValue(), AttributeModifier.Operation.ADD_NUMBER));
            npc.setHealth(e.getPlayer().getHealth());
            npc.setCanPickupItems(false);
            npc.setRemoveWhenFarAway(false);
            npc.setCustomNameVisible(true);
            npc.getLocation().getChunk().setForceLoaded(true);
            npc.setCustomName(ChatColor.RED + e.getPlayer().getDisplayName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "90");
            NPCOwner.put(npc, e.getPlayer().getUniqueId());

            long npcSpawnTime = System.currentTimeMillis();
            new BukkitRunnable() {
                long despawnTime = npcSpawnTime + (C.npcDespawnTimeSeconds * 1000);

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
        } else {
            C.loggingOut.remove(e.getPlayer());
        }

    }



    @EventHandler
    public void killNPC(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            if (e.getEntity() instanceof Zombie) {
                if (((Zombie) e.getEntity()).getHealth() < e.getFinalDamage()) {
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
                            for (ItemStack itemStack : InventoryToBase64.itemStackArrayFromBase64(PersistentData.get().getString(NPCOwner.get(e.getEntity()) + ".armour"))) {
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
                                        Bukkit.broadcastMessage(entityteamName + ChatColor.RESET  + Bukkit.getOfflinePlayer(NPCOwner.get(e.getEntity())).getName() + " was slain by " + damagerteamName + ChatColor.RESET  + ((Player) e.getDamager()).getDisplayName() + " using " + ChatColor.AQUA + "[" + ChatColor.RESET + ((Player) e.getDamager()).getInventory().getItemInMainHand().getItemMeta().getDisplayName() + ChatColor.AQUA + "]" + ChatColor.RESET);
                                        return;
                                    }
                                }
                            }
                        }
                        Bukkit.broadcastMessage(entityteamName + ChatColor.RESET + Bukkit.getOfflinePlayer(NPCOwner.get(e.getEntity())).getName() + " was slain by " + damagerteamName + ChatColor.RESET  + ((Player) e.getDamager()).getDisplayName());
                    }
                }
            }
        }
    }

    @EventHandler
    public void punishCombatLogger (PlayerJoinEvent e) {
        if (PersistentData.get().getBoolean(e.getPlayer().getUniqueId() + ".flagged")) {
            e.getPlayer().getInventory().clear();
            e.getPlayer().setHealth(0);
        }
        if (PersistentData.get().getString(e.getPlayer().getUniqueId() + ".npc") != null) {
            if (Bukkit.getEntity(UUID.fromString(PersistentData.get().getString(e.getPlayer().getUniqueId() + ".npc"))) != null) {
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


