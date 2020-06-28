package xyz.nyroma.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BotlinkManager {
    private File playerFile = new File("../../../commun/data.txt");
    private File dtm = new File("../../../commun/discToMc.txt");
    private File mtd = new File("../../../commun/mcToDisc.txt");
    private File logs = new File("../../../commun/logs.txt");
    public static boolean isActivated = false;

    public BotlinkManager(){
        if(playerFile.exists()){
            BotlinkManager.isActivated = true;
        }
    }

    public void updatePlayers(ArrayList<Player> players, boolean there){
        StringBuilder s = new StringBuilder();
        s.append("Players: ").append(players.size()).append("\n");
        if(!there) {
            s.append("List: ");
            for (Player p : players) {
                s.append(p.getName()).append(",");
            }
            s.append("\n");
        } else {
            s.append("Aucun connecté !");
        }
        MainUtils.writeInFile(playerFile, s.toString(), true);
    }

    public boolean hasMess(){
        return dtm.exists();
    }

    public void showMess(){
        List<String> content = MainUtils.getFileContent(dtm);
        dtm.delete();
        StringBuilder sb = new StringBuilder();
        sb.append(content.get(0)).append("\n");
        for(int i = 1 ; i < content.size() ; i++){
            sb.append(content.get(i)).append("\n");
        }
        Bukkit.broadcastMessage(sb.toString());
        log(sb.toString());
    }

    public void sendMess(String txt){
        MainUtils.writeInFile(mtd, txt, false);
        log(txt);
    }

    private void log(String txt){
        MainUtils.writeInFile(logs, txt, false);
    }


}
