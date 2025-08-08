package kiul.kiulsmputilitiesv3.towns;

import kiul.kiulsmputilitiesv3.C;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.awt.*;
import java.awt.Color;
import java.time.Duration;
import java.util.*;
import java.util.List;

public class Town {

    public static List<Town> townsList = new ArrayList<>();

    public static Town getTownForPlayer(Player p) {
        for (Town town : townsList) {
            if (town.getOwningTeam().getName().equals(C.getPlayerTeam(p).getName())) {
                return town;
            }
        }
        return null;
    }

    private final Location townCenter;
    private String townUUID;
    private Component townName;
    private Color townColour;
    private Team owningTeam;
    private final ArmorStand townCore;
    private BukkitTask areaCheck;

    // variables related to the protected area of the town
    private final int townProtectedRadius; // how many blocks should the protection extend from the center in every direction (square)
    private final Vector minimum;
    private final Vector maximum;
    private HashMap<Player, Integer> playersInsideTown;

    private int townMaxHealth;
    private int townHealth;
    private Set<String> collectedSkulls;

    private long disabledUntil;
    private long invulnerableUntil;
    private boolean isRegenerating;
    
    public Town(Location placedLocation, Player p) { // from placement
        this.townCenter = placedLocation;
        this.townProtectedRadius = 80;
        this.owningTeam = C.getPlayerTeam(p);
        this.townUUID = owningTeam.getName();
        this.townColour = new Color(owningTeam.color().red(),owningTeam.color().green(),owningTeam.color().blue());
        this.townName = Component.empty().append(owningTeam.prefix()).append(Component.text(" New Town"));
        this.collectedSkulls = new HashSet<>();
        this.townMaxHealth = 8000;
        this.townHealth = townMaxHealth;
        this.disabledUntil = 0;
        this.invulnerableUntil = 0;
        this.isRegenerating = false;


        ArmorStand stand = (ArmorStand) townCenter.getWorld().spawnEntity(townCenter.clone().add(0, 1.75, 0), EntityType.ARMOR_STAND);
        stand.setPersistent(true);
        stand.setMetadata("core", new FixedMetadataValue(C.plugin, true));
        stand.setCustomNameVisible(true);
        stand.customName(townName);
        stand.setInvisible(true);
        stand.setGravity(false);
        this.townCore = stand;

        Location firstLocation = new Location(townCenter.getWorld(), townCenter.getBlockX() + townProtectedRadius, 320, townCenter.getBlockZ() + townProtectedRadius);
        Location secondLocation = new Location(townCenter.getWorld(), townCenter.getBlockX() - townProtectedRadius, -64, townCenter.getBlockZ() - townProtectedRadius);
        Vector firstPoint = firstLocation.toVector();
        Vector secondPoint = secondLocation.toVector();

        this.minimum = Vector.getMinimum(firstPoint, secondPoint);
        this.maximum = Vector.getMaximum(firstPoint, secondPoint);
        this.playersInsideTown = new HashMap<>();
        areaCheck = initializeAreaCheck();
    }

    public Town(Location townCenter,String townUUID, Component townName,Set<String> collectedSkulls,int townHealth, int townMaxHealth, long disabledUntil,long invulnerableUntil,boolean isRegenerating) { // from config
        this.townCenter = townCenter;
        townProtectedRadius = 80;
        this.townUUID = townUUID;
        this.owningTeam = C.getPlayerTeamOffline(Bukkit.getOfflinePlayer(townUUID));
        this.townName = townName;
        this.townMaxHealth = townMaxHealth;
        this.townHealth = townHealth;
        this.collectedSkulls = collectedSkulls;
        this.disabledUntil = disabledUntil;
        this.invulnerableUntil = invulnerableUntil;
        this.townColour = new Color(owningTeam.color().red(),owningTeam.color().green(),owningTeam.color().blue());
        this.isRegenerating = isRegenerating;



        ArmorStand stand = (ArmorStand) townCenter.getWorld().spawnEntity(townCenter.clone().add(0, 1.75, 0), EntityType.ARMOR_STAND);
        stand.setPersistent(true);
        stand.setMetadata("core", new FixedMetadataValue(C.plugin, true));
        stand.setCustomNameVisible(true);
        stand.customName(townName);
        stand.setInvisible(true);
        stand.setGravity(false);
        this.townCore = stand;

        Location firstLocation = new Location(townCenter.getWorld(), townCenter.getBlockX() + townProtectedRadius, 320, townCenter.getBlockZ() + townProtectedRadius);
        Location secondLocation = new Location(townCenter.getWorld(), townCenter.getBlockX() - townProtectedRadius, -64, townCenter.getBlockZ() - townProtectedRadius);
        Vector firstPoint = firstLocation.toVector();
        Vector secondPoint = secondLocation.toVector();

        this.minimum = Vector.getMinimum(firstPoint, secondPoint);
        this.maximum = Vector.getMaximum(firstPoint, secondPoint);
        this.playersInsideTown = new HashMap<>();
        townsList.add(this);
        areaCheck = initializeAreaCheck();
    }

