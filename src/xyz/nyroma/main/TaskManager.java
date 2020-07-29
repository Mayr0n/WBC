package xyz.nyroma.main;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nyroma.banks.BankCache;
import xyz.nyroma.banks.Transaction;
import xyz.nyroma.bossmanagement.BossLoots;
import xyz.nyroma.bourseAPI.BourseCache;
import xyz.nyroma.bourseAPI.Item;
import xyz.nyroma.cityapi.citymanagement.CitiesCache;
import xyz.nyroma.cityapi.citymanagement.City;
import xyz.nyroma.cityapi.enums.RelationStatus;
import xyz.nyroma.listeners.CityListeners;

import java.util.Arrays;
import java.util.Hashtable;

import static xyz.nyroma.main.MainUtils.getTime;

public class TaskManager {
    private JavaPlugin plugin;
    private Server server;
    private Hashtable<String, Integer> timings = new Hashtable<>();

    public TaskManager(JavaPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    public void build() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : server.getOnlinePlayers()) {
                    applyEffects(p);
                    checkClaims(p);
                    checkEnemy(p);
                }
            }
        }.runTaskTimer(plugin, 5, 5);

        ScoreboardManager sm = new ScoreboardManager(server);
        sm.build();

        new BukkitRunnable() {
            @Override
            public void run() {
                sm.refresh();
                if (getTime().equals("23:59:59")) {
                    for (Item item : BourseCache.items) {
                        if (item.getStocks() == 0) {
                            item.setPrix(item.getPrix() * 1.01f);
                        }
                    }
                    Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "Le prix des items de la bourse dont les stocks sont nuls ont augmenté de 1%.");
                }
                if (getTime().equals("23:59:59") || getTime().equals("11:59:59")) {
                    Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Lancement de la tournée des taxes !");
                    for (City city : CitiesCache.getCities()) {
                        switch (city.applyTaxes()) {
                            case PAYED:
                                sendToAllPlayerInCity(city, ChatColor.GREEN + "Votre ville a été débitée de " + city.getTaxes() + " Nyr.");
                                break;
                            case BROKEN:
                                sendToAllPlayerInCity(city, ChatColor.RED + "Votre ville n'a pas pu être débitée de " + city.getTaxes() + " Nyr, elle est passée en faillite. Vous avez 12h" +
                                        " pour amasser assez d'argent afin de payer les prochaines taxes.");
                                break;
                            case REMOVED:
                                sendToAllPlayerInCity(city, ChatColor.DARK_RED + "Votre ville était en faillite, et vous n'avez pas pu payer la tournée des taxes, donc votre ville a été supprimée.");
                        }
                    }
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Fin de la tournée des taxes !");
                    for (Item item : BourseCache.items) {
                        int amount = 0.005 * item.getStocks() <= 1 ? 0 : (int) (0.005 * item.getStocks());
                        item.buy(amount);
                    }
                    Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "L'état a acheté des stocks à tous les items de la bourse.");
                }

                if (getTime().split(":")[1].equals("00") && getTime().split(":")[2].equals("00")) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "C'est ma tournée ! 5 Nyromarks pour tous les joueurs connectés !");
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        BankCache.get(p.getName()).add(5, Transaction.AUTO_ADD);
                    }
                }
            }
        }.runTaskTimer(plugin, 20, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                sm.setScoreboard(server);
            }
        }.runTaskTimer(plugin, 60 * 20, 60 * 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                CitiesCache.serializeAll();
                BourseCache.serializeAll();
            }
        }.runTaskTimer(plugin, 600 * 20L, 600 * 20L);


    }

    public void sendToAllPlayerInCity(City city, String txt) {
        for (String pseudo : city.getMembers()) {
            if (MainUtils.getPlayerByName(pseudo).isPresent()) {
                MainUtils.getPlayerByName(pseudo).get().sendMessage(txt);
            }
        }
    }

    public void applyEffects(Player p) {
        for (ItemStack is : p.getInventory().getContents()) {
            if (is != null) {
                if (isLoot(is, BossLoots.getJumper())) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, 1));
                }
                if (isLoot(is, BossLoots.getSpeeder())) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 1));
                }
                if (isLoot(is, BossLoots.getStrengthener())) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 1));
                }
                if (isLoot(is, BossLoots.getIRvision())) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 300, 1));
                }
            }
        }
        ItemStack im = p.getInventory().getItemInOffHand();
        if (im.getType().equals(Material.FEATHER) && im.hasItemMeta() && im.getItemMeta() != null && im.getItemMeta().hasEnchants()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 5));
        }
    }

    public void checkClaims(Player p) {
        if (CityListeners.getClaimerFromLoc(p.getLocation()).isPresent()) {
            City city = CityListeners.getClaimerFromLoc(p.getLocation()).get();
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED + "- Claim par " + city.getName() + " -"));
        } else {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_GREEN + "- Territoire libre -"));
        }
    }

    private void checkEnemy(Player p) {
        Location loc = p.getLocation();
        if (CityListeners.getClaimerFromLoc(loc).isPresent()) {
            City city = CityListeners.getClaimerFromLoc(loc).get();
            if (city.getRelationStatus(p.getName()) == RelationStatus.ENEMY) {
                if (timings.keySet().contains(p.getName())) {
                    int time = timings.get(p.getName());
                    if (time >= 5 * 20) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 4));
                        timings.put(p.getName(), 0);
                        p.sendTitle("ATTENTION", "Vous êtes entré dans un claim ennemi", 10, 40, 10);
                    } else {
                        timings.put(p.getName(), time + 5);
                        p.sendTitle("ATTENTION", "Vous êtes entré dans un claim ennemi", 10, 40, 10);
                    }
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 4));
                } else {
                    timings.put(p.getName(), 0);
                    p.sendTitle("ATTENTION", "Vous êtes entré dans un claim ennemi", 10, 40, 10);
                }
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 4));
            }
        }
    }
    private boolean isLoot(ItemStack itemToCompare, ItemStack loot){
        return itemToCompare.getItemMeta() != null && itemToCompare.getItemMeta().getLore() != null &&
                itemToCompare.getItemMeta().getLore().equals(loot.getItemMeta().getLore()) &&
                itemToCompare.getItemMeta().getDisplayName().equals(loot.getItemMeta().getDisplayName());
    }
}
