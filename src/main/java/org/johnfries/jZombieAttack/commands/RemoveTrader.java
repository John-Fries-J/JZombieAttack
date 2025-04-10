package org.johnfries.jZombieAttack.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.johnfries.jZombieAttack.JZombieAttack;

public class RemoveTrader implements CommandExecutor {

    private final JZombieAttack plugin;

    public RemoveTrader(JZombieAttack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        Villager targetVillager = getAxeTraderAtPlayerLocation(player);

        if (targetVillager != null) {
            targetVillager.remove();
            player.sendMessage(ChatColor.GREEN + "Axe Trader removed!");
        } else {
            player.sendMessage(ChatColor.RED + "You must be standing on the same block as an Axe Trader to remove it!");
        }

        return true;
    }

    private Villager getAxeTraderAtPlayerLocation(Player player) {
        Location playerLoc = player.getLocation();
        int playerX = playerLoc.getBlockX();
        int playerY = playerLoc.getBlockY();
        int playerZ = playerLoc.getBlockZ();

        for (Entity entity : player.getWorld().getNearbyEntities(playerLoc, 1.5, 1.5, 1.5)) { //checking for 3x3 cuz same block collision lol
            if (!(entity instanceof Villager villager)) continue;

            if (villager.getCustomName() == null || !villager.getCustomName().equals(ChatColor.GREEN + "Axe Trader")) {
                continue;
            }

            Location villagerLoc = villager.getLocation();
            int villagerX = villagerLoc.getBlockX();
            int villagerY = villagerLoc.getBlockY();
            int villagerZ = villagerLoc.getBlockZ();

            if (Math.abs(playerX - villagerX) <= 1 &&
                Math.abs(playerY - villagerY) <= 1 &&
                Math.abs(playerZ - villagerZ) <= 1) {
                return villager;
            }
        }

        return null;
    }
}