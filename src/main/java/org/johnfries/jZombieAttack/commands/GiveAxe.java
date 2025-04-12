package org.johnfries.jZombieAttack.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.ArrayList;
import java.util.List;

public class GiveAxe implements CommandExecutor {

    private final JZombieAttack plugin;

    public GiveAxe(JZombieAttack plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("battleaxe")) {
            int playersGiven = 0;
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (!hasAxe(player)) {
                    player.getInventory().addItem(plugin.getWeaponCreationEvent().createBattleAxe());
                    player.sendMessage(ChatColor.GREEN + "You received a Basic Bat!");
                    playersGiven++;
                } else {
                    player.sendMessage(ChatColor.YELLOW + "You already have an axe!");
                }
            }
            sender.sendMessage(ChatColor.GREEN + "Basic Bats distributed to " + playersGiven + " players!");
            return true;
        }
        return false;
    }

    private boolean hasAxe(Player player) {
        List<String> axeNames = new ArrayList<>();
        List<Integer> axeModelData = new ArrayList<>();

        axeNames.add(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("battle-axe.name", "&c&lBasic Bat")));
        axeModelData.add(plugin.getConfig().getInt("battle-axe.custom-model-data", 0));

        String[] upgrades = {"barbed-bat", "barbed-nail-bat", "shredder", "slasher", "apocalypse-blade"};
        for (String upgrade : upgrades) {
            String name = plugin.getConfig().getString("battle-axe.upgrades." + upgrade + ".name");
            int modelData = plugin.getConfig().getInt("battle-axe.upgrades." + upgrade + ".custom-model-data", -1);
            if (name != null) {
                axeNames.add(ChatColor.translateAlternateColorCodes('&', name));
                axeModelData.add(modelData);
            }
        }

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasDisplayName()) {
                    String displayName = meta.getDisplayName();
                    int customModelData = meta.hasCustomModelData() ? meta.getCustomModelData() : -1;
                    for (int i = 0; i < axeNames.size(); i++) {
                        if (displayName.equals(axeNames.get(i)) &&
                                (customModelData == -1 || customModelData == axeModelData.get(i))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}