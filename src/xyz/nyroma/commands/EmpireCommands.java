package xyz.nyroma.commands;

import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nyroma.banks.Bank;
import xyz.nyroma.banks.BankCache;
import xyz.nyroma.banks.Transaction;
import xyz.nyroma.cityapi.citymanagement.CitiesCache;
import xyz.nyroma.cityapi.citymanagement.City;
import xyz.nyroma.cityapi.citymanagement.CityUtils;
import xyz.nyroma.cityapi.empiremanagement.Empire;
import xyz.nyroma.cityapi.empiremanagement.EmpireUtils;
import xyz.nyroma.cityapi.empiremanagement.EmpiresCache;

import java.util.Arrays;
import java.util.List;

public class EmpireCommands implements CommandExecutor {

    public List<String> getCommands(){
        return Arrays.asList("empire");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command comm, String arg, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            String cmd = comm.getName();
            if(cmd.equals(getCommands().get(0))){
                if(args[0].equals("list")){
                    p.sendMessage(ChatColor.DARK_GREEN + "------ Empires ------");
                    for(Empire empire : EmpiresCache.getEmpires()){
                        p.sendMessage(ChatColor.DARK_GREEN + "Nom : " + ChatColor.GREEN + empire.getName());
                        p.sendMessage(ChatColor.DARK_GREEN + "Capitale : " + ChatColor.GREEN + empire.getCapitale());
                        p.sendMessage("");
                    }
                    p.sendMessage(ChatColor.DARK_GREEN + "------------");
                } else {
                    if (CityUtils.getCityOfMember(p.getName()).isPresent()){
                        City city = CityUtils.getCityOfMember(p.getName()).get();
                        if(city.getOwner().equals(p.getName())){
                            if(args[0].equals("create")){
                                if(args.length == 2 && !args[1].equals("")){
                                    new Empire(args[1], city);
                                    p.sendMessage(ChatColor.GREEN + "Votre empire " + args[1] + " a été créé !");
                                }
                            } else {
                                if(EmpireUtils.getEmpireOfCity(city).isPresent()){
                                    Empire empire = EmpireUtils.getEmpireOfCity(city).get();
                                    /*if(args[0].equals("add")){
                                        if(empire.getCapitale().getID() == city.getID()) {
                                            if (args.length == 2 && !args[1].equals("")) {
                                                if (CitiesCache.get(args[1]).isPresent()) {
                                                    City toAdd = CitiesCache.get(args[1]).get();
                                                    empire.addMember(toAdd);
                                                    empire.setTaxes(empire.getTaxes() + 25);
                                                    p.sendMessage(ChatColor.GREEN + "La ville " + toAdd.getName() + " a été ajoutée à l'empire !");
                                                    p.sendMessage(ChatColor.GREEN + "Les taxes ont été augmentées à " + empire.getTaxes() + " Nyr");
                                                } else {
                                                    p.sendMessage(ChatColor.RED + "Cette ville n'existe pas !");
                                                }
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /empire add <nom>");
                                            }
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Votre ville n'est pas la capitale de votre empire !");
                                        }
                                    } else */if(args[0].equals("remove")){
                                        if(empire.getCapitale().getID() == city.getID()) {
                                            if (args.length == 2 && !args[1].equals("")) {
                                                if (CitiesCache.get(args[1]).isPresent()) {
                                                    City toRemove = CitiesCache.get(args[1]).get();
                                                    if(toRemove.getID() != city.getID()){
                                                        empire.removeMember(toRemove);
                                                        empire.setTaxes(empire.getTaxes() - 25);
                                                        p.sendMessage(ChatColor.GREEN + "La ville " + toRemove.getName() + " a été retirée de l'empire !");
                                                        p.sendMessage(ChatColor.GREEN + "Les taxes ont été diminuées à " + empire.getTaxes() + " Nyr");
                                                    } else {
                                                        p.sendMessage(ChatColor.RED + "Vous ne pouvez pas virer votre propre ville !");
                                                    }
                                                } else {
                                                    p.sendMessage(ChatColor.RED + "Cette ville n'existe pas !");
                                                }
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /empire remove <nom>");
                                            }
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Votre ville n'est pas la capitale de votre empire !");
                                        }
                                    } else if(args[0].equals("bank")){
                                        if(args.length == 3){
                                            float montant = Float.parseFloat(args[2]);
                                            try {
                                                if (args[1].equals("add")) {
                                                    Bank bank = BankCache.get(p.getName());
                                                    if(bank.remove(montant, Transaction.STATE_REMOVE)){
                                                        empire.addMoney(montant);
                                                        p.sendMessage(ChatColor.GREEN + String.valueOf(montant) + " Nyr ont été virés sur le compte de l'empire.");
                                                    } else {
                                                        p.sendMessage(ChatColor.RED + "Vous n'avez pas le montant nécessaire dans votre banque.");
                                                    }
                                                } else if (args[1].equals("remove") && empire.getCapitale().getID() == city.getID()) {
                                                    Bank bank = BankCache.get(p.getName());
                                                    if(empire.removeMoney(montant)){
                                                        bank.add(montant, Transaction.STATE_ADD);
                                                        p.sendMessage(ChatColor.GREEN + String.valueOf(montant) + " Nyr ont été virés sur votre compte.");
                                                    } else {
                                                        p.sendMessage(ChatColor.RED + "Votre empire n'a pas le montant nécessaire.");
                                                    }
                                                } else {
                                                    p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /empire bank <add:remove> <montant>");
                                                }
                                            } catch (NumberFormatException e){
                                                p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /empire bank <add:remove> <montant>");
                                            }
                                        }
                                    } else if(args[0].equals("rename") && empire.getCapitale().getID() == city.getID()){
                                        if(args.length == 2 && !args[1].equals("") && !args[1].equals("DiscUniverse")){
                                            empire.setName(args[1]);
                                            p.sendMessage(ChatColor.GREEN + "Votre empire a été renommé " + args[1]);
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /empire rename <nom>");
                                        }
                                    } else if(args[0].equals("removeempire") && empire.getCapitale().getID() == city.getID()){
                                        EmpireUtils.removeEmpire(empire);
                                        p.sendMessage(ChatColor.GREEN + "Votre empire a été supprimé.");
                                    }
                                } else {
                                    p.sendMessage(ChatColor.RED + "Votre ville n'a pas d'empire !");
                                }
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Vous n'êtes pas propriétaire de votre ville !");
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "Vous n'appartenez à aucune ville !");
                    }
                }
            }
        }

        return false;
    }
}
