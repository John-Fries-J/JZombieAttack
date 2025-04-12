package org.johnfries.jZombieAttack.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NoscyCommand implements CommandExecutor, TabCompleter {

    private final JZombieAttack plugin;

    public NoscyCommand(JZombieAttack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /noscy <player>");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        ItemStack waterGun = plugin.getWeaponCreationEvent().createWaterGun();
        target.getInventory().addItem(waterGun);
        target.sendMessage(ChatColor.GREEN + "You received a Water Gun!");
        sender.sendMessage(ChatColor.GREEN + "Gave a Water Gun to " + target.getName() + "!");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}