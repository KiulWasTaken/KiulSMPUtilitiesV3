package kiul.kiulsmputilitiesv3.towns;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.locatorbar.LocatorBar;
import kiul.kiulsmputilitiesv3.locatorbar.Waypoint;
import kiul.kiulsmputilitiesv3.towns.augments.AugmentEnum;
import kiul.kiulsmputilitiesv3.towns.augments.AugmentEvent;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

import static kiul.kiulsmputilitiesv3.C.getHighestBlockY;

public class Town {

    public static Set<Town> townsList = new HashSet<>();
    public static HashMap<Player,Long> townPlaceCooldown = new HashMap<>();
    public static float DEFAULT_TOWN_MAX_HEALTH = 12000f;

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
    private String townNameString;
    private Color townColour;
    private Team owningTeam;
    private final TextDisplay townTextDisplay;
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
    private BossBar augmentingBossbar;
    private long timeSinceLastDamage;
    private HashMap<Location,Material> reinstateBlocks; // list of locations and original material for reinstating towns that have been sieged.

    // side mission
    private int townCharge;
    private AugmentEnum selectedAugment;
    private boolean isAugmenting;
    private AugmentEvent augmentEvent;
    private long bountyUntil;
    private UUID bountySkull;
    private ArrayList collectedSkulls;
    private Waypoint waypoint;
    
