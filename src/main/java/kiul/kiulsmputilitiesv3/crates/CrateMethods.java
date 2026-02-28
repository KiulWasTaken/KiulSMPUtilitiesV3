package kiul.kiulsmputilitiesv3.crates;

import kiul.kiulsmputilitiesv3.C;
import kiul.kiulsmputilitiesv3.accessories.IngredientItemEnum;
import kiul.kiulsmputilitiesv3.config.ConfigData;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.TimeUnit;



public class CrateMethods {

    public static HashMap<ArmorStand, Inventory> crateInventoryMap = new HashMap<>();
    public static HashMap<ArmorStand, Long> crateUnlockTime = new HashMap<>();

    public static ArrayList<String> playersWhoGotLoot = new ArrayList<>();
    public static HashMap<Location, Double> activeCratesLocation = new HashMap<>();

    public static ArrayList<ArmorStand> locked = new ArrayList<>();
    public static ArrayList<ArmorStand> unlocking = new ArrayList<>();

    public static HashMap<Player, ArrayList<Item>> privateItemStacks = new HashMap<>();


    public static Location returnCrateLocation(World world) {
        Random random = new Random();
        int x = random.nextInt((int) -(world.getWorldBorder().getSize() / 2), (int) (world.getWorldBorder().getSize() / 2));
        int z = random.nextInt((int) -(world.getWorldBorder().getSize() / 2), (int) (world.getWorldBorder().getSize() / 2));
        int y = world.getHighestBlockYAt(x, z);
        return new Location(world, x, y, z);
    }

