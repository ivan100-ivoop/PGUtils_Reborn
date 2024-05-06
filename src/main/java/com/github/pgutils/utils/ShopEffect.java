package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopEffect {
    private PGUtils pl = PGUtils.instance;
    private Map<Player, ClickMenu> players = new HashMap<>();
    private PlayerEffects effects;

    public ShopEffect(
            Map<Player, ClickMenu> players
    ){
        this.players = players;
        this.effects = new PlayerEffects();
    }

    public boolean applyEffect(ConfigurationSection item, Player buyer){
        if(item == null) return false;

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
