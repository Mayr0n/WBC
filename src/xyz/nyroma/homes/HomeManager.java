package xyz.nyroma.homes;

import org.bukkit.entity.Player;

public class HomeManager {

    public HomeManager(){

    }

    public static void checkOrAdd(Player p){
        try {
            if(!HomesCache.sethomes.contains(HomesCache.get(p.getName()))){
                HomesCache.add(new PlayerHomes(p.getName()));
            }
        } catch (HomesException e) {
            System.out.println(e.getMessage());
            HomesCache.add(new PlayerHomes(p.getName()));
        }
    }
}
