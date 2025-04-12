package org.johnfries.jZombieAttack.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class VillagerEvent implements Listener {

    @EventHandler
    public void onVillagerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Villager villager
                && villager.getCustomName() != null
                && villager.getCustomName().equals(ChatColor.GREEN + "Axe Trader")) {
            event.setCancelled(true);
        }
    }
}