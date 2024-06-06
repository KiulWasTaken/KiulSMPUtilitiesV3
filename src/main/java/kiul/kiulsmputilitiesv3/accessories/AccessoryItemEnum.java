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
            // CONSTANT - #9876AA

    testAccessory("Test Accessory",C.getURL("http://textures.minecraft.net/texture/737256eb057e4eedb78930b031acc24e6902f4e2984d0ab721115c70fb037439"), new String[]{""},"test",100,1.0,5.0,10.0, "% Test",null),
    ring("Silver Ring",C.getURL("http://textures.minecraft.net/texture/cdb464ea87f861b30f5e5e44a72dd8f2acf660431e57935319769820f0774778"),
            new String[]{ChatColor.GRAY+"slightly reduces",ChatColor.GRAY+"villager trade cost"},
            "ring_base",300,0.5,0.0,0.0, null,null),
    ring_ruby(Ruby.getPrefix()+"Silver Ring",C.getURL("http://textures.minecraft.net/texture/bf169e32a4fbe87d2d18f87732221a8311deacf3071fb3284a671d4eecd47246"),
            new String[]{ChatColor.GRAY+""+ChatColor.STRIKETHROUGH+"slightly reduces",ChatColor.GRAY+""+ChatColor.STRIKETHROUGH+"villager trade cost",
    C.t("&#AA0608q&#B10B0Bu&#B7100Fa&#BE1412d&#C51916r&#CB1E19u&#D2231Dp&#D92820l&#DF2D24e&#E63127s &#E93429t&#E32F26r&#DC2A22a&#D5251Fd&#CF211Be\n"),
            C.t("&#C81C18c&#C11714o&#BB1211s&#B40D0Dt &#AD080Aa&#AD080An&#B40D0Dd &#BB1211d&#C11714o&#C81C18u&#CF211Bb&#D5251Fl&#DC2A22e&#E32F26s\n"),
                    C.t("&#E93429t&#E63127r&#DF2D24a&#D92820d&#D2231De &#CB1E19r&#C51916e&#BE1412w&#B7100Fa&#B10B0Br&#AA0608d")},
            "ring_ruby",275,0.5,0.0,0.0, null,null),
    ring_opal(Opal.getPrefix()+"Silver Ring",C.getURL("http://textures.minecraft.net/texture/cc0f5b14defdb1df0e55546be88c54b68356313a54404c88c5d310757e925553"),
            new String[]{ChatColor.GRAY+"slightly reduces",ChatColor.GRAY+"villager trade cost",
                    C.t("&7and &#351296i&#38159Fn&#3C18A7c&#3F1AB0r&#431DB9e&#4620C2a&#4923CAs&#4D25D3e&#5028DCs &#532BE4m&#552CE9a&#522AE0x\n"),
                            C.t("&#4E27D7n&#4B24CFu&#4821C6m&#441EBDb&#411CB5e&#3D19ACr &#3A16A3o&#37139Af &#37139At&#3A16A3r&#3D19ACa&#411CB5d&#441EBDe&#4821C6s\n"),
                                    C.t("&#4B24CFf&#4E27D7o&#522AE0r &#552CE9e&#532BE4a&#5028DCc&#4D25D3h &#4923CAr&#4620C2e&#431DB9s&#3F1AB0t&#3C18A7o&#38159Fc&#351296k")},
            "ring_opal",350,0.5,0.0,0.0, null,null),
    ring_tanzanite(Tanzanite.getPrefix()+"Silver Ring",C.getURL("http://textures.minecraft.net/texture/5e191e4ec4eddb5a1d2fef458028c9ed3735ee415187a4de8412bd8a9513c1f4"),
            new String[]{ChatColor.GRAY+"slightly reduces",ChatColor.GRAY+"villager trade cost",
            C.t("&7and &#1645C9i&#1E4FCDm&#2659D2p&#2E63D6r&#366DDAo&#3E77DFv&#4681E3e&#4E8BE7s &#5695ECv&#5E9FF0i&#5695ECl&#4E8BE7l&#4681E3a&#3E77DFg&#366DDAe&#2E63D6r\n"),
                    C.t("&#2659D2m&#1E4FCDa&#1645C9s&#1E4FCDt&#2659D2e&#2E63D6r&#366DDAy &#3E77DFe&#4681E3x&#4E8BE7p&#5695ECe&#5E9FF0r&#5695ECi&#4E8BE7e&#4681E3n&#3E77DFc&#366DDAe &#2E63D6g&#2659D2a&#1E4FCDi&#1645C9n\n")},
            "ring_tanzanite",350,0.5,0.0,0.0, null,null),
    ring_peridot(Peridot.getPrefix()+"Silver Ring",C.getURL("http://textures.minecraft.net/texture/c697f2a782013a85af74e6f925c309d7456a2fdc9e753e38a3110f75bd0b7a02"),
            new String[]{ChatColor.GRAY+"slightly reduces",ChatColor.GRAY+"villager trade cost",
            C.t("&7and &#42B315i&#49B91Cn&#50BE22c&#57C429r&#5EC930e&#65CF36a&#6BD43Ds&#72DA43e&#79DF4As &#80E551a&#87EA57m&#8EF05Eo&#87EA57u&#80E551n&#79DF4At\n"),
                    C.t("&#72DA43o&#6BD43Df &#65CF36e&#5EC930x&#57C429p&#50BE22e&#49B91Cr&#42B315i&#49B91Ce&#50BE22n&#57C429c&#5EC930e &#65CF36d&#6BD43Dr&#72DA43o&#79DF4Ap&#80E551p&#87EA57e&#8EF05Ed\n"),
                            C.t("&#87EA57w&#80E551h&#79DF4Ae&#72DA43n &#6BD43Dt&#65CF36r&#5EC930a&#57C429d&#50BE22i&#49B91Cn&#42B315g\n")},
            "ring_peridot",350,0.5,0.0,0.0, null,null),

    eye("Crystallised Eye",C.getURL("http://textures.minecraft.net/texture/6dfc1ead1e88e0e16a4db114289c930f01732e8712821f954a5d736b1cea5402"),
            new String[]{ChatColor.GRAY+"players less than 10 blocks",ChatColor.GRAY+"away are afflicted with a glowing",ChatColor.GRAY+" effect only you can see."},
            "eye_base",500,1.2,0.0,0.0, null,null),
    eye_ruby(Ruby.getPrefix()+"Crystallised Eye",C.getURL("http://textures.minecraft.net/texture/4615791c42eb413979bac37ccb2d7f879bc7cdf1e32633460990593a0e729843"),
            new String[]{C.t("&7players less than &#AA06082&#CB1E190 &#CB1E19b&#AA0608l&#CB1E19o&#EB352Ac&#CB1E19k&#AA0608s"),ChatColor.GRAY+"away are afflicted with a glowing",ChatColor.GRAY+" effect only you can see."},
            "eye_ruby",750,1.0,0.0,0.0, null,null),
    eye_peridot(Peridot.getPrefix()+"Crystallised Eye",C.getURL("http://textures.minecraft.net/texture/eff30b6509ec06651d67dfc70cb9f8e190136cb263dc542fd7bde0ffc68f70e4"),
            new String[]{ChatColor.GRAY+"players less than 10 blocks",ChatColor.GRAY+"away are afflicted with a glowing",ChatColor.GRAY+" effect only you can see."
            ,C.t("&#42B315y&#4AB91Co&#51BF24u &#60CB32c&#68D23Aa&#70D841n &#7FE44Fh&#86EA57e&#8EF05Ea&#86EA57r &#77DE48a&#70D841l&#68D23Al &#59C52Bn&#51BF24o&#4AB91Ci&#42B315s&#4AB91Ce")
            ,C.t("&#59C52Bm&#60CB32a&#68D23Ad&#70D841e &#7FE44Fb&#86EA57y &#86EA57p&#7FE44Fl&#77DE48a&#70D841y&#68D23Ae&#60CB32r&#59C52Bs &#4AB91Ca&#42B315n&#4AB91Cd &#59C52Bi&#60CB32t&#68D23As")
            ,C.t("&#77DE48d&#7FE44Fi&#86EA57r&#8EF05Ee&#86EA57c&#7FE44Ft&#77DE48i&#70D841o&#68D23An &#59C52Bw&#51BF24i&#4AB91Ct&#42B315h&#4AB91Ci&#51BF24n &#60CB322&#68D23A0&#70D8410")},
            "eye_peridot",500,1.0,0.0,0.0, null,null),
    eye_tanzanite(Tanzanite.getPrefix()+"Crystallised Eye",C.getURL("http://textures.minecraft.net/texture/7d71a145bae550c75836124dbf169eed104c1dbec1e5e72d96792158cc68d461"),
            new String[]{ChatColor.GRAY+"players less than 10 blocks",ChatColor.GRAY+"away are afflicted with a glowing",ChatColor.GRAY+" effect only you can see."
            ,C.t("&#1645C9t&#1D4ECDe&#2457D1a&#2C60D5m&#3369D9m&#3A72DDa&#417BE0t&#4884E4e&#508DE8s &#5E9FF0i&#5796ECn &#4884E4y&#417BE0o&#3A72DDu&#3369D9r &#2457D1r&#1D4ECDe&#1645C9n&#1D4ECDd&#2457D1e&#2C60D5r")
            ,C.t("&#3A72DDd&#417BE0i&#4884E4s&#508DE8t&#5796ECa&#5E9FF0n&#5796ECc&#508DE8e &#417BE0a&#3A72DDr&#3369D9e &#2457D1a&#1D4ECDf&#1645C9f&#1D4ECDl&#2457D1i&#2C60D5c&#3369D9t&#3A72DDe&#417BE0d")
            ,C.t("&#508DE8w&#5796ECi&#5E9FF0t&#5796ECh &#4884E4a &#3A72DDg&#3369D9l&#2C60D5o&#2457D1w&#1D4ECDi&#1645C9n&#1D4ECDg &#2C60D5e&#3369D9f&#3A72DDf&#417BE0e&#4884E4c&#508DE8t &#5E9FF0t&#5796ECh&#508DE8a&#4884E4t")
            ,C.t("&#3A72DDo&#3369D9n&#2C60D5l&#2457D1y &#1645C9y&#1D4ECDo&#2457D1u &#3369D9c&#3A72DDa&#417BE0n &#508DE8s&#5796ECe&#5E9FF0e")},
            "eye_tanzanite",350,1.0,0.0,0.0, null,null),
    eye_opal(Opal.getPrefix()+"Crystallised Eye",C.getURL("http://textures.minecraft.net/texture/cd43260970f8d3c6bea5bb746db83d3a09dfa7993c9f15f83cbf92e446d68a43"),
            new String[]{ChatColor.GRAY+"players less than 10 blocks",ChatColor.GRAY+"away are afflicted with a glowing",ChatColor.GRAY+" effect only you can see."
            ,C.t("&#351296b&#3915A0l&#3C18A9o&#401BB3c&#441EBCk&#4821C6s &#4F27D9t&#532AE3h&#562DEAa&#522AE0t &#4A24CDh&#4721C4a&#431DBAv&#3F1AB1e &#38149Db&#361398e&#3A16A2e&#3D19ABn")
            ,C.t("&#451FBEr&#4822C8e&#4C25D2c&#5028DBe&#542BE5n&#552CE8t&#5129DEl&#4D26D5y &#4620C2p&#421DB8l&#3E1AAEa&#3B17A5c&#37149Be&#37139Ad &#3E19ADb&#421CB7y &#4923CAa")
            ,C.t("&#5129DDp&#542CE7l&#542BE6a&#5028DCy&#4D25D3e&#4922C9r &#411CB6w&#3E19ACi&#3A16A3l&#361399l &#3B17A6g&#3F1AAFl&#431DB9o&#4620C3w")
            ,C.t("&#4E26D6(&#5129DFv&#552CE9i&#532BE4s&#4F28DAi&#4C25D0b&#4822C7l&#441EBDe &#3D18AAt&#3915A1o &#38159Ey&#3C18A8o&#401BB2u &#4721C5o&#4B24CEn&#4F27D8l&#522AE1y&#562DEB)")},
            "eye_opal",600,1.0,0.0,0.0, null,null),
    potion("Cocktail",C.getURL("http://textures.minecraft.net/texture/4222bae2515c6205371f1d6cc232997c340baeef8c04fc1db13c23ea488f61e0"),
            new String[]{ChatColor.GRAY+"buff durations increased by 10%",ChatColor.GRAY+"debuff durations increased by 25%",ChatColor.GRAY+"debuff resistance does not affect you."},
            "potion_base",200,1.0,0.0,0.0, null,null),
    potion_ruby(Ruby.getPrefix()+"Cocktail",C.getURL("http://textures.minecraft.net/texture/6a054d2964f5fe09f6bb9476bac492e0aa049e653b0a3ec6a4604b50cc60886"),
            new String[]{ChatColor.GRAY+""+ChatColor.STRIKETHROUGH+"buff durations increased by 10%",ChatColor.GRAY+"debuff durations increased by 25%",ChatColor.GRAY+"debuff resistance does not affect you."
            ,C.t("&#AA0608W&#B00A0Bh&#B50E0Ee&#BB1211n &#C61A17a&#CC1F1Af&#D2231Df&#D72720e&#DD2B23c&#E32F26t&#E83329e&#E83329d &#DD2B23w&#D72720i&#D2231Dt&#CC1F1Ah")
            ,C.t("&#9876AA&oJUMP BOOST,&r &#D72720m&#DD2B23e&#E32F26l&#E83329e&#E83329e &#DD2B23a&#D72720t&#D2231Dt&#CC1F1Aa&#C61A17c&#C11614k")
            ,C.t("&#B50E0Ed&#B00A0Ba&#AA0608m&#B00A0Ba&#B50E0Eg&#BB1211e &#C61A17i&#CC1F1As &#D72720i&#DD2B23n&#E32F26c&#E83329r&#E83329e&#E32F26a&#DD2B23s&#D72720e&#D2231Dd &#C61A17b&#C11614y &#B50E0E1&#B00A0B0&#AA0608%")},
            "potion_ruby",400,1.0,0.0,0.0, null,null),
    potion_peridot(Peridot.getDisplayName()+"Cocktail",C.getURL("http://textures.minecraft.net/texture/a22f7e317d7fc8a2ac068b0d8cdf1c318cb65dd7a87e09ff3656db6daa22a337"),
            new String[]{ChatColor.GRAY+""+ChatColor.STRIKETHROUGH+"buff durations increased by 10%",ChatColor.GRAY+"debuff durations increased by 25%",ChatColor.GRAY+"debuff resistance does not affect you."
            ,C.t("&#42B315d&#4CBB1Fu&#57C429r&#61CC33a&#6BD43Dt&#76DD47i&#80E551o&#8BED5Bn &#7DE24Do&#72DA43f &#5EC930t&#53C126u&#49B91Cr&#45B618t&#50BE22l&#5AC62Ce &#6FD740m&#79DF4Aa&#84E854s&#8EF05Et&#84E854e&#79DF4Ar ")
            ,C.t("&#65CF36e&#5AC62Cf&#50BE22f&#45B618e&#49B91Cc&#53C126t&#5EC930s &#72DA43a&#7DE24Dr&#87EA57e &#80E551d&#76DD47o&#6BD43Du&#61CC33b&#57C429l&#4CBB1Fe&#42B315d")},
            "potion_peridot",400,1.0,0.0,0.0, null,null),
    potion_tanzanite(Tanzanite.getDisplayName()+"Cocktail",C.getURL("http://textures.minecraft.net/texture/9fc7bf2c693b73dceaefddbea12c607adca34c92f13f4c72b96ce97a6b271f60"),
            new String[]{ChatColor.GRAY+""+ChatColor.STRIKETHROUGH+"buff durations increased by 10%",ChatColor.GRAY+"debuff durations increased by 25%",ChatColor.GRAY+"debuff resistance does not affect you."
                    ,C.t("&#1645C9d&#2052CEo&#2A5ED4u&#346BD9b&#3E77DFl&#4884E4e&#5290EAs &#5695EBt&#4C88E6h&#427BE1e &#2D62D6d&#2356D0u&#1949CBr&#1D4DCDa&#275AD2t&#3166D8i&#3B73DDo&#4580E2n &#5999EDo&#5999EDf")
                    ,C.t("&#4580E2r&#3B73DDe&#3166D8g&#275AD2e&#1D4DCDn&#1949CBe&#2356D0r&#2D62D6a&#376FDBt&#427BE1i&#4C88E6o&#5695EBn &#5290EAe&#4884E4f&#3E77DFf&#346BD9e&#2A5ED4c&#2052CEt&#1645C9s")},
            "potion_tanzanite",400,1.0,0.0,0.0, null,null),
    potion_opal(Opal.getDisplayName()+"Cocktail",C.getURL("http://textures.minecraft.net/texture/54d6fd74b81bddd387e5afe5b678752608a4a03b317e5d0650fb01c78f361f63"),
            new String[]{ChatColor.GRAY+""+ChatColor.STRIKETHROUGH+"buff durations increased by 10%",ChatColor.GRAY+"debuff durations increased by 25%",ChatColor.GRAY+"debuff resistance does not affect you."
                    ,C.t("&#351296w&#38149Dh&#3A16A3e&#3D18AAn &#421CB7a&#441EBDf&#4721C4f&#4923CAe&#4C25D1c&#4E27D7t&#5129DEe&#532BE4d &#532BE4w&#5129DEi&#4E27D7t&#4C25D1h")
                    ,C.t("&#4721C4i&#441EBDn&#421CB7v&#3F1AB0i&#3D18AAs&#3A16A3i&#38149Db&#351296i&#38149Dl&#3A16A3i&#3D18AAt&#3F1AB0y&#421CB7, &#4721C4y&#4923CAo&#4C25D1u &#5129DEn&#532BE4o &#532BE4l&#5129DEo&#4E27D7n&#4C25D1g&#4923CAe&#4721C4r")
                    ,C.t("&#421CB7p&#3F1AB0r&#3D18AAo&#3A16A3d&#38149Du&#351296c&#38149De #3D18AAp&#3F1AB0o&#421CB7t&#441EBDi&#4721C4o&#4923CAn &#4E27D7p&#5129DEa&#532BE4r&#562DEBt&#532BE4i&#5129DEc&#4E27D7l&#4C25D1e")
                    ,C.t("&#4721C4e&#441EBDf&#421CB7f&#3F1AB0e&#3D18AAc&#3A16A3t&#38149Ds&#351296.")},
            "potion_opal",400,1.0,0.0,0.0, null,null),
    heart("Life Crystal",C.getURL("http://textures.minecraft.net/texture/d997e767e1b05502ede2d9804936b0e6557852c5ded31a39cf4ba3cb384a35c2"),
            new String[]{ChatColor.GRAY+"increases the potency of",ChatColor.GRAY+"regeneration effects."},
            "heart_base",100,1.0,0.0,0.0, null,null),
    heart_ruby(Ruby.getPrefix()+"Life Crystal",C.getURL("http://textures.minecraft.net/texture/3cdaa5bf0bc542050c734c58f4283215c9a4f0f18c3ddad871913dbf667ce433"),
            new String[]{ChatColor.GRAY+"increases the potency of",ChatColor.GRAY+"regeneration effects."
            ,C.t("&#AA0608a&#AF090Ap&#B30D0Dp&#B8100Fl&#BD1312i&#C11714e&#C61A17s &#CF211Br&#D4241Ee&#D82820s&#DD2B23i&#E22E25s&#E63228t&#EB352Aa&#E63228n&#E22E25c&#DD2B23e &#D4241Er&#CF211Be&#CB1E19l&#C61A17a&#C11714t&#BD1312i&#B8100Fv&#B30D0De")
            ,C.t("&#AA0608t&#AF090Ao &#B8100Ft&#BD1312h&#C11714e &#CB1E19a&#CF211Bm&#D4241Eo&#D82820u&#DD2B23n&#E22E25t &#EB352Ao&#E63228f &#DD2B23e&#D82820n&#D4241Ee&#CF211Bm&#CB1E19y")
            ,C.t("&#C11714p&#BD1312l&#B8100Fa&#B30D0Dy&#AF090Ae&#AA0608r&#AF090As &#B8100Fa&#BD1312r&#C11714o&#C61A17u&#CB1E19n&#CF211Bd &#D82820y&#DD2B23o&#E22E25u&#E63228. &#E63228B&#E22E25a&#DD2B23s&#D82820e")
            ,C.t("&#CF211Ba&#CB1E19r&#C61A17m&#C11714o&#BD1312r &#B30D0Dv&#AF090Aa&#AA0608l&#AF090Au&#B30D0De &#BD1312i&#C11714s &#CB1E19d&#CF211Be&#D4241Ec&#D82820r&#DD2B23e&#E22E25a&#E63228s&#EB352Ae&#E63228d")
            ,C.t("&#DD2B23w&#D82820h&#D4241Ee&#CF211Bn &#C61A17u&#C11714s&#BD1312i&#B8100Fn&#B30D0Dg &#AA0608t&#AF090Ah&#B30D0Di&#B8100Fs &#C11714a&#C61A17c&#CB1E19c&#CF211Be&#D4241Es&#D82820s&#DD2B23o&#E22E25r&#E63228y&#EB352A.")},
            "heart_ruby",500,1.0,0.0,0.0, null,null),
    heart_peridot(Peridot.getPrefix()+"Life Crystal",C.getURL("http://textures.minecraft.net/texture/f78593201dac0e58c68127d9b0da2b2a1f0463c07838a9366bb3388f6f04a299"),
            new String[]{ChatColor.GRAY+"increases the potency of",ChatColor.GRAY+"regeneration effects."
            ,C.t("&#42B315i&#47B71Af &#52C025y&#58C42Ao&#5DC92Fu &#68D23Aa&#6DD63Fr&#73DA44e &#7EE34Ea&#83E754b&#89EC59o&#8EF05Ev&#89EC59e &#7EE34E8&#78DF49♥&#73DA44, &#68D23Ad&#63CD34a&#5DC92Fm&#58C42Aa&#52C025g&#4DBC1Fe")
            ,C.t("&#42B315t&#47B71Ah&#4DBC1Fa&#52C025t &#5DC92Fw&#63CD34o&#68D23Au&#6DD63Fl&#73DA44d &#7EE34Eh&#83E754a&#89EC59v&#8EF05Ee &#83E754o&#7EE34Et&#78DF49h&#73DA44e&#6DD63Fr&#68D23Aw&#63CD34i&#5DC92Fs&#58C42Ae")
            ,C.t("&#4DBC1Fk&#47B71Ai&#42B315l&#47B71Al&#4DBC1Fe&#52C025d &#5DC92Fy&#63CD34o&#68D23Au &#73DA44l&#78DF49e&#7EE34Ea&#83E754v&#89EC59e&#8EF05Es &#83E754y&#7EE34Eo&#78DF49u &#6DD63Fo&#68D23An &#5DC92F0&#58C42A.&#52C0255&#4DBC1F♥&#47B71A.")},
            "heart_peridot",500,1.0,0.0,0.0, null,null),
    heart_tanzanite(Tanzanite.getPrefix()+"Life Crystal",C.getURL("http://textures.minecraft.net/texture/8c4767571f5efe5a578058fbc0489888e5023d7b0f1de804f25e89253313189"),
            new String[]{ChatColor.GRAY+"increases the potency of",ChatColor.GRAY+"regeneration effects."
            ,C.t("&#1645C9w&#1B4BCCh&#1F51CEi&#2457D1l&#295DD3s&#2E63D6t &#376FDBa&#3C74DEb&#417AE0o&#4580E3v&#4A86E5e &#5492EA6&#5898ED♥&#5D9EF0, &#5594EB3&#518EE90&#4C88E6% &#427CE1o&#3E76DEf &#346BD9d&#2F65D7a&#2B5FD4m&#2659D2a&#2153CFg&#1C4DCCe")
            ,C.t("&#1949CBd&#1E4FCDe&#2355D0a&#275BD2l&#2C61D5t &#366DDAt&#3A72DDo &#447EE2t&#4984E4e&#4D8AE7a&#5290EAm&#5796ECm&#5C9CEFa&#5C9CEFt&#5796ECe&#5290EAs &#4984E4i&#447EE2n &#3A72DDa &#3167D81&#2C61D50 &#2355D0b&#1E4FCDl&#1949CBo&#1847CAc&#1C4DCCk")
            ,C.t("&#2659D2r&#2B5FD4a&#2F65D7d&#346BD9i&#3971DCu&#3E76DEs &#4782E4i&#4C88E6s &#5594EBr&#5A9AEEe&#5D9EF0d&#5898EDi&#5492EAr&#4F8CE8e&#4A86E5c&#4580E3t&#417AE0e&#3C74DEd &#3269D8t&#2E63D6o &#2457D1y&#1F51CEo&#1B4BCCu&#1645C9.")},
            "heart_tanzanite",500,1.0,0.0,0.0, null,null),
    heart_opal(Opal.getPrefix()+"Life Crystal",C.getURL("http://textures.minecraft.net/texture/87fe1102b04e8f5cd280cab5c42ddf4a844193339035b9f9e4a898b7b6113713"),
            new String[]{ChatColor.GRAY+"increases the potency of",ChatColor.GRAY+"regeneration effects."
            ,C.t("&#351296i&#37149Bn&#3915A0c&#3B17A5r&#3D18AAe&#3F1AAFa&#411BB4s&#421DB9e&#441FBEs &#4822C8t&#4A23CCh&#4C25D1e &#5028DBp&#522AE0o&#542BE5t&#562DEAe&#542CE7n&#522AE2c&#5129DDy &#4D25D3a&#4B24CEn&#4922C9d")
            ,C.t("&#451FBFr&#431EBAe&#411CB5d&#3F1AB0u&#3D19ABc&#3B17A7e&#3916A2s &#361398t&#361399h&#38159Ee &#3C18A8d&#3E19ADu&#401BB2r&#421CB7a&#441EBCt&#4620C1i&#4821C6o&#4A23CBn &#4D26D5o&#4F27DAf")
            ,C.t("&#532BE4e&#552CE9f&#552CE9f&#532BE4e&#5129DFc&#4F27DAt&#4D26D5s &#4A23CBg&#4821C6i&#4620C1v&#441EBCe&#421CB7n &#3E19ADw&#3C18A8h&#3A16A3e&#38159En &#361398c&#38149Do&#3916A2n&#3B17A7s&#3D19ABu&#3F1AB0m&#411CB5i&#431EBAn&#451FBFg")
            ,C.t("&#4922C9e&#4B24CEn&#4D25D3c&#4F27D8h&#5129DDa&#522AE2n&#542CE7t&#562DEAe&#542BE5d &#5028DBg&#4E26D6o&#4C25D1l&#4A23CCd&#4822C8e&#4620C3n &#421DB9a&#411BB4p&#3F1AAFp&#3D18AAl&#3B17A5e&#3915A0s&#37149B.")},
            "heart_opal",500,1.0,0.0,0.0, null,null),
    tome("Arcane Tome",C.getURL("http://textures.minecraft.net/texture/b7218c955e73a956c92bd4aa164e3ee67e23ddcab0e9f9b52468cdf3153735a2"),
            new String[]{ChatColor.GRAY+"slightly increases experience gain.",ChatColor.GRAY+"(does not affect mending)"},
            "tome_base",300,1.0,0.0,0.0, null,null),
    tome_ruby(Ruby.getPrefix()+"Arcane Tome",C.getURL("http://textures.minecraft.net/texture/7cc9d3bfc889bbcd0971ec7083e362a7302c53d1c2315abe1e051da8edc2f760"),
            new String[]{ChatColor.GRAY+"slightly increases experience gain.",ChatColor.GRAY+"(does not affect mending)"
            ,C.t("&#AA0608c&#B00A0Ba&#B50E0Eu&#BB1211s&#C01614e&#C61A17s &#D1221Ce&#D7261Fx&#DC2A22p&#E22E25e&#E73228r&#E93429i&#E43026e&#DE2C23n&#D82820c&#D3241De &#C81B18o&#C21715r&#BD1312b&#B70F0Fs")
            ,C.t("&#AC0709t&#AC0C0Bo &#B11C16d&#B4241Br&#B72D20o&#BA3526p &#BF4630w&#C24E35h&#C5563Be&#C75F40n &#C75F40y&#C5563Bo&#C24E35u &#BC3D2Bp&#BA3526o&#B72D20p &#B11C16a&#AF1411n")
            ,C.t("&#AB090Ae&#AE110Fn&#B01914e&#B32219m&#B62A1Fy&#B93224'&#BB3B29s &#C14B34t&#C45439o&#C65C3Et&#C96443e&#C86142m &#C35137o&#C04932f &#BA3827u&#B83022n&#B5271Dd&#B21F18y&#AF1712i&#AD0E0Dn&#AA0608g")},
            "tome_ruby",100,1.0,0.0,0.0, null,null),
    tome_peridot(Peridot.getPrefix()+"Arcane Tome",C.getURL("http://textures.minecraft.net/texture/a126acca4debea38d1a03d89a580676de185ca3de2cd0dcf846b40aed3e7137b"),
            new String[]{ChatColor.GRAY+"slightly increases experience gain.",ChatColor.GRAY+"(does not affect mending)"
            ,C.t("&#42B315d&#47B71Aa&#4CBB1Fm&#51BF24a&#56C329g&#5CC82Ee&#61CC33d &#6BD43Ci&#70D841t&#75DC46e&#7AE04Bm&#7FE450s &#8AED5Ai&#8DEF5Dn &#83E753y&#7EE34Eo&#79DF49u&#74DB45r")
            ,C.t("&#69D33Bi&#64CE36n&#5FCA31v&#5AC62Ce&#55C227n&#50BE22t&#4BBA1Do&#45B618r&#44B417y &#4EBD20w&#53C125i&#58C52Al&#5DC92Fl &#68D139c&#6DD53Eo&#72D943n&#77DD48s&#7CE24Du&#81E652m&#86EA57e")
            ,C.t("&#8BEE5Cx&#86EA57p &#7CE24Da&#77DD48n&#72D943d &#68D139s&#62CD34l&#5DC92Fo&#58C52Aw&#53C125l&#4EBD20y &#44B417r&#45B618e&#4BBA1Dg&#50BE22e&#55C227n&#5AC62Ce&#5FCA31r&#64CE36a&#69D33Bt&#6ED740e")
            ,C.t("&#79DF49d&#7EE34Eu&#83E753r&#88EB58a&#8DEF5Db&#8AED5Ai&#85E855l&#7FE450i&#7AE04Bt&#75DC46y &#6BD43Co&#66D037v&#61CC33e&#5CC82Er &#51BF24t&#4CBB1Fi&#47B71Am&#42B315e")},
            "tome_peridot",300,1.0,0.0,0.0, null,null),
    tome_tanzanite(Tanzanite.getPrefix()+"Arcane Tome",C.getURL("http://textures.minecraft.net/texture/d997e767e1b05502ede2d9804936b0e6557852c5ded31a39cf4ba3cb384a35c2"),
            new String[]{ChatColor.GRAY+"slightly increases experience gain.",ChatColor.GRAY+"(does not affect mending)"
            ,C.t("#1645C9n&#1A4ACBe&#1E4FCEa&#2255D0r&#275AD2b&#2B5FD4y &#3369D9t&#376FDBe&#3B74DDa&#4079E0m&#447EE2m&#4883E4a&#4C89E6t&#508EE9e&#5493EBs &#5D9DEFw&#5B9CEFi&#5796ECl&#5391EAl")
            ,C.t("&#4B87E6r&#4682E3e&#427CE1c&#3E77DFi&#3A72DDe&#366DDAv&#3268D8e &#295DD4a &#2153CFd&#1D4ECDu&#1948CBp&#1747CAl&#1C4CCCi&#2051CEc&#2456D1a&#285CD3t&#2C61D5i&#3066D7v&#346BDAe")
            ,C.t("&#3D75DEp&#417BE0o&#4580E3r&#4985E5t&#4D8AE7i&#528FE9o&#5695ECn &#5E9FF0o&#5A9AEEf &#528FE9t&#4D8AE7h&#4985E5e &#417BE0e&#3D75DEx&#3970DCp&#346BDAe&#3066D7r&#2C61D5i&#285BD3e&#2456D1n&#2051CEc&#1C4CCCe")
            ,C.t("&#1948CBy&#1D4ECDo&#2153CFu &#295DD4g&#2E62D6e&#3268D8t &#3A72DDw&#3E77DFh&#427CE1e&#4682E3n&#4B87E6e&#4F8CE8v&#5391EAe&#5796ECr &#5D9DEFy&#5898EDo&#5493EBu &#4C88E6p&#4883E4i&#447EE2c&#4079E0k")
            ,C.t("&#376FDBu&#3369D9p &#2B5FD4a&#275AD2n &#1E4FCEo&#1A4ACBr&#1645C9b")},
            "tome_tanzanite",300,1.0,0.0,0.0, null,null),
    tome_opal(Opal.getPrefix()+"Arcane Tome",C.getURL("http://textures.minecraft.net/texture/31adb074d9c4206521742ceb99fb6858b4762876e35d4d698e4e04dca90a3dcf"),
            new String[]{ChatColor.GRAY+"slightly increases experience gain.",ChatColor.GRAY+"(does not affect mending)"
            ,C.t("&#351296P&#37139Ai&#38159Ec&#3A16A2k&#3B17A6i&#3D19ABn&#3F1AAFg &#421CB7u&#431EBBp &#4720C3e&#4822C7x&#4A23CBp&#4B24D0e&#4D26D4r&#4F27D8i&#5028DCe&#522AE0n&#532BE4c&#552CE8e &#542BE6o&#522AE1r&#5129DDb&#4F27D9s")
            ,C.t("&#4C25D1w&#4A23CDi&#4922C9l&#4721C5l &#441EBCo&#421DB8c&#411CB4c&#3F1AB0a&#3E19ACs&#3C18A8i&#3A16A4o&#3915A0n&#37149Ba&#361297l&#361399l&#38149Dy &#3B17A5g&#3C18A9r&#3E19ADa&#401BB1n&#411CB6t &#441FBEy&#4620C2o&#4821C6u")
            ,C.t("&#4B24CEa &#4E26D6r&#5028DBa&#5129DFn&#532AE3d&#542CE7o&#562DEBm &#532AE3p&#5129DFo&#5028DBs&#4E26D6i&#4C25D2t&#4B24CEi&#4923CAv&#4821C6e &#441FBEp&#431DBAo&#411CB6t&#401BB1i&#3E19ADo&#3C18A9n &#3915A1e&#38149Df&#361399f&#361297e&#37149Bc&#3915A0t")
            ,C.t("&#3C18A8t&#3E19ACh&#3F1AB0a&#411CB4t &#441EBCw&#451FC0i&#4721C5l&#4922C9l &#4C25D1n&#4D26D5o&#4F27D9t &#522AE1o&#542BE6v&#552DEAe&#552CE8r&#532BE4r&#522AE0i&#5028DCd&#4F27D8e &#4B24D0e&#4A23CBx&#4822C7i&#4720C3s&#451FBFt&#431EBBi&#421CB7n&#401BB3g")
            ,C.t("&#3D19ABb&#3B17A6u&#3A16A2f&#38159Ef&#37139As&#351296.")},
            "tome_opal",300,1.0,0.0,0.0, null,null);
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
