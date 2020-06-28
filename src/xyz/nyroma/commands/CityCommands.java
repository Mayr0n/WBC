package xyz.nyroma.commands;

import xyz.nyroma.main.MainUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import xyz.nyroma.towny.citymanagement.CitiesCache;
import xyz.nyroma.towny.citymanagement.City;
import xyz.nyroma.towny.citymanagement.CityManager;
import xyz.nyroma.towny.citymanagement.TownyException;
import xyz.nyroma.towny.cityparts.ClaimsManager;
import xyz.nyroma.towny.cityparts.MembersManager;
import xyz.nyroma.towny.cityparts.MoneyManager;
import xyz.nyroma.towny.cityparts.RelationsManager;
import xyz.nyroma.towny.enums.Commands;
import xyz.nyroma.towny.enums.SubCommands;

import java.util.Arrays;
import java.util.List;

public class CityCommands implements CommandExecutor {
    private List<String> commands = Arrays.asList("city", "scity");

    public List<String> getCommands() {
        return commands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command comm, String arg, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String cmd = comm.getName();
            Bank bank = BankCache.get(p.getName());
            CityManager cm = new CityManager();

            if (cmd.equals(commands.get(0))) {
                if (args.length >= 1) {
                    if (args[0].equals(Commands.CREATE.getCmd())) {
                        if (args.length >= 2) {
                            if (bank.getAmount() >= 50) {
                                String name = getArgsLeft(args, 1);
                                if (name.length() <= 20) {
                                    try {
                                        City city = new City(name, "DiscUniverse", p.getName());
                                        bank.remove(50, Transaction.STATE_REMOVE);
                                        city.getMoneyManager().addMoney(25);
                                        p.sendMessage(ChatColor.GREEN + "Votre ville " + name + " a été créée !");
                                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 100, 1);
                                        p.sendMessage(ChatColor.GREEN + "N'oubliez pas de remplir la banque de votre ville avec la commande " +
                                                ChatColor.DARK_GREEN + " /city money add <montant>" +
                                                ChatColor.GREEN + ", la taxe actuelle est de " + city.getMoneyManager().getTaxes() + " Nyr débitée toutes les 12h.");
                                    } catch (TownyException e) {
                                        p.sendMessage(ChatColor.RED + e.getMessage());
                                    }
                                } else {
                                    p.sendMessage(ChatColor.RED + "Arguments invalides ! Le nom doit faire moins de 20 caractères.");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent ! Il faut 50 Nyr pour créer votre ville !");
                            }
                        } else {
                            p.sendMessage(getErrorMessage("/city create <nom>"));
                        }
                    }
                    else if (args[0].equals(Commands.LIST.getCmd())) {
                        p.sendMessage(ChatColor.DARK_GREEN + "- Voici toutes les villes existantes et leurs owners ! -");
                        for (City c : CitiesCache.getCities()) {
                            p.sendMessage(ChatColor.GOLD + c.getName() + ", gérée par " + c.getOwner());
                        }
                        p.sendMessage(ChatColor.DARK_GREEN + "---------------------------------------------");
                    }
                    else if (args[0].equals(Commands.INFO.getCmd())) {
                        if (args.length >= 2) {
                            String name = getArgsLeft(args, 1);
                            City city;
                            if (CitiesCache.get(name).isPresent()) {
                                city = CitiesCache.get(name).get();
                                MoneyManager mm = city.getMoneyManager();
                                MembersManager mbm = city.getMembersManager();
                                ClaimsManager cmm = city.getClaimsManager();
                                RelationsManager rm = city.getRelationsManager();

                                p.sendMessage(ChatColor.DARK_GREEN + "-------- " + city.getName() + " --------");
                                p.sendMessage(ChatColor.DARK_GREEN + "Owner : " + ChatColor.GREEN + city.getOwner());
                                p.sendMessage(ChatColor.DARK_GREEN + "ID : " + ChatColor.GREEN + city.getID());
                                p.sendMessage(ChatColor.DARK_GREEN + "Réserve : " + ChatColor.GREEN + mm.getAmount() + " Nyr");
                                p.sendMessage(ChatColor.DARK_GREEN + "Taxes : " + ChatColor.GREEN + mm.getTaxes() + " Nyr");
                                p.sendMessage(ChatColor.DARK_GREEN + "Royaume : " + ChatColor.GREEN + city.getRoyaume());
                                p.sendMessage(ChatColor.DARK_GREEN + "Membres : ");
                                for (String st : mbm.getMembers()) {
                                    p.sendMessage(ChatColor.GREEN + "- " + st);
                                }
                                p.sendMessage(ChatColor.DARK_GREEN + "Alliés : ");
                                if (rm.getNice()) {
                                    p.sendMessage(ChatColor.GREEN + "- Tout le monde");
                                } else {
                                    for (String ally : rm.getAllies()) {
                                        p.sendMessage(ChatColor.GREEN + "- " + ally);
                                    }
                                }
                                p.sendMessage(ChatColor.DARK_GREEN + "Ennemis : ");
                                if (rm.getEvil()) {
                                    p.sendMessage(ChatColor.GREEN + "- Tout le monde");
                                } else {
                                    for (String enemy : rm.getEnemies()) {
                                        p.sendMessage(ChatColor.GREEN + "- " + enemy);
                                    }
                                }
                                p.sendMessage(ChatColor.DARK_GREEN + "Claims : " + cmm.getAmount() + "/" + cmm.getMax());
                                p.sendMessage(ChatColor.DARK_GREEN + "-----------------------");
                            } else {
                                p.sendMessage(ChatColor.RED + "Cette ville n'existe pas.");
                            }
                        } else {
                            p.sendMessage(getErrorMessage("/city info <nom>"));
                        }
                    }
                    else if (cm.getCityOfMember(p.getName()).isPresent()) {
                        City city = cm.getCityOfMember(p.getName()).get();
                        if (city.getOwner().equals(p.getName())) {
                            if (args[0].equals(Commands.REMOVE.getCmd())) {
                                if (cm.removeCity(city)) {
                                    p.sendMessage(ChatColor.GREEN + "Votre ville a bien été supprimée.");
                                } else {
                                    p.sendMessage(ChatColor.RED + "Votre ville n'a pas pu être supprimée ?");
                                }
                            }
                            else if (args[0].equals(Commands.RENAME.getCmd())) {
                                if (args.length >= 2) {
                                    String name = getArgsLeft(args, 1);
                                    if (!CitiesCache.contains(name)) {
                                        if (name.length() <= 20) {
                                            city.rename(name);
                                            p.sendMessage(ChatColor.GREEN + "Votre ville a été renommée.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Arguments invalides ! Le nom doit faire moins de 20 caractères.");
                                        }
                                    } else {
                                        p.sendMessage(ChatColor.RED + "Une ville a déjà ce nom.");
                                    }
                                } else {
                                    p.sendMessage(getErrorMessage("/city rename <nom>"));
                                }
                            }
                            else if (args[0].equals(Commands.OWNER.getCmd())) {
                                if (args.length == 2) {
                                    if (MainUtils.getPlayerByName(args[1]).isPresent()) {
                                        city.changeOwner(args[1]);
                                        p.sendMessage(ChatColor.GREEN + "Le propriétaire de la ville a été changé.");
                                    } else {
                                        p.sendMessage(ChatColor.RED + "Le joueur doit être connecté.");
                                    }
                                } else {
                                    p.sendMessage(getErrorMessage("/city owner <pseudo>"));
                                }
                            }
                            else if (args[0].equals(Commands.ALLY.getCmd())) {
                                if (args.length >= 3) {
                                    String name = getArgsLeft(args, 2);
                                    if (CitiesCache.get(name).isPresent()) {
                                        City c = CitiesCache.get(name).get();
                                        if (args[1].equals(SubCommands.ALLY_ADD.toString())) {
                                            if (city.getRelationsManager().addAlly(c)) {
                                                p.sendMessage(ChatColor.GREEN + "La ville " + c.getName() + " est devenue votre alliée.");
                                                if (MainUtils.getPlayerByName(c.getOwner()).isPresent()) {
                                                    Player owner = MainUtils.getPlayerByName(c.getOwner()).get();
                                                    owner.sendMessage(ChatColor.RED + city.getName() + " a ajouté votre ville de la liste de ses alliées !");
                                                }
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Cette ville n'a pas pu devenir votre alliée.");
                                            }
                                        } else if (args[1].equals(SubCommands.ALLY_REMOVE.toString())) {
                                            if (city.getRelationsManager().removeAlly(c)) {
                                                p.sendMessage(ChatColor.GREEN + "La ville " + c.getName() + " n'est plus votre alliée.");
                                                if (MainUtils.getPlayerByName(c.getOwner()).isPresent()) {
                                                    Player owner = MainUtils.getPlayerByName(c.getOwner()).get();
                                                    owner.sendMessage(ChatColor.RED + city.getName() + " a retiré votre ville de la liste de ses alliées !");
                                                }
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Cette ville n'a pas pu être retirée de la liste de vos alliés.");
                                            }
                                        } else if (args[1].equals(SubCommands.ALLY_NICE.toString())) {
                                            if (args[2].equals("enable")) {
                                                city.getRelationsManager().setNice(true);
                                            } else if (args[2].equals("disable")) {
                                                city.getRelationsManager().setNice(false);
                                            } else {
                                                p.sendMessage(getErrorMessage("/city ally nice <enable:disable>"));
                                            }
                                        } else {
                                            p.sendMessage(getErrorMessage("/city ally <add:remove:(nice)> <nom:(enable:disable)>"));
                                        }
                                    } else {
                                        p.sendMessage(ChatColor.RED + "Cette ville n'existe pas.");
                                    }
                                } else {
                                    p.sendMessage(getErrorMessage("/city ally <add:remove:get>"));
                                }
                            }
                            else if (args[0].equals(Commands.CLAIM.getCmd())) {
                                if (args.length == 2) {
                                    Location loc = p.getLocation();
                                    if (args[1].equals(SubCommands.CLAIM_ADD.toString())) {
                                        if (city.getClaimsManager().addClaim(loc.getWorld().getName(), loc.getChunk().getX(), loc.getChunk().getZ())) {
                                            p.sendMessage(ChatColor.GREEN + "Le chunk a été claim par votre ville.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Le chunk n'a pu être claim par votre ville.");
                                        }
                                    } else if (args[1].equals(SubCommands.CLAIM_REMOVE.toString())) {
                                        if (city.getClaimsManager().removeClaim(loc.getWorld().getName(), loc.getChunk().getX(), loc.getChunk().getZ())) {
                                            p.sendMessage(ChatColor.GREEN + "Le chunk a été unclaim par votre ville.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Le chunk n'a pu être unclaim par votre ville.");
                                        }
                                    } else if (args[1].equals(SubCommands.CLAIM_MORE.toString())) {
                                        float newTaxes = city.getMoneyManager().getTaxes() + 1.5f;
                                        if (city.getMoneyManager().getAmount() >= (newTaxes + 25)) {
                                            city.getClaimsManager().setMax(city.getClaimsManager().getMax() + 1);
                                            city.getMoneyManager().setTaxes(newTaxes);
                                            city.getMoneyManager().removeMoney(25);
                                            p.sendMessage(ChatColor.GREEN + "Votre ville \"" + city.getName() + "\" comporte désormais " + city.getClaimsManager().getMax() +
                                                    "claims, les taxes sont maintenant de " + city.getMoneyManager().getTaxes() + " Nyr toutes les 12h");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'argent dans la banque de votre ville ! (" + (newTaxes + 25) + "Nyr nécessaires)");
                                        }
                                    } else if (args[1].equals(SubCommands.CLAIM_LESS.toString())) {
                                        if (city.getClaimsManager().getMax() > 5) {
                                            city.getClaimsManager().setMax(city.getClaimsManager().getMax() - 1);
                                            city.getMoneyManager().setTaxes(city.getMoneyManager().getTaxes() - 1.5f);
                                            p.sendMessage(ChatColor.GREEN + "Vous avez maintenant " + city.getClaimsManager().getMax() + " claims, donc une taxe de " +
                                                    city.getMoneyManager().getTaxes() + " Nyr toutes les 12h.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Votre ville ne peut pas avoir moins de 5 claims !");
                                        }
                                    }
                                } else {
                                    p.sendMessage(getErrorMessage("/city claim <add:remove:more:less>"));
                                }
                            }
                            else if (args[0].equals(Commands.ENEMY.getCmd())) {
                                if (args.length >= 3) {
                                    String name = getArgsLeft(args, 2);
                                    if (CitiesCache.get(name).isPresent()) {
                                        City c = CitiesCache.get(name).get();
                                        if (args[1].equals(SubCommands.ENEMY_ADD.toString())) {
                                            if (city.getRelationsManager().addEnemy(c)) {
                                                p.sendMessage(ChatColor.GREEN + "La ville " + c.getName() + " est devenue votre ennemie.");
                                                if (MainUtils.getPlayerByName(c.getOwner()).isPresent()) {
                                                    Player owner = MainUtils.getPlayerByName(c.getOwner()).get();
                                                    owner.sendMessage(ChatColor.RED + city.getName() + " a ajouté votre ville de la liste de ses ennemis !");
                                                }
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Cette ville n'a pas pu devenir votre ennemi.");
                                            }
                                        } else if (args[1].equals(SubCommands.ENEMY_REMOVE.toString())) {
                                            if (city.getRelationsManager().removeEnemy(c)) {
                                                p.sendMessage(ChatColor.GREEN + "La ville " + c.getName() + " n'est plus votre ennemie.");
                                                if (MainUtils.getPlayerByName(c.getOwner()).isPresent()) {
                                                    Player owner = MainUtils.getPlayerByName(c.getOwner()).get();
                                                    owner.sendMessage(ChatColor.RED + city.getName() + " a retiré votre ville de la liste de ses ennemis !");
                                                }
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Cette ville n'a pas pu être retirée de la liste de vos ennemis.");
                                            }
                                        } else if (args[1].equals(SubCommands.ENEMY_EVIL.toString())) {
                                            if (args[2].equals("enable")) {
                                                city.getRelationsManager().setEvil(true);
                                            } else if (args[2].equals("disable")) {
                                                city.getRelationsManager().setEvil(false);
                                            } else {
                                                p.sendMessage(getErrorMessage("/city enemy evil <enable:disable>"));
                                            }
                                        } else {
                                            p.sendMessage(getErrorMessage("/city enemy <add:remove:(evil)> <nom:(enable:disable)>"));
                                        }
                                    } else {
                                        p.sendMessage(ChatColor.RED + "Cette ville n'existe pas.");
                                    }
                                } else {
                                    p.sendMessage(getErrorMessage("/city enemy <add:remove> <nom>"));
                                }
                            }
                            else if (args[0].equals(Commands.MEMBERS.getCmd())) {
                                if (args.length == 3) {
                                    if (args[1].equals(SubCommands.MEMBERS_ADD.toString())) {
                                        if (MainUtils.getPlayerByName(args[2]).isPresent()) {
                                            sendInvit(city, MainUtils.getPlayerByName(args[2]).get());
                                            p.sendMessage(ChatColor.GREEN + "Une invitation lui a été envoyée.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Ce joueur n'est pas connecté.");
                                        }
                                    } else if (args[1].equals(SubCommands.MEMBERS_REMOVE.toString())) {
                                        if (city.getMembersManager().getMembers().contains(args[2])) {
                                            if (city.getMembersManager().removeMember(args[2])) {
                                                p.sendMessage(ChatColor.RED + args[2] + " a été viré de votre ville.");
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Le joueur n'a pas pu être viré.");
                                            }
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Ce joueur n'est pas dans votre ville.");
                                        }
                                    }
                                } else {
                                    p.sendMessage(getErrorMessage("/city member <add:remove> <pseudo>"));
                                }
                            }
                            else if (args[0].equals(Commands.MONEY.getCmd())) {
                                if (args.length == 3) {
                                    if (args[1].equals(SubCommands.MONEY_ADD.toString())) {
                                        float amount = Float.parseFloat(args[2]);
                                        if (bank.remove(amount, Transaction.CITY_REMOVE)) {
                                            city.getMoneyManager().addMoney(amount);
                                            p.sendMessage(ChatColor.GREEN + args[2] + " Nyr ont été placés dans la banque de votre ville.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Vous n'avez pas le montant nécessaire dans votre banque personnelle.");
                                        }
                                    } else if (args[1].equals(SubCommands.MONEY_REMOVE.toString())) {
                                        float a = Float.parseFloat(args[2]);
                                        if (city.getMoneyManager().removeMoney(a)) {
                                            bank.add(a, Transaction.CITY_ADD);
                                            p.sendMessage(ChatColor.GREEN + args[2] + " Nyr ont été déplacés vers votre banque personnelle.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Votre ville n'a pas l'argent nécessaire pour retirer cette somme.");
                                        }
                                    }
                                } else {
                                    p.sendMessage(getErrorMessage("/city money <add:remove> <montant>"));
                                }
                            }
                            /*else if (args[0].equals(Commands.WAR.getCmd())){
                                if(args.length >= 4 && args[1].equals(SubCommands.WAR_DECLARE.toString())){
                                    String name = getArgsLeft(args, 3);
                                    if(CitiesCache.get(name).isPresent()){
                                        City enemy = CitiesCache.get(name).get();
                                        try {
                                            int prime = Integer.parseInt(args[2]);
                                            if(!(prime <= 0)){
                                                if(!(prime > city.getMoneyManager().getAmount() && prime > enemy.getMoneyManager().getAmount())){
                                                    if(MainUtils.getPlayerByName(enemy.getOwner()).isPresent()){
                                                        sendWarDeclaration(enemy, city, prime);
                                                        p.sendMessage(ChatColor.BLACK + "La déclaration de guerre a été lancée.");
                                                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 100, 1);
                                                    } else {
                                                        p.sendMessage(ChatColor.RED + "Le propriétaire de la ville adverse n'est pas connecté.");
                                                    }
                                                } else {
                                                    p.sendMessage(ChatColor.RED + "Votre ville ou la ville adverse ne possède pas le montant nécessaire.");
                                                }
                                            } else {
                                                p.sendMessage(ChatColor.RED + "La prime ne peut être ni négative ni nulle.");
                                            }
                                        } catch(NumberFormatException e){
                                            p.sendMessage(getErrorMessage("/city war declare <prime> <nom de la ville enemie>"));
                                        }
                                    } else {
                                        p.sendMessage(ChatColor.RED + "Cette ville n'existe pas !");
                                    }
                                } else if(args.length == 2){
                                    if(args[1].equals(SubCommands.WAR_PEACE.toString())){
                                        if(city.getWarManager().isInWar()){
                                            city.getWarManager().declarePeace(city.getWarManager().getEnemy());
                                            p.sendMessage(ChatColor.GREEN + "Vous avez déclaré la paix.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Vous n'êtes pas en guerre !");
                                        }
                                    }
                                } else {
                                    p.sendMessage(getErrorMessage("/city war <(declare):peace> <(prime)> <(nom de la ville)>"));
                                }
                            }*/
                            else {
                                p.sendMessage(getErrorMessage("/city <remove:rename:owner:ally:claim:enemy:members:money>"));
                            }
                        } else {
                            if (args[0].equals(Commands.MONEY.getCmd())) {
                                if (args.length == 3) {
                                    if (args[1].equals(SubCommands.MONEY_ADD.toString())) {
                                        float amount = Float.parseFloat(args[2]);
                                        if (bank.remove(amount, Transaction.CITY_ADD)) {
                                            city.getMoneyManager().addMoney(amount);
                                            p.sendMessage(ChatColor.GREEN + args[2] + " Nyr ont été placés dans la banque de votre ville.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Vous n'avez pas le montant nécessaire dans votre banque personnelle.");
                                        }
                                    } else {
                                        p.sendMessage(getErrorMessage("/city money add <montant>"));
                                    }
                                }
                            } else {
                                p.sendMessage(getErrorMessage("/city <create:list:info:money>"));
                            }
                        }
                    }
                    else {
                        p.sendMessage(ChatColor.RED + "Vous n'appartenez à aucune ville !");
                    }
                }
            } else if (cmd.equals(commands.get(1)) && p.isOp()) {
                if(args.length >= 4) {
                    if (args[0].equals("modify")) {
                        if(MainUtils.getPlayerByName(args[2]).isPresent() && CitiesCache.get(getArgsLeft(args, 3)).isPresent()) {
                            Player play = MainUtils.getPlayerByName(args[2]).get();
                            City ci = CitiesCache.get(getArgsLeft(args, 3)).get();

                            if ("owner".equals(args[1])) {
                                ci.changeOwner(play.getName());
                                ci.getMembersManager().addMember(play.getName());
                                p.sendMessage(ChatColor.GREEN + "Owner modifié.");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Le joueur n'est pas connecté ou cette ville n'existe pas.");
                        }
                    } else {
                        p.sendMessage(getErrorMessage("/scity modify owner <pseudo> <ville>"));
                    }
                }
            }
        }
        return false;
    }

