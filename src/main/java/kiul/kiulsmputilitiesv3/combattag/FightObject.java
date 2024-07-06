package kiul.kiulsmputilitiesv3.combattag;

import com.sun.source.tree.Tree;
import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class FightObject {

    private ArrayList<UUID> participants;
    private ArrayList<String> everParticipated;
    private HashMap<String,Integer> hits;
    private HashMap<String,Double> damageDealt;
    private HashMap<String,Double> damageTaken;
    private HashMap<String,String> killer;
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

    public void addParticipant(Player p) {
        getParticipants().add(p.getUniqueId());
        getEverParticipated().add(p.getUniqueId().toString());
        getJoinTimestamp().put(p.getUniqueId().toString(),System.currentTimeMillis());
        getDamageDealt().put(p.getUniqueId().toString(),0.0);
        getDamageTaken().put(p.getUniqueId().toString(),0.0);
        getHits().put(p.getUniqueId().toString(),0);
    }
    public void removeParticipant(Player p, boolean die) {
        if (die) {
            getDieTimestamp().put(p.getUniqueId().toString(),System.currentTimeMillis());
        } else {
            if (participants.size() <= 2) {
                for (int i = 0; i < participants.size(); i++) {
                    UUID uuids = participants.get(i);
                    getLeaveTimestamp().put(uuids.toString(),System.currentTimeMillis()+i);
                }
                participants.clear();
                C.fightManager.disbandFight(this);
                return;
            } else {
                getLeaveTimestamp().put(p.getUniqueId().toString(),System.currentTimeMillis());
            }
        }

        participants.remove(p.getUniqueId());
        if (participants.size() <= 1 || getParticipatingTeams().size() < 2) {
            C.fightManager.disbandFight(this);
        }
    }

}
