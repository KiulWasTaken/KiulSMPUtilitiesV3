package kiul.kiulsmputilitiesv3.accessories;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.net.URL;
import java.util.*;


public enum IngredientItemEnum {
    /**
     * Symbols
     */


    Peridot(C.t("&7[&#6ae839&l◆&7] Ingredient (Peridot)"),C.getURL("http://textures.minecraft.net/texture/9e4af2c9ff6a9103a5a82d100b056ab0cb7c2c84be46356b3f57355c13f65b7e"), new String[]{ChatColor.GRAY+"crafting ingredient used to",ChatColor.GRAY+"upgrade accessories for"+ChatColor.GREEN+" self-preservation"},"peridot",C.t("&7[&#6ae839&l◆&7] ")),
    Ruby(C.t("&7[&#ce0b03&l▲&7] Ingredient (Ruby)"),C.getURL("http://textures.minecraft.net/texture/1be0a247e6db70b39acf1e851e9ba266d16be404993641cdcc7804a9bbd63c0"), new String[]{ChatColor.GRAY+"crafting ingredient used to",ChatColor.GRAY+"upgrade accessories for"+ChatColor.RED+" increased power"},"ruby",C.t("&7[&#ce0b03&l▲&7] ")),
    Tanzanite(C.t("&7[&#1e5ce2&l⏹&7] Ingredient (Tanzanite)"),C.getURL("http://textures.minecraft.net/texture/59f817e35f25804d4785ce4b27e83c3b27bbf51cebe16797dc3c2f9e34dbbec3"), new String[]{ChatColor.GRAY+"crafting ingredient used to",ChatColor.GRAY+"upgrade accessories for"+ChatColor.BLUE+" support"},"tanzanite",C.t("&7[&#1e5ce2&l⏹&7] ")),
    Opal(C.t("&7[&#3c20b6&l⏺&7] Ingredient (Opal)"),C.getURL("http://textures.minecraft.net/texture/6a01bcbd1869edaad6dd04ec383656b88e5a676280d7fa2d4f631c2f34cf8350"), new String[]{ChatColor.GRAY+"crafting ingredient used to",ChatColor.GRAY+"upgrade accessories for"+ChatColor.DARK_PURPLE+" magic"},"opal",C.t("&7[&#3c20b6&l⏺&7] "));

    private String displayName;
    private URL URL;
    private String[] lore;
    private String localName;
    private String prefix;


    IngredientItemEnum(String displayName, URL url, String[] lore, String localName,String prefix) {
        this.displayName = displayName;
        this.URL = url;
        this.lore = lore;
        this.localName = localName;
        this.prefix = prefix;
    }

    public String getDisplayName() {
        return displayName;
    }
    public Material getMaterial() {
        return Material.PLAYER_HEAD;
    }
    public String[] getLore() {return lore;}
    public String getLocalName() {
        return localName;
    }

    public java.net.URL getURL() {
        return URL;
    }
    public ItemStack getIngredient() {
        ItemStack itemStack = C.createItemStack(getDisplayName(),getMaterial(),1,getLore(),null,null,getLocalName(),getURL());
        return itemStack;}

    public String getPrefix() {
        return prefix;
    }
}
