package xyz.nyroma.capitalism.jobs;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nyroma.main.MainUtils;

import java.io.*;
import java.util.Hashtable;
import java.util.Optional;

public class JobUtils {

    private static Hashtable<String, Job> jobs = new Hashtable<>(); //Pseudo, Job

    private static File file = new File("data/towny/" + "jobs.txt");
    public static void setup(JavaPlugin plugin){
        MainUtils.testFolderExist(new File("data/towny/"));
        try {
            MainUtils.testFileExist(file);
        } catch (FileNotFoundException e) {
            System.out.println("ERREUR SETUP JOBMANAGER");
        }

        System.out.println("Chargement des jobs...");

        try {
            jobs = getJobsFromFile();
        } catch (JobException e) {
            jobs = new Hashtable<>();
        }

        System.out.println("Jobs chargés !");

        new BukkitRunnable() {
            @Override
            public void run() {
                JobUtils.serializeAll();
            }
        }.runTaskTimer(plugin, 10 * 60 * 20L, 10 * 60 * 20L);
    }
    public static void serializeAll(){
        System.out.println("Enregistrement des jobs...");
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            oos.writeObject(jobs);
            oos.close();
            System.out.println("Jobs enregistrés !");
        } catch(IOException e){
            System.out.println("Le fichier des jobs n'est pas créé.");
        }
    }
    private static Hashtable<String, Job> getJobsFromFile() throws JobException {
        try {
            ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            Object obj = oos.readObject();
            oos.close();
            if (obj.getClass().equals(Hashtable.class)) {
                return (Hashtable<String, Job>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            file.delete();
        }
        throw new JobException("An error occured while the job's loading.");
    }
    public static Optional<Job> getJob(String pseudo) {
        if(jobs.containsKey(pseudo)){
            return Optional.of(jobs.get(pseudo));
        } else {
            return Optional.empty();
        }
    }
    public static void setJob(String pseudo, Job job){
        jobs.put(pseudo, job);
    }

}
