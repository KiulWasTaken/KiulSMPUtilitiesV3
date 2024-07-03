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

        String fightUUID = UUID.randomUUID().toString();

        PersistentData.get().set("recaps."+fightUUID+".starttime",fightObject.getStartTime());
        PersistentData.get().set("recaps."+fightUUID+".endtime",System.currentTimeMillis());
        PersistentData.get().createSection("recaps."+fightUUID+".damagedealt",fightObject.getDamageDealt());
        PersistentData.get().createSection("recaps."+fightUUID+".damagetaken",fightObject.getDamageTaken());
        PersistentData.get().set("recaps."+fightUUID+".participants",fightObject.getEverParticipated());
        PersistentData.get().createSection("recaps."+fightUUID+".jointime",fightObject.getJoinTimestamp());
        PersistentData.get().createSection("recaps."+fightUUID+".leavetime",fightObject.getLeaveTimestamp());
        PersistentData.get().createSection("recaps."+fightUUID+".dietime",fightObject.getDieTimestamp());
        PersistentData.get().createSection("recaps."+fightUUID+".hits",fightObject.getHits());
        PersistentData.save();
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
