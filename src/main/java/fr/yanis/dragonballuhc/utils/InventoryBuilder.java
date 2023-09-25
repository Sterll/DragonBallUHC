package fr.yanis.dragonballuhc.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryBuilder {

    private final Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> clickHandlers;
    private final Consumer<InventoryCloseEvent> closeHandler;

    public InventoryBuilder(String title, int rows, Plugin plugin) {
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        this.clickHandlers = new HashMap<>();
        this.closeHandler = null;

        Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
    }

    public InventoryBuilder(String title, InventoryType type, Plugin plugin) {
        this.inventory = Bukkit.createInventory(null, type, title);
        this.clickHandlers = new HashMap<>();
        this.closeHandler = null;

        Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
    }

    public InventoryBuilder setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> clickHandler) {
        inventory.setItem(slot, item);
        if (clickHandler != null) {
            clickHandlers.put(slot, clickHandler);
        }
        return this;
    }

    public InventoryBuilder removeItem(int slot) {
        inventory.setItem(slot, null);
        clickHandlers.remove(slot);
        return this;
    }

    public InventoryBuilder clear() {
        inventory.clear();
        clickHandlers.clear();
        return this;
    }

    public InventoryBuilder update(){
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                item.setItemMeta(meta);
                inventory.setItem(i, item);
            }
        }
        return this;
    }

    public InventoryBuilder updateName(int slot, String name){
        ItemStack item = inventory.getItem(slot);
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
        }
        return this;
    }

    public InventoryBuilder updateLore(int slot, List<String> lore) {
        ItemStack item = inventory.getItem(slot);
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
        }
        return this;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    private class InventoryListener implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (event.getInventory().equals(inventory)) {
                event.setCancelled(true);
                Consumer<InventoryClickEvent> clickHandler = clickHandlers.get(event.getRawSlot());
                if (clickHandler != null) {
                    clickHandler.accept(event);
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (event.getInventory().equals(inventory) && closeHandler != null) {
                closeHandler.accept(event);
            }
        }
    }

}
