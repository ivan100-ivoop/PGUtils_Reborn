package com.github.pgutils.utils;

import com.github.pgutils.PGUtils;
import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.customitems.effects.DynamicEffect;
import com.github.pgutils.entities.Lobby;
import com.github.pgutils.entities.PlaySpace;
import com.github.pgutils.entities.service.PortalService;
import com.github.pgutils.selections.PlayerLobbySelector;
import com.github.pgutils.selections.PlayerPlaySpaceSelector;
import com.github.pgutils.utils.db.PortalSave;
import com.github.pgutils.utils.db.RespawnSave;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralUtils {
    public static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");


    public static final String fixColors(String message) {
        return GeneralUtils.colorize(GeneralUtils.translateHexColorCodes(message));
    }

    private static final String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static final String translateHexColorCodes(String message) {
        Matcher matcher = GeneralUtils.HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 32);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, "§x§" + group.charAt(0) + '§' + group.charAt(1) + '§' + group.charAt(2) + '§' + group.charAt(3) + '§' + group.charAt(4) + '§' + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }

    public static String hexToMinecraftColor(String hexColor) {
        if (hexColor.startsWith("#") && hexColor.length() == 7) {
            StringBuilder converted = new StringBuilder("§x");
            for (char c : hexColor.substring(1).toCharArray()) {
                converted.append("§").append(c);
            }
            return converted.toString();
        }
        return hexColor;
    }

    public static void runCommand(CommandSender sender, String cmd) {
        Bukkit.getServer().dispatchCommand(sender, cmd);
    }

    public static void playerSelectPlaySpace(Player sender, PlaySpace playSpace) {
        if (PGUtils.loader.selectedPlaySpace.stream().anyMatch(selector -> selector.player.equals(sender))) {
            PGUtils.loader.selectedPlaySpace.stream()
                    .filter(selector -> selector.player.equals(sender))
                    .forEach(selector -> selector.playSpace = playSpace);
        } else {
            PGUtils.loader.selectedPlaySpace.add(new PlayerPlaySpaceSelector(sender, playSpace));
        }
    }

    public static void playerSelectLobby(Player sender, Lobby lobby) {
        if (PGUtils.loader.selectedLobby.stream().anyMatch(selector -> selector.player.equals(sender))) {
            PGUtils.loader.selectedLobby.stream()
                    .filter(selector -> selector.player.equals(sender))
                    .forEach(selector -> selector.lobby = lobby);
        } else {
            PGUtils.loader.selectedLobby.add(new PlayerLobbySelector(sender, lobby));
        }

    }


    public static Lobby isPlayerInGame(Player player) {
        Optional<Lobby> _lobby = Lobby.lobbies.stream()
                .filter(lobby -> lobby.getPlayers().contains(player))
                .findFirst();
        if (!_lobby.isPresent()) {
            return null;
        }
        Lobby lobby = _lobby.get();
        return lobby;
    }

    public static boolean kickPlayerGlobal(Player player) {
        Lobby lobby = GeneralUtils.isPlayerInGame(player);
        if (lobby == null) {
            return false;
        }
        lobby.kickPlayer(player);
        return true;
    }

    public static double speedFunc(double a, double b, double c) {
        if (c == a || c == b) {
            return 0.0;
        }
        double middle = (a + b) / 2.0;
        double distanceToMiddle = Math.abs(c - middle);
        double normalizedValue = 1.0 - distanceToMiddle / ((b - a) / 2.0);
        return Math.max(0.0, Math.min(1.0, normalizedValue));
    }

    public static boolean setRespawnPoint(Location loc1) {
        new PortalService.RespawnServer().updateRespawn(new RespawnSave().setRespawn(loc1));
        return true;
    }

    public static Location getRespawnPoint() {
       return new PortalService.RespawnServer().getRespawn().getRespawn();
    }

    public static int findPriorityLobby() {
        return 1;
    }

    public static String generateUniqueID() {
        String id = "";
        for (int i = 0; i < 10; i++) {
            id += (int) (Math.random() * 10);
        }
        return id;
    }

    public static int generateID(int length) {
        return (int) (Math.random() * length);
    }

    public static String generateLoadingBar(int percentage, String barColor, String backgroundColor) {
        String bar = "";
        for (int i = 1; i < 11; i++) {
            if (percentage >= i * 10) {
                bar += barColor + "█";
            } else {
                bar += backgroundColor + "█";
            }
        }
        return bar;
    }

    public static Lobby getLobbyByID(int id) {
        Optional<Lobby> _lobby = Lobby.lobbies.stream()
                .filter(lobby -> lobby.getID() == id)
                .findFirst();
        if (!_lobby.isPresent()) {
            return null;
        }
        Lobby lobby = _lobby.get();
        return lobby;
    }

    public static String formatSeconds(int seconds) {
        int minutes = seconds/60;
        seconds = seconds%60;

        String sMinutes = minutes + "";
        String sSeconds = seconds + "";

        if(minutes < 10) sMinutes = "0" + minutes;
        if(seconds < 10) sSeconds = "0" + seconds;

        return sMinutes + ":" + sSeconds;
    }

    public static PlaySpace getPlaySpaceByID(int id) {
        Optional<PlaySpace> _playSpace = PlaySpace.playSpaces.stream()
                .filter(playSpace -> playSpace.getID() == id)
                .findFirst();
        if (!_playSpace.isPresent()) {
            return null;
        }
        PlaySpace playSpace = _playSpace.get();
        return playSpace;
    }

    public static float getAngleFromTo(Location location, Location entityLocation) {
        double x = entityLocation.getX() - location.getX();
        double z = entityLocation.getZ() - location.getZ();
        return (float) Math.toDegrees(Math.atan2(-x, z));
    }

    public static double speedFunc2(double target, double current, double speed) {
        return (target - current) / speed;

    }

    public static void cleanupArmorStands() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
                if (entity instanceof ArmorStand && entity.getPersistentDataContainer().has(Keys.dynamicObject, PersistentDataType.BOOLEAN) && !CustomEffect.hasEffect(entity, DynamicEffect.class)) {
                    entity.teleport(new Location(player.getWorld(), 0, -100000, 0));
                }
            }
        }
    }

    public static PlaySpace getPlaySpaceByName(String name) {
        Optional<PlaySpace> _playSpace = PlaySpace.playSpaces.stream()
                .filter(playSpace -> playSpace.getName().equalsIgnoreCase(name))
                .findFirst();
        if (!_playSpace.isPresent()) {
            return null;
        }
        PlaySpace playSpace = _playSpace.get();
        return playSpace;
    }

    public static Lobby getLobbyByName(String name) {
        Optional<Lobby> _lobby = Lobby.lobbies.stream()
                .filter(lobby -> lobby.getName().equalsIgnoreCase(name))
                .findFirst();
        if (!_lobby.isPresent()) {
            return null;
        }
        Lobby lobby = _lobby.get();
        return lobby;
    }

    public static Location getClosestLocation(Location location, List<Location> locations) {
        double distance = Double.MAX_VALUE;
        Location closest = null;
        for (Location loc : locations) {
            if (loc.distance(location) < distance) {
                distance = loc.distance(location);
                closest = loc;
            }
        }
        return closest;
    }


    public static int generateEntityId() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }
}
