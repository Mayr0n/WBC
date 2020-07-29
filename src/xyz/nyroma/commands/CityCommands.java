package xyz.nyroma.commands;

import xyz.nyroma.cityapi.citymanagement.CitiesCache;
import xyz.nyroma.cityapi.citymanagement.City;
import xyz.nyroma.cityapi.citymanagement.CityException;
import xyz.nyroma.cityapi.citymanagement.CityUtils;
import xyz.nyroma.cityapi.main.SLocation;
import xyz.nyroma.listeners.CityListeners;
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

            if (cmd.equals(commands.get(0))) {
                if (args.length >= 1) {
                    if (args[0].equals("create")) {
                        if (args.length >= 2 && !CityUtils.getCityOfMember(p.getName()).isPresent()) {
                            if (bank.getAmount() >= 50) {
                                String name = getArgsLeft(args, 1);
                                if (name.length() <= 20) {
                                    try {
                                        City city = new City(name, p.getName());
                                        bank.remove(50, Transaction.STATE_REMOVE);
                                        city.addMoney(25);
                                        p.sendMessage(ChatColor.GREEN + "Votre ville " + name + " a été créée !");
                                        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 100, 1);
                                        p.sendMessage(ChatColor.GREEN + "N'oubliez pas de remplir la banque de votre ville avec la commande " +
                                                ChatColor.DARK_GREEN + " /city money add <montant>" +
                                                ChatColor.GREEN + ", la taxe actuelle est de " + city.getTaxes() + " Nyr débitée toutes les 12h.");
                                    } catch (CityException e) {
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
                    else if (args[0].equals("list")) {
                        p.sendMessage(ChatColor.DARK_GREEN + "- Voici toutes les villes existantes et leurs owners ! -");
                        for (City c : CitiesCache.getCities()) {
                            p.sendMessage(ChatColor.GOLD + c.getName() + ", gérée par " + c.getOwner());
                        }
                        p.sendMessage(ChatColor.DARK_GREEN + "---------------------------------------------");
                    }
                    else if (args[0].equals("info")) {
                        if (args.length >= 2) {
                            String name = getArgsLeft(args, 1);
                            City city;
                            if (CitiesCache.get(name).isPresent()) {
                                city = CitiesCache.get(name).get();

                                p.sendMessage(ChatColor.DARK_GREEN + "-------- " + city.getName() + " --------");
                                p.sendMessage(ChatColor.DARK_GREEN + "Empire : " + ChatColor.GREEN + city.getEmpire().getName());
                                p.sendMessage(ChatColor.DARK_GREEN + "Owner : " + ChatColor.GREEN + city.getOwner());
                                p.sendMessage(ChatColor.DARK_GREEN + "ID : " + ChatColor.GREEN + city.getID());
                                p.sendMessage(ChatColor.DARK_GREEN + "Réserve : " + ChatColor.GREEN + city.getBankAmount() + " Nyr");
                                p.sendMessage(ChatColor.DARK_GREEN + "Taxes : " + ChatColor.GREEN + city.getTaxes() + " Nyr");
                                p.sendMessage(ChatColor.DARK_GREEN + "Membres : ");
                                for (String st : city.getMembers()) {
                                    p.sendMessage(ChatColor.GREEN + "- " + st);
                                }
                                p.sendMessage(ChatColor.DARK_GREEN + "Etat fermé : " + ChatColor.GREEN + (city.getClosed() ? "Oui" : "Non"));
                                if(!city.getClosed()){
                                    p.sendMessage(ChatColor.DARK_GREEN + "Alliés : ");
                                    for (long allyID : city.getAllies()) {
                                        if(CityUtils.getCityFromID(allyID).isPresent()){
                                            p.sendMessage(ChatColor.GREEN + "- " + CityUtils.getCityFromID(allyID).get().getName());
                                        }
                                    }
                                    p.sendMessage(ChatColor.DARK_GREEN + "Ennemis : ");
                                    for (long enemyID : city.getAllies()) {
                                        if(CityUtils.getCityFromID(enemyID).isPresent()){
                                            p.sendMessage(ChatColor.GREEN + "- " + CityUtils.getCityFromID(enemyID).get().getName());
                                        }
                                    }
                                }
                                p.sendMessage(ChatColor.DARK_GREEN + "Claims : " + city.getClaims().size() + "/" + city.getMaxClaims());
                                p.sendMessage(ChatColor.DARK_GREEN + "-----------------------");
                            } else {
                                p.sendMessage(ChatColor.RED + "Cette ville n'existe pas.");
                            }
                        } else {
                            p.sendMessage(getErrorMessage("/city info <nom>"));
                        }
                    }
                    else if (CityUtils.getCityOfMember(p.getName()).isPresent()) {
                        City city = CityUtils.getCityOfMember(p.getName()).get();
                        if (city.getOwner().equals(p.getName())) {
                            if (args[0].equals("delete")) {
                                p.sendMessage(CityUtils.removeCity(city) ?
                                        ChatColor.GREEN + "Votre ville a bien été supprimée." :
                                        ChatColor.RED + "Votre ville n'a pas pu être supprimée ?");
                            }
                            else if (args[0].equals("rename")) {
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
                            else if (args[0].equals("owner")) {
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
                            else if (args[0].equals("ally")) {
                                if (args.length >= 3) {
                                    String name = getArgsLeft(args, 2);
                                    if (CitiesCache.get(name).isPresent()) {
                                        City c = CitiesCache.get(name).get();
                                        if (args[1].equals("add")) {
                                            if (city.addAlly(c)) {
                                                p.sendMessage(ChatColor.GREEN + "La ville " + c.getName() + " est devenue votre alliée.");
                                                if (MainUtils.getPlayerByName(c.getOwner()).isPresent()) {
                                                    Player owner = MainUtils.getPlayerByName(c.getOwner()).get();
                                                    owner.sendMessage(ChatColor.RED + city.getName() + " a ajouté votre ville de la liste de ses alliées !");
                                                }
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Cette ville n'a pas pu devenir votre alliée.");
                                            }
                                        } else if (args[1].equals("remove")) {
                                            if (city.removeAlly(c)) {
                                                p.sendMessage(ChatColor.GREEN + "La ville " + c.getName() + " n'est plus votre alliée.");
                                                if (MainUtils.getPlayerByName(c.getOwner()).isPresent()) {
                                                    Player owner = MainUtils.getPlayerByName(c.getOwner()).get();
                                                    owner.sendMessage(ChatColor.RED + city.getName() + " a retiré votre ville de la liste de ses alliées !");
                                                }
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Cette ville n'a pas pu être retirée de la liste de vos alliés.");
                                            }
                                        } else {
                                            p.sendMessage(getErrorMessage("/city ally <add:remove> <nom>"));
                                        }
                                    } else {
                                        p.sendMessage(ChatColor.RED + "Cette ville n'existe pas.");
                                    }
                                } else {
                                    p.sendMessage(getErrorMessage("/city ally <add:remove:get>"));
                                }
                            }
                            else if (args[0].equals("claim")) {
                                if (args.length == 2) {
                                    Location loc = p.getLocation();
                                    if (args[1].equals("add")) {
                                        if(!CityListeners.getClaimerFromLoc(loc).isPresent()){
                                                switch (city.addClaim(loc.getWorld().getName(), loc.getChunk().getX(), loc.getChunk().getZ())) {
                                                    case CLAIMED:
                                                        p.sendMessage(ChatColor.GREEN + "Le chunk a été claim par votre ville.");
                                                        break;
                                                    case MAX_CLAIM:
                                                        p.sendMessage(ChatColor.RED + "Votre ville a déjà atteint le nombre maximum de claims. Ajoutez en avec /city claim more");
                                                        break;
                                                    case ALREADY_CLAIM:
                                                        p.sendMessage(ChatColor.RED + "Ce chunk est déjà claim !");
                                                        break;
                                                }
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Ce chunk est déjà claim.");
                                        }
                                    } else if (args[1].equals("remove")) {
                                        switch(city.removeClaim(loc.getWorld().getName(), loc.getChunk().getX(), loc.getChunk().getZ())){
                                            case UNCLAIMED:
                                                p.sendMessage(ChatColor.GREEN + "Le chunk a bien été unclaim.");
                                                break;
                                            case NOT_CLAIMED:
                                                p.sendMessage(ChatColor.RED + "Ce chunk n'est pas claim par votre ville !");
                                                break;
                                        }
                                    } else if (args[1].equals("list")) {
                                        for(SLocation sloc : city.getClaims()){
                                            p.sendMessage(ChatColor.GREEN + sloc.getWorld() + ", " + sloc.getX() + ", " + sloc.getZ() + ".");
                                        }
                                    }
                                } else {
                                    p.sendMessage(getErrorMessage("/city claim <add:remove:more:less>"));
                                }
                            }
                            else if (args[0].equals("enemy")) {
                                if (args.length >= 3) {
                                    String name = getArgsLeft(args, 2);
                                    if (CitiesCache.get(name).isPresent()) {
                                        City c = CitiesCache.get(name).get();
                                        if (args[1].equals("add")) {
                                            if (city.addEnemy(c)) {
                                                p.sendMessage(ChatColor.GREEN + "La ville " + c.getName() + " est devenue votre ennemie.");
                                                if (MainUtils.getPlayerByName(c.getOwner()).isPresent()) {
                                                    Player owner = MainUtils.getPlayerByName(c.getOwner()).get();
                                                    owner.sendMessage(ChatColor.RED + city.getName() + " a ajouté votre ville de la liste de ses ennemis !");
                                                }
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Cette ville n'a pas pu devenir votre ennemi.");
                                            }
                                        } else if (args[1].equals("remove")) {
                                            if (city.removeEnemy(c)) {
                                                p.sendMessage(ChatColor.GREEN + "La ville " + c.getName() + " n'est plus votre ennemie.");
                                                if (MainUtils.getPlayerByName(c.getOwner()).isPresent()) {
                                                    Player owner = MainUtils.getPlayerByName(c.getOwner()).get();
                                                    owner.sendMessage(ChatColor.RED + city.getName() + " a retiré votre ville de la liste de ses ennemis !");
                                                }
                                            } else {
                                                p.sendMessage(ChatColor.RED + "Cette ville n'a pas pu être retirée de la liste de vos ennemis.");
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
                            else if (args[0].equals("members")) {
                                if (args.length == 3) {
                                    if (args[1].equals("add")) {
                                        if (MainUtils.getPlayerByName(args[2]).isPresent()) {
                                            sendInvit(city, MainUtils.getPlayerByName(args[2]).get());
                                            p.sendMessage(ChatColor.GREEN + "Une invitation lui a été envoyée.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Ce joueur n'est pas connecté.");
                                        }
                                    } else if (args[1].equals("remove")) {
                                        if (city.getMembers().contains(args[2])) {
                                            if (city.removeMember(args[2])) {
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
                            else if (args[0].equals("money")) {
                                if (args.length == 3) {
                                    if (args[1].equals("add")) {
                                        float amount = Float.parseFloat(args[2]);
                                        if (bank.remove(amount, Transaction.CITY_REMOVE)) {
                                            city.addMoney(amount);
                                            p.sendMessage(ChatColor.GREEN + args[2] + " Nyr ont été placés dans la banque de votre ville.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Vous n'avez pas le montant nécessaire dans votre banque personnelle.");
                                        }
                                    } else if (args[1].equals("remove")) {
                                        float a = Float.parseFloat(args[2]);
                                        if (city.removeMoney(a)) {
                                            bank.add(a, Transaction.CITY_ADD);
                                            p.sendMessage(ChatColor.GREEN + args[2] + " Nyr ont été déplacés vers votre banque personnelle.");
                                        } else {
                                            p.sendMessage(ChatColor.RED + "Votre ville n'a pas l'argent nécessaire pour retirer cette somme.");
                                        }
                                    }
                                } else {
                                    p.sendMessage(getErrorMessage("/city money <add:remove> <montant>"));
                                }
                            } else if(args[0].equals("close")){
                                if(args.length == 2){
                                    if(args[1].equals("yes")){
                                        city.setClosed(true);
                                        p.sendMessage(ChatColor.GREEN + "Votre ville a été fermée.");
                                    } else if(args[1].equals("no")){
                                        city.setClosed(false);
                                        p.sendMessage(ChatColor.GREEN + "Votre ville a été ouverte !");
                                    } else {
                                        p.sendMessage(getErrorMessage("/city close <yes:no>"));
                                    }
                                }
                            } else {
                                p.sendMessage(getErrorMessage("/city <delete:rename:owner:ally:claim:enemy:members:money>"));
                            }
                        } else {
                            if (args[0].equals("money")) {
                                if (args.length == 3) {
                                    if (args[1].equals("add")) {
                                        float amount = Float.parseFloat(args[2]);
                                        if (bank.remove(amount, Transaction.CITY_ADD)) {
                                            city.addMoney(amount);
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
                                ci.addMember(play.getName());
                                p.sendMessage(ChatColor.GREEN + "Owner modifié.");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Le joueur n'est pas connecté ou cette ville n'existe pas.");
                        }
                    } else {
                        p.sendMessage(getErrorMessage("/scity modify owner <pseudo> <ville>"));
                    }
                } else if(args.length == 2){
                    if(args[0].equals("remove")){
                        if(CitiesCache.get(args[1]).isPresent()){
                            CityUtils.removeCity(CitiesCache.get(args[1]).get());
                            p.sendMessage(ChatColor.GREEN + "Ville supprimée.");
                        } else {
                            p.sendMessage(ChatColor.RED + "Cette ville n'existe pas.");
                        }
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
}
