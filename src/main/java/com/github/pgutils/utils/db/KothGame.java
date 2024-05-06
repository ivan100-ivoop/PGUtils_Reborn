package com.github.pgutils.utils.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pgutils.entities.games.kothadditionals.KOTHPoint;
import com.github.pgutils.entities.games.kothadditionals.KOTHSpawn;
import com.github.pgutils.utils.db.location.*;
import org.bukkit.Location;
import org.github.icore.mysql.utils.IEntity;
import org.github.icore.mysql.utils.ITable;
import org.github.icore.mysql.utils.Indexed;

import java.util.List;
import java.util.UUID;

@ITable(name = "koth")
public class KothGame extends IEntity<UUID> {

    @Indexed
    private UUID key;

    @JsonProperty("lobby")
    private UUID lobby;

    @JsonProperty("name")
    private String name;

    @JsonProperty("teamsAmount")
    private int teamsAmount;

    @JsonProperty("matchTime")
    private int matchTime;

    @JsonProperty("initial_points_active")
    private int initialPointsActive;

    @JsonSerialize(using = KothPointsSerialize.class)
    @JsonDeserialize(using = KothPointsDeserialize.class)
    @JsonProperty("points")
    private List<KOTHPoint> points;

    @JsonSerialize(using = KothSpawnsSerialize.class)
    @JsonDeserialize(using = KothSpawnsDeserialize.class)
    @JsonProperty("spawns")
    private List<KOTHSpawn> spawns;

    @JsonSerialize(using = Serialize.class)
    @JsonDeserialize(using = Deserialize.class)
    @JsonProperty("location")
    private Location location;

    public KothGame setName(String name) { this.name = name; return this; }
    public KothGame setTeamsAmount(int teamsAmount) { this.teamsAmount = teamsAmount; return this; }
    public KothGame setMatchTime(int matchTime) { this.matchTime = matchTime; return this; }
    public KothGame setInitialPointsActive(int initialPointsActive) { this.initialPointsActive = initialPointsActive; return this; }
    public KothGame setPoints(List<KOTHPoint> points) { this.points = points; return this; }
    public KothGame setLobby(UUID lobby) { this.lobby = lobby; return this; }
    public KothGame setSpawns(List<KOTHSpawn> spawns) { this.spawns = spawns; return this; }
    public KothGame setLocation(Location location) { this.location = location; return this; }

    public String getName() { return this.name; }
    public int getTeamsAmount() { return this.teamsAmount; }
    public int getMatchTime() { return this.matchTime; }
    public int getInitialPointsActive() { return this.initialPointsActive; }
    public List<KOTHPoint> getPoints() { return this.points; }
    public List<KOTHSpawn> getSpawns() { return this.spawns; }
    public UUID getLobby() { return this.lobby; }
    public Location getLocation() { return this.location; }

    public KothGame() {}
}
