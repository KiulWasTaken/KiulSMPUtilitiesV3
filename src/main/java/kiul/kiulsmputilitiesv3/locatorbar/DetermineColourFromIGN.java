package kiul.kiulsmputilitiesv3.locatorbar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.HashMap;

public class DetermineColourFromIGN {

    public static Color getRGBFromChatColor (ChatColor chatColor) {
        Bukkit.broadcastMessage("baseteamcolour: " + chatColor.name());
        switch (chatColor) {
            case BLACK:
                return new Color(0,0,0);
            case DARK_BLUE:
                return new Color(0,0,170);
            case DARK_GREEN:
                return new Color(0,170,0);
            case DARK_AQUA:
                return new Color(0,170,170);
            case DARK_RED:
                return new Color(170,0,0);
            case DARK_PURPLE:
                return new Color(170,0,170);
            case GOLD:
                return new Color(255,170,0);
            case GRAY:
                return new Color(170,170,170);
            case DARK_GRAY:
                return new Color(85,85,85);
            case BLUE:
                return new Color(85, 85, 255);
            case GREEN:
                return new Color(85, 255, 85);
            case AQUA:
                return new Color(85, 255, 255);
            case RED:
                return new Color(255, 85, 85);
            case LIGHT_PURPLE:
                return new Color(255, 85, 255);
            case YELLOW:
                return new Color(255, 255, 85);
            case WHITE:
                return new Color(255, 255, 255);
            default:
                return null;
        }

    }

    public static net.md_5.bungee.api.ChatColor getDisplayNameRGBValue (ChatColor teamColor, String displayName) {
        Color teamColour = getRGBFromChatColor(teamColor);
        Bukkit.broadcastMessage("teamcolour: " + teamColour.getRed() + " " + teamColour.getBlue()+ " " +teamColour.getGreen());
        int[] finalRGB = new int[]{teamColour.getRed(),teamColour.getBlue(),teamColour.getGreen()};

        for (int i = 0; i < displayName.length(); i++) {
            int[] letterRGB = letterRGBValue.get(displayName.toLowerCase().charAt(i));
            finalRGB[0] += letterRGB[0]*3;
            finalRGB[1] += letterRGB[1]*3;
            finalRGB[2] += letterRGB[2]*3;
        }
        for (int i = 0; i < finalRGB.length; i++) {
            if (finalRGB[i] > 255) {
                finalRGB[i] = 255;
            }
            if (finalRGB[i] < 0) {
                finalRGB[i] = 0;
            }
        }
        Bukkit.broadcastMessage(finalRGB[0] + " " + finalRGB[1] + " " + finalRGB[2]);
        return net.md_5.bungee.api.ChatColor.of(new Color(finalRGB[0],finalRGB[1],finalRGB[2]));
    }


    public static HashMap<Character,int[]> letterRGBValue = new HashMap<>() {{
        put('a', new int[]{1, 0, 0});
        put('b', new int[]{0, 1, 0});
        put('c', new int[]{0, 0, 1});
        put('d', new int[]{1, 1, 0});
        put('e', new int[]{0, 1, 1});
        put('f', new int[]{2, 0, 0});
        put('g', new int[]{2, 1, 0});
        put('h', new int[]{0, 2, 0});
        put('i', new int[]{0, 2, 1});
        put('j', new int[]{1, 2, 1});
        put('k', new int[]{2, 2, 1});
        put('l', new int[]{1, 2, 2});
        put('m', new int[]{-1, 0, 0});
        put('n', new int[]{0, -1, 0});
        put('o', new int[]{0, 0, -1});
        put('p', new int[]{-1, -1, 0});
        put('q', new int[]{0, -1, -1});
        put('r', new int[]{-2, 0, 0});
        put('s', new int[]{-2, -1, 0});
        put('t', new int[]{0, -2, 0});
        put('u', new int[]{0, -2, -1});
        put('v', new int[]{-1, -2, -1});
        put('w', new int[]{-2, -2, -1});
        put('x', new int[]{-1, -2, -1});
        put('y', new int[]{1, -1, 0});
        put('z', new int[]{-1, 1, 0});
        put('0', new int[]{0, 1, -1});
        put('1', new int[]{0, -1, 1});
        put('2', new int[]{0, 0, 0});
        put('3', new int[]{-2, 1, 0});
        put('4', new int[]{2, -1, 0});
        put('5', new int[]{0, -2, 1});
        put('6', new int[]{0, 2, -1});
        put('7', new int[]{-1, 2, -1});
        put('8', new int[]{1, -2, 1});
        put('9', new int[]{1, -2, -1});
        put('_', new int[]{-1, 2, 1});

    }};
}
