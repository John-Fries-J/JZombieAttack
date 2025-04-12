package org.johnfries.jZombieAttack.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaterGunEvent implements Listener {

    private final JZombieAttack plugin;
    private final Map<UUID, Long> waterGunCooldowns = new HashMap<>();

    public WaterGunEvent(JZombieAttack plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_AIR
                || event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();
            String configName = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("water-gun.name", "&bWater Gun"));
            int configModelData = plugin.getConfig().getInt("water-gun.custom-model-data", 1001);

            if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                    && item.getItemMeta().getDisplayName().equals(configName)
                    && item.getItemMeta().hasCustomModelData()
                    && item.getItemMeta().getCustomModelData() == configModelData) {

                long cooldownTicks = plugin.getConfig().getLong("water-gun.cooldown", 20);
                long currentTime = System.currentTimeMillis();
                UUID playerId = player.getUniqueId();

                if (waterGunCooldowns.containsKey(playerId)) {
                    long lastUse = waterGunCooldowns.get(playerId);
                    long ticksPassed = (currentTime - lastUse) / 50;
                    if (ticksPassed < cooldownTicks) {
                        player.sendMessage(ChatColor.RED + "Water Gun on cooldown for " +
                                String.format("%.1f", (cooldownTicks - ticksPassed) / 20.0) + " seconds!");
                        return;
                    }
                }

                waterGunCooldowns.put(playerId, currentTime);

                String particleName = plugin.getConfig().getString("water-gun.particle", "DRIP_WATER");
                Particle particle;
                try {
                    particle = Particle.valueOf(particleName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    particle = Particle.DRIP_WATER;
                    plugin.getLogger().warning("Invalid particle in config: " + particleName + ". Using DRIP_WATER.");
                }

                double playerDamage = plugin.getConfig().getDouble("water-gun.player-damage", 2.0);
                double entityDamage = plugin.getConfig().getDouble("water-gun.entity-damage", 4.0);
                int particleDuration = plugin.getConfig().getInt("water-gun.particle-duration", 40);

                RayTraceResult result = player.getWorld().rayTraceEntities(
                        player.getEyeLocation(),
                        player.getEyeLocation().getDirection(),
                        20.0,
                        entity -> entity != player
                );

                if (result != null && result.getHitEntity() != null) {
                    Entity target = result.getHitEntity();
                    double damage = target instanceof Player ? playerDamage : entityDamage;
                    target.setLastDamageCause(new EntityDamageByEntityEvent(player, target, EntityDamageEvent.DamageCause.CUSTOM, damage));
                    ((org.bukkit.entity.LivingEntity) target).damage(damage, player);
                }

                Vector direction = player.getEyeLocation().getDirection().normalize();
                Location loc = player.getEyeLocation().clone();
                int steps = particleDuration / 2;
                for (int i = 0; i < steps && i < 40; i++) {
                    loc.add(direction.clone().multiply(0.5));
                    player.getWorld().spawnParticle(particle, loc, 5, 0.1, 0.1, 0.1, 0.0);
                }
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 1.0f, 1.0f);
            }
        }
    }
}