package kiul.kiulsmputilitiesv3;

import kiul.kiulsmputilitiesv3.accessories.AccessoryMethods;
import kiul.kiulsmputilitiesv3.config.AccessoryData;
import kiul.kiulsmputilitiesv3.crates.CrateMethods;
import kiul.kiulsmputilitiesv3.crates.CrateTypeEnum;
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
            p.sendMessage(C.chatColour + "you are now logging out, do not move.");
            C.logoutTimer.add(p);


            long currentTimeMillis = System.currentTimeMillis();
            new BukkitRunnable() {
                long despawnTime = currentTimeMillis + (C.npcDespawnTimeSeconds * 1000);
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
                            C.loggingOut.add(p);
                            p.sendMessage(C.chatColour + "you can now leave the server safely.");
                            stand.remove();
                            cancel();
                        }
                    } else {
                        p.sendMessage(C.chatColour + "logout interrupted..");
                        stand.remove();
                        cancel();
                    }
                }
            }.runTaskTimer(C.plugin, 0, 20);
        }

        switch (label) {
            case "give-accessory":
                AccessoryMethods.giveAccessory(p,args[0]);
                break;
            case "give-ingredient":
                AccessoryMethods.giveIngredient(p,args[0]);
                break;
            case "accessory":
                AccessoryMethods.equipAccessory(p);
                break;
            case "toggle-sounds":
                AccessoryData.get().set(p.getUniqueId()+".sounds.all",!AccessoryData.get().getBoolean(p.getUniqueId()+".sounds"));
                if (AccessoryData.get().getBoolean(p.getUniqueId()+".sounds.all")) {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Tracking Sounds " + ChatColor.GREEN + "ENABLED"));
                } else {
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Tracking Sounds " + ChatColor.RED + "DISABLED"));
                }
                switch (args[0]) {
                    case "teammates":
                        if (AccessoryData.get().getBoolean(p.getUniqueId()+".sounds.teammates")) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Teammate Tracking Sounds " + ChatColor.GREEN + "ENABLED"));
                        } else {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Teammate Tracking Sounds " + ChatColor.RED + "DISABLED"));
                        }
                        break;
                    case "enemies":
                        if (AccessoryData.get().getBoolean(p.getUniqueId()+".sounds.enemies")) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Enemy Tracking Sounds " + ChatColor.GREEN + "ENABLED"));
                        } else {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Enemy Tracking Sounds " + ChatColor.RED + "DISABLED"));
                        }
                        break;
                    case "all":
                        if (AccessoryData.get().getBoolean(p.getUniqueId()+".sounds.all")) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Tracking Sounds " + ChatColor.GREEN + "ENABLED"));
                        } else {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "Tracking Sounds " + ChatColor.RED + "DISABLED"));
                        }
                        break;
                    case "self":
                        if (AccessoryData.get().getBoolean(p.getUniqueId()+".sounds.self")) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "All Tracking Sounds " + ChatColor.GREEN + "ENABLED"));
                        } else {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "All Tracking Sounds " + ChatColor.RED + "DISABLED"));
                        }
                        break;
                }
                break;
            case "test-crate":
                CrateMethods.createCrate(p.getWorld(),"end");
                break;
            case "populate-crate":
                CrateTypeEnum crateType = CrateMethods.getCrate(args[0]);
                CrateMethods.populateCrate(crateType,null,p);
                break;
            case "translate":
                p.sendMessage(C.t(args[0]));
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command cmd, String label, String[] args) {
        Player p = (Player) commandSender;
        ArrayList<String> argsList = new ArrayList<>();

        switch (label) {
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
