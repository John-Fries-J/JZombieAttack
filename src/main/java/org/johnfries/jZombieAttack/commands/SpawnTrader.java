package org.johnfries.jZombieAttack.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.ArrayList;
import java.util.List;

public class SpawnTrader implements CommandExecutor {

    private final JZombieAttack plugin;

    public SpawnTrader(JZombieAttack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();

        Villager trader = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        trader.setCustomName(ChatColor.GREEN + "Axe Trader");
        trader.setCustomNameVisible(true);
        trader.setProfession(Villager.Profession.WEAPONSMITH);
        trader.setVillagerLevel(2);
        trader.setAI(false);

        List<MerchantRecipe> recipes = new ArrayList<>();

        ItemStack basicBat = plugin.getWeaponCreationEvent().createBattleAxe();
        ItemStack barbedBat = plugin.getWeaponCreationEvent().createUpgradedBattleAxe("barbed-bat");
        MerchantRecipe recipe1 = new MerchantRecipe(barbedBat, 10);
        recipe1.addIngredient(basicBat);
        recipe1.addIngredient(createDerpicBottleCap(plugin.getConfig().getInt("battle-axe.upgrades.barbed-bat.cost", 10)));
        recipe1.setExperienceReward(false);
        recipes.add(recipe1);

        ItemStack barbedNailBat = plugin.getWeaponCreationEvent().createUpgradedBattleAxe("barbed-nail-bat");
        MerchantRecipe recipe2 = new MerchantRecipe(barbedNailBat, 10);
        recipe2.addIngredient(barbedBat);
        recipe2.addIngredient(createDerpicBottleCap(plugin.getConfig().getInt("battle-axe.upgrades.barbed-nail-bat.cost", 20)));
        recipe2.setExperienceReward(false);
        recipes.add(recipe2);

        ItemStack shredder = plugin.getWeaponCreationEvent().createUpgradedBattleAxe("shredder");
        MerchantRecipe recipe3 = new MerchantRecipe(shredder, 10);
        recipe3.addIngredient(barbedNailBat);
        recipe3.addIngredient(createDerpicBottleCap(plugin.getConfig().getInt("battle-axe.upgrades.shredder.cost", 30)));
        recipe3.setExperienceReward(false);
        recipes.add(recipe3);

        ItemStack slasher = plugin.getWeaponCreationEvent().createUpgradedBattleAxe("slasher");
        MerchantRecipe recipe4 = new MerchantRecipe(slasher, 10);
        recipe4.addIngredient(shredder);
        recipe4.addIngredient(createDerpicBottleCap(plugin.getConfig().getInt("battle-axe.upgrades.slasher.cost", 40)));
        recipe4.setExperienceReward(false);
        recipes.add(recipe4);

        ItemStack apocalypseBlade = plugin.getWeaponCreationEvent().createUpgradedBattleAxe("apocalypse-blade");
        MerchantRecipe recipe5 = new MerchantRecipe(apocalypseBlade, 10);
        recipe5.addIngredient(slasher);
        recipe5.addIngredient(createDerpicBottleCap(plugin.getConfig().getInt("battle-axe.upgrades.apocalypse-blade.cost", 50)));
        recipe5.setExperienceReward(false);
        recipes.add(recipe5);

        trader.setRecipes(recipes);

        player.sendMessage(ChatColor.GREEN + "A trader has been spawned at your location!");
        return true;
    }

    private ItemStack createDerpicBottleCap(int amount) {
        ItemStack bottleCap = new ItemStack(Material.RED_DYE, amount);
        ItemMeta meta = bottleCap.getItemMeta();
        meta.setCustomModelData(2);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eDerpic Bottle Cap"));
        bottleCap.setItemMeta(meta);
        return bottleCap;
    }
}