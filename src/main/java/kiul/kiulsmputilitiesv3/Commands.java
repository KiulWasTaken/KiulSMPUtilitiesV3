package kiul.kiulsmputilitiesv3;

import kiul.kiulsmputilitiesv3.accessories.AccessoryMethods;
import kiul.kiulsmputilitiesv3.combattag.RecapInventory;
import kiul.kiulsmputilitiesv3.config.AccessoryData;
import kiul.kiulsmputilitiesv3.config.ConfigData;
import kiul.kiulsmputilitiesv3.crates.CrateMethods;
import kiul.kiulsmputilitiesv3.crates.CrateTypeEnum;
import kiul.kiulsmputilitiesv3.end_fight.CloseEndDimension;
import kiul.kiulsmputilitiesv3.featuretoggle.FeatureInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        }
        return null;
    }
}
