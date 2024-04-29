package com.github.pgutils.entities;

import com.github.pgutils.PGUtils;
import com.github.pgutils.customitems.CustomEffect;
import com.github.pgutils.enums.GameStatus;
import com.github.pgutils.utils.GameScoreboardManager;
import com.github.pgutils.utils.GeneralUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.function.BiFunction;

public abstract class PlaySpace {

    private int ID;

    // Save
    private String UID;

    public static List<PlaySpace> playSpaces = new ArrayList<>();

    public static Map<String, Class<? extends PlaySpace>> playSpaceTypes = new HashMap<>();

    public Map<String, BiFunction<Player, String[], Boolean>> setMap = new HashMap<>();

    // Save
    private Location pos;

    protected List<Player> players = new ArrayList<>();

    // Save
    private Lobby currentLobby = null;

    protected GameStatus status = GameStatus.INACTIVE;

    private GameScoreboardManager scoreboardManager;

    public static List<String> addGameObjects;

    public static List<String> setGameObjects;


    protected int tick = 0;

    protected String type = "Typeless";

    // Save
    private String name;

    public PlaySpace() {
        playSpaces.add(this);
        ID = playSpaces.size();
        UID = UUID.randomUUID().toString();
        name = "PlaySpace-" + ID;
        scoreboardManager = new GameScoreboardManager();

    }

    public void setCurrentLobby(Lobby lobby) {
        currentLobby = lobby;
    }

    public Lobby getCurrentLobby() {
        return currentLobby;
    }

    public void setup(List<Player> players) {
        this.players.addAll(players);
        status = GameStatus.STARTING;
        start();
    }

    abstract public void start();

    public void update() {
        tick++;
        onUpdate();
    }

    abstract public void onUpdate();

    abstract public void endProcedure();

    public boolean delete() {
        deletePlaySpace();
        playSpaces.remove(this);
        if (getLobby() != null) {
            end(null);
            getLobby().removePlaySpace(this);
            setLobby(null);
        }
        for (int i = PGUtils.loader.selectedPlaySpace.size() - 1; i >= 0; i--) {
            if (PGUtils.loader.selectedPlaySpace.get(i).playSpace == this) {
                PGUtils.loader.selectedPlaySpace.remove(i);
            }
        }
        return true;
    }

    public abstract void deletePlaySpace();

    public void end(List<Player> players) {
        endProcedure();
        reset();
        if (currentLobby != null) {
            currentLobby.reset(players);
        }
    }


    public void reset() {
        status = GameStatus.INACTIVE;
        tick = 0;
        if (getSbManager() != null && getSbManager().hasGame(getID())) {
            getSbManager().removeGameScore(getID());
        }
        for (int i = players.size() - 1; i >= 0; i--) {
            removePlayer(players.get(i));
        }
    }

    public void setPos(Location pos) {
        this.pos = pos;
        savePos();
    }

    public void setName(String name) {
        this.name = name;
        saveName();
    }

    protected abstract void saveName();

    protected abstract void savePos();


    public Location getPos() {
        return pos;
    }

    public int getID() {
        return ID;
    }

    public String getType() {
        return type;
    }

    public void setLobby(Lobby lobby) {
        currentLobby = lobby;
    }

    public Lobby getLobby() {
        return currentLobby;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void removePlayer(Player player) {
        CustomEffect.removeAllEffects(player);
        player.setGameMode(GameMode.SURVIVAL);
        players.remove(player);
        onRemovePlayer(player);
    }

    public abstract void onRemovePlayer(Player player);

    public abstract String passesChecks();

    public void updateView(Player player) {
        player.spawnParticle(Particle.END_ROD, getLocation(), 1, 0.3, 1, 0.3, 0.01);
        updateViewGame(player);

    }
    public abstract void updateViewGame(Player player);

    public GameStatus getStatus() {
        return status;
    }

    public Location getLocation() {
        return pos;
    }

    public void setLocation(Location location) {
        this.pos = location;
    }

    public void setUID(String readObject) {
        UID = readObject;
    }

    public String getUID() {
        return UID;
    }

    public abstract boolean addGameObjects(Player player, String[] args);

    public abstract boolean removeGameObjects(Player player, String[] args);

    public abstract boolean setGameObjects(Player player, String[] args);

    public abstract boolean setGameOptions(Player player, String[] args);

    public Scoreboard getScoreboard() {
        return scoreboardManager.getScoreboard(getID());
    }

    public GameScoreboardManager getSbManager() {
        return scoreboardManager;
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    // return the commandmap
    public Map<String, BiFunction<Player, String[], Boolean>> getSetMap() {
        return setMap;
    }

    public abstract void savePlaySpace();
}
