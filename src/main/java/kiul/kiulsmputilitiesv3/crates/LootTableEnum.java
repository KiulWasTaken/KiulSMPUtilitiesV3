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
    gold_gem("gold",Material.PLAYER_HEAD,1,1,0.5,-1),


    oxidized_core("oxidized",Material.HEAVY_CORE,1,1,1,3),
    oxidized_key("oxidized",Material.OMINOUS_TRIAL_KEY,4,8,0.7,-1),
    oxidized_eGaps("oxidized",Material.ENCHANTED_GOLDEN_APPLE,1,4,0.5,1),
    oxidized_maceBook("oxidized",Material.ENCHANTED_BOOK,1,1,1,1),
    oxidized_bottles("oxidized",Material.EXPERIENCE_BOTTLE,48,64,0.4,1),
    oxidized_TNT("oxidized",Material.IRON_BLOCK,12,24,0.4,1),
    oxidized_windCharges("oxidized",Material.WIND_CHARGE,48,64,0.3,1),
    oxidized_breezeRods("oxidized",Material.BREEZE_ROD,32,64,0.3,1),
    oxidized_omen("oxidized",Material.OMINOUS_BOTTLE,1,4,0.3,1),
    oxidized_boltTrim("oxidized",Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE,1,1,0.1,1),
    oxidized_flowTrim("oxidized",Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE,1,1,0.1,1),
    oxidized_gem("oxidized",Material.PLAYER_HEAD,1,1,0.2,-1),


    end_bottles("end",Material.EXPERIENCE_BOTTLE,52,64,0.3,1),
    end_eGap("end",Material.ENCHANTED_GOLDEN_APPLE,2,4,0.5,1),
    end_breathe("end",Material.DRAGON_BREATH,8,24,0.5,1),
    end_diamondArmour("end",Material.DIAMOND_BLOCK,1,1,0.8,1),
    end_diamondTools("end",Material.DIAMOND_ORE,1,1,0.6,1),
    end_shulker("end",Material.SHULKER_BOX,1,1,0.8,2),
    end_chorusFruit("end",Material.CHORUS_FRUIT,32,64,0.3,1),
    end_elytra("end",Material.ELYTRA,1,1,0.05,3),
    end_pearl("end",Material.ENDER_PEARL,12,16,0.3,1),
    end_eyeTrim("end",Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,1,1,0.5,1),
    end_spireTrim("end",Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,1,1,0.5,1),
    end_gem("end",Material.PLAYER_HEAD,1,1,0.5,-1),


    nether_scrap("nether",Material.NETHERITE_SCRAP,4,8,1,1),
    nether_goldBlock("nether",Material.GOLD_BLOCK,24,48,1,1),
    nether_nIngot("nether",Material.NETHERITE_INGOT,1,4,0.5,1),
    nether_upgrade("nether",Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,1,4,0.4,1),
    nether_armour("nether",Material.NETHERITE_BLOCK,1,1,0.8,1),
    nether_skulls("nether",Material.WITHER_SKELETON_SKULL,2,6,0.5,1),
    nether_exp("nether",Material.EXPERIENCE_BOTTLE,52,64,0.4,1),
    nether_eGap("nether",Material.ENCHANTED_GOLDEN_APPLE,4,8,0.8,1),
    nether_gap("nether",Material.GOLDEN_APPLE,16,32,0.4,1),
    nether_ribTrim("nether",Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,1,1,0.5,1),
    nether_snoutTrim("nether",Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,1,1,0.5,1),
    nether_gem("nether",Material.PLAYER_HEAD,1,1,0.4,-1);




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
