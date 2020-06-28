package xyz.nyroma.main;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.nyroma.banks.BankCache;
import xyz.nyroma.banks.Transaction;
import xyz.nyroma.betterItems.BetterArmor;
import xyz.nyroma.betterItems.BetterArmors;
import xyz.nyroma.bourseAPI.BourseCache;
import xyz.nyroma.bourseAPI.Item;
import xyz.nyroma.towny.citymanagement.CitiesCache;
import xyz.nyroma.towny.citymanagement.City;
import xyz.nyroma.towny.citymanagement.CityManager;

import static xyz.nyroma.main.MainUtils.getTime;

public class TaskManager {
    private JavaPlugin plugin;
    private Server server;
    private CityManager cm = new CityManager();

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

                if(getTime().equals("23:59:59") || getTime().equals("11:59:59")){
                    Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Lancement de la tournée des taxes !");
                    for(City city : CitiesCache.getCities()){
                        switch(cm.applyTaxes(city)){
                            case PAYED:
                                sendToAllPlayerInCity(city, ChatColor.GREEN + "Votre ville a été débitée de " + city.getMoneyManager().getTaxes() + " Nyr.");
                                break;
                            case BROKEN:
                                sendToAllPlayerInCity(city, ChatColor.RED + "Votre ville n'a pas pu être débitée de " + city.getMoneyManager().getTaxes() + " Nyr, elle est passée en faillite. Vous avez 12h" +
                                        " pour amasser assez d'argent afin de payer les prochaines taxes.");
                                break;
                            case REMOVED:
                                sendToAllPlayerInCity(city, ChatColor.DARK_RED + "Votre ville était en faillite, et vous n'avez pas pu payer la tournée des taxes, donc votre ville a été supprimée.");
                        }
                    }
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Fin de la tournée des taxes !");
                    for(Item item : BourseCache.items){
                        int amount = 0.005*item.getStocks() <= 1 ? 0 : (int) (0.005 * item.getStocks());
                        item.buy(amount);
                    }
                    Bukkit.broadcastMessage(ChatColor.GREEN + "L'état a acheté des stocks à tous les items de la bourse.");
                }

                if(getTime().split(":")[1].equals("00") && getTime().split(":")[2].equals("00")){
                    Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "C'est ma tournée ! 5 Nyromarks pour tous les joueurs connectés !");
                    for(Player p : Bukkit.getServer().getOnlinePlayers()){
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
        }.runTaskTimer(plugin, 600*20L, 600*20L);


    }

    public void sendToAllPlayerInCity(City city, String txt){
        for(String pseudo : city.getMembersManager().getMembers()){
            if(MainUtils.getPlayerByName(pseudo).isPresent()){
                MainUtils.getPlayerByName(pseudo).get().sendMessage(txt);
            }
        }
    }

    public void applyEffects(Player p) {
            ItemStack is = p.getInventory().getItemInMainHand();
            if (is.getType().equals(Material.GOLDEN_AXE) && is.hasItemMeta() && is.getItemMeta() != null
                    && is.getItemMeta().hasEnchants() && is.getItemMeta().getEnchants().size() > 0 && is.getItemMeta().getEnchants().containsValue(10)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2));
            }

            for (ItemStack item : p.getInventory().getArmorContents()) {
                if(item != null) {
                    if (item.hasItemMeta() && item.getItemMeta() != null) {
                        ItemMeta im = item.getItemMeta();
                        if (im.hasLore() && im.getLore() != null && im.getLore().size() > 0) {
                            BetterArmor[] bt = new BetterArmor[]{new BetterArmor(BetterArmors.NIGHT_HELMET),
                                    new BetterArmor(BetterArmors.WINGED_CHESTPLATE),
                                    new BetterArmor(BetterArmors.SPEEDY_LEGGINGS),
                                    new BetterArmor(BetterArmors.JUMPER_BOOTS),
                                    new BetterArmor(BetterArmors.HEROES_CHESTPLATE),
                                    new BetterArmor(BetterArmors.XRAY_HELMET)
                            };
                            for (BetterArmor ba : bt) {
                                for (int i = 1; i <= 5; i++) {
                                    ItemStack tool = ba.getItemStack(i);
                                    if (im.getLore().equals(tool.getItemMeta().getLore())) {
                                        if (i < 5) {
                                            if(ba.getEffect() != PotionEffectType.GLOWING) {
                                                p.addPotionEffect(ba.getEffect().createEffect(300, i - 1));
                                            } else {
                                                for(Entity entity : p.getNearbyEntities(10,10,10)){
                                                    if(entity instanceof LivingEntity){
                                                        LivingEntity le = (LivingEntity) entity;
                                                        le.addPotionEffect(ba.getEffect().createEffect(10, i - 1));
                                                    }
                                                }
                                            }
                                        } else {
                                            if(ba.getEffect() != PotionEffectType.GLOWING) {
                                                p.addPotionEffect(ba.getEffect().createEffect(300, (int) (i * 1.5)));
                                            } else {
                                                for(Entity entity : p.getNearbyEntities(25,25,25)){
                                                    if(entity instanceof LivingEntity){
                                                        LivingEntity le = (LivingEntity) entity;
                                                        le.addPotionEffect(ba.getEffect().createEffect(10, (int) (i * 1.5)));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ItemStack im = p.getInventory().getItemInOffHand();
            if (im.getType().equals(Material.FEATHER) && im.hasItemMeta() && im.getItemMeta() != null && im.getItemMeta().hasEnchants()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 5));
            }
    }

    public void checkClaims(Player p) {
        Location loc = p.getLocation();
        if (MainUtils.getClaimer(loc).isPresent()) {
            City city = MainUtils.getClaimer(loc).get();
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED + "- Claim par " + city.getName() + " -"));
        } else {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_GREEN + "- Territoire libre -"));
        }
    }

    private void checkEnemy(Player p) {
        Location loc = p.getLocation();
        if (MainUtils.getClaimer(loc).isPresent()) {
            City city = MainUtils.getClaimer(loc).get();
            if(new CityManager().getCityOfMember(p.getName()).isPresent()){
                City city2 = new CityManager().getCityOfMember(p.getName()).get();
                if(city.getRelationsManager().getEnemies().contains(city2.getName())){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 5));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 5));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 5));
                }
            }
        }
    }
}
