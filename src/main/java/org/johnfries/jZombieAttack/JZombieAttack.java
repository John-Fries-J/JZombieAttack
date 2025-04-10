package org.johnfries.jZombieAttack;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.johnfries.jZombieAttack.commands.GiveAxe;
import org.johnfries.jZombieAttack.commands.SpawnTrader;
import org.johnfries.jZombieAttack.commands.RemoveTrader;
import org.johnfries.jZombieAttack.commands.WaveCommand;

import java.util.Arrays;

public final class JZombieAttack extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("JZombieAttack has started up hopefully with no issues");
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("battleaxe").setExecutor(new GiveAxe(this));
        getCommand("spawntrader").setExecutor(new SpawnTrader(this));
        getCommand("removetrader").setExecutor(new RemoveTrader(this));
        getCommand("wave").setExecutor(new WaveCommand(this));
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("JZombieAttack has shutdown hopefully with no issues");
    }

    public ItemStack createBattleAxe() {
        ItemStack battleAxe = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = battleAxe.getItemMeta();

        meta.setCustomModelData(12345);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lBattle Axe"));
        meta.setLore(Arrays.asList(
                ChatColor.translateAlternateColorCodes('&', "&7&oA battle axe that is used by the"),
                ChatColor.translateAlternateColorCodes('&', "&7&ozombies of the apocalypse."),
                ChatColor.translateAlternateColorCodes('&', "")
        ));
        meta.setUnbreakable(true);

        battleAxe.setItemMeta(meta);
        return battleAxe;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item != null && item.getType() == Material.NETHERITE_SWORD
                    && item.hasItemMeta() && item.getItemMeta().hasCustomModelData()
                    && item.getItemMeta().getCustomModelData() == 12345) {

                player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), 30, 0.5, 0.5, 0.5, 0.1);
                player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 1.0f, 1.0f);
            }
        }
    }

    @EventHandler
    public void onVillagerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Villager villager
                && villager.getCustomName() != null
                && villager.getCustomName().equals(ChatColor.GREEN + "Axe Trader")) {
            event.setCancelled(true);
        }
    }
}