package xyz.nyroma.listeners;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nyroma.cityapi.citymanagement.City;
import xyz.nyroma.cityapi.citymanagement.CityUtils;
import xyz.nyroma.cityapi.main.SLocation;
import xyz.nyroma.crafts.BetterCrafts;
import xyz.nyroma.crafts.CraftsManager;
import xyz.nyroma.main.ScoreboardManager;

import java.util.*;

import static xyz.nyroma.main.MainUtils.getTime;

public class MainListeners implements Listener {

    private int sap = 0;
    private int flint = 0;
    private int kelp = 0;
    private int playerSleep = 0;
    private List<Biome> netherBiomes = Arrays.asList(Biome.NETHER_WASTES, Biome.BASALT_DELTAS, Biome.SOUL_SAND_VALLEY, Biome.CRIMSON_FOREST);
    public static boolean netherActivated = true;
    public static boolean endActivated = true;

    @EventHandler
    public void onSpawn(EntitySpawnEvent e){
        if(e.getEntity() instanceof Wither && e.getLocation().getWorld().getName().equals("world")){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && !netherActivated) {
            e.setCancelled(true);
        } else if (e.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && !endActivated) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        if (e.getItem().isSimilar(BetterCrafts.getDApple())) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 60 * 20, 1));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 60 * 20, 3));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5 * 60 * 20, 0));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 5 * 60 * 20, 2));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 60 * 20, 1));
        } else if (e.getItem().isSimilar(BetterCrafts.getOApple())) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 5 * 60 * 20, 2));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5 * 60 * 20, 2));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 60 * 20, 1));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message;
        if (CityUtils.getCityOfMember(p.getName()).isPresent()) {
            City city = CityUtils.getCityOfMember(p.getName()).get();
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
                    + "[" + ChatColor.BLUE + city.getEmpire().getName() + ChatColor.WHITE + "] "
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
        } else if (b.getType().equals(Material.SPAWNER)) {
            if (item.hasItemMeta()) {
                ItemMeta im = item.getItemMeta();
                if (im != null && im.hasEnchants()) {
                    if (im.hasEnchant(Enchantment.SILK_TOUCH) && !netherBiomes.contains(b.getLocation().getBlock().getBiome())) {
                        b.getLocation().getWorld().dropItem(b.getLocation(), new ItemStack(Material.SPAWNER));
                    }
                }
            }
        } else if (b.getType().equals(Material.GRASS)) {
            List<Material> mats = Arrays.asList(Material.ROSE_BUSH, Material.SWEET_BERRIES, Material.LILAC, Material.BEETROOT_SEEDS,
                    Material.SUNFLOWER, Material.PUMPKIN_SEEDS, Material.MELON_SEEDS);
            loc.getWorld().dropItem(loc, new ItemStack(mats.get(new Random().nextInt(mats.size()))));
        } else if(b.getType() == Material.GRASS){
            if(new Random().nextInt(1000000) == 1234){
                b.getWorld().dropItemNaturally(b.getLocation().add(0,1,0), new ItemStack(Material.EMERALD));
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Location loc = p.getLocation();
        p.sendMessage(
                ChatColor.RED + "Tu es mort en x = " + (int) loc.getX() + ", y = " + (int) loc.getY() + ", z = " + (int) loc.getZ() + "\n Tu as 5 minutes pour récupérer ton stuff !"
        );
        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 100, 1);
        e.setDeathMessage(ChatColor.DARK_GREEN + e.getDeathMessage());

        ItemStack sk = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) sk.getItemMeta();
        if (skull != null) {
            skull.setOwningPlayer(p.getPlayer());
        }
        sk.setItemMeta(skull);
        Objects.requireNonNull(p.getLocation().getWorld()).dropItem(p.getLocation(), sk);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        System.out.println(p.getName() + " a rejoint ! IP : " + p.getAddress().getAddress().toString());
        ChatColor color = p.isOp() ? ChatColor.RED : ChatColor.AQUA;
        e.setJoinMessage(color + p.getName() + ChatColor.GREEN + " est connecté !");

        p.discoverRecipes(CraftsManager.namespacedKeys);
        p.setScoreboard(ScoreboardManager.current);
    }

    @EventHandler
    public void onDisconect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        System.out.println(p.getName() + " est parti ! IP : " + p.getAddress().getAddress().toString());
        ChatColor color = p.isOp() ? ChatColor.RED : ChatColor.AQUA;
        e.setQuitMessage(color + p.getName() + ChatColor.DARK_GREEN + " s'est déconnecté !");
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        Block b = loc.getBlock();
        if (b.getType().equals(Material.GRASS)) {
            sap++;
            if (sap == 30) {
                Random r = new Random();
                List<Material> mats = Arrays.asList(Material.OAK_SAPLING, Material.ACACIA_SAPLING, Material.SPRUCE_SAPLING, Material.BIRCH_SAPLING,
                        Material.JUNGLE_SAPLING, Material.DARK_OAK_SAPLING, Material.OAK_SAPLING);
                loc.getWorld().dropItem(loc, new ItemStack(r.nextInt(2) == 0 ? mats.get(r.nextInt(mats.size())) : Material.WHEAT_SEEDS));
                sap = 0;
            }
        } else if (b.getType().equals(Material.SEAGRASS)) {
            kelp++;
            if (kelp == 20) {
                List<Material> mats = Arrays.asList(Material.BRAIN_CORAL_FAN, Material.BUBBLE_CORAL_FAN, Material.FIRE_CORAL_FAN, Material.HORN_CORAL_FAN,
                        Material.TUBE_CORAL_FAN);
                loc.getWorld().dropItem(loc, new ItemStack(mats.get(new Random().nextInt(mats.size()))));
                kelp = 0;
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

            if ((b.getType().equals(Material.GRASS_BLOCK) || b.getType().equals(Material.DIRT)) && item.getType().equals(Material.AIR)) {
                flint++;
                if (flint == 3) {
                    flint = 0;
                    p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.FLINT));
                }
            }
            if((b.getType() == Material.POPPY || b.getType() == Material.DANDELION || b.getType() == Material.LILY_OF_THE_VALLEY) && item.getType() == Material.BONE_MEAL){
                p.getInventory().getItemInMainHand().setAmount(item.getAmount()-1);
                b.getLocation().getWorld().dropItemNaturally(b.getLocation(), new ItemStack(b.getType()));
            }

        } else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (b != null && b.getType() == Material.BEDROCK && b.getLocation().getY() > 10) {
                boolean can = false;
                SLocation sloc = new SLocation(b.getWorld().getName(), b.getLocation().getChunk().getX(), b.getLocation().getChunk().getZ());
                if (!CityUtils.getClaimer(sloc.getWorld(), sloc.getX(), sloc.getZ()).isPresent()) {
                    if (CityUtils.getCityOfMember(p.getName()).isPresent()) {
                        if (CityUtils.getClaimer(sloc.getWorld(), sloc.getX(), sloc.getZ()).get()
                                == CityUtils.getCityOfMember(p.getName()).get()) {
                            can = true;
                        }
                    }
                } else {
                    can = true;
                }
                if (can) {
                    ItemStack is = p.getInventory().getItemInMainHand();
                    if (is.getType() != Material.AIR) {
                        if (is.hasItemMeta() && is.getItemMeta() != null && is.getItemMeta() != null) {
                            ItemMeta im = is.getItemMeta();
                            if (im.hasEnchants() && im.getEnchants().size() > 0) {
                                if (im.getEnchants().containsKey(Enchantment.DIG_SPEED) && im.getEnchants().get(Enchantment.DIG_SPEED) >= 10) {
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
            r = new Random().nextInt(20);
            if(r == 12){
                loc.getWorld().dropItem(loc, new ItemStack(Material.TNT));
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
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "Un PNJ a été tué. Localisation :" + e.getEntity().getLocation().toString());
            System.out.println(e.getEventName());
        } else if (type.equals(EntityType.ENDER_DRAGON)) {
            for (int i = 0; i < 10; i++) {
                ExperienceOrb orb = (ExperienceOrb) ent.getLocation().getWorld().spawnEntity(ent.getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(600);
            }
        }

        List<EntityType> banned = Arrays.asList(EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.GHAST, EntityType.ENDERMAN, EntityType.PIGLIN, EntityType.ZOMBIFIED_PIGLIN);

        if (ent instanceof Monster) {
            if (!banned.contains(type)) {
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
            if (item.getType().equals(Material.FLINT)) {
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

