package xyz.nyroma.capitalism.bourse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nyroma.bourseAPI.BourseCache;
import xyz.nyroma.bourseAPI.Category;

public class CategoryHolder implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, "Bourse DiscUniverse");
        int slot = 20;

        inv.setItem(13, getPresentation());
        inv.setItem(53, GUIUtils.getQuit());

        int[] slots = {0,1,7,8,9,17,36,44,45,46,52};
        for(int s : slots){
            inv.setItem(s, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
        }

        if(BourseCache.categories.size() > 0){
            for(Category category : BourseCache.categories){
                if(slot == 25 || slot == 34){
                    slot += 4;
                }
                inv.setItem(slot, GUIUtils.getCategoryIcon(category));
                slot++;
            }
        }
        return inv;
    }

    private ItemStack getPresentation(){
        ItemStack is = new ItemStack(Material.PAPER);
        ItemMeta im = is.getItemMeta();
        im.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 10, false);
        im.setDisplayName(ChatColor.DARK_GREEN + "Catégories");
        is.setItemMeta(im);
        return is;
    }



}
