package kiul.kiulsmputilitiesv3.featuretoggle;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum FeatureEnum {
    /**
     * Kit
     */
    Crates("&#89adafRandom Crates", Material.CHEST, new String[]{ChatColor.GRAY+"Random crate events that announce their location",ChatColor.GRAY+"and provide pvp incentive"}, 12,"crates",true),
    CombatTag("&#89adafCombat Tag", Material.NAME_TAG, new String[]{ChatColor.GRAY+"Automatic fight detection that applies",ChatColor.GRAY+"cooldowns and nerfs to various pvp items"}, 13,"combattag",true),
    CombatLog("&#89adafCombat Log", Material.VILLAGER_SPAWN_EGG, new String[]{ChatColor.GRAY+"\"Safelog\" system that requires players to run",ChatColor.GRAY+"A command before logging out or they will stay",ChatColor.GRAY+"in the world for a short time. "}, 14,"combatlog",true),
    ItemHistory("&#89adafItem History", Material.CLOCK, new String[]{ChatColor.GRAY+"Adaptive item descriptions based on",ChatColor.GRAY+"enchantments, crafting, ownership changes",ChatColor.GRAY+"and more."}, 15,"itemhistory",true),
    Accessories("&#89adafAccessory Crafting & Abilities", Material.DRAGON_EGG, new String[]{ChatColor.GRAY+"toggle accessories",ChatColor.GRAY+"and all their associated uses"}, 16,"accessories",true),
    Close_End("&#89adafClose End Dimension", Material.END_PORTAL_FRAME, new String[]{ChatColor.GRAY+"clears all eyes of ender placed by players into",ChatColor.GRAY+"portal frames, disables the ability to refill them and",ChatColor.GRAY+"increases the end dimension border ready for the outer isles event."}, 39,"close_end",false);


    private String displayName;
    private Material material;
    private String[] lore;
    private Integer inventorySlot;
    private String localName;
    private boolean toggleable;


    FeatureEnum(String displayName, Material material, String[] lore, Integer inventorySlot, String localName, boolean toggleable) {
        this.displayName = displayName;
        this.material = material;
        this.lore = lore;
        this.inventorySlot = inventorySlot;
        this.localName = localName;
        this.toggleable = toggleable;
    }

    public boolean isToggleable() {
        return toggleable;
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

}
