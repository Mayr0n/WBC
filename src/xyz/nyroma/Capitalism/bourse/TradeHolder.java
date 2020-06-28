package xyz.nyroma.Capitalism.bourse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nyroma.bourseAPI.Item;

import java.util.Arrays;

public class TradeHolder implements InventoryHolder {
    private Item item;
    private Player p;

    public TradeHolder(Item item, Player p){
        this.item = item;
        this.p = p;
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "Trade");

        inv.setItem(10, getItem(item, 0));
        inv.setItem(12, getItem(item, 8));
        inv.setItem(14, getItem(item, 16));
        inv.setItem(16, getItem(item, 64));

        inv.setItem(4, GUIUtils.getBankAmount(p));
        inv.setItem(22, GUIUtils.getQuit());
        return inv;
    }

    private ItemStack getItem(Item item, int delta){
        Material mat = Material.valueOf(item.getProduct());
        ItemStack is = delta == 0 ? new ItemStack(mat) : new ItemStack(mat, delta);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + String.valueOf(item.getID()));
        im.setLore(Arrays.asList(
                ChatColor.DARK_GREEN + "Stocks restants :", ChatColor.GREEN + String.valueOf(item.getStocks()),
                ChatColor.DARK_GREEN + "Prix d'achat :", ChatColor.GREEN + String.valueOf(item.getBuyPrice(delta)) + " Nyr",
                ChatColor.DARK_GREEN + "Prix de vente :", ChatColor.GREEN + String.valueOf(item.getSellPrice(delta)) + " Nyr")
        );
        is.setItemMeta(im);
        return is;
    }
}
