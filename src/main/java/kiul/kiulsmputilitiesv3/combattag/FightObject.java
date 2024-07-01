package kiul.kiulsmputilitiesv3.combattag;

import com.sun.source.tree.Tree;
import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class FightObject {

    private ArrayList<UUID> participants;
    private ArrayList<UUID> everParticipated;
    private HashMap<UUID,Double> damageDealt;
    private HashMap<UUID,Double> damageTaken;
    private TreeMap<UUID,Long> joinTimestamp;
    private TreeMap<UUID,Long> leaveTimestamp;
    private TreeMap<UUID,Long> dieTimestamp;
    private long startTime;

    public FightObject(ArrayList<UUID> participants, long startTime) {
        this.participants = participants;
        this.startTime = startTime;
        this.everParticipated = (ArrayList<UUID>) participants.clone();
        this.damageDealt = new HashMap<>();
        this.damageTaken = new HashMap<>();
        this.joinTimestamp = new TreeMap<>();
        this.leaveTimestamp = new TreeMap<>();
        this.dieTimestamp = new TreeMap<>();
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

    public ArrayList<UUID> getEverParticipated() {
        return everParticipated;
    }

    public HashMap<UUID, Double> getDamageDealt() {
        return damageDealt;
    }

    public HashMap<UUID, Double> getDamageTaken() {
        return damageTaken;
    }

    public TreeMap<UUID, Long> getDieTimestamp() {
        return dieTimestamp;
    }

    public TreeMap<UUID, Long> getJoinTimestamp() {
        return joinTimestamp;
    }

    public TreeMap<UUID, Long> getLeaveTimestamp() {
        return leaveTimestamp;
    }

    public void addParticipant(Player p) {
        participants.add(p.getUniqueId());
        everParticipated.add(p.getUniqueId());
        getJoinTimestamp().put(p.getUniqueId(),System.currentTimeMillis());
    }
    public void removeParticipant(Player p, boolean die) {
        if (die) {
            getDieTimestamp().put(p.getUniqueId(),System.currentTimeMillis());
        } else {
            getLeaveTimestamp().put(p.getUniqueId(),System.currentTimeMillis());
        }

        participants.remove(p.getUniqueId());
        if (participants.size() <= 1 || getParticipatingTeams().size() < 2) {
            C.fightManager.disbandFight(this);
        }
    }

}
