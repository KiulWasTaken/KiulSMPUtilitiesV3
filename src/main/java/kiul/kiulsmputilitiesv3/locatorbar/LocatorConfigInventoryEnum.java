package kiul.kiulsmputilitiesv3.locatorbar;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.net.URL;

public enum LocatorConfigInventoryEnum {

    ENABLE_BAR(C.GOLD+"Enable Actionbar Locator Bar", Material.ENCHANTING_TABLE, new String[]{
            ChatColor.GRAY + "Optionally completely disable the bar. This may have gameplay downsides as you cannot",
            ChatColor.GRAY + "see town cores or enemies beyond visual range whilst in combat."}, 1,"enabled",null),
    SHOW_TOWNS(C.GOLD+"Show Towns", Material.RESPAWN_ANCHOR, new String[]{
        ChatColor.GRAY + "Changes whether town cores show up on the locator bar at any distance.",
        ChatColor.GRAY + "make sure you have the towns waypointed if you do this!"}, 2,"show_towns",null),
    SHARE_SELF(C.GOLD+"Share Own Location", Material.ENDER_PEARL, new String[]{
        ChatColor.GRAY + "Change whether teammates can see you as a dot on their locator bar. When disabled, ",
            ChatColor.GRAY + "it is disabled at all times including whilst in combat."}, 4,"share_self",null),
    SHOW_TEAMMATES(C.GOLD+"Show Teammates", Material.NAME_TAG, new String[]{
        ChatColor.GRAY + "Change whether you can see your teammates on the bar. Helpful if you want to",
            ChatColor.GRAY + "effectively disable the bar outside of combat and reduce screen clutter."}, 5,"show_teammates",null);

    private String displayName;
    private Material material;
    private String[] lore;
    private Integer inventorySlot;
    private String localName;
    private URL skullValue;

    LocatorConfigInventoryEnum(String displayName, Material material, String[] lore, Integer inventorySlot, String localName, URL skullValue) {
        this.displayName = displayName;
        this.material = material;
        this.lore = lore;
        this.inventorySlot = inventorySlot;
        this.localName = localName;
        this.skullValue = skullValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public String[] getLore() {
        return lore;
    }

    public Integer getInventorySlot() {
        return inventorySlot - 1;
    }

    public String getlocalName() {
        return localName;
    }
    public URL getSkullValue() {
        return skullValue;
    }
}
