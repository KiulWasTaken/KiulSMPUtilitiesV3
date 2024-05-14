package kiul.kiulsmputilitiesv3.combattag;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FightManager {
    private static List<Fight> fights;

    public FightManager() {
        this.fights = new ArrayList<>();
    }

    public Fight createFight(ArrayList<UUID> participants) {
        Fight fight = new Fight(participants,System.currentTimeMillis());
        fights.add(fight);
        return fight;
    }

    public void disbandFight(Fight fight) {
        fights.remove(fight);
    }

    public Fight findFightForMember(Player p) {
        for (Fight fight : fights) {
            if (fight.isPartaking(p.getUniqueId())) {
                return fight;
            }
        }
        return null;  // Player is not in any party
    }
    public boolean playerIsInFight(Player p) {
        for (Fight fight : fights) {
            if (fight.isPartaking(p.getUniqueId())) {
                return true;
            }
        }
        return false;  // Player is not in any party
    }
}
