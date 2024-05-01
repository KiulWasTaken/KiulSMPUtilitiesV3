package kiul.kiulsmputilitiesv3.crates;

import kiul.kiulsmputilitiesv3.C;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static kiul.kiulsmputilitiesv3.crates.CrateMethods.*;

public class SmallCrates {


    public static void createNewCrate (String type, int minutesUntilSpawn, World world) {
//        if (Bukkit.getOnlinePlayers().size() < 10) {
//            runAutoCrates();
//            return;
//        }
        long crateSpawnTime = (System.currentTimeMillis() + (long) minutesUntilSpawn *60*1000);
        long remainingTime = (crateSpawnTime - System.currentTimeMillis());
        ChatColor color;
        if (type.equalsIgnoreCase("nether")) {
            color = ChatColor.RED;
        } else if (type.equalsIgnoreCase("end")) {
            color = ChatColor.LIGHT_PURPLE;
        } else {
            color = ChatColor.GREEN;
        }
        Location crateSpawnLocation = returnCrateLocation(world);

        int x = (int) crateSpawnLocation.getX();
        int y = (int) crateSpawnLocation.getY();
        int z = (int) crateSpawnLocation.getZ();
        crateSpawnLocation.getChunk().setForceLoaded(true);
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(C.eventPrefix + color + type + ChatColor.WHITE + " drop will arrive at " + x + " " + y + " " + z + " in " + ChatColor.RED + (((crateSpawnTime - System.currentTimeMillis()) / 1000) / 60) + " minutes.");
        Bukkit.broadcastMessage("");
        new BukkitRunnable() {
            ArmorStand crate = (ArmorStand) world.spawnEntity(crateSpawnLocation.add(0.5,2,0.5), EntityType.ARMOR_STAND);
            @Override
            public void run() {
                if (crateSpawnLocation.getChunk().isLoaded()) {
                    crate.setInvulnerable(true);
                    crate.setVisible(false);
                    crate.setGravity(false);
                    crate.setCustomNameVisible(true);
                    crate.setPersistent(true);
                    crate.setMarker(true);
                    crate.setCustomName(String.format("%02d : %02d",
                            TimeUnit.MILLISECONDS.toMinutes(crateSpawnTime - System.currentTimeMillis()),
                            TimeUnit.MILLISECONDS.toSeconds(crateSpawnTime - System.currentTimeMillis()) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(crateSpawnTime - System.currentTimeMillis()))
                    ));
                    if (System.currentTimeMillis() >= crateSpawnTime) {
                        crate.remove();
                        cancel();
                    }
                }
            }
        }.runTaskTimer(C.plugin, 0L, 20L);
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (System.currentTimeMillis() > crateSpawnTime) {
                    crateSpawn(type, 1, world, x, z);
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(C.eventPrefix + color + type + ChatColor.WHITE +  " drop has arrived at " + x + " " + y + " " + z);
                    Bukkit.broadcastMessage("");
                    cancel();
                    runAutoCrates();
                    return;
                }
                tick++;
                if (tick >= (((remainingTime / 4) / 1000) / 60)) {
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(C.eventPrefix + color + type + ChatColor.WHITE + " drop will arrive at " + x + " " + y + " " + z + " in " + ChatColor.RED + (((crateSpawnTime - System.currentTimeMillis()) / 1000) / 60) + " minutes.");
                    Bukkit.broadcastMessage("");
                    tick = 0;
                }
            }
        }.runTaskTimer(C.plugin, 0L, 1200L);
    }

    public static void crateSpawn (String type, int unlockMinutes, World world,int x, int z) {
        int y = world.getHighestBlockYAt(x,z);
        Random random = new Random();
        Location crateSpawnLocation = new Location(world,x,y,z);
        ArmorStand crate = (ArmorStand) world.spawnEntity(crateSpawnLocation.add(0.5,-0.4,0.5), EntityType.ARMOR_STAND);
        crate.setInvulnerable(true);
        crate.setVisible(false);
        crate.setGravity(false);
        crate.setCustomNameVisible(true);
        crate.setPersistent(true);
        crate.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        standMap.put(crateSpawnLocation.getChunk(),crate);
        long unlockTime = System.currentTimeMillis()+(unlockMinutes*60*1000);
        crateUnlockTime.put(crate,unlockTime);
        ChatColor color;
        switch (type) {
            case "NETHER":
                crate.setHelmet(new ItemStack(Material.SCULK));
                color = ChatColor.RED;
                break;
            case "END":
                crate.setHelmet(new ItemStack(Material.GOLD_BLOCK));
                color = ChatColor.LIGHT_PURPLE;
                break;
            case "OVERWORLD":
                crate.setHelmet(new ItemStack(Material.BARREL));
                color = ChatColor.GREEN;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        crate.setRotation(random.nextFloat(0,180),0);
        crate.setCustomNameVisible(true);
        locked.add(crate);
        world.playSound(crateSpawnLocation,Sound.ENTITY_GENERIC_EXPLODE,50f,0.8f);


        new BukkitRunnable() {
            @Override
            public void run() {
                if (!locked.contains(crate)) {
                    long unlockTime = CrateMethods.crateUnlockTime.get(crate);
                    unlocking.add(crate);
                    crate.setCustomName(color + String.format("%02d : %02d",
                            TimeUnit.MILLISECONDS.toMinutes(unlockTime - System.currentTimeMillis()),
                            TimeUnit.MILLISECONDS.toSeconds(unlockTime - System.currentTimeMillis()) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(unlockTime - System.currentTimeMillis()))
                    ));
                    if (System.currentTimeMillis() >= unlockTime) {
                        unlocking.remove(crate);
                        crate.setCustomName(color + "CLICK TO OPEN");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (CrateMethods.crateInventoryMap.get(crate).isEmpty()) {
                                    world.playSound(crate.getLocation(), Sound.BLOCK_BELL_RESONATE, 10f, 1f);
                                    crate.remove();
                                    CrateMethods.crateInventoryMap.get(crate).clear();

                                    HashSet<String> uniqueSet = new HashSet<>(CrateMethods.playersWhoGotLoot);
                                    List<String> uniqueList = new ArrayList<>(uniqueSet);
                                    Bukkit.broadcastMessage("");
                                    Bukkit.broadcastMessage(C.eventPrefix + ChatColor.WHITE + "Players " + ChatColor.RED + uniqueList.toString() + ChatColor.WHITE + " Looted the crate!");
                                    Bukkit.broadcastMessage("");
                                    for (String displayName : uniqueList) {
                                        Player p = Bukkit.getPlayer(displayName);
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1200, 0, false, false));
                                    }

                                    CrateMethods.playersWhoGotLoot.clear();
                                    cancel();
                                }

                            }
                        }.runTaskTimer(C.plugin, 0L, 5L);
                        cancel();
                    }
                } else {
                    crate.setCustomName(color + "\uD83D\uDD12");
                }
            }
        }.runTaskTimer(C.plugin, 0L, 20L);
    }

    public static void runAutoCrates() {
        if (C.plugin.getConfig().getBoolean("supplydrops.isenabled")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(C.plugin, new Runnable() {
                @Override
                public void run() {

                    Random random = new Random();
                    String type = null;
                    int randomType = random.nextInt(1, 3);

                    switch (randomType) {
                        case 1:
                            type = "NETHER";
                            break;
                        case 2:
                            type = "END";
                    }

                    createNewCrate(type, 60, Bukkit.getWorld("world"));

                }
            }, getRandomTime());
        }
    }

    public static void populateCrate (CrateTypeEnum crateType, ArmorStand crate, Player p) {
        Random random = new Random();
        ArrayList<ItemStack> crateInventory = new ArrayList<>();
        //crateType.getRolls()
        for (int i = 0; i < crateType.getLootTableRolls(); i++) {
            ItemStack item = CrateMethods.getLootTableItem(crateType.getLootTable(), crateType);
            crateInventory.add(item);
            i = i + CrateMethods.getRollConsumption(item, crateType);
            LootTableEnum lootTable = CrateMethods.getLootTable(item, crateType.getIdentifier());
            if (lootTable != null) {
                int amount = random.nextInt(lootTable.getMinStackSize(), lootTable.getMaxStackSize() + 1);


            item.setAmount(amount);
            }
            if (item.getMaxStackSize() == 1) {
                item.setAmount(1);
            }
        }


        // Shuffling the contents
        Collections.shuffle(crateInventory);
        Inventory trueCrateInventory = Bukkit.createInventory(null,27,"Crate");
        for (ItemStack itemStack : crateInventory) {
            int randomSlot;

            do {
                randomSlot = random.nextInt(trueCrateInventory.getSize());

                if (trueCrateInventory.getItem(randomSlot) == null) {
                    trueCrateInventory.setItem(randomSlot, itemStack);
                    break;
                }
            } while (trueCrateInventory.getItem(randomSlot) != itemStack); // avoid infinite loop
        }

        if (p != null) {
            p.openInventory(trueCrateInventory);
            return;
        }
        Bukkit.broadcastMessage(crate.toString());
        Bukkit.broadcastMessage(crateInventory.toString());
        Bukkit.broadcastMessage(trueCrateInventory.toString());
        crateInventoryMap.put(crate,trueCrateInventory);

    }
}
