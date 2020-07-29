package xyz.nyroma.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftItem;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.nyroma.capitalism.jobs.Job;
import xyz.nyroma.capitalism.jobs.JobUtils;
import xyz.nyroma.banks.Bank;
import xyz.nyroma.banks.BankCache;
import xyz.nyroma.banks.Transaction;

import java.util.List;

public class JobListeners implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        if(BankCache.get(p.getName()).remove(50, Transaction.AUTO_REMOVE)) p.sendMessage(ChatColor.RED + "Vous avez perdu 50 Nyr lors de votre mort.");
    }

    @EventHandler
    public void onFarm(PlayerItemDamageEvent e){
        ItemStack is = e.getItem();
        Player p = e.getPlayer();
        if(is.getType() == Material.WOODEN_HOE || is.getType() == Material.STONE_HOE || is.getType() == Material.IRON_HOE || is.getType() == Material.GOLDEN_HOE || is.getType() == Material.DIAMOND_HOE){
            if(JobUtils.getJob(p.getName()).isPresent()){
                if(JobUtils.getJob(p.getName()).get().equals(Job.FARMER)) {
                    Bank bank = BankCache.get(p.getName());
                    bank.add(0.01f, Transaction.JOB_ADD);
                }
            }
        }
    }

    @EventHandler
    public void onTrade(InventoryClickEvent e){
        if(e.getInventory().getType().equals(InventoryType.MERCHANT)){
            Inventory inv  = e.getInventory();
            List<HumanEntity> players = inv.getViewers();
            for(HumanEntity p : players){
                    if(JobUtils.getJob(p.getName()).isPresent()){
                        if(JobUtils.getJob(p.getName()).get().equals(Job.TRADER)) {
                            Bank bank = BankCache.get(p.getName());
                            bank.add(0.05f, Transaction.JOB_ADD);
                        }
                    }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
            Player p = (Player) e.getDamager();
            LivingEntity ent = (LivingEntity) e.getEntity();
                if(e.getDamage() >= ent.getHealth() && JobUtils.getJob(p.getName()).isPresent()){
                    if(JobUtils.getJob(p.getName()).get().equals(Job.HUNTER)) {
                        Bank bank = BankCache.get(p.getName());
                        switch (ent.getType()) {
                            case WITHER:
                            case ELDER_GUARDIAN:
                                bank.add(200, Transaction.JOB_ADD);
                                break;
                            case SHULKER:
                                bank.add(0.5f, Transaction.JOB_ADD);
                                break;
                            case WITHER_SKELETON:
                            case GHAST:
                                bank.add(0.2f, Transaction.JOB_ADD);
                                break;
                            case ENDERMAN:
                            case PIGLIN:
                                bank.add(0.05f, Transaction.JOB_ADD);
                                break;
                            case BLAZE:
                            case WITCH:
                            case GUARDIAN:
                            case MAGMA_CUBE:
                            case PHANTOM:
                                bank.add(0.1f, Transaction.JOB_ADD);
                                break;
                            case CREEPER:
                            case SKELETON:
                            case ZOMBIE:
                            case SPIDER:
                                bank.add(0.01f, Transaction.JOB_ADD);
                                break;
                        }
                    }
                }
        }
    }

    @EventHandler
    public void onDragonKilled(EntityDeathEvent e){
        if(e.getEntity().getType().equals(EntityType.ENDER_DRAGON)){
            Entity ent = e.getEntity();
            for(Entity en : ent.getNearbyEntities(20,20,20)){
                if(en instanceof Player){
                    Player p = (Player) en;
                        if(JobUtils.getJob(p.getName()).isPresent()){
                            if(JobUtils.getJob(p.getName()).get().equals(Job.HUNTER)) {
                                BankCache.get(p.getName()).add(100, Transaction.JOB_ADD);
                            }
                        }
                }
            }
        }
    }

    @EventHandler
    public void onFarm(BlockBreakEvent e) {
        Block b = e.getBlock();
        if(b.getBlockData() instanceof Ageable){
            Ageable ag = (Ageable) b.getBlockData();
            if(ag.getAge() == ag.getMaximumAge()){
                Player p = e.getPlayer();
                    if(JobUtils.getJob(p.getName()).isPresent()){
                        if(JobUtils.getJob(p.getName()).get().equals(Job.FARMER)) {
                            Bank bank = BankCache.get(p.getName());
                            bank.add(0.125f, Transaction.JOB_ADD);
                        }
                    }
            }
        }
    }

    @EventHandler
    public void onLumber(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (b.getType().equals(Material.JUNGLE_LOG) ||
                b.getType().equals(Material.ACACIA_LOG) ||
                b.getType().equals(Material.BIRCH_LOG) ||
                b.getType().equals(Material.DARK_OAK_LOG) ||
                b.getType().equals(Material.OAK_LOG) ||
                b.getType().equals(Material.SPRUCE_LOG)
        ) {
            Player p = e.getPlayer();
                if(JobUtils.getJob(p.getName()).isPresent()){
                    if(JobUtils.getJob(p.getName()).get().equals(Job.LUMBER)) {
                        Bank bank = BankCache.get(p.getName());
                        bank.add(0.25f, Transaction.JOB_ADD);
                    }
                }
        }
    }

    @EventHandler
    public void onFishing(PlayerFishEvent e) {
        Player p = e.getPlayer();
        if(e.getCaught() != null) {
            if(e.getCaught() instanceof CraftItem) {
                    if(JobUtils.getJob(p.getName()).isPresent()){
                        if(JobUtils.getJob(p.getName()).get().equals(Job.FISHER)) {
                            Bank bank = BankCache.get(p.getName());
                            bank.add(0.25f, Transaction.JOB_ADD);
                        }
                    }
            }
        }
    }

    @EventHandler
    public void onMine(BlockBreakEvent e){
        Player p = e.getPlayer();
        Block b = e.getBlock();
            if(JobUtils.getJob(p.getName()).isPresent()){
                if(JobUtils.getJob(p.getName()).get().equals(Job.MINER)) {
                    Bank bank = BankCache.get(p.getName());
                    switch (b.getType()) {
                        case OBSIDIAN:
                            bank.add(0.5f, Transaction.JOB_ADD);
                            break;
                        case DIAMOND_ORE:
                        case EMERALD_ORE:
                            bank.add(0.25f, Transaction.JOB_ADD);
                            break;
                        case END_STONE:
                        case REDSTONE_ORE:
                        case LAPIS_ORE:
                        case GOLD_ORE:
                            bank.add(0.1f, Transaction.JOB_ADD);
                            break;
                        case COBBLESTONE:
                        case STONE:
                        case STONE_BRICKS:
                        case NETHER_QUARTZ_ORE:
                            bank.add(0.05f, Transaction.JOB_ADD);
                            break;
                        case NETHERRACK:
                            bank.add(0.001f, Transaction.JOB_ADD);
                            break;
                    }
                }
            }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e){
        Player p = e.getEnchanter();

            if(JobUtils.getJob(p.getName()).isPresent()){
                if(JobUtils.getJob(p.getName()).get().equals(Job.ENCHANTER)) {
                    Bank bank = BankCache.get(p.getName());
                    bank.add(0.1f, Transaction.JOB_ADD);
                }
            }
    }

    @EventHandler
    public void onBreed(EntityBreedEvent e){
        LivingEntity ent = e.getBreeder();
        if(ent instanceof Player){
            Player p = (Player) ent;
            if(JobUtils.getJob(p.getName()).isPresent()){
                if(JobUtils.getJob(p.getName()).get() == Job.BREEDER){
                    Bank bank = BankCache.get(p.getName());
                    bank.add(0.05f, Transaction.JOB_ADD);
                }
            }
        }
    }
}
