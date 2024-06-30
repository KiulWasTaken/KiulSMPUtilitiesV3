package kiul.kiulsmputilitiesv3.accessories;

import com.destroystokyo.paper.event.entity.ExperienceOrbMergeEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import jdk.jfr.Enabled;
import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.potions.CustomHastePotion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;

public class TomeAccessory implements Listener {

    HashMap<Player,Long> experiencePickupCooldown = new HashMap<>();

    @EventHandler
    public void baseEffect (PlayerExpChangeEvent e) {
        if (AccessoryMethods.getActiveAccessoryIdentifier(e.getPlayer()).contains("tome")) {
            if (e.getAmount() > 0) {
                e.setAmount((int) (e.getAmount() * 1.25));
            }
        }
    }

    @EventHandler
    public void rubyPreventJank (ExperienceOrbMergeEvent e) {
        if (e.getEntity().hasMetadata("no")) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void rubyPreventSteal (PlayerPickupExperienceEvent e) {
        if (e.getExperienceOrb().hasMetadata("no")) {
            if (experiencePickupCooldown.containsKey(e.getPlayer())) {
                if (experiencePickupCooldown.get(e.getPlayer()) <= System.currentTimeMillis()) {
                    experiencePickupCooldown.remove(e.getPlayer());
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void rubyEffect (EntityResurrectEvent e) {
        if (!e.isCancelled() && e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Entity entity = ((EntityDamageByEntityEvent) e.getEntity().getLastDamageCause()).getDamager();
            if (entity instanceof Player damager) {
                if (AccessoryMethods.getActiveAccessoryIdentifier(damager).equals("tome_ruby")) {
                    int numOrbs = (int) (Math.random() * 4) + 4;
                    for (int i = 0; i < numOrbs; i++) {
                        ExperienceOrb orb = (ExperienceOrb) damager.getWorld().spawnEntity(e.getEntity().getLocation().add(Math.random() * 2, Math.random() * 2, Math.random() * 2), EntityType.EXPERIENCE_ORB);
                        orb.setGlowing(true);
                        orb.setExperience(10);
                        orb.setMetadata("no", new FixedMetadataValue(C.plugin, "no"));
                        if (e.getEntity() instanceof Player damaged) {
                            experiencePickupCooldown.put(damaged, System.currentTimeMillis() + 5000);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void peridotEffectCooldown (PlayerItemDamageEvent e) {
        Player p = e.getPlayer();
        if (AccessoryMethods.getActiveAccessoryIdentifier(p).equals("tome_peridot")) {
            if (itemRepairCooldown.get(p) == null) {
                itemRepairCooldown.put(p,new HashMap<>());
                itemRepairCooldown.get(p).put(e.getItem(),System.currentTimeMillis()+1000*60*10);
            } else {
                itemRepairCooldown.get(p).put(e.getItem(),System.currentTimeMillis()+1000*60*10);
            }
        }
    }

    @EventHandler
    public void startPeridotEffect (PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (AccessoryMethods.getActiveAccessoryIdentifier(p).equals("tome_peridot")) {
            peridotEffect(p);
        }
    }

   static HashMap<Player,HashMap<ItemStack,Long>> itemRepairCooldown = new HashMap<>();
    public static void peridotEffect (Player p) {
        if (itemRepairCooldown.get(p) == null) {
            itemRepairCooldown.put(p, new HashMap<>());
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline() || !AccessoryMethods.getActiveAccessoryIdentifier(p).equals("tome_peridot")) {cancel();return;}
                if (itemRepairCooldown.get(p) != null) {
                    for (int i = 9; i <= 35; i++) {
                        ItemStack itemsToRepair = p.getInventory().getItem(i);
                        if (itemsToRepair != null) {
                            if (itemRepairCooldown.get(p).get(itemsToRepair) == null) {
                                if (itemsToRepair.getItemMeta() instanceof Damageable itemMeta && itemMeta.getDamage() > 0) {
                                        if (Math.random() < 0.3 && p.getExp() > 0) {
                                            itemMeta.setDamage(itemMeta.getDamage() - 1);
                                            itemsToRepair.setItemMeta(itemMeta);
                                            if (p.getExp()-0.013889F <= 0 && p.getLevel() > 0) {
                                                p.setLevel(p.getLevel() - 1);
                                                p.setExp(0.99F);
                                            } else {
                                                p.setExp(p.getExp() - 0.013889F);
                                            }

                                        }
                                    break;
                                }
                            } else {
                                if (itemRepairCooldown.get(p).get(itemsToRepair) < System.currentTimeMillis()) {
                                    itemRepairCooldown.get(p).remove(itemsToRepair);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(C.plugin,0,20);
    }


    @EventHandler
    public void tanzaniteEffect (PlayerPickupExperienceEvent e) {
        Player p = e.getPlayer();
        if (AccessoryMethods.getActiveAccessoryIdentifier(p).equals("tome_tanzanite")) {
            Team team = C.getPlayerTeam(p);
            for (String teammateNames : team.getEntries()) {
                if (Bukkit.getPlayer(teammateNames) != null) {
                    Player teammate = Bukkit.getPlayer(teammateNames);
                    if (teammate != p) {
                        if (p.getLocation().distance(Bukkit.getPlayer(teammateNames).getLocation()) < 5) {
                            ExperienceOrb orb = (ExperienceOrb) teammate.getWorld().spawnEntity(teammate.getLocation(), EntityType.EXPERIENCE_ORB);
                            orb.setExperience(1 + (e.getExperienceOrb().getExperience() / 5));
                        }
                    }
                }
            }
        }
    }

    ArrayList<PotionEffectType> positivePotionEffects = new ArrayList<>() {{
       add(PotionEffectType.ABSORPTION);
       add(PotionEffectType.REGENERATION);
       add(PotionEffectType.SPEED);
       add(PotionEffectType.INCREASE_DAMAGE);
       add(PotionEffectType.DAMAGE_RESISTANCE);
       add(PotionEffectType.FIRE_RESISTANCE);
       add(PotionEffectType.LUCK);
       add(PotionEffectType.FAST_DIGGING);
       add(PotionEffectType.DOLPHINS_GRACE);
       add(PotionEffectType.HERO_OF_THE_VILLAGE);
       add(PotionEffectType.NIGHT_VISION);
       add(PotionEffectType.JUMP);
       add(PotionEffectType.CONDUIT_POWER);
       add(PotionEffectType.SATURATION);
       add(PotionEffectType.WATER_BREATHING);
       add(PotionEffectType.HEALTH_BOOST);
       add(PotionEffectType.HEAL);
    }};

    @EventHandler
    public void opalEffect (PlayerPickupExperienceEvent e) {
        Player p = e.getPlayer();
        if (AccessoryMethods.getActiveAccessoryIdentifier(p).equals("tome_opal")) {
            int effect = (int)(Math.random()*(positivePotionEffects.size()+1));
            e.getPlayer().sendMessage(Math.random()+" < 0.2");
            if (Math.random() < 0.2) {
                if (!p.hasPotionEffect(positivePotionEffects.get(effect))) {
                    p.addPotionEffect(new PotionEffect(positivePotionEffects.get(effect), (int) ((Math.random() * 30) * 20), (int) (Math.random() * 2), false, true));
                }
            }
        }
    }
}