    public Town(Location placedLocation, Player p) {// from placement
        this.townCenter = placedLocation;

        if (placedLocation.getBlock().getType() != Material.RESPAWN_ANCHOR) {
            placedLocation.getWorld().setBlockData(placedLocation,Material.RESPAWN_ANCHOR.createBlockData());
        }
        this.selectedAugment = null;
        this.townProtectedRadius = 80;
        this.owningTeam = C.getPlayerTeam(p);
        this.townUUID = owningTeam.getName();
        this.townColour = new Color(owningTeam.color().red(),owningTeam.color().green(),owningTeam.color().blue());
        this.townName = Component.text("New Town");
        this.townNameString = "New Town";
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

        this.townTextDisplay = (TextDisplay) townCenter.getWorld().spawnEntity(townCenter.clone().add(0.5, 1.4, 0.5), EntityType.TEXT_DISPLAY);
        townTextDisplay.setText(owningTeam.getPrefix() + townNameString + "\n" + (townCharge < 12 ? C.t(C.GOLD + "⚡ " + C.YELLOW + townCharge + ChatColor.GRAY + "/" + C.GOLD + "12") : C.t(C.GOLD + "⚡ " + C.YELLOW + townCharge + ChatColor.GRAY + "/" + C.GOLD + "12" + ChatColor.DARK_GRAY + " (Right-Click)")) + "\n" +
                getTownStatus());
        townTextDisplay.setVisibleByDefault(true);
        townTextDisplay.setSeeThrough(true);
        townTextDisplay.setShadowed(true);
        townTextDisplay.setBillboard(Display.Billboard.VERTICAL);

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
        this.defendingBossbar = null; // ❤♥♦⬩
        TreeMap<Integer,String> textures = new TreeMap<Integer,String>() {{
            put(0,"❤");
            put(100,"♥");
            put(1000,"♦");
            put(5000,"⬩");
        }};
        this.waypoint = new Waypoint(townUUID,townCenter.clone().add(0.5,0,0.5),textures,owningTeam,false);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (LocatorBar.playerLocatorBar.get(onlinePlayer.getUniqueId()) != null) {
                LocatorBar.playerLocatorBar.get(onlinePlayer.getUniqueId()).addWaypoint(waypoint);
            }
        }
    }

    public Town(Location townCenter, String townUUID, Component townName,String townNameString, ArrayList collectedSkulls, float townHealth, float townMaxHealth, long disabledUntil, long invulnerableUntil, boolean isRegenerating, UUID bountySkull, long bountyUntil, boolean isActive, HashMap<Location,Material> reinstateBlocks, Team attackingTeam) { // from config
        this.townCenter = townCenter;
        townCenter.getBlock().setType(Material.RESPAWN_ANCHOR);
        this.selectedAugment = null;
        townProtectedRadius = 80;
        this.townUUID = townUUID;
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        C.plugin.getLogger().warning(townUUID);
        this.owningTeam = null;
        for (Team team : sb.getTeams()) {
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
        this.attackingTeam = attackingTeam;
        this.isActive = isActive;
        this.timeSinceLastDamage = System.currentTimeMillis();
        this.reinstateBlocks = reinstateBlocks;
        this.townNameString = townNameString;

        for (Entity textEntity : townCenter.getNearbyEntities(3,3,3)) {
            if (textEntity instanceof TextDisplay) {
                textEntity.remove();
            }
        }

        this.townTextDisplay = (TextDisplay) townCenter.getWorld().spawnEntity(townCenter.clone().add(0.5, 1.4, 0.5), EntityType.TEXT_DISPLAY);
        townTextDisplay.setText(owningTeam.getPrefix() + townNameString + "\n" + (townCharge < 12 ? C.t(C.GOLD + "⚡ " + C.YELLOW + townCharge + ChatColor.GRAY + "/" + C.GOLD + "12") : C.t(C.GOLD + "⚡ " + C.YELLOW + townCharge + ChatColor.GRAY + "/" + C.GOLD + "12" + ChatColor.DARK_GRAY + " (Right-Click)")) + "\n" +
                getTownStatus());
        townTextDisplay.setVisibleByDefault(true);
        townTextDisplay.setSeeThrough(true);
        townTextDisplay.setShadowed(true);
        townTextDisplay.setBillboard(Display.Billboard.VERTICAL);

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
        TreeMap<Integer,String> textures = new TreeMap<Integer,String>() {{
            put(0,"❤");
            put(100,"♥");
            put(1000,"♦");
            put(5000,"⬩");
        }};
        this.waypoint = new Waypoint(townUUID,townCenter.clone().add(0.5,0,0.5),textures,owningTeam,false);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            LocatorBar.playerLocatorBar.get(onlinePlayer.getUniqueId()).addWaypoint(waypoint);
        }
    }

    public boolean protectedAreaContains(Location location) {
        return location.toVector().isInAABB(minimum, maximum) && location.getWorld() == townCenter.getWorld();

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
        if (isAugmenting) {
            float augmentPercentage = (float) (augmentEvent.getEventHealth()/augmentEvent.getEventMaxHealth())*100;
            return C.YELLOW+"✪ "+C.GOLD+"Augmenting "+ChatColor.DARK_GRAY+"("+String.format("%.1f",augmentPercentage) +"%)";
        }

        return C.PURPLE+"\uD83D\uDEE1 "+C.LIGHT_PURPLE+"Shielded "+ChatColor.DARK_GRAY+"("+String.format("%.1f",percentage) +"%)";
    }

    public void updateTownTextDisplay() {
        townTextDisplay.setText(owningTeam.getPrefix() + townNameString + "\n" + (townCharge < 12 ? C.t(C.GOLD + "⚡ " + C.YELLOW + townCharge + ChatColor.GRAY + "/" + C.GOLD + "12") : C.t(C.GOLD + "⚡ " + C.YELLOW + townCharge + ChatColor.GRAY + "/" + C.GOLD + "12" + ChatColor.DARK_GRAY + " (Right-Click)")) + "\n" +
                getTownStatus());
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
            if (C.getPlayerTeam(p) == null) continue;
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
        updateTownTextDisplay();
    }

    static List<Material> DO_NOT_REINSTATE = new ArrayList<>() {{
       add(Material.NETHERITE_BLOCK);
       add(Material.DIAMOND_BLOCK);
       add(Material.IRON_BLOCK);
       add(Material.GOLD_BLOCK);
       add(Material.LAPIS_BLOCK);
       add(Material.EMERALD_BLOCK);
       add(Material.COAL_BLOCK);

       add(Material.ANCIENT_DEBRIS);
       add(Material.DIAMOND_ORE);
       add(Material.DEEPSLATE_DIAMOND_ORE);
       add(Material.IRON_ORE);
       add(Material.DEEPSLATE_IRON_ORE);
       add(Material.GOLD_ORE);
       add(Material.DEEPSLATE_GOLD_ORE);
       add(Material.NETHER_GOLD_ORE);
       add(Material.LAPIS_ORE);
       add(Material.DEEPSLATE_LAPIS_ORE);
       add(Material.EMERALD_ORE);
       add(Material.DEEPSLATE_EMERALD_ORE);
       add(Material.COAL_ORE);
       add(Material.DEEPSLATE_COAL_ORE);

    }};

    public void breakBlockInDisabledTownArea(Location blockLocation, Material initialBlockType) {
        if (reinstateBlocks.containsKey(blockLocation)) return;
        if (DO_NOT_REINSTATE.contains(initialBlockType)) return;
        reinstateBlocks.put(blockLocation,initialBlockType);
    }

    public void damageTownShield(float damage, boolean isExplosion,Player attacker,Location damageLocation,double originDistance) {
        if (isDisabled()) return;
        if (isInvulnerable()) return;
        float multiplier = (float) numMembersInsideTown() > 0 ? (float) 1 /numMembersInsideTown() : 1;
        if (isExplosion) multiplier = 0.05f;
        damage = damage*multiplier;
        double coreDistanceMultiplier = -Math.log(((1.39)*this.townCenter.distance(damageLocation))+1)+5;
        // -ln(ax+b)+c
        // where x = distance.
        // and a = the sharpness of the curve. at a = 1.39 the multiplier at town edge is 0.27 and at 40 blocks its 0.97
        // c = multiplier at 0 distance to core
        // b = dont touch, it controls the asymptote
        damage = (float) (damage*coreDistanceMultiplier);
        double originDistanceDamageMultiplier = 1-(0.01*originDistance);
        damage = (float) (damage*Math.max(originDistanceDamageMultiplier,0));
        if (((townHealth-damage)/townMaxHealth)*100 <= 95) {
            displayHealthBar();
            if (attackingTeam == null && attacker != null) {
                attackingTeam = C.getPlayerTeam(attacker);
                if (C.getPlayerTeam(attacker) == null) {
                    attackingTeam = Bukkit.getScoreboardManager().getNewScoreboard().registerNewTeam(attacker.getUniqueId()+ "");
                    attackingTeam.setPrefix(attacker.getName()+" ");
                }
                announceSiegeStage(0,true);
            }
        }

        TextColor color;
        if (isAugmenting && this.townCenter.distance(damageLocation) < 20) {
            color = TextColor.color(181, 154, 78);
        } else {
            color = TextColor.color(109, 43, 148);
        }

        if (isAugmenting) {
            if (this.townCenter.distance(damageLocation) < 20) {
                augmentEvent.damageEvent(damage);
                damageMarker(attacker,damage,damageLocation,color);
                damage = 0;
            } else {
                damage = 0;
                damageMarker(attacker,damage,damageLocation,color);
            }
        } else {
            damageMarker(attacker,damage,damageLocation,color);
        }
        if (!isRegenerating && ((townHealth-damage)/townMaxHealth)*100 <= 10) {
            isRegenerating = true;
            invulnerableUntil = System.currentTimeMillis()+(1000 * 60 * 60 * 6); // invulnerable for 6 hours
            townHealth = townMaxHealth/10;
            announceSiegeStage(1,true);
            return;
        }
        if (isRegenerating && townHealth-damage <= 0) {
            isActive = false;
            disabledUntil = System.currentTimeMillis()+1000*60;
            townHealth = 0;
            updateTownTextDisplay();
            // announce attackers WON
            if (attacker != null) {
                attackingTeam = C.getPlayerTeam(attacker);
                if (C.getPlayerTeam(attacker) == null) {
                    attackingTeam = Bukkit.getScoreboardManager().getNewScoreboard().registerNewTeam(attacker.getUniqueId()+ "");
                    attackingTeam.setPrefix(attacker.getName()+" ");
                }
                announceSiegeStage(3,true);
            }

            return;
        }


        timeSinceLastDamage = System.currentTimeMillis();
        townHealth -= damage;
        updateTownTextDisplay();

    }

    public void announceSiegeStage (int stage, boolean attackersProceed) {
        ZonedDateTime calendar = ZonedDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "d MMMM h:mma",
                Locale.ENGLISH
        );


        String siegePrefix = C.LIGHT_PURPLE + ChatColor.BOLD+ "SIEGE! " + C.PURPLE;
        switch (stage) {
            case 0:
                Bukkit.broadcastMessage(siegePrefix + attackingTeam.getPrefix() + ChatColor.GRAY + "are trying to initiate a siege on " + owningTeam.getPrefix() + townNameString);
                break;
            case 1:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (notInvoledBossbar != null)
                        notInvoledBossbar.removeViewer(player);
                    if (attackersBossbar != null)
                        attackersBossbar.removeViewer(player);
                    if (defendingBossbar != null)
                        defendingBossbar.removeViewer(player);
                }
                if (attackersProceed) {
                     calendar = Instant
                            .ofEpochMilli(invulnerableUntil) // or disabledUntil
                            .atZone(ZoneId.systemDefault());
                    Bukkit.broadcastMessage(siegePrefix + attackingTeam.getPrefix() + ChatColor.GRAY + "have successfully weakened " + owningTeam.getPrefix() + townNameString + ChatColor.GRAY  + "'s core. It is invulnerable until defense phase is activated at any time by defenders before " + ChatColor.DARK_GRAY + formatter.format(calendar) + " " + ZoneId.systemDefault().getDisplayName(TextStyle.SHORT,Locale.ENGLISH));
                } else {

                    Bukkit.broadcastMessage(siegePrefix + attackingTeam.getPrefix() + ChatColor.GRAY + "have been repelled from " + owningTeam.getPrefix() + townNameString + ChatColor.GRAY  + " and failed to initiate a siege.");
                }
                break;
            case 2:
                calendar = ZonedDateTime.now().plusHours(3);

                Bukkit.broadcastMessage(siegePrefix + owningTeam.getPrefix() + ChatColor.GRAY + "have started their defense of " + ChatColor.WHITE + townNameString + ChatColor.GRAY  + "! Without " + attackingTeam.getPrefix() + ChatColor.GRAY  + "intervention, the town will be fully healed at " + ChatColor.DARK_GRAY + formatter.format(calendar) + " " + ZoneId.systemDefault().getDisplayName(TextStyle.SHORT,Locale.ENGLISH));
                break;
            case 3:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (notInvoledBossbar != null)
                        notInvoledBossbar.removeViewer(player);
                    if (attackersBossbar != null)
                        attackersBossbar.removeViewer(player);
                    if (defendingBossbar != null)
                        defendingBossbar.removeViewer(player);
                }
                if (attackersProceed) {
                    calendar = Instant
                            .ofEpochMilli(disabledUntil) // or disabledUntil
                            .atZone(ZoneId.systemDefault());
                    Bukkit.broadcastMessage(siegePrefix + attackingTeam.getPrefix() + ChatColor.GRAY + "have successfully sieged " + owningTeam.getPrefix() + townNameString + ChatColor.GRAY + ". All block protection has been disabled within its claim until " + ChatColor.DARK_GRAY + formatter.format(calendar) + " " + ZoneId.systemDefault().getDisplayName(TextStyle.SHORT,Locale.ENGLISH));
                } else {
                    Bukkit.broadcastMessage(siegePrefix + attackingTeam.getPrefix() + ChatColor.GRAY + "have been repelled by the defenders of " + owningTeam.getPrefix() + townNameString + ChatColor.GRAY + " and failed the siege.");
                }
                break;
        }

        // [0] ATTACKERS are trying to initiate a siege on TOWN!

        // [1] ATTACKERS have been repelled from TOWN and failed to initiate a siege.
        // OR
        // [1] ATTACKERS have successfully weakened TOWN's core. TOWN is invulnerable until defense phase is activated at any time by defenders before XXX ACDT

        // [2] DEFENDERS have started their defense of TOWN! Without ATTACKER intervention TOWN will be fully healed at XXX ACDT

        // [3] ATTACKERS have been repelled by the defenders of TOWN.
        // OR
        // [3] ATTACKERS have successfully sieged TOWN. All block protection has been disabled within its claim until XXX ACDT.
    }

    public void damageMarker(Player attacker, float damage, Location damageLocation, TextColor color) {
        // === DAMAGE MARKER ===
        if (attacker != null) {
            float finalDamage = damage;
            ArmorStand stand = damageLocation.getWorld().spawn(damageLocation.add(0.5,0.2,0.5), ArmorStand.class, as -> {
                as.setMarker(true);
                as.setInvisible(true);
                as.setGravity(true);
                as.setSmall(true);
                as.customName(Component.empty().append(Component.text(C.getNumTeammatesDivSymbol(numMembersInsideTown()) + " ").color(NamedTextColor.DARK_GRAY)).append(Component.text(String.format("-%.1f", finalDamage)).color(color)));
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
            updateTownTextDisplay();
            return;
        }

        townHealth += amount;
        updateTownTextDisplay();
    }

    String destroy = C.LAVENDER_PURPLE + ChatColor.BOLD+ "TOWN DISBANDED! " + ChatColor.GRAY;
    public void destroy() {
        townCenter.getBlock().setType(Material.AIR);
        boolean teamExists = false;
        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            if (team.getName().equalsIgnoreCase(townUUID)) {
                teamExists = true;
                for (String entry : team.getEntries()) {
                    Player p = Bukkit.getPlayer(entry);
                    if (p != null) {
                        Town.townPlaceCooldown.put(p, System.currentTimeMillis() + (1000 * 60 * 60 * 24));
                    }
                }
            }
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            LocatorBar.playerLocatorBar.get(onlinePlayer.getUniqueId()).removeWaypoint(waypoint);
        }

        if (teamExists) {
            Bukkit.broadcastMessage(destroy+this.getOwningTeam().getPrefix()+ChatColor.GRAY+"has disbanded the town core for "+ ChatColor.WHITE + this.getTownNameString() + ChatColor.GRAY + " at coordinates " + ChatColor.WHITE + townCenter.x() +", " + townCenter.y() + ", " + townCenter.z());
        } else {
            Bukkit.broadcastMessage(destroy+"town core for "+ ChatColor.WHITE + this.getTownNameString() + ChatColor.GRAY + " has been disbanded at coordinates " + ChatColor.WHITE + townCenter.x() + ", " + townCenter.y() + ", " + townCenter.z());
        }
        C.plugin.getLogger().info(townsList.toString());
        List<String> lore = new ArrayList<>();
        ItemStack townCore = new ItemStack(Material.RESPAWN_ANCHOR);
        ItemMeta townCoreMeta = townCore.getItemMeta();
        lore.add(ChatColor.GRAY+"Can be placed to create a safe zone for your team.");
        lore.add(ChatColor.GRAY+"You cannot move your town core for 24h if you pick it up,");
        lore.add(ChatColor.GRAY+"make sure you place it in the right spot");
        townCoreMeta.setLore(lore);
        townCoreMeta.setDisplayName(C.t("&eTown Core"));
        townCoreMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING,"towncore");
        townCore.setItemMeta(townCoreMeta);
        getTownTextDisplay().remove();
        C.plugin.getLogger().info("-------------------------");
        FileConfiguration config = C.plugin.getConfig();
        townCenter.getBlock().setType(Material.AIR);
        townCenter.getWorld().dropItemNaturally(townCenter,townCore);
        config.set("towns." + townUUID,null);
        areaCheck.cancel();
        townsList.remove(this);
        C.plugin.saveConfig();
        C.plugin.getLogger().info(townsList.toString());
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(this.getTownCenter().getWorld()))) {
            int radius = this.getTownProtectedRadius();

            Location cornerA = this.getTownCenter().clone().add(-radius, 0, -radius);

            for (int x = 0; x <= radius * 2; x++) {
                Location loc1 = cornerA.clone().add(x, 0, 0);
                Location loc2 = loc1.clone().add(0, 0, radius * 2);
                loc1.setY(getHighestBlockY(loc1));
                loc2.setY(getHighestBlockY(loc2));
                editSession.setBlock(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
                editSession.setBlock(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
                editSession.setBlock(loc1.getBlockX(), loc1.getBlockY() + 1, loc1.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
                editSession.setBlock(loc2.getBlockX(), loc2.getBlockY() + 1, loc2.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
            }
            for (int z = 0; z <= radius * 2; z++) {
                Location loc1 = cornerA.clone().add(0, 0, z);
                Location loc2 = loc1.clone().add(radius * 2, 0, 0);
                loc1.setY(getHighestBlockY(loc1));
                loc2.setY(getHighestBlockY(loc2));
                editSession.setBlock(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
                editSession.setBlock(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
                editSession.setBlock(loc1.getBlockX(), loc1.getBlockY() + 1, loc1.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
                editSession.setBlock(loc2.getBlockX(), loc2.getBlockY() + 1, loc2.getBlockZ(), new BaseBlock(BlockTypes.AIR.getDefaultState()));
            }
        }


    }


    public BukkitTask initializeAreaCheck() {
        Town town = this;
        return new BukkitRunnable() {
            @Override
            public void run() {
                regenerateTownHealth();

                if (attackingTeam != null && System.currentTimeMillis()-timeSinceLastDamage >= 1000*60*5) {

                    town.setTownHealth(town.getTownMaxHealth());
                    announceSiegeStage(1,false);
                    attackingTeam = null;
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
                    announceSiegeStage(3,false);
                }

                updateTownTextDisplay();
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
                                        owningTeamMember.sendMessage(C.msg(unauthorisedPlayerTeamPrefix + p.getName() + ChatColor.WHITE + " has entered the protected radius of your town "));
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
                                    owningTeamMember.sendMessage(C.msg(unauthorisedPlayerTeamPrefix + p.getName() + ChatColor.WHITE + " has left the protected radius of your town "));
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
        if (isAugmenting) {
            float progress = (float) (augmentEvent.getEventHealth()/augmentEvent.getEventMaxHealth());

            Component name = Component.empty().append(
                            owningTeam.prefix())
                    .append(Component.text("is Augmenting "))
                    .append(augmentEvent.getInputItem().name());
            BossBar global = BossBar.bossBar(name,
                    progress, BossBar.Color.YELLOW, BossBar.Overlay.NOTCHED_12);
            if (augmentingBossbar == null) {
                augmentingBossbar = global;
            } else {
                augmentingBossbar.name(name);
                augmentingBossbar.progress(progress);
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                augmentingBossbar.addViewer(p);
            }
        } else {
            if (augmentingBossbar != null) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    augmentingBossbar.removeViewer(p);
                }
                augmentingBossbar = null;
            }
        }

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
                                progress, BossBar.Color.RED, BossBar.Overlay.NOTCHED_12);
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
            } else {
                this.destroy();
            }
        }
    }

    public static void saveToConfig(Town town) {
        String uuid = town.getTownUUID();
        FileConfiguration config = C.plugin.getConfig();

        // Save location
        Location loc = town.getTownCenter();
        config.set("towns."+ uuid + ".loc.world",loc.getWorld().getName());
        config.set("towns." + uuid + ".loc.x", loc.getX());
        config.set("towns." + uuid + ".loc.y", loc.getY());
        config.set("towns." + uuid + ".loc.z", loc.getZ());

        // Save name
        config.set("towns." + uuid + ".name", MiniMessage.miniMessage().serialize(town.getTownName()));
        config.set("towns." + uuid + ".namestring", town.getTownNameString());

        // Save basic stats
        config.set("towns." + uuid + ".maxhealth", town.getTownMaxHealth());
        config.set("towns." + uuid + ".health", town.getTownHealth());
        config.set("towns." + uuid + ".disableduntil", town.getDisabledUntil());
        config.set("towns." + uuid + ".invulnerableuntil", town.getInvulnerableUntil());
        config.set("towns." + uuid + ".regenerating", town.isRegenerating());
        config.set("towns." + uuid + ".active", town.isActive());
        if (town.attackingTeam != null) {
            config.set("towns." + uuid + ".attackingteam", town.attackingTeam.getName());
        } else {
            config.set("towns." + uuid + ".attackingteam", null);
        }
        // Save bounty
        if (town.getBountySkull() != null) {
            config.set("towns." + uuid + ".bountyuuid", town.getBountySkull().toString());
            config.set("towns." + uuid + ".bountyuntil", town.getBountyUntil());
        }
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
// Remove holograms
        if (town.getTownTextDisplay() != null) town.getTownTextDisplay().remove();
        C.plugin.saveConfig();


    }

    public static Town loadFromConfig(String townUUID) {

        FileConfiguration config = C.plugin.getConfig();
        ConfigurationSection townSection = config.getConfigurationSection("towns." + townUUID);
        if (townSection == null) return null;

        // Load location
        Location loc = new Location(Bukkit.getWorld(townSection.getString("loc.world")),
                townSection.getDouble("loc.x"),
                townSection.getDouble("loc.y"),
                townSection.getDouble("loc.z"));
        loc.getChunk().load();
        // Load name
        Component townName = MiniMessage.miniMessage().deserialize(townSection.getString("name"));
        String townNameString = townSection.getString("namestring");

        // Load basic stats
        int maxHealth = townSection.getInt("maxhealth");
        int health = townSection.getInt("health");
        long invulnerableUntil = townSection.getLong("invulnerableuntil");
        long disabledUntil = townSection.getLong("disableduntil");
        boolean regenerating = townSection.getBoolean("regenerating");
        boolean isActive = townSection.getBoolean("active");

        Team attackingTeam = null;
        if ((String)townSection.get("attackingteam") != null) {
             attackingTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam((String) townSection.get("attackingteam"));
        }
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

        return new Town(loc, townUUID, townName,townNameString, new ArrayList<>(collectedSkulls), health, maxHealth,
                disabledUntil, invulnerableUntil, regenerating, bountyUUID, bountyUntil, isActive, reinstateBlocks,attackingTeam);
    }

    public Component getTownName() {
        return townName;
    }

    public void setTownHealth(float townHealth) {
        this.townHealth = townHealth;
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

    public void setAugmentEvent(AugmentEvent augmentEvent) {
        this.augmentEvent = augmentEvent;
    }

    public AugmentEvent getAugmentEvent() {
        return augmentEvent;
    }

    public boolean isAugmenting() {
        return getAugmentEvent() != null;
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

    public void setAugmenting(boolean augmenting) {
        isAugmenting = augmenting;
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

    public AugmentEnum getSelectedAugment() {
        return selectedAugment;
    }

    public void setSelectedAugment(AugmentEnum selectedAugment) {
        this.selectedAugment = selectedAugment;
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    public String getTownNameString() {
        return townNameString;
    }

    public void setTownNameString(String townNameString) {
        this.townNameString = townNameString;
    }

    public TextDisplay getTownTextDisplay() {
        return townTextDisplay;
    }
}
