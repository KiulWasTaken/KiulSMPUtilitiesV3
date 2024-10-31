package kiul.kiulsmputilitiesv3.advancements;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.PersistentData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

import java.util.function.UnaryOperator;


public class AdvancementMethods {

    public static void grantAdvancement (Player p,String advancementIdentifier) {
        if (!PersistentData.get().getBoolean(p.getUniqueId() + ".advancements." + advancementIdentifier)) {

            for (AdvancementEnum advancement : AdvancementEnum.values()) {
                if (advancement.getIdentifier().equalsIgnoreCase(advancementIdentifier)) {
                    Component message;
                    String color;
                    if (advancement.isRare()) {
                        color = "<dark_purple>";
                        message = MiniMessage.miniMessage().deserialize(C.getPlayerTeamPrefix(p) + p.getDisplayName() + " has completed the challenge <dark_purple>" + advancement.getDisplayText());
                        p.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE,1,1);
                    } else {
                        color = "<green>";
                        message = MiniMessage.miniMessage().deserialize(C.getPlayerTeamPrefix(p) + p.getDisplayName() + " has made the advancement <green>" + advancement.getDisplayText());
                        p.playSound(p, Sound.UI_TOAST_IN,1,1);
                    }
                    message = Component.empty().append(message).hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize(color+advancement.getHoverText())));

                    Bukkit.broadcast(message);

                    ExperienceOrb experienceOrb = (ExperienceOrb) p.getWorld().spawnEntity(p.getLocation(), EntityType.EXPERIENCE_ORB);
                    experienceOrb.setExperience(advancement.getRewardExp());

                    PersistentData.get().set(p.getUniqueId()+".advancements."+advancementIdentifier,true);
                    PersistentData.save();
                }
            }
        }
    }
}
