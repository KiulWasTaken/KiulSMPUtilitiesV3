package kiul.kiulsmputilitiesv3.featuretoggle;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum FeatureEnum {
    /**
     * Kit
     */
    Crates("&eRandom Crates", Material.CHEST, new String[]{ChatColor.DARK_GRAY+"Left Click »",ChatColor.GRAY+"toggle the spawning of",ChatColor.GRAY+"random crate events"}, 18,"crates"),
    CombatTag("&eCombat Tag", Material.NAME_TAG, new String[]{ChatColor.DARK_GRAY+"Left Click »",ChatColor.GRAY+"toggle the combat tag",ChatColor.GRAY+"system and associated cooldowns"}, 18,"combattag"),
    CombatLog("&eCombat Log", Material.VILLAGER_SPAWN_EGG, new String[]{ChatColor.DARK_GRAY+"Left Click »",ChatColor.GRAY+"toggle the combat log",ChatColor.GRAY+"NPC that spawns on logout"}, 18,"combatlog"),
    ItemHistory("&eItem History", Material.CLOCK, new String[]{ChatColor.DARK_GRAY+"Left Click »",ChatColor.GRAY+"toggle the item history",ChatColor.GRAY+"lore changes on craft/grindstone events"}, 18,"itemhistory"),
    Accessories("&eAccessory Crafting & Abilities", Material.DRAGON_EGG, new String[]{ChatColor.DARK_GRAY+"Left Click »",ChatColor.GRAY+"toggle accessories",ChatColor.GRAY+"and all their associated uses"}, 18,"accessories"),
    Potions("&eCustom Potion Crafting", Material.GLASS_BOTTLE, new String[]{ChatColor.DARK_GRAY+"Left Click »",ChatColor.GRAY+"toggle the crafting of custom",ChatColor.GRAY+"haste and debuff resistance potions"}, 27,"potions");

    private String displayName;
    private Material material;
    private String[] lore;
    private Integer inventorySlot;
    private String localName;


    FeatureEnum(String displayName, Material material, String[] lore, Integer inventorySlot, String localName) {
        this.displayName = displayName;
        this.material = material;
        this.lore = lore;
        this.inventorySlot = inventorySlot;
        this.localName = localName;
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
