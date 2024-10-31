package kiul.kiulsmputilitiesv3.advancements;

import org.bukkit.ChatColor;

public enum AdvancementEnum {
    /**
     * Symbols
     */
    CRAFT_ACCESSORY("[Makeshift]","Makeshift\nCraft or reforge an accessory","homemade",0,false),
    ACTIVATE_ACCESSORY("[Master of One]","Master of One\nActivate an accessory","activate",0,false),
    CRAFT_PERFECT_ACCESSORY("[One Of A Kind]","One Of A Kind\nCraft or reforge an accessory with near-perfect stats","perfect",500,true);


    private String displayText;
    private String hoverText;
    private String identifier;
    private boolean rare;
    private int rewardExp;


    AdvancementEnum(String displayText, String hoverText, String identifier, int rewardExp, boolean rare ) {
        this.displayText = displayText;
        this.hoverText = hoverText;
        this.identifier = identifier;
        this.rare = rare;
        this.rewardExp = rewardExp;
    }

    public String getDisplayText() {return displayText;}
    public String getHoverText() {return hoverText;}
    public String getIdentifier() {return identifier;}
    public int getRewardExp() {return rewardExp;}
    public boolean isRare() {return rare;}




}
