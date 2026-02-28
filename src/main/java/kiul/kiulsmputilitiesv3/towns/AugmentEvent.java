package kiul.kiulsmputilitiesv3.towns;

import io.papermc.paper.event.player.AsyncChatCommandDecorateEvent;
import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.towns.augments.AugmentEnum;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class AugmentEvent {
    private Town town;
    private Item inputItem;
    private ItemStack outputItemStack;
    private AugmentEnum augment;
    private double eventMaxHealth;
    private double eventHealth;
    private boolean eventActivated;
    private long eventTimerInactive;
    private long eventTimerActive;
    private ArmorStand timerStand;
    private BukkitTask runnable;


    public AugmentEvent(Town town, Item itemToAugment,AugmentEnum augment) {
        this.town = town;
        this.augment = augment;
        this.inputItem = itemToAugment;
        this.eventMaxHealth = town.getTownMaxHealth()*0.2;
        this.eventHealth = eventMaxHealth;
        this.eventActivated = false;
        this.eventTimerInactive = System.currentTimeMillis()+1000*60*1; // 1000*60*15
        this.eventTimerActive = System.currentTimeMillis()+1000*60*30;
        this.runnable = null;
    }

    public void start() {
        Bukkit.broadcastMessage(C.PASTEL_PINK+ ChatColor.BOLD+"AUGMENTING! "+ ChatColor.RESET + ChatColor.WHITE + town.getOwningTeam().getPrefix() + "is augmenting " + inputItem.getItemStack().getType().name()
                + " with " + augment.getTitle() + ChatColor.WHITE + " at x" + town.getTownCenter().x() + " y" + town.getTownCenter().y() + " z" + town.getTownCenter().z() + ". Attack their town core to steal the item!");
        inputItem.setCanPlayerPickup(false);
        inputItem.setUnlimitedLifetime(true);
        inputItem.setNoPhysics(true);
        inputItem.setInvulnerable(true);
        inputItem.teleport(town.getTownCenter().clone().add(0.5, 1.1, 0.5));
        // set item floating in the air above the tc
        // set timer above tc
        town.setAugmentEvent(this);
        town.setAugmenting(true);
        int[] timestamp = C.splitTimestamp(eventTimerInactive);
        this.timerStand = (ArmorStand) town.getTownCenter().getWorld().spawnEntity(town.getTownCenter().clone().add(0.5, 1.4, 0.5), EntityType.ARMOR_STAND);
        timerStand.setPersistent(true);
        timerStand.setCustomNameVisible(true);
        timerStand.setCustomName(C.YELLOW+timestamp[1]+":"+timestamp[2]);
        timerStand.setInvisible(true);
        timerStand.setGravity(false);
        timerStand.setMarker(true);
        town.getTownNameStand().teleport(town.getTownNameStand().getLocation().clone().add(0,0.9,0));
        town.getTownStatusStand().teleport(town.getTownNameStand().getLocation().clone().add(0,-0.3,0));
        town.getTownChargeStand().setCustomNameVisible(false);


        this.runnable = new BukkitRunnable() {
            double angle = 0; // keep track of rotation
            @Override
            public void run() {
                int[] updatedTimestamp = C.splitTimestamp(eventTimerInactive);
                if (eventActivated) {
                    updatedTimestamp = C.splitTimestamp(eventTimerActive);
                    if (System.currentTimeMillis() >= eventTimerActive) {
                        end();
                        cancel();
                        return;
                    }
                } else {
                    if (System.currentTimeMillis() >= eventTimerInactive) {
                        end();
                        cancel();
                        return;
                    }
                }


                timerStand.setCustomName(C.YELLOW+String.format("%02d : %02d",updatedTimestamp[1],updatedTimestamp[2]));
                // decrement timer
                // check if eventActive and update to other timer if true
                // particle effects !?

                if (inputItem.getLocation().getWorld() == null) {
                    cancel();
                    return;
                }

                // Optional: rotate the circle slowly
                angle += Math.toRadians(5);

                for (int i = 0; i < 5; i++) {
                    double theta = (2 * Math.PI / 20) * i + angle;
                    double x = Math.cos(theta) * 1;
                    double z = Math.sin(theta) * 1;

                    Location particleLoc = inputItem.getLocation().clone().add(x, 1.5, z);
                    inputItem.getLocation().getWorld().spawnParticle(
                            Particle.ENCHANT,
                            particleLoc,
                            1, // count
                            0, 0, 0, // offset
                            0 // speed
                    );
                }
            }
        }.runTaskTimer(C.plugin,0,1);
    }

    public void damageEvent(double damage) {
        eventHealth -= damage;
        if (eventHealth <= 0) {
            end();
        }
    }

    public void abort() { // in case of shutdown or whatever
        town.setAugmenting(false);
        town.setAugmentEvent(null);
        town.getTownNameStand().teleport(town.getTownNameStand().getLocation().clone().add(0,-0.9,0));
        town.getTownStatusStand().teleport(town.getTownNameStand().getLocation().clone().add(0,-0.3,0));
        timerStand.remove();
        town.setAugmenting(false);
        inputItem.setCanPlayerPickup(true);
        inputItem.setNoPhysics(false);
        // drop input item and return town holograms to normal
    }

    public void end() {
        if (!runnable.isCancelled()) runnable.cancel();
        town.setAugmenting(false);
        town.setAugmentEvent(null);
        timerStand.remove();
        town.getTownNameStand().setCustomNameVisible(false);
        town.getTownStatusStand().setCustomNameVisible(false);
        // flashy effects

        inputItem.setCanPlayerPickup(true);
        inputItem.setNoPhysics(false);
        inputItem.setItemStack(AugmentEnum.augmentItem(inputItem.getItemStack(),town,augment));
        inputItem.setGlowing(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (inputItem == null || inputItem.isDead()) {
                    town.getTownNameStand().setCustomNameVisible(true);
                    town.getTownStatusStand().setCustomNameVisible(true);
                    town.getTownChargeStand().setCustomNameVisible(true);
                    town.getTownNameStand().teleport(town.getTownNameStand().getLocation().clone().add(0,-0.9,0));
                    town.getTownStatusStand().teleport(town.getTownNameStand().getLocation().clone().add(0,-0.3,0));
                    cancel();
                }
            }
        }.runTaskTimer(C.plugin,0,20);
    }


    public BukkitTask getRunnable() {
        return runnable;
    }

    public double getEventHealth() {
        return eventHealth;
    }

    public double getEventMaxHealth() {
        return eventMaxHealth;
    }

    public Item getInputItem() {
        return inputItem;
    }

    public ItemStack getOutputItemStack() {
        return outputItemStack;
    }

    public long getEventTimerActive() {
        return eventTimerActive;
    }

    public long getEventTimerInactive() {
        return eventTimerInactive;
    }

    public Town getTown() {
        return town;
    }
}
