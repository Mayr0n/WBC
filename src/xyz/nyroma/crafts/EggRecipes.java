package xyz.nyroma.crafts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class EggRecipes {
    private JavaPlugin plugin;

    public EggRecipes(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public List<ShapedRecipe> build(){
        return Arrays.asList(
                harderSpawnRecipe(Material.DIRT, Material.LEATHER, Material.GOLD_INGOT, "cowegg", Material.COW_SPAWN_EGG),
                basicSpawnRecipe(Material.WHITE_WOOL, "sheepegg", Material.SHEEP_SPAWN_EGG),
                basicSpawnRecipe(Material.WHEAT_SEEDS, "chickegg", Material.CHICKEN_SPAWN_EGG),
                harderSpawnRecipe(Material.LEATHER, Material.SUGAR, Material.WATER_BUCKET, "dolphinegg", Material.DOLPHIN_SPAWN_EGG),
                harderSpawnRecipe(Material.CARROT, Material.LEATHER, Material.IRON_INGOT, "rabbitegg", Material.RABBIT_SPAWN_EGG),
                harderSpawnRecipe(Material.BEETROOT, Material.LEATHER, Material.IRON_INGOT, "pigegg", Material.PIG_SPAWN_EGG),
                harderSpawnRecipe(Material.LEATHER, Material.HAY_BLOCK, Material.GOLD_INGOT, "horsegg", Material.HORSE_SPAWN_EGG),
                basicSpawnRecipe(Material.BLACK_DYE, "squidegg", Material.SQUID_SPAWN_EGG),
                harderSpawnRecipe(Material.FEATHER, Material.VINE, Material.FEATHER, "parrotegg", Material.PARROT_SPAWN_EGG),
                harderSpawnRecipe(Material.LEATHER, Material.BONE_MEAL, Material.GOLD_INGOT, "lamaegg", Material.LLAMA_SPAWN_EGG),
                harderSpawnRecipe(Material.SNOW_BLOCK, Material.ICE, Material.COD, "polaregg", Material.POLAR_BEAR_SPAWN_EGG),
                harderSpawnRecipe(Material.BAMBOO, Material.WHITE_WOOL, Material.BLACK_WOOL, "pandegg", Material.PANDA_SPAWN_EGG),
                harderSpawnRecipe(Material.LEATHER, Material.COD, Material.VINE, "ocelotegg", Material.OCELOT_SPAWN_EGG),
                harderSpawnRecipe(Material.DANDELION, Material.ROSE_BUSH, Material.LEATHER, "beeegg", Material.BEE_SPAWN_EGG),
                harderSpawnRecipe(Material.BONE, Material.ROTTEN_FLESH, Material.GOLD_INGOT, "wolfegg", Material.WOLF_SPAWN_EGG),
                harderSpawnRecipe(Material.SEAGRASS, Material.BONE_BLOCK, Material.IRON_INGOT, "turtlegg", Material.TURTLE_SPAWN_EGG),
                coronavirusEgg(),
                getVillagerEggRecipe()
        );
    }

    private ShapedRecipe getVillagerEggRecipe() {

        ItemStack r = new ItemStack(Material.VILLAGER_SPAWN_EGG);
        ShapedRecipe rr = new ShapedRecipe(CraftsManager.getNamespacedkey(plugin, "villageregg"), r);

        rr.shape("aba", "def", "ihi");
        rr.setIngredient('i', Material.DIAMOND);
        rr.setIngredient('a', Material.HAY_BLOCK);
        rr.setIngredient('d', Material.LEATHER_CHESTPLATE);
        rr.setIngredient('b', Material.LEATHER_LEGGINGS);
        rr.setIngredient('f', Material.LEATHER_BOOTS);
        rr.setIngredient('e', Material.EGG);
        rr.setIngredient('h', Material.EMERALD_BLOCK);


        return rr;
    }

    private ShapedRecipe basicSpawnRecipe(Material mat, String name, Material item){
        ItemStack i = new ItemStack(item);
        ShapedRecipe r = new ShapedRecipe(CraftsManager.getNamespacedkey(plugin, name), i);
        r.shape("aaa","aba","aaa");

        r.setIngredient('a', mat);
        r.setIngredient('b', Material.EGG);

        return r;
    }
    private ShapedRecipe harderSpawnRecipe(Material m1, Material m2, Material m3, String name, Material item){
        ItemStack i = new ItemStack(item);
        ShapedRecipe r = new ShapedRecipe(CraftsManager.getNamespacedkey(plugin, name), i);
        r.shape("aba","aca","ada");

        r.setIngredient('a', m1);
        r.setIngredient('b', m2);
        r.setIngredient('c', Material.EGG);
        r.setIngredient('d', m3);

        return r;
    }
    private ShapedRecipe coronavirusEgg(){
        ItemStack i = new ItemStack(Material.BAT_SPAWN_EGG);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName("Coronavirus en boîte");
        im.setLore(Arrays.asList("Aucune garantie anti-virus !", "/!\\ NE PAS MANGER"));
        i.setItemMeta(im);
        ShapedRecipe r = new ShapedRecipe(CraftsManager.getNamespacedkey(plugin, "bategg"), i);
        r.shape("aba","aca","ada");

        r.setIngredient('a', Material.RABBIT_HIDE);
        r.setIngredient('b', Material.SPIDER_EYE);
        r.setIngredient('c', Material.EGG);
        r.setIngredient('d', Material.FEATHER);

        return r;
    }

}
