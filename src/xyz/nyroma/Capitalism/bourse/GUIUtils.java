package xyz.nyroma.Capitalism.bourse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nyroma.banks.Bank;
import xyz.nyroma.banks.BankCache;
import xyz.nyroma.bourseAPI.Category;
import xyz.nyroma.bourseAPI.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GUIUtils {

    public static ItemStack getBankAmount(Player p){
        ItemStack is = new ItemStack(Material.PAPER);
        ItemMeta im = is.getItemMeta();
        Bank bank = BankCache.get(p.getName());
        im.setDisplayName(ChatColor.DARK_GREEN + "Montant de votre banque :");
        im.addEnchant(Enchantment.LOYALTY, 3, true);
        im.setLore(Arrays.asList(ChatColor.GREEN + String.valueOf(bank.getAmount()) + " Nyr"));
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getQuit(){
        ItemStack is = new ItemStack(Material.ARROW);
        ItemMeta im = is.getItemMeta();
        im.addEnchant(Enchantment.KNOCKBACK, 10, false);
        im.setDisplayName(ChatColor.DARK_GREEN + "Quitter");
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack getCategoryIcon(Category category){
        Material mat = Material.valueOf(category.getIcon());
        ItemStack is = new ItemStack(mat);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + String.valueOf(category.getID()));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GREEN + "Nom :");
        lore.add(ChatColor.GREEN + category.getName());
        lore.add(ChatColor.DARK_GREEN + "Items à vendre :");
        for(Item item : category.getItems()){
            lore.add(ChatColor.GREEN + item.getProduct());
        }
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

}
