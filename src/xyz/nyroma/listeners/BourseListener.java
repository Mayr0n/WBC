package xyz.nyroma.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nyroma.capitalism.bourse.CategoryHolder;
import xyz.nyroma.capitalism.bourse.GUIUtils;
import xyz.nyroma.capitalism.bourse.ItemHolder;
import xyz.nyroma.capitalism.bourse.TradeHolder;
import xyz.nyroma.banks.Bank;
import xyz.nyroma.banks.BankCache;
import xyz.nyroma.banks.Transaction;
import xyz.nyroma.bourseAPI.BourseCache;
import xyz.nyroma.bourseAPI.Item;

public class BourseListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e){
        Player p = (Player) e.getView().getPlayer();
        if(e.getInventory().getHolder() instanceof CategoryHolder && e.getCurrentItem() != null && e.getClick() == ClickType.LEFT){
            ItemStack is = e.getCurrentItem();
            if(is.equals(GUIUtils.getQuit())){
                p.closeInventory();
            } else {
                ItemMeta im = is.getItemMeta();
                if (is.getType() != Material.AIR && im != null) {
                    if (BourseCache.getCategory(is.getType().toString()).isPresent()) {
                        p.openInventory(new ItemHolder(BourseCache.getCategory(is.getType().toString()).get(), p).getInventory());
                    } else {
                        p.sendMessage(ChatColor.RED + "Une erreur est survenue !");
                    }
                }
            }
        }
        else if(e.getInventory().getHolder() instanceof TradeHolder && e.getCurrentItem() != null && (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT)){
            ItemStack is = e.getCurrentItem();
            if(is.equals(GUIUtils.getQuit())){
                p.closeInventory();
                p.openInventory(new CategoryHolder().getInventory());
            } else {
                if (BourseCache.getItem(is.getType().toString()).isPresent() && is.getType() != Material.AIR) {
                    Item bourse = BourseCache.getItem(is.getType().toString()).get();
                    Bank bank = BankCache.get(p.getName());
                    ItemStack itemStack = new ItemStack(Material.valueOf(bourse.getProduct()), is.getAmount());

                    int delta = itemStack.getAmount() == 1 ? 0 : itemStack.getAmount();
                    float price;

                    if (e.getClick() == ClickType.LEFT) {
                        price = bourse.getBuyPrice(delta);
                        if(bourse.getStocks() > 0) {
                            if (bank.getAmount() >= price) {
                                if (bourse.buy(delta)) {
                                    if(delta == 0){
                                        bourse.setStocks(bourse.getStocks()-1);
                                    }
                                    bank.remove(price, Transaction.BOURSE_REMOVE);
                                    p.getInventory().addItem(itemStack);
                                    p.sendMessage(ChatColor.GREEN + String.valueOf(price) + " Nyromarks ont été débités de votre compte.");
                                } else {
                                    p.sendMessage(ChatColor.RED + "Il n'y a plus de stocks pour cet item.");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "Vous n'avez pas l'argent nécessaire.");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Impossible d'acheter, il n'y a plus de stocks.");
                        }
                    } else if (e.getClick() == ClickType.RIGHT) {
                        boolean sold = false;
                        int amount = itemStack.getAmount() == 1 ? 0 : itemStack.getAmount();
                        price = bourse.getSellPrice(amount);
                        for (int i = 0; i < p.getInventory().getContents().length; i++) {
                            ItemStack item = p.getInventory().getContents()[i];
                            if (item != null) {
                                if (item.getType() == itemStack.getType() && item.getAmount() >= itemStack.getAmount()) {
                                    bank.add(price, Transaction.BOURSE_ADD);
                                    if (item.getAmount() > itemStack.getAmount()) {
                                        p.getInventory().getContents()[i].setAmount(item.getAmount() - itemStack.getAmount());
                                    } else {
                                        p.getInventory().getContents()[i].setAmount(0);
                                    }
                                    sold = true;
                                    break;
                                }
                            }
                        }
                        if (sold) {
                            p.sendMessage(ChatColor.GREEN + "L'item a été vendu " + price + " Nyromarks.");
                            bourse.sell(itemStack.getAmount());
                            p.closeInventory();
                            p.openInventory(new TradeHolder(bourse, p).getInventory());
                        } else {
                            p.sendMessage(ChatColor.RED + "Vous n'avez pas cet item dans votre inventaire !");
                        }
                    }

                    e.setCancelled(true);
                    p.closeInventory();
                    p.openInventory(new TradeHolder(BourseCache.getItem(is.getType().toString()).get(), p).getInventory());
                } else {
                    p.sendMessage(ChatColor.RED + "Une erreur est survenue.");
                }
            }
        }
        else if(e.getInventory().getHolder() instanceof ItemHolder && e.getCurrentItem() != null && e.getClick() == ClickType.LEFT){
            ItemStack is = e.getCurrentItem();
            ItemMeta im = is.getItemMeta();
            if(is.equals(GUIUtils.getQuit())){
                p.closeInventory();
                p.openInventory(new CategoryHolder().getInventory());
            } else {
                if (is.getType() != Material.AIR && im != null) {
                    if (BourseCache.getItem(is.getType().toString()).isPresent()) {
                        p.openInventory(new TradeHolder(BourseCache.getItem(is.getType().toString()).get(), p).getInventory());
                    } else {
                        p.sendMessage(ChatColor.RED + "Une erreur est survenue !");
                    }
                }
            }
        }

        if((e.getInventory().getHolder() instanceof TradeHolder || e.getInventory().getHolder() instanceof CategoryHolder || e.getInventory().getHolder() instanceof ItemHolder)){
            e.setCancelled(true);
        }
    }
}
