package kiul.kiulsmputilitiesv3;

import kiul.kiulsmputilitiesv3.accessories.AccessoryMethods;
import kiul.kiulsmputilitiesv3.combattag.RecapInventory;
import kiul.kiulsmputilitiesv3.config.*;
import kiul.kiulsmputilitiesv3.crates.CrateMethods;
import kiul.kiulsmputilitiesv3.crates.CrateTypeEnum;
import kiul.kiulsmputilitiesv3.locatorbar.LocatorBar;
import kiul.kiulsmputilitiesv3.locatorbar.LocatorConfigInventory;
import kiul.kiulsmputilitiesv3.locatorbar.Waypoint;
import kiul.kiulsmputilitiesv3.server_events.CloseEndDimension;
import kiul.kiulsmputilitiesv3.featuretoggle.FeatureInventory;
import kiul.kiulsmputilitiesv3.server_events.FinalFight;
import kiul.kiulsmputilitiesv3.server_events.SuddenDeath;
import kiul.kiulsmputilitiesv3.towns.Town;
import kiul.kiulsmputilitiesv3.towns.augments.AugmentEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;
import org.pat.pattyEssentialsV3.Enums.MenuEnum;
import org.pat.pattyEssentialsV3.Listeners.ClickInv;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Commands implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {
        Player p = (Player) commandSender;

        if (label.equalsIgnoreCase("safelog") || label.equalsIgnoreCase("logout")) {

            int combatMultiplier = 1;

            if (C.fightManager.playerIsInFight(p)) {
                combatMultiplier = 2;
            }

            p.sendMessage(C.chatColour + "you are now logging out, do not move.");
            C.logoutTimer.add(p);


            long currentTimeMillis = System.currentTimeMillis();
            int finalCombatMultiplier = combatMultiplier;
            new BukkitRunnable() {
                long despawnTime = currentTimeMillis + (C.NPC_DESPAWN_SECONDS * 1000 * finalCombatMultiplier);
                ArmorStand stand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().add(0,2.2,0), EntityType.ARMOR_STAND);

                @Override
                public void run() {

                    if (C.logoutTimer.contains(p)) {
                        if (System.currentTimeMillis() < despawnTime) {
                            stand.setMarker(true);
                            stand.setVisible(false);
                            stand.setCustomNameVisible(true);
                            stand.setInvulnerable(true);
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.format("%02d : %02d",
                                    TimeUnit.MILLISECONDS.toMinutes(despawnTime - System.currentTimeMillis()),
                                    TimeUnit.MILLISECONDS.toSeconds(despawnTime - System.currentTimeMillis()) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(despawnTime - System.currentTimeMillis()))
                            )));
                            stand.setCustomName(ChatColor.RED + "Logging Out " + ChatColor.GRAY + "- " + ChatColor.YELLOW + String.format("%02d : %02d",
                                    TimeUnit.MILLISECONDS.toMinutes(despawnTime - System.currentTimeMillis()),
                                    TimeUnit.MILLISECONDS.toSeconds(despawnTime - System.currentTimeMillis()) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(despawnTime - System.currentTimeMillis()))
                            ));
                            if (p.isSneaking()) {
                                C.logoutTimer.remove(p);
                                stand.remove();
                            }

                        } else {
                            C.logoutTimer.remove(p);
                            p.sendMessage(C.chatColour + "you can now leave the server safely.");
                            p.kick();
                            stand.remove();
                            cancel();
                        }
                    } else {
                        p.sendMessage(C.chatColour + "logout interrupted..");
                        stand.remove();
                        cancel();
                    }
                }
            }.runTaskTimer(C.plugin, 0, 5);
        }

        switch (label) {
            case "locator_bar":
                LocatorConfigInventory.open(p);
                break;
            case "close-end":
                CloseEndDimension.deleteEndPortalBlocks(p.getWorld());
                break;
            case "give-accessory":
                if (p.hasPermission("kiulsmp.debug")) {
                    AccessoryMethods.giveAccessory(p, args[0]);
                }
                break;
            case "give-ingredient":
                if (p.hasPermission("kiulsmp.debug")) {
                    AccessoryMethods.giveIngredient(p, args[0]);
                }
                break;
            case "accessory":
                if (!ConfigData.get().getBoolean("accessories")) {break;}
                AccessoryMethods.equipAccessory(p);
                break;
            case "kmenu":
                if (p.hasPermission("kiulsmp.debug")) {
                    FeatureInventory.open(p,false);
                }
                break;
            case "toggle-sounds":
                if (args.length == 0) {
                    AccessoryData.get().set(p.getUniqueId() + ".sounds.all", !AccessoryData.get().getBoolean(p.getUniqueId() + ".sounds.all"));
                    if (AccessoryData.get().getBoolean(p.getUniqueId() + ".sounds.all")) {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Tracking Sounds " + ChatColor.GREEN + "ENABLED"));
                    } else {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Tracking Sounds " + ChatColor.RED + "DISABLED"));
                    }
                }
                if (args.length > 0) {
                    switch (args[0]) {
                        case "teammates":
                            AccessoryData.get().set(p.getUniqueId() + ".sounds.teammates", !AccessoryData.get().getBoolean(p.getUniqueId() + ".sounds.teammates"));
                            if (AccessoryData.get().getBoolean(p.getUniqueId() + ".sounds.teammates")) {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Teammate Tracking Sounds " + ChatColor.GREEN + "ENABLED"));
                            } else {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Teammate Tracking Sounds " + ChatColor.RED + "DISABLED"));
                            }
                            break;
                        case "enemies":
                            AccessoryData.get().set(p.getUniqueId() + ".sounds.enemies", !AccessoryData.get().getBoolean(p.getUniqueId() + ".sounds.enemies"));
                            if (AccessoryData.get().getBoolean(p.getUniqueId() + ".sounds.enemies")) {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Enemy Tracking Sounds " + ChatColor.GREEN + "ENABLED"));
                            } else {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Enemy Tracking Sounds " + ChatColor.RED + "DISABLED"));
                            }
                            break;
                        case "all":
                            AccessoryData.get().set(p.getUniqueId()+".sounds.all",!AccessoryData.get().getBoolean(p.getUniqueId()+".sounds.all"));
                            if (AccessoryData.get().getBoolean(p.getUniqueId() + ".sounds.all")) {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Tracking Sounds " + ChatColor.GREEN + "ENABLED"));
                            } else {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Tracking Sounds " + ChatColor.RED + "DISABLED"));
                            }
                            break;
                        case "self":
                            AccessoryData.get().set(p.getUniqueId() + ".sounds.self", !AccessoryData.get().getBoolean(p.getUniqueId() + ".sounds.self"));
                            if (AccessoryData.get().getBoolean(p.getUniqueId() + ".sounds.self")) {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Self Tracking Sounds " + ChatColor.GREEN + "ENABLED"));
                            } else {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Self Tracking Sounds " + ChatColor.RED + "DISABLED"));
                            }
                            break;
                    }
                }
                break;
            case "test-crate":
                if (p.hasPermission("kiulsmp.debug")) {
                    CrateMethods.createCrate(p.getWorld(), args[0],true);
                }
                break;
            case "debug-event":
                if (p.hasPermission("kiulsmp.debug")) {
                    switch (args[0]) {
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
                            FinalFight.beginShrinkWorldBorder(20,150);
                            // final fight method 😨
                            break;
                        case "border_closed":
                            WorldData.get().set("border_closed",true);
                            WorldData.save();
                            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                                if (p.isOnline()) {
                                    Bukkit.getPlayer(offlinePlayer.getName()).sendMessage(C.LIGHT_PASTEL_PINK+"\uD83D\uDD31 " + C.PASTEL_PINK + ChatColor.BOLD+ "BORDER CLOSED! " + C.LIGHT_PASTEL_PINK + "Late joiners will die and logouts will be punished! Fight to the death!");
                                }
                                PersistentData.get().set(offlinePlayer.getUniqueId().toString()+".final_fight.time_quit",System.currentTimeMillis());
                            }
                            break;
                        case "sudden_death":
                            List<Player> suddenDeathPlayers = new ArrayList<>();
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                if (onlinePlayer.getGameMode().equals(GameMode.SURVIVAL)) {
                                    suddenDeathPlayers.add(onlinePlayer);
                                }
                            }
                            C.suddenDeath = new SuddenDeath(suddenDeathPlayers,1,2,true);
                            C.suddenDeath.start();
                            break;
                        case "reset":
                            WorldData.get().set("final_fight", false);
                            WorldData.get().set("border_closed",false);
                            WorldData.save();
                            if (C.suddenDeath != null) {
                                for (Player livingPlayers : C.suddenDeath.getLivingPlayersDamageMap().keySet()) {
                                    if (livingPlayers == null) continue;
                                    livingPlayers.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
                                }

                                C.suddenDeath.stop();
                                C.suddenDeath = null;
                            }
                            break;
                        default:
                            p.sendMessage(C.failPrefix + "please enter the name of a valid scheduled event");
                    }
                }
                break;
            case "spawn-crate":
                if (p.hasPermission("kiulsmp.debug")) {
                    CrateMethods.createCrate(p.getWorld(), args[0],false);
                }
                break;
            case "populate-crate":
                if (p.hasPermission("kiulsmp.debug")) {
                    CrateTypeEnum crateType = CrateMethods.getCrate(args[0]);
                    CrateMethods.populateCrate(crateType, null, p);
                }
                break;
            case "debug-waypoints":
                if (p.hasPermission("kiulsmp.debug")) {
                    for (Waypoint w : LocatorBar.playerLocatorBar.get(p.getUniqueId()).getWaypoints()) {
                        p.sendMessage(w.getName());
                    }

                }
                break;
            case "debug-augment":
                if (p.hasPermission("kiulsmp.debug")) {
                    Town town = null;
                    for (Town towns : Town.townsList) {
                        if (towns.protectedAreaContains(p.getLocation())) {
                            town = towns;
                        }
                    }
                    if (town == null) {
                        p.sendMessage(C.failPrefix+"need to be in a town");
                    }
                    AugmentEnum.augmentItem(p.getInventory().getItemInMainHand(),town,AugmentEnum.getAugment(args[0]));
                }
                break;
            case "recaps":
                p.sendMessage(Component.text("recaps are currently unstable").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC));
                RecapInventory.open(p,0);
                break;
            case "translate":
                p.sendMessage(C.t(args[0]));
                break;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command cmd, String label, String[] args) {
        Player p = (Player) commandSender;
        ArrayList<String> argsList = new ArrayList<>();

        switch (label) {
            case "test-crate","spawn-crate":
                for (CrateTypeEnum crateTypeEnum : CrateTypeEnum.values()) {
                    argsList.add(crateTypeEnum.getIdentifier());
                }
                return argsList;
            case "give-accessory":
                argsList.add("[ACCESSORY-IDENTIFIER]");
                return argsList;
            case "populate-crate":
                argsList.add("[CRATE-IDENTIFIER]");
                return argsList;
            case "translate":
                argsList.add("[TEXT-TO-TRANSLATE]");
                return argsList;
            case "toggle-sounds":
                argsList.add("all");
                argsList.add("enemies");
                argsList.add("teammates");
                argsList.add("self");
                return argsList;
            case "debug-event":
                for (String event : ScheduleConfig.get().getConfigurationSection("event").getKeys(false)) {
                    argsList.add(event);
                    argsList.add("reset");
                }
                return argsList;
            case "debug-augment":
                for (AugmentEnum augmentEnum : AugmentEnum.values()) {
                    argsList.add(augmentEnum.getLocalName());
                }
                return argsList;
        }
        return null;
    }
}
