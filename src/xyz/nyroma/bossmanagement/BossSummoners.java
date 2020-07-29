package xyz.nyroma.bossmanagement;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import sun.security.provider.ConfigFile;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BossSummoners {
    private static Random r = new Random();

    public static void summonNormalBoss(EntityType type, Location loc) {
        Monster monster = (Monster) loc.getWorld().spawnEntity(loc, type);
        monster.setCustomName(ChatColor.RED + "Boss");
        if (loc.getWorld() != null) {
            switch(type){
                case CREEPER:
                    Creeper creeper = (Creeper) monster;
                    creeper.setExplosionRadius(r.nextInt(5) + 5);
                    creeper.setPowered(true);
                    monster.setCustomName(ChatColor.RED + "Boss creeper");
                    break;
                case ZOMBIE:
                    Zombie zombie = (Zombie) monster;
                    zombie.setBaby(r.nextBoolean());
                    monster.setCustomName(ChatColor.RED + "Boss zombie");
                    break;
                case SKELETON:
                    monster.setCustomName(ChatColor.RED + "Boss skeleton");
                    break;
                case SPIDER:
                    monster.setCustomName(ChatColor.RED + "Boss spider");
                    break;
            }

            if(monster.getEquipment() != null) {
                monster.getEquipment().setHelmet(getArmorPiece(Material.DIAMOND_HELMET,r.nextInt(10) + 5, false));
                monster.getEquipment().setChestplate(getArmorPiece(Material.DIAMOND_CHESTPLATE,r.nextInt(10) + 5, false));
                monster.getEquipment().setLeggings(getArmorPiece(Material.DIAMOND_LEGGINGS,r.nextInt(10) + 5, false));
                monster.getEquipment().setBoots(getArmorPiece(Material.DIAMOND_BOOTS,r.nextInt(10) + 5, false));
                monster.getEquipment().setHelmetDropChance(0);
                monster.getEquipment().setChestplateDropChance(0);
                monster.getEquipment().setLeggingsDropChance(0);
                monster.getEquipment().setBootsDropChance(0);
                monster.getEquipment().setItemInMainHand(getSword(Material.NETHERITE_SWORD,r.nextInt(5) + 5));
                monster.getEquipment().setItemInMainHandDropChance(0.00012207f);
                monster.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
                monster.getEquipment().setItemInOffHandDropChance(0);
            }
            float hp = r.nextInt(1000) + 500;
            monster.setMaxHealth(hp);
            monster.setHealth(hp);
            monster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0));
            monster.setCanPickupItems(true);

            for (Entity ent : monster.getNearbyEntities(50, 50, 50)) {
                if (ent instanceof Player) {
                    Player p = (Player) ent;
                    p.sendMessage(ChatColor.RED + "Un boss est apparu à moins de 50 blocks de vous.");
                    p.playSound(monster.getLocation(), Sound.ENTITY_WITHER_SPAWN, 100, 100);
                }
            }
        }
    }
    public static void summonSupremeBoss(Location loc) {
        Monster monster = (Monster) loc.getWorld().spawnEntity(loc, EntityType.WITHER_SKELETON);
        monster.setCustomName(ChatColor.BLACK + "Boss suprême");
        if (loc.getWorld() != null) {
            if(monster.getEquipment() != null) {
                monster.getEquipment().setHelmet(getArmorPiece(Material.NETHERITE_HELMET,10, true));
                monster.getEquipment().setChestplate(getArmorPiece(Material.NETHERITE_CHESTPLATE,10, true));
                monster.getEquipment().setLeggings(getArmorPiece(Material.NETHERITE_LEGGINGS,10, true));
                monster.getEquipment().setBoots(getArmorPiece(Material.NETHERITE_BOOTS,10, true));
                monster.getEquipment().setHelmetDropChance(0);
                monster.getEquipment().setChestplateDropChance(0);
                monster.getEquipment().setLeggingsDropChance(0);
                monster.getEquipment().setBootsDropChance(0);
                monster.getEquipment().setItemInMainHand(getSword(Material.NETHERITE_SWORD, 10));
                monster.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
                monster.getEquipment().setItemInOffHandDropChance(0);
            }
            monster.setMaxHealth(5000);
            monster.setHealth(5000);
            monster.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 0));
            monster.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999, 0));
            monster.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 99999, 1));

            for (Entity ent : monster.getNearbyEntities(50, 50, 50)) {
                if (ent instanceof Player) {
                    Player p = (Player) ent;
                    p.sendMessage(ChatColor.BLACK + "Un boss suprême est apparu à moins de 50 blocks de vous. Conseil. Fuyez.");
                    p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 100, 100);
                    p.playSound(monster.getLocation(), Sound.ENTITY_WITHER_SPAWN, 100, 100);
                }
            }
        }
    }

    private static ItemStack getArmorPiece(Material piece, int level, boolean maxed){
        ItemStack h = new ItemStack(piece);
        ItemMeta hm = h.getItemMeta();
        hm.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, level, true);
        hm.addEnchant(Enchantment.THORNS, level, true);
        if(maxed){
            hm.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, level, true);
            hm.addEnchant(Enchantment.PROTECTION_FIRE, level, true);
            hm.addEnchant(Enchantment.PROTECTION_PROJECTILE, level, true);
        }
        hm.setUnbreakable(true);
        h.setItemMeta(hm);
        return h;
    }

    private static ItemStack getSword(Material sword, int level) {
        ItemStack sw = new ItemStack(sword);
        ItemMeta swm = sw.getItemMeta();
        swm.addEnchant(Enchantment.DAMAGE_ALL, level, true);
        swm.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        swm.addEnchant(Enchantment.KNOCKBACK, 2, true);
        sw.setItemMeta(swm);
        return sw;
    }
}
