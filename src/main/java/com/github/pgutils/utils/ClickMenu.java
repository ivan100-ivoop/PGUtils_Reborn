package com.github.pgutils.utils;
import com.github.pgutils.PGUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickMenu implements Listener {

    private String name;
    private int size;
    private onClick click;
    List<String> viewing = new ArrayList<String>();

    private Player view_player = null;

    private ItemStack[] items;

    private Map<ItemStack, String> itemID = new HashMap<>();

    public ClickMenu(String name, int size, onClick click) {
        this.name = name;
        this.size = size * 9;
        this.items = new ItemStack[this.size];
        this.click = click;
        Bukkit.getPluginManager().registerEvents(this, PGUtils.instance);
    }

    public ClickMenu(){
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugins()[0]);
    }

    public ClickMenu setName(String name){
        this.name = name;
        return this;
    }

    public ClickMenu setPlayer(Player player){
        this.view_player = player;
        return this;
    }

    public ClickMenu setSize(int size){
        this.size = size * 9;
        this.items = new ItemStack[this.size];
        return this;
    }

    public ClickMenu onClick(onClick click){
        this.click = click;
        return this;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (Player p : this.getViewers())
            close(p);
    }

    public ClickMenu open(Player p) {
        p.openInventory(getInventory(p));
        viewing.add(p.getName());
        return this;
    }

    public ClickMenu open() {
        if(this.view_player != null) {
            this.view_player.openInventory(getInventory(this.view_player));
            viewing.add(this.view_player.getName());
        }
        return this;
    }

    private Inventory getInventory(Player p) {
        Inventory inv = Bukkit.createInventory(p, size, name);
        inv.setContents(this.items);
        return inv;
    }

    public ClickMenu close(Player p) {
        if (p.getOpenInventory().getTitle().equals(name))
            p.closeInventory();
        return this;
    }

    public List<Player> getViewers() {
        List<Player> viewers = new ArrayList<Player>();
        for (String s : viewing)
            viewers.add(Bukkit.getPlayer(s));
        return viewers;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (viewing.contains(event.getWhoClicked().getName())) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            if(event.getSlot() >= 0) {
                Row row = getRowFromSlot(event.getSlot());
                if(!itemID.isEmpty() && itemID.containsKey(event.getCurrentItem())){
                    if (!click.click(p, this, row, event.getSlot() - row.getRow() * 9, itemID.get(event.getCurrentItem()))){
                        close(p);
                    }
                } else {
                    if (!click.click(p, this, row, event.getSlot() - row.getRow() * 9, event.getCurrentItem())){
                        close(p);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (viewing.contains(event.getPlayer().getName()))
            viewing.remove(event.getPlayer().getName());
    }

    public ClickMenu addButton(Row row, int position, ItemStack item, String name, String id, String... lore) {
        items[row.getRow() * 9 + position] = getItem(item, name, id, lore);
        return this;
    }

    public Row getRowFromSlot(int slot) {
        return new Row(slot / 9, items);
    }

    public Row getRow(int row) {
        return new Row(row, items);
    }

    public interface onClick {
        public abstract boolean click(Player clicker, ClickMenu menu, Row row, int slot, ItemStack item);
        public abstract boolean click(Player clicker, ClickMenu menu, Row row, int slot, String ID);
    }

    public class Row {
        private ItemStack[] rowItems = new ItemStack[9];
        int row;

        public Row(int row, ItemStack[] items) {
            this.row = row;
            int j = 0;
            for (int i = (row * 9); i < (row * 9) + 9; i++) {
                rowItems[j] = items[i];
                j++;
            }
        }

        public ItemStack[] getRowItems() {
            return rowItems;
        }

        public ItemStack getRowItem(int item) {
            return rowItems[item] == null ? new ItemStack(Material.AIR) : rowItems[item];
        }

        public int getRow() {
            return row;
        }
    }

    private ItemStack getItem(ItemStack item, String name, String id, String... lores) {
        List<String> _lore = new ArrayList<>();
        ItemMeta im = item.getItemMeta();

        im.setDisplayName(GeneralUtils.fixColors(name));

        for (String lore: lores){
            _lore.add(lore);
        }

        im.setLore(_lore);
        item.setItemMeta(im);

        itemID.put(item, id);

        return item;
    }

}