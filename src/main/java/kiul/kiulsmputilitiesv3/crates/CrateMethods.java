package kiul.kiulsmputilitiesv3.crates;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.accessories.IngredientItemEnum;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.TimeUnit;



public class CrateMethods {

    public static HashMap<ArmorStand, Inventory> crateInventoryMap = new HashMap<>();
    public static HashMap<ArmorStand,Long> crateUnlockTime = new HashMap<>();

    public static ArrayList<String> playersWhoGotLoot = new ArrayList<>();
    public static HashMap<Chunk,ArmorStand> standMap = new HashMap<>();

    public static ArrayList<ArmorStand> locked = new ArrayList<>();
    public static ArrayList<ArmorStand> unlocking = new ArrayList<>();

    public static HashMap<Player,ArrayList<Item>> privateItemStacks = new HashMap<>();


    public static Location returnCrateLocation (World world) {
        Random random = new Random();
        int x = random.nextInt((int)-(world.getWorldBorder().getSize()/2),(int)(world.getWorldBorder().getSize()/2));
        int z = random.nextInt((int)-(world.getWorldBorder().getSize()/2),(int)(world.getWorldBorder().getSize()/2));
        int y = world.getHighestBlockYAt(x,z);
        return new Location(world,x,y,z);
    }

    public static ItemStack randomEnchantedGear (ItemStack itemStack) {
        ArrayList<Enchantment> armourEnchantments = new ArrayList<>() {{
            add(Enchantment.PROTECTION);
            add(Enchantment.UNBREAKING);
            add(Enchantment.MENDING);
        }};

        ArrayList<Enchantment> maceBookEnchantments = new ArrayList<>() {{
            add(Enchantment.WIND_BURST);
            add(Enchantment.DENSITY);
            add(Enchantment.BREACH);
        }};
        ArrayList<Enchantment> toolEnchants = new ArrayList<>() {{
            add(Enchantment.EFFICIENCY);
            add(Enchantment.SILK_TOUCH);
            add(Enchantment.FORTUNE);
            add(Enchantment.UNBREAKING);
            add(Enchantment.MENDING);
        }};

        ArrayList<ItemStack> netheriteBlock = new ArrayList<>() {{
            add(new ItemStack(Material.NETHERITE_HELMET));
            add(new ItemStack(Material.NETHERITE_CHESTPLATE));
            add(new ItemStack(Material.NETHERITE_LEGGINGS));
            add(new ItemStack(Material.NETHERITE_BOOTS));
        }};

        ArrayList<ItemStack> ancientDebris = new ArrayList<>() {{
            add(new ItemStack(Material.NETHERITE_PICKAXE));
            add(new ItemStack(Material.NETHERITE_AXE));
            add(new ItemStack(Material.NETHERITE_SHOVEL));
        }};
        ArrayList<ItemStack> diamondBlock = new ArrayList<>() {{
            add(new ItemStack(Material.DIAMOND_HELMET));
            add(new ItemStack(Material.DIAMOND_CHESTPLATE));
            add(new ItemStack(Material.DIAMOND_LEGGINGS));
            add(new ItemStack(Material.DIAMOND_BOOTS));
        }};
        ArrayList<ItemStack> diamondOre = new ArrayList<>() {{
            add(new ItemStack(Material.DIAMOND_PICKAXE));
            add(new ItemStack(Material.DIAMOND_AXE));
            add(new ItemStack(Material.DIAMOND_SHOVEL));
        }};
        ItemStack gearItem = itemStack;

        switch (itemStack.getType()) {
            case PLAYER_HEAD:

                double[] chance = {0.25, 0.5, 0.75, 1};
                int event = Arrays.binarySearch(chance, Math.random());
                if (event < 0) event = -event - 1;
                switch (event) {
                    case 0:
                        gearItem = IngredientItemEnum.Opal.getIngredient();
                        break;
                    case 1:
                        gearItem = IngredientItemEnum.Ruby.getIngredient();
                        break;
                    case 2:
                        gearItem = IngredientItemEnum.Tanzanite.getIngredient();
                        break;
                    case 3:
                        gearItem = IngredientItemEnum.Peridot.getIngredient();
                        break;
                    default:
                        gearItem = IngredientItemEnum.Ruby.getIngredient();
                }
                break;
            case ENCHANTED_BOOK:
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
                int enchantmentNum = (int)(Math.random()*maceBookEnchantments.size());
                meta.addStoredEnchant(maceBookEnchantments.get(enchantmentNum),(int)(Math.random()*maceBookEnchantments.get(enchantmentNum).getMaxLevel()+0.5),false);
                if (Math.random() > 0.5) {
                    meta.addStoredEnchant(armourEnchantments.get(1),(int)(Math.random()*3.5),false);
                }
                if (!meta.hasStoredEnchants()) {
                    meta.addStoredEnchant(Enchantment.MENDING,1,false);
                }
                itemStack.setItemMeta(meta);
                break;
            case NETHERITE_BLOCK:
                gearItem = netheriteBlock.get((int)(Math.random()*netheriteBlock.size()));
                gearItem.addEnchantment(armourEnchantments.get(0),4);
                gearItem.addEnchantment(armourEnchantments.get(1),3);
                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(armourEnchantments.get(2),1);
                }
                break;
            case DIAMOND_BLOCK:
                gearItem = diamondBlock.get((int)(Math.random()*diamondBlock.size()));
                gearItem.addEnchantment(armourEnchantments.get(0),4);
                gearItem.addEnchantment(armourEnchantments.get(1),3);
                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(armourEnchantments.get(2),1);
                }
                break;
            case ANCIENT_DEBRIS:
                gearItem = ancientDebris.get((int)(Math.random()*ancientDebris.size()));
                gearItem.addEnchantment(toolEnchants.get(0),4+(int)(Math.random()+0.5));

                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(toolEnchants.get(1),1);
                } else {
                    gearItem.addEnchantment(toolEnchants.get(2),3);
                }
                gearItem.addEnchantment(toolEnchants.get(3),3);
                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(toolEnchants.get(4),1);
                }
                break;
            case DIAMOND_ORE:
                gearItem = diamondOre.get((int)(Math.random()*diamondOre.size()));
                gearItem.addEnchantment(toolEnchants.get(0),4+(int)(Math.random()+0.5));

                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(toolEnchants.get(1),1);
                } else {
                    gearItem.addEnchantment(toolEnchants.get(2),3);
                }
                gearItem.addEnchantment(toolEnchants.get(3),3);
                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(toolEnchants.get(4),1);
                }



                break;
        }
        gearItem.setAmount(1);
        return gearItem;
    }
    public static ItemStack getLootTableItem (ArrayList<ItemStack> crateLootTable,CrateTypeEnum crateType) {
//        ArrayList<ItemStack> lootTable = (ArrayList<ItemStack>) crateLootTable.clone();

            ArrayList<LootTableEnum> lootTableValues = new ArrayList<>();
            for (LootTableEnum lootTable : LootTableEnum.values()) {
                if (lootTable.getCrateType().equalsIgnoreCase(crateType.getIdentifier())) {
                    lootTableValues.add(lootTable);
                }
            }
            double totalWeight = 0.0;
            for (LootTableEnum i : lootTableValues) {
                totalWeight += i.getWeight();
            }


            int idx = 0;
            for (double r = Math.random() * totalWeight; idx < lootTableValues.size() - 1; ++idx) {
                r -= lootTableValues.get(idx).getWeight();
                if (r <= 0.0) break;
            }
            LootTableEnum item = lootTableValues.get(idx);
            ItemStack itemStack = new ItemStack(item.getMaterial());
            itemStack = randomEnchantedGear(itemStack);
             return itemStack;
    }

    public static LootTableEnum getLootTable (ItemStack itemStack,String identifier) {
        for (LootTableEnum lootTableEnum : LootTableEnum.values()) {
            if (lootTableEnum.getMaterial() == itemStack.getType()) {
                if (lootTableEnum.getCrateType().equalsIgnoreCase(identifier)) {
                    return lootTableEnum;
                }
            }
        }
        return null;}


    public static CrateTypeEnum getCrate(String identifier) {
        for (CrateTypeEnum crateTypeEnum : CrateTypeEnum.values()) {
            if (crateTypeEnum.getIdentifier().equalsIgnoreCase(identifier)) {
                return crateTypeEnum;
            }
        }
        return CrateTypeEnum.Gold;}

    public static int getRollConsumption(ItemStack item,CrateTypeEnum crateType) {
        for (LootTableEnum lootTableEnum : LootTableEnum.values()) {
            if (lootTableEnum.getMaterial() == item.getType()) {
                if (lootTableEnum.getCrateType().equalsIgnoreCase(crateType.getIdentifier())) {
                    return lootTableEnum.getRollConsumption()-1;
                }
            }
        }
        return 0;}






    public static void createCrate(World world, String type,boolean debug) {


        CrateTypeEnum crateType = getCrate(type);

        Location crateSpawnLocation = returnCrateLocation(world);
        Bukkit.broadcastMessage(crateSpawnLocation.toString());
        long crateSpawnTime = System.currentTimeMillis()+crateType.getSpawnTime();
        if (debug) {
            crateSpawnTime = System.currentTimeMillis()+1000*60;
        }

        int x = (int) crateSpawnLocation.getX();
        int y = (int) crateSpawnLocation.getY();
        int z = (int) crateSpawnLocation.getZ();
        crateSpawnLocation.getChunk().setForceLoaded(true);
        int[] timestamps = C.splitTimestamp(crateSpawnTime);
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(C.eventPrefix + crateType.getDisplayName() + ChatColor.WHITE + " container will arrive at " + x + " " + y + " " + z + " in " + ChatColor.RED + String.format("%02d:%02d:%02d",timestamps[0],timestamps[1],timestamps[2]));
        Bukkit.broadcastMessage("");
        long spawnTime = crateSpawnTime;
        new BukkitRunnable() {
            ArmorStand nameplate = (ArmorStand) world.spawnEntity(crateSpawnLocation.add(0.5,2,0.5),EntityType.ARMOR_STAND);
            long timeUntilSpawn = spawnTime-System.currentTimeMillis();
            int tick = 0;
            @Override
            public void run() {

                if (System.currentTimeMillis() < spawnTime) {
                    tick++;
                    int[] timestamps = C.splitTimestamp(spawnTime);
                    nameplate.setInvulnerable(true);
                    nameplate.setVisible(false);
                    nameplate.setGravity(false);
                    nameplate.setCustomNameVisible(true);
                    nameplate.setPersistent(true);
                    nameplate.setMarker(true);
                    if (tick >= 300) {
                        tick = 0;
                        Bukkit.broadcastMessage("");
                        Bukkit.broadcastMessage(C.eventPrefix + crateType.getDisplayName() + ChatColor.WHITE + " container will arrive at " + x + " " + y + " " + z + " in " + ChatColor.RED + String.format("%02d:%02d:%02d",timestamps[0],timestamps[1],timestamps[2]));
                        Bukkit.broadcastMessage("");
                    }
                    if (spawnTime-System.currentTimeMillis() <= 5*1000*60) {
                        if (spawnTime-System.currentTimeMillis() <= 5*1000) {
                            tick = 0;
                            Bukkit.broadcastMessage("");
                            Bukkit.broadcastMessage(C.eventPrefix + crateType.getDisplayName() + ChatColor.WHITE + " container will arrive at " + x + " " + y + " " + z + " in " + ChatColor.RED + String.format("%02d:%02d:%02d", timestamps[0], timestamps[1], timestamps[2]));
                            Bukkit.broadcastMessage("");
                        }
                        if (tick >= 30) {
                            tick = 0;
                            Bukkit.broadcastMessage("");
                            Bukkit.broadcastMessage(C.eventPrefix + crateType.getDisplayName() + ChatColor.WHITE + " container will arrive at " + x + " " + y + " " + z + " in " + ChatColor.RED + String.format("%02d:%02d:%02d", timestamps[0], timestamps[1], timestamps[2]));
                            Bukkit.broadcastMessage("");
                        }
                    }

                    nameplate.setCustomName(String.format("%02d:%02d:%02d", timestamps[0], timestamps[1], timestamps[2]));
                } else {
                    nameplate.remove();
                        Random random = new Random();
                        String color = ChatColor.getLastColors(crateType.getDisplayName());
                        ArmorStand crate = (ArmorStand) world.spawnEntity(crateSpawnLocation.add(0.5, -2.4, 0.5), EntityType.ARMOR_STAND);
                        crate.setInvulnerable(true);
                        crate.setVisible(false);
                        crate.setGravity(false);
                        crate.setCustomNameVisible(true);
                        crate.setPersistent(true);
                        crate.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
                        crate.setHelmet(new ItemStack(crateType.getCrateType()));
                    int particleCount = 48;
                    new BukkitRunnable() {
                        int i = 0;
                        public void run() {
                            if (!crate.isDead()) {
                                if (i > particleCount)
                                    i = 0;
                                double angle = (2 * Math.PI * i) / particleCount;
                                double xOffset = 15 * Math.cos(angle);
                                double zOffset = 15 * Math.sin(angle);
                                Location particleLocation = crateSpawnLocation.clone().add(xOffset, 7, zOffset);
                                Location particleLocation1 = crateSpawnLocation.clone().add(-xOffset, 7, -zOffset);
                                crateSpawnLocation.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, particleLocation, 1, 0.1, 0.1, 0.1, 0);
                                crateSpawnLocation.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, particleLocation1, 1, 0.1, 0.1, 0.1, 0);
                                i++;
                            } else {
                                cancel();
                            }
                        }
                    }.runTaskTimer(C.plugin, 0L, 1L);
                        crate.setRotation(random.nextFloat(0, 180), 0);
                        String phases = "\uD83D\uDD12";
                        crate.setCustomName(color + phases.repeat(crateType.getUnlockPhases()));
                        locked.add(crate);
                        if (crateType.getPointsPerPhase() == 0) {

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!locked.contains(crate)) {
                                        long unlockTime = crateUnlockTime.get(crate);
                                        unlocking.add(crate);
                                        crate.setCustomName(color + String.format("%02d : %02d",
                                                TimeUnit.MILLISECONDS.toMinutes(unlockTime - System.currentTimeMillis()),
                                                TimeUnit.MILLISECONDS.toSeconds(unlockTime- System.currentTimeMillis()) -
                                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(unlockTime - System.currentTimeMillis()))
                                        ));

                                        if (System.currentTimeMillis() >= unlockTime) {
                                            Bukkit.broadcastMessage(crate.toString());
                                            populateCrate(crateType,crate,null);
                                            unlocking.remove(crate);
                                            crate.setCustomName(color + "CLICK TO OPEN");
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    if (CrateMethods.crateInventoryMap.get(crate).isEmpty()) {
                                                        world.playSound(crate.getLocation(), Sound.BLOCK_BELL_RESONATE, 10f, 1f);
                                                        crate.remove();
                                                        CrateMethods.crateInventoryMap.remove(crate);
                                                        crateSpawnLocation.getChunk().setForceLoaded(false);

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
                                    }
                                }
                            }.runTaskTimer(C.plugin, 0, 20);
                        } else {
                            new BukkitRunnable() {
                                HashMap<Team,Integer> teamScores = new HashMap<Team, Integer>();
                                HashMap<Team,Long> teamCooldown = new HashMap<Team, Long>();
                                Set<Team> involvedTeams = new HashSet<>();

                                int phase = 0;

                                boolean wait = false;
                                int maxScore = crateType.getPointsPerPhase();

                                String blue = ChatColor.BLUE+ "|";
                                String red = ChatColor.RED + "|";
                                String gray = ChatColor.GRAY + "|";

                                @Override
                                public void run() {
                                    if (!wait) {
                                        Iterator<Team> iterator = involvedTeams.iterator();
                                        int total = 0;

                                        for (Team team : teamScores.keySet()) {
                                            total = total+teamScores.get(team);
                                        }
                                        for (Team team : teamScores.keySet()) {
                                            for (String teamMemberNames : team.getEntries()) {
                                                if (Bukkit.getPlayer(teamMemberNames) != null) {

                                                    int blueLength = (int)(C.safeDivide(teamScores.get(team), maxScore) * 50);
                                                    int redLength = (int)(C.safeDivide((total - teamScores.get(team)), maxScore) * 50);
                                                    int grayLength = (int)(C.safeDivide((maxScore - total), maxScore) * 50);

// Ensure non-negative lengths
                                                    blueLength = Math.max(blueLength, 0);
                                                    redLength = Math.max(redLength, 0);
                                                    grayLength = Math.max(grayLength, 0);

                                                    String bar = blue.repeat(blueLength) + red.repeat(redLength) + gray.repeat(grayLength);
                                                    Bukkit.getPlayer(teamMemberNames).spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(bar));
                                                }
                                            }
                                        }

                                        for (Entity nearbyEntities : crateSpawnLocation.getWorld().getNearbyEntities(crateSpawnLocation, 15, 15, 15)) {
                                            if (nearbyEntities instanceof Player p) {
                                                if (C.getPlayerTeam(p) != null) {
                                                    Team team = C.getPlayerTeam(p);

                                                    if (teamScores.get(team) == null) {
                                                        teamScores.put(team, 10);
                                                        teamCooldown.put(team, System.currentTimeMillis() + 3 * 1000);
                                                        involvedTeams.add(team);
                                                    } else {
                                                        if (total+10 < maxScore) {
                                                            teamScores.put(team, teamScores.get(team) + 10);
                                                        }
                                                        if (involvedTeams.size() == 1 && teamScores.get(team) == maxScore-10) {
                                                            teamScores.put(team, teamScores.get(team) + 10);
                                                        }
                                                        teamCooldown.put(team, System.currentTimeMillis() + 3 * 1000);
                                                    }

                                                }
                                            }
                                        }


                                        while (iterator.hasNext()) {
                                            Team team = iterator.next();
                                            if (teamCooldown.get(team) < System.currentTimeMillis()) {
                                                teamScores.put(team, teamScores.get(team) - 20);
                                                if (teamScores.get(team) <= 0) {
                                                    iterator.remove();
                                                    teamScores.remove(team);
                                                }
                                            }
                                            if (teamScores.get(team) >= maxScore) {
                                                teamScores.put(team, maxScore);
                                                if (maxScore == crateType.getPointsPerPhase()) {
                                                    phase++;
                                                    String phases = "\uD83D\uDD12";
                                                    int phasesRemaining = crateType.getUnlockPhases()-phase;
                                                    crate.setCustomName(color + phases.repeat(phasesRemaining));
                                                    wait = true;
                                                    Bukkit.broadcastMessage("");
                                                    Bukkit.broadcastMessage(C.eventPrefix + ChatColor.RESET + team.getPrefix() + ChatColor.RESET + ChatColor.WHITE + "has broken a lock! " + color + phasesRemaining + ChatColor.WHITE + " lock(s) remain!");
                                                    Bukkit.broadcastMessage("");
                                                    Firework fw = (Firework) world.spawnEntity(crate.getLocation().clone().add(0,3,0), EntityType.FIREWORK_ROCKET);
                                                    FireworkMeta fwM = fw.getFireworkMeta();
                                                    FireworkEffect fwE = FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.WHITE).build();
                                                    fwM.addEffect(fwE);
                                                    fw.setFireworkMeta(fwM);
                                                    fw.setMetadata("pat",new FixedMetadataValue(C.plugin,"rat"));
                                                    fw.detonate();
                                                    new BukkitRunnable() {
                                                        int tick = 0;
                                                        ArrayList<String> playerNames = new ArrayList<>(team.getEntries());

                                                        @Override
                                                        public void run() {
                                                            Player teamMember = Bukkit.getPlayer(playerNames.get(tick));
                                                            if (teamMember != null) {
                                                                privateItemStacks.put(teamMember,new ArrayList<>());
                                                                Location location = crateSpawnLocation.clone().add(0, 3,0); // Add the offsets to the center location
                                                                for (int i = 0; i < crateType.getLootTableRolls(); i++) {
                                                                    ItemStack item = CrateMethods.getLootTableItem(crateType.getLootTable(), crateType);
                                                                    i = i + CrateMethods.getRollConsumption(item, crateType);
                                                                    LootTableEnum lootTable = CrateMethods.getLootTable(item, crateType.getIdentifier());
                                                                    if (lootTable != null) {
                                                                        int amount = random.nextInt(lootTable.getMinStackSize(), lootTable.getMaxStackSize() + 1);


                                                                        item.setAmount(amount);
                                                                    }
                                                                    if (item.getMaxStackSize() == 1) {
                                                                        item.setAmount(1);
                                                                    }
                                                                    Item droppedItem = world.dropItem(location,item);
                                                                    for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                                                        onlinePlayers.hideEntity(C.plugin,droppedItem);
                                                                    }
                                                                    teamMember.showEntity(C.plugin,droppedItem);
                                                                    privateItemStacks.get(teamMember).add(droppedItem);
                                                                }
                                                            }
                                                            tick++;
                                                            if (tick >= team.getSize()) {
                                                                wait = false;
                                                                for (Team teams : involvedTeams) {
                                                                    teamScores.put(teams,10);
                                                                }
                                                                cancel();
                                                            }
                                                        }
                                                    }.runTaskTimer(C.plugin,0,10);
                                                    if (phasesRemaining < 1) {
                                                        crate.remove();
                                                        cancel();
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                            }.runTaskTimer(C.plugin,0,5);
                        }
                        cancel();
                    }
                }
        }.runTaskTimer(C.plugin,100,20);
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

    public static void startRandomCrates(World world) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!C.restarting && C.cratesEnabled) {
                    if (Math.random() < 0.016 && Bukkit.getOnlinePlayers().size() > 10) {
                        double[] chance = {0.3, 0.6, 0.85, 0.9, 1};
                        int event = Arrays.binarySearch(chance, Math.random());
                        if (event < 0) event = -event - 1;
                        switch (event) {
                            case 0:
                                createCrate(world, "gold",false);

                                break;
                            case 1:
                                createCrate(world, "oxidized",false);
                                // event 1
                                break;
                            case 2:
                                createCrate(world, "end",false);
                                // event 2
                                break;
                            case 3:
                                createCrate(world, "nether",false);
                                // event 3
                                break;
                            case 4:
                                // blackbox
                                createCrate(world, "nether",false);
                                // event 4
                                break;
                        }
                        cancel();
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(C.plugin,3600,1200);
    }
}
