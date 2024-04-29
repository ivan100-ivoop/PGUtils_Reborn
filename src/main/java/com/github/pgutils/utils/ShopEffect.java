package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopEffect {
    private PGUtils pl = PGUtils.instance;
    private Map<String, ConfigurationSection> playerShop = new HashMap<>();
    private Map<String, ConfigurationSection> tntShop = new HashMap<>();
    private Map<Player, ClickMenu> players = new HashMap<>();
    private Map<Player, ClickMenu> tntPlayers = new HashMap<>();
    private PlayerEffects effects;

    public ShopEffect(
            Map<String, ConfigurationSection> playerShop,
            Map<String, ConfigurationSection> tntShop,
            Map<Player, ClickMenu> players,
            Map<Player, ClickMenu> tntPlayers
    ){
        this.tntShop = tntShop;
        this.playerShop = playerShop;
        this.players = players;
        this.tntPlayers = tntPlayers;
        this.effects = new PlayerEffects();
    }

    public boolean applyEffect(String ID, Player buyer){
        ConfigurationSection item = getConfiguration(ID);
        boolean isTNTPlayer = this.tntPlayers.containsKey(buyer);
        if(item == null) return false;

        if(item.getBoolean("onetime", false)) {
            if(this.tntShop.containsKey(ID)){
                this.tntShop.remove(ID);
            } else {
                this.playerShop.remove(ID);
            }
        }

        if(item.getBoolean("freeze", false)){
            for (Map.Entry<Player, ClickMenu> entity : this.players.entrySet()) {
                if(!this.effects.freezePlayer(entity.getKey(), item.getInt("timer", 10), item.getInt("timeout", 0))){
                    return false;
                }
            }
        } else if(!item.getString("effect", "none").equalsIgnoreCase("none")){
            String playerEffect = item.getString("effect", "none");
            for (Map.Entry<Player, ClickMenu> entity : this.players.entrySet()) {
                if(!this.effects.runEffect(entity.getKey(), playerEffect, item.getInt("timer", 10), item.getInt("timeout", 0))){
                    return false;
                }
            }
        }
        return true;
    }

    private ConfigurationSection getConfiguration(String id) {
        return (this.tntShop.containsKey(id) ? this.tntShop.get(id) : this.playerShop.containsKey(id) ? this.playerShop.get(id) : null);
    }

    public static class PlayerEffects {
        private boolean isAllowUse = true;
        private int waitTime = 0;
        private Map<String, Integer> disableEffect = new HashMap<>();

        public boolean freezePlayer(Player player, int durationSeconds, int useAgainAfter) {
            if(!isAllowUse) return false;
            waitTime = useAgainAfter;
            isAllowUse = false;
            PlayerManager.disableMove(player);
            this.freezeDisable();
            new BukkitRunnable() {
                @Override
                public void run() {
                    unfreezePlayer(player);
                    cancel();
                }
            }.runTaskLater(PGUtils.instance, durationSeconds * 20L);
            return true;
        }

        private void freezeDisable() {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(waitTime <= 0){
                        isAllowUse = true;
                        cancel();
                    } else {
                        waitTime--;
                    }
                }
            }.runTaskTimer(PGUtils.instance,  0L, 20L);
        }

        public void unfreezePlayer(Player player) {
            PlayerManager.enableMove(player);
        }

        public boolean runEffect(Player player, String effect, int durationSeconds, int useAgainAfter) {
            if(disableEffect.containsKey(effect)) return false;
            disableEffect.put(effect, useAgainAfter);
            PotionEffectType effectType = PotionEffectType.getByName(effect);
            if (effectType != null) {
                player.addPotionEffect(new PotionEffect(effectType, durationSeconds, 1));
                this.startTimerDisable(effect);
                return true;
            }
            return false;
        }

        private void startTimerDisable(String effect) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    int remainingTime = disableEffect.getOrDefault(effect, 1);
                    if(remainingTime <= 0){
                        disableEffect.remove(effect);
                        cancel();
                    } else {
                        disableEffect.put(effect, remainingTime - 1);
                    }
                }
            }.runTaskTimer(PGUtils.instance, 0L, 20L);
        }
    }

}
