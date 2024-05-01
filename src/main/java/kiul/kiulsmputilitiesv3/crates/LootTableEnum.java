package kiul.kiulsmputilitiesv3.crates;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum LootTableEnum {
    /**
     * Symbols
     */
    gold_block("gold",Material.GOLD_BLOCK,16,32,1,2),
    gold_ingot("gold",Material.GOLD_INGOT,24,64,1,1),
    gold_gApple("gold",Material.GOLDEN_APPLE,4,12,0.3,2),
    gold_rawGold("gold",Material.RAW_GOLD,32,64,0.5,1),
    gold_garrot("gold",Material.GOLDEN_CARROT,32,64,0.5,1),
    gold_totem("gold",Material.TOTEM_OF_UNDYING,1,1,0.8,1),
    exp_bottles("exp",Material.EXPERIENCE_BOTTLE,48,64,1,1),
    exp_gunpowder("exp",Material.GUNPOWDER,48,64,0.5,1),
    exp_emeralds("exp",Material.EMERALD,48,64,0.5,1),
    exp_book("exp",Material.BOOK,32,64,0.5,1),
    exp_ebook("exp",Material.ENCHANTED_BOOK,1,1,1,1),
    end_bottles("end",Material.EXPERIENCE_BOTTLE,52,64,1.2,1),
    end_eGap("end",Material.ENCHANTED_GOLDEN_APPLE,2,4,0.5,1),
    end_diamondArmour("end",Material.DIAMOND_BLOCK,1,1,0.5,1),
    end_diamondTools("end",Material.DIAMOND_ORE,1,1,0.5,1),
    end_shulker("end",Material.SHULKER_BOX,1,1,1,2),
    end_chorusFruit("end",Material.CHORUS_FRUIT,32,64,0.5,1),
    end_elytra("end",Material.ELYTRA,1,1,0.05,3),
    end_pearl("end",Material.ENDER_PEARL,12,16,0.5,1),
    end_eyeTrim("end",Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,1,1,0.5,1),
    end_spireTrim("end",Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,1,1,0.5,1),
    nether_scrap("nether",Material.NETHERITE_SCRAP,4,8,1,1),
    nether_goldBlock("nether",Material.GOLD_BLOCK,24,48,1,1),
    nether_nIngot("nether",Material.NETHERITE_INGOT,1,4,0.5,1),
    nether_upgrade("nether",Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,1,4,0.4,1),
    nether_armour("nether",Material.NETHERITE_BLOCK,1,1,0.8,1),
    nether_skulls("nether",Material.WITHER_SKELETON_SKULL,2,6,0.5,1),
    nether_exp("nether",Material.EXPERIENCE_BOTTLE,52,64,1.2,1),
    nether_eGap("nether",Material.ENCHANTED_GOLDEN_APPLE,4,8,0.8,1),
    nether_gap("nether",Material.GOLDEN_APPLE,16,32,1,1),
    nether_ribTrim("nether",Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,1,1,0.3,1),
    nether_snoutTrim("nether",Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,1,1,0.3,1);




    private String crateType;
    private Material material;
    private int minStackSize;
    private int maxStackSize;
    private double weight;
    private int rollConsumption;


    LootTableEnum(String crateType, Material material, int minStackSize, int maxStackSize, double weight, int rollConsumption) {
        this.crateType = crateType;
        this.material = material;
        this.minStackSize = minStackSize;
        this.maxStackSize = maxStackSize;
        this.weight = weight;
        this.rollConsumption = rollConsumption;
    }

    public String getCrateType() {
        return crateType;
    }
    public Material getMaterial() {
        return material;
    }
    public int getMinStackSize() {return minStackSize;}
    public int getMaxStackSize() {
        return maxStackSize;
    }
    public double getWeight() {return weight;}
    public int getRollConsumption() {return rollConsumption;}




}
