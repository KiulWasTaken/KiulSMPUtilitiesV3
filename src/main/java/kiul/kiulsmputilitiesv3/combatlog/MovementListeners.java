package kiul.kiulsmputilitiesv3.combatlog;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MovementListeners implements Listener {

    @EventHandler
    public void checkForMovement (PlayerMoveEvent e) {
        if (e.getTo().distance(e.getFrom()) > 0.01) {
            if (C.logoutTimer.contains(e.getPlayer())) {
                C.logoutTimer.remove(e.getPlayer());
            }
            if (C.loggingOut.contains(e.getPlayer())) {
                C.loggingOut.remove(e.getPlayer());
            }
        }
    }
}