    public boolean protectedAreaContains(Location location) {
        return location.toVector().isInAABB(minimum, maximum);

    }

    public void destroy() {

        List<String> lore = new ArrayList<>();
        ItemStack townCore = new ItemStack(Material.RESPAWN_ANCHOR);
        ItemMeta townCoreMeta = townCore.getItemMeta();
        lore.add(ChatColor.GRAY+"Can be placed to create a safe zone for your team.");
        townCoreMeta.setLore(lore);
        townCoreMeta.setDisplayName(C.t("&eTown Core"));
        townCoreMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING,"towncore");
        townCore.setItemMeta(townCoreMeta);

        FileConfiguration config = C.plugin.getConfig();
        townCenter.getBlock().setType(Material.AIR);
        townCenter.getWorld().dropItemNaturally(townCenter,townCore);
        config.set("towns." + townUUID,null);
        areaCheck.cancel();
        townsList.remove(this);
        C.plugin.saveConfig();
    }

    public BukkitTask initializeAreaCheck() {

        return new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!playersInsideTown.containsKey(p) && protectedAreaContains(p.getLocation())) {
                        playersInsideTown.put(p, 0);
                        Component shrineOwners = owningTeam != null ? Component.empty().append(Component.text("Owned by ")).append(owningTeam.prefix()) : Component.text("Unowned");
                        Title shrineWelcomeTitle = Title.title(townName, shrineOwners, Title.Times.times(Duration.ofMillis(1000), Duration.ofMillis(2000), Duration.ofMillis(500)));
                        p.showTitle(shrineWelcomeTitle);
                        if (owningTeam != null && !owningTeam.equals(C.getPlayerTeam(p))) {
                            p.sendMessage(C.fMsg("Because you are not a member of this town, remaining within its protected area for greater than 30 seconds will notify the owning team of your presence. Leave the area now to avoid being revealed!"));
                        }
                    }
                }
                for (Player p : playersInsideTown.keySet()) {
                    if (protectedAreaContains(p.getLocation())) {
                        playersInsideTown.put(p, playersInsideTown.get(p) + 5);
                        if (playersInsideTown.get(p) == 30) {
                            // send owners a notification
                            if (owningTeam != null && !owningTeam.equals(C.getPlayerTeam(p))) {
                                for (String entries : owningTeam.getEntries()) {
                                    if (Bukkit.getPlayer(entries) != null) {
                                        Player owningTeamMember = Bukkit.getPlayer(entries);
                                        String unauthorisedPlayerTeamPrefix = C.getPlayerTeam(p) != null ? C.getPlayerTeamPrefix(p) : "";
                                        owningTeamMember.sendMessage(C.msg(unauthorisedPlayerTeamPrefix + p.getName() + ChatColor.WHITE + " has entered the protected radius of your town " + C.componentToString(townName) + "."));
                                    }
                                }
                            }
                        }
                    } else {
                        // send owners a notification
                        if (owningTeam != null && !owningTeam.equals(C.getPlayerTeam(p))) {
                            for (String entries : owningTeam.getEntries()) {
                                if (Bukkit.getPlayer(entries) != null) {
                                    Player owningTeamMember = Bukkit.getPlayer(entries);
                                    String unauthorisedPlayerTeamPrefix = C.getPlayerTeam(p) != null ? C.getPlayerTeamPrefix(p) : "";
                                    owningTeamMember.sendMessage(C.msg(unauthorisedPlayerTeamPrefix + p.getName() + ChatColor.WHITE + " has left the protected radius of your town " + C.componentToString(townName) + "."));
                                }
                            }
                        }
                        playersInsideTown.remove(p);
                    }
                }
            }
        }.runTaskTimer(C.plugin, 0, 100);
    }

    public static void saveToConfig(Town town) {
        String uuid = town.getTownUUID();
        FileConfiguration config = C.plugin.getConfig();

        //save location (not loc object cuz that can break)
        config.set("towns." + uuid + ".loc.x", town.getTownCenter().getX());
        config.set("towns." + uuid + ".loc.y", town.getTownCenter().getY());
        config.set("towns." + uuid + ".loc.z", town.getTownCenter().getZ());

        config.set("towns." + uuid + ".name", MiniMessage.miniMessage().serialize(town.getTownName()));

        // save timestamps so that upgrade/protection time are not cheesed by rebooting.
        config.set("towns." + uuid + ".maxhealth", town.getTownMaxHealth());
        config.set("towns." + uuid + ".health", town.getTownHealth());
        config.set("towns." + uuid + ".collected_skulls", town.getCollectedSkulls());
        config.set("towns." + uuid + ".disableduntil", town.getDisabledUntil());
        config.set("towns." + uuid + ".invulnerableuntil", town.getInvulnerableUntil());
        config.set("towns." + uuid + ".regenerating", town.isRegenerating());

        C.plugin.saveConfig();
    }

    public static Town loadFromConfig(String townUUID) {

        FileConfiguration config = C.plugin.getConfig();

        if (C.plugin.getConfig().getConfigurationSection("towns." + townUUID) == null)
            return null;

        Location loc = new Location(Bukkit.getWorld("world"),
                config.getDouble("towns." + townUUID + ".loc.x"),
                config.getDouble("towns." + townUUID + ".loc.y"),
                config.getDouble("towns." + townUUID + ".loc.z"));

        Component townName = MiniMessage.miniMessage().deserialize(config.getString("towns." + townUUID + ".name"));
        int maxHealth = config.getInt("towns." + townUUID + ".maxhealth");
        int health = config.getInt("towns." + townUUID + ".health");
        Set<String> collectedSkulls = config.getConfigurationSection("towns."+townUUID+".collectedskulls").getKeys(false);
        long invulnerableUntil = config.getLong("towns." + townUUID + ".invulnerableuntil");
        long disabledUntil = config.getLong("towns." + townUUID + ".disableduntil");
        boolean regenerating = config.getBoolean("towns."+townUUID+".regenerating");
        return new Town(loc,townUUID,townName,collectedSkulls,health,maxHealth,disabledUntil,invulnerableUntil,regenerating);
    }

    public Component getTownName() {
        return townName;
    }

    public ArmorStand getTownCore() {
        return townCore;
    }

    public String getTownUUID() {
        return townUUID;
    }

    public Color getTownColour() {
        return townColour;
    }

    public int getTownHealth() {
        return townHealth;
    }

    public long getDisabledUntil() {
        return disabledUntil;
    }

    public long getInvulnerableUntil() {
        return invulnerableUntil;
    }

    public int getTownMaxHealth() {
        return townMaxHealth;
    }

    public boolean isRegenerating() {
        return isRegenerating;
    }

    public HashMap<Player, Integer> getPlayersInsideTown() {
        return playersInsideTown;
    }

    public Set<String> getCollectedSkulls() {
        return collectedSkulls;
    }

    public int getTownProtectedRadius() {
        return townProtectedRadius;
    }

    public Location getTownCenter() {
        return townCenter;
    }

    public Team getOwningTeam() {
        return owningTeam;
    }
}
