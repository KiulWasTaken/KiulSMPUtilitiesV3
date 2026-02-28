package kiul.kiulsmputilitiesv3.towns.augments;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.ConfigData;
import kiul.kiulsmputilitiesv3.itemhistory.ItemMethods;
import kiul.kiulsmputilitiesv3.towns.AugmentEvent;
import kiul.kiulsmputilitiesv3.towns.Town;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum AugmentEnum {




    MACE_CLEAVE("Density X",new String[]{
            ChatColor.GRAY+"20% of smash attack damage will splash to other players",
            ChatColor.GRAY+"in a small radius on hit."},
            "mace_cleave",Enchantment.DENSITY,Material.MACE),

    XBOW_LAUNCHER("Slingshot",new String[]{
            ChatColor.GRAY+"Load any kind of potion into your crossbow and launch",
            ChatColor.GRAY+"it with the same velocity and firing arc as an arrow."},
            "xbow_launcher",null,Material.CROSSBOW),

    BOW_PIERCE("Power X",new String[]{
            ChatColor.GRAY+"Arrows will not be stopped when they collide with an entity",
            ChatColor.GRAY+"or shield."},
            "bow_pierce",Enchantment.POWER,Material.BOW),

    SWORD_SPEED("Footwork",new String[]{
            ChatColor.GRAY+"whenever holding this sword, get permanent speed 2"},
            "sword_speed",null,Material.NETHERITE_SWORD),

    AXE_STAGGER("Stagger",new String[]{
            ChatColor.GRAY+"When disabling a player's shield, axe melee damage will",
            ChatColor.GRAY+"be applied instead of discarded."},
            "axe_stagger",null,Material.NETHERITE_AXE),

    SHIELD_REGEN("Divine Protection",new String[]{
            ChatColor.GRAY+"Halves all incoming durability damage to this shield,",
            ChatColor.GRAY+"and grants regeneration 1 whilst blocking."},
            "shield_regen",null,Material.SHIELD),

    HELMET_SPLASH("Relay Effect",new String[]{
            ChatColor.GRAY+"When you gain a new buff from any source,broadcast",
            ChatColor.GRAY+"the effect and duration to teammates within 8 blocks."},
            "helmet_splash",null,Material.NETHERITE_HELMET),

    LEGGINGS_SNEAK("Swift Sneak X",new String[]{
            ChatColor.GRAY+"Your steps will not set off sculk sensors, and whilst",
            ChatColor.GRAY+"crouching you gain 3s of invisibility every second."},
            "leggings_sneak",Enchantment.SWIFT_SNEAK,Material.NETHERITE_LEGGINGS),

    CHESTPLATE_REACH("Exoskeleton",new String[]{
            ChatColor.GRAY+"Increases block placement and interaction range by 1"},
            "chestplate_reach",null,Material.NETHERITE_CHESTPLATE),

    BOOTS_FROSTWALKER("Frost Walker X",new String[]{
            ChatColor.GRAY+"water is immediately turned to ice when you are walking",
            ChatColor.GRAY+"or jumping two blocks above, and you can stand on powder",
            ChatColor.GRAY+"snow like you are wearing leather boots."},
            "boots_frostwalker",Enchantment.FROST_WALKER,Material.NETHERITE_BOOTS),

    BOOTS_FEATHERFALL("Feather Falling X",new String[]{
            ChatColor.GRAY+"ender pearls no longer deal damage when teleporting you",
            ChatColor.GRAY+"and endermites do not spawn from your pearls.",},
            "boots_featherfall",Enchantment.FEATHER_FALLING,Material.NETHERITE_BOOTS);

    private String title;
    private String[] lore;
    private String localName;
    private Enchantment prerequisite;
    private Material requiredType;

    AugmentEnum(String title, String[] lore,String localName,Enchantment prerequisite, Material requiredType) {
        this.title = C.LIGHT_PASTEL_PINK+title;
        this.lore = lore;
        this.localName = localName;
        this.requiredType = requiredType;
        this.prerequisite = prerequisite;
    }


    public String getTitle() {
        return title;
    }

    public List<String> getLore() {
        return Arrays.stream(lore).toList();
    }
    public String[] getLoreArray() {
        return lore;
    }

    public String getLocalName() {
        return localName;
    }

    public Material getRequiredType() {
        return requiredType;
    }

    public static AugmentEnum getAugment (String localName) {
        for (AugmentEnum augmentEnum : AugmentEnum.values()) {
            if (augmentEnum.localName.equals(localName)) {
                return augmentEnum;
            }
        }
        return null;
    }

    public static ItemStack repairItemLore (ItemStack preItemStack, ItemStack postItemStack) {
        AugmentEnum augment = null;
        for (AugmentEnum augmentEnum : AugmentEnum.values()) {
            if (preItemStack.getPersistentDataContainer().has(new NamespacedKey(C.plugin,"local"))) {
                String localName = preItemStack.getPersistentDataContainer().get(new NamespacedKey(C.plugin,"local"),PersistentDataType.STRING);
                if (augmentEnum.localName.equalsIgnoreCase(localName)) {
                    augment = augmentEnum;
                }
            }
        }
        if (augment == null) return preItemStack;

        ItemMeta preItemMeta = preItemStack.getItemMeta();
        ItemMeta postItemMeta = postItemStack.getItemMeta();
        List<String> lore = postItemMeta.getLore();
        for (int i = 0; i < preItemMeta.getEnchants().size(); i++) {
            lore.remove(i);
        }
        if (augment.getPrerequisite() == null) {
            lore.add(0,augment.getTitle());
        }
        for (Enchantment enchantment : postItemMeta.getEnchants().keySet()) {
            if (enchantment.equals(augment.getPrerequisite())) {
                lore.add(0,augment.getTitle());
                continue;
            }
            PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
            String rawText = plainSerializer.serialize(enchantment.displayName(postItemMeta.getEnchants().get(enchantment)));
            lore.add(0,ChatColor.GRAY+rawText);
        }

        postItemStack.setItemMeta(postItemMeta);
        return postItemStack;
    }

    public static String getRomanNumeral (int num) {
        return switch (num) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> "";
        };
    }

    public static ItemStack augmentItem (ItemStack itemStack, Town town, AugmentEnum augment) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> augmentLore = new ArrayList<>();
        if (augment.getPrerequisite() == null) {
            augmentLore.add(augment.getTitle());
            if (itemMeta.getEnchants().isEmpty()) {
                itemMeta.addEnchant(Enchantment.PROTECTION,1,true);
            }
        }
        for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
            if (augment.getPrerequisite() == null && enchantment.equals(Enchantment.PROTECTION) && itemMeta.getEnchants().get(enchantment) == 1) {
                continue;
            }
            if (enchantment.equals(augment.getPrerequisite())) {
                augmentLore.add(augment.getTitle() + ChatColor.DARK_GRAY + " (" + getRomanNumeral(itemMeta.getEnchants().get(enchantment)) +")");
                continue;
            }
            PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
            String rawText = plainSerializer.serialize(enchantment.displayName(itemMeta.getEnchants().get(enchantment)));
            augmentLore.add(ChatColor.GRAY+rawText);
        }



        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);



        augmentLore.add("");
        augmentLore.add(augment.getTitle());
        augmentLore.addAll(augment.getLore());
        augmentLore.add("");


        if (itemStack.getLore() == null || itemStack.getLore().isEmpty()) {
            itemMeta.setLore(augmentLore);

        } else {
            List<String> lore = itemMeta.getLore();
            for (int i = 0; i < augmentLore.size(); i++) {
                lore.add(i+1,augmentLore.get(i));
            }
            lore.add(augmentLore.size()," ");
            if (ConfigData.get().getBoolean("itemhistory")) {
                LocalDate currentDate = LocalDate.now();
                PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
                String rawText = plainSerializer.serialize(town.getTownName());
                ItemMethods.addLore(itemStack, ChatColor.GRAY + "\uD83D\uDD27 - " + rawText + ChatColor.DARK_GRAY + " (" + C.dtf.format(currentDate) + ")");
            }
            if (lore.get(0).isBlank()) lore.remove(0);
            itemMeta.setLore(lore);
        }


        itemMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin,"local"), PersistentDataType.STRING,augment.getLocalName());

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public Enchantment getPrerequisite() {
        return prerequisite;
    }
}
