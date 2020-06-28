package xyz.nyroma.betterItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nyroma.crafts.CraftsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BetterToolsRecipes {

    public static List<String> getLore(String intro, String leftClick, String rightClick){
        String intr = intro != null ? ChatColor.GREEN + intro : "";
        String left = leftClick != null ? ChatColor.DARK_AQUA + "Clic gauche : " + ChatColor.AQUA + leftClick : "";
        String right = rightClick != null ? ChatColor.DARK_AQUA + "Clic droit : " + ChatColor.AQUA + rightClick : "";
        List<String> lore = new ArrayList<>();

        if(!intr.equals("")) lore.add(intr);
        if(!left.equals("")) lore.add(left);
        if(!right.equals("")) lore.add(right);

        return lore;
    }

    public List<ShapedRecipe> getRecipes(JavaPlugin plugin){
        return Arrays.asList(
                ironerRecipe(Material.COBBLESTONE, new BetterTool(BetterTools.IRONER).getItemStack(1), plugin, "ir1"),
                ironerRecipe(Material.GOLD_INGOT, new BetterTool(BetterTools.IRONER).getItemStack(2), plugin, "ir2"),
                ironerRecipe(Material.IRON_INGOT, new BetterTool(BetterTools.IRONER).getItemStack(3), plugin, "ir3"),
                ironerRecipe(Material.DIAMOND, new BetterTool(BetterTools.IRONER).getItemStack(4), plugin, "ir4"),
                ironerRecipe(Material.NETHER_STAR, new BetterTool(BetterTools.IRONER).getItemStack(5), plugin, "ir5"),
                golderRecipe(Material.COBBLESTONE, new BetterTool(BetterTools.GOLDER).getItemStack(1), plugin, "go1"),
                golderRecipe(Material.GOLD_INGOT, new BetterTool(BetterTools.GOLDER).getItemStack(2), plugin, "go2"),
                golderRecipe(Material.IRON_INGOT, new BetterTool(BetterTools.GOLDER).getItemStack(3), plugin, "go3"),
                golderRecipe(Material.DIAMOND, new BetterTool(BetterTools.GOLDER).getItemStack(4), plugin, "go4"),
                golderRecipe(Material.NETHER_STAR, new BetterTool(BetterTools.GOLDER).getItemStack(5), plugin, "go5"),
                stringerRecipe(Material.COBBLESTONE, new BetterTool(BetterTools.STRINGER).getItemStack(1), plugin, "st1"),
                stringerRecipe(Material.GOLD_INGOT, new BetterTool(BetterTools.STRINGER).getItemStack(2), plugin, "st2"),
                stringerRecipe(Material.IRON_INGOT, new BetterTool(BetterTools.STRINGER).getItemStack(3), plugin, "st3"),
                stringerRecipe(Material.DIAMOND, new BetterTool(BetterTools.STRINGER).getItemStack(4), plugin, "st4"),
                stringerRecipe(Material.NETHER_STAR, new BetterTool(BetterTools.STRINGER).getItemStack(5), plugin, "st5")
        );
    }

    private ShapedRecipe ironerRecipe(Material material, ItemStack item, JavaPlugin plugin, String name) {
        ShapedRecipe ironer = new ShapedRecipe(CraftsManager.getNamespacedkey(plugin, name), item);

        ironer.shape("aaa", "aba", ".b.");
        ironer.setIngredient('a', material);
        ironer.setIngredient('b', Material.STICK);

        return ironer;
    }
    private ShapedRecipe stringerRecipe(Material material, ItemStack item, JavaPlugin plugin, String name) {
        ShapedRecipe stringer = new ShapedRecipe(CraftsManager.getNamespacedkey(plugin, name), item);

        stringer.shape("cab", "aab", "..b");
        stringer.setIngredient('a', material);
        stringer.setIngredient('b', Material.STICK);
        stringer.setIngredient('c', Material.SPIDER_EYE);

        return stringer;
    }
    private ShapedRecipe golderRecipe(Material material, ItemStack item, JavaPlugin plugin, String name) {
        ShapedRecipe golder = new ShapedRecipe(CraftsManager.getNamespacedkey(plugin, name), item);

        golder.shape("aaa", ".a.", ".b.");
        golder.setIngredient('a', material);
        golder.setIngredient('b', Material.STICK);

        return golder;
    }

}
