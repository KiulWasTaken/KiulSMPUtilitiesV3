package kiul.kiulsmputilitiesv3.stats;

import io.papermc.paper.event.player.PlayerTradeEvent;
import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.combattag.FightObject;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.CartographyInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatDBListeners implements Listener {

    HashMap<Material,Integer> valuedTypes = new HashMap<>() {{
       put(Material.NETHERITE_HELMET,2);
       put(Material.NETHERITE_CHESTPLATE,2);
       put(Material.NETHERITE_LEGGINGS,2);
       put(Material.NETHERITE_BOOTS,2);

       put(Material.DIAMOND_HELMET,1);
       put(Material.DIAMOND_CHESTPLATE,1);
       put(Material.DIAMOND_LEGGINGS,1);
       put(Material.DIAMOND_BOOTS,1);
    }};

    public boolean isStacked(Player p) {
        int points = 0;
        if (PersistentData.get().get(p.getUniqueId()+".value") != null) {
            points += PersistentData.get().getInt(p.getUniqueId()+".value");
        }
        int ticksLived = p.getStatistic(Statistic.TIME_SINCE_DEATH);
        if (ticksLived < 20*60*60*2) {return false;} // if the player has been alive for <2 hours they cant be counted as "stacked"
        // one point of value per 4 hours since their last death, assuming that the statistic counts in ticks and not ms.
        points += ticksLived/20*60*60*4;

        for (ItemStack item :p.getInventory().getArmorContents()) {
            if (valuedTypes.containsKey(item.getType())) {
                points += valuedTypes.get(item.getType());
            }
        }
        if (points >= 6) {
            // to have 6 points, the player needs to be in full netherite,
            // or have survived a long time and be in diamond, or have
            // broken their armor previously.

            return true;
        }

        return false;
    }

    @EventHandler
    public void conservePointsOnItemBreak (PlayerItemBreakEvent e) {
        if (valuedTypes.containsKey(e.getBrokenItem().getType())) {
            if (PersistentData.get().get(e.getPlayer().getUniqueId() +".value") != null) {
                PersistentData.get().set(e.getPlayer().getUniqueId()+".value",PersistentData.get().getInt(e.getPlayer().getUniqueId()+".value")+valuedTypes.get(e.getBrokenItem().getType()));
            } else {
                PersistentData.get().set(e.getPlayer().getUniqueId()+".value",valuedTypes.get(e.getBrokenItem().getType()));
            }
        }
    }

    @EventHandler
    public void restockStat (BlockPlaceEvent e) {
        if ((e.getBlock().getType().toString().contains("SHULKER_BOX") || e.getBlock().getType().equals(Material.ENDER_CHEST)) && C.fightManager.findFightForMember(e.getPlayer()) != null) {
            StatDB.writePlayer(e.getPlayer().getUniqueId(),"stat_restocks",(int)StatDB.readPlayer(e.getPlayer().getUniqueId(),"stat_restocks")+1);
        }
    }

    @EventHandler
    public void tradeStat (PlayerTradeEvent e) {
        if (e.getTrade().getDemand() > 5) {
            StatDB.writePlayer(e.getPlayer().getUniqueId(),"stat_trades",(int)StatDB.readPlayer(e.getPlayer().getUniqueId(),"stat_trades")+1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void killPlayer (EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player killed && e.getDamager() instanceof Player killer && e.getFinalDamage() >= killed.getHealth()) {
            if ((new EntityResurrectEvent(killed).isCancelled()) && C.getPlayerTeam(killer).getName() != C.getPlayerTeam(killed).getName()) {
                if (isStacked(killed)) {
                    StatDB.writePlayer(killer.getUniqueId(), "stat_kills", (int) StatDB.readPlayer(killer.getUniqueId(), "stat_kills") + 1);
                    FightObject fight = C.fightManager.findFightForMember(killer);
                    if (fight == null) {
                        return;
                    }
                    if (fight.isPartaking(killed.getUniqueId())) {
                        HashMap<Team, List<Player>> team = C.sortTeams(fight.getParticipants());
                        int numEnemies = fight.getParticipants().size() - team.get(C.getPlayerTeam(killer)).size();
                        if (numEnemies > team.get(C.getPlayerTeam(killer)).size()) {
                            StatDB.writePlayer(killer.getUniqueId(), "stat_kills_odds", (int) StatDB.readPlayer(killer.getUniqueId(), "stat_kills_odds") + 1);
                        }
                        if (fight.getDuration() < 1000 * 60 * 60 * 5) {
                            StatDB.writePlayer(killer.getUniqueId(), "stat_kills_quick", (int) StatDB.readPlayer(killer.getUniqueId(), "stat_kills_quick") + 1);
                        }
                        if (fight.getDuration() > 1000 * 60 * 60 * 30) {
                            StatDB.writePlayer(killer.getUniqueId(), "stat_kills_drain", (int) StatDB.readPlayer(killer.getUniqueId(), "stat_kills_drain") + 1);
                        }
                    }
                }
            }
        }
    }

    static HashMap<ExplosiveMinecart,Player> cartShooter = new HashMap<>();
    static HashMap<ExplosiveMinecart,ArrayList<Player>> damagedByCart = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void cartStat (EntityExplodeEvent e) {
        if (e.getEntity() instanceof ExplosiveMinecart cart) {
            if (cart.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamageCause) {
                if ((lastDamageCause.getDamager().getType() == EntityType.ARROW || lastDamageCause.getDamager().getType() == EntityType.SPECTRAL_ARROW || lastDamageCause.getDamager().getType() == EntityType.PLAYER)) {
                    Player damager = null;
                    if ((lastDamageCause.getDamager() instanceof Projectile arrow )) {
                        damager = (Player) arrow.getShooter();
                    }
                    if (lastDamageCause.getDamager() instanceof Player) {
                        damager = (Player) lastDamageCause.getDamager();
                    }
                    if (damager != null) {
                    cartShooter.put(cart,damager);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Player p = cartShooter.get(cart);
                            if (damagedByCart.get(cart) != null && damagedByCart.get(cart).size() >= 2) {
                                StatDB.writePlayer(p.getUniqueId(),"stat_carts",(int)StatDB.readPlayer(p.getUniqueId(),"stat_carts")+1);
                                cartShooter.remove(cart);
                                damagedByCart.remove(cart);
                            } else {
                                cartShooter.remove(cart);
                                if (damagedByCart.get(cart) != null) {
                                    damagedByCart.remove(cart);
                                }
                            }
                        }
                    }.runTaskLater(C.plugin,5);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void cartDamageListener (EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof ExplosiveMinecart cart && e.getEntity() instanceof Player p && e.getFinalDamage() >= p.getHealth()) {
            if (cartShooter.get(cart) != null) {
                if (damagedByCart.get(cart) != null) {
                    damagedByCart.get(cart).add(p);
                } else {
                    damagedByCart.put(cart,new ArrayList<Player>() {{add(p);}});
                }

            }
        }
    }
}
