package xyz.nyroma.betteritems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class BetterArmor {
    private String name;
    private String type;
    private String effet;
    private PotionEffectType effect;
    private ChatColor lowTier = ChatColor.RED;
    private ChatColor highTier = ChatColor.DARK_RED;

    public BetterArmor(BetterArmors bi){
        switch(bi){
            case NIGHT_HELMET:
                this.name = ChatColor.DARK_BLUE + "Night glasses";
                this.type = "HELMET";
                this.effet = "permet un effet de night vision infini.";
                this.effect = PotionEffectType.NIGHT_VISION;
                this.lowTier = ChatColor.BLUE;
                this.highTier = ChatColor.DARK_BLUE;
                break;
            case WINGED_CHESTPLATE:
                this.name = ChatColor.WHITE + "Winged Chestplate";
                this.type = "CHESTPLATE";
                this.effet = "permet un effet de slow falling infini.";
                this.effect = PotionEffectType.SLOW_FALLING;
                this.lowTier = ChatColor.WHITE;
                this.highTier = ChatColor.WHITE;
                break;
            case SPEEDY_LEGGINGS:
                this.name = ChatColor.BLUE + "Speedy Leggings";
                this.type = "LEGGINGS";
                this.effet = "permet un effet de speed infini.";
                this.effect = PotionEffectType.SPEED;
                this.lowTier = ChatColor.BLUE;
                this.highTier = ChatColor.AQUA;
                break;
            case JUMPER_BOOTS:
                this.name = ChatColor.GREEN + "Jumper boots";
                this.type = "BOOTS";
                this.effet = "permet un effet de jump boost infini.";
                this.effect = PotionEffectType.JUMP;
                this.lowTier = ChatColor.GREEN;
                this.highTier = ChatColor.DARK_GREEN;
                break;
            case HEROES_CHESTPLATE:
                this.name = ChatColor.GREEN + "Heroes' chestplate";
                this.type = "CHESTPLATE";
                this.effet = "permet un effet \"Héros du village\" infini.";
                this.effect = PotionEffectType.HERO_OF_THE_VILLAGE;
                this.lowTier = ChatColor.GREEN;
                this.highTier = ChatColor.DARK_GREEN;
                break;
            case XRAY_HELMET:
                this.name = ChatColor.GREEN + "X-ray helmet";
                this.type = "HELMET";
                this.effet = "permet de voir les mobs à travers les blocks.";
                this.effect = PotionEffectType.GLOWING;
                this.lowTier = ChatColor.GOLD;
                this.highTier = ChatColor.GOLD;
                break;
        }
    }

    private static List<String> getLore(String intro, String effet){
        return Arrays.asList(" ", intro, " ",ChatColor.DARK_AQUA + "Sur soi : " + ChatColor.AQUA + effet);
    }

    public ItemStack getItemStack(int level){
        String iName = "";
        Material mat = Material.valueOf("LEATHER_" + type);
        List<String> lore = Arrays.asList(ChatColor.RED + "Ça, tu n'es pas censé l'avoir !");

        switch(level){
            case 1:
                mat = Material.valueOf("LEATHER_" + type);
                iName = lowTier + this.name + " [I]";
                lore = getLore(ChatColor.GREEN + "-= Tier I =-", effet);
                break;
            case 2:
                mat = Material.valueOf("GOLDEN_" + type);
                iName = lowTier + this.name + " [II]";
                lore = getLore(ChatColor.YELLOW + "-= Tier II =-", effet);
                break;
            case 3:
                mat = Material.valueOf("IRON_" + type);
                iName = highTier + this.name + " [III]";
                lore = getLore(ChatColor.GOLD + "-= Tier III =-", effet);
                break;
            case 4:
                mat = Material.valueOf("DIAMOND_" + type);
                iName = highTier + this.name + " [IV]";
                lore = getLore(ChatColor.RED + "-= Tier IV =-", effet);
                break;
            case 5:
                mat = Material.valueOf("DIAMOND_" + type);
                iName = highTier + this.name + " [V]";
                lore = getLore(ChatColor.DARK_RED + "-= Tier V =-", effet);
                break;
        }
        ItemStack is = new ItemStack(mat);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(iName);
        im.setLore(lore);
        is.setItemMeta(im);

        return is;
    }

    public PotionEffectType getEffect() {
        return effect;
    }
}
