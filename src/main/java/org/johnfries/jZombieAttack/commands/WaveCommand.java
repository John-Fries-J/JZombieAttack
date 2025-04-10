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
        EntityEquipment equipment = zombie.getEquipment();

        Material helmetMaterial, chestplateMaterial, leggingsMaterial, bootsMaterial;
        Material weaponMaterial = Material.IRON_SWORD;
        switch (tier) {
            case 1 -> {
                helmetMaterial = Material.LEATHER_HELMET;
                chestplateMaterial = Material.LEATHER_CHESTPLATE;
                leggingsMaterial = Material.LEATHER_LEGGINGS;
                bootsMaterial = Material.LEATHER_BOOTS;
            }
            case 2 -> {
                helmetMaterial = Material.CHAINMAIL_HELMET;
                chestplateMaterial = Material.CHAINMAIL_CHESTPLATE;
                leggingsMaterial = Material.CHAINMAIL_LEGGINGS;
                bootsMaterial = Material.CHAINMAIL_BOOTS;
            }
            case 3 -> {
                helmetMaterial = Material.IRON_HELMET;
                chestplateMaterial = Material.IRON_CHESTPLATE;
                leggingsMaterial = Material.IRON_LEGGINGS;
                bootsMaterial = Material.IRON_BOOTS;
            }
            case 4 -> {
                helmetMaterial = Material.GOLDEN_HELMET;
                chestplateMaterial = Material.GOLDEN_CHESTPLATE;
                leggingsMaterial = Material.GOLDEN_LEGGINGS;
                bootsMaterial = Material.GOLDEN_BOOTS;
            }
            case 5 -> {
                helmetMaterial = Material.DIAMOND_HELMET;
                chestplateMaterial = Material.DIAMOND_CHESTPLATE;
                leggingsMaterial = Material.DIAMOND_LEGGINGS;
                bootsMaterial = Material.DIAMOND_BOOTS;
            }
            default -> {
                helmetMaterial = Material.LEATHER_HELMET;
                chestplateMaterial = Material.LEATHER_CHESTPLATE;
                leggingsMaterial = Material.LEATHER_LEGGINGS;
                bootsMaterial = Material.LEATHER_BOOTS;
            }
        }

        int armorChance = random.nextInt(100);
        if (armorChance >= 30) {
            ItemStack helmet = random.nextBoolean() ? new ItemStack(helmetMaterial) : null;
            ItemStack chestplate = random.nextBoolean() ? new ItemStack(chestplateMaterial) : null;
            ItemStack leggings = random.nextBoolean() ? new ItemStack(leggingsMaterial) : null;
            ItemStack boots = random.nextBoolean() ? new ItemStack(bootsMaterial) : null;

            equipment.setHelmet(helmet);
            equipment.setChestplate(chestplate);
            equipment.setLeggings(leggings);
            equipment.setBoots(boots);

            int enchantChance = (tier == 5) ? 50 : 20;
            if (tier >= 4 && random.nextInt(100) < enchantChance) {
                if (helmet != null) helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(2) + 1);
                if (chestplate != null) chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, random.nextInt(2) + 1);
            }
        }

        int weaponChance = random.nextInt(100);
        if (weaponChance >= 40) {
            ItemStack weapon;
            if (weaponChance < 70) {
                weapon = new ItemStack(weaponMaterial);
                if (tier >= 4 && random.nextInt(100) < 20) {
                    weapon.addEnchantment(Enchantment.DAMAGE_ALL, random.nextInt(2) + 1);
                }
            } else {
                weapon = new ItemStack(Material.BOW);
                if (tier >= 4 && random.nextInt(100) < 20) {
                    weapon.addEnchantment(Enchantment.ARROW_DAMAGE, random.nextInt(2) + 1);
                }
            }
            equipment.setItemInMainHand(weapon);
        }

        double health, speed, damage;
        switch (tier) {
            case 1 -> {
                health = 5.0 + random.nextDouble() * 5.0;
                speed = 0.15 + random.nextDouble() * 0.05;
                damage = 1.0 + random.nextDouble() * 1.0;
            }
            case 2 -> {
                health = 15.0 + random.nextDouble() * 5.0;
                speed = 0.20 + random.nextDouble() * 0.05;
                damage = 2.0 + random.nextDouble() * 1.5;
            }
            case 3 -> {
                health = 20.0 + random.nextDouble() * 10.0;
                speed = 0.25 + random.nextDouble() * 0.05;
                damage = 3.0 + random.nextDouble() * 1.5;
            }
            case 4 -> {
                health = 30.0 + random.nextDouble() * 10.0;
                speed = 0.30 + random.nextDouble() * 0.05;
                damage = 4.0 + random.nextDouble() * 1.5;
            }
            case 5 -> {
                health = 40.0 + random.nextDouble() * 20.0;
                speed = 0.35 + random.nextDouble() * 0.10;
                damage = 5.0 + random.nextDouble() * 2.0;
            }
            default -> {
                health = 20.0; speed = 0.23; damage = 3.0;
            }
        }

        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        zombie.setHealth(health);
        zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
        zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);

        equipment.setHelmetDropChance(0.0f);
        equipment.setChestplateDropChance(0.0f);
        equipment.setLeggingsDropChance(0.0f);
        equipment.setBootsDropChance(0.0f);
        equipment.setItemInMainHandDropChance(0.0f);

        if (tier == 2 && random.nextInt(100) < 30) {
            zombie.setPassenger(zombie.getWorld().spawnEntity(zombie.getLocation(), EntityType.CHICKEN));
        }
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