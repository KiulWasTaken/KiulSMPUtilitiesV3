package kiul.kiulsmputilitiesv3.server_events;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SuddenDeath {

    int startingSize; // the starting number of players to base percentage
    int numSpacesAboveThreshold;
    int numSpacesBelowThreshold;
    int minimumPlayers; // after the player amount reaches this, the threshold will no longer exclude players.
    int maximumPlayersAlive; // decreases over time. cannot go below minimumPlayers.
    int tick;
    int timeBetweenTicksMinutes;
    long startTime;

    HashMap<Player,Boolean> aboveThreshold;
    TreeMap<Player,Double> livingPlayersDamageMap;


    public SuddenDeath(List<Player> involvedPlayers, HashMap<Integer,Integer> maximumPlayersAliveAtTick, int timeBetweenTicksMinutes, int minimumPlayers) {

        this.startingSize = involvedPlayers.size();
        this.minimumPlayers = minimumPlayers;
        this.livingPlayersDamageMap = new TreeMap<>();
        this.aboveThreshold = new HashMap<>();
        this.maximumPlayersAlive = involvedPlayers.size();
        for (Player involvedPlayer : involvedPlayers) {
            aboveThreshold.put(involvedPlayer,true);
            livingPlayersDamageMap.put(involvedPlayer,0.0);
        }
        this.startTime = System.currentTimeMillis()+(5*60*1000); // starts after 5 minutes

        this.timeBetweenTicksMinutes = timeBetweenTicksMinutes;
    }



    public void start() {


        new BukkitRunnable() {

            int tickMinutes = 0;
            int damageTick = 0;
            int tickSeconds = 0;
            int stageTick = livingPlayersDamageMap.size()/(5+livingPlayersDamageMap.size()/15);
// 30
// 23
//            17
//            12
//
//            10
//            9

            @Override
            public void run() {
                if (System.currentTimeMillis() > startTime) {
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

                    if (livingPlayersDamageMap.keySet().size() > maximumPlayersAlive) {
                        damageTick++;
                        Double thresholdDamage = 0.0;
                        for (Map.Entry entry : livingPlayersDamageMap.entrySet()) {
                            Player player = (Player) entry.getKey();
                            if (aboveThreshold.get(player)) {
                                thresholdDamage = (Double) entry.getValue();
                                break;
                            }
                        }
                        for (Player player : livingPlayersDamageMap.keySet()) {
                            aboveThreshold.put(player, true);
                        }
                        int i = 1;
                        for (Map.Entry entry : livingPlayersDamageMap.entrySet()) {
                            if (i++ < livingPlayersDamageMap.keySet().size() - maximumPlayersAlive) {
                                Player player = (Player) entry.getKey();
                                aboveThreshold.put(player, false);
                            }
                        }

                        for (Player p : aboveThreshold.keySet()) {
                            if (aboveThreshold.get(p)) {
                                p.sendMessage(C.t(C.LIGHT_RED + (livingPlayersDamageMap.get(p) - thresholdDamage) + " damage above threshold \uD83D\uDD25"));
                                if (damageTick >= 10) {
                                    if (p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() < 20) {
                                        p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() + 1);
                                    }
                                }
                            } else {
                                if (damageTick >= 10) {
                                    if (p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() > 1) {
                                        p.setFreezeTicks(100);
                                        p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() - 1);
                                    }
                                }
                                p.sendMessage(C.t(C.BLUE + (thresholdDamage - livingPlayersDamageMap.get(p)) + " damage below threshold ❄"));
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

}
