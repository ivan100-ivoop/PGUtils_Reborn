package com.github.pgutils.customitems.effects;

import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.utils.GeneralUtils;
import com.github.pgutils.utils.Keys;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class PartyEffectCooldown extends CustomEffect {

    int maxTicks = 200;

    Player effectedPlayer;

    public PartyEffectCooldown(Player effectedPlayer) {
        super(effectedPlayer);
        this.effectedPlayer = effectedPlayer;
    }

    @Override
    public void onUpdate() {
        ItemStack item = effectedPlayer.getInventory().getItemInMainHand();
        if (getTicks() > maxTicks){
            CustomEffect.removeEffect(this);
            effectedPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&aParty Cooldown Ready!")));
            effectedPlayer.playSound(getEffectedEntity().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
            return;
        }
        if (item == null)
            return;
        if (item.getItemMeta() == null)
            return;
        if (item.getItemMeta().getPersistentDataContainer() == null)
            return;



        if (item.getItemMeta().getPersistentDataContainer().has(Keys.partyStick, PersistentDataType.BOOLEAN)) {
            int percentage = (int) ((double) getTicks() / (double) maxTicks * 100);
            effectedPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(GeneralUtils.fixColors("&cParty Cooldown %cooldownbar%")
                    .replace("%cooldownbar%", GeneralUtils.generateLoadingBar(percentage, "§e", "§7"))));
        }

    }

    @Override
    public void onRemove() {

    }
}
