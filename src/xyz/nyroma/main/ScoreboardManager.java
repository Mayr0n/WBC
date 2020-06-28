package xyz.nyroma.main;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import xyz.nyroma.banks.Bank;
import xyz.nyroma.banks.BankCache;
import xyz.nyroma.towny.citymanagement.CitiesCache;
import xyz.nyroma.towny.citymanagement.City;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class ScoreboardManager {
    private Server server;
    private Objective money;
    private Objective pourcentage;
    private Objective deaths;
    private Objective cities;
    private Objective global;
    private Scoreboard moneyScoreboard;
    private Scoreboard miscScoreboard;
    private Scoreboard deathScoreboard;
    private Scoreboard cityScoreboard;
    private Scoreboard globalScoreboard;
    private Hashtable<Scoreboard, Objective> test = new Hashtable<>();
    public static Scoreboard current;

    public ScoreboardManager(Server server){
        this.server = server;
    }

    public void build(){
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        //-------------------------------------
        moneyScoreboard = manager.getNewScoreboard();

        this.money = moneyScoreboard.registerNewObjective("Money", "dummy", ChatColor.DARK_RED + "Money");
        this.money.setDisplaySlot(DisplaySlot.SIDEBAR);

        //---------------------------------
        miscScoreboard = manager.getNewScoreboard();

        this.pourcentage = miscScoreboard.registerNewObjective("Pourcentages", "dummy", ChatColor.DARK_RED + "Pour% richesses");
        this.pourcentage.setDisplaySlot(DisplaySlot.SIDEBAR);

        //------------------------------
        this.deathScoreboard = manager.getNewScoreboard();

        this.deaths = deathScoreboard.registerNewObjective("Temps (en h)", "dummy", ChatColor.DARK_RED + "Temps (en h)");
        this.deaths.setDisplaySlot(DisplaySlot.SIDEBAR);

        //------------------------------

        this.cityScoreboard = manager.getNewScoreboard();

        this.cities = cityScoreboard.registerNewObjective("Cities' money", "dummy", ChatColor.DARK_RED + "Cities' money");
        this.cities.setDisplaySlot(DisplaySlot.SIDEBAR);
        //------------------------------

        this.globalScoreboard = manager.getNewScoreboard();

        this.global = globalScoreboard.registerNewObjective("Global money", "dummy", ChatColor.DARK_RED + "Global money");
        this.global.setDisplaySlot(DisplaySlot.SIDEBAR);

        current = this.moneyScoreboard;
        setScoreboard(this.server);
    }

    public void refresh(){
        int total = 0;
        for(Bank bank : BankCache.getBanks()){
            Score score = this.money.getScore(ChatColor.BLUE + bank.getPlayer());
            score.setScore((int) bank.getAmount());
            total += bank.getAmount();
        }
        for(Bank bank : BankCache.getBanks()){
            Score score = this.pourcentage.getScore(ChatColor.BLUE + bank.getPlayer());
            score.setScore((int) bank.getAmount()*100/total);
        }
        for(City city : CitiesCache.getCities()){
            Score score = this.cities.getScore(ChatColor.DARK_AQUA + city.getName());
            score.setScore((int) city.getMoneyManager().getAmount());
        }
        for(City city : CitiesCache.getCities()){
            Score score = this.global.getScore(ChatColor.DARK_AQUA + city.getName());
            int amount = 0;
            for(String player : city.getMembersManager().getMembers()){
                amount += BankCache.get(player).getAmount();
            }
            amount += city.getMoneyManager().getAmount();
            score.setScore(amount);
        }
        this.pourcentage.getScore(ChatColor.RED + "1% des richesses").setScore(total/100);
        for(OfflinePlayer p : Bukkit.getServer().getWhitelistedPlayers()){
            Score score = this.deaths.getScore(ChatColor.DARK_AQUA + p.getName());
            if(p.getPlayer() != null){
                score.setScore((p.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE)/20)/3600);
            }
        }
    }

    public void setScoreboard(Server server){
        List<Scoreboard> sc = Arrays.asList(this.moneyScoreboard, this.miscScoreboard, this.deathScoreboard, this.cityScoreboard, this.globalScoreboard);
        for(Player p : server.getOnlinePlayers()) {
            int ind = sc.indexOf(p.getScoreboard());
            if(ind == sc.size()-1 || ind == -1){
                ind = 0;
            } else {
                ind++;
            }

            if(sc.get(ind).getObjective(DisplaySlot.PLAYER_LIST) == null){
                Objective o = sc.get(ind).registerNewObjective("health", "health", "health");
                o.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                o.setRenderType(RenderType.HEARTS);
            }

            p.setScoreboard(sc.get(ind));
            current = sc.get(ind);
        }
    }

}
