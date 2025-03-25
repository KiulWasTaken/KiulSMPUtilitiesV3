package kiul.kiulsmputilitiesv3.server_events;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SuddenDeath {

    int startingSize; // the starting number of players to base percentage
    int minimumPlayers; // after the player amount reaches this, the threshold will no longer exclude players.
    int maximumPlayersAlive; // decreases over time. cannot go below minimumPlayers.
    int timeBetweenTicksMinutes;
    long graceTime;

    HashMap<Player,Boolean> aboveThreshold;
    HashMap<Player,Double> livingPlayersDamageMap;


    public SuddenDeath(List<Player> involvedPlayers, int timeBetweenTicksMinutes, int minimumPlayers) {

        this.startingSize = involvedPlayers.size();
        this.minimumPlayers = minimumPlayers;
        this.livingPlayersDamageMap = new HashMap<>();
        this.aboveThreshold = new HashMap<>();
        this.maximumPlayersAlive = involvedPlayers.size();
        for (Player involvedPlayer : involvedPlayers) {
            aboveThreshold.put(involvedPlayer,true);
            livingPlayersDamageMap.put(involvedPlayer,0.0);
        }
        this.graceTime = System.currentTimeMillis()+(1*60*1000); // starts after 5 minutes
        this.timeBetweenTicksMinutes = timeBetweenTicksMinutes;
    }



    public void start() {
        for (Player p : livingPlayersDamageMap.keySet()) {
            p.sendMessage(C.t(C.ICE_BLUE + "❄ " + C.LIGHT_ICE_BLUE + "You feel the cold down to your bones"));
        }

        new BukkitRunnable() {

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
                    List<Player> playersArranged = sortHashMapByValue(livingPlayersDamageMap);
                    if (playersArranged.size() > maximumPlayersAlive) {
                        damageTick++;
                        Double thresholdDamage = 0.0;
                        thresholdDamage = livingPlayersDamageMap.get(playersArranged.get(maximumPlayersAlive-1));

                        for (Player player : playersArranged) {
                            aboveThreshold.put(player, true);
                        }
                        for (int j = maximumPlayersAlive; j < playersArranged.size(); j++) {
                            aboveThreshold.put(playersArranged.get(j), false);
                        }

                        for (Player p : aboveThreshold.keySet()) {
                            if (aboveThreshold.get(p)) {
                                if (damageTick >= 10) {
                                    p.sendMessage(C.t( C.RED+"\uD83D\uDD25 " + C.LIGHT_RED + (livingPlayersDamageMap.get(p) - thresholdDamage) + " damage above threshold"));
                                    if (p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() < 20) {
                                        p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() + 1);
                                    }
                                }
                            } else {
                                if (damageTick >= 10) {
                                    p.sendMessage(C.t(C.ICE_BLUE+ "❄ " +C.LIGHT_ICE_BLUE + (thresholdDamage - livingPlayersDamageMap.get(p)) + " damage below threshold"));
                                    if (p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() > 1) {
                                        p.setFreezeTicks(80);
                                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE,1f,1f);
                                        p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() - 1);
                                    }
                                }

                            }


                        }
                        if (damageTick >= 10) {
                            damageTick = 0;
                        }

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
}
