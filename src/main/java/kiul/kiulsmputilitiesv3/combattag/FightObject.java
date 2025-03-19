package kiul.kiulsmputilitiesv3.combattag;

import com.sun.source.tree.Tree;
import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class FightObject {

    // participants
    private ArrayList<UUID> participants;
    private ArrayList<UUID> offlineParticipants;
    private ArrayList<String> everParticipated;

    // stats
    private HashMap<String,Integer> hits;
    
    private HashMap<String,String> killer;

    private HashMap<String,Double> damageDealt;
    private HashMap<String,Double> damageTaken;

    private HashMap<String,Double> durabilityDamageDealt;
    private HashMap<String,Double> durabilityDamageTaken;
    
    private HashMap<String,HashMap<String,Double>> damageDealtToPlayer;
    private HashMap<String,HashMap<String,Double>> damageTakenFromPlayer;

    private HashMap<String,HashMap<String,Double>> rangedDamageTakenFromPlayer;
    private HashMap<String,HashMap<String,Double>> rangedDamageDealtToPlayer;

    private HashMap<String,HashMap<String,Double>> explosiveDamageTakenFromPlayer;
    private HashMap<String,HashMap<String,Double>> explosiveDamageDealtToPlayer;

    private HashMap<String,HashMap<String,Double>> meleeDamageTakenFromPlayer;
    private HashMap<String,HashMap<String,Double>> meleeDamageDealtToPlayer;

    private HashMap<String,HashMap<String,Double>> maceDamageTakenFromPlayer;
    private HashMap<String,HashMap<String,Double>> maceDamageDealtToPlayer;

    private HashMap<String,HashMap<String,Double>> durabilityDamageTakenFromPlayer;
    private HashMap<String,HashMap<String,Double>> durabilityDamageDealtToPlayer;

    private HashMap<String,HashMap<String,Double>> untypedDamageTakenFromPlayer;
    private HashMap<String,HashMap<String,Double>> untypedDamageDealtToPlayer;

    // timestamps
    private TreeMap<String,Long> joinTimestamp;
    private TreeMap<String,Long> leaveTimestamp;
    private TreeMap<String,Long> dieTimestamp;
    private long startTime;

    public FightObject(ArrayList<UUID> participants, long startTime) {
        this.participants = participants;
        this.startTime = startTime;
        this.everParticipated = new ArrayList<>();
        this.damageDealt = new HashMap<>();
        this.damageTaken = new HashMap<>();
        this.joinTimestamp = new TreeMap<>();
        this.leaveTimestamp = new TreeMap<>();
        this.dieTimestamp = new TreeMap<>();
        this.hits = new HashMap<>();
        this.killer = new HashMap<>();
        this.offlineParticipants = new ArrayList<>();
        this.durabilityDamageDealt = new HashMap<>();
        this.durabilityDamageTaken = new HashMap<>();
        this.damageDealtToPlayer = new HashMap<>();
        this.damageTakenFromPlayer = new HashMap<>();
        this.rangedDamageDealtToPlayer = new HashMap<>();
        this.rangedDamageTakenFromPlayer = new HashMap<>();
        this.meleeDamageDealtToPlayer = new HashMap<>();
        this.meleeDamageTakenFromPlayer = new HashMap<>();
        this.maceDamageDealtToPlayer = new HashMap<>();
        this.maceDamageTakenFromPlayer = new HashMap<>();
        this.explosiveDamageDealtToPlayer = new HashMap<>();
        this.explosiveDamageTakenFromPlayer = new HashMap<>();
        this.durabilityDamageTakenFromPlayer = new HashMap<>();
        this.durabilityDamageDealtToPlayer = new HashMap<>();
        this.untypedDamageDealtToPlayer = new HashMap<>();
        this.untypedDamageTakenFromPlayer = new HashMap<>();

        for (int i = 0; i < participants.size(); i++) {
            UUID uuids = participants.get(i);
            everParticipated.add(uuids.toString());
            damageDealt.put(uuids.toString(),0.0);
            damageTaken.put(uuids.toString(),0.0);
            hits.put(uuids.toString(),0);
            joinTimestamp.put(uuids.toString(),System.currentTimeMillis()+i);
        }
    }

    public ArrayList<UUID> getParticipants() {return participants;}
    public ArrayList<Team> getParticipatingTeams() {
        ArrayList<Team> teams = new ArrayList<>();
        for (UUID uuids : getParticipants()) {
            teams.add(C.getPlayerTeam(Bukkit.getPlayer(uuids)));
        }
        return teams;
    }
    public boolean isPartaking(UUID p) {
        return participants.contains(p);
    }
    public long getStartTime() {return startTime;}
    public long getDuration() {return System.currentTimeMillis()-startTime;}

    public ArrayList<UUID> getOfflineParticipants() {
        return offlineParticipants;
    }

    public ArrayList<String> getEverParticipated() {
        return everParticipated;
    }

    public HashMap<String, Double> getDamageDealt() {
        return damageDealt;
    }

    public HashMap<String, Double> getDamageTaken() {
        return damageTaken;
    }

    public HashMap<String, String> getKiller() {
        return killer;
    }

    public TreeMap<String, Long> getDieTimestamp() {
        return dieTimestamp;
    }

    public TreeMap<String, Long> getJoinTimestamp() {
        return joinTimestamp;
    }

    public TreeMap<String, Long> getLeaveTimestamp() {
        return leaveTimestamp;
    }

    public HashMap<String, Integer> getHits() {
        return hits;
    }

    public HashMap<String, Double> getDurabilityDamageDealt() {
        return durabilityDamageDealt;
    }

    public HashMap<String, Double> getDurabilityDamageTaken() {
        return durabilityDamageTaken;
    }

    public HashMap<String, HashMap<String, Double>> getDamageDealtToPlayer() {
        return damageDealtToPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getDamageTakenFromPlayer() {
        return damageTakenFromPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getRangedDamageTakenFromPlayer() {
        return rangedDamageTakenFromPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getDurabilityDamageDealtToPlayer() {
        return durabilityDamageDealtToPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getDurabilityDamageTakenFromPlayer() {
        return durabilityDamageTakenFromPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getExplosiveDamageDealtToPlayer() {
        return explosiveDamageDealtToPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getExplosiveDamageTakenFromPlayer() {
        return explosiveDamageTakenFromPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getMaceDamageDealtToPlayer() {
        return maceDamageDealtToPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getMaceDamageTakenFromPlayer() {
        return maceDamageTakenFromPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getRangedDamageDealtToPlayer() {
        return rangedDamageDealtToPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getMeleeDamageDealtToPlayer() {
        return meleeDamageDealtToPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getMeleeDamageTakenFromPlayer() {
        return meleeDamageTakenFromPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getUntypedDamageDealtToPlayer() {
        return untypedDamageDealtToPlayer;
    }

    public HashMap<String, HashMap<String, Double>> getUntypedDamageTakenFromPlayer() {
        return untypedDamageTakenFromPlayer;
    }

    public void mergeFight (FightObject otherFight) {
        FightObject thisFight = this;
        Bukkit.getScheduler().runTaskAsynchronously(C.plugin, () -> {
            for (UUID otherFightParticipants : otherFight.getParticipants()) {
                // Execute the operations for each participant
                addParticipant(Bukkit.getPlayer(otherFightParticipants));
                FightMethods.startDistanceCheck(Bukkit.getPlayer(otherFightParticipants), thisFight);
                getDamageDealt().put(otherFightParticipants.toString(), otherFight.getDamageDealt().get(otherFightParticipants.toString()));
                getDamageTaken().put(otherFightParticipants.toString(), otherFight.getDamageTaken().get(otherFightParticipants.toString()));
                getJoinTimestamp().put(otherFightParticipants.toString(), otherFight.getJoinTimestamp().get(otherFightParticipants.toString()));
                getLeaveTimestamp().put(otherFightParticipants.toString(), otherFight.getLeaveTimestamp().get(otherFightParticipants.toString()));
                getDieTimestamp().put(otherFightParticipants.toString(), otherFight.getDieTimestamp().get(otherFightParticipants.toString()));
                getDurabilityDamageDealtToPlayer().put(otherFightParticipants.toString(), otherFight.getDurabilityDamageDealtToPlayer().get(otherFightParticipants.toString()));
                getDurabilityDamageTakenFromPlayer().put(otherFightParticipants.toString(), otherFight.getDurabilityDamageTakenFromPlayer().get(otherFightParticipants.toString()));
                getMeleeDamageDealtToPlayer().put(otherFightParticipants.toString(), otherFight.getMeleeDamageDealtToPlayer().get(otherFightParticipants.toString()));
                getMeleeDamageTakenFromPlayer().put(otherFightParticipants.toString(), otherFight.getMeleeDamageTakenFromPlayer().get(otherFightParticipants.toString()));
                getMaceDamageDealtToPlayer().put(otherFightParticipants.toString(), otherFight.getMaceDamageDealtToPlayer().get(otherFightParticipants.toString()));
                getMaceDamageTakenFromPlayer().put(otherFightParticipants.toString(), otherFight.getMaceDamageTakenFromPlayer().get(otherFightParticipants.toString()));
                getExplosiveDamageDealtToPlayer().put(otherFightParticipants.toString(), otherFight.getExplosiveDamageDealtToPlayer().get(otherFightParticipants.toString()));
                getExplosiveDamageTakenFromPlayer().put(otherFightParticipants.toString(), otherFight.getExplosiveDamageTakenFromPlayer().get(otherFightParticipants.toString()));
                getRangedDamageDealtToPlayer().put(otherFightParticipants.toString(), otherFight.getRangedDamageDealtToPlayer().get(otherFightParticipants.toString()));
                getRangedDamageTakenFromPlayer().put(otherFightParticipants.toString(), otherFight.getRangedDamageTakenFromPlayer().get(otherFightParticipants.toString()));
                getDamageTakenFromPlayer().put(otherFightParticipants.toString(), otherFight.getDamageTakenFromPlayer().get(otherFightParticipants.toString()));
                getDamageDealtToPlayer().put(otherFightParticipants.toString(), otherFight.getDamageDealtToPlayer().get(otherFightParticipants.toString()));
                getUntypedDamageDealtToPlayer().put(otherFightParticipants.toString(), otherFight.getUntypedDamageDealtToPlayer().get(otherFightParticipants.toString()));
                getUntypedDamageTakenFromPlayer().put(otherFightParticipants.toString(), otherFight.getUntypedDamageTakenFromPlayer().get(otherFightParticipants.toString()));

                getDurabilityDamageTaken().put(otherFightParticipants.toString(), otherFight.getDurabilityDamageTaken().get(otherFightParticipants.toString()));
                getDurabilityDamageDealt().put(otherFightParticipants.toString(), otherFight.getDurabilityDamageDealt().get(otherFightParticipants.toString()));

            }

            // After the loop is finished, schedule the disbanding task on the main thread
            Bukkit.getScheduler().runTask(C.plugin, () -> {
                C.fightManager.disbandFight(otherFight);
            });
        });
        
    }
    
    public void increaseStat (HashMap<String, HashMap<String, Double>> statMap,Player primary,Player secondary, double amount) {
        if (statMap.get(primary.getUniqueId().toString()) == null) {
            statMap.put(primary.getUniqueId().toString(),new HashMap<>());    
        }
        if (statMap.get(primary.getUniqueId().toString()).get(secondary.getUniqueId().toString()) == null) {
            statMap.get(primary.getUniqueId().toString()).put(secondary.getUniqueId().toString(),amount);
            return;
        }
        statMap.get(primary.getUniqueId().toString()).put(secondary.getUniqueId().toString(),statMap.get(primary.getUniqueId()).get(secondary.getUniqueId().toString())+amount);
        
    }
    public void increaseStat (HashMap<String, Double> statMap,Player primary, double amount) {
        if (statMap.get(primary.getUniqueId().toString()) == null) {
            statMap.put(primary.getUniqueId().toString(),amount);
            return;
        }
        statMap.put(primary.getUniqueId().toString(),statMap.get(primary.getUniqueId().toString())+amount);
    }

    public void addParticipant(Player p) {
        getParticipants().add(p.getUniqueId());
        getEverParticipated().add(p.getUniqueId().toString());
        getJoinTimestamp().put(p.getUniqueId().toString(),System.currentTimeMillis());
        getDamageDealt().put(p.getUniqueId().toString(),0.0);
        getDamageTaken().put(p.getUniqueId().toString(),0.0);
        getHits().put(p.getUniqueId().toString(),0);
    }
    public void removeParticipant(UUID uuid, boolean die) {
        if (die) {
            getDieTimestamp().put(uuid.toString(),System.currentTimeMillis());
        } else {
            if (participants.size() <= 2) {
                for (int i = 0; i < participants.size(); i++) {
                    UUID uuids = participants.get(i);
                    getLeaveTimestamp().put(uuids.toString(),System.currentTimeMillis()+i);
                }
                participants.clear();
                offlineParticipants.clear();
                C.fightManager.disbandFight(this);
                return;
            } else {
                getLeaveTimestamp().put(uuid.toString(), System.currentTimeMillis());
            }
        }

        participants.remove(uuid);
        if (participants.size() <= 1 || getParticipatingTeams().size() < 2) {
            C.fightManager.disbandFight(this);
        }
    }

    public void disband() {
        participants.clear();
        offlineParticipants.clear();
        C.fightManager.killFight(this);
    }
}
