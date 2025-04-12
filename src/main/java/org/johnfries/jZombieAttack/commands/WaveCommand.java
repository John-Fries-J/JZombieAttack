package org.johnfries.jZombieAttack.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WaveCommand implements CommandExecutor, TabCompleter {

    private final JZombieAttack plugin;
    private final Random random = new Random();

    public WaveCommand(JZombieAttack plugin) {
        this.plugin = plugin;
        startBowShootingTask();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /wave [amount per player] [tier]");
            return true;
        }

        int amountPerPlayer;
        int tier;

        try {
            amountPerPlayer = Integer.parseInt(args[0]);
            tier = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Amount and tier must be numbers!");
            return true;
        }

        if (amountPerPlayer <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be greater than 0!");
            return true;
        }

        if (tier < 1 || tier > 5) {
            sender.sendMessage(ChatColor.RED + "Tier must be 1, 2, 3, 4, or 5!");
            return true;
        }

        Player player = (Player) sender;
        int totalZombies = amountPerPlayer * plugin.getServer().getOnlinePlayers().size();
        spawnWave(player.getLocation(), totalZombies, tier);

        player.sendMessage(ChatColor.GREEN + "Spawned a wave of " + totalZombies + " zombies (Tier " + tier + ")!");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("1", "3", "5", "10"));
        } else if (args.length == 2) {
            completions.addAll(Arrays.asList("1", "2", "3", "4", "5"));
        }

        String currentArg = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.startsWith(currentArg));

        return completions;
    }

    private void spawnWave(Location center, int totalZombies, int tier) {
        for (int i = 0; i < totalZombies; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 20;
            double offsetZ = (random.nextDouble() - 0.5) * 20;
            Location spawnLoc = center.clone().add(offsetX, 0, offsetZ);
            spawnLoc.setY(center.getWorld().getHighestBlockYAt(spawnLoc) + 1);

            Zombie zombie = (Zombie) center.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);
            configureZombie(zombie, tier);
        }
    }

    private void configureZombie(Zombie zombie, int tier) {
        String tierPath = "waves.tier" + tier;
        EntityEquipment equipment = zombie.getEquipment();

        double health = plugin.getConfig().getDouble(tierPath + ".health", 20.0);
        double healthVariation = plugin.getConfig().getDouble(tierPath + ".health-variation", 0.0);
        double speed = plugin.getConfig().getDouble(tierPath + ".speed", 0.23);
        double speedVariation = plugin.getConfig().getDouble(tierPath + ".speed-variation", 0.0);
        double damage = plugin.getConfig().getDouble(tierPath + ".damage", 3.0);
        double damageVariation = plugin.getConfig().getDouble(tierPath + ".damage-variation", 0.0);
        double armorChance = plugin.getConfig().getDouble(tierPath + ".armor-chance", 0.7);
        double weaponChance = plugin.getConfig().getDouble(tierPath + ".weapon-chance", 0.6);
        float bottleCapDropChance = (float) plugin.getConfig().getDouble(tierPath + ".bottle-cap-drop-chance", 0.0);

        String helmetName = plugin.getConfig().getString(tierPath + ".helmet", "LEATHER_HELMET");
        String chestplateName = plugin.getConfig().getString(tierPath + ".chestplate", "LEATHER_CHESTPLATE");
        String leggingsName = plugin.getConfig().getString(tierPath + ".leggings", "LEATHER_LEGGINGS");
        String bootsName = plugin.getConfig().getString(tierPath + ".boots", "LEATHER_BOOTS");

        Material helmetMaterial, chestplateMaterial, leggingsMaterial, bootsMaterial;
        try {
            helmetMaterial = Material.valueOf(helmetName);
            chestplateMaterial = Material.valueOf(chestplateName);
            leggingsMaterial = Material.valueOf(leggingsName);
            bootsMaterial = Material.valueOf(bootsName);
        } catch (IllegalArgumentException e) {
            helmetMaterial = Material.LEATHER_HELMET;
            chestplateMaterial = Material.LEATHER_CHESTPLATE;
            leggingsMaterial = Material.LEATHER_LEGGINGS;
            bootsMaterial = Material.LEATHER_BOOTS;
            plugin.getLogger().warning("Invalid armor material for tier " + tier + ": " + e.getMessage());
        }

        if (random.nextDouble() < armorChance) {
            ItemStack helmet = random.nextBoolean() ? new ItemStack(helmetMaterial) : null;
            ItemStack chestplate = random.nextBoolean() ? new ItemStack(chestplateMaterial) : null;
            ItemStack leggings = random.nextBoolean() ? new ItemStack(leggingsMaterial) : null;
            ItemStack boots = random.nextBoolean() ? new ItemStack(bootsMaterial) : null;

            equipment.setHelmet(helmet);
            equipment.setChestplate(chestplate);
            equipment.setLeggings(leggings);
            equipment.setBoots(boots);

            double enchantChance = plugin.getConfig().getDouble(tierPath + ".enchant-chance", tier == 5 ? 0.5 : tier == 4 ? 0.2 : 0.0);
            if (random.nextDouble() < enchantChance) {
                if (helmet != null) helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(2) + 1);
                if (chestplate != null) chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(2) + 1);
            }
        }

        if (random.nextDouble() < weaponChance) {
            ItemStack weapon;
            if (random.nextDouble() < 0.7) {
                weapon = new ItemStack(Material.IRON_SWORD);
                double enchantChance = plugin.getConfig().getDouble(tierPath + ".enchant-chance", tier == 4 || tier == 5 ? 0.2 : 0.0);
                if (random.nextDouble() < enchantChance) {
                    weapon.addEnchantment(Enchantment.DAMAGE_ALL, random.nextInt(2) + 1);
                }
            } else {
                weapon = new ItemStack(Material.BOW);
                double enchantChance = plugin.getConfig().getDouble(tierPath + ".enchant-chance", tier == 4 || tier == 5 ? 0.2 : 0.0);
                if (random.nextDouble() < enchantChance) {
                    weapon.addEnchantment(Enchantment.ARROW_DAMAGE, random.nextInt(2) + 1);
                }
            }
            equipment.setItemInMainHand(weapon);
        }

        double finalHealth = health + random.nextDouble() * healthVariation;
        double finalSpeed = speed + random.nextDouble() * speedVariation;
        double finalDamage = damage + random.nextDouble() * damageVariation;

        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(finalHealth);
        zombie.setHealth(finalHealth);
        zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(finalSpeed);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(finalDamage);

        equipment.setHelmetDropChance(0.0f);
        equipment.setChestplateDropChance(0.0f);
        equipment.setLeggingsDropChance(0.0f);
        equipment.setBootsDropChance(0.0f);
        equipment.setItemInMainHandDropChance(0.0f);

        ItemStack derpicBottleCap = createDerpicBottleCap();
        equipment.setItemInOffHand(derpicBottleCap);
        equipment.setItemInOffHandDropChance(bottleCapDropChance);

        double chickenChance = plugin.getConfig().getDouble(tierPath + ".chicken-chance", tier == 2 ? 0.3 : 0.0);
        if (random.nextDouble() < chickenChance) {
            zombie.setPassenger(zombie.getWorld().spawnEntity(zombie.getLocation(), EntityType.CHICKEN));
        }
    }

    private ItemStack createDerpicBottleCap() {
        ItemStack bottleCap = new ItemStack(Material.RED_DYE, 1);
        ItemMeta meta = bottleCap.getItemMeta();
        meta.setCustomModelData(2);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eDerpic Bottle Cap"));
        bottleCap.setItemMeta(meta);
        return bottleCap;
    }

    private void startBowShootingTask() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 20, 20, 20)) {
                    if (entity instanceof Zombie zombie) {
                        ItemStack mainHand = zombie.getEquipment().getItemInMainHand();
                        if (mainHand != null && mainHand.getType() == Material.BOW) {
                            Player target = findNearestPlayer(zombie);
                            if (target != null && zombie.getLocation().distance(target.getLocation()) <= 15) {
                                shootArrow(zombie, target);
                            }
                        }
                    }
                }
            }
        }, 0L, 40L);
    }

    private Player findNearestPlayer(Zombie zombie) {
        Player nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (Player player : zombie.getWorld().getPlayers()) {
            double distance = zombie.getLocation().distance(player.getLocation());
            if (distance < minDistance && distance <= 15) {
                minDistance = distance;
                nearest = player;
            }
        }
        return nearest;
    }

    private void shootArrow(Zombie zombie, Player target) {
        Location zombieLoc = zombie.getLocation().add(0, 1.5, 0);
        Location targetLoc = target.getLocation().add(0, 1.5, 0);
        Vector direction = targetLoc.subtract(zombieLoc).toVector().normalize();

        Arrow arrow = zombie.getWorld().spawnArrow(zombieLoc, direction, 1.0f, 12.0f);
        arrow.setShooter(zombie);
        arrow.setDamage(2.0);
        zombie.getWorld().playSound(zombieLoc, org.bukkit.Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
    }
}