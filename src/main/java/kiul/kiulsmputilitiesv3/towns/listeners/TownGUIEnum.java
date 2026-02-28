package kiul.kiulsmputilitiesv3.towns.listeners;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.net.URL;

public enum TownGUIEnum {

    AUGMENT(C.PASTEL_PINK+"✪ "+C.LIGHT_PASTEL_PINK+"Augments", Material.ENCHANTING_TABLE, new String[]{
            ChatColor.GRAY + "Custom enchantments which can be added to most tools, weapons and armour.",
            ChatColor.GRAY + "To get augments, activate them in this menu and drop the item you want to",
            ChatColor.GRAY + "augment onto the town core."}, 2,"augment",null),
    BOUNTIES(C.YELLOW+"⛃ "+C.GOLD+"Bounties", Material.PLAYER_HEAD, new String[]{
            ChatColor.GRAY + "Players accrue bounties by dealing damage to other players, and lose them by dying. ",
            ChatColor.GRAY + "You can turn in their skull to retrieve the bounty they had at the time you killed them,",
            ChatColor.GRAY + "and if you have never turned in their skull before the reward is multiplied.",
            }, 4,"bounty",C.getURL("http://textures.minecraft.net/texture/ab69967163c743ddb1f083566757576b9e63ac380cc150f518b33dc4e91ef712"));

    private String displayName;
    private Material material;
    private String[] lore;
    private Integer inventorySlot;
    private String localName;
    private URL skullValue;

    TownGUIEnum(String displayName, Material material, String[] lore, Integer inventorySlot, String localName, URL skullValue) {
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
