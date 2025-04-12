package org.johnfries.jZombieAttack.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.Collections;
import java.util.List;

public class ZombiesCommand implements CommandExecutor, TabCompleter {

    private final JZombieAttack plugin;

    public ZombiesCommand(JZombieAttack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.RED + "Usage: /zombies reload");
            return true;
        }

        if (!sender.hasPermission("jzombieattack.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload the config!");
            return true;
        }

        try {
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "Config reloaded successfully!");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to reload config: " + e.getMessage());
            plugin.getLogger().warning("Error reloading config: " + e.getMessage());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }
}