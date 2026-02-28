package kiul.kiulsmputilitiesv3.server_events;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class SuddenDeath {
    boolean debug;
    List<OfflinePlayer> involvedPlayers;
    int startingSize; // the starting number of players to base percentage
    int minimumPlayers; // after the player amount reaches this, the threshold will no longer exclude players.
    int maximumPlayersAlive; // decreases over time. cannot go below minimumPlayers.
    int timeBetweenTicksMinutes;
    long graceTime;
    BukkitTask runnable;

    HashMap<Player,Boolean> aboveThreshold;
    HashMap<Player,Double> livingPlayersDamageMap;


    public SuddenDeath(List<Player> involvedPlayers, int timeBetweenTicksMinutes, int minimumPlayers, boolean debug) {

        this.involvedPlayers = new ArrayList<>();
        for (Player p : involvedPlayers) {
            this.involvedPlayers.add(Bukkit.getOfflinePlayer(p.getUniqueId()));
        }
        this.startingSize = involvedPlayers.size();
        this.minimumPlayers = minimumPlayers;
        this.livingPlayersDamageMap = new HashMap<>();
        this.aboveThreshold = new HashMap<>();
        this.maximumPlayersAlive = involvedPlayers.size();
        for (Player involvedPlayer : involvedPlayers) {
            aboveThreshold.put(involvedPlayer,true);
            livingPlayersDamageMap.put(involvedPlayer,0.0);
        }
        this.graceTime = System.currentTimeMillis()+(10*60*1000);// starts after 5 minutes
        if (debug) {
            graceTime = System.currentTimeMillis();
        }
        this.timeBetweenTicksMinutes = timeBetweenTicksMinutes;
        this.debug = debug;
    }

    public HashMap<Player, Double> getLivingPlayersDamageMap() {
        return livingPlayersDamageMap;
    }

    public void start() {
        C.suddenDeath = this;
        for (Player p : livingPlayersDamageMap.keySet()) {
            p.sendMessage(C.t(C.LIGHT_ICE_BLUE + "❄ " + C.ICE_BLUE + "&lSUDDEN DEATH!&r" + C.LIGHT_ICE_BLUE + " Deal damage to avoid certain doom!"));
        }

        runnable = new BukkitRunnable() {

            int tickMinutes = 0;
            int damageTick = 0;
            int tickSeconds = 0;
            int stageTick = livingPlayersDamageMap.size()/(5+livingPlayersDamageMap.size()/15);



            @Override
            public void run() {
                if (System.currentTimeMillis() > graceTime) {
                    tickSeconds++;
                    if (tickSeconds >= 60) {
                        tickMinutes++;
                        tickSeconds = 0;
                    }


                    if (tickMinutes >= timeBetweenTicksMinutes) {
                        stageTick--;
                        if (stageTick <= 0) stageTick = 0;
                        maximumPlayersAlive -= 1 + (stageTick);
                        if (maximumPlayersAlive < minimumPlayers) maximumPlayersAlive = minimumPlayers;
                        tickMinutes = 0;

                    }
                }
                List<Player> playersArranged = sortHashMapByValue(livingPlayersDamageMap);
                if (debug) {
                    Bukkit.getLogger().info("----- active suddendeath debug info -----");
                    Bukkit.getLogger().info("max. alive: " + maximumPlayersAlive);
                    Bukkit.getLogger().info("min. alive: " + minimumPlayers);
                    Bukkit.getLogger().info("next. alive: " + (maximumPlayersAlive - (1 + stageTick)));
                    Bukkit.getLogger().info("current tick: " + tickMinutes + "m, " + tickSeconds + "s out of " + timeBetweenTicksMinutes + "m");
                    Bukkit.getLogger().info("damage tick: " + damageTick);
                    Bukkit.getLogger().info("---------- damage map ----------");

                    for (int i = 0; i < playersArranged.size(); i++) {
                        Player arranged = playersArranged.get(i);
                        Bukkit.getLogger().info(arranged.getName() + "-> " + livingPlayersDamageMap.get(arranged));
                        if ((maximumPlayersAlive-1) < livingPlayersDamageMap.size()-1) {
                            if (Objects.equals(livingPlayersDamageMap.get(playersArranged.get(maximumPlayersAlive - 1)), livingPlayersDamageMap.get(arranged))) {
                                Bukkit.getLogger().info("===== Threshold =====");
                            }
                        }
                    }

                    Double thresholdDamage = 0.0;

                    damageTick++;
                    if (playersArranged.size() > maximumPlayersAlive) {
                        thresholdDamage = livingPlayersDamageMap.get(playersArranged.get(maximumPlayersAlive - 1));
                        for (Player player : playersArranged) {
                            if (player == null) continue;
                            aboveThreshold.put(player, true);
                        }
                        for (int j = maximumPlayersAlive; j < playersArranged.size(); j++) {
                            if (playersArranged.get(j) == null) continue;
                            aboveThreshold.put(playersArranged.get(j), false);
                        }
                    } else {
                        for (Player player : playersArranged) {
                            aboveThreshold.put(player, true);
                        }
                    }

                    ArrayList<Player> removedPlayers = new ArrayList<>();
                    for (Player p : aboveThreshold.keySet()) {
                        if (p.getGameMode() != GameMode.SURVIVAL) {
                            removedPlayers.add(p);
                            p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
                            continue;
                        }
                        if (livingPlayersDamageMap.get(p) == null) {
                            removedPlayers.add(p);
                        }
                    }
                    for (Player p : removedPlayers) {
                        removePlayer(p);
                    }

                    for (Player p : aboveThreshold.keySet()) {
                        if (p == null) continue;
                        double visualThreshold = thresholdDamage;

                        if ((livingPlayersDamageMap.get(p) - thresholdDamage) == 0 && playersArranged.size() > maximumPlayersAlive) {
                            visualThreshold = livingPlayersDamageMap.get(playersArranged.get(maximumPlayersAlive));
                        }
                        if (aboveThreshold.get(p)) {

                            if (damageTick >= 10) {
                                p.sendMessage(C.t(C.RED + "\uD83D\uDD25 " + C.LIGHT_RED + C.twoPointDecimal.format(livingPlayersDamageMap.get(p) - visualThreshold) + " damage above threshold"));
                                if (p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() < 20) {
                                    p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() + 1);
                                }
                            }
                        } else {
                            if (damageTick >= 10) {
                                p.sendMessage(C.t(C.ICE_BLUE + "❄ " + C.LIGHT_ICE_BLUE + C.twoPointDecimal.format(visualThreshold - livingPlayersDamageMap.get(p)) + " damage below threshold"));
                                if (p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() > 1 && playersArranged.size() > maximumPlayersAlive) {
                                    p.setFreezeTicks(60);
                                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1f, 1f);
                                    p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() - 1);
                                }
                            }
                        }


                    }
                    if (damageTick >= 10) {
                        damageTick = 0;
                    }
                    if (livingPlayersDamageMap.size() <= minimumPlayers) {
                        for (Player lastPlayers : livingPlayersDamageMap.keySet()) {
                            lastPlayers.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
                            for (Player p : livingPlayersDamageMap.keySet()) {
                                p.sendMessage(C.t(C.LIGHT_RED + "❄ " + C.RED + "&lSUDDEN DEATH OVER!&r" + C.LIGHT_RED + " Fight to the death!"));
                            }
                        }
                        cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(C.plugin,0,20);
    }
    private List<Player> sortHashMapByValue(HashMap<Player, Double> map) {
        // Create a list from elements of the HashMap
        List<Map.Entry<Player, Double>> entryList = new ArrayList<>(map.entrySet());

        // Sort the list based on the values in descending order
        entryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Create a list to store the sorted strings
        List<Player> sortedList = new ArrayList<>();
        for (Map.Entry<Player, Double> entry : entryList) {
            sortedList.add(entry.getKey());
        }

        return sortedList;
    }
    public void stop() {
        for (OfflinePlayer p : involvedPlayers) {
            if (p.getPlayer() != null) {
                p.getPlayer().getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
            }
        }
        runnable.cancel();
    }
    public void removePlayer(Player p) {
        aboveThreshold.remove(p);
        livingPlayersDamageMap.remove(p);
        p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
    }

    public boolean isDebug() {
        return debug;
    }
}
