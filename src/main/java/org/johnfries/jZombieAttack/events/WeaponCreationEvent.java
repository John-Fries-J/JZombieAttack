package org.johnfries.jZombieAttack.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WeaponCreationEvent {

    private final JZombieAttack plugin;

    public WeaponCreationEvent(JZombieAttack plugin) {
        this.plugin = plugin;
    }

    public ItemStack createBattleAxe() {
        return createWeapon("battle-axe");
    }

    public ItemStack createWaterGun() {
        return createWeapon("water-gun");
    }

    public ItemStack createUpgradedBattleAxe(String upgradeName) {
        return createWeapon("battle-axe.upgrades." + upgradeName);
    }

    private ItemStack createWeapon(String configPath) {
        String name = plugin.getConfig().getString(configPath + ".name", "&c&lBasic Bat");
        int customModelData = plugin.getConfig().getInt(configPath + ".custom-model-data", 0);
        String materialName = plugin.getConfig().getString(configPath + ".material", "NETHERITE_SWORD");
        double attackSpeed = plugin.getConfig().getDouble(configPath + ".attack-speed", 4.0);
        boolean unbreakable = plugin.getConfig().getBoolean(configPath + ".unbreakable", false);
        List<String> lore = plugin.getConfig().getStringList(configPath + ".lore");
        List<String> enchantStrings = plugin.getConfig().getStringList(configPath + ".enchants");

        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = configPath.contains("water-gun") ? Material.NETHERITE_HOE : Material.NETHERITE_SWORD;
            plugin.getLogger().warning("Invalid material for " + configPath + ": " + materialName + ". Using default.");
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setCustomModelData(customModelData);
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(coloredLore);
        meta.setUnbreakable(unbreakable);

        for (String enchantString : enchantStrings) {
            String[] parts = enchantString.split(":");
            if (parts.length == 2) {
                try {
                    Enchantment enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(parts[0].toLowerCase()));
                    int level = Integer.parseInt(parts[1]);
                    if (enchantment != null && level >= 0) {
                        meta.addEnchant(enchantment, level, true);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Invalid enchant for " + configPath + ": " + enchantString);
                }
            }
        }

        double modifier = attackSpeed - 4.0;
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
                new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", modifier,
                        AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));

        item.setItemMeta(meta);
        return item;
    }
}