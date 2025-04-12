package org.johnfries.jZombieAttack.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.johnfries.jZombieAttack.JZombieAttack;

import java.util.Arrays;

public class EnchantBookEvent implements Listener {

    private final JZombieAttack plugin;

    public EnchantBookEvent(JZombieAttack plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();

        if (title.equals(ChatColor.DARK_PURPLE + "Enchant Armor")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta()) return;

            String displayName = clickedItem.getItemMeta().getDisplayName();
            int xpCost = 0;
            Enchantment enchantment = null;
            int level = 0;

            if (displayName.equals(ChatColor.GREEN + "Protection I")) {
                xpCost = 10;
                enchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
                level = 1;
            } else if (displayName.equals(ChatColor.GREEN + "Protection II")) {
                xpCost = 20;
                enchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
                level = 2;
            } else if (displayName.equals(ChatColor.GREEN + "Fire Protection I")) {
                xpCost = 15;
                enchantment = Enchantment.PROTECTION_FIRE;
                level = 1;
            } else if (displayName.equals(ChatColor.GREEN + "Blast Protection I")) {
                xpCost = 15;
                enchantment = Enchantment.PROTECTION_EXPLOSIONS;
                level = 1;
            }

            if (xpCost > 0 && enchantment != null && level > 0) {
                if (player.getLevel() >= xpCost) {
                    player.setLevel(player.getLevel() - xpCost);
                    ItemStack book = createEnchantedBook(enchantment, level, displayName);
                    player.getInventory().addItem(book);
                    player.sendMessage(ChatColor.GREEN + "Purchased " + displayName + "!");
                } else {
                    player.sendMessage(ChatColor.RED + "Not enough XP levels!");
                }
            }
            return;
        }

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getInventory()) &&
                event.getClick() == ClickType.LEFT) {
            int slot = event.getSlot();
            ItemStack cursor = event.getCursor();
            ItemStack current = event.getCurrentItem();

            if (slot >= 36 && slot <= 39 && isArmor(current) && cursor != null &&
                    cursor.getType() == Material.ENCHANTED_BOOK && cursor.hasItemMeta()) {
                event.setCancelled(true);
                EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) cursor.getItemMeta();
                if (bookMeta.hasStoredEnchants()) {
                    for (Enchantment enchantment : bookMeta.getStoredEnchants().keySet()) {
                        int level = bookMeta.getStoredEnchants().get(enchantment);
                        ItemMeta armorMeta = current.getItemMeta();
                        if (armorMeta == null) {
                            armorMeta = plugin.getServer().getItemFactory().getItemMeta(current.getType());
                            current.setItemMeta(armorMeta);
                        }
                        armorMeta.addEnchant(enchantment, level, true);
                        current.setItemMeta(armorMeta);
                        cursor.setAmount(cursor.getAmount() - 1);
                        player.sendMessage(ChatColor.GREEN + "Applied enchant to armor!");
                        break;
                    }
                }
                return;
            }
        }

        if (event.getCurrentItem() != null && event.getCursor() != null) {
            ItemStack current = event.getCurrentItem();
            ItemStack cursor = event.getCursor();

            if (isArmor(current) && cursor.getType() == Material.ENCHANTED_BOOK && cursor.hasItemMeta()) {
                EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) cursor.getItemMeta();
                if (bookMeta.hasStoredEnchants()) {
                    for (Enchantment enchantment : bookMeta.getStoredEnchants().keySet()) {
                        int level = bookMeta.getStoredEnchants().get(enchantment);
                        ItemMeta armorMeta = current.getItemMeta();
                        if (armorMeta == null) {
                            armorMeta = plugin.getServer().getItemFactory().getItemMeta(current.getType());
                            current.setItemMeta(armorMeta);
                        }
                        armorMeta.addEnchant(enchantment, level, true);
                        current.setItemMeta(armorMeta);
                        cursor.setAmount(cursor.getAmount() - 1);
                        player.sendMessage(ChatColor.GREEN + "Applied enchant to armor!");
                        break;
                    }
                }
            }
        }
    }

    private ItemStack createEnchantedBook(Enchantment enchantment, int level, String displayName) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(enchantment, level, true);
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Left-click equipped armor or drag-and-drop to apply"));
        book.setItemMeta(meta);
        return book;
    }

    private boolean isArmor(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type.toString().endsWith("_HELMET") || type.toString().endsWith("_CHESTPLATE") ||
                type.toString().endsWith("_LEGGINGS") || type.toString().endsWith("_BOOTS");
    }
}