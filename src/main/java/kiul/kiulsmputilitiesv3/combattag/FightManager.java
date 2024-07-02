package kiul.kiulsmputilitiesv3.combattag;

import kiul.kiulsmputilitiesv3.config.PersistentData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FightManager {
    private static List<FightObject> fightObjects;

    public FightManager() {
        this.fightObjects = new ArrayList<>();
    }

    public FightObject createFight(ArrayList<UUID> participants) {
        FightObject fightObject = new FightObject(participants,System.currentTimeMillis());
        fightObjects.add(fightObject);
        return fightObject;
    }

    public void disbandFight(FightObject fightObject) {

        UUID fightUUID = UUID.randomUUID();

        PersistentData.get().set("recaps."+fightUUID+".starttime",fightObject.getStartTime());
        PersistentData.get().set("recaps."+fightUUID+".endtime",System.currentTimeMillis());
        PersistentData.get().set("recaps."+fightUUID+".damagedealt",fightObject.getDamageDealt());
        PersistentData.get().set("recaps."+fightUUID+".damagetaken",fightObject.getDamageTaken());
        PersistentData.get().set("recaps."+fightUUID+".participants",fightObject.getEverParticipated());
        PersistentData.get().set("recaps."+fightUUID+".jointime",fightObject.getJoinTimestamp());
        PersistentData.get().set("recaps."+fightUUID+".leavetime",fightObject.getLeaveTimestamp());
        PersistentData.get().set("recaps."+fightUUID+".dietime",fightObject.getDieTimestamp());
        PersistentData.get().set("recaps."+fightUUID+".hits",fightObject.getHits());
        fightObjects.remove(fightObject);
    }

    public FightObject findFightForMember(Player p) {
        for (FightObject fightObject : fightObjects) {
            if (fightObject.isPartaking(p.getUniqueId())) {
                return fightObject;
            }
        }
        return null;  // Player is not in any party
    }
    public boolean playerIsInFight(Player p) {
        for (FightObject fightObject : fightObjects) {
            if (fightObject.isPartaking(p.getUniqueId())) {
                return true;
            }
        }
        return false;  // Player is not in any party
    }
}
