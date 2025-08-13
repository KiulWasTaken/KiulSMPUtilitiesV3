package kiul.kiulsmputilitiesv3.towns;

import kiul.kiulsmputilitiesv3.C;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.awt.Color;
import java.time.Duration;
import java.util.*;
import java.util.List;

public class Town {

    public static List<Town> townsList = new ArrayList<>();
    public static float DEFAULT_TOWN_MAX_HEALTH = 200f;

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
    private final ArmorStand townNameStand;
    private final ArmorStand townStatusStand;
    private final ArmorStand townChargeStand;
    private BukkitTask areaCheck;

    // variables related to the protected area of the town
    private final int townProtectedRadius; // how many blocks should the protection extend from the center in every direction (square)
    private final Vector minimum;
    private final Vector maximum;
    private HashMap<Player, Integer> playersInsideTown;


    // variables related to siege of the town
    private float defaultTownMaxHealth;
    private float townMaxHealth;
    private float townHealth;

    private long disabledUntil;
    boolean isActive;
    private long invulnerableUntil;
    private boolean isRegenerating;
    private Team attackingTeam;
    private BossBar notInvoledBossbar;
    private BossBar attackersBossbar;
    private BossBar defendingBossbar;
    private long timeSinceLastDamage;
    private HashMap<Location,Material> reinstateBlocks; // list of locations and original material for reinstating towns that have been sieged.

    // side mission
    private int townCharge;
    private long bountyUntil;
    private UUID bountySkull;
    private ArrayList collectedSkulls;
    
