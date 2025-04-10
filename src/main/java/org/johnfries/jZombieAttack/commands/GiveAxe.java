package org.johnfries.jZombieAttack.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.Arrays;
import java.util.List;

public class GiveAxe implements CommandExecutor {

    private final JZombieAttack plugin;
    private final List<String> axeNames = Arrays.asList(
            ChatColor.RED + "" + ChatColor.BOLD + "Battle Axe",
            ChatColor.RED + "" + ChatColor.BOLD + "Reaper's Edge",
            ChatColor.RED + "" + ChatColor.BOLD + "Doom Cleaver",
            ChatColor.RED + "" + ChatColor.BOLD + "Soul Shredder",
            ChatColor.RED + "" + ChatColor.BOLD + "Apocalypse Blade",
            ChatColor.RED + "" + ChatColor.BOLD + "Zombie Bane"
    );

    public GiveAxe(JZombieAttack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("battleaxe")) {
            int playersGiven = 0;
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (!hasAxe(player)) {
                    player.getInventory().addItem(plugin.createBattleAxe());
                    player.sendMessage(ChatColor.GREEN + "You received a Battle Axe!");
                    playersGiven++;
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You already have an axe!");
                }
            }
            sender.sendMessage(ChatColor.GREEN + "Battle Axes distributed to " + playersGiven + " players!");
            return true;
        }
        return false;
    }

    private boolean hasAxe(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasDisplayName()) {
                    String displayName = meta.getDisplayName();
                    if (axeNames.contains(displayName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}