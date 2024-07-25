package kiul.kiulsmputilitiesv3.combattag;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.stats.StatDB;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FightMethods {

    public static HashMap<UUID,Long> lastPearlThrow = new HashMap<>();
    public static HashMap<UUID,Integer> fastPearlThrow = new HashMap<>();

    public static void startDistanceCheck (Player p,FightObject fight) {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean nearby = false;
                if (fight != null && fight.isPartaking(p.getUniqueId())) {
                    for (int i = 0; i < fight.getParticipants().size(); i++) {
                        if (Bukkit.getPlayer(fight.getParticipants().get(i)) == null) {
                            fight.removeParticipant(fight.getParticipants().get(i),false);
                            fight.getOfflineParticipants().add(fight.getParticipants().get(i));
                            break;
                        }
                        if (Bukkit.getPlayer(fight.getParticipants().get(i)) != null && Bukkit.getPlayer(fight.getParticipants().get(i)) != p) {
                            if (p.getWorld() != Bukkit.getPlayer(fight.getParticipants().get(i)).getWorld()) {
                                nearby = false;
                            } else {
                                if (p.getLocation().distance(Bukkit.getPlayer(fight.getParticipants().get(i)).getLocation()) < 500) {
                                    nearby = true;
                                }
                            }
                            if (!nearby) {
                                HashMap<Team, List<Player>> team = C.sortTeams(fight.getParticipants());
                                int numEnemies = fight.getParticipants().size() - team.get(C.getPlayerTeam(p)).size();
                                if (numEnemies > team.get(C.getPlayerTeam(p)).size()) {
                                    StatDB.writePlayer(p.getUniqueId(), "stat_run", (int) StatDB.readPlayer(p.getUniqueId(), "stat_run") + 1);
                                }
                                fight.removeParticipant(p.getUniqueId(),false);
                                cancel();
                                return;
                            }
                        }

                    }


                } else {
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(C.plugin,20,60);
    }
}
