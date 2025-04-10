package org.johnfries.jZombieAttack.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

        ItemStack basicBat = plugin.createBattleAxe();
        ItemStack barbedBat = createUpgradedBattleAxe(1, "&c&lBarbed Bat", 2, 4.2);
        MerchantRecipe recipe1 = new MerchantRecipe(barbedBat, 10);
        recipe1.addIngredient(basicBat);
        recipe1.addIngredient(createRedDye(10, 2));
        recipe1.setExperienceReward(false);
        recipes.add(recipe1);

        ItemStack barbedNailBat = createUpgradedBattleAxe(2, "&c&lBarbed and Nail Bat", 3, 4.4);
        MerchantRecipe recipe2 = new MerchantRecipe(barbedNailBat, 10);
        recipe2.addIngredient(barbedBat);
        recipe2.addIngredient(createRedDye(20, 2));
        recipe2.setExperienceReward(false);
        recipes.add(recipe2);

        ItemStack shredder = createUpgradedBattleAxe(3, "&c&lShredder", 5, 4.6);
        MerchantRecipe recipe3 = new MerchantRecipe(shredder, 10);
        recipe3.addIngredient(barbedNailBat);
        recipe3.addIngredient(createRedDye(30, 2));
        recipe3.setExperienceReward(false);
        recipes.add(recipe3);

        ItemStack slasher = createUpgradedBattleAxe(4, "&c&lSlasher", 6, 4.8);
        MerchantRecipe recipe4 = new MerchantRecipe(slasher, 10);
        recipe4.addIngredient(shredder);
        recipe4.addIngredient(createRedDye(40, 2));
        recipe4.setExperienceReward(false);
        recipes.add(recipe4);

        ItemStack apocalypseBlade = createUpgradedBattleAxe(10, "&c&lApocalypse Blade", 4, 4.0);
        MerchantRecipe recipe5 = new MerchantRecipe(apocalypseBlade, 10);
        recipe5.addIngredient(slasher);
        recipe5.addIngredient(createRedDye(50, 2));
        recipe5.setExperienceReward(false);
        recipes.add(recipe5);

        trader.setRecipes(recipes);

        player.sendMessage(ChatColor.GREEN + "A trader has been spawned at your location!");
        return true;
    }

    private ItemStack createUpgradedBattleAxe(int sharpnessLevel, String name, int customModelData, double attackSpeed) {
        ItemStack upgradedBattleAxe = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = upgradedBattleAxe.getItemMeta();

        meta.setCustomModelData(customModelData);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&7&oA sharper battle axe forged by the"),
                ChatColor.translateAlternateColorCodes('&', "&7&ozombies of the apocalypse.")
        ));
        meta.setUnbreakable(true);
        if (sharpnessLevel > 0) {
            meta.addEnchant(Enchantment.DAMAGE_ALL, sharpnessLevel, true);
        }
        double modifier = attackSpeed - 4.0;
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
                new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", modifier, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));

        upgradedBattleAxe.setItemMeta(meta);
        return upgradedBattleAxe;
    }

    private ItemStack createRedDye(int amount, int customModelData) {
        ItemStack redDye = new ItemStack(Material.RED_DYE, amount);
        ItemMeta meta = redDye.getItemMeta();
        meta.setCustomModelData(customModelData);
        redDye.setItemMeta(meta);
        return redDye;
    }
}