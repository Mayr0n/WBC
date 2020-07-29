package xyz.nyroma.main;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainUtils {

    public static void testFileExist(File file) throws FileNotFoundException{
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new FileNotFoundException();
            }
        }
    }
    public static void testFolderExist(File file){
        if(!file.exists()){
            file.mkdir();
        }
    }
    public static Optional<Player> getPlayerByName(String name) {
        for(Player p : Bukkit.getServer().getOnlinePlayers()){
            if(p.getName().equals(name)){
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }
    public static void sendErrorMessage(Player p, String message) {
        p.sendMessage(ChatColor.RED + message);
    }
    public static String getUUID(Player p){
        return p.getUniqueId().toString();
    }
    public static List<String> getFileContent(File file){
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line = reader.readLine();
            while(line != null){
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch(IOException e){
            e.printStackTrace();
        }
        return lines;
    }
    public static void writeInFile(File file, String txt, boolean erase){
        try {
            testFileExist(file);
            OutputStreamWriter osw;
            if (erase) {
                osw = new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8);
            } else {
                osw = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8);
            }
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(txt);
            bw.close();
            osw.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    public static String getDate(String type){
        String date = new GregorianCalendar().getTime().toString();
        switch(type){
            case "hours":
                date = date.substring(11,18);
                break;
            case "days":
                date = date.substring(0,9);
                break;
            case "year":
                date = date.substring(24,27);
                break;
        }
        return date;
    }
    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY) + 6;
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        String min;
        String h;
        String s;
        if (hours >= 24) {
            h = "0" + (hours - 24);
        } else {
            h = String.valueOf(hours);
        }
        if (minutes < 10) {
            min = "0" + minutes;
        } else {
            min = String.valueOf(minutes);
        }
        if (seconds < 10) {
            s = "0" + seconds;
        } else {
            s = String.valueOf(seconds);
        }
        return h + ":" + min + ":" + s;
    }
}
