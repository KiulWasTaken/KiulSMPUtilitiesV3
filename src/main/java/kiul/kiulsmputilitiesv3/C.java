package kiul.kiulsmputilitiesv3;

import com.google.j2objc.annotations.Property;
import kiul.kiulsmputilitiesv3.combattag.FightManager;
import kiul.kiulsmputilitiesv3.potions.BrewingRecipe;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class C {

    /* static utilities */
    public static Plugin plugin = KiulSMPUtilitiesV3.getPlugin(KiulSMPUtilitiesV3.class);
    public static String chatColour = ChatColor.GRAY + "" + ChatColor.ITALIC;
    public static String eventPrefix = ChatColor.GOLD+""+ChatColor.BOLD+"CRATE" + ChatColor.RESET+ChatColor.GRAY+" » ";
    public static boolean restarting = false;
    public static DecimalFormat twoPointDecimal = new DecimalFormat("#.##");
    public static int claimCoreRange = 32;
    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yy");
    public static FightManager fightManager = new FightManager();

    /* static lists */

    public static ArrayList<BrewingRecipe> brewingRecipes = new ArrayList<>();

    public static HashMap<BrewerInventory, BukkitTask> brewingTasks = new HashMap<>();
    public static ArrayList<Player> loggingOut = new ArrayList<>();
    public static ArrayList<Player> logoutTimer = new ArrayList<>();

    /* Configurable */
    public static int blockRegenTimeSeconds = 90;
    public static int npcDespawnTimeSeconds = 90;
    public static int accessoryCooldownTimeMinutes = 0;

    /* Global Utility Methods */
    private static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])");
    public static String t(String textToTranslate) {
        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuffer buffer = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }
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
            iM.setLocalizedName(localizedName);
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
            iM.setLocalizedName(localizedName);
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
        for (Team team : sb.getTeams()) {
            if (team.hasEntry(p.getDisplayName())) {
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
