package com.github.pgutils.utils.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pgutils.entities.games.tntradditionals.TNTRJump;
import com.github.pgutils.entities.games.tntradditionals.TNTRSpawn;
import com.github.pgutils.utils.db.location.*;
import org.bukkit.Location;
import org.github.icore.mysql.utils.IEntity;
import org.github.icore.mysql.utils.ITable;
import org.github.icore.mysql.utils.Indexed;

import java.util.List;
import java.util.UUID;

@ITable(name = "tnt")
public class TNTRun extends IEntity<UUID> {
    @Indexed
    private UUID key;

    @JsonProperty("name")
    public String name;
    @JsonSerialize(using = TNTRSpawnSerialize.class)
    @JsonDeserialize(using = TNTRSpawnDeserialize.class)
    @JsonProperty("spawns")
    public List<TNTRSpawn> spawns;

    @JsonProperty("lobby")
    public UUID lobby;
    @JsonSerialize(using = TNTRJumpSerialize.class)
    @JsonDeserialize(using = TNTRJumpDeserialize.class)
    @JsonProperty("jumps")
    public List<TNTRJump> jumps;

    @JsonSerialize(using = Serialize.class)
    @JsonDeserialize(using = Deserialize.class)
    @JsonProperty("location")
    public Location location;

    @JsonProperty("bombRatio")
    public int bombRatio;

    @JsonProperty("bombTimer")
    public int bombTimer;
}
