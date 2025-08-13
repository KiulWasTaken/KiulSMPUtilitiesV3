package kiul.kiulsmputilitiesv3;

import kiul.kiulsmputilitiesv3.combattag.FightManager;
import kiul.kiulsmputilitiesv3.server_events.SuddenDeath;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.awt.*;
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class C {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])");
    public static String t(String textToTranslate) {
        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuffer buffer = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static String GOLD = C.t("&#ebbc3d");
    public static String LIGHT_RED = C.t("&#f5633b");
    public static String RED = C.t("&#e33630");
    public static String DARK_RED = C.t("&#a11813");
    public static String GREEN = C.t("&#27a33a");
    public static String YELLOW = C.t("&#b59a4e");
    public static String ICE_BLUE = C.t("&#a8caff");
    public static String LIGHT_ICE_BLUE = C.t("&#c7ddff");
    //5493f6
    public static String BLUE = C.t("&#658bb5"); //old &#5f95ed
    public static String GRAY = C.t("&#787878");
    public static String PURPLE = C.t("&#6d2b94");
    public static String FUCHSIA = C.t("&#c72850");
    public static String LIGHT_PURPLE = C.t("&#9139c4");
    public static String PINK = C.t("&#c73e8b");
    public static String LIGHT_GREEN = C.t("&#31e862");
    public static String DARK_GREEN = C.t("&#218a3c");
    public static String GRAY_BLUE = C.t("&#89adaf");
    public static String GRAY_PINK = C.t("&#af89a4");
    public static String GRAY_PURPLE = C.t("&#a989af");
    /*#c73e8b pink
#6d2b94 purple*/

    /* static utilities */
    public static String fMsg(String msg) { return t("&7[&4\uD83C\uDFDB&7] " + RED + msg); }
    public static String msg(String msg) { return t("&7[" + GREEN + "\uD83C\uDFDB&7] " + GOLD + msg); }
    public static String failPrefix = C.t(DARK_RED+"❌ "+RED);
    public static String warnPrefix = C.t(YELLOW+"⚠ "+ GOLD);
    public static String successPrefix = C.t(DARK_GREEN+"✔ "+GREEN);
    public static BukkitTask smpScheduler;
    public static Plugin plugin = KiulSMPUtilitiesV3.getPlugin(KiulSMPUtilitiesV3.class);
    public static String chatColour = ChatColor.GRAY + "" + ChatColor.ITALIC;
    public static String eventPrefix = ChatColor.GOLD+""+ChatColor.BOLD+"EVENT" + ChatColor.RESET+ChatColor.GRAY+" » ";
    public static String pluginPrefix = C.t("&7[&#27a33a\uD83D\uDD31&7] "+ChatColor.RESET+ChatColor.GRAY+"");
    public static boolean restarting = false;
    public static DecimalFormat twoPointDecimal = new DecimalFormat("#.##");
    public static int claimCoreRange = 32;
    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy");
    public static SuddenDeath suddenDeath = null;
    public static FightManager fightManager = new FightManager();

    /* static lists */


    public static HashMap<BrewerInventory, BukkitTask> brewingTasks = new HashMap<>();
    public static ArrayList<Player> loggingOut = new ArrayList<>();
    public static ArrayList<Player> logoutTimer = new ArrayList<>();

    /* Configurable */
    public static int BLOCK_REGEN_SECONDS = 5;
    public static int NPC_DESPAWN_SECONDS = 45;
    public static int ACCESSORY_COOLDOWN_MINUTES = 0;
    public static int CONNECTION_ISSUE_PROTECTION_SECONDS = 15;

    /* Global Utility Methods */

    public static int[] splitTimestamp(long futureTimestamp) {
        long millisecondsRemaining = futureTimestamp - System.currentTimeMillis();
        long hours = millisecondsRemaining / 3600000;
        long minutes = (millisecondsRemaining % 3600000) / 60000;
        long seconds = ((millisecondsRemaining % 3600000) % 60000) / 1000;
        return new int[]{(int)hours, (int)minutes, (int)seconds};
    }
    public static int[] splitTimestampManual(long startTimestamp, long endTimestamp) {
        long millisecondsRemaining = endTimestamp - startTimestamp;
        long hours = millisecondsRemaining / 3600000;
        long minutes = (millisecondsRemaining % 3600000) / 60000;
        long seconds = ((millisecondsRemaining % 3600000) % 60000) / 1000;
        return new int[]{(int)hours, (int)minutes, (int)seconds};
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    public static  HashMap<Team,List<Player>> sortTeams (List<UUID> playerList) {
        HashMap<Team,List<Player>> teams = new HashMap<>();
        for (UUID uuids : playerList) {
            if (Bukkit.getPlayer(uuids) != null) {
                Player p = Bukkit.getPlayer(uuids);
                if (teams.get(C.getPlayerTeam(p)) != null) {
                    teams.get(C.getPlayerTeam(p)).add(p);
                } else {
                    teams.put(C.getPlayerTeam(p),new ArrayList<>());
                    teams.get(C.getPlayerTeam(p)).add(p);
                }
            }
        }
        return teams;
    }
    public static net.md_5.bungee.api.ChatColor returnT(String textToTranslate) {
        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuffer buffer = new StringBuffer();
        if (matcher.find()) {
            while (matcher.find()) {
                return net.md_5.bungee.api.ChatColor.of("#" + matcher.group(1));
            }
        }
        return net.md_5.bungee.api.ChatColor.WHITE;}

    public static String componentToString(Component input1) {

        String input = MiniMessage.miniMessage().serialize(input1);

        // Regular expression to match the hex color code in the format <#xxxxxx>
        String regex = "<#([0-9A-Fa-f]{6})>";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        // If a match is found, extract the color and the remaining text
        if (matcher.find()) {
            // Extract the hex color
            String hexColor = matcher.group(1);  // This will capture the hex part without the <# and >

            // Extract everything after the '>'
            String textAfterColor = input.substring(matcher.end());  // Get substring after the hex part

            // For this case, we return the color and the text after it
            return C.t("&#" + hexColor + textAfterColor.trim());
        }

        // Return a message if no hex color was found
        return "No valid hex color found!";
    }

    public static ItemStack getHeadFromURL(URL value) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        profile.getTextures().setSkin(value);
        meta.setOwnerProfile(profile);
        head.setItemMeta(meta);

        return head;
    }

    public static ItemStack createHead (String itemName, Material material, int amount, String[] lore, String localizedName, URL URL,String displayName) {
        ItemStack i = new ItemStack(material);
        if (material == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) i.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(displayName)));
            i.setItemMeta(meta);
            if (URL != null) {
                i = C.getHeadFromURL(URL);
            }
        }
        ItemMeta iM = i.getItemMeta();
        List<String> adjustedLore = new ArrayList<>();
        for (String oldLore : lore) {
            adjustedLore.add(C.t(oldLore));
        }
        iM.setLore(adjustedLore);
        if (localizedName != null) {
            iM.getPersistentDataContainer().set(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING,localizedName);
        }

        i.setAmount(amount);
        iM.setDisplayName(C.t(itemName));

        i.setItemMeta(iM);
        return i;
    }

    public static ItemStack createItemStack (String itemName, Material material, int amount, String[] lore, Enchantment enchantment, Integer enchantLvl, String localizedName,URL URL) {
        ItemStack i = new ItemStack(material);
        if (material == Material.PLAYER_HEAD) {
            if (URL != null) {
                i = C.getHeadFromURL(URL);
            }
        }
        ItemMeta iM = i.getItemMeta();
        List<String> adjustedLore = new ArrayList<>();
        for (String oldLore : lore) {
            adjustedLore.add(C.t(oldLore));
        }
        iM.setLore(adjustedLore);
        if (localizedName != null) {
            iM.getPersistentDataContainer().set(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING,localizedName);
        }

        i.setAmount(amount);
        iM.setDisplayName(C.t(itemName));
        if (enchantment != null) {
            iM.addEnchant(enchantment, enchantLvl, true);
        }

        i.setItemMeta(iM);
        return i;
    }

    public static String getPlayerTeamPrefix (Player p ) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "";
        for (Team team : sb.getTeams()) {
            if (team.hasEntry(p.getDisplayName())) {
                teamName = team.getPrefix();
                return teamName;
            }
        }
        return teamName;
    }

    public static URL getURL (String URL) {
        URL url = null;
        try {
            url = new URL(URL);
        } catch (MalformedURLException err) {err.printStackTrace();}
        return url;
    }
    public static Team getPlayerTeam (Player p ) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        if (p == null) {
            return null;
        }
        for (Team team : sb.getTeams()) {
            if (team.hasEntry(p.getDisplayName())) {
                return team;
            }
        }
        return null;
    }
    public static Team getPlayerTeamOffline (OfflinePlayer p ) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team team : sb.getTeams()) {
            if (team.hasEntry(p.getName())) {
                return team;
            }
        }
        return null;
    }

    public static double safeDivide(double dividend, double divisor) {
        if(Double.compare(divisor, Double.NaN) == 0) return Double.NaN;
        if(Double.compare(dividend, Double.NaN) == 0) return Double.NaN;
        if(Double.compare(divisor, 0.0) == 0) {
            if(Double.compare(dividend, 0.0) == -1) {
                dividend = 1.0;
            }
            divisor = 1.0;
        }
        if(Double.compare(divisor, -0.0) == 0) {
            if(Double.compare(dividend, -0.0) == 1) {
                dividend = 1.0;
            }
            divisor = 1.0;
        }
        return dividend / divisor;
    }

    public static net.md_5.bungee.api.ChatColor getAverageTeamColour(Player p) {
        Team team = getPlayerTeam(p);
        String teamPrefix = team.getPrefix();
        String firstLetter = ChatColor.getLastColors(teamPrefix.substring(1,7));
        String lastLetter = ChatColor.getLastColors(teamPrefix.substring(teamPrefix.length()-7,teamPrefix.length()-1));
        net.md_5.bungee.api.ChatColor color1 = returnT(firstLetter);
        net.md_5.bungee.api.ChatColor color2 = returnT(lastLetter);


        int R1 = color1.getColor().getRed();
        int G1 = color1.getColor().getGreen();
        int B1 = color1.getColor().getBlue();

        int R2 = color2.getColor().getRed();
        int G2 = color2.getColor().getGreen();
        int B2 = color2.getColor().getBlue();

        int R3 = (int)Math.sqrt((R1^2+R2^2)/2);
        int G3 = (int)Math.sqrt((G1^2+G2^2)/2);
        int B3 = (int)Math.sqrt((B1^2+B2^2)/2);

        net.md_5.bungee.api.ChatColor averagedColour = net.md_5.bungee.api.ChatColor.of(new Color(R3,G3,B3));
        return averagedColour;
    }


    public static boolean playerIsOnTeam(String teamName, Player p) {
        if (getPlayerTeam(p) != null) {
            if (getPlayerTeam(p).getName().equalsIgnoreCase(teamName)) {
                return true;
            }
        }
    return false;}

    public static net.md_5.bungee.api.ChatColor getChatColor(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ChatColor.COLOR_CHAR && i + 1 < input.length()) {
                char code = input.charAt(i + 1);
                ChatColor color = ChatColor.getByChar(code);
                if (color != null) {
                    return color.asBungee();
                }
            }
        }
        return null;
    }

    public List<Location> getHollowCube(Location corner1, Location corner2, double particleDistance) {
        List<Location> result = new ArrayList<Location>();
        World world = corner1.getWorld();
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        for (double x = minX; x <= maxX; x+=particleDistance) {
            for (double y = minY; y <= maxY; y+=particleDistance) {
                for (double z = minZ; z <= maxZ; z+=particleDistance) {
                    int components = 0;
                    if (x == minX || x == maxX) components++;
                    if (y == minY || y == maxY) components++;
                    if (z == minZ || z == maxZ) components++;
                    if (components >= 2) {
                        result.add(new Location(world, x, y, z));
                    }
                }
            }
        }

        return result;
    }

}