    public static ItemStack randomEnchantedGear(ItemStack itemStack) {
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
                int enchantmentNum = (int) (Math.random() * maceBookEnchantments.size());
                meta.addStoredEnchant(maceBookEnchantments.get(enchantmentNum), (int) (Math.random() * maceBookEnchantments.get(enchantmentNum).getMaxLevel() + 0.5), false);
                if (Math.random() > 0.5) {
                    meta.addStoredEnchant(armourEnchantments.get(1), (int) (Math.random() * 3.5), false);
                }
                if (!meta.hasStoredEnchants()) {
                    meta.addStoredEnchant(Enchantment.MENDING, 1, false);
                }
                itemStack.setItemMeta(meta);
                break;
            case NETHERITE_BLOCK:
                gearItem = netheriteBlock.get((int) (Math.random() * netheriteBlock.size()));
                gearItem.addEnchantment(armourEnchantments.get(0), 4);
                gearItem.addEnchantment(armourEnchantments.get(1), 3);
                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(armourEnchantments.get(2), 1);
                }
                break;
            case DIAMOND_BLOCK:
                gearItem = diamondBlock.get((int) (Math.random() * diamondBlock.size()));
                gearItem.addEnchantment(armourEnchantments.get(0), 4);
                gearItem.addEnchantment(armourEnchantments.get(1), 3);
                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(armourEnchantments.get(2), 1);
                }
                break;
            case ANCIENT_DEBRIS:
                gearItem = ancientDebris.get((int) (Math.random() * ancientDebris.size()));
                gearItem.addEnchantment(toolEnchants.get(0), 4 + (int) (Math.random() + 0.5));

                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(toolEnchants.get(1), 1);
                } else {
                    gearItem.addEnchantment(toolEnchants.get(2), 3);
                }
                gearItem.addEnchantment(toolEnchants.get(3), 3);
                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(toolEnchants.get(4), 1);
                }
                break;
            case DIAMOND_ORE:
                gearItem = diamondOre.get((int) (Math.random() * diamondOre.size()));
                gearItem.addEnchantment(toolEnchants.get(0), 4 + (int) (Math.random() + 0.5));

                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(toolEnchants.get(1), 1);
                } else {
                    gearItem.addEnchantment(toolEnchants.get(2), 3);
                }
                gearItem.addEnchantment(toolEnchants.get(3), 3);
                if (Math.random() > 0.5) {
                    gearItem.addEnchantment(toolEnchants.get(4), 1);
                }


                break;
        }
        gearItem.setAmount(1);
        return gearItem;
    }

    public static ItemStack getLootTableItem(ArrayList<ItemStack> crateLootTable, CrateTypeEnum crateType) {
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

    public static LootTableEnum getLootTable(ItemStack itemStack, String identifier) {
        for (LootTableEnum lootTableEnum : LootTableEnum.values()) {
            if (lootTableEnum.getMaterial() == itemStack.getType()) {
                if (lootTableEnum.getCrateType().equalsIgnoreCase(identifier)) {
                    return lootTableEnum;
                }
            }
        }
        return null;
    }


    public static CrateTypeEnum getCrate(String identifier) {
        for (CrateTypeEnum crateTypeEnum : CrateTypeEnum.values()) {
            if (crateTypeEnum.getIdentifier().equalsIgnoreCase(identifier)) {
                return crateTypeEnum;
            }
        }
        return CrateTypeEnum.Gold;
    }

    public static int getRollConsumption(ItemStack item, CrateTypeEnum crateType) {
        for (LootTableEnum lootTableEnum : LootTableEnum.values()) {
            if (lootTableEnum.getMaterial() == item.getType()) {
                if (lootTableEnum.getCrateType().equalsIgnoreCase(crateType.getIdentifier())) {
                    return lootTableEnum.getRollConsumption() - 1;
                }
            }
        }
        return 0;
    }


    public static void createCrate(World world, String type, boolean debug) {


        CrateTypeEnum crateType = getCrate(type);

        Location crateSpawnLocation = returnCrateLocation(world);
        long crateSpawnTime = System.currentTimeMillis() + crateType.getSpawnTime();
        if (debug) {
            crateSpawnTime = System.currentTimeMillis() + 1000 * 60;
        }

        int x = (int) crateSpawnLocation.getX();
        int y = (int) crateSpawnLocation.getY();
        int z = (int) crateSpawnLocation.getZ();
        crateSpawnLocation.getChunk().setForceLoaded(true);
        int[] timestamps = C.splitTimestamp(crateSpawnTime);
        long spawnTime = crateSpawnTime;
        new BukkitRunnable() {
            long startTime = System.currentTimeMillis();
            ArmorStand nameplate = (ArmorStand) world.spawnEntity(crateSpawnLocation.add(0.5, 2, 0.5), EntityType.ARMOR_STAND);
            long timeUntilSpawn = spawnTime - System.currentTimeMillis();
            int tick = 0;
            BossBar bossBar = BossBar.bossBar(crateType.getDisplayName(), 0, BossBar.Color.WHITE, BossBar.Overlay.NOTCHED_12);

            @Override
            public void run() {

                if (System.currentTimeMillis() < spawnTime && !debug) {
                    long currentTime = System.currentTimeMillis();
                    tick++;
                    int[] timestamps = C.splitTimestamp(spawnTime);
                    nameplate.setInvulnerable(true);
                    nameplate.setVisible(false);
                    nameplate.setGravity(false);
                    nameplate.setCustomNameVisible(true);
                    nameplate.setPersistent(true);
                    nameplate.setMarker(true);
                    nameplate.setCustomName(String.format("%02d:%02d:%02d", timestamps[0], timestamps[1], timestamps[2]));


                    float progress = 1 - (float) (currentTime - startTime) / (spawnTime - startTime);
                    progress = Math.min(1f, Math.max(0f, progress));
                    bossBar.progress(progress);
                    Component time = Component.empty();
                    if (spawnTime - currentTime > 30000) {
                        if (tick <= 15) {
                            time = Component.text(" Crate Is Spawning At ").append(Component.text(crateSpawnLocation.x() + "x " + crateSpawnLocation.y() + "y " + crateSpawnLocation.z() + "z").color(NamedTextColor.WHITE));
                        }
                        if (tick > 15) {
                            time = Component.text(" Crate Is Spawning").append(Component.text(" In " + timestamps[0] + "h " + timestamps[1] + "m " + timestamps[2] + "s").color(NamedTextColor.WHITE));

                        }
                    } else {
                        time = Component.text(" Crate Is Spawning").append(Component.text(" In " + timestamps[0] + "h " + timestamps[1] + "m " + timestamps[2] + "s").color(NamedTextColor.WHITE));
                    }
                    if (tick > 30) tick = 0;
                    bossBar.name(crateType.getDisplayName().append(time));
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        bossBar.addViewer(p);
                    }
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        bossBar.removeViewer(p);
                    }
                    nameplate.setCustomNameVisible(false);
                    nameplate.remove();
                    //  ChatColor.GOLD+""+ChatColor.BOLD+"EVENT" + ChatColor.RESET+ChatColor.GRAY+" » ";
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcast(Component.empty().append(Component.text("[EVENT]").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD)
                            .append(Component.text(" » ").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                                    .append(crateType.getDisplayName()).append(Component.text(" Crate Has Spawned At " + crateSpawnLocation.x() + "x " + crateSpawnLocation.y() + "y " + crateSpawnLocation.z() + "z").color(NamedTextColor.WHITE)))));
                    Bukkit.broadcastMessage("");
                    Random random = new Random();
                    Vector cornerA = crateSpawnLocation.clone().add(10, 10, 10).toVector();
                    Vector cornerB = crateSpawnLocation.clone().add(-10, -10, -10).toVector();
                    Vector squareCornerMin = Vector.getMinimum(cornerA, cornerB);
                    Vector squareCornerMax = Vector.getMaximum(cornerA, cornerB);
                    String color = crateType.getTextColor();
                    ArmorStand crate = (ArmorStand) world.spawnEntity(crateSpawnLocation.add(0.5, -2.4, 0.5), EntityType.ARMOR_STAND);
                    crate.setInvulnerable(true);
                    crate.setVisible(false);
                    crate.setGravity(false);
                    crate.setCustomNameVisible(true);
                    crate.setPersistent(true);
                    crate.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
                    crate.setHelmet(new ItemStack(crateType.getCrateType()));


                    new BukkitRunnable() {
                        public void run() {
                            if (!crate.isDead()) {
                                drawCubeEdgesOnce(crateSpawnLocation.clone().add(0.5,0.4,0.5),20,0.5);
                            } else {
                                cancel();
                            }
                        }
                    }.runTaskTimer(C.plugin, 0L, 10L);
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
                                            TimeUnit.MILLISECONDS.toSeconds(unlockTime - System.currentTimeMillis()) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(unlockTime - System.currentTimeMillis()))
                                    ));

                                    if (System.currentTimeMillis() >= unlockTime) {
                                        unlocking.remove(crate);
                                        populateCrate(crateType, crate, null);
                                        crate.setCustomName(color + "CLICK TO OPEN");
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                if (CrateMethods.crateInventoryMap.get(crate).isEmpty()) {
                                                    world.playSound(crate.getLocation(), Sound.BLOCK_BELL_RESONATE, 10f, 1f);
                                                    crate.remove();
                                                    CrateMethods.crateInventoryMap.remove(crate);
                                                    crateSpawnLocation.getChunk().setForceLoaded(false);

                                                    HashSet<String> uniqueSet = new HashSet<>(playersWhoGotLoot);
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
                        ArmorStand damageDisplay = (ArmorStand) world.spawnEntity(crateSpawnLocation.clone().add(0, 2.5, 0), EntityType.ARMOR_STAND);
                        damageDisplay.setMarker(true);
                        damageDisplay.setInvulnerable(true);
                        damageDisplay.setVisible(false);
                        damageDisplay.setGravity(false);
                        damageDisplay.setCustomNameVisible(true);
                        damageDisplay.setPersistent(true);
                        TextDisplay captureDisplay = (TextDisplay) world.spawnEntity(crateSpawnLocation.clone().add(0, 2.53, 0), EntityType.TEXT_DISPLAY);
                        captureDisplay.setVisibleByDefault(true);
                        captureDisplay.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);
                        activeCratesLocation.put(crateSpawnLocation, 0.0);

                        BukkitTask captureRunnable = new BukkitRunnable() {
                            HashMap<Team, Integer> teamScores = new HashMap<Team, Integer>();
                            HashMap<Team, Long> teamCooldown = new HashMap<Team, Long>();
                            Set<Team> involvedTeams = new HashSet<>();

                            int phase = 0;
                            int despawnTick = 0;
                            boolean wait = false;
                            int maxScore = crateType.getPointsPerPhase();


                            @Override
                            public void run() {

                                if (involvedTeams.isEmpty()) {
                                    despawnTick++;
                                } else {
                                    despawnTick = 0;
                                }

                                if (despawnTick >= 4*300) {//*300 5 minutes
                                    Bukkit.broadcastMessage("");
                                    Bukkit.broadcast(Component.empty().append(Component.text("[EVENT]").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD)
                                            .append(Component.text(" » ").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false)
                                                    .append(crateType.getDisplayName()).append(Component.text(" crate has despawned due to inactivity").color(NamedTextColor.WHITE)))));
                                    Bukkit.broadcastMessage("");
                                    crate.remove();
                                    damageDisplay.remove();
                                    captureDisplay.remove();
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            activeCratesLocation.remove(crateSpawnLocation);
                                        }
                                    }.runTaskLater(C.plugin, 20);
                                    cancel();

                                }
                                damageDisplay.setCustomName(color + "" + C.twoPointDecimal.format(activeCratesLocation.get(crateSpawnLocation)));
                                if (!wait) {
                                    Iterator<Team> iterator = involvedTeams.iterator();
                                    int total = 0;

                                    for (Team team : teamScores.keySet()) {
                                        total = total + teamScores.get(team);
                                    }
                                    int size = 30;

                                    String str = "";
                                    double grayLength = size;
                                    for (Team team : teamScores.keySet()) {
                                        double teamLength = ((double) teamScores.get(team) / (double) maxScore) * size;

                                        teamLength = Math.round(teamLength);

                                        // Ensure its not exceeding 50 length
                                        teamLength = (teamLength > size) ? size : teamLength;

                                        // Ensure non-negative lengths
                                        teamLength = Math.max(teamLength, 0);
                                        String teamBar = team.getColor() + "|";
                                        str += teamBar.repeat((int) teamLength);
                                        grayLength -= teamLength;
                                    }

                                    grayLength = (grayLength > size) ? size : grayLength;
                                    grayLength = Math.max(grayLength, 0);
                                    String grayBar = "|";
                                    captureDisplay.setText(str+C.GRAY_BLUE+grayBar.repeat((int) grayLength));

                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (p.getLocation().toVector().isInAABB(squareCornerMin, squareCornerMax)) {
                                            if (C.getPlayerTeam(p) != null) {
                                                Team team = C.getPlayerTeam(p);

                                                if (teamScores.get(team) == null) {
                                                    teamScores.put(team, 10);
                                                    teamCooldown.put(team, System.currentTimeMillis() + 3 * 1000);
                                                    involvedTeams.add(team);
                                                } else {
                                                    if (total + 10 < maxScore) {
                                                        teamScores.put(team, teamScores.get(team) + 10);
                                                    }
                                                    if (involvedTeams.size() == 1 && teamScores.get(team) == maxScore - 10) {
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
                                                continue;
                                            }
                                        }
                                        if (teamScores.get(team) >= maxScore) {
                                            teamScores.put(team, maxScore);
                                            if (maxScore == crateType.getPointsPerPhase()) {
                                                phase++;
                                                String phases = "\uD83D\uDD12";
                                                int phasesRemaining = crateType.getUnlockPhases() - phase;
                                                crate.setCustomName(color + phases.repeat(phasesRemaining));
                                                wait = true;
                                                Bukkit.broadcastMessage("");
                                                Bukkit.broadcastMessage(C.eventPrefix + ChatColor.RESET + team.getPrefix() + ChatColor.RESET + ChatColor.WHITE + "has broken a lock! " + color + phasesRemaining + ChatColor.WHITE + " lock(s) remain!");
                                                Bukkit.broadcastMessage("");
                                                Firework fw = (Firework) world.spawnEntity(crate.getLocation().clone().add(0, 3, 0), EntityType.FIREWORK_ROCKET);
                                                FireworkMeta fwM = fw.getFireworkMeta();
                                                FireworkEffect fwE = FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.WHITE).build();
                                                fwM.addEffect(fwE);
                                                fw.setFireworkMeta(fwM);
                                                fw.setMetadata("pat", new FixedMetadataValue(C.plugin, "rat"));
                                                fw.detonate();
                                                new BukkitRunnable() {
                                                    int tick = 0;
                                                    ArrayList<String> playerNames = new ArrayList<>(team.getEntries());

                                                    @Override
                                                    public void run() {
                                                        Player teamMember = Bukkit.getPlayer(playerNames.get(tick));
                                                        if (teamMember != null) {
                                                            privateItemStacks.put(teamMember, new ArrayList<>());
                                                            Location location = crateSpawnLocation.clone().add(0, 3, 0); // Add the offsets to the center location

                                                            /* increase the amount of times loot is rolled depending on how much damage has been dealt in the
                                                             * vicinity of the crate since the KOTH event started. */
                                                            int lootTableExtraRolls = (int) (Math.floor(activeCratesLocation.get(crateSpawnLocation)) / 400);
                                                            if (lootTableExtraRolls > 5) {
                                                                lootTableExtraRolls = 5;
                                                            }

                                                            for (int i = 0; i < crateType.getLootTableRolls() + lootTableExtraRolls; i++) {
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
                                                                item.getItemMeta().setMaxStackSize(item.getAmount());
                                                                Item droppedItem = world.dropItem(location, item);
                                                                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                                                    onlinePlayers.hideEntity(C.plugin, droppedItem);
                                                                }
                                                                teamMember.showEntity(C.plugin, droppedItem);
                                                                privateItemStacks.get(teamMember).add(droppedItem);
                                                            }
                                                        }
                                                        tick++;
                                                        if (tick >= team.getSize()) {
                                                            tick = 0;
                                                            wait = false;
                                                            for (Team teams : involvedTeams) {
                                                                teamScores.put(teams, 10);
                                                            }
                                                            cancel();
                                                        }
                                                    }
                                                }.runTaskTimer(C.plugin, 0, 10);
                                                if (phasesRemaining < 1) {
                                                    crate.remove();
                                                    damageDisplay.remove();
                                                    captureDisplay.remove();
                                                    new BukkitRunnable() {
                                                        @Override
                                                        public void run() {
                                                            activeCratesLocation.remove(crateSpawnLocation);
                                                        }
                                                    }.runTaskLater(C.plugin, 20);
                                                    cancel();
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }.runTaskTimer(C.plugin, 0, 5);

                    }
                    cancel();
                }
            }
        }.runTaskTimer(C.plugin, 0, 20);
    }

    public static void populateCrate(CrateTypeEnum crateType, ArmorStand crate, Player p) {
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
        Inventory trueCrateInventory = Bukkit.createInventory(null, 27, "Crate");
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
        crateInventoryMap.put(crate, trueCrateInventory);
    }

    public static void startRandomCrates(World world) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!C.restarting && !ConfigData.get().getBoolean("crates")) {

                    if (Bukkit.getOnlinePlayers().size() >= 10) {
                        double magicNumber = 0.001 * Bukkit.getOnlinePlayers().size() - 10;
                        if (Math.random() < 0.016 + magicNumber) {
                            double[] chance = {0.3, 0.6, 0.85, 0.9, 1};
                            int event = Arrays.binarySearch(chance, Math.random());
                            if (event < 0) event = -event - 1;
                            switch (event) {
                                case 0:
                                    createCrate(world, "gold", false);

                                    break;
                                case 1:
                                    createCrate(world, "oxidized", false);
                                    // event 1
                                    break;
                                case 2:
                                    createCrate(world, "end", false);
                                    // event 2
                                    break;
                                case 3:
                                    createCrate(world, "nether", false);
                                    // event 3
                                    break;
                                case 4:
                                    // blackbox
                                    createCrate(world, "nether", false);
                                    // event 4
                                    break;
                            }
                            cancel();
                        }
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(C.plugin, 3600, 1200);
    }

    public static HashMap<Player, HashMap<Integer, TextDisplay>> captureAreaBorders = new HashMap<>();

    public static void showCaptureAreaBorders(Player p, CrateTypeEnum crateType, Vector min, Vector max, Location crateSpawnLocation) {
        if (p.getLocation().distance(crateSpawnLocation) > 30) return;
        boolean inside = p.getLocation().toVector().isInAABB(min, max);
        float distanceToClosestEdge = (float) AABBUtils.getDistanceToClosestEdge(p, min, max, inside);
        Bukkit.broadcastMessage(distanceToClosestEdge + "");
        if (distanceToClosestEdge > 5) {
            if (captureAreaBorders.get(p) != null) {
                for (TextDisplay display : captureAreaBorders.get(p).values()) {
                    display.remove();
                }
                captureAreaBorders.remove(p);
            }
        } else {
            float normalizedDistance = 1 - distanceToClosestEdge / 5.0f;  // Assuming distanceToClosestEdge is calculated earlier
            if (captureAreaBorders.get(p) == null) {
                captureAreaBorders.put(p, new HashMap<>());

                // Define the distance for each cardinal direction
                double distance = 10.0;  // Change this to adjust the distance

                // Cardinal directions (north, south, east, west, up, down)
                Location[] directions = {
                        new Location(p.getWorld(), 0, 0, distance),   // North (positive Z direction)
                        new Location(p.getWorld(), 0, 0, -distance),  // South (negative Z direction)
                        new Location(p.getWorld(), distance, 0, 0),   // East (positive X direction)
                        new Location(p.getWorld(), -distance, 0, 0),  // West (negative X direction)
                        new Location(p.getWorld(), 0, distance, 0),   // Up (positive Y direction)
                        new Location(p.getWorld(), 0, -distance, 0)   // Down (negative Y direction)
                };


                // Iterate over each direction and create a TextDisplay entity
                for (int i = 0; i < 6; i++) {
                    // Spawn the TextDisplay
                    TextDisplay textDisplay = (TextDisplay) p.getWorld().spawnEntity(crateSpawnLocation.clone().add(directions[i]), EntityType.TEXT_DISPLAY);
                    textDisplay.setSeeThrough(true);
                    textDisplay.setTransformation(new Transformation(textDisplay.getTransformation().getTranslation(), textDisplay.getTransformation().getLeftRotation(), new Vector3f(100.0f), textDisplay.getTransformation().getRightRotation()));
                    textDisplay.setVisibleByDefault(false);
                    p.showEntity(C.plugin, textDisplay);

                    // Move textDisplay to the proper location
                    Location newLocation = crateSpawnLocation.clone().add(directions[i]);
                    textDisplay.teleport(newLocation);
                    // Calculate the yaw to make the text perpendicular to the crate spawn
                    float[] angle = calculateYawAndPitch(crateSpawnLocation, newLocation, inside);

                    // Set rotation (yaw controls horizontal rotation, pitch controls vertical rotation)
                    textDisplay.setRotation(angle[0], angle[1]);
                    // Set text color based on the crateType
                    float alpha = (normalizedDistance * 255);
                    TextColor crateTextColor = crateType.getDisplayName().color();
                    textDisplay.setBackgroundColor(Color.fromARGB((int) alpha, 255, 255, 255));


                    // Store textDisplay for later use (capturing area borders)
                    captureAreaBorders.get(p).put(i, textDisplay);
                }
            } else {
                for (TextDisplay textDisplay : captureAreaBorders.get(p).values()) {
                    float alpha = (normalizedDistance * 255);
                    textDisplay.setBackgroundColor(Color.fromARGB((int) alpha, 255, 255, 255));
                    Bukkit.broadcastMessage("" + alpha);
                }
            }
        }
    }

    private static float[] calculateYawAndPitch(Location origin, Location target, boolean inside) {
        // Get the direction vector from the origin to the target
        Vector direction = target.clone().subtract(origin).toVector();

        // Calculate the yaw (horizontal angle) based on the direction vector
        double yaw = Math.atan2(direction.getZ(), direction.getX());

        // Calculate the pitch (vertical angle) based on the vertical distance and horizontal distance
        double horizontalDistance = Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ());
        double pitch = Math.atan2(direction.getY(), horizontalDistance);

        // Convert from radians to degrees and normalize between 0 and 360 for yaw, and -90 to 90 for pitch
        float yawInDegrees = inside ? (float) Math.toDegrees(yaw) + 90 : (float) Math.toDegrees(yaw) - 90;  // Adding 90 to adjust orientation
        float pitchInDegrees = (float) Math.toDegrees(pitch);

        // Return both yaw and pitch
        return new float[]{yawInDegrees, pitchInDegrees};
    }

    public static void drawCubeEdgesOnce(Location center, double edgeLength, double step) {
        if (step <= 0) throw new IllegalArgumentException("step must be > 0");

        World world = center.getWorld();
        double half = edgeLength / 2.0;

        // 8 cube corners relative to center
        double[][] corners = new double[][]{
                {-half, -half, -half}, {half, -half, -half}, {half, half, -half}, {-half, half, -half},
                {-half, -half, half}, {half, -half, half}, {half, half, half}, {-half, half, half}
        };

        // 12 edges (pairs of corner indices)
        int[][] edges = new int[][]{
                {0, 1}, {1, 2}, {2, 3}, {3, 0},
                {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        Particle.DustOptions dust = new Particle.DustOptions(Color.RED, 1.0f);

        for (int[] edge : edges) {
            double[] a = corners[edge[0]];
            double[] b = corners[edge[1]];

            double dx = b[0] - a[0];
            double dy = b[1] - a[1];
            double dz = b[2] - a[2];

            double edgeLen = Math.sqrt(dx * dx + dy * dy + dz * dz);
            int steps = Math.max(1, (int) Math.ceil(edgeLen / step));

            for (int i = 0; i <= steps; i++) {
                double t = (double) i / steps;

                double x = center.getX() + a[0] + dx * t;
                double y = center.getY() + a[1] + dy * t;
                double z = center.getZ() + a[2] + dz * t;

                world.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0, dust);
            }
        }
    }
}
