package kiul.kiulsmputilitiesv3.combattag;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.ConfigData;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static kiul.kiulsmputilitiesv3.combattag.FightMethods.totalArmourDurability;
import static kiul.kiulsmputilitiesv3.crates.CrateMethods.activeCratesLocation;
import static kiul.kiulsmputilitiesv3.crates.CrateMethods.playersWhoGotLoot;

public class FightLogicListeners implements Listener {


    HashMap<UUID, HashMap<Material, Integer>> itemCooldowns = new HashMap<>();
    public static HashMap<UUID, Long> relogCooldown = new HashMap<>();
    private HashMap<Entity, Player> entityOwner = new HashMap<>();

    ArrayList<Material> itemsWithCooldowns = new ArrayList<>() {{
        add(Material.ELYTRA);
        add(Material.TRIDENT);
        add(Material.FIREWORK_ROCKET);
    }};

    @EventHandler
    public void logoutInCombat(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        itemCooldowns.put(p.getUniqueId(), new HashMap<>());
        relogCooldown.put(p.getUniqueId(), System.currentTimeMillis());
        for (Material material : itemsWithCooldowns) {
            if (p.getCooldown(material) > 0) {
                itemCooldowns.get(p.getUniqueId()).put(material, p.getCooldown(material));
            }
        }
    }

    @EventHandler
    public void loginInCombat(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        relogCooldown.remove(p.getUniqueId());
        if (itemCooldowns.get(p.getUniqueId()) == null) return;
        for (Material material : itemCooldowns.get(p.getUniqueId()).keySet()) {
            p.setCooldown(material, itemCooldowns.get(p.getUniqueId()).get(material));
        }
    }

    /**
     * FIGHT BEHAVIOUR
     **/