    public Town(Location placedLocation, Player p) { // from placement
        this.townCenter = placedLocation;
        this.townProtectedRadius = 80;
        this.owningTeam = C.getPlayerTeam(p);
        this.townUUID = owningTeam.getName();
        this.townColour = new Color(owningTeam.color().red(),owningTeam.color().green(),owningTeam.color().blue());
        this.townName = Component.text("New Town");
        this.collectedSkulls = new ArrayList<>();
        this.defaultTownMaxHealth = DEFAULT_TOWN_MAX_HEALTH;
        this.townMaxHealth = defaultTownMaxHealth;
        this.townHealth = townMaxHealth;
        this.disabledUntil = 0;
        this.invulnerableUntil = 0;
        this.isRegenerating = false;
        this.bountyUntil = System.currentTimeMillis()+(1000*60*60*24);
        this.townCharge = 0;
        this.attackingTeam = null;
        this.isActive = true;
        this.timeSinceLastDamage = System.currentTimeMillis();
        this.reinstateBlocks = new HashMap<>();

        OfflinePlayer[] allOfflinePlayers = Bukkit.getOfflinePlayers();
        List<OfflinePlayer> activeOfflinePlayers = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : allOfflinePlayers) {
            if (owningTeam.hasEntry(offlinePlayer.getName())) continue;
            if (System.currentTimeMillis()-offlinePlayer.getLastLogin() <= 1000*60*60*24) { // player login is less than 24 hours ago
                activeOfflinePlayers.add(offlinePlayer);
            }
        }
      if (!activeOfflinePlayers.isEmpty()) {
          this.bountySkull = activeOfflinePlayers.get(new Random().nextInt(0, activeOfflinePlayers.size())).getUniqueId();
      } else {
          this.bountySkull = null;
      }
        ArmorStand stand = (ArmorStand) townCenter.getWorld().spawnEntity(townCenter.clone().add(0.5, 1.4, 0.5), EntityType.ARMOR_STAND);
        owningTeam.addEntity(stand);
        stand.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE).setBaseValue(10000);
        stand.setPersistent(true);
        stand.setMetadata("core", new FixedMetadataValue(C.plugin, true));
        stand.setCustomNameVisible(true);
        stand.customName(owningTeam.prefix().append(townName));
        stand.setInvisible(true);
        stand.setGravity(false);
        stand.setMarker(true);
        this.townNameStand = stand;

        ArmorStand chargeStand = (ArmorStand) townCenter.getWorld().spawnEntity(townCenter.clone().add(0.5, 0.8, 0.5), EntityType.ARMOR_STAND);
        chargeStand.setPersistent(true);
        chargeStand.setMetadata("core", new FixedMetadataValue(C.plugin, true));
        chargeStand.setCustomNameVisible(true);
        chargeStand.setCustomName(C.t(C.GOLD+"⚡ "+C.YELLOW+ townCharge +ChatColor.GRAY+"/"+C.GOLD+"12"));
        chargeStand.setInvisible(true);
        chargeStand.setGravity(false);
        chargeStand.setMarker(true);
        this.townChargeStand = chargeStand;

        ArmorStand statusStand = (ArmorStand) townCenter.getWorld().spawnEntity(townCenter.clone().add(0.5, 1.1, 0.5), EntityType.ARMOR_STAND);
        statusStand.setPersistent(true);
        statusStand.setCustomNameVisible(true);
        statusStand.setCustomName(getTownStatus());
        statusStand.setInvisible(true);
        statusStand.setGravity(false);
        statusStand.setMarker(true);
        this.townStatusStand = statusStand;

        Location firstLocation = new Location(townCenter.getWorld(), townCenter.getBlockX() + townProtectedRadius, 320, townCenter.getBlockZ() + townProtectedRadius);
        Location secondLocation = new Location(townCenter.getWorld(), townCenter.getBlockX() - townProtectedRadius, -64, townCenter.getBlockZ() - townProtectedRadius);
        Vector firstPoint = firstLocation.toVector();
        Vector secondPoint = secondLocation.toVector();

        this.minimum = Vector.getMinimum(firstPoint, secondPoint);
        this.maximum = Vector.getMaximum(firstPoint, secondPoint);
        this.playersInsideTown = new HashMap<>();
        areaCheck = initializeAreaCheck();

        this.notInvoledBossbar = null;
        this.attackersBossbar = null;
        this.defendingBossbar = null;
    }

    public Town(Location townCenter, String townUUID, Component townName, ArrayList collectedSkulls, float townHealth, float townMaxHealth, long disabledUntil, long invulnerableUntil, boolean isRegenerating, UUID bountySkull, long bountyUntil, boolean isActive, HashMap<Location,Material> reinstateBlocks) { // from config
        this.townCenter = townCenter;
        townProtectedRadius = 80;
        this.townUUID = townUUID;
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        C.plugin.getLogger().warning(townUUID);

        for (Team team : sb.getTeams()) {
            C.plugin.getLogger().warning(team.getName());
            if (team.getName().equalsIgnoreCase(townUUID)) {
                this.owningTeam = team;
                break;
            }
        }
        this.townName = townName;
        this.defaultTownMaxHealth = DEFAULT_TOWN_MAX_HEALTH;
        this.townMaxHealth = townMaxHealth;
        this.townHealth = townHealth;
        this.collectedSkulls = collectedSkulls;
        this.disabledUntil = disabledUntil;
        this.invulnerableUntil = invulnerableUntil;
        this.townColour = new Color(owningTeam.color().red(),owningTeam.color().green(),owningTeam.color().blue());
        this.isRegenerating = isRegenerating;
        this.bountySkull = bountySkull;
        this.bountyUntil = bountyUntil;
        this.attackingTeam = null;
        this.isActive = isActive;
        this.timeSinceLastDamage = System.currentTimeMillis();
        this.reinstateBlocks = reinstateBlocks;

        ArmorStand stand = (ArmorStand) townCenter.getWorld().spawnEntity(townCenter.clone().add(0.5, 1.4, 0.5), EntityType.ARMOR_STAND);
        owningTeam.addEntity(stand);
        stand.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE).setBaseValue(10000);
        stand.setPersistent(true);
        stand.setMetadata("core", new FixedMetadataValue(C.plugin, true));
        stand.setCustomNameVisible(true);
        stand.customName(owningTeam.prefix().append(townName));
        stand.setInvisible(true);
        stand.setGravity(false);
        stand.setMarker(true);
        this.townNameStand = stand;

        ArmorStand chargeStand = (ArmorStand) townCenter.getWorld().spawnEntity(townCenter.clone().add(0.5, 0.8, 0.5), EntityType.ARMOR_STAND);
        chargeStand.setPersistent(true);
        chargeStand.setMetadata("core", new FixedMetadataValue(C.plugin, true));
        chargeStand.setCustomNameVisible(true);
        chargeStand.setCustomName(C.t(C.GOLD+"⚡ "+C.YELLOW+ townCharge +ChatColor.GRAY+"/"+C.GOLD+"12"));
        chargeStand.setInvisible(true);
        chargeStand.setGravity(false);
        chargeStand.setMarker(true);
        this.townChargeStand = chargeStand;

        ArmorStand statusStand = (ArmorStand) townCenter.getWorld().spawnEntity(townCenter.clone().add(0.5, 1.1, 0.5), EntityType.ARMOR_STAND);
        statusStand.setPersistent(true);
        statusStand.setCustomNameVisible(true);
        statusStand.setCustomName(getTownStatus());
        statusStand.setInvisible(true);
        statusStand.setGravity(false);
        statusStand.setMarker(true);
        this.townStatusStand = statusStand;

        Location firstLocation = new Location(townCenter.getWorld(), townCenter.getBlockX() + townProtectedRadius, 320, townCenter.getBlockZ() + townProtectedRadius);
        Location secondLocation = new Location(townCenter.getWorld(), townCenter.getBlockX() - townProtectedRadius, -64, townCenter.getBlockZ() - townProtectedRadius);
        Vector firstPoint = firstLocation.toVector();
        Vector secondPoint = secondLocation.toVector();

        this.minimum = Vector.getMinimum(firstPoint, secondPoint);
        this.maximum = Vector.getMaximum(firstPoint, secondPoint);
        this.playersInsideTown = new HashMap<>();
        townsList.add(this);
        areaCheck = initializeAreaCheck();

        this.notInvoledBossbar = null;
        this.attackersBossbar = null;
        this.defendingBossbar = null;
    }

    public boolean protectedAreaContains(Location location) {
        return location.toVector().isInAABB(minimum, maximum);

    }

    public void reinstate() {
        townHealth = townMaxHealth;
        isActive = true;
        for (Location blockLocation : reinstateBlocks.keySet()) {
            Block block = blockLocation.getBlock();
            block.setType(reinstateBlocks.get(blockLocation));
        }
    }

    public String getTownStatus() {
        if (disabledUntil > System.currentTimeMillis()) {
            int[] timestamps = C.splitTimestamp(disabledUntil);
            // disabled
            return C.LIGHT_RED+"❌ "+C.RED+"Disabled "+ChatColor.DARK_GRAY+"("+timestamps[0]+"h "+timestamps[1]+"m)";
        }
        if (!isActive) {
            return C.RED+"❌ "+C.LIGHT_RED+"Disabled "+ChatColor.DARK_GRAY+"(Right-Click)";
        }
        if (invulnerableUntil > System.currentTimeMillis()) {
            // invulnerable
            int[] timestamps = C.splitTimestamp(invulnerableUntil);
            return C.YELLOW+"\uD83D\uDEE1 "+C.GOLD+"Invulnerable "+ChatColor.DARK_GRAY+"("+timestamps[0]+"h "+timestamps[1]+"m)";
            // 🛡 Invulnerable
        }

        float percentage = (float) (townHealth/DEFAULT_TOWN_MAX_HEALTH)*100;
        if (isRegenerating) {
            // regenerating

            return C.ICE_BLUE+"❤ " +C.LIGHT_ICE_BLUE+"Regenerating "+ChatColor.DARK_GRAY+"("+String.format("%.1f",percentage) +"%)";
        }

        return C.PURPLE+"\uD83D\uDEE1 "+C.LIGHT_PURPLE+"Shielded "+ChatColor.DARK_GRAY+"("+String.format("%.1f",percentage) +"%)";
    }

    public void updateTownStatus() {
        townStatusStand.setCustomName(getTownStatus());
    }
    public void updateTownCharge() {
        if (townCharge < 12) {
            townChargeStand.setCustomName(C.t(C.GOLD + "⚡ " + C.YELLOW + townCharge + ChatColor.GRAY + "/" + C.GOLD + "12"));
        } else {
            townChargeStand.setCustomName(C.t(C.GOLD + "⚡ " + C.YELLOW + townCharge + ChatColor.GRAY + "/" + C.GOLD + "12" + ChatColor.DARK_GRAY + " (Right-Click)"));
        }
    }

    public int getTownCharge() {
        return townCharge;
    }

    public void setTownMaxHealth(float townMaxHealth) {
        this.townMaxHealth = townMaxHealth;
    }

    public int numMembersInsideTown() {
        int membersInsideTown = 0;
        for (Player p : playersInsideTown.keySet()) {
            if (C.getPlayerTeam(p).getName().equalsIgnoreCase(townUUID)) {
                membersInsideTown++;
            }
        }
        return membersInsideTown;
    }

    public void increaseTownCharge(int chargeAmount) {
        if (townCharge+chargeAmount > 12) {
            townCharge = 12;
            return;
        }
        townCharge+=chargeAmount;
        Block townAnchor = getTownCenter().getBlock();
        BlockState state = townAnchor.getState();
        if (state.getBlockData() instanceof RespawnAnchor anchorData) {
            anchorData.setCharges(townCharge/3); // Set between 0–4
            state.setBlockData(anchorData);
            state.update(true);
        }
        getTownCenter().getWorld().playSound(getTownCenter(),Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,1,1.1f-((float)0.1*chargeAmount));
        updateTownCharge();
    }
    public void breakBlockInDisabledTownArea(Location blockLocation, Material initialBlockType) {
        if (reinstateBlocks.containsKey(blockLocation)) return;
        reinstateBlocks.put(blockLocation,initialBlockType);
    }

    public void damageTownShield(float damage, boolean isExplosion,Player attacker,Location damageLocation) {
        if (isDisabled()) return;
        if (isInvulnerable()) return;
        float multiplier = (float) (1-(numMembersInsideTown()*0.1));
        if (isExplosion) multiplier = 0.2f;
        damage = damage*multiplier;

        if (!isRegenerating && ((townHealth-damage)/townMaxHealth)*100 <= 10) {
            isRegenerating = true;
            invulnerableUntil = System.currentTimeMillis()+(1000 * 60); // invulnerable for 6 hours
            townHealth = townMaxHealth/10;
            return;
        }
        if (isRegenerating && townHealth-damage <= 0) {
            isActive = false;
            disabledUntil = System.currentTimeMillis()+1000*60;
            townHealth = 0;
            updateTownStatus();
            // announce attackers WON
            if (attacker != null) {
                attackingTeam = C.getPlayerTeam(attacker);
                Bukkit.broadcast(attackingTeam.prefix().append(Component.text(" has successfully sieged ").append(owningTeam.prefix()).append(townName)));
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (notInvoledBossbar != null)
                    notInvoledBossbar.removeViewer(player);
                if (attackersBossbar != null)
                    attackersBossbar.removeViewer(player);
                if (defendingBossbar != null)
                    defendingBossbar.removeViewer(player);
            }
            return;
        }

        if (((townHealth-damage)/townMaxHealth)*100 <= 95) {
            if (attacker != null) {
                attackingTeam = C.getPlayerTeam(attacker);
                displayHealthBar();
            }
        }
        timeSinceLastDamage = System.currentTimeMillis();
        townHealth -= damage;
        updateTownStatus();

        // === DAMAGE MARKER ===
        if (attacker != null) {
            float finalDamage = damage;
            ArmorStand stand = damageLocation.getWorld().spawn(damageLocation.add(0.5,0.2,0.5), ArmorStand.class, as -> {
                as.setMarker(true);
                as.setInvisible(true);
                as.setGravity(true);
                as.setSmall(true);
                as.customName(Component.text(String.format("-%.1f", finalDamage)).color(TextColor.color(109, 43, 148)));
                as.setCustomNameVisible(true); // required so name actually exists
                as.setCollidable(false);
            });

            // Make it jump upward slightly
            stand.setVelocity(new Vector(0, 0.5, 0));

            // Only show to attacker (Paper API 1.19+)
            attacker.showEntity(C.plugin, stand);

            // Schedule removal after 0.8s (16 ticks)
            Bukkit.getScheduler().runTaskLater(C.plugin, () -> {
                stand.remove();
            }, 16L);
        }
    }


    public void regenerateTownHealth() {
        if (isDisabled()) return;
        if (isInvulnerable()) return;
        float amount = townMaxHealth/90000; // 5*60*60 = 5 hours in seconds * 5 = 90000 ticks. Town health regenerates in 5 hours from 0.

        if (townHealth+amount > townMaxHealth) {
            townHealth = townMaxHealth;
            updateTownStatus();
            return;
        }

        townHealth += amount;
        updateTownStatus();
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
        getTownNameStand().remove();
        getTownStatusStand().remove();
        getTownChargeStand().remove();

        FileConfiguration config = C.plugin.getConfig();
        townCenter.getBlock().setType(Material.AIR);
        townCenter.getWorld().dropItemNaturally(townCenter,townCore);
        config.set("towns." + townUUID,null);
        areaCheck.cancel();
        townsList.remove(this);
        C.plugin.saveConfig();
    }

    public ArmorStand getTownChargeStand() {
        return townChargeStand;
    }

    public BukkitTask initializeAreaCheck() {

        return new BukkitRunnable() {
            @Override
            public void run() {
                regenerateTownHealth();

                if (attackingTeam != null && System.currentTimeMillis()-timeSinceLastDamage >= 1000*60*5) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (notInvoledBossbar != null)
                            notInvoledBossbar.removeViewer(player);
                        if (attackersBossbar != null)
                            attackersBossbar.removeViewer(player);
                        if (defendingBossbar != null)
                            defendingBossbar.removeViewer(player);
                    }
                }

                if (isRegenerating && (float) (townHealth/DEFAULT_TOWN_MAX_HEALTH)*100 >= 95) {
                    isRegenerating = false;
                    attackingTeam = null;
                }
                updateTownStatus();
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

    public void displayHealthBar() {
        if (attackingTeam == null) return;

        if ((townHealth/townMaxHealth)*100 >= 95) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (notInvoledBossbar != null)
                    notInvoledBossbar.removeViewer(player);
                if (attackersBossbar != null)
                    attackersBossbar.removeViewer(player);
                if (defendingBossbar != null)
                    defendingBossbar.removeViewer(player);
            }
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()) { // loop all online players

            assert attackingTeam != null;
            float progress = ((float) townHealth / (float) townMaxHealth);
            if (progress > 1) {
                progress = 1;
            }
            if (progress < 0) {
                progress = 0;
            }
            if (owningTeam != null) {
                if (owningTeam.getEntries().contains(p.getName())) { // if they are the defending team...
                    // [Hopeless] is Capturing ε Epsilon
                    // {owning-team-name} is Capturing {shrine-name}
                    Component name = Component.empty()
                            .append(attackingTeam.prefix())
                            .append(Component.text("is Attacking ")).append(owningTeam.prefix())
                            .append(townName);
                    BossBar defending = BossBar.bossBar(name,
                            progress, BossBar.Color.PURPLE, BossBar.Overlay.NOTCHED_12);
                    if (defendingBossbar == null) {
                        defendingBossbar = defending;
                    } else {
                        defendingBossbar.name(name);
                        defendingBossbar.progress(progress);
                    }
                    defendingBossbar.addViewer(p);
                } else {
                    if (defendingBossbar != null)
                        defendingBossbar.removeViewer(p);

                    if (attackingTeam.getEntries().contains(p.getName())) { // if they are the attacking team...
                        // Capturing ε Epsilon from [Hopeless]
                        // Capturing {shrine-name} from {team-name}
                        Component name = Component.empty()
                                .append(Component.text("Attacking ")).append(owningTeam.prefix())
                                .append(townName);
                        BossBar attacking = BossBar.bossBar(name,
                                progress, BossBar.Color.YELLOW, BossBar.Overlay.NOTCHED_12);
                        if (attackersBossbar == null) {
                            attackersBossbar = attacking;
                        } else {
                            attackersBossbar.name(name);
                            attackersBossbar.progress(progress);
                        }
                        attackersBossbar.addViewer(p);
                    } else {
                        if (attackersBossbar != null)
                            attackersBossbar.removeViewer(p);


                        // if they are not involved in the conflict...

                        // [Hopeless] is Capturing ε Epsilon from [Meowzers]
                        // {owning-team-name} is Capturing {shrine-name} from {attacking-team-name}
                        Component name = Component.empty()
                                .append(attackingTeam.prefix())
                                .append(Component.text("is Attacking ")).append(owningTeam.prefix())
                                .append(townName);
                        BossBar notInvolved = BossBar.bossBar(name,
                                progress, BossBar.Color.WHITE, BossBar.Overlay.NOTCHED_12);
                        if (notInvoledBossbar == null) {
                            notInvoledBossbar = notInvolved;
                        } else {
                            notInvoledBossbar.name(name);
                            notInvoledBossbar.progress(progress);
                        }
                    }
                    if (notInvoledBossbar != null)
                        notInvoledBossbar.addViewer(p);
                }
            }
        }
    }

    public static void saveToConfig(Town town) {
        String uuid = town.getTownUUID();
        FileConfiguration config = C.plugin.getConfig();

        // Save location
        Location loc = town.getTownCenter();
        config.set("towns." + uuid + ".loc.x", loc.getX());
        config.set("towns." + uuid + ".loc.y", loc.getY());
        config.set("towns." + uuid + ".loc.z", loc.getZ());

        // Save name
        config.set("towns." + uuid + ".name", MiniMessage.miniMessage().serialize(town.getTownName()));

        // Save basic stats
        config.set("towns." + uuid + ".maxhealth", town.getTownMaxHealth());
        config.set("towns." + uuid + ".health", town.getTownHealth());
        config.set("towns." + uuid + ".disableduntil", town.getDisabledUntil());
        config.set("towns." + uuid + ".invulnerableuntil", town.getInvulnerableUntil());
        config.set("towns." + uuid + ".regenerating", town.isRegenerating());
        config.set("towns." + uuid + ".active", town.isActive());

        // Save bounty
        config.set("towns." + uuid + ".bountyuuid", town.getBountySkull().toString());
        config.set("towns." + uuid + ".bountyuntil", town.getBountyUntil());

        // Save collected skulls
        config.set("towns." + uuid + ".collectedskulls", town.getCollectedSkulls());

        // Save reinstate blocks
        HashMap<Location, Material> blocks = town.getReinstateBlocks();
        ConfigurationSection reinstateSection = config.createSection("towns." + uuid + ".reinstate_data");
        for (Map.Entry<Location, Material> entry : blocks.entrySet()) {
            Location blockLoc = entry.getKey();
            String key = blockLoc.getBlockX() + "," + blockLoc.getBlockY() + "," + blockLoc.getBlockZ();
            reinstateSection.set(key, entry.getValue().name());
        }

        C.plugin.saveConfig();

        // Remove holograms
        if (town.getTownNameStand() != null) town.getTownNameStand().remove();
        if (town.getTownStatusStand() != null) town.getTownStatusStand().remove();
        if (town.getTownChargeStand() != null) town.getTownChargeStand().remove();
    }

    public static Town loadFromConfig(String townUUID) {
        FileConfiguration config = C.plugin.getConfig();
        ConfigurationSection townSection = config.getConfigurationSection("towns." + townUUID);
        if (townSection == null) return null;

        // Load location
        Location loc = new Location(Bukkit.getWorld("world"),
                townSection.getDouble("loc.x"),
                townSection.getDouble("loc.y"),
                townSection.getDouble("loc.z"));

        // Load name
        Component townName = MiniMessage.miniMessage().deserialize(townSection.getString("name"));

        // Load basic stats
        int maxHealth = townSection.getInt("maxhealth");
        int health = townSection.getInt("health");
        long invulnerableUntil = townSection.getLong("invulnerableuntil");
        long disabledUntil = townSection.getLong("disableduntil");
        boolean regenerating = townSection.getBoolean("regenerating");
        boolean isActive = townSection.getBoolean("active");

        // Load bounty
        UUID bountyUUID = UUID.fromString(townSection.getString("bountyuuid", UUID.randomUUID().toString()));
        long bountyUntil = townSection.getLong("bountyuntil");

        // Load collected skulls
        List<String> collectedSkulls = townSection.getStringList("collectedskulls");

        // Load reinstate blocks
        HashMap<Location, Material> reinstateBlocks = new HashMap<>();
        ConfigurationSection reinstateSection = townSection.getConfigurationSection("reinstate_data");
        if (reinstateSection != null) {
            for (String key : reinstateSection.getKeys(false)) {
                String[] parts = key.split(",");
                if (parts.length != 3) continue;
                try {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int z = Integer.parseInt(parts[2]);
                    Location blockLoc = new Location(Bukkit.getWorld("world"), x, y, z);
                    Material mat = Material.valueOf(reinstateSection.getString(key, "AIR"));
                    reinstateBlocks.put(blockLoc, mat);
                } catch (IllegalArgumentException ignored) {}
            }
        }

        return new Town(loc, townUUID, townName, new ArrayList<>(collectedSkulls), health, maxHealth,
                disabledUntil, invulnerableUntil, regenerating, bountyUUID, bountyUntil, isActive, reinstateBlocks);
    }

    public Component getTownName() {
        return townName;
    }

    public void setTownHealth(float townHealth) {
        this.townHealth = townHealth;
    }

    public ArmorStand getTownNameStand() {
        return townNameStand;
    }

    public ArmorStand getTownStatusStand() {
        return townStatusStand;
    }

    public HashMap<Location, Material> getReinstateBlocks() {
        return reinstateBlocks;
    }

    public String getTownUUID() {
        return townUUID;
    }

    public UUID getBountySkull() {
        return bountySkull;
    }

    public void setInvulnerableUntil(long invulnerableUntil) {
        this.invulnerableUntil = invulnerableUntil;
    }

    public boolean isActive() {
        return isActive;
    }

    public long getBountyUntil() {
        return bountyUntil;
    }

    public Color getTownColour() {
        return townColour;
    }

    public float getTownHealth() {
        return townHealth;
    }

    public long getDisabledUntil() {
        return disabledUntil;
    }

    public boolean isInvulnerable() {return invulnerableUntil > System.currentTimeMillis();}
    public boolean isDisabled() {return !isActive;}


    public long getInvulnerableUntil() {
        return invulnerableUntil;
    }

    public float getTownMaxHealth() {
        return townMaxHealth;
    }

    public boolean isRegenerating() {
        return isRegenerating;
    }

    public HashMap<Player, Integer> getPlayersInsideTown() {
        return playersInsideTown;
    }

    public ArrayList getCollectedSkulls() {
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
