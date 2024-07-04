package kiul.kiulsmputilitiesv3.crates;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public enum CrateTypeEnum {
    /**
     * Symbols
     */
    Gold(C.t("&#FFC70BG&#FFD007O&#FFDA04L&#FFE300D"),"gold",(long)1000*60*60,1,0,1000*60*1,Material.RAW_GOLD_BLOCK, new ArrayList<ItemStack>() {{
        add(new ItemStack(Material.GOLD_BLOCK));
        add(new ItemStack(Material.GOLD_INGOT));
        add(new ItemStack(Material.GOLDEN_APPLE));
        add(new ItemStack(Material.RAW_GOLD));
        add(new ItemStack(Material.GOLDEN_CARROT));
        add(new ItemStack(Material.TOTEM_OF_UNDYING));
    }},7),
    Oxidized(C.t("&#96694DO&#8F7757X&#878462I&#80926CD&#789F77I&#71AD81Z&#69BA8CE&#62C896D"),"oxidized",(long)1000*60*60,1,0,1000*60*5,Material.VAULT, new ArrayList<ItemStack>() {{
        add(new ItemStack(Material.EXPERIENCE_BOTTLE));
        add(new ItemStack(Material.GUNPOWDER));
        add(new ItemStack(Material.EMERALD));
        add(new ItemStack(Material.BOOK));
        add(new ItemStack(Material.ENCHANTED_BOOK));
        add(new ItemStack(Material.ENCHANTED_BOOK));
        add(new ItemStack(Material.ENCHANTED_BOOK));
    }},7),
    End(C.t("&#B766D9R&#C579D3E&#D48CCEM&#D48CCEO&#C579D3T&#B766D9E"),"end",(long)1000*60*120,2,2000,0,Material.SHULKER_BOX,new ArrayList<ItemStack>() {{
        add(new ItemStack(Material.EXPERIENCE_BOTTLE));
        add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
        add(new ItemStack(Material.DIAMOND_BLOCK));
        add(new ItemStack(Material.DIAMOND_ORE));
        add(new ItemStack(Material.SHULKER_BOX));
        add(new ItemStack(Material.SHULKER_SHELL));
        add(new ItemStack(Material.CHORUS_FRUIT));
        add(new ItemStack(Material.ELYTRA));
        add(new ItemStack(Material.ENDER_PEARL));
        add(new ItemStack(Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE));
        add(new ItemStack(Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE));
    }},5),
    Nether(C.t("&#CF2200M&#D1310EO&#D3401CL&#D3401CT&#D1310EE&#CF2200N"),"nether",(long)1000*60*120,2,2000,0,Material.GILDED_BLACKSTONE,new ArrayList<ItemStack>() {{
        add(new ItemStack(Material.NETHERITE_SCRAP));
        add(new ItemStack(Material.GOLD_BLOCK));
        add(new ItemStack(Material.NETHERITE_INGOT));
        add(new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
        add(new ItemStack(Material.NETHERITE_BLOCK));
        add(new ItemStack(Material.WITHER_SKELETON_SKULL));
        add(new ItemStack(Material.EXPERIENCE_BOTTLE));
        add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
        add(new ItemStack(Material.GOLDEN_APPLE));
        add(new ItemStack(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE));
        add(new ItemStack(Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE));
    }},5),
    BlackBox(C.t("&#252624B&#2C2D2BL&#333433A&#3A3B3AC&#3A3B3AK &#333433B&#2C2D2BO&#252624X"),"blackbox",(long)1000*60*180,3,4000,0,Material.BLACK_SHULKER_BOX,new ArrayList<ItemStack>() {{

    }},5);


    private String displayName;
    private String identifier;
    private Long spawnTime;
    private int unlockPhases;
    private int pointsPerPhase;
    private long unlockTime;
    private Material crateType;
    private ArrayList<ItemStack> lootTable;
    private int lootTableRolls;


    CrateTypeEnum(String displayName, String identifier, Long spawnTime, int unlockPhases, int pointsPerPhase, long unlockTime, Material crateType, ArrayList<ItemStack> lootTable,int lootTableRolls) {
        this.displayName = displayName;
        this.identifier = identifier;
        this.spawnTime = spawnTime;
        this.unlockPhases = unlockPhases;
        this.pointsPerPhase = pointsPerPhase;
        this.unlockTime = unlockTime;
        this.crateType = crateType;
        this.lootTable = lootTable;
        this.lootTableRolls = lootTableRolls;
    }

    public String getDisplayName() {
        return displayName;
    }
    public String getIdentifier() {
        return identifier;
    }
    public Long getSpawnTime() {return spawnTime;}
    public int getUnlockPhases() {return unlockPhases;}
    public int getPointsPerPhase() {return pointsPerPhase;}
    public long getUnlockTime() {return unlockTime;}
    public Material getCrateType() {return crateType;}
    public ArrayList<ItemStack> getLootTable() {return lootTable;}
    public int getLootTableRolls() {return lootTableRolls;}





}
