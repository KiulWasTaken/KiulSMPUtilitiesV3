package kiul.kiulsmputilitiesv3.accessories;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NimbleAccessory implements Listener {

    @EventHandler
    public void weakenPlayerAttacks (EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) {
            if (AccessoryMethods.getActiveAccessoryIdentifier(p).equalsIgnoreCase("nimble")) {
                e.setDamage(e.getDamage()-(e.getDamage()/10));
            }
        }
     }
}
