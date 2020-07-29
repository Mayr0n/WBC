package xyz.nyroma.bossmanagement;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public enum BossType {
    ZOMBIE(new BossLootsTable(
            Arrays.asList(BossLoots.getHungerSword(), BossLoots.getSatur()),
            Arrays.asList(BossLoots.getStrengthener(), BossLoots.getSatur())
    )),
    CREEPER(new BossLootsTable(
            Arrays.asList(BossLoots.getSpeeder(), new ItemStack(Material.TNT, 64)),
            Arrays.asList(BossLoots.getThunderStick())
    )),
    PHANTOM(new BossLootsTable(
            Arrays.asList(new ItemStack(Material.ELYTRA)),
            Arrays.asList(new ItemStack(Material.ELYTRA))
    )),
    SPIDER(new BossLootsTable(
            Arrays.asList(BossLoots.getIRvision()),
            Arrays.asList(BossLoots.getPoisonSword())
    )),
    SKELETON(new BossLootsTable(
            Arrays.asList(BossLoots.getPouf(), BossLoots.getFarmerHoe()),
            Arrays.asList(BossLoots.getWitherSword())
    ));

    BossLootsTable loots;

    BossType(BossLootsTable loots){
        this.loots = loots;
    }

    public BossLootsTable getLoots() {
        return loots;
    }
}
