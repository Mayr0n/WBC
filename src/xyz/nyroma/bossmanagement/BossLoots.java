package xyz.nyroma.bossmanagement;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BossLoots {
    public static List<ItemStack> getAllLoots(){
        List<ItemStack> loots = new ArrayList<>();
        loots.addAll(BossType.CREEPER.getLoots().getLoots(Rarety.COMMON));
        loots.addAll(BossType.CREEPER.getLoots().getLoots(Rarety.RARE));
        loots.addAll(BossType.ZOMBIE.getLoots().getLoots(Rarety.COMMON));
        loots.addAll(BossType.ZOMBIE.getLoots().getLoots(Rarety.RARE));
        loots.addAll(BossType.SKELETON.getLoots().getLoots(Rarety.COMMON));
        loots.addAll(BossType.SKELETON.getLoots().getLoots(Rarety.RARE));
        loots.addAll(BossType.SPIDER.getLoots().getLoots(Rarety.COMMON));
        loots.addAll(BossType.SPIDER.getLoots().getLoots(Rarety.RARE));
        return loots;
    }
    public static ItemStack getGenericLoot(Rarety rarety){
        switch(rarety){
            case COMMON:
                switch(new Random().nextInt(3)){
                    case 0:
                        return getJumper();
                    case 1:
                        return new ItemStack(Material.DIAMOND, 2);
                    case 2:
                        return new ItemStack(Material.EMERALD_BLOCK, 4);
                }
                return new ItemStack(Material.GOLDEN_APPLE, 5);
            case RARE:
                switch(new Random().nextInt(3)){
                    case 0:
                        return new ItemStack(Material.ANCIENT_DEBRIS, 2);
                    case 1:
                        return new ItemStack(Material.WITHER_SKELETON_SKULL, 2);
                    case 2:
                        return new ItemStack(Material.DIAMOND_BLOCK, 2);
                }
                return new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
            case SUPREME:
                switch(new Random().nextInt(3)){
                    case 0:
                        return new ItemStack(Material.NETHERITE_BLOCK);
                    case 1:
                        return new ItemStack(Material.NETHER_STAR, 2);
                    case 2:
                        return new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 2);
                }
                return new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3);
        }
        return new ItemStack(Material.GOLDEN_APPLE, 3);
    }
    public static ItemStack getSpecificBossLoot(BossType type, Rarety rarety){
        return type.getLoots().getLoot(rarety);
    }

    public static ItemStack getThunderStick(){
        ItemStack it = new ItemStack(Material.BLAZE_ROD);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(ChatColor.YELLOW + "ThunderStick");
        im.addEnchant(Enchantment.DURABILITY, 10,true);
        im.setLore(Arrays.asList("Foudroyez tout ce que vous voulez avec ce baton !"));
        it.setItemMeta(im);
        return it;
    }

    public static ItemStack getSatur(){
        ItemStack it = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta im = it.getItemMeta();
        im.setDisplayName(ChatColor.DARK_PURPLE + "Festin");
        im.addEnchant(Enchantment.DURABILITY, 10,true);
        im.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "De quoi n'avoir jamais faim !"));
        it.setItemMeta(im);
        return it;
    }

    public static ItemStack getPouf(){
        ItemStack is = new ItemStack(Material.BOW);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.DARK_AQUA + "Pouf+");
        im.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
        im.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        im.addEnchant(Enchantment.ARROW_FIRE, 10, true);
        im.addEnchant(Enchantment.ARROW_KNOCKBACK, 5, true);
        im.addEnchant(Enchantment.DURABILITY, 10, true);
        im.setLore(Arrays.asList(ChatColor.AQUA + "Pouf ! Envoyez vos adversaires dans l'arrière du décor avec ce magnifique arc."));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getHungerSword(){
        ItemStack is = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.DARK_AQUA + "Hunger Sword");
        im.addEnchant(Enchantment.KNOCKBACK, 3, true);
        im.setLore(Arrays.asList(ChatColor.AQUA + "En tapant une entité avec cette épée vous lui donnerez hunger II !"));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getFarmerHoe(){
        ItemStack is = new ItemStack(Material.NETHERITE_HOE);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.DARK_GREEN + "Farmer's hoe");
        im.addEnchant(Enchantment.DURABILITY, 10, true);
        im.setLore(Arrays.asList(ChatColor.GREEN + "Une houe dédiée aux farmers les plus avancés !"));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getPoisonSword(){
        ItemStack is = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.DARK_GREEN + "Poison Sword");
        im.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        im.setLore(Arrays.asList(ChatColor.GREEN + "En tapant une entité avec cette épée vous lui donnerez poison II !"));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getWitherSword(){
        ItemStack is = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.BLACK + "Wither Sword");
        im.addEnchant(Enchantment.DAMAGE_ALL, 7, true);
        im.setLore(Arrays.asList(ChatColor.BLACK + "En tapant une entité avec cette épée vous lui donnerez wither II !"));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getSpeeder(){
        ItemStack is = new ItemStack(Material.SUGAR);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.WHITE + "Speeder");
        im.addEnchant(Enchantment.DURABILITY, 10,true);
        im.setLore(Arrays.asList(ChatColor.WHITE + "Prenez cet item dans l'inventaire pour obtenir un bonus de vitesse !"));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getStrengthener(){
        ItemStack is = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.DARK_RED + "Strengthener");
        im.addEnchant(Enchantment.DURABILITY, 10,true);
        im.setLore(Arrays.asList(ChatColor.RED + "Prenez cet item dans l'inventaire pour obtenir un bonus de force !"));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getJumper(){
        ItemStack is = new ItemStack(Material.RABBIT_FOOT);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + "Jumper");
        im.addEnchant(Enchantment.DURABILITY, 10,true);
        im.setLore(Arrays.asList(ChatColor.GREEN + "Prenez cet item dans l'inventaire pour pouvoir sauter plus haut !"));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getIRvision(){
        ItemStack is = new ItemStack(Material.GOLDEN_CARROT);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.DARK_BLUE + "Nighter");
        im.addEnchant(Enchantment.DURABILITY, 10,true);
        im.setLore(Arrays.asList(ChatColor.BLUE + "Prenez cet item dans l'inventaire pour pouvoir voir dans la nuit !"));
        is.setItemMeta(im);
        return is;
    }
}
