package xyz.nyroma.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nyroma.banks.Bank;
import xyz.nyroma.banks.BankCache;
import xyz.nyroma.banks.Transaction;

public class BankListeners implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            Player p = e.getPlayer();
            ItemStack item = p.getInventory().getItemInMainHand();
            try {
                if (item.getType().equals(Material.PAPER) && item.hasItemMeta()) {
                    ItemMeta im = item.getItemMeta();
                    if (im.getLore().get(0).equals("Montant du billet :")) {
                        try {
                            if (item.containsEnchantment(Enchantment.LOYALTY)) {
                                Bank bank = BankCache.get(p.getName());
                                float amount = Float.parseFloat(im.getLore().get(1).split(" ")[0]);
                                bank.add(amount, Transaction.PLAYER_ADD);
                                if(p.getInventory().getItemInMainHand().getAmount() != 1){
                                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
                                } else {
                                    p.getInventory().setItemInMainHand(null);
                                }
                                p.sendMessage(ChatColor.GREEN + String.valueOf(amount) + " Nyr ont été ajoutés à votre compte.");
                            }
                        } catch (NumberFormatException | NullPointerException ee) {
                            p.sendMessage(ChatColor.RED + "Une erreur est survenue.");
                        }
                    }
                }
            } catch (NullPointerException ignored) {
            }
        }
    }

    @EventHandler
    public void onAdvance(PlayerAdvancementDoneEvent e){
        Bank bank = BankCache.get(e.getPlayer().getName());
        bank.add(5, Transaction.AUTO_ADD);
    }

}
