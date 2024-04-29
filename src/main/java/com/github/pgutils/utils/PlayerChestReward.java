package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayerChestReward {
    private static final int maxSlot = 9;
    public static final String ChestTitle = Messages.getMessage("reward-title", "&8Reward Chest", false);

    public static boolean isPlayerHaveChest(Player player) {
        File chestFile = new File(PGUtils.getPlugin(PGUtils.class).loader.rewardsChest, player.getName() + ".yml");
        return chestFile.exists();
    }

    public static boolean createEmptyPlayerChest(Player player) {
        if (!isPlayerHaveChest(player)) {
            File chestFile = new File(PGUtils.getPlugin(PGUtils.class).loader.rewardsChest, player.getName() + ".yml");
            try {
                chestFile.createNewFile();
                FileConfiguration chest = YamlConfiguration.loadConfiguration(chestFile);
                chest.set("content", new ArrayList<>());
                chest.save(chestFile);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isPlayerChestFull(Player player) {
        File chestFile = new File(PGUtils.getPlugin(PGUtils.class).loader.rewardsChest, player.getName() + ".yml");

        if (PlayerChestReward.isPlayerHaveChest(player)) {
            FileConfiguration chest = YamlConfiguration.loadConfiguration(chestFile);
            return chest.getList("content") != null && chest.getList("content").size() >= maxSlot;
        }

        return true;
    }

    public static boolean clearPlayerChest(Player player) {
        if (PlayerChestReward.isPlayerHaveChest(player)) {
            try {
                File chestFile = new File(PGUtils.getPlugin(PGUtils.class).loader.rewardsChest, player.getName() + ".yml");
                FileConfiguration chest = YamlConfiguration.loadConfiguration(chestFile);
                chest.set("content", new ArrayList<ItemStack>());
                chest.save(chestFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Inventory getPlayerChest(Player player) {
        File chestFile = new File(PGUtils.getPlugin(PGUtils.class).loader.rewardsChest, player.getName() + ".yml");

        if (PlayerChestReward.isPlayerHaveChest(player)) {
            FileConfiguration chest = YamlConfiguration.loadConfiguration(chestFile);
            ItemStack[] chestContents = chest.getList("content").toArray(new ItemStack[0]);
            Inventory inv = Bukkit.createInventory(null, InventoryType.PLAYER, ChestTitle);
            inv.setContents(chestContents);
            return inv;
        } else {
            PlayerChestReward.createEmptyPlayerChest(player);
            return getPlayerChest(player);
        }
    }

    public static boolean updatePlayerCheste(ItemStack[] contents, Player player) {
        if (PlayerChestReward.isPlayerHaveChest(player)) {
            try {
                File chestFile = new File(PGUtils.getPlugin(PGUtils.class).loader.rewardsChest, player.getName() + ".yml");
                FileConfiguration chest = YamlConfiguration.loadConfiguration(chestFile);
                List<ItemStack> chestContents = new ArrayList<ItemStack>();

                for (ItemStack item : contents) {
                    if (item != null)
                        chestContents.add(item);
                }

                chest.set("content", chestContents);
                chest.save(chestFile);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean addItem(ItemStack item, Player player) {
        if (!PlayerChestReward.isPlayerHaveChest(player)) {
            if (PlayerChestReward.createEmptyPlayerChest(player)) {
                return putItem(item, player);
            }
        }
        return putItem(item, player);
    }

    private static boolean putItem(ItemStack item, Player player) {
        try {
            File chestFile = new File(PGUtils.getPlugin(PGUtils.class).loader.rewardsChest, player.getName() + ".yml");
            FileConfiguration chest = YamlConfiguration.loadConfiguration(chestFile);
            List<ItemStack> chestContents = (List<ItemStack>) chest.getList("content");
            if (chestContents.size() < 9) {
                chestContents.add(item);
                chest.set("content", chestContents);
                chest.save(chestFile);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean restoreInv(Player player) {
        try {
            File invBackup = new File(PGUtils.getPlugin(PGUtils.class).loader.saveInv, player.getName() + ".yml");
            if (invBackup.exists()) {
                YamlConfiguration invPlayer = new YamlConfiguration();
                invPlayer.load(invBackup);
                ArrayList<ItemStack> tempInv = (ArrayList<ItemStack>) invPlayer.getList("inv");
                for (int i = 0; i < tempInv.size(); i++) {
                    player.getInventory().setItem(i, tempInv.get(i));
                }
                invBackup.delete();
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean saveInv(Player player) {
        try {
            ArrayList<ItemStack> tempInv = new ArrayList<ItemStack>();
            File invBackup = new File(PGUtils.getPlugin(PGUtils.class).loader.saveInv, player.getName() + ".yml");
            if (!invBackup.exists()) {
                invBackup.createNewFile();
                YamlConfiguration invPlayer = new YamlConfiguration();
                invPlayer.load(invBackup);
                player.getInventory().forEach(itemStack -> {
                    tempInv.add(itemStack);
                });
                invPlayer.set("inv", tempInv);
                invPlayer.save(invBackup);
                player.getInventory().clear();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
