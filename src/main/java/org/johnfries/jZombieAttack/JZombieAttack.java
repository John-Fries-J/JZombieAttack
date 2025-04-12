package org.johnfries.jZombieAttack;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.johnfries.jZombieAttack.commands.GiveAxe;
import org.johnfries.jZombieAttack.commands.NoscyCommand;
import org.johnfries.jZombieAttack.commands.SpawnTrader;
import org.johnfries.jZombieAttack.commands.RemoveTrader;
import org.johnfries.jZombieAttack.commands.WaveCommand;
import org.johnfries.jZombieAttack.commands.ZombiesCommand;
import org.johnfries.jZombieAttack.events.WaterGunEvent;
import org.johnfries.jZombieAttack.events.VillagerEvent;
import org.johnfries.jZombieAttack.events.WeaponCreationEvent;
import org.johnfries.jZombieAttack.events.WeaponOffhandEvent;
import org.johnfries.jZombieAttack.events.EnchantBookEvent;

public final class JZombieAttack extends JavaPlugin implements Listener {

    private WeaponCreationEvent weaponCreationEvent;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("JZombieAttack has started up hopefully with no issues");
        saveDefaultConfig();
        weaponCreationEvent = new WeaponCreationEvent(this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new WaterGunEvent(this), this);
        getServer().getPluginManager().registerEvents(new VillagerEvent(), this);
        getServer().getPluginManager().registerEvents(new WeaponOffhandEvent(this), this);
        getServer().getPluginManager().registerEvents(new EnchantBookEvent(this), this);
        getCommand("battleaxe").setExecutor(new GiveAxe(this));
        getCommand("spawntrader").setExecutor(new SpawnTrader(this));
        getCommand("removetrader").setExecutor(new RemoveTrader(this));
        WaveCommand waveCommand = new WaveCommand(this);
        getCommand("wave").setExecutor(waveCommand);
        getCommand("wave").setTabCompleter(waveCommand);
        NoscyCommand noscyCommand = new NoscyCommand(this);
        getCommand("noscy").setExecutor(noscyCommand);
        getCommand("noscy").setTabCompleter(noscyCommand);
        ZombiesCommand zombiesCommand = new ZombiesCommand(this);
        getCommand("zombies").setExecutor(zombiesCommand);
        getCommand("zombies").setTabCompleter(zombiesCommand);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("JZombieAttack has shutdown hopefully with no issues");
    }

    public WeaponCreationEvent getWeaponCreationEvent() {
        return weaponCreationEvent;
    }
}