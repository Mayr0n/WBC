package xyz.nyroma.betteritems;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class BetterListeners implements Listener {

    @EventHandler
    public void betterTools(BlockBreakEvent e){
        Player p = e.getPlayer();
        ItemStack is = p.getInventory().getItemInMainHand();
        Block b = e.getBlock();
        if(is.hasItemMeta() && is.getItemMeta() != null){
            ItemMeta im = is.getItemMeta();
            if(im.hasLore() && im.getLore() != null && im.getLore().size() > 0){
                BetterTool[] bt = new BetterTool[]{new BetterTool(BetterTools.IRONER), new BetterTool(BetterTools.GOLDER), new BetterTool(BetterTools.STRINGER)};
                for(BetterTool be : bt){
                    for(int i = 1 ; i <= be.getPourcentages().size() ; i++){
                        ItemStack tool = be.getItemStack(i);
                        if(im.getLore().equals(tool.getItemMeta().getLore())){
                            for(Material mat : be.getDrops().keySet()){
                                if(b.getType().equals(mat)){
                                    e.setDropItems(false);
                                    int pourcentage = be.getPourcentages().get(i-1);
                                    if(pourcentage >= 100){
                                        for(int j = 1 ; j <= pourcentage/100 ; j++){
                                            b.getLocation().getWorld().dropItem(b.getLocation(), new ItemStack(be.getDrops().get(mat)));
                                        }
                                        return;
                                    } else {
                                        if(new Random().nextInt(100) < pourcentage){
                                            b.getLocation().getWorld().dropItem(b.getLocation(), new ItemStack(be.getDrops().get(mat)));
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
