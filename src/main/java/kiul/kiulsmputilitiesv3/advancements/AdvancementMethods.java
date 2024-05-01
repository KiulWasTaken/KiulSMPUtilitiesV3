package kiul.kiulsmputilitiesv3.advancements;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;


public class AdvancementMethods {

    public static void grantAdvancement (Player p,String advancementIdentifier) {
        if (!PersistentData.get().getBoolean(p.getUniqueId() + ".advancements." + advancementIdentifier)) {

            for (AdvancementEnum advancement : AdvancementEnum.values()) {
                if (advancement.getIdentifier().equalsIgnoreCase(advancementIdentifier)) {
                    TextComponent message;
                    if (advancement.getRare()) {
                        message = new TextComponent(C.getPlayerTeamPrefix(p) + p.getDisplayName() + " has completed the challenge " + advancement.getDisplayText());
                        p.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE,1,1);
                    } else {
                        message = new TextComponent(C.getPlayerTeamPrefix(p) + p.getDisplayName() + " has made the advancement " + advancement.getDisplayText());
                        p.playSound(p, Sound.UI_TOAST_IN,1,1);
                    }
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(advancement.getHoverText())));
                    Bukkit.spigot().broadcast(message);
                    p.setTotalExperience(p.getTotalExperience()+500);

                    PersistentData.get().set(p.getUniqueId()+".advancements."+advancementIdentifier,true);
                    PersistentData.save();
                }
            }
        }
    }
}
