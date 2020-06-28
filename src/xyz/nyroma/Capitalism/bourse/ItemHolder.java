package xyz.nyroma.Capitalism.bourse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nyroma.bourseAPI.Category;
import xyz.nyroma.bourseAPI.Item;

import java.util.Arrays;

public class ItemHolder implements InventoryHolder {
    private Category category;
    private Player p;

    public ItemHolder(Category category, Player p){
        this.category = category;
        this.p = p;
    }


    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, category.getName());
        for(int s = 9 ; s <= 17 ; s++){
            inv.setItem(s, new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
        }

        inv.setItem(2, GUIUtils.getCategoryIcon(category));
        inv.setItem(6, GUIUtils.getBankAmount(p));
        inv.setItem(8, GUIUtils.getQuit());

        int slot = 18;

        if(category.getItems().size() > 0){
            for(Item item : category.getItems()){
                inv.setItem(slot, getItem(item));
                slot++;
            }
        }
        return inv;
    }

    private ItemStack getItem(Item item){
        Material mat = Material.valueOf(item.getProduct());
        ItemStack is = new ItemStack(mat);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + String.valueOf(item.getID()));
        im.setLore(Arrays.asList(
                ChatColor.DARK_GREEN + "Stocks restants :", ChatColor.GREEN + String.valueOf(item.getStocks()),
                ChatColor.DARK_GREEN + "Prix de référence :", ChatColor.GREEN + String.valueOf(item.getPrix()) + " Nyr")
        );
        is.setItemMeta(im);
        return is;
    }

}
