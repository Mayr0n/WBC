package xyz.nyroma.homes;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nyroma.main.MainUtils;

import java.io.*;
import java.util.ArrayList;

public class HomesCache {
    public static ArrayList<PlayerHomes> sethomes = new ArrayList<>();
    private static File folder = new File("data/sethomes/");
    public static void setup(JavaPlugin plugin) {
        MainUtils.testFolderExist(folder);
        try {
            sethomes.addAll(HomesCache.getHomesFromFile());
        } catch (HomesException e) {
            System.out.println(e.getMessage());
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                System.out.println("Enregistrement des homes...");
                serializeAll();
                System.out.println("Homes enregistrés !");
            }
        }.runTaskTimer(plugin, 10 * 60 * 20L, 10 * 60 * 20L);
    }
    public static void add(PlayerHomes ph){
        sethomes.add(ph);
        serializeHome(ph);
    }
    public static PlayerHomes get(String pseudo) throws HomesException {
        for(PlayerHomes ph : sethomes){
            if(ph.getPlayer().equals(pseudo)){
                return ph;
            }
        }
        throw new HomesException(pseudo + " n'a pas de Playerhomes.");
    }
    public static ArrayList<PlayerHomes> getAllSethomes(){
        return sethomes;
    }
    public static void serializeAll() {
        System.out.println("Enregistrement des homes...");
        for (PlayerHomes ph : sethomes) {
            serializeHome(ph);
        }
        System.out.println("Homes enregistrés !");
    }
    public static void serializeHome(PlayerHomes ph) {
        try {
            File homeFile = new File("data/sethomes/" + ph.getPlayer() + ".txt");
            if (!homeFile.exists()) {
                homeFile.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(homeFile)));
            oos.writeObject(ph);
            oos.close();
        } catch(IOException e){
            System.out.println("Une erreur est survenue.");
            e.printStackTrace();
        }
    }
    public static ArrayList<PlayerHomes> getHomesFromFile() throws HomesException {
        ArrayList<PlayerHomes> homes = new ArrayList<>();
        try {
            for (File file : folder.listFiles()) {
                PlayerHomes ph = getHomeFromFile(file);
                homes.add(ph);
            }
        } catch (NullPointerException e) {
            throw new HomesException("Il n'y a pas de sethomes enregistrés.");
        }
        return homes;
    }
    public static PlayerHomes getHomeFromFile(File file) throws HomesException {
        try {
            ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            Object obj = oos.readObject();
            oos.close();
            if (obj.getClass().equals(PlayerHomes.class)) {
                return (PlayerHomes) obj;
            } else {
                throw new HomesException("Il y a une erreur dans le plugin.");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new HomesException("Il y a une erreur dans le plugin.");
        }
    }
}
