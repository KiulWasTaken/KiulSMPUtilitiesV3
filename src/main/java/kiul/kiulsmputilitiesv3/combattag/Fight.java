package kiul.kiulsmputilitiesv3.combattag;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.UUID;

public class Fight {

    private ArrayList<UUID> participants;
    private long startTime;

    public Fight(ArrayList<UUID> participants, long startTime) {
        this.participants = participants;
        this.startTime = startTime;
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

    public void addParticipant(Player p) {
        participants.add(p.getUniqueId());
    }
    public void removeParticipant(Player p) {
        participants.remove(p.getUniqueId());
        if (participants.size() <= 1 || getParticipatingTeams().size() < 2) {
            C.fightManager.disbandFight(this);
        }
    }

}
