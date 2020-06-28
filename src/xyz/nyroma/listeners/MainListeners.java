package xyz.nyroma.listeners;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nyroma.crafts.BetterCrafts;
import xyz.nyroma.crafts.CraftsManager;
import xyz.nyroma.main.BotlinkManager;
import xyz.nyroma.main.MainUtils;
import xyz.nyroma.main.ScoreboardManager;
import xyz.nyroma.towny.citymanagement.City;
import xyz.nyroma.towny.citymanagement.CityManager;

import java.util.*;

import static xyz.nyroma.main.MainUtils.getTime;

public class MainListeners implements Listener {

    private int sap = 0;
    private int seeds = 0;
    private int saplings = 0;
    private int water = 0;
    private int flint = 0;
    private int kelp = 0;
    private int playerSleep = 0;
    private CityManager cm = new CityManager();
    private List<Biome> netherBiomes = Arrays.asList(Biome.NETHER_WASTES, Biome.BASALT_DELTAS, Biome.SOUL_SAND_VALLEY, Biome.CRIMSON_FOREST);
    public static boolean netherActivated = true;
    public static boolean endActivated = true;

    @EventHandler
    public void onPortal(PlayerPortalEvent e){
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && !netherActivated){
            e.setCancelled(true);
        } else if(e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && !endActivated){
            e.setCancelled(true);
        }
    }

    public MainListeners() {

    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        if (e.getItem().isSimilar(BetterCrafts.getSatur())) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 2 * 7200 * 20, 1));
        } else if(e.getItem().isSimilar(BetterCrafts.getDApple())){
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 60 * 20, 0));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 60 * 20, 1));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5 * 60 * 20, 0));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 5 * 60 * 20, 1));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 60 * 20, 2));
        } else if(e.getItem().isSimilar(BetterCrafts.getOApple())){
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 5 * 60 * 20, 1));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 60 * 20, 4));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 60 * 20, 4));
        }

        if(e.getItem().getType().equals(Material.APPLE)){
            for(int i = 0 ; i <= 3 ; i++){
                if(e.getItem().isSimilar(BetterCrafts.getOmen(i))){
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 5 * 60 * 20, i));
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message;
        if (cm.getCityOfMember(p.getName()).isPresent()) {
            City city = cm.getCityOfMember(p.getName()).get();
            ChatColor cc;
            ChatColor ps;
            if (p.isOp()) {
                cc = ChatColor.RED;
            } else {
                cc = ChatColor.YELLOW;
            }
            if (city.getOwner().equals(p.getName())) {
                ps = ChatColor.DARK_AQUA;
            } else {
                ps = ChatColor.AQUA;
            }

            message = "[" + ChatColor.GRAY + getTime() + ChatColor.WHITE + "] "
                    + "[" + ChatColor.BLUE + city.getRoyaume() + ChatColor.WHITE + "] "
                    + "[" + cc + city.getName() + ChatColor.WHITE + "] "
                    + ps + p.getName() + ChatColor.WHITE + " : " + e.getMessage();
        } else {
            message = "[" + ChatColor.GRAY + getTime() + ChatColor.WHITE + "] "
                    + "[" + ChatColor.YELLOW + "g" + ChatColor.WHITE + "] "
                    + ChatColor.AQUA + p.getName() + ChatColor.WHITE + " : " + e.getMessage();
        }

        for (Player play : p.getServer().getOnlinePlayers()) {
            play.sendMessage(message);
        }
        System.out.println(message);
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        Location loc = b.getLocation();
        ItemStack item = p.getInventory().getItemInMainHand();
        int r = new Random().nextInt(10) + 1;
        if (b.getType().equals(Material.SUGAR_CANE)) {
            if (r == 6) {
                loc.getWorld().dropItem(loc, new ItemStack(Material.BAMBOO));
            }
        } else if (b.getType().equals(Material.SAND)) {
            if (r == 6) {
                switch (new Random().nextInt(2)) {
                    case 0:
                        loc.getWorld().dropItem(loc, new ItemStack(Material.SUGAR_CANE));
                        break;
                    case 1:
                        loc.getWorld().dropItem(loc, new ItemStack(Material.CACTUS));
                        break;
                }
            }
        } else if (b.getType().equals(Material.GLASS)) {
            Location loc1 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
            Location loc2 = loc.add(0, -1, 0);
            if (loc2.getBlock().getType().equals(Material.MAGMA_BLOCK) && netherBiomes.contains(loc1.getBlock().getBiome())) {
                for (int i = 0; i < new Random().nextInt(5) + 1; i++) {
                    loc1.getWorld().dropItemNaturally(loc1, new ItemStack(Material.QUARTZ));
                }
                b.getLocation().getWorld().spawnParticle(Particle.FLAME, loc1, 100);
                loc1.getBlock().setType(Material.AIR);
            }
        } else if (b.getType().equals(Material.SPAWNER)) {
            try {
                if (item.hasItemMeta()) {
                    ItemMeta im = item.getItemMeta();
                    if (im.hasEnchants()) {
                        if (im.hasEnchant(Enchantment.SILK_TOUCH) && !netherBiomes.contains(b.getLocation().getBlock().getBiome())) {
                            b.getLocation().getWorld().dropItem(b.getLocation(), new ItemStack(Material.SPAWNER));
                        }
                    }
                }
            } catch (NullPointerException ignored) {
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Location loc = p.getLocation();
        p.sendMessage(ChatColor.RED + "Tu es mort en x = " + (int) loc.getX() + ", y = " + (int) loc.getY() + ", z = " + (int) loc.getZ() + "\n Tu as 5 minutes pour récupérer ton stuff !");
        if (e.getDeathMessage().contains("blown up by Creeper")) {
            e.setDeathMessage(ChatColor.DARK_GREEN + "Aw maaaan ! A Creeper blew up " + p.getName());
        } else if (e.getDeathMessage().contains("shot by Skeleton") && !p.getName().equals("Attiyas")) {
            e.setDeathMessage(ChatColor.DARK_GREEN + "Is it Attiyas ? Oh no, it's " + p.getName() + " who died because of a skeleton, f in the chat everyone");
        } else if (e.getDeathMessage().contains("fell out of the world") || e.getDeathMessage().contains("didn't want to live")) {
            /*if (p.getInventory().getItemInOffHand().isSimilar(new customItems().getSaver())) {
                e.setDeathMessage(ChatColor.GREEN + p.getName() + " a été sauvé par son saver !");
                e.setKeepInventory(true);
                e.setKeepLevel(true);
                p.getInventory().setItemInOffHand(null);
            } else {

            }*/
            e.setKeepInventory(false);
            e.setKeepLevel(false);
            e.setDeathMessage(ChatColor.DARK_GREEN + "Oops, " + p.getName() + " est tombé, f in the chat everyone");
        } else {
            e.setDeathMessage(ChatColor.DARK_GREEN + e.getDeathMessage());
        }
    }

    @EventHandler
    public void onPlayerKilled(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player p = (Player) e.getDamager();
            Player d = (Player) e.getEntity();
            ItemStack i = p.getInventory().getItemInMainHand();
            if (i.getType().equals(Material.GOLDEN_AXE) && i.getItemMeta().getEnchants().containsValue(10) && e.getDamage() >= d.getHealth()) {
                ItemStack sk = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skull = (SkullMeta) sk.getItemMeta();
                try {
                    if (skull != null) {
                        skull.setOwningPlayer(d.getPlayer());
                    }
                    sk.setItemMeta(skull);
                    Objects.requireNonNull(d.getLocation().getWorld()).dropItem(d.getLocation(), sk);
                } catch (NullPointerException ee) {
                    Bukkit.broadcastMessage("MainListeners, l.84");
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        System.out.println(p.getName() + " a rejoint ! IP : " + p.getAddress().getAddress().toString());
        ChatColor color;
        if (p.isOp()) {
            color = ChatColor.RED;
        } else {
            color = ChatColor.AQUA;
        }
        e.setJoinMessage(color + p.getName() + ChatColor.GREEN + " est connecté !");
        if (BotlinkManager.isActivated) {
            botUpdatePlayers(p);
        }

        p.discoverRecipes(CraftsManager.namespacedKeys);
        p.setScoreboard(ScoreboardManager.current);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        if (BotlinkManager.isActivated) {
            botUpdatePlayers(new ArrayList<>(Bukkit.getServer().getOnlinePlayers()));
        }
    }

    @EventHandler
    public void onDisconect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        System.out.println(p.getName() + " est parti ! IP : " + p.getAddress().getAddress().toString());
        ChatColor color;
        if (p.isOp()) {
            color = ChatColor.RED;
        } else {
            color = ChatColor.AQUA;
        }
        e.setQuitMessage(color + p.getName() + ChatColor.DARK_GREEN + " s'est déconnecté !");
    }

    private void botUpdatePlayers(Player p) {
        BotlinkManager blm = new BotlinkManager();
        Collection<? extends Player> cpl = p.getServer().getOnlinePlayers();
        System.out.println(cpl);
        if (cpl.isEmpty()) {
            blm.updatePlayers(new ArrayList<>(cpl), true);
        } else {
            blm.updatePlayers(new ArrayList<>(cpl), false);
        }
    }

    private void botUpdatePlayers(ArrayList<Player> players) {
        BotlinkManager blm = new BotlinkManager();
        if (players.isEmpty()) {
            blm.updatePlayers(new ArrayList<>(players), true);
        } else {
            blm.updatePlayers(new ArrayList<>(players), false);
        }
    }

    @EventHandler
    public void pssssBoom(EntityExplodeEvent e) {
        Entity ent = e.getEntity();
        if (ent.getType().equals(EntityType.CREEPER)) {
            e.blockList().removeAll(e.blockList());
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        Entity ent = e.getEntity();
        EntityType type = ent.getType();
        Location loc = ent.getLocation();
        if ((type.equals(EntityType.SKELETON) || type.equals(EntityType.GHAST)) && netherBiomes.contains(loc.getBlock().getBiome())) {
            if (new Random().nextInt(10) == 1) {
                ent.remove();
                loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
            }
        } else if (type.equals(EntityType.MAGMA_CUBE) && netherBiomes.contains(loc.getBlock().getBiome())) {
            if (new Random().nextInt(5) == 1) {
                ent.remove();
                loc.getWorld().spawnEntity(loc, EntityType.BLAZE);
            }
        } else if (type.equals(EntityType.PIGLIN) && netherBiomes.contains(loc.getBlock().getBiome())) {
            int i = new Random().nextInt(20);
            if (i == 7) {
                ent.remove();
                loc.getWorld().spawnEntity(loc, EntityType.BLAZE);
            } else if (i == 17) {
                ent.remove();
                loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        Block b = loc.getBlock();
        Block bl = loc.add(0, 1, 0).getBlock();
        BlockData b2 = bl.getBlockData();
        if (b.getType().equals(Material.GRASS)) {
            sap++;
            if (sap == 30) {
                sap = 0;
                Random r = new Random();
                if (r.nextInt(2) == 0) {
                    Material sapling = Material.OAK_SAPLING;
                    switch (r.nextInt(11)) {
                        case 0:
                            sapling = Material.ACACIA_SAPLING;
                            break;
                        case 1:
                            sapling = Material.SPRUCE_SAPLING;
                            break;
                        case 2:
                            sapling = Material.BIRCH_SAPLING;
                            break;
                        case 3:
                            sapling = Material.JUNGLE_SAPLING;
                            break;
                        case 4:
                            sapling = Material.DARK_OAK_SAPLING;
                            break;
                        case 5:
                            sapling = Material.OAK_SAPLING;
                            break;
                        case 6:
                            sapling = Material.ROSE_BUSH;
                            break;
                        case 7:
                            sapling = Material.SWEET_BERRIES;
                            break;
                        case 8:
                            sapling = Material.LILAC;
                            break;
                        case 9:
                            sapling = Material.BEETROOT_SEEDS;
                            break;
                        case 10:
                            sapling = Material.SUNFLOWER;
                            break;
                    }
                    loc.getWorld().dropItem(loc, new ItemStack(sapling));
                } else {
                    loc.getWorld().dropItem(loc, new ItemStack(Material.WHEAT_SEEDS));
                }
            }
            int r = new Random().nextInt(100);
            if (r == 82) {
                loc.getWorld().dropItem(loc, new ItemStack(Material.PUMPKIN_SEEDS));
            } else if (r == 24) {
                loc.getWorld().dropItem(loc, new ItemStack(Material.MELON_SEEDS));
            }
        } else if (b2 instanceof Ageable) {
            seeds++;
            if (seeds == 30) {
                seeds = 0;
                Ageable age = (Ageable) b2;
                int m = age.getAge();
                if (m < age.getMaximumAge()) {
                    age.setAge(m + 1);
                }
                bl.setBlockData(age);
                loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 100);
            }
        } else if (b.getBlockData() instanceof Sapling) {
            saplings++;
            if (saplings == 50) {
                b.getLocation().getWorld().generateTree(b.getLocation(), TreeType.BIG_TREE);
                loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 100);
            }
        } else if (b.getType().equals(Material.SEAGRASS)) {
            kelp++;
            Material mat = Material.KELP;
            if (kelp == 20) {
                switch (new Random().nextInt(6)) {
                    case 0:
                        mat = Material.BRAIN_CORAL_FAN;
                        break;
                    case 1:
                        mat = Material.BUBBLE_CORAL_FAN;
                        break;
                    case 2:
                        mat = Material.FIRE_CORAL_FAN;
                        break;
                    case 3:
                        mat = Material.HORN_CORAL_FAN;
                        break;
                    case 4:
                        mat = Material.TUBE_CORAL_FAN;
                        break;
                }
                kelp = 0;
                loc.getWorld().dropItem(loc, new ItemStack(mat));
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ItemStack items2 = p.getInventory().getItemInOffHand();

            Location loc = b.getLocation().add(0, 1, 0);
            if (b.getType().equals(Material.CAULDRON)) {
                water++;
                if (water == 50) {
                    water = 0;
                    p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.WATER_BUCKET));
                }
            } else if ((b.getType().equals(Material.GRASS_BLOCK) || b.getType().equals(Material.DIRT)) && p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                flint++;
                if (flint == 3) {
                    flint = 0;
                    p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.FLINT));
                }
            } else if (b.getType().equals(Material.MAGMA_BLOCK) && item.getType().equals(Material.BUCKET) && !netherBiomes.contains(b.getBiome())) {
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                ItemStack is = p.getInventory().getItemInMainHand();
                ItemStack i = new ItemStack(is.getType(), is.getAmount() - 1);
                p.getInventory().setItemInMainHand(i);
                loc.getWorld().dropItem(loc, new ItemStack(Material.LAVA_BUCKET));
            }

            if (item.getType().equals(Material.FIREWORK_STAR) && item.getItemMeta().hasEnchants()) {
                propulse(item, p);
            } else if (items2.getType().equals(Material.FIREWORK_STAR) && items2.getItemMeta().hasEnchants()) {
                propulse(item, p);
            }
        }
        else if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item.getType() == Material.DIRT && item.hasItemMeta()) {
                if (item.getItemMeta().hasEnchants()) {
                    if (item.getItemMeta().getEnchants().keySet().contains(Enchantment.BINDING_CURSE)) {
                        Location eye = p.getEyeLocation();
                        double yaw = eye.getYaw();
                        double pitch = eye.getPitch();

                        double X = 0;
                        double Y = -5 * sin(pitch);
                        double Z = 0;

                        if (yaw <= 0 && yaw >= -90) {
                            X = 5 * cos(yaw);
                            Z = 5 * sin(-yaw);
                        } else if (yaw < -90 && yaw >= -180) {
                            X = -5 * cos(yaw);
                            Z = 5 * sin(yaw);
                        } else if (yaw < -180 && yaw >= -270) {
                            X = 5 * cos(yaw);
                            Z = -5 * sin(yaw);
                        } else if (yaw < -270) {
                            X = -5 * cos(yaw);
                            Z = 5 * sin(yaw);
                        }

                        if (X > -1 && X < 1) {
                            X = 0;
                        }
                        if (Y > -1 && Y < 1) {
                            Y = 0;
                        }
                        if (Z > -1 && Z < 1) {
                            Z = 0;
                        }

                        Location loc = eye.add(X, Y, Z);

                        if (loc.getBlock().getType() == Material.AIR) {
                            CityManager cm = new CityManager();
                            boolean can = false;
                            if (MainUtils.getClaimer(loc).isPresent()) {
                                City city = MainUtils.getClaimer(loc).get();
                                if (cm.getCityOfMember(p.getName()).isPresent()) {
                                    if (cm.getCityOfMember(p.getName()).get() == city) {
                                        can = true;
                                    }
                                }
                            } else {
                                can = true;
                            }
                            if (can) {
                                loc.getBlock().setType(Material.DIRT);
                                int amount = item.getAmount();
                                if (amount > 0) {
                                    p.getInventory().getItemInMainHand().setAmount(amount - 1);
                                } else {
                                    p.getInventory().setItemInMainHand(null);
                                }
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Il faut que le block soit un block d'air !");
                        }
                        p.spawnParticle(Particle.CLOUD, loc, 50);
                        System.out.println(loc);
                    }
                }
            }
        }
        else if(e.getAction() == Action.LEFT_CLICK_BLOCK){
            if(b != null && b.getType() == Material.BEDROCK){
                boolean can = false;
                if(!new CityManager().getClaimer(b.getWorld().getName(), b.getLocation().getChunk().getX(), b.getLocation().getChunk().getZ()).isPresent()){
                    if(new CityManager().getCityOfMember(p.getName()).isPresent()){
                        if(new CityManager().getClaimer(b.getWorld().getName(), b.getLocation().getChunk().getX(), b.getLocation().getChunk().getZ()).get()
                                == new CityManager().getCityOfMember(p.getName()).get()){
                            can = true;
                        }
                    }
                } else {
                    can = true;
                }
                if(can){
                    ItemStack is = p.getInventory().getItemInMainHand();
                    if(is.getType() != Material.AIR) {
                        if (is.hasItemMeta() && is.getItemMeta() != null && is.getItemMeta() != null) {
                            ItemMeta im = is.getItemMeta();
                            if(im.hasEnchants() && im.getEnchants().size() > 0){
                                if(im.getEnchants().containsKey(Enchantment.DIG_SPEED) && im.getEnchants().get(Enchantment.DIG_SPEED) >= 10){
                                    b.setType(Material.AIR);
                                    b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.BEDROCK));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public double cos(double angl) {
        return Math.round(Math.cos(Math.toRadians(angl)));
    }

    public double sin(double angl) {
        return Math.round(Math.sin(Math.toRadians(angl)));
    }

    public void propulse(ItemStack it, Player p) {
        if (p.getLocation().getY() < 255) {
            Map<Enchantment, Integer> enchs = it.getItemMeta().getEnchants();
            if (enchs.size() > 0) {
                for (Enchantment ench : enchs.keySet()) {
                    if (ench.equals(Enchantment.KNOCKBACK)) {
                        Particle particle;
                        int duration;
                        switch (enchs.get(ench)) {
                            case 1:
                                particle = Particle.EXPLOSION_NORMAL;
                                duration = 10;
                                break;
                            case 2:
                                particle = Particle.EXPLOSION_LARGE;
                                duration = 20;
                                break;
                            case 3:
                                particle = Particle.EXPLOSION_HUGE;
                                duration = 30;
                                break;
                            default:
                                return;
                        }
                        p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, duration, 20));
                        p.playSound(p.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 100, 1);
                        p.spawnParticle(particle, p.getLocation(), 50);
                    }
                }
            }
        } else {
            p.sendMessage(ChatColor.RED + "Tu es beaucoup trop haut ! Le propulseur ne fonctionne pas à cette hauteur...");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Block b = e.getBlock();
        if (b.getType().equals(Material.FIRE)) {
            Location loc1 = b.getLocation().add(0, -1, 0);
            Location loc2 = b.getLocation().add(0, -2, 0);
            if (loc1.getBlock().getType().equals(Material.NETHERRACK) && loc2.getBlock().getType().equals(Material.SAND) && loc1.getWorld().getName().equals("n")) {
                loc1.getBlock().setType(Material.SOUL_SAND);
                loc2.getBlock().setType(Material.AIR);
                b.getLocation().getWorld().spawnParticle(Particle.FLAME, b.getLocation(), 100);
            }
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent e) {
        Entity ent = e.getEntity();
        EntityType type = ent.getType();
        Location loc = ent.getLocation();
        if (type.equals(EntityType.ZOMBIE) || type.equals(EntityType.SKELETON)) {
            int r = new Random().nextInt(3);
            if (r == 2) {
                loc.getWorld().dropItem(loc, new ItemStack(Material.IRON_NUGGET));
            }
        } else if (type.equals(EntityType.CREEPER)) {
            int r = new Random().nextInt(100);
            if (r == 2) {
                loc.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT));
            }
        } else if (type.equals(EntityType.ENDERMAN)) {
            int r = new Random().nextInt(100);
            if (r == 2) {
                loc.getWorld().dropItem(loc, new ItemStack(Material.DIAMOND));
            }
        } else if (type.equals(EntityType.BLAZE)) {
            if (new Random().nextInt(3) == 1) {
                loc.getWorld().dropItem(loc, new ItemStack(Material.GLOWSTONE_DUST));
            }
        } else if (type.equals(EntityType.VILLAGER)) {
            Bukkit.broadcastMessage(ChatColor.DARK_RED + e.getEventName() + " : " + e.getEntity().getLocation().toString() + ", " + e.getHandlers());
            System.out.println(e.getEventName());
        } else if (type.equals(EntityType.ENDER_DRAGON)) {
            for (int i = 0; i < 10; i++) {
                ExperienceOrb orb = (ExperienceOrb) ent.getLocation().getWorld().spawnEntity(ent.getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(600);
            }
        }

        List<EntityType> banned = Arrays.asList(EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.GHAST, EntityType.ENDERMAN);

        if(ent instanceof Monster){
            if(!banned.contains(type)){
                if (new Random().nextInt(100) == 27) {
                    loc.getWorld().dropItem(loc, new ItemStack(Material.valueOf(type.toString() + "_SPAWN_EGG")));
                }
            }
        }
    }

    @EventHandler
    public void onDropped(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        ItemStack item = e.getItemDrop().getItemStack();
        if (loc.getBlock().getType().equals(Material.WATER)) {
            if (item.getType().equals(Material.IRON_NUGGET)) {
                e.getItemDrop().setItemStack(new ItemStack(Material.LAPIS_LAZULI, item.getAmount()));
            } else if (item.getType().equals(Material.FLINT)) {
                e.getItemDrop().setItemStack(new ItemStack(Material.PRISMARINE_SHARD, item.getAmount()));
            } else if (item.getType().equals(Material.GLOWSTONE_DUST)) {
                e.getItemDrop().setItemStack(new ItemStack(Material.PRISMARINE_CRYSTALS, item.getAmount()));
            } else if (item.getType().equals(Material.SNOW_BLOCK)) {
                e.getItemDrop().setItemStack(new ItemStack(Material.ICE, item.getAmount()));
            }
        }

    }

    @EventHandler
    public void onSleep(PlayerBedLeaveEvent e) {
        this.playerSleep++;
        Player p = e.getPlayer();
        int max = p.getWorld().getPlayers().size() / 2;
        if (max == 0) {
            max = 1;
        }
        if (this.playerSleep >= max && p.getWorld().getTime() > 12500) {
            this.playerSleep = 0;
            p.getWorld().setTime(0);
        }
        if (this.playerSleep == 0) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + Integer.toString(this.playerSleep + 1) + " dort sur " + max);
        } else {
            Bukkit.broadcastMessage(ChatColor.YELLOW + Integer.toString(this.playerSleep + 1) + " dorment sur " + max);
        }
    }
}

