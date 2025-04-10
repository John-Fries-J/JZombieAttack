package org.johnfries.jZombieAttack.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.ArrayList;
import java.util.Arrays;
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

        ItemStack battleAxe = plugin.createBattleAxe();
        ItemStack reapersEdge = createUpgradedBattleAxe(1, "&c&lReaper's Edge");
        MerchantRecipe recipe1 = new MerchantRecipe(reapersEdge, 10);
        recipe1.addIngredient(battleAxe);
        recipe1.addIngredient(new ItemStack(Material.STONE, 10));
        recipe1.setExperienceReward(false);
        recipes.add(recipe1);

        ItemStack doomCleaver = createUpgradedBattleAxe(2, "&c&lDoom Cleaver");
        MerchantRecipe recipe2 = new MerchantRecipe(doomCleaver, 10);
        recipe2.addIngredient(reapersEdge);
        recipe2.addIngredient(new ItemStack(Material.STONE, 20));
        recipe2.setExperienceReward(false);
        recipes.add(recipe2);

        ItemStack soulShredder = createUpgradedBattleAxe(3, "&c&lSoul Shredder");
        MerchantRecipe recipe3 = new MerchantRecipe(soulShredder, 10);
        recipe3.addIngredient(doomCleaver);
        recipe3.addIngredient(new ItemStack(Material.STONE, 30));
        recipe3.setExperienceReward(false);
        recipes.add(recipe3);

        ItemStack apocalypseBlade = createUpgradedBattleAxe(4, "&c&lApocalypse Blade");
        MerchantRecipe recipe4 = new MerchantRecipe(apocalypseBlade, 10);
        recipe4.addIngredient(soulShredder);
        recipe4.addIngredient(new ItemStack(Material.STONE, 40));
        recipe4.setExperienceReward(false);
        recipes.add(recipe4);

        ItemStack zombieBane = createUpgradedBattleAxe(5, "&c&lZombie Bane");
        MerchantRecipe recipe5 = new MerchantRecipe(zombieBane, 10);
        recipe5.addIngredient(apocalypseBlade);
        recipe5.addIngredient(new ItemStack(Material.STONE, 50));
        recipe5.setExperienceReward(false);
        recipes.add(recipe5);

        trader.setRecipes(recipes);

        player.sendMessage(ChatColor.GREEN + "A trader has been spawned at your location!");
        return true;
    }

    private ItemStack createUpgradedBattleAxe(int sharpnessLevel, String name) {
        ItemStack upgradedBattleAxe = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = upgradedBattleAxe.getItemMeta();

        meta.setCustomModelData(12345);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&7&oA sharper battle axe forged by the"),
                ChatColor.translateAlternateColorCodes('&', "&7&ozombies of the apocalypse."),
                ChatColor.translateAlternateColorCodes('&', "&7Sharpness " + romanNumeral(sharpnessLevel))
        ));
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.DAMAGE_ALL, sharpnessLevel, true);

        upgradedBattleAxe.setItemMeta(meta);
        return upgradedBattleAxe;
    }

    private String romanNumeral(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(number);
        };
    }
}