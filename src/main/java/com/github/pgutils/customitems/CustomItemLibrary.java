package com.github.pgutils.customitems;

import com.github.pgutils.PGUtils;
import com.github.pgutils.customitems.effects.*;
import com.github.pgutils.utils.Keys;
import com.github.pgutils.utils.Messages;
import com.github.pgutils.utils.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CustomItemLibrary implements Listener {

    @EventHandler
    public static void onPlayerItemHeld(PlayerItemHeldEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkIfCustomItemInMainHand(event.getPlayer(), event);
            }
        }.runTaskLater(PGUtils.loader.instance, 1);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                checkIfCustomItemInArmor(player, event);
                checkIfCustomItemInMainHand(player, event);
                checkIfCustomItemInOffHand(player, event);
                checkIfCustomItemInInventory(player, event);
            }
        }.runTaskLater(PGUtils.loader.instance, 1);

    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();


        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            checkIfCustomItemInMainHandUsedRight(player,  event);
            checkIfCustomItemInArmor(player, event);
        }
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            checkIfCustomItemInMainHandUsedLeft(player, event);
            checkIfCustomItemInArmor(player, event);
        }
    }


    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (PlayerManager.cannotDamage.contains(event.getDamager())) {
                event.setCancelled(true);
                return;
            }
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            checkIfCustomItemInArmor(victim, event);
            checkIfCustomItemInMainHand(victim, event);
            checkIfCustomItemInOffHand(victim, event);
            checkIfCustomItemInInventory(victim, event);

            checkIfCustomItemInArmor(attacker, event);
            checkIfCustomItemInMainHand(attacker, event);
            checkIfCustomItemInOffHand(attacker, event);
            checkIfCustomItemInInventory(attacker, event);

        }
        if (event.getDamager() instanceof Firework) {
            Firework fw = (Firework) event.getDamager();
            if (fw.hasMetadata("nodamage")) {
                event.setCancelled(true);
            }
        }
        if (event.getDamager() instanceof Arrow) {

            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.hasMetadata("GoldenHarpArrow")) {
                if (arrow.getShooter() instanceof Entity) {
                    Entity shooter = (Entity) arrow.getShooter();
                    Location shooterLocation = shooter.getLocation();
                    new GoldenHarpTargetEffect(event.getEntity(), shooterLocation.add(0, 10, 0));
                    arrow.remove();
                    event.setCancelled(true);
                }

            }

            if (arrow.hasMetadata("GoldenHarpArrowSmall")) {
                if (event.getEntity() instanceof LivingEntity) {
                    if (arrow.getShooter() instanceof Player) {
                        LivingEntity entity = (LivingEntity) event.getEntity();
                        entity.damage(1, (Player) arrow.getShooter());
                        entity.setNoDamageTicks(1);
                        event.setCancelled(true);
                        arrow.remove();
                    }
                }
            }


            if (CustomEffect.hasEffect(arrow, QuantumLTFArrowEffect.class)) {
                QuantumLTFArrowEffect effect = (QuantumLTFArrowEffect) CustomEffect.getEffect(arrow, QuantumLTFArrowEffect.class);
                effect.activate();
                event.setCancelled(true);
                arrow.remove();

            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        Entity e = event.getEntity();

        if (e instanceof Player && CustomEffect.hasEffect((Player) e, CrownOfTheFallenEffect.class)){
            CrownOfTheFallenEffect effect = (CrownOfTheFallenEffect) CustomEffect.getEffect((Player) e, CrownOfTheFallenEffect.class);
            effect.handleGeneralDamage(event);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        if (event.getClickedInventory() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        new BukkitRunnable() {
            @Override
            public void run() {
                checkIfCustomItemInArmor(player, event);
                checkIfCustomItemInMainHand(player, event);
                checkIfCustomItemInOffHand(player, event);
            }
        }.runTaskLater(PGUtils.loader.instance, 1);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        checkIfCustomItemInArmor(player, event);
        checkIfCustomItemInMainHand(player, event);
        checkIfCustomItemInOffHand(player, event);
        checkIfCustomItemInInventory(player, event);
    }

    public static void onStart() {
        Event event = new Event() {
            @NotNull
            @Override
            public HandlerList getHandlers() {
                return null;
            }
        };
        for (Player player : Bukkit.getOnlinePlayers()) {
            checkIfCustomItemInArmor(player, event);
            checkIfCustomItemInMainHand(player, event);
            checkIfCustomItemInOffHand(player, event);
            checkIfCustomItemInInventory(player, event);
        }
    }

    public static <E extends Event> void checkIfCustomItemInMainHand(Player player, E event) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null)
            return;

        if (item.getItemMeta() == null)
            return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(Keys.goldenHarp, PersistentDataType.BOOLEAN)) {
            if (event instanceof EntityShootBowEvent) {
                EntityShootBowEvent shootEvent = (EntityShootBowEvent) event;
                Arrow arrow = (Arrow) shootEvent.getProjectile();
                arrow.setMetadata("GoldenHarpArrow", new FixedMetadataValue(PGUtils.loader.instance, true));
            }
        }

        if (container.has(Keys.quantumLTF, PersistentDataType.BOOLEAN)) {
            if (event instanceof EntityShootBowEvent) {
                EntityShootBowEvent shootEvent = (EntityShootBowEvent) event;
                if (CustomEffect.hasEffect(player, QuantumLTFHoldEffect.class)) {
                    QuantumLTFHoldEffect effect = (QuantumLTFHoldEffect) CustomEffect.getEffect(player, QuantumLTFHoldEffect.class);
                    effect.shoot(shootEvent);
                }
            }
        }

        if (container.has(Keys.bombHead, PersistentDataType.BOOLEAN)) {
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
                if (damageEvent.getEntity() instanceof Player) {
                    Player attacker = (Player) damageEvent.getDamager();
                    Player victim = (Player) damageEvent.getEntity();
                    if (CustomEffect.hasEffect(attacker, HumanBombEffect.class)) {
                       if (!CustomEffect.hasEffect(victim, HumanBombEffect.class)) {
                           HumanBombEffect effect = (HumanBombEffect) CustomEffect.getEffect(attacker, HumanBombEffect.class);
                           new HumanBombEffect(victim, effect.getBombTicks());
                           CustomEffect.removeEffect(effect);
                       }
                       else {
                           attacker.sendMessage(Messages.getMessage("human-bomb-already-active", "That player already has a bomb on their head!", false));
                           damageEvent.setCancelled(true);
                       }
                    }
                }

            }
        }

        if (container.has(Keys.quantumLTF, PersistentDataType.BOOLEAN)) {
            if (!CustomEffect.hasEffect(player, QuantumLTFHoldEffect.class))
                new QuantumLTFHoldEffect(player);
        }

        if (container.has(Keys.miniBeacon, PersistentDataType.BOOLEAN)) {
            if (event instanceof PlayerToggleSneakEvent || event instanceof PlayerItemHeldEvent) {
                System.out.println(player.isSneaking());
                if (!player.isSneaking())
                    return;
                if (!CustomEffect.hasEffect(player, MiniBeaconHoldSneakEffect.class))
                    new MiniBeaconHoldSneakEffect(player);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();

        if (droppedItem == null)
            return;

        if (droppedItem.getItemMeta() == null)
            return;

        if (droppedItem.getItemMeta().getPersistentDataContainer() == null)
            return;
        if (droppedItem.getItemMeta().getPersistentDataContainer().has(Keys.undroppable, PersistentDataType.BOOLEAN)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Messages.getMessage("undroppable-item", "You cannot drop this item!", false));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack placedBlock = event.getItemInHand();

        if (placedBlock == null)
            return;

        if (placedBlock.getItemMeta() == null)
            return;

        if (placedBlock.getItemMeta().getPersistentDataContainer() == null)
            return;

        if (placedBlock.getItemMeta().getPersistentDataContainer().has(Keys.unplaceable, PersistentDataType.BOOLEAN)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Messages.getMessage("unplaceable-item", "You cannot place this item!", false));
        }
    }

    // after item it should be something that extends event but is not event
    public static <E extends Event> void checkIfCustomItemInMainHandUsedRight(Player player, E event) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null)
            return;
        if (item.getItemMeta() == null)
            return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(Keys.partyStick, PersistentDataType.BOOLEAN)) {
            if(!CustomEffect.hasEffect(player, PartyEffectCooldown.class)) {
                new PartyEffect(player);
                Vector direction = player.getLocation().getDirection().clone();
                direction.normalize();
                direction.multiply(1.5);
                player.setVelocity(direction);
                new PartyEffectCooldown(player);
                player.getLocation().getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 50, 0.5, 0.2, 0.5, 0.01);
            }
        }

        if (container.has(Keys.quantumLTF, PersistentDataType.BOOLEAN)) {
            if (event instanceof PlayerInteractEvent) {
                if (CustomEffect.hasEffect(player, QuantumLTFHoldEffect.class)) {
                    // Check if player is sneaking
                    if (player.isSneaking()) {
                        QuantumLTFHoldEffect effect = (QuantumLTFHoldEffect) CustomEffect.getEffect(player, QuantumLTFHoldEffect.class);
                        effect.holding();
                    } else {
                        QuantumLTFHoldEffect effect = (QuantumLTFHoldEffect) CustomEffect.getEffect(player, QuantumLTFHoldEffect.class);
                        effect.notHolding();
                    }
                }
            }
        }


    }

    public static void checkIfCustomItemInMainHandUsedLeft(Player player, Event event) {

    }


    public static void checkIfCustomItemInOffHand(Player player, Event event) {
        ItemStack item = player.getInventory().getItemInOffHand();

    }

    public static void checkIfCustomItemInArmor(Player player, Event event) {
        ItemStack[] armor = player.getInventory().getArmorContents();

        if (armor[3] == null){
            if (CustomEffect.hasEffect(player, CrownOfTheFallenEffect.class)) {
                CustomEffect.removeEffect(CustomEffect.getEffect(player, CrownOfTheFallenEffect.class));
            }

            if (CustomEffect.hasEffect(player, GodlessEffect.class)) {
                CustomEffect.removeEffect(CustomEffect.getEffect(player, GodlessEffect.class));
            }

            if (CustomEffect.hasEffect(player, AtomizerEffect.class)) {
                CustomEffect.removeEffect(CustomEffect.getEffect(player, AtomizerEffect.class));
            }
        }

        if (armor[3] != null) {
            if (armor[3].getItemMeta() != null) {
                ItemMeta meta = armor[3].getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();

                if (container.has(Keys.crownOfTheFallen, PersistentDataType.BOOLEAN)) {
                    if (!CustomEffect.hasEffect(player, CrownOfTheFallenEffect.class)) {
                        new CrownOfTheFallenEffect(player);
                    }
                    else if (event instanceof EntityDamageByEntityEvent) {
                        if (CustomEffect.hasEffect(player, CrownOfTheFallenEffect.class)) {
                            CrownOfTheFallenEffect effect = (CrownOfTheFallenEffect) CustomEffect.getEffect(player, CrownOfTheFallenEffect.class);
                            effect.handlePlayerDamage((EntityDamageByEntityEvent) event);
                        }
                    }

                }

                if (container.has(Keys.godLess, PersistentDataType.BOOLEAN)) {
                    if (!CustomEffect.hasEffect(player, GodlessEffect.class)) {
                        new GodlessEffect(player);
                    }
                    else if (event instanceof EntityDamageByEntityEvent) {
                        if (CustomEffect.hasEffect(player, GodlessEffect.class)) {
                            GodlessEffect effect = (GodlessEffect) CustomEffect.getEffect(player, GodlessEffect.class);
                            effect.handlePlayerDamage((EntityDamageByEntityEvent) event);
                        }
                    }
                }

                if (container.has(Keys.atomizer, PersistentDataType.BOOLEAN)) {
                    if (!CustomEffect.hasEffect(player, AtomizerEffect.class)) {
                        new AtomizerEffect(player);
                    }
                    else if (event instanceof EntityDamageByEntityEvent) {
                        if (CustomEffect.hasEffect(player, AtomizerEffect.class)) {
                            AtomizerEffect effect = (AtomizerEffect) CustomEffect.getEffect(player, AtomizerEffect.class);
                            effect.handlePlayerDamage((EntityDamageByEntityEvent) event);
                        }
                    }
                }

            }
        }
    }

    public static void checkIfCustomItemInInventory(Player player, Event event) {
        ItemStack[] inventory = player.getInventory().getContents();


    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        CustomEffect.removeAllEffects(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        CustomEffect.removeAllEffects(player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        checkIfCustomItemInArmor(player, event);
        checkIfCustomItemInMainHand(player, event);
        checkIfCustomItemInOffHand(player, event);
        checkIfCustomItemInInventory(player, event);
    }


    @EventHandler
    public void onArrowHitGround(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.hasMetadata("GoldenHarpArrow") && event.getHitEntity() == null) {
                if (arrow.getShooter() instanceof Entity) {
                    Entity shooter = (Entity) arrow.getShooter();
                    Location shooterLocation = shooter.getLocation();
                    new GoldenHarpGroundEffect(event.getHitBlock().getLocation(), shooterLocation.clone().add(0, 10, 0));
                    arrow.remove();
                }
            }

            if (CustomEffect.hasEffect(arrow, QuantumLTFArrowEffect.class)) {
                QuantumLTFArrowEffect effect = (QuantumLTFArrowEffect) CustomEffect.getEffect(arrow, QuantumLTFArrowEffect.class);
                effect.activate();
                arrow.remove();
            }
        }
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        checkIfCustomItemInMainHand(player, event);

    }





}
