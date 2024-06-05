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

import static kiul.kiulsmputilitiesv3.accessories.IngredientItemEnum.*;


public enum AccessoryItemEnum {
    /**
     * Symbols
     */
            // RUBY - #aa0608 -> #eb352a
            // PERIDOT - #42b315 -> #8ef05e
            // TANZANITE - #1645c9 -> #5e9ff0
            // OPAL - #351296 -> #562deb

    testAccessory("Test Accessory",C.getURL("http://textures.minecraft.net/texture/737256eb057e4eedb78930b031acc24e6902f4e2984d0ab721115c70fb037439"), new String[]{""},"test",100,1.0,5.0,10.0, "% Test",null),
    ring("Silver Ring",C.getURL("http://textures.minecraft.net/texture/cdb464ea87f861b30f5e5e44a72dd8f2acf660431e57935319769820f0774778"),
            new String[]{ChatColor.GRAY+"slightly reduces",ChatColor.GRAY+"villager trade cost"},
            "ring_base",100,0.0,0.0,0.0, null,null),
    ring_ruby(Ruby.getPrefix()+"Silver Ring",C.getURL("http://textures.minecraft.net/texture/bf169e32a4fbe87d2d18f87732221a8311deacf3071fb3284a671d4eecd47246"),
            new String[]{
    C.t("&#AA0608q&#B10B0Bu&#B7100Fa&#BE1412d&#C51916r&#CB1E19u&#D2231Dp&#D92820l&#DF2D24e&#E63127s &#E93429t&#E32F26r&#DC2A22a&#D5251Fd&#CF211Be\n"),
            C.t("&#C81C18c&#C11714o&#BB1211s&#B40D0Dt &#AD080Aa&#AD080An&#B40D0Dd &#BB1211d&#C11714o&#C81C18u&#CF211Bb&#D5251Fl&#DC2A22e&#E32F26s\n"),
                    C.t("&#E93429t&#E63127r&#DF2D24a&#D92820d&#D2231De &#CB1E19r&#C51916e&#BE1412w&#B7100Fa&#B10B0Br&#AA0608d")},
            "ring_ruby",100,0.0,0.0,0.0, null,null),
    ring_opal(Opal.getPrefix()+"Silver Ring",C.getURL("http://textures.minecraft.net/texture/cc0f5b14defdb1df0e55546be88c54b68356313a54404c88c5d310757e925553"),
            new String[]{ChatColor.GRAY+"slightly reduces",ChatColor.GRAY+"villager trade cost",
                    C.t("&7and &#351296i&#38159Fn&#3C18A7c&#3F1AB0r&#431DB9e&#4620C2a&#4923CAs&#4D25D3e&#5028DCs &#532BE4m&#552CE9a&#522AE0x\n"),
                            C.t("&#4E27D7n&#4B24CFu&#4821C6m&#441EBDb&#411CB5e&#3D19ACr &#3A16A3o&#37139Af &#37139At&#3A16A3r&#3D19ACa&#411CB5d&#441EBDe&#4821C6s\n"),
                                    C.t("&#4B24CFf&#4E27D7o&#522AE0r &#552CE9e&#532BE4a&#5028DCc&#4D25D3h &#4923CAr&#4620C2e&#431DB9s&#3F1AB0t&#3C18A7o&#38159Fc&#351296k")},
            "ring_opal",100,0.0,0.0,0.0, null,null),
    ring_tanzanite(Tanzanite.getPrefix()+"Silver Ring",C.getURL("http://textures.minecraft.net/texture/5e191e4ec4eddb5a1d2fef458028c9ed3735ee415187a4de8412bd8a9513c1f4"),
            new String[]{ChatColor.GRAY+"slightly reduces",ChatColor.GRAY+"villager trade cost",
            C.t("&7and &#1645C9i&#1E4FCDm&#2659D2p&#2E63D6r&#366DDAo&#3E77DFv&#4681E3e&#4E8BE7s &#5695ECv&#5E9FF0i&#5695ECl&#4E8BE7l&#4681E3a&#3E77DFg&#366DDAe&#2E63D6r\n"),
                    C.t("&#2659D2m&#1E4FCDa&#1645C9s&#1E4FCDt&#2659D2e&#2E63D6r&#366DDAy &#3E77DFe&#4681E3x&#4E8BE7p&#5695ECe&#5E9FF0r&#5695ECi&#4E8BE7e&#4681E3n&#3E77DFc&#366DDAe &#2E63D6g&#2659D2a&#1E4FCDi&#1645C9n\n")},
            "ring_tanzanite",100,0.0,0.0,0.0, null,null),
    ring_peridot(Peridot.getPrefix()+"Silver Ring",C.getURL("http://textures.minecraft.net/texture/c697f2a782013a85af74e6f925c309d7456a2fdc9e753e38a3110f75bd0b7a02"),
            new String[]{ChatColor.GRAY+"slightly reduces",ChatColor.GRAY+"villager trade cost",
            C.t("&7and &#42B315i&#49B91Cn&#50BE22c&#57C429r&#5EC930e&#65CF36a&#6BD43Ds&#72DA43e&#79DF4As &#80E551a&#87EA57m&#8EF05Eo&#87EA57u&#80E551n&#79DF4At\n"),
                    C.t("&#72DA43o&#6BD43Df &#65CF36e&#5EC930x&#57C429p&#50BE22e&#49B91Cr&#42B315i&#49B91Ce&#50BE22n&#57C429c&#5EC930e &#65CF36d&#6BD43Dr&#72DA43o&#79DF4Ap&#80E551p&#87EA57e&#8EF05Ed\n"),
                            C.t("&#87EA57w&#80E551h&#79DF4Ae&#72DA43n &#6BD43Dt&#65CF36r&#5EC930a&#57C429d&#50BE22i&#49B91Cn&#42B315g\n")},
            "ring_peridot",100,0.0,0.0,0.0, null,null);

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
        if (getValueName() != null) {
            double value = random.nextGaussian() * getStandardDeviation() + getMean();
            itemMeta.getCustomTagContainer().setCustomTag(key, ItemTagType.DOUBLE, value);
            String posNeg;
            if (value > 0) {
                posNeg = ChatColor.BLUE + "+";
            } else {
                posNeg = ChatColor.RED + "";
            }

            lore.add(posNeg + C.twoPointDecimal.format(value) + valueName);
            lore.add(ChatColor.DARK_GRAY + itemUUID);
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;}




}