    @EventHandler
    public void playerPlaceEntity (EntityPlaceEvent e) {
        Player p = e.getPlayer();
        if (e.getEntity() instanceof ExplosiveMinecart cart) {
            entityOwner.put(cart,p);
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(VehicleDamageEvent event) {
        // Check if the damaged entity is a minecart
        if (event.getVehicle() instanceof ExplosiveMinecart) {
            // Check if the damaging entity is a player
            Player attacker;
            ExplosiveMinecart minecart = (ExplosiveMinecart) event.getVehicle();
            if (event.getAttacker() instanceof Player p) {
                attacker = p;
            } else if (event.getAttacker() instanceof Projectile arrow) {
                if (arrow.getShooter() instanceof Player) {
                    attacker = (Player) arrow.getShooter();
                } else return;
            } else return;
            // Handle the event, for example, you can send a message when the player punches the minecart
            entityOwner.put(minecart, attacker);
        }
    }
    @EventHandler
    public void playerHitMinecart (EntityCombustByEntityEvent e) {
        if (e.getCombuster() instanceof Projectile arrow && e.getEntity() instanceof ExplosiveMinecart cart) {
            Player attacker;
            if (arrow.getShooter() instanceof Player) {
                attacker = (Player) arrow.getShooter();
            } else return;
            entityOwner.put(cart,attacker);
        }
    }
    @EventHandler
    public void incrementStatsInFight(EntityDamageByEntityEvent e) {
        if (!ConfigData.get().getBoolean("combattag")) {
            return;
        }
        boolean isMarkedDown = false;
        if (e.getEntity() instanceof Player p2) {
            Player p1 = null;
            FightObject fight = C.fightManager.findFightForMember(p2);
            if (fight == null) return;
            if (e.getDamager() instanceof ExplosiveMinecart cart) {
                if (entityOwner.get(cart) != null) {
                    Player damager = entityOwner.get(cart);
                    p1 = damager;
                    fight.increaseStat(fight.getExplosiveDamageDealtToPlayer(),p1,p2,e.getDamage());
                    fight.increaseStat(fight.getExplosiveDamageTakenFromPlayer(),p2,p1,e.getDamage());
                    isMarkedDown = true;
                }
            }

            if (e.getDamager() instanceof Projectile arrow) {
                if (arrow.getShooter() instanceof Player) {
                    p1 = (Player) arrow.getShooter();
                    fight.increaseStat(fight.getRangedDamageDealtToPlayer(),p1,p2,e.getDamage());
                    fight.increaseStat(fight.getRangedDamageTakenFromPlayer(),p2,p1,e.getDamage());
                    isMarkedDown = true;
                }
            }
            if (e.getDamager() instanceof Player) {
                p1 = (Player) e.getDamager();
                if (p1.getInventory().getItemInMainHand().getType().equals(Material.MACE)) {
                    fight.increaseStat(fight.getMaceDamageDealtToPlayer(),p1,p2,e.getDamage());
                    fight.increaseStat(fight.getMaceDamageTakenFromPlayer(),p2,p1,e.getDamage());
                } else {
                    fight.increaseStat(fight.getMeleeDamageDealtToPlayer(),p1,p2,e.getDamage());
                    fight.increaseStat(fight.getMaceDamageDealtToPlayer(),p2,p1,e.getDamage());
                }
                isMarkedDown = true;
            }
            if (p1 != null) {
                fight.increaseStat(fight.getDamageDealt(), p1, e.getDamage());
                if (C.getPlayerTeam(p1) != C.getPlayerTeam(p2)) {
                    for (Location crateLocation : activeCratesLocation.keySet()) {
                        Vector cornerA = crateLocation.clone().add(10, 10, 10).toVector();
                        Vector cornerB = crateLocation.clone().add(-10, -10, -10).toVector();
                        Vector squareCornerA = Vector.getMinimum(cornerA, cornerB);
                        Vector squareCornerB = Vector.getMaximum(cornerA, cornerB);
                        if (p2.getLocation().toVector().isInAABB(squareCornerA, squareCornerB)) {
                            activeCratesLocation.put(crateLocation, activeCratesLocation.get(crateLocation) + e.getFinalDamage());
                        }
                    }
                }
            }
            fight.increaseStat(fight.getDamageTaken(),p2,e.getDamage());
            if (!isMarkedDown) {
                fight.increaseStat(fight.getUntypedDamageDealtToPlayer(),p1,p2,e.getDamage());
                fight.increaseStat(fight.getUntypedDamageTakenFromPlayer(),p2,p1,e.getDamage());
            }
            if (p1 == null) {
                return;
            }


            int p2preDurability = totalArmourDurability(p2);
            Player finalP = p1;
            Bukkit.getScheduler().scheduleSyncDelayedTask(C.plugin, new Runnable() {
                @Override
                public void run() {
                    int p2postDurability = totalArmourDurability(p2);
                    if (p2postDurability > p2preDurability) {
                        int difference = p2postDurability - p2preDurability;
                        // increase stats by difference
                        fight.increaseStat(fight.getDurabilityDamageDealtToPlayer(), finalP, p2, difference);
                        fight.increaseStat(fight.getDurabilityDamageTakenFromPlayer(), p2, finalP, difference);
                    }
                }
            }, 1);
        }

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void startFight(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        if (!ConfigData.get().getBoolean("combattag")) {
            return;
        }
        if (e.getEntity() instanceof Player) {
            Player p1 = null;
            if (e.getDamager() instanceof ExplosiveMinecart cart) {
                if (entityOwner.get(cart) != null) {
                    Player damager = entityOwner.get(cart);
                    p1 = damager;
                }
            }
            if (e.getDamager() instanceof AbstractArrow arrow) {
                if (arrow.getShooter() instanceof Player) {
                    p1 = (Player) arrow.getShooter();
                }
            }
            if (e.getDamager() instanceof Player) {
                p1 = (Player) e.getDamager();
            }

            Player p2 = (Player) e.getEntity();
            if (p1 == null) {
                return;
            }
            if (C.getPlayerTeam(p1) != null && C.getPlayerTeam(p2) != null) {
                if ((C.getPlayerTeam(p1).getName() == C.getPlayerTeam(p2).getName())) {
                    return;
                }
            }
            boolean p1inFight = C.fightManager.playerIsInFight(p1);
            boolean p2inFight = C.fightManager.playerIsInFight(p2);
            FightObject p1FightObject = C.fightManager.findFightForMember(p1);
            FightObject p2FightObject = C.fightManager.findFightForMember(p2);

            if (!p1inFight && !p2inFight) {
                ArrayList<UUID> participants = new ArrayList<>();
                participants.add(p1.getUniqueId());
                participants.add(p2.getUniqueId());
                FightObject newFightObject = C.fightManager.createFight(participants);
                FightMethods.startDistanceCheck(p1, newFightObject);
                FightMethods.startDistanceCheck(p2, newFightObject);
                return;
            }

            if (p1inFight ^ p2inFight) {
                if (p1inFight) {
                    p1FightObject.addParticipant(p2);
                    FightMethods.startDistanceCheck(p2, p1FightObject);
                    return;
                }
                if (p2inFight) {
                    p2FightObject.addParticipant(p1);
                    FightMethods.startDistanceCheck(p1, p2FightObject);
                    return;
                }
            }

            if (p1FightObject != p2FightObject) {
                if (p1FightObject.getDuration() > p2FightObject.getDuration()) {
                    p1FightObject.mergeFight(p2FightObject);
                } else {
                    p2FightObject.mergeFight(p1FightObject);
                }
            }
        }
    }



    @EventHandler
    public void removeFromFightOnDeath (PlayerDeathEvent e) {
        if (!ConfigData.get().getBoolean("combattag")) {return;}
        Player p = e.getEntity();
        FightObject fightObject = C.fightManager.findFightForMember(p);
        if (fightObject != null) {
            if (p.getKiller() != null) {
                fightObject.getKiller().put(p.getUniqueId().toString(), p.getKiller().getUniqueId().toString());
            }
            fightObject.removeParticipant(p.getUniqueId(),true);
        }
    }

    @EventHandler
    public void addBackOnJoin (PlayerJoinEvent e) {
        FightObject fight = C.fightManager.findFightForMember(e.getPlayer());
        if (fight != null && fight.getOfflineParticipants().contains(e.getPlayer().getUniqueId())) {
            fight.getParticipants().add(e.getPlayer().getUniqueId());
            fight.getOfflineParticipants().remove(e.getPlayer().getUniqueId());
            FightMethods.startDistanceCheck(e.getPlayer(),fight);
        }
    }

    /**
     *
     *  COOLDOWNS
     *
     **/

//    @EventHandler
//    public void pearlSlowDown (ProjectileLaunchEvent e) {
//        if (!C.combatTagEnabled) {return;}
//        if (e.getEntity() instanceof EnderPearl && e.getEntity().getShooter() instanceof Player p) {
//            if (C.fightManager.playerIsInFight(p)) {
//                if (lastPearlThrow.get(p.getUniqueId()) == null) {
//                    lastPearlThrow.put(p.getUniqueId(), System.currentTimeMillis());
//                    fastPearlThrow.put(p.getUniqueId(), 0);
//                }
//
//                    if ((System.currentTimeMillis() - lastPearlThrow.get(p.getUniqueId())) < 6000) {
//                        fastPearlThrow.put(p.getUniqueId(), fastPearlThrow.get(p.getUniqueId()) + 1);
//
//                        new BukkitRunnable() {
//                            int cooldown = 10*fastPearlThrow.get(p.getUniqueId());
//
//                            @Override
//                            public void run() {
//                                if (cooldown > 120) {
//                                    cooldown = 120;
//                                }
//                                p.setCooldown(Material.ENDER_PEARL,(20 + cooldown));
//                            }
//                        }.runTaskLater(C.plugin,0);
//                    } else {
//                        lastPearlThrow.put(p.getUniqueId(), null);
//                        fastPearlThrow.put(p.getUniqueId(), 0);
//                    }
//                lastPearlThrow.put(p.getUniqueId(), System.currentTimeMillis());
//            }
//        }
//    }

    @EventHandler
    public void pearlSlowDown (ProjectileLaunchEvent e) {
        if (!ConfigData.get().getBoolean("combattag")) {return;}
            if (e.getEntity() instanceof EnderPearl && e.getEntity().getShooter() instanceof Player p) {
                if (C.fightManager.playerIsInFight(p)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.setCooldown(Material.ENDER_PEARL, 300);
                        }
                    }.runTaskLater(C.plugin, 0);
            }
        }
    }

    @EventHandler
    public void useTridentCoolDown (PlayerRiptideEvent e) {
        if (!ConfigData.get().getBoolean("combattag")) {return;}
        Player p = e.getPlayer();
        if (C.fightManager.playerIsInFight(p)) {
            p.setCooldown(Material.TRIDENT,600);
            if (p.getInventory().contains(Material.ELYTRA) || (p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() == Material.ELYTRA)) {
                p.setCooldown(Material.TRIDENT, 900);
            }
        }
    }
    @EventHandler
    public void useWindChargeCoolDown (ProjectileLaunchEvent e) {
        if (!ConfigData.get().getBoolean("combattag")) {return;}
        if (e.getEntity() instanceof WindCharge && e.getEntity().getShooter() instanceof Player p) {
                if (C.fightManager.playerIsInFight(p)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.setCooldown(Material.WIND_CHARGE, 140);
                        }
                    }.runTaskLater(C.plugin, 0);

                }
            }
        }

    @EventHandler
    public void damagedTridentCoolDown (EntityDamageByEntityEvent e) {
        if (!ConfigData.get().getBoolean("combattag")) {return;}
        if (e.getEntity() instanceof Player) {
            Player p1 = null;
            if (e.getDamager() instanceof ExplosiveMinecart cart) {
                if (cart.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamageCause) {
                    if ((lastDamageCause.getDamager().getType() == EntityType.ARROW || lastDamageCause.getDamager().getType() == EntityType.SPECTRAL_ARROW || lastDamageCause.getDamager().getType() == EntityType.PLAYER)) {
                        Player damager = null;
                        if ((lastDamageCause.getDamager() instanceof Projectile arrow)) {
                            damager = (Player) arrow.getShooter();
                        }
                        if (lastDamageCause.getDamager() instanceof Player) {
                            damager = (Player) lastDamageCause.getDamager();
                        }
                        p1 = damager;
                    }
                }
            }
            if (e.getDamager() instanceof AbstractArrow arrow) {
                if (arrow.getShooter() instanceof Player) {
                    p1 = (Player) arrow.getShooter();
                }
            }
            if (e.getDamager() instanceof Player) {
                p1 = (Player) e.getDamager();
            }

            Player p2 = (Player) e.getEntity();
            if (C.getPlayerTeam(p1) != null && C.getPlayerTeam(p2) != null) {
                if ((C.getPlayerTeam(p1).getName() == C.getPlayerTeam(p2).getName())) {
                    return;
                }
            }
            if (p1 != null) {
                int p2cooldown = p2.getCooldown(Material.TRIDENT) + 100;
                if (p2cooldown > 300) {
                    p2cooldown = 300;
                }
                p2.setCooldown(Material.TRIDENT, p2cooldown);


                    int p1cooldown = p1.getCooldown(Material.TRIDENT) + 100;
                    if (p1cooldown > 300) {
                        p1cooldown = 300;
                    }
                    p1.setCooldown(Material.TRIDENT, p1cooldown);
                }

        }
    }
    ArrayList<Player> glidingPlayers = new ArrayList<>();
    public void updateElytraHitBox (Player p) {

        new BukkitRunnable() {

            @Override
            public void run() {
               for (Entity nearbyEntities : p.getNearbyEntities(2,2,2)) {
                   if (nearbyEntities instanceof Arrow || nearbyEntities instanceof SpectralArrow) {
                       if (((AbstractArrow) nearbyEntities).getShooter() instanceof Player attacker && attacker != p) {
                           ((AbstractArrow) nearbyEntities).hitEntity(p);
                       }

                   }
               }
                if (!p.isGliding()) {
                    p.setCollidable(true);
                    glidingPlayers.remove(p);
                    cancel();
                }
            }
        }.runTaskTimer(C.plugin,0,1);
    }
    @EventHandler
    public void elytraGlidingCheck (PlayerMoveEvent e) {
        if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
            if (!e.getPlayer().isGliding()) {return;}
            if (!glidingPlayers.contains(e.getPlayer())) {
                glidingPlayers.add(e.getPlayer());
                updateElytraHitBox(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void preventStartGlidingOnCooldown (EntityToggleGlideEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (p.getCooldown(Material.ELYTRA) > 0) {
                p.setGliding(false);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void elytraMidAirDisable (EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player p) {
            Player damager = null;
            if (e.getDamager() instanceof AbstractArrow arrow) {
                if (arrow.getShooter() instanceof Player) {
                    damager = (Player) arrow.getShooter();
                }
            }
            if (e.getDamager() instanceof Player) {
                damager = (Player) e.getDamager();
            }
            if (damager != null) {
                if (p.isGliding()) {
                    p.setGliding(false);
                    ItemStack chestplate = p.getInventory().getChestplate();
                    if (chestplate == null || chestplate.getType() != Material.ELYTRA) {
                        for (ItemStack item : p.getInventory().getContents()) {
                            if (item == null) continue;
                            if (item.getType().equals(Material.ELYTRA)) {
                                Damageable itemMeta = (Damageable) item.getItemMeta();
                                if (!itemMeta.hasDamage() || itemMeta.getDamage() < 432) {
                                    chestplate = item;
                                }
                            }
                        }
                    }
                    Damageable elytra = (Damageable) chestplate.getItemMeta();

                    elytra.setDamage(432);
                    chestplate.setItemMeta(elytra);
                    damager.spawnParticle(Particle.BLOCK_CRUMBLE,p.getLocation(),20,0.5,0.5,0.5,Material.GRAY_WOOL.createBlockData());
                    damager.playSound(damager,Sound.ENTITY_ITEM_BREAK,1f,1f);
                }
            }
        }
    }

    @EventHandler
    public void maceHitRestoreWindchargeAndRocket (EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Player damager = null;
            if (e.getDamager() instanceof Player) {
                damager = (Player) e.getDamager();
            }
            if (damager != null) {
                if (damager.getInventory().getItemInMainHand().getType().equals(Material.MACE) && damager.getFallDistance() > 1.5) {
                    if (e.getFinalDamage() > 0.0) {
                        damager.setCooldown(Material.WIND_CHARGE, 0);
                        damager.setCooldown(Material.FIREWORK_ROCKET, 0);
                        fireworkUses.put(damager, 1);
                        return;
                    }
                    if (e.getFinalDamage() <= 0.0 && !((Player) e.getEntity()).isBlocking()) {
                        damager.setCooldown(Material.WIND_CHARGE, 0);
                        damager.setCooldown(Material.FIREWORK_ROCKET, 0);
                        fireworkUses.put(damager, 1);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
            public void resetCooldowns (PlayerGameModeChangeEvent e) {
        Player p = e.getPlayer();
        p.setCooldown(Material.FIREWORK_ROCKET,0);
        p.setCooldown(Material.WIND_CHARGE,0);
        p.setCooldown(Material.TRIDENT,0);
        fireworkUses.put(p, 1);
    }

    HashMap<Player,Integer> fireworkUses = new HashMap<>();

    @EventHandler
    public void equipElytraLoud (PlayerInteractEvent e) {
        if (!C.PAT_MODE) {
            if (!ConfigData.get().getBoolean("combattag")) {
                return;
            }
        }
        if (e.getPlayer().getLocation().getWorld().getName().equals("practice")) return;
        if (e.getItem() == null) return;
        if (e.getItem().getType() == Material.ELYTRA &&
                (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            Player p = e.getPlayer();
            p.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_ELYTRA,100f,1f);
        }
    }

    @EventHandler
    public void fireworkCastCooldown (PlayerInteractEvent e) {
        if (!C.PAT_MODE) {
            if (!ConfigData.get().getBoolean("combattag")) {
                return;
            }
        }
        if (e.getPlayer().getLocation().getWorld().getName().equals("practice")) return;
        if (e.getItem() == null) return;
        if (e.getItem().getType() == Material.FIREWORK_ROCKET &&
                (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            Player p = e.getPlayer();
            if (!p.isGliding() || !p.getInventory().getChestplate().getType().equals(Material.ELYTRA) || p.getCooldown(Material.FIREWORK_ROCKET) > 0) return;
            if (!fireworkUses.containsKey(p)) {
                fireworkUses.put(p,1);
            }
            int newFireworkUses = fireworkUses.get(p)-1;
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,100f,1f);
            if (newFireworkUses <= 0) {
                newFireworkUses = 0;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.setCooldown(Material.FIREWORK_ROCKET, 12000);
                    }
                }.runTaskLater(C.plugin,0);

            }
            fireworkUses.put(p,newFireworkUses);
            FireworkMeta fireworkMeta = (FireworkMeta) e.getItem().getItemMeta();
            dealElytraBoostDamage(p,fireworkMeta.getPower());
        }
    }

    public void dealElytraBoostDamage (Player p, int duration) {
        new BukkitRunnable() {
            int boostTicks = (duration+2)*20;
            int elytraDamageTick = 0;
            int elytraBoostTick = 0;
            @Override
            public void run() {
                if (elytraBoostTick >= boostTicks) {
                    cancel();
                    return;
                }

                if (p.getInventory().getChestplate().getType().equals(Material.ELYTRA)) {
                    ItemStack chestplate = p.getInventory().getChestplate();
                    Damageable elytra = (Damageable) chestplate.getItemMeta();
                    int elytraDamage = elytra.getDamage();
                    int damageToDeal = ((elytraDamageTick/4));
                    if (elytraDamage+damageToDeal >= 432) {
                        elytra.setDamage(432);
                        chestplate.setItemMeta(elytra);
                        cancel();
                        return;
                    }
                    elytra.setDamage(elytraDamage+damageToDeal);
                    chestplate.setItemMeta(elytra);
                } else {
                    if (elytraBoostTick > boostTicks) {
                        cancel();
                        return;
                    }

                }
                if (p.getLocation().getPitch() < 0 && p.isGliding()) {
                    elytraDamageTick += 1;
                } else {
                    elytraDamageTick = 0;
                }
                elytraBoostTick += 1;

            }
        }.runTaskTimer(C.plugin,0,1);
    }
}
