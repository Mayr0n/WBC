package xyz.nyroma.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.nyroma.capitalism.bourse.CategoryHolder;
import xyz.nyroma.bourseAPI.BourseCache;
import xyz.nyroma.bourseAPI.Category;
import xyz.nyroma.bourseAPI.Item;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class BourseCommands implements CommandExecutor {
    public static List<String> getCommands() {
        return Arrays.asList("bourse");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command comm, String arg, String[] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;
            String cmd = comm.getName();

            if (cmd.equalsIgnoreCase(getCommands().get(0))) {
                if (args.length >= 1 && p.isOp()) {
                    if (args.length >= 3) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
                                switch (args[1]) {
                                    case "category":
                                        Category category = new Category(args[2], p.getInventory().getItemInMainHand().getType().toString());
                                        p.sendMessage(ChatColor.DARK_GREEN + "Catégorie ajoutée.");
                                        p.sendMessage(ChatColor.GREEN + "ID : " + category.getID());
                                        p.sendMessage(ChatColor.GREEN + "Nom : " + category.getName());
                                        p.sendMessage(ChatColor.GREEN + "Icône : " + category.getIcon());
                                        break;
                                    case "item":
                                        try {
                                            Item bourse = new Item(p.getInventory().getItemInMainHand().getType().toString(), Float.parseFloat(args[2]));
                                            p.sendMessage(ChatColor.DARK_GREEN + "Bourse ajoutée.");
                                            p.sendMessage(ChatColor.GREEN + "Produit : " + bourse.getProduct());
                                            p.sendMessage(ChatColor.GREEN + "Prix initial : " + bourse.getPrix());
                                            p.sendMessage(ChatColor.GREEN + "X : " + bourse.getX());
                                            p.sendMessage(ChatColor.GREEN + "Stocks : " + bourse.getStocks());
                                            p.sendMessage(ChatColor.GREEN + "Prix achat : " + bourse.getBuyPrice(0));
                                            p.sendMessage(ChatColor.GREEN + "Prix vente : " + bourse.getSellPrice(0));
                                        } catch (NumberFormatException e) {
                                            p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /bourse add item <price>");
                                        }
                                        break;
                                    default:
                                        p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /bourse add <category:item>");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "Il faut un item dans votre main principale.");
                            }

                        }
                        else if (args[0].equalsIgnoreCase("edit")) {
                            ItemStack itemInMainHand = p.getInventory().getItemInMainHand();
                            ItemStack itemInOffHand = p.getInventory().getItemInOffHand();
                            if (p.getInventory().getItemInMainHand().getType() != Material.AIR) {
                                switch (args[1]) {
                                    case "item":
                                        if (BourseCache.getItem(itemInMainHand.getType().toString()).isPresent()) {
                                            Item bourse = BourseCache.getItem(itemInMainHand.getType().toString()).get();
                                            if (args.length == 4) {
                                                try {
                                                    switch (args[2]) {
                                                        case "prix":
                                                            bourse.setPrix(Float.parseFloat(args[3]));
                                                            p.sendMessage(ChatColor.GREEN + "Prix initial modifié à " + args[3] + " Nyr.");
                                                            break;
                                                        case "stocks":
                                                            bourse.setStocks(Integer.parseInt(args[3]));
                                                            p.sendMessage(ChatColor.GREEN + "Nombre de stocks modifié à " + args[3]);
                                                            break;
                                                        case "X":
                                                            bourse.resetX();
                                                            p.sendMessage(ChatColor.GREEN + "Le X a été reset.");
                                                            break;
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /bourse edit item <prix:stocks:X> <nb>");
                                                }
                                            } else if(args.length == 3 && args[2].equalsIgnoreCase("remove")){
                                                BourseCache.removeItem(bourse);
                                                p.sendMessage(ChatColor.GREEN + "L'item a été remove.");
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /bourse edit item <prix:stocks:X> <nb>");
                                            }
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Il n'y a pas de bourse pour cet item.");
                                        }
                                        break;
                                    case "category":
                                        if (BourseCache.getCategory(itemInMainHand.getType().toString()).isPresent()) {
                                            Category category = BourseCache.getCategory(itemInMainHand.getType().toString()).get();
                                            switch (args[2]) {
                                                case "name":
                                                    if (args.length == 4) {
                                                        category.setName(args[3]);
                                                        p.sendMessage(ChatColor.GREEN + "Nom modifié en " + args[3]);
                                                    } else {
                                                        p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /bourse edit category <name:remove:add> <name>");
                                                    }
                                                    break;
                                                case "remove":
                                                    if (itemInOffHand.getType() != Material.AIR) {
                                                        if (BourseCache.getItem(itemInOffHand.getType().toString()).isPresent()) {
                                                            Item item = BourseCache.getItem(itemInOffHand.getType().toString()).get();
                                                            if (category.getItems().remove(item)) {
                                                                p.sendMessage(ChatColor.GREEN + "L'item a été supprimé de la catégorie.");
                                                            } else {
                                                                p.sendMessage(ChatColor.RED + "L'item n'a pas pu être supprimé de la catégorie");
                                                            }
                                                        } else {
                                                            p.sendMessage(ChatColor.RED + "Il n'y a pas de bourse pour cet item !");
                                                        }
                                                    } else {
                                                        p.sendMessage(ChatColor.RED + "Il vous faut un item dans la main secondaire à supprimer de la catégorie !");
                                                    }
                                                    break;
                                                case "add":
                                                    if (itemInOffHand.getType() != Material.AIR) {
                                                        if (BourseCache.getItem(itemInOffHand.getType().toString()).isPresent()) {
                                                            Item item = BourseCache.getItem(itemInOffHand.getType().toString()).get();
                                                            category.getItems().add(item);
                                                            p.sendMessage(ChatColor.GREEN + "L'item a été ajouté à la catégorie.");
                                                        } else {
                                                            p.sendMessage(ChatColor.RED + "Il n'y a pas de bourse pour cet item !");
                                                        }
                                                    } else {
                                                        p.sendMessage(ChatColor.RED + "Il vous faut un item dans la main secondaire à supprimer de la catégorie !");
                                                    }
                                                    break;
                                                case "icon":
                                                    if (itemInOffHand.getType() != Material.AIR) {
                                                        category.setIcon(itemInOffHand.getType().toString());
                                                        p.sendMessage(ChatColor.GREEN + "L'icône de la catégorie a été modifiée.");
                                                    } else {
                                                        p.sendMessage(ChatColor.RED + "Il vous faut un item dans la main secondaire à supprimer de la catégorie !");
                                                    }
                                                    break;
                                            }
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Il n'y a pas de catégories avec cette icône.");
                                        }
                                        break;
                                    default:
                                        p.sendMessage(ChatColor.RED + "Arguments invalides : /bourse edit <item:category>");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "Il faut un item dans votre main principale.");
                            }
                        }
                    } else if (args.length == 1 && args[0].equals("forcereload")) {
                        BourseCache.setup();
                        Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Bourses reload.");
                    } else if (args.length == 1 && args[0].equals("resetX")) {
                        for (Item item : BourseCache.items) {
                            item.resetX();
                        }
                        Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Tous les X ont été resets.");
                    }
                } else {
                    p.sendMessage(ChatColor.GREEN + "Bienvenue dans la bourse du serveur !");
                    p.openInventory(new CategoryHolder().getInventory());
                }
            }
        }

        return false;
    }
}
