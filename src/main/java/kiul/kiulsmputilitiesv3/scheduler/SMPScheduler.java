package kiul.kiulsmputilitiesv3.scheduler;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.ConfigData;
import kiul.kiulsmputilitiesv3.config.ScheduleConfig;
import kiul.kiulsmputilitiesv3.end_fight.CloseEndDimension;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.pat.pattyEssentialsV3.Enums.MenuEnum;
import org.pat.pattyEssentialsV3.Listeners.ClickInv;
import org.pat.pattyEssentialsV3.PattyEssentialsV3;
import org.pat.pattyEssentialsV3.Utils;

import java.util.HashMap;
import java.util.stream.Collectors;

public class SMPScheduler {

    public static BossBar graceBossBar = BossBar.bossBar(Component.text("⚔ PVP Enables In ").color(NamedTextColor.RED),
            0, BossBar.Color.RED, BossBar.Overlay.NOTCHED_6);
    public static BossBar netheriteEnableBossBar = BossBar.bossBar(Component.text("Netherite Enables In ").color(NamedTextColor.GOLD),
            0, BossBar.Color.YELLOW, BossBar.Overlay.NOTCHED_6);
    public static BossBar dragonFightBossBar = BossBar.bossBar(Component.text("End (Dragon Fight) Opening In ").color(NamedTextColor.LIGHT_PURPLE),
            0, BossBar.Color.PINK, BossBar.Overlay.NOTCHED_6);
    public static BossBar endClosesBossBar = BossBar.bossBar(Component.text("End Closes In ").color(NamedTextColor.LIGHT_PURPLE),
            0, BossBar.Color.PINK, BossBar.Overlay.NOTCHED_6);
    public static BossBar endOpensBossBar = BossBar.bossBar(Component.text("End Opens In ").color(NamedTextColor.DARK_PURPLE),
            0, BossBar.Color.PURPLE, BossBar.Overlay.NOTCHED_6);
    public static BossBar FinalFightBossBar = BossBar.bossBar(Component.text("Final Fight Border Closing In ").color(NamedTextColor.BLUE),
            0, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_6);

    static HashMap<String,BossBar> eventBossBars = new HashMap<>() {{
        put("grace_ends",graceBossBar);
        put("netherite_enables",netheriteEnableBossBar);
        put("dragon_opens",dragonFightBossBar);
        put("end_closes",endClosesBossBar);
        put("end_opens",endOpensBossBar);
        put("final_fight",FinalFightBossBar);
    }};

    public static void initializeScheduleConfig() {
        if (ScheduleConfig.get().get("event.grace_ends.name") == null) {
            ScheduleConfig.get().set("start_time", null);
            ScheduleConfig.get().set("event.grace_ends.time", 24);
            ScheduleConfig.get().set("event.grace_ends.name", "<red>⚔ PVP Enables In ");
            ScheduleConfig.get().set("event.netherite_enables.time", 48);
            ScheduleConfig.get().set("event.netherite_enables.name", "<gold>⚔ Netherite Enables In ");
            ScheduleConfig.get().set("event.dragon_opens.time", 96);
            ScheduleConfig.get().set("event.dragon_opens.name", "<light_purple>End (Dragon Fight) Opening In ");
            ScheduleConfig.get().set("event.end_closes.time", 120);
            ScheduleConfig.get().set("event.end_closes.name", "<light_purple>End Closes In ");
            ScheduleConfig.get().set("event.end_opens.time", 168);
            ScheduleConfig.get().set("event.end_opens.name", "<dark_purple>End Opens In ");
            ScheduleConfig.get().set("event.final_fight.time", 216);
            ScheduleConfig.get().set("event.final_fight.name", "<blue>Final Fight Border Closing In ");
            ScheduleConfig.save();
        }
    }

