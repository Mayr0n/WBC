package xyz.nyroma.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.nyroma.main.MainUtils;
import xyz.nyroma.towny.citymanagement.CitiesCache;
import xyz.nyroma.towny.citymanagement.City;
import xyz.nyroma.towny.citymanagement.CityManager;
import xyz.nyroma.towny.enums.RelationStatus;
import xyz.nyroma.towny.enums.WarType;

import java.util.List;
import java.util.Optional;

public class CityListeners implements Listener {
    private CityManager cm = new CityManager();

    @EventHandler
    public void hasInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) && !p.isOp()) {
            if (e.getClickedBlock() != null) {
                Location loc = e.getClickedBlock().getLocation();
                if (cantInteract(loc, p).isPresent()) {
                    City city = cantInteract(loc, p).get();
                    switch (city.getRelationStatus(p.getName())) {
                        case ALLY:
                            break;
                        case ENEMY:
                            p.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 1));
                        case NEUTRAL:
                            p.sendMessage(ChatColor.RED + "Ce chunk appartient à " + city.getName() + ", tu ne peux interagir avec ce block !");
                            e.setCancelled(true);
                            break;
                    }
                }
            }
        } else if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            ItemStack is = p.getInventory().getItemInMainHand();
            if (is.getType().equals(Material.PAPER)) {
                if (is.hasItemMeta() && is.getItemMeta() != null) {
                    ItemMeta im = is.getItemMeta();
                    if (is.containsEnchantment(Enchantment.DURABILITY) && im.hasLore() && im.getLore() != null && im.getLore().size() == 4) {
                        List<String> lore = im.getLore();
                        if (CitiesCache.get(lore.get(1)).isPresent()) {
                            City city = CitiesCache.get(lore.get(1)).get();
                            city.getMembersManager().addMember(lore.get(3));
                            for (String pseudo : city.getMembersManager().getMembers()) {
                                if (MainUtils.getPlayerByName(pseudo).isPresent()) {
                                    Player play = MainUtils.getPlayerByName(pseudo).get();
                                    play.sendMessage(ChatColor.GREEN + p.getName() + " a rejoint la ville " + city.getName() + " !");

                                } else {
                                    p.sendMessage(ChatColor.RED + "Une erreur est survenue. CityListeners:62");
                                }
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Cette ville n'existe plus.");
                        }
                        p.getInventory().setItemInMainHand(null);
                    } else if(is.containsEnchantment(Enchantment.DAMAGE_ALL) && im.hasLore() && im.getLore() != null && im.getLore().size() == 5){
                        CityManager cm = new CityManager();
                        if(cm.getCityOfMember(p.getName()).isPresent()){
                            City city = cm.getCityOfMember(p.getName()).get();
                            List<String> lore = im.getLore();
                            if(CitiesCache.get(lore.get(1).substring(2)).isPresent()){
                                City enemy = CitiesCache.get(lore.get(1).substring(2)).get();
                                int prime = Integer.parseInt(lore.get(3).split(" ")[0].substring(2));
                                WarType type;
                                if(prime >= 1500){
                                    type = WarType.HARD;
                                } else {
                                    type = WarType.SOFT;
                                }
                                city.getWarManager().declareWar(enemy, type, prime);
                                Bukkit.broadcastMessage(ChatColor.BLACK + "Une guerre de type " + type.toString() + " a été déclarée entre la ville " + city.getName() + " et " + enemy.getName() + " !");
                            }
                        } else {
                            p.sendMessage(ChatColor.RED + "Vous n'avez pas de ville !");
                        }
                        p.getInventory().setItemInMainHand(null);
                    }
                }
            }
        }
    }

    @EventHandler
    public void tntExplode(EntityExplodeEvent e) {
        Entity ent = e.getEntity();
        if (ent instanceof TNTPrimed) {
            Location loc = ent.getLocation();
            if (MainUtils.getClaimer(loc).isPresent()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent e) {
        for (Block b : e.getBlocks()) {
            if (b.getType().equals(Material.SLIME_BLOCK) || b.getType().equals(Material.HONEY_BLOCK)) {
                Location loc = b.getLocation();
                if (MainUtils.getClaimer(loc).isPresent()) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void ifHit(EntityDamageByEntityEvent e) {
        Entity ent = e.getEntity();
        Location loc = ent.getLocation();
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            if (cantInteract(loc, p).isPresent()) {
                City city = cantInteract(loc, p).get();
                if (!(ent instanceof Creature)) {
                    p.sendMessage(ChatColor.RED + "Ce chunk appartient à " + city + ", tu ne peux pas taper ce mob !");
                    e.setCancelled(true);
                }
                if (ent instanceof Villager) {
                    String message = ChatColor.BLACK + p.getName() + " a frappé un PNJ dans la ville " + city.getName() + " !";
                    Bukkit.broadcastMessage(message);
                    System.out.println(message);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 600, 50));
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Villager) {
            Location loc = e.getEntity().getLocation();
            if (MainUtils.getClaimer(loc).isPresent()) {
                if (!e.getCause().equals(EntityDamageEvent.DamageCause.FIRE)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void openInv(InventoryOpenEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player p = (Player) e.getPlayer();
            Inventory inv = e.getInventory();
            Location loc = p.getLocation();
            if (cantInteract(loc, p).isPresent() && inv.getType() == InventoryType.MERCHANT) {
                City city = cantInteract(loc, p).get();
                p.sendMessage(ChatColor.RED + "Ce chunk appartient à " + city.getName() + ", tu ne peux pas interagir avec ce PNJ !");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        if (cantInteract(loc, p).isPresent()) {
            City city = cantInteract(loc, p).get();
            p.sendMessage(ChatColor.RED + "Ce chunk appartient à " + city.getName() + ", tu ne peux pas interagir avec cette entité !");
            e.setCancelled(true);
        }
    }

    public Optional<City> cantInteract(Location loc, Player p) {
        if (MainUtils.getClaimer(loc).isPresent()) {
            City city = MainUtils.getClaimer(loc).get();
            if(!p.isOp()) {
                if (city.getRelationStatus(p.getName()) == RelationStatus.ENEMY || city.getRelationStatus(p.getName()) == RelationStatus.NEUTRAL) {
                    return Optional.of(city);
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
