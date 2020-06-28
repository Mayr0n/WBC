package xyz.nyroma.homes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.Hashtable;

public class PlayerHomes implements Serializable {
    private String pseudo;
    private int max = 5;
    private Hashtable<String, String> homes = new Hashtable<>(); //nom, location

    public PlayerHomes(String pseudo){
        this.pseudo = pseudo;
        HomesCache.add(this);
    }

    public String getPlayer(){
        return this.pseudo;
    }

    public void setMax(int i){
        this.max = i;
    }

    public Hashtable<String, String> getHomes(){
        return homes;
    }

    public boolean sethome(String name, Location location) throws HomesException {
        if(homes.keySet().size() < this.max){
            if(!homes.keySet().contains(name)){
                String loc = location.getWorld().getName() + "/" + (float) location.getX() + "/" + (float) location.getY() + "/" + (float) location.getZ();
                this.homes.put(name, loc);
                return true;
            } else {
                throw new HomesException("Tu as déjà un home avec ce nom !");
            }
        } else {
            throw new HomesException("Tu as déjà 5 homes !");
        }
    }

    public boolean delhome(String name) throws HomesException {
        if(homes.keySet().contains(name)){
            this.homes.remove(name);
            return true;
        } else {
            throw new HomesException("Ce home n'existe pas.");
        }
    }

    public boolean tpHome(String name, Player p) throws HomesException {
        if(homes.keySet().contains(name)){
            String[] args = this.homes.get(name).split("/");
            Location loc = new Location(Bukkit.getWorld(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]));
            if(!loc.getChunk().isLoaded()){
                loc.getChunk().load();
            }
            p.teleport(loc);
            return true;
        } else {
            throw new HomesException("Ce home n'existe pas.");
        }
    }
}
