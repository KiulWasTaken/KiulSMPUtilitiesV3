package kiul.kiulsmputilitiesv3.potions;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class PotionListeners implements Listener {
    @EventHandler
    public void customPotionItemStackClick(InventoryClickEvent event) {

        Inventory inv = event.getClickedInventory();
        InventoryView pInv = event.getWhoClicked().getOpenInventory();
        ClickType type = event.getClick();
        
        if (event.getClickedInventory() == pInv.getBottomInventory() && pInv.getTopInventory() instanceof BrewerInventory) {
            if (type == ClickType.SHIFT_LEFT || type == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
                if (pInv.getTopInventory().firstEmpty() == -1) {
                    return;
                }
                pInv.getTopInventory().setItem(pInv.getTopInventory().firstEmpty(), event.getCurrentItem());
                event.setCurrentItem(event.getCursor());
                return;
            }
        }

        if (inv == null || inv.getType() != InventoryType.BREWING) {
            return;
        }
        if (C.brewingTasks.containsKey(inv)) {
            C.brewingTasks.get(inv).cancel();
        }
        if (!(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)) {
            return;
        }

        ItemStack is = event.getCurrentItem(); // GETS ITEMSTACK THAT IS BEING CLICKED
        ItemStack is2 = event.getCursor(); // GETS CURRENT ITEMSTACK HELD ON MOUSE

        if (event.getClick() == ClickType.RIGHT && is.isSimilar(is2)) {
            return;
        }

        event.setCancelled(true);

        Player p = (Player)(event.getView().getPlayer());

        boolean compare = is.isSimilar(is2);


        int firstAmount = is.getAmount();
        int secondAmount = is2.getAmount();

        int stack = is.getMaxStackSize();
        int half = firstAmount / 2;

        int clickedSlot = event.getSlot();



        if (type == ClickType.LEFT) {


            if (is == null || (is != null && is.getType() == Material.AIR)) {

                p.setItemOnCursor(is);
                inv.setItem(clickedSlot, is2);

            } else if (compare) {

                int used = stack - firstAmount;
                if (secondAmount <= used) {

                    is.setAmount(firstAmount + secondAmount);
                    p.setItemOnCursor(null);

                } else {

                    is2.setAmount(secondAmount - used);
                    is.setAmount(firstAmount + used);
                    p.setItemOnCursor(is2);

                }

            } else if (!compare) {

                inv.setItem(clickedSlot, is2);
                p.setItemOnCursor(is);

            }

        } else if (type == ClickType.RIGHT) {

            if (is == null || (is != null && is.getType() == Material.AIR)) {

                p.setItemOnCursor(is);
                inv.setItem(clickedSlot, is2);

            } else if ((is != null && is.getType() != Material.AIR) &&
                    (is2 == null || (is2 != null && is2.getType() == Material.AIR))) {

                ItemStack isClone = is.clone();
                isClone.setAmount(is.getAmount() % 2 == 0 ? firstAmount - half : firstAmount - half - 1);
                p.setItemOnCursor(isClone);

                is.setAmount(firstAmount - half);

            } else if (compare) {

                if ((firstAmount + 1) <= stack) {

                    is2.setAmount(secondAmount - 1);
                    is.setAmount(firstAmount + 1);

                }

            } else if (!compare) {

                inv.setItem(clickedSlot, is2);
                p.setItemOnCursor(is);
            }

        }

        if (((BrewerInventory) inv).getIngredient() == null) {
            return;
        }

        BrewingRecipe recipe = BrewingRecipe.getRecipe((BrewerInventory) inv);

        if (recipe == null) {
            return;
        }

        recipe.startBrewing((BrewerInventory) inv);

    }

    @EventHandler
    public void stopOnBreak (BlockBreakEvent e) {
        if (e.getBlock() instanceof BlockInventoryHolder bi) {
            if (bi instanceof BrewerInventory && C.brewingTasks.keySet().contains(bi)) {
                C.brewingTasks.get(bi).cancel();
                C.brewingTasks.remove(bi);
;            }
        }
    }
    ArrayList<PotionEffectType> debuffTypes = new ArrayList<>() {{
       add(PotionEffectType.WEAKNESS);
       add(PotionEffectType.POISON);
       add(PotionEffectType.WITHER);
       add(PotionEffectType.HUNGER);
       add(PotionEffectType.SLOWNESS);
       add(PotionEffectType.MINING_FATIGUE);
       add(PotionEffectType.SLOW_FALLING);
       add(PotionEffectType.NAUSEA);
       add(PotionEffectType.DARKNESS);
       add(PotionEffectType.BAD_OMEN);
    }};

    @EventHandler
    public void instantPurity (EntityPotionEffectEvent e) {
        if (e.getNewEffect() != null) {
            if (e.getEntity() instanceof Player p) {
                if (p.hasPotionEffect(PotionEffectType.LUCK) && debuffTypes.contains(e.getNewEffect().getType())) {
                    e.setCancelled(true);
                    return;
                }

                if (e.getNewEffect().getType().equals(PotionEffectType.LUCK) && e.getNewEffect().getAmplifier() > 0) {
                    for (PotionEffect potionEffects : p.getActivePotionEffects()) {
                        if (debuffTypes.contains(potionEffects.getType())) {
                            p.removePotionEffect(potionEffects.getType());
                        }
                    }
                }
            }
        }
    }
}