    public static HashMap<String,int[]> getTimeUntilEvents() {
        HashMap<String,int[]> eventTimes = new HashMap<>();
        for (String event : ScheduleConfig.get().getConfigurationSection("event").getKeys(false)) {
            long startTime = ScheduleConfig.get().getLong("start_time");
            if (startTime+((long)ScheduleConfig.get().getInt("event." + event + ".time") * 60 * 60 * 1000) - System.currentTimeMillis() > 0) {
                long timeUntilEvent = startTime+((long)ScheduleConfig.get().getInt("event." + event + ".time") * 60 * 60 * 1000) - System.currentTimeMillis();
                float progress = (float) System.currentTimeMillis() / (startTime + timeUntilEvent);
                eventBossBars.get(event).progress(progress);
                int[] times = C.splitTimestamp(startTime+((long)ScheduleConfig.get().getInt("event." + event + ".time") * 60 * 60 * 1000));
                eventTimes.put(event,times);

            }
        }
        return eventTimes;
    }

    public static void initializeScheduler () {
        if (ConfigData.get().getBoolean("scheduler") && ScheduleConfig.get().get("start_time") != null) {
            // hardcode some events with times in schedule config
            BukkitTask runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    for (String event : ScheduleConfig.get().getConfigurationSection("event").getKeys(false)) {
                        long startTime = ScheduleConfig.get().getLong("start_time");
                        if (startTime+((long)ScheduleConfig.get().getInt("event." + event + ".time") * 60 * 60 * 1000) - System.currentTimeMillis() > 0) {
                            long timeUntilEvent = startTime+((long)ScheduleConfig.get().getInt("event." + event + ".time") * 60 * 60 * 1000) - System.currentTimeMillis();
                            if (timeUntilEvent < 24*60*60*1000) {
                                float progress = (float) System.currentTimeMillis() /(startTime+timeUntilEvent);
                                eventBossBars.get(event).progress(progress);
                                int[] times = C.splitTimestamp(startTime+((long)ScheduleConfig.get().getInt("event." + event + ".time") * 60 * 60 * 1000));
                                Component time = Component.text(times[0] + " Hours " + times[1] + " Minutes " + times[2] + " Seconds").color(NamedTextColor.WHITE);
                                eventBossBars.get(event).name(MiniMessage.miniMessage().deserialize(ScheduleConfig.get().getString("event."+event+".name")).append(time));

                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    eventBossBars.get(event).addViewer(onlinePlayer);
                                }
                            } else {
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    eventBossBars.get(event).removeViewer(onlinePlayer);
                                }
                                if (!ScheduleConfig.get().getBoolean(event+".occured")) {
                                    ScheduleConfig.get().set(event+".occurred",true);
                                    ScheduleConfig.save();
                                    switch (event) {
                                        case "grace_ends":
                                            ClickInv.clickCheck(null, MenuEnum.grace.getPath(), ClickType.LEFT, "Grace", MenuEnum.grace);
                                            break;
                                        case "netherite_enable":
                                            ClickInv.clickCheck(null, MenuEnum.netherite.getPath(), ClickType.LEFT, "netherite", MenuEnum.netherite);
                                            break;
                                        case "dragon_opens","end_opens":
                                            ClickInv.clickCheck(null, MenuEnum.endDimension.getPath(), ClickType.LEFT, "end", MenuEnum.endDimension);
                                            break;
                                        case "end_closes":
                                            ClickInv.clickCheck(null, MenuEnum.endDimension.getPath(), ClickType.LEFT, "Grace", MenuEnum.endDimension);
                                            CloseEndDimension.deleteEndPortalBlocks(Bukkit.getWorld("world"));
                                            CloseEndDimension.increaseEndBorder();
                                            break;
                                        case "final_fight":
                                            // final fight method 😨
                                            break;
                                    }
                                }

                            }
                        }
                    }
                    // go through all listed events in schedule config
                    // if they are within x time then show the time remaining until event to the entire server with a bossbar
                    // up to 2 bossbars showing server events at once - crates can spawn a 3rd bossbar if 2 are already on.
                }
            }.runTaskTimer(C.plugin,0,20);

            C.smpScheduler = runnable;
        }
    }
}
