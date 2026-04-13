package kiul.kiulsmputilitiesv3.towns.bounty;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.config.BountyData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.ChatPaginator;

import java.util.ArrayList;
import java.util.List;

public class KillBountiedListener implements Listener {

    public ItemStack createBountySkull(Player p) {
        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta playerSkullMeta = (SkullMeta) playerSkull.getItemMeta();
        playerSkullMeta.setOwningPlayer(p);
        playerSkullMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin,"emerald_bounty"), PersistentDataType.INTEGER, (int) BountyData.get().getDouble(p.getUniqueId() + ".emeralds"));
        playerSkullMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin,"gold_bounty"), PersistentDataType.INTEGER, (int) BountyData.get().getDouble(p.getUniqueId() + ".gold"));
        playerSkullMeta.getPersistentDataContainer().set(new NamespacedKey(C.plugin,"scrap_bounty"), PersistentDataType.INTEGER, (int) BountyData.get().getDouble(p.getUniqueId() + ".scrap"));
        playerSkullMeta.setDisplayName(ChatColor.GOLD+"Bounty Skull " + ChatColor.DARK_GRAY+"("+p.getDisplayName()+")");

        int emeraldBlocks = (int) (BountyData.get().getDouble(p.getUniqueId() + ".emeralds")/9)-1;
        int emeralds =  (int) (BountyData.get().getDouble(p.getUniqueId() + ".emeralds")%9)-1;
        int goldBlocks = (int) (BountyData.get().getDouble(p.getUniqueId() + ".gold")/9)-1;
        int gold =  (int) (BountyData.get().getDouble(p.getUniqueId() + ".gold")%9)-1;
        int scrap = (int) BountyData.get().getDouble(p.getUniqueId() + ".scrap");
        List<String> lore = new ArrayList<>();
        lore.add(C.YELLOW+"Return this to your town core to retrieve the bounty:");
        lore.add("");
        lore.add(C.GREEN+emeraldBlocks + " Emerald blocks and " +emeralds + " emeralds");
        lore.add(C.GOLD+goldBlocks + " Gold blocks and "+gold+" ingots");
        lore.add((C.RED)+scrap + " Netherite scrap");
        playerSkullMeta.setLore(lore);
        playerSkull.setItemMeta(playerSkullMeta);
        BountyData.get().set(p.getUniqueId() + ".emeralds",0);
        BountyData.get().set(p.getUniqueId() + ".gold",0);
        BountyData.get().set(p.getUniqueId() + ".scrap",0);
        BountyData.save();
        return playerSkull;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void PlayerKillPlayer (PlayerDeathEvent e) {
        Player p = e.getPlayer();
        if (C.fightManager.playerIsInFight(p)) {
            ItemStack skull = createBountySkull(p);
            p.getWorld().dropItemNaturally(p.getLocation(),skull);

        }
    }
}
