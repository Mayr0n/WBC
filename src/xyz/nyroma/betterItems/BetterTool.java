package xyz.nyroma.betterItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class BetterTool {
    private String name;
    private String type = "HOE";
    private String drop;
    private ChatColor lowTier = ChatColor.RED;
    private ChatColor highTier = ChatColor.DARK_RED;
    private List<Integer> pourcentages;
    private Hashtable<Material, Material> drops = new Hashtable<>();

    public BetterTool(BetterTools bi){
        switch(bi){
            case IRONER:
                this.type = "PICKAXE";
                this.drop = "une pépite de fer";
                this.name = "Ironer";
                this.lowTier = ChatColor.GRAY;
                this.highTier = ChatColor.DARK_GRAY;
                this.pourcentages = Arrays.asList(25,50,100,200,500);
                this.drops.put(Material.COBBLESTONE, Material.IRON_NUGGET);
                break;
            case GOLDER:
                this.type = "SHOVEL";
                this.drop = "une pépite d'or";
                this.name = "Golder";
                this.lowTier = ChatColor.YELLOW;
                this.highTier = ChatColor.GOLD;
                this.pourcentages = Arrays.asList(10,20,50,100,200);
                this.drops.put(Material.SAND, Material.GOLD_NUGGET);
                break;
            case STRINGER:
                this.type = "HOE";
                this.drop = "une ficelle";
                this.name = "Stringer";
                this.lowTier = ChatColor.WHITE;
                this.highTier = ChatColor.WHITE;
                this.pourcentages = Arrays.asList(25,50,100,200,500);
                this.drops.put(Material.OAK_LEAVES, Material.STRING);
                break;
        }
    }

    private static List<String> getLore(String intro, String leftClick, String rightClick){
        String intr = intro != null ? org.bukkit.ChatColor.GREEN + intro : "";
        String left = leftClick != null ? org.bukkit.ChatColor.DARK_AQUA + "Clic gauche : " + org.bukkit.ChatColor.AQUA + leftClick : "";
        String right = rightClick != null ? org.bukkit.ChatColor.DARK_AQUA + "Clic droit : " + org.bukkit.ChatColor.AQUA + rightClick : "";
        List<String> lore = new ArrayList<>();

        if(!intr.equals("")) {
            lore.add(" ");
            lore.add(intr);
            lore.add(" ");
        }
        if(!left.equals("")) lore.add(left);
        if(!right.equals("")) lore.add(right);

        return lore;
    }

    public ItemStack getItemStack(int level){
        String iName = "";
        Material mat = Material.valueOf("WOODEN_" + type);
        List<String> lore = Arrays.asList(ChatColor.RED + "Ça, tu n'es pas censé l'avoir !");

        switch(level){
            case 1:
                mat = Material.valueOf("STONE_" + type);
                iName = lowTier + name + " [I]";
                lore = getLore(ChatColor.GREEN + "-= Tier I =-", "possède " + pourcentages.get(0) + "% de chance de drop " + drop, null);
                break;
            case 2:
                mat = Material.valueOf("GOLDEN_" + type);
                iName = lowTier + name + " [II]";
                lore = getLore(ChatColor.YELLOW + "-= Tier II =-", "possède " + pourcentages.get(1) + "% de chance de drop " + drop, null);
                break;
            case 3:
                mat = Material.valueOf("IRON_" + type);
                iName = highTier + name + " [III]";
                lore = getLore(ChatColor.GOLD + "-= Tier III =-", "possède " + pourcentages.get(2) + "% de chance de drop " + drop, null);
                break;
            case 4:
                mat = Material.valueOf("DIAMOND_" + type);
                iName = highTier + name + " [IV]";
                lore = getLore(ChatColor.RED + "-= Tier IV =-", "possède " + pourcentages.get(3) + "% de chance de drop une " + drop, null);
                break;
            case 5:
                mat = Material.valueOf("DIAMOND_" + type);
                iName = highTier + name + " [V]";
                lore = getLore(ChatColor.DARK_RED + "-= Tier V =-", "possède " + pourcentages.get(4) + "% de chance de drop une " + drop, null);
                break;
        }
        ItemStack is = new ItemStack(mat);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(iName);
        im.setLore(lore);
        is.setItemMeta(im);

        return is;
    }

    public List<Integer> getPourcentages() {
        return pourcentages;
    }

    public Hashtable<Material, Material> getDrops() {
        return drops;
    }
}
