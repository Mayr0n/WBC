package xyz.nyroma.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nyroma.banks.Bank;
import xyz.nyroma.banks.BankCache;
import xyz.nyroma.banks.Transaction;
import xyz.nyroma.main.MainUtils;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BankCommands implements CommandExecutor {

    @Nullable
    private ItemStack getBill(Bank bank, float amount) {
        if (bank.getAmount() - amount > 0) {
            ItemStack b = new ItemStack(Material.PAPER);
            ItemMeta im = b.getItemMeta();
            im.addEnchant(Enchantment.LOYALTY, 5, true);
            im.setLore(Arrays.asList("Montant du billet :", amount + " Nyr", "Propriétaire :", bank.getPlayer()));
            b.setItemMeta(im);
            bank.remove(amount, Transaction.PLAYER_REMOVE);
            return b;
        } else {
            return null;
        }
    }

    public List<String> getCommands() {
        return Arrays.asList("bank", "sbank");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command comm, String arg, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String cmd = comm.getName();
            if (cmd.equals(getCommands().get(0))) {
                Bank bank = BankCache.get(p.getName());
                try {
                    switch (args[0]) {
                        case "send":
                            if (args.length == 3) {
                                if (MainUtils.getPlayerByName(args[1]).isPresent()) {
                                    Player toSend = MainUtils.getPlayerByName(args[1]).get();
                                    Bank b = BankCache.get(toSend.getName());
                                    try {
                                        int amount = Integer.parseInt(args[2]);
                                        if (bank.remove(amount, Transaction.PLAYER_REMOVE)) {
                                            p.sendMessage(ChatColor.GREEN + Integer.toString(amount) + " Nyr ont été retirés de votre compte.");
                                            b.add(amount, Transaction.PLAYER_ADD);
                                            toSend.sendMessage(ChatColor.GREEN + "Vous avez reçu " + amount + " Nyr de la part de " + p.getName() + " !");
                                            p.sendMessage(ChatColor.GREEN + toSend.getName() + " a bien reçu les " + amount + " Nyr !");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Vous n'avez pas le montant nécessaire dans votre banque !");
                                        }
                                    } catch (NumberFormatException e) {
                                        p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /bank send <pseudo> <montant>");
                                    }
                                } else {
                                    p.sendMessage(ChatColor.RED + "Arguments invalides ! Ce joueur n'est pas connecté.");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /bank send <pseudo> <montant>");
                            }
                            break;
                        case "get":
                            p.sendMessage(ChatColor.GREEN + "Montant de votre banque : " + bank.getAmount() + " Nyr");
                            break;
                        case "bill":
                            try {
                                if (getBill(bank, Float.parseFloat(args[1])) != null) {
                                    p.getInventory().addItem(getBill(bank, Float.parseFloat(args[1])));
                                    p.sendMessage(ChatColor.GREEN + "Un billet de " + Float.parseFloat(args[1]) + " a été débité de votre banque.");
                                } else {
                                    p.sendMessage(ChatColor.RED + "Vous n'avez pas le montant nécessaire dans votre banque !");
                                }
                            } catch (NumberFormatException e) {
                                p.sendMessage(ChatColor.RED + "Argument invalide ! Syntaxe : /bank bill <montant>");
                            }
                            break;
                        case "getall":
                            p.sendMessage(ChatColor.GREEN + "Voici les montants des banques existantes :");
                            for (Bank b : BankCache.getBanks()) {
                                p.sendMessage(ChatColor.GREEN + "-> " + b.getPlayer() + " : " + b.getAmount() + " Nyr");
                            }
                            break;
                        default:
                            p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /bank <send:bill:get:getall>");
                            break;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /bank <send:bill:get:getall>");
                }
            } else if (cmd.equals(getCommands().get(1)) && p.isOp()) {
                try {
                    String pseudo = args[1];
                    switch (args[0]) {
                        case "add":
                            int amount = Integer.parseInt(args[2]);
                            BankCache.get(pseudo).add(amount, Transaction.STATE_ADD);
                            p.sendMessage(ChatColor.GREEN + args[2] + " Nyr ont été ajoutés au compte de " + pseudo);
                            break;
                        case "remove":
                            int a = Integer.parseInt(args[2]);
                            if (BankCache.get(pseudo).remove(a, Transaction.STATE_REMOVE)) {
                                p.sendMessage(ChatColor.GREEN + args[2] + " Nyr ont été supprimés du compte de " + pseudo);
                            } else {
                                p.sendMessage(ChatColor.GREEN + pseudo + " n'a pas assez de money pour retirer ce montant.");
                            }
                            break;
                        case "get":
                            p.sendMessage(ChatColor.GREEN + "Montant de la banque : " + BankCache.get(pseudo).getAmount() + " Nyr");
                            break;
                        case "reset":
                            if (pseudo.equals("all")) {
                                for (Bank bank : BankCache.getBanks()) {
                                    bank.remove(bank.getAmount(), Transaction.STATE_REMOVE);
                                }
                                p.sendMessage(ChatColor.GREEN + "Toutes les banques ont perdu leurs réserves.");
                            } else {
                                BankCache.banks.remove(BankCache.get(pseudo));
                                p.sendMessage(ChatColor.GREEN + "La banque de " + pseudo + " a été réinitialisée.");
                            }
                            break;
                        default:
                            p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /sbank <add:remove:get>");
                            break;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /sbank <add:remove:get>");
                }
            }
        }
        return false;
    }
}
