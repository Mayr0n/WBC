package xyz.nyroma.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nyroma.bossmanagement.BossLoots;
import xyz.nyroma.bossmanagement.BossSummoners;
import xyz.nyroma.bossmanagement.BossType;
import xyz.nyroma.bossmanagement.Rarety;
import xyz.nyroma.crafts.BetterCrafts;

import java.util.Random;

public class BossListeners implements Listener {
    public static boolean activated = true;

    @EventHandler
    public void onCreatureSpawn(EntitySpawnEvent e) {
        if (
                (e.getEntity() instanceof Creeper || e.getEntity() instanceof Zombie || e.getEntity() instanceof Skeleton || e.getEntity() instanceof Spider)
                && !isBoss(e.getEntity()) && activated
        ) {
            Location loc = e.getEntity().getLocation();
            if(new Random().nextInt(200) == 123){
                System.out.println("Un boss a spawn en " + loc.getX() + "/" + loc.getY() + "/" + loc.getZ());
                BossSummoners.summonNormalBoss(e.getEntity().getType(), loc);
                e.setCancelled(true);
            } else if(new Random().nextInt(4096) == 1234){
                BossSummoners.summonSupremeBoss(loc);
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onHurt(EntityDamageEvent e) {
        if (isBoss(e.getEntity())) {
            if (e.getCause() == EntityDamageEvent.DamageCause.CRAMMING
                    || e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION
                    || e.getCause() == EntityDamageEvent.DamageCause.DROWNING
                    || e.getCause() == EntityDamageEvent.DamageCause.LAVA
                    || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE
                    || e.getCause() == EntityDamageEvent.DamageCause.FIRE) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            if (isBoss(e.getEntity())) {
                Monster monster = (Monster) e.getEntity();
                if(monster.getMaxHealth() >= 1000 && monster.getMaxHealth() <= 2000){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 2));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 10));
                    if(new Random().nextInt(10) == 5){
                        monster.getWorld().spawnEntity(monster.getLocation(), monster.getType());
                    }
                } else {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 5));
                    if(new Random().nextInt(20) == 5){
                        monster.getWorld().spawnEntity(monster.getLocation(), monster.getType());
                    }
                }
                p.spawnParticle(Particle.FLAME, monster.getLocation(),50);
                p.playSound(monster.getLocation(), Sound.ENTITY_GHAST_HURT, 100, 1);
                if(new Random().nextInt(10) == 1){
                    monster.teleport(p);
                }
            }
            if (e.getEntity() instanceof Creature) {
                ItemStack iteminMain = p.getInventory().getItemInMainHand();
                Creature ent = (Creature) e.getEntity();
                if (isLoot(iteminMain, BetterCrafts.getHealther())) {
                    p.sendMessage(ChatColor.GREEN + "Mob : " + ent.getType().toString() + ", Vie restante : " + (float) ent.getHealth() + " HP");
                }
                if(isLoot(iteminMain, BossLoots.getHungerSword())) {
                    ent.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 4));
                }
                if(isLoot(iteminMain, BossLoots.getPoisonSword())) {
                    ent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 4));
                }
                if(isLoot(iteminMain, BossLoots.getWitherSword())) {
                    ent.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 4));
                }
            }
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e){
        if (e.getItem().isSimilar(BossLoots.getSatur())) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 4 * 3600 * 20, 1));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            Player p = e.getPlayer();
            Block b = e.getClickedBlock();
            if(isLoot(p.getInventory().getItemInMainHand(), BossLoots.getThunderStick())){
                b.getWorld().strikeLightning(b.getLocation());
            }
            if(isLoot(p.getInventory().getItemInMainHand(), BossLoots.getFarmerHoe())){
                if(b instanceof Ageable){
                    Ageable ageable = (Ageable) b;
                    ageable.setAge(ageable.getMaximumAge());
                    b.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, b.getLocation(), 100);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if(e.getEntity() instanceof Monster){
            Monster monster = (Monster) e.getEntity();
            if (isBoss(monster)) {
                ExperienceOrb orb = (ExperienceOrb) monster.getWorld().spawnEntity(monster.getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(1500);
                switch(monster.getType()){
                    case CREEPER:
                        monster.getWorld().dropItemNaturally(monster.getLocation(), BossLoots.getSpecificBossLoot(BossType.CREEPER, monster.getMaxHealth() >= 1000 ? Rarety.RARE : Rarety.COMMON));
                        break;
                    case ZOMBIE:
                        monster.getWorld().dropItemNaturally(monster.getLocation(), BossLoots.getSpecificBossLoot(BossType.ZOMBIE, monster.getMaxHealth() >= 1000 ? Rarety.RARE : Rarety.COMMON));
                        break;
                    case SKELETON:
                        monster.getWorld().dropItemNaturally(monster.getLocation(), BossLoots.getSpecificBossLoot(BossType.SKELETON, monster.getMaxHealth() >= 1000 ? Rarety.RARE : Rarety.COMMON));
                        break;
                    case SPIDER:
                        monster.getWorld().dropItemNaturally(monster.getLocation(), BossLoots.getSpecificBossLoot(BossType.SPIDER, monster.getMaxHealth() >= 1000 ? Rarety.RARE : Rarety.COMMON));
                        break;
                    case PHANTOM:
                        monster.getWorld().dropItemNaturally(monster.getLocation(), BossLoots.getSpecificBossLoot(BossType.PHANTOM, monster.getMaxHealth() >= 1000 ? Rarety.RARE : Rarety.COMMON));
                        break;
                    case WITHER_SKELETON:
                        monster.getWorld().dropItemNaturally(monster.getLocation(), BossLoots.getGenericLoot(Rarety.SUPREME));
                        break;
                    default: monster.getWorld().dropItemNaturally(monster.getLocation(), BossLoots.getGenericLoot(Rarety.COMMON));
                }
                for (Entity ent : monster.getNearbyEntities(20, 20, 20)) {
                    if (ent instanceof Player) {
                        Player p = (Player) ent;
                        p.playSound(monster.getLocation(), Sound.ENTITY_WITHER_DEATH, 100, 1);
                    }
                }
                Bukkit.broadcastMessage(ChatColor.GREEN + "Un boss a été tué.");
            }
        }
    }

    private boolean isBoss(Entity entity) {
        return entity.getCustomName() != null && entity instanceof Monster &&
                (entity.getCustomName().equals(ChatColor.RED + "Boss creeper") || entity.getCustomName().equals(ChatColor.RED + "Boss zombie") ||
                        entity.getCustomName().equals(ChatColor.RED + "Boss spider") || entity.getCustomName().equals(ChatColor.RED + "Boss skeleton") ||
                        entity.getCustomName().equals(ChatColor.RED + "Boss") || entity.getCustomName().equals(ChatColor.BLACK + "Boss suprême")
                );
    }

    private boolean isLoot(ItemStack itemToCompare, ItemStack loot){
        return itemToCompare.getItemMeta() != null && itemToCompare.getItemMeta().getLore() != null &&
                itemToCompare.getItemMeta().getLore().equals(loot.getItemMeta().getLore()) &&
                itemToCompare.getItemMeta().getDisplayName().equals(loot.getItemMeta().getDisplayName());
    }

}
