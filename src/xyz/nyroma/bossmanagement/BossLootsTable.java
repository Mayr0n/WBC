package xyz.nyroma.bossmanagement;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class BossLootsTable {
    private List<ItemStack> commonLoots;
    private List<ItemStack> rareLoots;

    public BossLootsTable(List<ItemStack> commonLoots, List<ItemStack> rareLoots) {
        this.commonLoots = commonLoots;
        this.rareLoots = rareLoots;
    }

    public List<ItemStack> getLoots(Rarety rarety) {
        if(rarety == Rarety.COMMON){
            return this.commonLoots;
        } else {
            return this.rareLoots;
        }
    }

    public ItemStack getLoot(Rarety rarety) {
        if(new Random().nextInt(getLoots(rarety).size() + 3) <= getLoots(rarety).size()){
            return this.commonLoots.get(new Random().nextInt(this.commonLoots.size()));
        } else {
            return BossLoots.getGenericLoot(rarety);
        }
    }
}
