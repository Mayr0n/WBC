package xyz.nyroma.crafts;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nyroma.betteritems.BetterToolsRecipes;

import java.util.ArrayList;
import java.util.List;

public class CraftsManager {
    private JavaPlugin plugin;
    private Server server;
    public static List<NamespacedKey> namespacedKeys = new ArrayList<>();

    public CraftsManager(JavaPlugin plugin, Server server){
        this.plugin = plugin;
        this.server = server;
    }


    public void build(){
        for(ShapedRecipe r : new EggRecipes(plugin).build()){
            Bukkit.addRecipe(r);
        }
        for(ShapedRecipe r : new BetterToolsRecipes().getRecipes(plugin)){
            Bukkit.addRecipe(r);
        }
        for(ShapedRecipe r : new BetterCrafts(plugin).buildShapes()){
            Bukkit.addRecipe(r);
        }
        for(ShapelessRecipe r : new BetterCrafts(plugin).buildShapeless()){
            Bukkit.addRecipe(r);
        }
    }


    public static NamespacedKey getNamespacedkey(JavaPlugin plugin, String name){
        NamespacedKey nk = new NamespacedKey(plugin, name);
        namespacedKeys.add(nk);
        return nk;
    }
}
