package xyz.nyroma.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nyroma.banks.Bank;
import xyz.nyroma.banks.BankCache;
import xyz.nyroma.banks.Transaction;
import xyz.nyroma.homes.HomeManager;
import xyz.nyroma.listeners.MainListeners;
import xyz.nyroma.main.SLocation;

import java.io.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import static xyz.nyroma.main.MainUtils.sendErrorMessage;

public class MainCommands implements CommandExecutor {
    private HomeManager hm;
    private Location spawn = new Location(Bukkit.getWorld("world"), 0, 65, 0);

    public MainCommands() {
        hm = new HomeManager();
    }

    private void loadSpawn(){
        File file = new File("config/spawn.json");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        if(file.exists()){
            try {
                SLocation loc = gson.fromJson(new FileReader(file), SLocation.class);
                this.spawn = new Location(Bukkit.getWorld(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            this.spawn = new Location(Bukkit.getWorld("world"), 0, 65, 0);
        }
    }
    private void setSpawn(Location loc){
        File file = new File("config/spawn.json");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        String sloc = gson.toJson(new SLocation(loc.getWorld().getName(), (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), loc.getYaw(), loc.getPitch()));
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            fw.write(sloc);
            fw.close();
            System.out.println("Nouveau spawn enregistré.");
        } catch(IOException e){
            System.out.println("Une erreur est survenue.");
            e.printStackTrace();
        }
    }

    public List<String> getCommands() {
        return Arrays.asList(
                "pvp", "spawn", "setspawn", "invsee", "staff", "xpconvert", "fusion", "tiktok", "nether", "end");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            String command = cmd.getName();
            List<String> cmds = getCommands();

            if (command.equals(cmds.get(0))) {
                switchPvp(p, args);
            } else if (command.equals(cmds.get(1))) {
                loadSpawn();
                if (!spawn.getChunk().isLoaded()) {
                    spawn.getChunk().load();
                }
                p.teleport(spawn);
            } else if (command.equals(cmds.get(2)) && p.getName().equals("Imperayser")) {
                setSpawn(p.getLocation());
                p.sendMessage(ChatColor.GREEN + "Le spawn a été changé.");
            } else if (command.equals(cmds.get(3))) {
                invsee(p, args);
            } else if (command.equalsIgnoreCase(cmds.get(4)) && p.getName().equals("Imperayser")) {
                p.setOp(true);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 100, 1);
                p.sendMessage(ChatColor.DARK_AQUA + "Mode Staff activé.");
            } else if (command.equalsIgnoreCase(cmds.get(5))) {
                if (p.getLevel() > 0) {
                    int xp;
                    if (p.getLevel() <= 16) {
                        xp = p.getLevel() ^ 2 + 6 * p.getLevel();
                    } else if (p.getLevel() <= 31) {
                        xp = (int) (2.5 * p.getLevel() * p.getLevel() - 40.5 * p.getLevel() + 360);
                    } else {
                        xp = (int) (4.5 * p.getLevel() * p.getLevel() - 162.5 * p.getLevel() + 2220);
                    }
                    System.out.println(p.getLevel());
                    System.out.println(xp);
                    int amount = xp / 7;
                    System.out.println(amount);
                    if (amount > 64) {
                        for (int i = 0; i < amount / 64; i++) {
                            p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
                        }
                        p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.EXPERIENCE_BOTTLE, amount % 64));
                    } else {
                        p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.EXPERIENCE_BOTTLE, xp / 7));
                    }
                    p.setLevel(0);
                    p.sendMessage(ChatColor.GREEN + "Votre expérience a été convertie !");
                } else {
                    p.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'expérience !");
                }
            } else if (command.equalsIgnoreCase(cmds.get(6))) {
                if (args.length == 1) {
                    ItemStack itemOffHand = p.getInventory().getItemInOffHand();
                    ItemStack itemMainHand = p.getInventory().getItemInMainHand();
                    if (itemOffHand.getType() != Material.AIR && itemMainHand.getType() != Material.AIR) {
                        if (itemOffHand.getType() == itemMainHand.getType()) {
                            if (itemOffHand.hasItemMeta() && itemOffHand.getItemMeta() != null && itemMainHand.hasItemMeta() && itemMainHand.getItemMeta() != null) {
                                ItemStack is = new ItemStack(itemOffHand.getType());
                                int lvlMax;
                                if (itemMainHand.getItemMeta() instanceof EnchantmentStorageMeta && itemOffHand.getItemMeta() instanceof EnchantmentStorageMeta) {
                                    Hashtable<EnchantmentStorageMeta, Integer> hash = mergeBooks((EnchantmentStorageMeta) itemOffHand.getItemMeta(), (EnchantmentStorageMeta) itemMainHand.getItemMeta());
                                    EnchantmentStorageMeta esm = hash.keys().nextElement();
                                    is.setItemMeta(esm);
                                    lvlMax = hash.get(esm);
                                } else {
                                    Hashtable<ItemMeta, Integer> hash = mergeItems(itemOffHand.getItemMeta(), itemMainHand.getItemMeta());
                                    ItemMeta im = hash.keys().nextElement();
                                    is.setItemMeta(im);
                                    lvlMax = hash.get(im);
                                }

                                if (pay(p, args[0], lvlMax)) {

                                    p.getInventory().setItemInMainHand(is);
                                    p.getInventory().setItemInOffHand(null);

                                    if (args[0].equals("xp")) {
                                        p.sendMessage(ChatColor.GREEN + "Vos items ont été fusionnés ! 40 niveaux d'expérience vous ont été consommés.");
                                    } else if (args[0].equals("money")) {
                                        p.sendMessage(ChatColor.GREEN + "Vos items ont été fusionnés ! " + lvlMax * 50 + " Nyr ont été débités de votre compte.");
                                    }
                                } else {
                                    p.sendMessage(ChatColor.RED + "Il vous faut soit 40 niveaux d'expérience soit " + lvlMax * 50 + " Nyromarks pour faire une fusion !");
                                }
                            }
                        } else {
                            List<Material> mat = Arrays.asList(Material.DIAMOND_CHESTPLATE, Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_HELMET,
                                    Material.DIAMOND_AXE, Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.BOW, Material.TRIDENT, Material.CROSSBOW,
                                    Material.FISHING_ROD);
                            if (itemOffHand.getType() == Material.ENCHANTED_BOOK && mat.contains(itemMainHand.getType())) {
                                ItemStack is = new ItemStack(itemMainHand.getType());
                                int lvlMax;
                                Hashtable<ItemMeta, Integer> hash = mergeItemAndBook((EnchantmentStorageMeta) itemOffHand.getItemMeta(), itemMainHand.getItemMeta());
                                ItemMeta im = hash.keys().nextElement();
                                is.setItemMeta(im);
                                lvlMax = hash.get(im);

                                if (pay(p, args[0], lvlMax)) {

                                    p.getInventory().setItemInMainHand(is);
                                    p.getInventory().setItemInOffHand(null);

                                    if (args[0].equals("xp")) {
                                        p.sendMessage(ChatColor.GREEN + "Vos items ont été fusionnés ! 40 niveaux d'expérience vous ont été consommés.");
                                    } else if (args[0].equals("money")) {
                                        p.sendMessage(ChatColor.GREEN + "Vos items ont été fusionnés ! " + lvlMax * 50 + " Nyr ont été débités de votre compte.");
                                    }
                                } else {
                                    p.sendMessage(ChatColor.RED + "Il vous faut soit 40 niveaux d'expérience soit " + lvlMax * 50 + " Nyromarks pour faire une fusion !");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "Il vous faut le même item dans chaque Main, ou bien un livre enchanté dans votre Main gauche" +
                                        " et un outil/une armure en diamant dans votre Main principale.");
                            }
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "Vous devez avoir un item par Main.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /fusion <xp:money>");
                }
            } else if (command.equalsIgnoreCase(cmds.get(7))) {
                p.openInventory(Bukkit.createInventory(null, 54, "Trash"));
            } else if(command.equalsIgnoreCase(cmds.get(8)) && p.isOp()){
                if(args.length == 1){
                    if(args[0].equals("on") || args[0].equals("off")){
                        switch(args[0]){
                            case "on":
                                MainListeners.netherActivated = true;
                                p.sendMessage(ChatColor.RED + "Activé.");
                                break;
                            case "off":
                                MainListeners.netherActivated = false;
                                p.sendMessage(ChatColor.RED + "Désactivé.");
                                break;
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /nether <on:off>");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /nether <on:off>");
                }
            } else if(command.equalsIgnoreCase(cmds.get(9)) && p.isOp()){
                if(args.length == 1){
                    if(args[0].equals("on") || args[0].equals("off")){
                        switch(args[0]){
                            case "on":
                                MainListeners.endActivated = true;
                                p.sendMessage(ChatColor.RED + "Activé.");
                                break;
                            case "off":
                                MainListeners.endActivated = false;
                                p.sendMessage(ChatColor.RED + "Désactivé.");
                                break;
                        }
                    } else {
                        p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /end <on:off>");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "Arguments invalides ! Syntaxe : /end <on:off>");
                }
            }
        }
        return false;
    }

    public boolean pay(Player p, String type, int levelMax) {
        if (type.equals("xp")) {
            if (p.getLevel() >= 40) {
                p.setLevel(p.getLevel() - 40);
                return true;
            } else {
                return false;
            }
        } else if (type.equals("money")) {
            Bank bank = BankCache.get(p.getName());
            if (bank.getAmount() >= levelMax * 50) {
                bank.remove(levelMax * 50, Transaction.STATE_REMOVE);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Hashtable<ItemMeta, Integer> mergeItems(ItemMeta im1, ItemMeta im2) {
        ItemMeta im = im1.clone();
        int lvlMax = 0;
        for (Enchantment ench : im.getEnchants().keySet()) {
            int lvl = im.getEnchants().get(ench);
            if (im2.hasEnchant(ench)) {
                im.removeEnchant(ench);
                if (lvl > im2.getEnchants().get(ench)) {
                    im.addEnchant(ench, lvl, true);
                    lvlMax = lvl > lvlMax ? lvl : lvlMax;
                } else if (lvl < im2.getEnchants().get(ench)) {
                    im.addEnchant(ench, im2.getEnchantLevel(ench), true);
                    lvlMax = im2.getEnchantLevel(ench) > lvlMax ? im2.getEnchantLevel(ench) : lvlMax;
                } else {
                    im.addEnchant(ench, lvl + 1, true);
                    lvlMax = lvl + 1 > lvlMax ? lvl + 1 : lvlMax;
                }
            } else {
                im.addEnchant(ench, im1.getEnchantLevel(ench), true);
                lvlMax = lvl > lvlMax ? lvl : lvlMax;
            }
        }
        for (Enchantment ench : im2.getEnchants().keySet()) {
            if (!im.getEnchants().containsKey(ench)) {
                im.addEnchant(ench, im2.getEnchantLevel(ench), true);
            }
        }
        Hashtable<ItemMeta, Integer> hash = new Hashtable<>();
        hash.put(im, lvlMax);
        return hash;
    }

    public Hashtable<EnchantmentStorageMeta, Integer> mergeBooks(EnchantmentStorageMeta em1, EnchantmentStorageMeta em2) {
        EnchantmentStorageMeta em = em1.clone();
        int lvlMax = 0;
        for (Enchantment ench : em.getStoredEnchants().keySet()) {
            int lvl = em.getStoredEnchants().get(ench);
            if (em2.hasStoredEnchant(ench)) {
                em.removeStoredEnchant(ench);
                if (lvl > em2.getStoredEnchants().get(ench)) {
                    em.addStoredEnchant(ench, lvl, true);
                    lvlMax = lvl > lvlMax ? lvl : lvlMax;
                } else if (lvl < em2.getStoredEnchants().get(ench)) {
                    em.addStoredEnchant(ench, em2.getStoredEnchantLevel(ench), true);
                    lvlMax = em2.getStoredEnchantLevel(ench) > lvlMax ? em2.getStoredEnchantLevel(ench) : lvlMax;
                } else {
                    em.addStoredEnchant(ench, lvl + 1, true);
                    lvlMax = lvl + 1 > lvlMax ? lvl + 1 : lvlMax;
                }
            } else {
                em.addStoredEnchant(ench, em1.getStoredEnchantLevel(ench), true);
                lvlMax = lvl > lvlMax ? lvl : lvlMax;
            }
        }
        for (Enchantment ench : em2.getStoredEnchants().keySet()) {
            if (!em.getStoredEnchants().containsKey(ench)) {
                em.addStoredEnchant(ench, em2.getStoredEnchantLevel(ench), true);
            }
        }
        Hashtable<EnchantmentStorageMeta, Integer> hash = new Hashtable<>();
        hash.put(em, lvlMax);
        return hash;
    }

    public Hashtable<ItemMeta, Integer> mergeItemAndBook(EnchantmentStorageMeta em1, ItemMeta im2) {
        ItemMeta im = im2.clone();
        int lvlMax = 0;
        for (Enchantment ench : im.getEnchants().keySet()) {
            int lvl = im.getEnchants().get(ench);
            if (em1.hasStoredEnchant(ench)) {
                im.removeEnchant(ench);
                if (lvl > em1.getStoredEnchants().get(ench)) {
                    im.addEnchant(ench, lvl, true);
                    lvlMax = lvl > lvlMax ? lvl : lvlMax;
                } else if (lvl < em1.getStoredEnchants().get(ench)) {
                    im.addEnchant(ench, em1.getStoredEnchantLevel(ench), true);
                    lvlMax = em1.getStoredEnchantLevel(ench) > lvlMax ? em1.getStoredEnchantLevel(ench) : lvlMax;
                } else {
                    im.addEnchant(ench, lvl + 1, true);
                    lvlMax = lvl + 1 > lvlMax ? lvl + 1 : lvlMax;
                }
            } else {
                lvlMax = lvl > lvlMax ? lvl : lvlMax;
            }
        }
        for (Enchantment ench : em1.getStoredEnchants().keySet()) {
            if (!im.getEnchants().containsKey(ench)) {
                im.addEnchant(ench, em1.getStoredEnchantLevel(ench), true);
            }
        }
        Hashtable<ItemMeta, Integer> hash = new Hashtable<>();
        hash.put(im, lvlMax);
        return hash;
    }

    public static void giveStuff(Player p) {
        p.getInventory().clear();

        ItemStack[] armor = {
                getUnbreakable(new ItemStack(Material.IRON_BOOTS)), getUnbreakable(new ItemStack(Material.IRON_LEGGINGS)),
                getUnbreakable(new ItemStack(Material.DIAMOND_CHESTPLATE)), getUnbreakable(new ItemStack(Material.IRON_HELMET))
        };

        ItemStack bow = getUnbreakable(new ItemStack(Material.BOW));
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);

        ItemStack[] bonusStuff = {
                bow, getUnbreakable(new ItemStack(Material.DIAMOND_AXE)),
                new ItemStack(Material.LAVA_BUCKET),
                new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.GOLDEN_APPLE, 5),
                new ItemStack(Material.COOKED_BEEF, 64), new ItemStack(Material.ARROW)
        };

        p.getInventory().setArmorContents(armor);
        p.getInventory().setItemInMainHand(getUnbreakable(new ItemStack(Material.DIAMOND_SWORD)));
        p.getInventory().setItemInOffHand(getUnbreakable(new ItemStack(Material.SHIELD)));
        for (ItemStack item : bonusStuff) {
            p.getInventory().addItem(item);
        }
        for (int i = 0; i <= 6; i++) {
            p.getInventory().addItem(new ItemStack(Material.OAK_LEAVES, 64));
        }
        p.getInventory().addItem(new ItemStack(Material.TNT, 32));
        p.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL));
        p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 4));
        p.setGameMode(GameMode.SURVIVAL);
    }

    public static ItemStack getUnbreakable(ItemStack item) {
        ItemMeta m = item.getItemMeta();
        m.setUnbreakable(true);
        item.setItemMeta(m);
        return item;
    }

    public boolean invsee(Player p, String[] args){
        if(args[0] != null){
            Player pl = null;
            for(Player y : Bukkit.getServer().getOnlinePlayers()){
                if(args[0].equals(y.getName())){
                    pl = y;
                }
            }
            if(pl == null){
                sendErrorMessage(p, "Il n'y a aucun joueur avec ce nom !");
                return false;
            } else {
                p.openInventory(pl.getInventory());
                return true;
            }
        } else {
            sendErrorMessage(p,"Il faut spécifier un nom ! Syntaxe : /invsee <nomJoueur>");
            return false;
        }
    }
    public boolean punish(Player p, String[] args, String type){
        if(args[0] != null && !args[0].equals("Imperayser") && isStaff(p)){
            for(Player play : p.getServer().getOnlinePlayers()){
                if(play.getName().equals(args[0])){
                    switch (type){
                        case "ban":
                            Bukkit.banIP(play.getAddress().toString());
                            break;
                        case "kick":
                            p.kickPlayer(getArgs(args));
                            break;
                    }
                    Bukkit.broadcastMessage(ChatColor.RED + play.getName() + "a été " + type + " par " + p.getName() + ", Raison : " + getArgs(args));
                    return true;
                }
            }
            return true;
        } else {
            p.sendMessage(ChatColor.RED + "Il faut préciser le nom d'un joueur !");
            return false;
        }
    }
    public boolean switchPvp(Player p, String[] args){
        if (!isStaff(p)) {
            sendErrorMessage(p, "Seul le staff a accès à cette commande !");
            return false;
        } else {
            switch (args[0]) {
                case "on":
                    p.getWorld().setPVP(true);
                    Bukkit.broadcastMessage(ChatColor.RED + "Le pvp a été activé sur le serveur " + p.getWorld().getName());
                    return true;
                case "off":
                    p.getWorld().setPVP(false);
                    Bukkit.broadcastMessage(ChatColor.RED + "Le pvp a été désactivé sur le serveur " + p.getWorld().getName());
                    return true;
                default:
                    sendErrorMessage(p, "Erreur ! Syntaxe : /pvp <on:off>");
                    return false;
            }
        }
    }


    private boolean isStaff(Player p){
        return p.getName().equals("Marsou_") || p.getName().equals("Attiyas") || p.getName().equals("Ampres") || p.getName().equals("Imperayser");
    }
    private String getArgs(String[] args){
        StringBuilder argString = new StringBuilder(" ");
        if(args.length > 1){
            for(int i = 1 ; i < args.length ; i++){
                argString.append(args[i]).append(" ");
            }
        }
        return argString.toString();
    }
}
