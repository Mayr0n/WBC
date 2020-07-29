package xyz.nyroma.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nyroma.capitalism.jobs.Job;
import xyz.nyroma.capitalism.jobs.JobUtils;

import java.util.Arrays;
import java.util.List;

public class JobCommands implements CommandExecutor {

    public static List<String> getCommands() {
        return Arrays.asList("job");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command comm, String arg, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String cmd = comm.getName();
            if (cmd.equals(getCommands().get(0))) {
                if (args.length == 1) {
                    switch (args[0]) {
                        case "HUNTER":
                            JobUtils.setJob(p.getName(), Job.HUNTER);
                            p.sendMessage(ChatColor.GREEN + "Vous êtes " + JobUtils.getJob(p.getName()).get().toString());
                            break;
                        case "FARMER":
                            JobUtils.setJob(p.getName(), Job.FARMER);
                            p.sendMessage(ChatColor.GREEN + "Vous êtes " + JobUtils.getJob(p.getName()).get().toString());
                            break;
                        case "TRADER":
                            JobUtils.setJob(p.getName(), Job.TRADER);
                            p.sendMessage(ChatColor.GREEN + "Vous êtes " + JobUtils.getJob(p.getName()).get().toString());
                            break;
                        case "LUMBER":
                            JobUtils.setJob(p.getName(), Job.LUMBER);
                            p.sendMessage(ChatColor.GREEN + "Vous êtes " + JobUtils.getJob(p.getName()).get().toString());
                            break;
                        case "FISHER":
                            JobUtils.setJob(p.getName(), Job.FISHER);
                            p.sendMessage(ChatColor.GREEN + "Vous êtes " + JobUtils.getJob(p.getName()).get().toString());
                            break;
                        case "MINER":
                            JobUtils.setJob(p.getName(), Job.MINER);
                            p.sendMessage(ChatColor.GREEN + "Vous êtes " + JobUtils.getJob(p.getName()).get().toString());
                            break;
                        case "ENCHANTER":
                            JobUtils.setJob(p.getName(), Job.ENCHANTER);
                            p.sendMessage(ChatColor.GREEN + "Vous êtes " + JobUtils.getJob(p.getName()).get().toString());
                            break;
                        case "BREEDER":
                            JobUtils.setJob(p.getName(), Job.BREEDER);
                            p.sendMessage(ChatColor.GREEN + "Vous êtes " + JobUtils.getJob(p.getName()).get().toString());
                            break;
                        case "get":
                            if (JobUtils.getJob(p.getName()).isPresent()) {
                                p.sendMessage(ChatColor.GREEN + "Vous êtes " + JobUtils.getJob(p.getName()).toString());
                            } else {
                                p.sendMessage(ChatColor.RED + "Vous n'avez pas de métier.");
                            }
                            break;
                        case "getall":
                            for (Player play : p.getServer().getOnlinePlayers()) {
                                if (JobUtils.getJob(play.getName()).isPresent()) {
                                    p.sendMessage(ChatColor.GREEN + play.getName() + " est " + JobUtils.getJob(play.getName()).get().toString());
                                } else {
                                    p.sendMessage(ChatColor.GREEN + play.getName() + " n'a pas de métier.");
                                }
                            }
                            break;
                        default:
                            p.sendMessage(ChatColor.GREEN + "Vous êtes " + JobUtils.getJob(p.getName()).toString());
                            break;
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Argument invalide ! Syntaxe : /job <HUNTER:FARMER:TRADER:LUMBER:FISHER:ENCHANTER:BREEDER:get:getall>");
                }
            }
        }
        return false;
    }
}
