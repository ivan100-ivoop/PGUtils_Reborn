package com.github.pgutils.entities.games;

import com.github.pgutils.PGUtils;
import com.github.pgutils.utils.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Shop implements Listener {
    private PGUtils pl = PGUtils.instance;
    private String title = GeneralUtils.fixColors(pl.getConfig().getString("shop.title"));
    private List<ConfigurationSection> items = new ArrayList<>();
    private Map<String, ConfigurationSection> playerShop = new HashMap<>();

    private Map<Player, ClickMenu> players = new HashMap<>();
    private TNTRArena arena;
    private ShopEffect effects;

    public Shop (TNTRArena arena){
        FileConfiguration config = PGUtils.instance.getConfig();

        for (String key : config.getConfigurationSection("shop.items").getKeys(false)) {
            items.add(config.getConfigurationSection(String.format("shop.items.%s", key)));
        }

        this.arena = arena;
        this.effects = new ShopEffect(players);
    }
    public void resetAll(){

        for (Map.Entry<Player, ClickMenu> pls : players.entrySet()){
            ClickMenu cl =  pls.getValue();
            cl.close(pls.getKey());
        }

    }

    public void addPlayer(Player player){
        ClickMenu shop_1 = getPlayerShop();
        shop_1.setPlayer(player);
        player.getInventory().setItem(8, Shop.menuItem());
        players.put(player, shop_1);
    }

    public void open(Player player){
        ClickMenu shop = (players.containsKey(player) ? players.get(player) : null);
        if(shop != null){
            shop.open(player);
            shop.onClick(new ClickMenu.onClick() {
                @Override
                public boolean click(Player clicker, ClickMenu menu, ClickMenu.Row row, int slot, String ID) {

                    if(playerShop.containsKey(ID)){
                        ConfigurationSection shopId = playerShop.get(ID);

                        if(shopId == null) return false;

                        if(shopId.getBoolean("onlyTNT", false)){
                            if(!clicker.getInventory().contains(Material.TNT)) return false;
                        }

                        if(effects.applyEffect(shopId, clicker)){
                            if(shopId.getBoolean("onetime", false)) {
                                playerShop.remove(ID);
                                for (Map.Entry<Player, ClickMenu> menuItem : players.entrySet()){
                                    ClickMenu menus = menuItem.getValue();

                                    ItemStack itemsStack = new ItemStack(Material.valueOf(shopId.getString("material", "STONE")), shopId.getInt("amount", 1));

                                    menus.closeAll();
                                    menus.removeBtn(itemsStack);
                                }
                            }
                        }

                        //clicker.sendMessage(Messages.messageWithPrefix("errors.shop.coins", "&eYou need to have &b%cost%&e!").replace("%cost%", shopId.get(ID).getString("effect", "Test")));
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public static ItemStack menuItem(){
        ItemStack shopItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta4 = shopItem.getItemMeta();
        meta4.setDisplayName("§f§lShop Menu");
        meta4.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta4.setUnbreakable(true);
        shopItem.setItemMeta(meta4);
        return shopItem;
    }

    public ClickMenu getPlayerShop(){
        int Menu_index = 0;
        int Menu_row = 0;

        ClickMenu shop = new ClickMenu().setName(title);
        shop.setSize(PGUtils.instance.getConfig().getInt("row", 6));

        for (ConfigurationSection _item : items) {
            String ID = GeneralUtils.generateUniqueID();

            ItemStack item = new ItemStack(Material.valueOf(_item.getString("material", "STONE")), _item.getInt("amount", 1));
            shop.addButton(shop.getRow(Menu_row), Menu_index, item, _item.getString("name", "Item 1"), ID, _item.getStringList("lore").toArray(new String[0]));

            playerShop.put(ID, _item);

            if (Menu_index == 8) {
                Menu_row++;
                Menu_index = 0;
            } else {
                Menu_index++;
            }
        }
        return shop;
    }

}
