package kiul.kiulsmputilitiesv3.towns.listeners;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.towns.Town;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class TeamDisband implements Listener {

    @EventHandler
    public void onTeamCommand (PlayerCommandPreprocessEvent e) {
        if (e.getMessage().contains("team disband") || e.getMessage().contains("team leave")) {
                Town.townPlaceCooldown.put(e.getPlayer(), System.currentTimeMillis() + (1000 * 60 * 60 * 24));
                if (C.getPlayerTeam(e.getPlayer()) != null) {
                    for (String entry : C.getPlayerTeam(e.getPlayer()).getEntries()) {
                        Player p = Bukkit.getPlayer(entry);
                        if (p != null) {
                            Town.townPlaceCooldown.put(p, System.currentTimeMillis() + (1000 * 60 * 60 * 24));
                        }
                    }
                }
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Town towns : Town.townsList) {
                        boolean destroy = true;
                        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                            if (team.getName().equalsIgnoreCase(towns.getTownUUID())) {
                                destroy = false;
                                break;
                            }
                        }
                        if (destroy) {
                            towns.destroy();
                        }
                    }
                }
            }.runTaskLater(C.plugin,1);
        }
    }
}
