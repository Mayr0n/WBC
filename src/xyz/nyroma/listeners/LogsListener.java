package xyz.nyroma.listeners;

import com.google.common.collect.HashMultimap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nyroma.main.MainUtils;

import java.io.*;
import java.util.Arrays;

public class LogsListener implements Listener {
    private static File brokenF = new File("data/blocks/broken.txt");
    private static File placedF = new File("data/blocks/placed.txt");
    private ItemStack lookTool = getLookTool();
    private static HashMultimap<String, String> broken = HashMultimap.create(); //Location, Material & Joueur & Date
    private static HashMultimap<String, String> placed = HashMultimap.create(); //Location, Material & Joueur & Date

    public static ItemStack getLookTool(){
        ItemStack b = new ItemStack(Material.BEDROCK);
        ItemMeta meta = b.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        meta.setLore(Arrays.asList("Pour observer les logs des blocks :O"));
        meta.setLocalizedName("LookTool");
        meta.setDisplayName("LookTool");
        meta.setUnbreakable(true);
        b.setItemMeta(meta);
        return b;
    }

    public LogsListener(JavaPlugin plugin) {
        MainUtils.testFolderExist(new File("data/"));
        MainUtils.testFolderExist(new File("data/blocks/"));
        try {
            MainUtils.testFileExist(brokenF);
            MainUtils.testFileExist(placedF);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println("Enregistrement des logs de blocks...");
                serializeAll();
                System.out.println("Logs des blocks enregistrés !");
            }
        }.runTaskTimer(plugin, 10 * 60 * 20L, 10 * 60 * 20L);
    }

    public static void serializeAll(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(brokenF)));
            oos.writeObject(broken);
            oos.close();

            ObjectOutputStream oos2 = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(placedF)));
            oos2.writeObject(placed);
            oos2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setup(){
        deserialize(brokenF, broken);
        deserialize(placedF, placed);
    }

    public static void deserialize(File file, HashMultimap<String, String> log){
        try {
            MainUtils.testFolderExist(file);
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            Object o = ois.readObject();
            ois.close();
            log = (HashMultimap<String, String>) o;

        } catch(EOFException ignored){
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String[] getRegister(Block b, Player p){
        return new String[]{
                b.getLocation().toString(),
                b.getType().toString() + " / " + p.getName() + " / " + MainUtils.getDate("all")
        };
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        Block b = e.getBlockPlaced();
        Player p = e.getPlayer();

        //System.out.println(p.getInventory().getItemInMainHand().toString());

        placed.put(getRegister(e.getBlockPlaced(), e.getPlayer())[0], getRegister(e.getBlockPlaced(), e.getPlayer())[1]);

        if(b.getType().equals(Material.BEDROCK) && p.isOp()){
            sendLogs(p, b.getLocation());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack inHand = p.getInventory().getItemInMainHand();
        if(e.getAction().equals(Action.LEFT_CLICK_BLOCK) && inHand.equals(this.lookTool) && p.isOp()){
            sendLogs(p, e.getClickedBlock().getLocation());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        Block b = e.getBlock();
        Player p = e.getPlayer();

        broken.put(getRegister(e.getBlock(), e.getPlayer())[0], getRegister(e.getBlock(), e.getPlayer())[1]);

        if(b.getType().equals(Material.BEDROCK) && p.isOp()) {
            sendLogs(p, b.getLocation());
            e.setCancelled(true);
        }
    }

    private void sendLogs(Player p, Location loc){
        int current = 0;
        if(broken.size() != 0 && placed.size() != 0){
            p.sendMessage(ChatColor.RED + "----- Logs du block -----");
            p.sendMessage(ChatColor.DARK_RED + "<<< Blocks cassés >>>");
            for(String location : this.broken.keySet()){
                if(loc.toString().equals(location)){
                    for(String info : this.broken.get(location)){
                        if(current < 10) {
                            p.sendMessage(ChatColor.DARK_GREEN + info);
                            current++;
                        }
                    }
                }
            }
            current = 0;
            p.sendMessage(ChatColor.DARK_RED + "<<< Blocks placés >>>");
            for(String location : placed.keySet()){
                if(loc.toString().equals(location)){
                    for(String info : placed.get(location)){
                        if(current < 10) {
                            p.sendMessage(ChatColor.DARK_GREEN + info);
                            current++;
                        }
                    }
                }
            }
            p.sendMessage(ChatColor.RED + "-------------------------");
        } else {
            p.sendMessage(ChatColor.RED + "Il n'y a pas de logs pour ce block...");
        }
    }
}
