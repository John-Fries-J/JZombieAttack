package org.johnfries.jZombieAttack.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.Arrays;

public class WeaponOffhandEvent implements Listener {

    private final JZombieAttack plugin;

    public WeaponOffhandEvent(JZombieAttack plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack offHandItem = event.getOffHandItem();

        String axeName = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("battle-axe.name", "&c&lBasic Bat"));
        String gunName = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("water-gun.name", "&bWater Gun"));
        int axeModelData = plugin.getConfig().getInt("battle-axe.custom-model-data", 0);
        int gunModelData = plugin.getConfig().getInt("water-gun.custom-model-data", 1001);

        if (offHandItem != null && offHandItem.hasItemMeta() && offHandItem.getItemMeta().hasDisplayName()) {
            ItemMeta meta = offHandItem.getItemMeta();
            String displayName = meta.getDisplayName();
            int customModelData = meta.hasCustomModelData() ? meta.getCustomModelData() : -1;

            if ((displayName.equals(axeName) && customModelData == axeModelData) ||
                    (displayName.equals(gunName) && customModelData == gunModelData)) {
                event.setCancelled(true);
                openArmorUpgradeGUI(player);
            }
        }
    }

    private void openArmorUpgradeGUI(Player player) {
        Inventory gui = plugin.getServer().createInventory(null, 27, ChatColor.DARK_GREEN + "Armor Upgrade");

        ItemStack helmet = player.getInventory().getHelmet() != null ? player.getInventory().getHelmet().clone() : new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = player.getInventory().getChestplate() != null ? player.getInventory().getChestplate().clone() : new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = player.getInventory().getLeggings() != null ? player.getInventory().getLeggings().clone() : new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = player.getInventory().getBoots() != null ? player.getInventory().getBoots().clone() : new ItemStack(Material.LEATHER_BOOTS);

        boolean helmetMaxed = helmet.getType() == Material.DIAMOND_HELMET;
        ItemMeta helmetMeta = helmet.getItemMeta() != null ? helmet.getItemMeta() : plugin.getServer().getItemFactory().getItemMeta(Material.LEATHER_HELMET);
        if (helmetMeta == null) {
            helmet = new ItemStack(Material.LEATHER_HELMET);
            helmetMeta = helmet.getItemMeta();
        }
        helmetMeta.setDisplayName(ChatColor.GREEN + "Upgrade Helmet");
        helmetMeta.setLore(Arrays.asList(
                helmetMaxed ? ChatColor.RED + "Max Upgraded!" : ChatColor.YELLOW + "Cost: 10 Derpic Bottle Caps"
        ));
        helmet.setItemMeta(helmetMeta);

        boolean chestplateMaxed = chestplate.getType() == Material.DIAMOND_CHESTPLATE;
        ItemMeta chestplateMeta = chestplate.getItemMeta() != null ? chestplate.getItemMeta() : plugin.getServer().getItemFactory().getItemMeta(Material.LEATHER_CHESTPLATE);
        if (chestplateMeta == null) {
            chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            chestplateMeta = chestplate.getItemMeta();
        }
        chestplateMeta.setDisplayName(ChatColor.GREEN + "Upgrade Chestplate");
        chestplateMeta.setLore(Arrays.asList(
                chestplateMaxed ? ChatColor.RED + "Max Upgraded!" : ChatColor.YELLOW + "Cost: 15 Derpic Bottle Caps"
        ));
        chestplate.setItemMeta(chestplateMeta);

        boolean leggingsMaxed = leggings.getType() == Material.DIAMOND_LEGGINGS;
        ItemMeta leggingsMeta = leggings.getItemMeta() != null ? leggings.getItemMeta() : plugin.getServer().getItemFactory().getItemMeta(Material.LEATHER_LEGGINGS);
        if (leggingsMeta == null) {
            leggings = new ItemStack(Material.LEATHER_LEGGINGS);
            leggingsMeta = leggings.getItemMeta();
        }
        leggingsMeta.setDisplayName(ChatColor.GREEN + "Upgrade Leggings");
        leggingsMeta.setLore(Arrays.asList(
                leggingsMaxed ? ChatColor.RED + "Max Upgraded!" : ChatColor.YELLOW + "Cost: 12 Derpic Bottle Caps"
        ));
        leggings.setItemMeta(leggingsMeta);

        boolean bootsMaxed = boots.getType() == Material.DIAMOND_BOOTS;
        ItemMeta bootsMeta = boots.getItemMeta() != null ? boots.getItemMeta() : plugin.getServer().getItemFactory().getItemMeta(Material.LEATHER_BOOTS);
        if (bootsMeta == null) {
            boots = new ItemStack(Material.LEATHER_BOOTS);
            bootsMeta = boots.getItemMeta();
        }
        bootsMeta.setDisplayName(ChatColor.GREEN + "Upgrade Boots");
        bootsMeta.setLore(Arrays.asList(
                bootsMaxed ? ChatColor.RED + "Max Upgraded!" : ChatColor.YELLOW + "Cost: 8 Derpic Bottle Caps"
        ));
        boots.setItemMeta(bootsMeta);

        ItemStack enchantOption = new ItemStack(Material.ENCHANTING_TABLE);
        ItemMeta enchantMeta = enchantOption.getItemMeta();
        enchantMeta.setDisplayName(ChatColor.AQUA + "Enchant Armor");
        enchantMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Purchase enchanted books"));
        enchantOption.setItemMeta(enchantMeta);

        gui.setItem(10, helmet);
        gui.setItem(12, chestplate);
        gui.setItem(14, leggings);
        gui.setItem(16, boots);
        gui.setItem(22, enchantOption);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (!title.equals(ChatColor.DARK_GREEN + "Armor Upgrade")) return;

        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String displayName = clickedItem.getItemMeta().getDisplayName();
        if (displayName.equals(ChatColor.AQUA + "Enchant Armor")) {
            openEnchantGUI(player);
            return;
        }

        int bottleCapCost = 0;
        if (displayName.equals(ChatColor.GREEN + "Upgrade Helmet")) {
            bottleCapCost = 10;
        } else if (displayName.equals(ChatColor.GREEN + "Upgrade Chestplate")) {
            bottleCapCost = 15;
        } else if (displayName.equals(ChatColor.GREEN + "Upgrade Leggings")) {
            bottleCapCost = 12;
        } else if (displayName.equals(ChatColor.GREEN + "Upgrade Boots")) {
            bottleCapCost = 8;
        }

        if (bottleCapCost > 0) {
            int slot = getArmorSlot(displayName);
            ItemStack currentArmor = getPlayerArmor(player, slot);
            if (currentArmor != null && isMaxedArmor(currentArmor)) {
                player.sendMessage(ChatColor.RED + "This armor is already fully upgraded!");
                return;
            }

            ItemStack bottleCap = createDerpicBottleCap();
            if (hasEnoughBottleCaps(player, bottleCap, bottleCapCost)) {
                removeBottleCaps(player, bottleCap, bottleCapCost);
                upgradeArmor(player, displayName);
                player.sendMessage(ChatColor.GREEN + "Armor upgraded successfully!");
                player.closeInventory();
                openArmorUpgradeGUI(player);
            } else {
                player.sendMessage(ChatColor.RED + "Not enough Derpic Bottle Caps!");
            }
        }
    }

    private void openEnchantGUI(Player player) {
        Inventory gui = plugin.getServer().createInventory(null, 27, ChatColor.DARK_PURPLE + "Enchant Armor");

        ItemStack protectionI = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta p1Meta = protectionI.getItemMeta();
        p1Meta.setDisplayName(ChatColor.GREEN + "Protection I");
        p1Meta.setLore(Arrays.asList(ChatColor.YELLOW + "Cost: 10 XP Levels"));
        protectionI.setItemMeta(p1Meta);

        ItemStack protectionII = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta p2Meta = protectionII.getItemMeta();
        p2Meta.setDisplayName(ChatColor.GREEN + "Protection II");
        p2Meta.setLore(Arrays.asList(ChatColor.YELLOW + "Cost: 20 XP Levels"));
        protectionII.setItemMeta(p2Meta);

        ItemStack fireProtection = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta fpMeta = fireProtection.getItemMeta();
        fpMeta.setDisplayName(ChatColor.GREEN + "Fire Protection I");
        fpMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Cost: 15 XP Levels"));
        fireProtection.setItemMeta(fpMeta);

        ItemStack blastProtection = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta bpMeta = blastProtection.getItemMeta();
        bpMeta.setDisplayName(ChatColor.GREEN + "Blast Protection I");
        bpMeta.setLore(Arrays.asList(ChatColor.YELLOW + "Cost: 15 XP Levels"));
        blastProtection.setItemMeta(bpMeta);

        gui.setItem(10, protectionI);
        gui.setItem(11, protectionII);
        gui.setItem(12, fireProtection);
        gui.setItem(14, blastProtection);

        player.openInventory(gui);
    }

    private ItemStack createDerpicBottleCap() {
        ItemStack bottleCap = new ItemStack(Material.RED_DYE, 1);
        ItemMeta meta = bottleCap.getItemMeta();
        meta.setCustomModelData(2);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eDerpic Bottle Cap"));
        bottleCap.setItemMeta(meta);
        return bottleCap;
    }

    private boolean hasEnoughBottleCaps(Player player, ItemStack bottleCap, int required) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(bottleCap)) {
                count += item.getAmount();
            }
        }
        return count >= required;
    }

    private void removeBottleCaps(Player player, ItemStack bottleCap, int required) {
        int remaining = required;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.isSimilar(bottleCap)) {
                if (item.getAmount() > remaining) {
                    item.setAmount(item.getAmount() - remaining);
                    break;
                } else {
                    remaining -= item.getAmount();
                    player.getInventory().setItem(i, null);
                }
            }
        }
    }

    private void upgradeArmor(Player player, String upgradeType) {
        Material[] helmetPath = {Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET};
        Material[] chestplatePath = {Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE};
        Material[] leggingsPath = {Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS};
        Material[] bootsPath = {Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS};

        int slot = -1;
        Material[] path = null;
        if (upgradeType.equals(ChatColor.GREEN + "Upgrade Helmet")) {
            slot = 39;
            path = helmetPath;
        } else if (upgradeType.equals(ChatColor.GREEN + "Upgrade Chestplate")) {
            slot = 38;
            path = chestplatePath;
        } else if (upgradeType.equals(ChatColor.GREEN + "Upgrade Leggings")) {
            slot = 37;
            path = leggingsPath;
        } else if (upgradeType.equals(ChatColor.GREEN + "Upgrade Boots")) {
            slot = 36;
            path = bootsPath;
        }

        if (slot == -1 || path == null) return;

        ItemStack current = player.getInventory().getItem(slot);
        Material currentMaterial = current != null ? current.getType() : null;
        Material nextMaterial = null;
        for (int i = 0; i < path.length - 1; i++) {
            if (path[i] == currentMaterial) {
                nextMaterial = path[i + 1];
                break;
            }
        }

        if (nextMaterial == null) {
            nextMaterial = path[0];
        }

        if (nextMaterial != null) {
            player.getInventory().setItem(slot, new ItemStack(nextMaterial));
        }
    }

    private int getArmorSlot(String displayName) {
        if (displayName.equals(ChatColor.GREEN + "Upgrade Helmet")) return 39;
        if (displayName.equals(ChatColor.GREEN + "Upgrade Chestplate")) return 38;
        if (displayName.equals(ChatColor.GREEN + "Upgrade Leggings")) return 37;
        if (displayName.equals(ChatColor.GREEN + "Upgrade Boots")) return 36;
        return -1;
    }

    private ItemStack getPlayerArmor(Player player, int slot) {
        return player.getInventory().getItem(slot);
    }

    private boolean isMaxedArmor(ItemStack armor) {
        if (armor == null) return false;
        return armor.getType() == Material.DIAMOND_HELMET ||
                armor.getType() == Material.DIAMOND_CHESTPLATE ||
                armor.getType() == Material.DIAMOND_LEGGINGS ||
                armor.getType() == Material.DIAMOND_BOOTS;
    }
}