    public String getErrorMessage(String syntax) {
        return ChatColor.RED + "Arguments invalides ! Syntaxe : " + ChatColor.GOLD + syntax;
    }

    public String getArgsLeft(String[] args, int firstIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = firstIndex; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String s = sb.toString();
        int l = s.length() - 1;
        return s.substring(0, l);
    }

    public void sendInvit(City city, Player toAdd) {
        ItemStack is = new ItemStack(Material.PAPER);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName("Invitation");
        im.setLore(Arrays.asList("Invitation à rejoindre la ville :", city.getName(), "pour :", toAdd.getName()));
        im.addEnchant(Enchantment.DURABILITY, 10, true);
        is.setItemMeta(im);

        toAdd.getInventory().addItem(is);
    }

    public void sendWarDeclaration(City city, City enemy, int prime) {
        ItemStack is = new ItemStack(Material.PAPER);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.DARK_RED + "Déclaration de guerre");
        im.setLore(Arrays.asList(
                ChatColor.RED + "La ville suivante vous déclare la guerre :", ChatColor.DARK_RED + enemy.getName(),
                ChatColor.RED + "Prime de victoire :", ChatColor.DARK_RED + String.valueOf(prime) + " Nyromarks",
                ChatColor.DARK_AQUA + "Pour accepter, faites un clic droit avec cette déclaration en Main."));
        im.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        is.setItemMeta(im);

        if(MainUtils.getPlayerByName(city.getOwner()).isPresent()){
            Player p = MainUtils.getPlayerByName(city.getOwner()).get();
            p.sendMessage(ChatColor.RED + "La ville " + enemy.getName() + " vous déclare la guerre !");
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 100, 1);
            p.getWorld().dropItem(p.getLocation(), is);
        }
    }
}
