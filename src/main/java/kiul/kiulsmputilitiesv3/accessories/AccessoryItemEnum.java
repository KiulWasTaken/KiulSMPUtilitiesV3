package kiul.kiulsmputilitiesv3.accessories;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.ItemTagType;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;


public enum AccessoryItemEnum {
    /**
     * Symbols
     */
    testAccessory("Test Accessory",C.getURL("http://textures.minecraft.net/texture/737256eb057e4eedb78930b031acc24e6902f4e2984d0ab721115c70fb037439"), new String[]{""},"test",100,1.0,5.0,10.0, "% Test",null);


    private String displayName;
    private URL URL;
    private String[] lore;
    private String localName;
    private Integer range;
    private Double trackingMultiplier;
    private Double mean;
    private Double standardDeviation;
    private String valueName;
    private Attribute attribute;


    AccessoryItemEnum(String displayName, URL url, String[] lore, String localName,Integer range, Double trackingMultiplier,Double mean,Double standardDeviation,String valueName,Attribute attribute) {
        this.displayName = displayName;
        this.URL = url;
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
        return Material.PLAYER_HEAD;
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

    public java.net.URL getURL() {
        return URL;
    }

    public Double getStandardDeviation() {return standardDeviation;}
    public Attribute getAttribute() {return attribute;}
    public ItemStack getAccessory() {
        List<String> lore = new ArrayList<>(Arrays.stream(getLore()).toList());
        Random random = new Random();
        String itemUUID = UUID.randomUUID().toString().replace("-","").substring(0,8);
        ItemStack itemStack = C.createItemStack(getDisplayName(),getMaterial(),1,getLore(),null,null,getLocalName(),getURL());
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
