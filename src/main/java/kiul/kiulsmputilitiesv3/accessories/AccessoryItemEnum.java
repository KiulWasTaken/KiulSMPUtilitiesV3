package kiul.kiulsmputilitiesv3.accessories;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.text.DecimalFormat;
import java.util.*;


public enum AccessoryItemEnum {
    /**
     * Symbols
     */
    testAccessory("Test Accessory",Material.DIAMOND, new String[]{""},"test",100,1.0,5.0,10.0, "% Test",null),
    gluttony(C.t("&f&k0&r &eTrinket of Gluttony &f&k0"),Material.GOLDEN_CARROT, new String[]{ChatColor.GRAY+"Increases or decreases the saturation",ChatColor.GRAY+"and hunger values of any food eaten",ChatColor.GRAY+"to match the stats of this accessory","",ChatColor.BLUE+"+6 Hunger"},"gluttony",100,1.0,14.0,2.0," Saturation",null),
    nimble(C.t("&b&k0&r &fTrinket of Nimble &b&k0"),Material.QUARTZ, new String[]{ChatColor.GRAY+"Increases the attack speed of the user",ChatColor.GRAY+"but reduces their overall damage output","",ChatColor.RED+"-10.0% Attack Damage"},"nimble",300,1.0,10.0,2.5,"% Attack Speed",Attribute.GENERIC_ATTACK_SPEED);


    private String displayName;
    private Material material;
    private String[] lore;
    private String localName;
    private Integer range;
    private Double trackingMultiplier;
    private Double mean;
    private Double standardDeviation;
    private String valueName;
    private Attribute attribute;


    AccessoryItemEnum(String displayName, Material material, String[] lore, String localName,Integer range, Double trackingMultiplier,Double mean,Double standardDeviation,String valueName,Attribute attribute) {
        this.displayName = displayName;
        this.material = material;
        this.lore = lore;
        this.localName = localName;
        this.range = range;
        this.trackingMultiplier = trackingMultiplier;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
        this.valueName = valueName;
        this.attribute = attribute;
    }

    public String getDisplayName() {
        return displayName;
    }
    public Material getMaterial() {
        return material;
    }
    public String[] getLore() {return lore;}
    public String getLocalName() {
        return localName;
    }
    public String getValueName() {
        return valueName;
    }
    public Integer getRange() {return range;}
    public Double getTrackingMultiplier() {return trackingMultiplier;}
    public Double getMean() {return mean;}
    public Double getStandardDeviation() {return standardDeviation;}
    public Attribute getAttribute() {return attribute;}
    public ItemStack getAccessory() {
        List<String> lore = new ArrayList<>(Arrays.stream(getLore()).toList());
        Random random = new Random();
        String itemUUID = UUID.randomUUID().toString().replace("-","").substring(0,8);
        ItemStack itemStack = C.createItemStack(getDisplayName(),getMaterial(),1,getLore(),null,null,getLocalName());
        NamespacedKey key = new NamespacedKey(C.plugin, itemUUID);
        ItemMeta itemMeta = itemStack.getItemMeta();
        double value = random.nextGaussian()*getStandardDeviation()+getMean();
        itemMeta.getCustomTagContainer().setCustomTag(key, ItemTagType.DOUBLE, value);
        String posNeg;
        if(value>0) {
            posNeg = ChatColor.BLUE+"+";
        } else {
            posNeg = ChatColor.RED+"";
        }

        lore.add(posNeg+C.twoPointDecimal.format(value) + valueName);
        lore.add(ChatColor.DARK_GRAY+itemUUID);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;}




}
