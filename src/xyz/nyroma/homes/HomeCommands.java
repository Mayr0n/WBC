package xyz.nyroma.homes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class HomeCommands implements CommandExecutor {
    public List<String> getCommands() {
        return Arrays.asList("sethome", "delhome", "home", "fsethome", "fdelhome", "fhome", "gethomes");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command comm, String arg, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String cmd = comm.getName();
            if (cmd.equals(getCommands().get(0))) {
                PlayerHomes ph;
                try {
                    ph = HomesCache.get(p.getName());
                } catch (HomesException e) {
                    ph = new PlayerHomes(p.getName());
                }
                try {
                    String name = args[0];
                    if (ph.sethome(name, p.getLocation())) {
                        p.sendMessage(ChatColor.GREEN + "Ton home \"" + name + "\" a été ajouté !");
                    } else {
                        p.sendMessage(ChatColor.RED + "Une erreur est survenue. Contactez Imperayser.");
                    }
                } catch (HomesException e) {
                    p.sendMessage(ChatColor.RED + e.getMessage());
                } catch (ArrayIndexOutOfBoundsException e) {
                    p.sendMessage(ChatColor.RED + "Il faut spécifier le nom du home à ajouter !");
                }
            } else if (cmd.equals(getCommands().get(1))) {
                try {
                    PlayerHomes ph = HomesCache.get(p.getName());
                    try {
                        String name = args[0];
                        if (ph.delhome(name)) {
                            p.sendMessage(ChatColor.GREEN + "Ton home \"" + name + "\" a été supprimé !");
                        } else {
                            p.sendMessage(ChatColor.RED + "Une erreur est survenue. Contactez Imperayser.");
                        }
                    } catch (HomesException e) {
                        p.sendMessage(ChatColor.RED + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        p.sendMessage(ChatColor.RED + "Il faut spécifier le nom du home à supprimer !");
                    }
                } catch (HomesException e) {
                    p.sendMessage(ChatColor.RED + "Tu n'a aucun home enregistré !");
                }
            } else if (cmd.equals(getCommands().get(2))) {
                try {
                    PlayerHomes ph = HomesCache.get(p.getName());
                    try {
                        String name = args[0];
                        if (ph.tpHome(name, p)) {
                            p.sendMessage(ChatColor.GREEN + "Tu a été téléporté à ton home \"" + name + "\" !");
                        } else {
                            p.sendMessage(ChatColor.RED + "Une erreur est survenue. Contactez Imperayser.");
                        }
                    } catch (HomesException e) {
                        p.sendMessage(ChatColor.RED + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Vos homes : ");
                        for(String s : ph.getHomes().keySet()){
                            sb.append(s).append(", ");
                        }
                        String s = sb.toString();
                        int l = s.length() - 2;
                        p.sendMessage(ChatColor.GREEN + s.substring(0, l));
                    }
                } catch (HomesException e) {
                    p.sendMessage(ChatColor.RED + "Tu n'a aucun home enregistré !");
                }
            } else if (cmd.equals(getCommands().get(3)) && p.isOp()) {
                try {
                    String pseudo = args[0];
                    String name = args[1];
                    String world = args[2];
                    float x = Float.parseFloat(args[3]);
                    float y = Float.parseFloat(args[4]);
                    float z = Float.parseFloat(args[5]);
                    PlayerHomes ph;
                    try {
                        ph = HomesCache.get(pseudo);
                    } catch (HomesException e) {
                        ph = new PlayerHomes(pseudo);
                    }
                    try {
                        if (ph.sethome(name, new Location(Bukkit.getWorld(world), x, y, z))) {
                            p.sendMessage(ChatColor.GREEN + "Le home \"" + name + "\" a été ajouté !");
                        } else {
                            p.sendMessage(ChatColor.RED + "Une erreur est survenue. Contactez Imperayser.");
                        }
                    } catch (HomesException e) {
                        p.sendMessage(ChatColor.RED + e.getMessage());
                    }
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /fsethome <pseudo> <nom> <monde> <x> <y> <z>");
                }
            } else if (cmd.equals(getCommands().get(4)) && p.isOp()) {
                try {
                    String pseudo = args[0];
                    String name = args[1];
                    PlayerHomes ph = HomesCache.get(pseudo);
                    if (ph.delhome(name)) {
                        p.sendMessage(ChatColor.GREEN + "Le home \"" + name + "\" a été supprimé !");
                    } else {
                        p.sendMessage(ChatColor.RED + "Une erreur est survenue. Contactez Imperayser.");
                    }
                } catch (HomesException e) {
                    p.sendMessage(ChatColor.RED + e.getMessage());
                } catch (ArrayIndexOutOfBoundsException e) {
                    p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /fdelhome <pseudo> <nom>");
                }
            } else if (cmd.equals(getCommands().get(5)) && p.isOp()) {
                try {
                    String pseudo = args[0];
                    String name = args[1];
                    PlayerHomes ph = HomesCache.get(pseudo);
                    if (ph.tpHome(name, p)) {
                        p.sendMessage(ChatColor.GREEN + "Tu a été téléporté au home \"" + name + "\" !");
                    } else {
                        p.sendMessage(ChatColor.RED + "Une erreur est survenue. Contactez Imperayser.");
                    }
                } catch (HomesException e) {
                    p.sendMessage(ChatColor.RED + e.getMessage());
                } catch (ArrayIndexOutOfBoundsException e) {
                    p.sendMessage(ChatColor.RED + "Il faut spécifier le nom du home à se téléporter !");
                }
            } else if (cmd.equals(getCommands().get(6)) && p.isOp()) {
                for (PlayerHomes ph : HomesCache.getAllSethomes()) {
                    p.sendMessage(ChatColor.GREEN + "-----------------------");
                    p.sendMessage(ChatColor.GREEN + ph.getPlayer());
                    for (String name : ph.getHomes().keySet()) {
                        p.sendMessage(ChatColor.GREEN + name + " : " + ph.getHomes().get(name));
                    }
                    p.sendMessage(ChatColor.GREEN + "-----------------------");
                }
                System.out.println("gethomes");
            }
            return true;
        }
        return false;
    }
}
