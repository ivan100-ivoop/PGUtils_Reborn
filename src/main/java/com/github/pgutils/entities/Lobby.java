package com.github.pgutils.entities;


import com.github.pgutils.PGUtils;
import com.github.pgutils.enums.LobbyMode;
import com.github.pgutils.enums.LobbyStatus;
import com.github.pgutils.utils.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lobby {

    public static List<Lobby> lobbies = new ArrayList<>();
    private final int ID;

    // Saved
    private String uniqueID;

    // Saved
    private Location pos;

    private final List<Player> players;

    private final List<Player> waitingPlayers;

    private LobbyStatus status;

    private final List<PlaySpace> playSpaces = new ArrayList<>();

    private PlaySpace currentPlaySpace = null;

    private final int lastGame = 0;

    private int pickedGameID = 0;

    // Saved
    private int maxPlayers = 32;

    // Saved
    private int minPlayers = 2;

    private final int lobbyStartingTime = 200;

    private final int lobbyResettingTime = 200;

    private int lobbyStartingTick = 0;

    private int lobbyResettingTick = 0;

    private final int showPlayersMessageTime = 5;

    private int showPlayersMessageTick = 0;

    // Saved
    private boolean tournamentMode = false;

    // Saved
    private LobbyMode mode = LobbyMode.AUTO;

    // Saved
    private boolean isLocked = false;

    // Saved
    private String name;

    private boolean testMode = false;

    public Lobby() {
        players = new ArrayList<>();
        waitingPlayers = new ArrayList<>();
        status = LobbyStatus.WAITING_FOR_PLAYERS;
        lobbies.add(this);
        ID = lobbies.size();
        uniqueID = UUID.randomUUID().toString();
        name = "Unnamed Lobby " + ID;
    }

    public void update() {
        players.stream()
                .forEach(player -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 5, 1, true, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 5, 5, true, false));
                });
        if (status == LobbyStatus.WAITING_FOR_PLAYERS) {
            if (players.size() >= minPlayers && mode == LobbyMode.AUTO) {
                startSequence();
            } else if (players.size() >= minPlayers && mode == LobbyMode.MANUAL) {
                startWaitingForHost();
            }
            showPlayersMessageTick++;
            if (showPlayersMessageTick >= showPlayersMessageTime) {
                showPlayersMessageTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("lobby-waiting-players", "&eWaiting for players &b%players%/%min_players% &e!", false).replace("%players%", String.valueOf(players.size())).replace("%min_players%", String.valueOf(minPlayers)))));
            }
        } else if (status == LobbyStatus.WAITING_FOR_HOST) {

            showPlayersMessageTick++;
            if (showPlayersMessageTick >= showPlayersMessageTime) {
                showPlayersMessageTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("lobby-waiting-host", "&eWaiting for a host to start the game!", false))));
            }
            if (players.size() < minPlayers) {
                status = LobbyStatus.WAITING_FOR_PLAYERS;
                lobbyStartingTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("lobby-filed-start-game-players", "&4Game starting failed due to not enough players!", false))));
            }

            if (mode == LobbyMode.AUTO)
                startSequence();
        } else if (status == LobbyStatus.STARTING) {
            if (lobbyStartingTick >= lobbyStartingTime) {
                start();
            }
            lobbyStartingTick++;
            if (lobbyStartingTick % 20 == 0)
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("lobby-start-timer", "&eThe game will start in &b%time%&e seconds!", false).replace("%time%", String.valueOf((lobbyStartingTime - lobbyStartingTick) / 20)))));
            if (players.size() < minPlayers) {
                status = LobbyStatus.WAITING_FOR_PLAYERS;
                lobbyStartingTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("lobby-filed-start-game-players", "&4Game starting failed due to not enough players!", false))));
            }
        } else if (status == LobbyStatus.IN_PROGRESS) {
            currentPlaySpace.update();
        } else if (status == LobbyStatus.RESETTING) {
            if (lobbyResettingTick >= lobbyResettingTime) {
                status = LobbyStatus.WAITING_FOR_PLAYERS;
                lobbyResettingTick = 0;
            }
            lobbyResettingTick++;
            if (lobbyResettingTick % 20 == 0)
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("lobby-status-resetting-message", "&6The lobby is resetting!", false))));
        }
    }

    private int pickRandomGame() {
        if (playSpaces.size() == 0) return -1;
        List<PlaySpace> possiblePlaySpaces = new ArrayList<>();
        for (PlaySpace playSpace : playSpaces) {
            if (checkIfPlayspaceIsValid(playSpace) == "All Done") {
                possiblePlaySpaces.add(playSpace);
            }
        }
        if (possiblePlaySpaces.size() == 0) return -1;

        if (possiblePlaySpaces.size() > 1)
            possiblePlaySpaces.remove(playSpaces.get(lastGame));
        int chosen = playSpaces.indexOf(possiblePlaySpaces.get((int) (Math.random() * possiblePlaySpaces.size())));
        return chosen;
    }

    private String checkIfPlayspaceIsValid(PlaySpace playSpace) {
        return (testMode) ? "All Done" : playSpace.passesChecks();

    }

    public void startWaitingForHost() {
        status = LobbyStatus.WAITING_FOR_HOST;
    }

    public void startSequence() {
        status = LobbyStatus.STARTING;
        lobbyStartingTick = 0;
    }

    private void start() {
        if (mode == LobbyMode.AUTO) {
            pickedGameID = pickRandomGame();
            if (pickedGameID == -1) {
                status = LobbyStatus.WAITING_FOR_PLAYERS;
                lobbyStartingTick = 0;
                players.stream()
                        .forEach(player -> player.spigot()
                                .sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Messages.getMessage("lobby-filed-start-game", "&eGame starting failed due to no suitable gamemodes!", false))));
                return;
            }
        }
        status = LobbyStatus.IN_PROGRESS;
        currentPlaySpace = playSpaces.get(pickedGameID);
        currentPlaySpace.setup(players);
    }

    public void reset(List<Player> winner) {
        status = LobbyStatus.RESETTING;
        lobbyResettingTick = 0;
        pickedGameID = lastGame;

        if (winner != null) {
            for (Player player : winner) {
                RewardManager.giveRewards(getID(), player);
            }


            if (tournamentMode) {
                for (Player player : players) {
                    if (!winner.contains(player)) {
                        kickPlayer(player);
                    }
                }
            }
        }

        players.stream()
                .forEach(player -> {
                    player.spigot()
                            .sendMessage(ChatMessageType.ACTION_BAR,
                                    new TextComponent(Messages.getMessage("lobby-game-end-message", "&eThe game has been ended!", false)));
                    player.teleport(pos);
                    PlayerManager.disablePVP(player);
                    PlayerManager.enableMove(player);
                    if (waitingPlayers.contains(player)) {
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                });
        waitingPlayers.clear();

    }

    public boolean addPlayer(Player player) {
        if (isLocked) {
            player.sendMessage(Messages.messageWithPrefix("lobby-error-lobby-locked", "&cLobby is locked!"));
            return false;
        }
        if (players.size() >= maxPlayers) {
            player.sendMessage(Messages.messageWithPrefix("lobby-error-lobby-full", "&cLobby is full!"));
            return false;
        }
        if (players.contains(player)) {
            player.sendMessage(Messages.messageWithPrefix("lobby-error-already-in-lobby", "&cYou are already in the lobby!"));
            return false;
        }
        GeneralUtils.kickPlayerGlobal(player);
        player.sendMessage(Messages.messageWithPrefix("lobby-success-joined-lobby", "&aYou have joined lobby &6%id% &a!").replace("%id%", String.valueOf(ID)));
        player.teleport(pos);
        PlayerChestReward.saveInv(player);
        PlayerManager.disablePVP(player);
        players.add(player);
        if (status == LobbyStatus.IN_PROGRESS) {
            waitingPlayers.add(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(currentPlaySpace.getPos());
        }
        return true;
    }

    public boolean removePlayer(Player player) {
        if (!players.contains(player)) {
            player.sendMessage(Messages.messageWithPrefix("lobby-error-not-in-lobby", "&cYou are not in the lobby!"));
            return false;
        }
        if (currentPlaySpace != null) {
            if (currentPlaySpace.players.contains(player)) {
                currentPlaySpace.removePlayer(player);
            }
        }
        player.sendMessage(Messages.messageWithPrefix("lobby-success-left-lobby", "&aYou have left lobby &6%id% &a!").replace("%id%", String.valueOf(ID)));
        PlayerChestReward.restoreInv(player);

        Location leaveLocation = GeneralUtils.getRespawnPoint();
        if (leaveLocation != null) {
            player.teleport(GeneralUtils.getRespawnPoint());
        }

        player.removePotionEffect(PotionEffectType.SATURATION);
        player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        PlayerManager.enablePVP(player);
        PlayerManager.enableMove(player);
        PlayerManager.enableDamage(player);
        if (waitingPlayers.contains(player)) {
            waitingPlayers.remove(player);
            player.setGameMode(GameMode.SURVIVAL);
        }
        players.remove(player);
        return true;
    }

    public void addPlaySpace(PlaySpace playSpace) {
        playSpaces.add(playSpace);
    }

    public void removePlaySpace(PlaySpace playSpace) {
        playSpaces.remove(playSpace);
    }

    public boolean delete() {
        kickAll();
        if (getCurrentPlaySpace() != null)
            getCurrentPlaySpace().end(null);
        playSpaces.stream().forEach(
                playSpace -> {
                    playSpace.end(null);
                    playSpace.setLobby(null);
                });
        lobbies.remove(this);
        for (int i = PGUtils.loader.selectedLobby.size() - 1; i >= 0; i--) {
            if (PGUtils.loader.selectedLobby.get(i).lobby == this) {
                PGUtils.loader.selectedLobby.remove(i);
            }
        }
        return true;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getCurrentPlayersAmount() {
        return players.size();
    }

    public String setGame(int gameID) {
        if (gameID < 1 || gameID > playSpaces.size()) {
            return "Invalid game ID!";
        }
        String check = checkIfPlayspaceIsValid(playSpaces.get(gameID - 1));
        if (check == "All Good") {
            pickedGameID = gameID;
        }
        return check;
    }

    public Location getPos() {
        return this.pos;
    }

    public void setPos(Location pos) {
        this.pos = pos;
    }

    public int getID() {
        return ID;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getStatus() {
        return (status == LobbyStatus.STARTING ?
                Messages.getMessage("lobby-status-starting", "&6Starting", false)
                : (status == LobbyStatus.IN_PROGRESS ? Messages.getMessage("lobby-status-started", "&aStarted", false)
                : (status == LobbyStatus.WAITING_FOR_PLAYERS ? Messages.getMessage("lobby-status-waiting", "&eWaiting for Players", false)
                : (status == LobbyStatus.RESETTING ? Messages.getMessage("lobby-status-resetting", "&bResetting", false)
                : Messages.getMessage("lobby-status-waiting-for-host", "&eWaiting for Host", false)))));
    }

    public LobbyMode getMode() {
        return mode;
    }

    public void setMode(LobbyMode mode) {
        this.mode = mode;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void kickPlayer(Player player) {
        if (players.contains(player)) {
            removePlayer(player);
            player.sendMessage(Messages.messageWithPrefix("lobby-success-kicked-from-lobby", "&4You have been kicked from lobby &6%id% &4!").replace("%id%", String.valueOf(ID)));
        }
    }

    public void kickAll() {
        for (int i = players.size() - 1; i >= 0; i--) {
            kickPlayer(players.get(i));
        }
    }

    public PlaySpace getCurrentPlaySpace() {
        return currentPlaySpace;
    }

    public void setCurrentPlaySpace(PlaySpace o) {
        currentPlaySpace = o;
    }

    public List<PlaySpace> getPlaySpaces() {
        return playSpaces;
    }

    public void closeDown() {
        kickAll();
        getCurrentPlaySpace().end(null);
    }

    public Location getLocation() {
        return pos;
    }

    public void setLocation(Location pos) {
        this.pos = pos;
    }

    public String getUID() {
        return uniqueID;
    }

    public void setUID(String uid) {
        uniqueID = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLock(boolean lock) {
        isLocked = lock;
    }

    public void setTournamentMode(boolean tournamentMode) {
        this.tournamentMode = tournamentMode;
        setLock(tournamentMode);
    }

    public boolean startGame() {
        if (status == LobbyStatus.WAITING_FOR_HOST) {
            if (currentPlaySpace == null) {
                if (playSpaces.size() == 0) {
                    return false;
                }
                pickedGameID = 0;
                currentPlaySpace = playSpaces.get(pickedGameID);
            }
            if (currentPlaySpace.passesChecks() == "All Good") {
                startSequence();
                return true;
            }
        }
        return false;
    }

    public boolean isTournament() {
        return tournamentMode;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }
}
