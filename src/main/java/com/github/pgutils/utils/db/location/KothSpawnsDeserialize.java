package com.github.pgutils.utils.db.location;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.pgutils.entities.games.kothadditionals.KOTHSpawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KothSpawnsDeserialize extends JsonDeserializer<List<KOTHSpawn>> {

    @Override
    public List<KOTHSpawn> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        List<KOTHSpawn> kothSpawns = new ArrayList<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        if (node.isArray()) {
            for (JsonNode spawnNode : node) {
                String worldName = spawnNode.get("world").asText();
                double x = spawnNode.get("x").asDouble();
                double y = spawnNode.get("y").asDouble();
                double z = spawnNode.get("z").asDouble();
                float yaw = spawnNode.get("yaw").floatValue();
                float pitch = spawnNode.get("pitch").floatValue();

                int teamID = spawnNode.get("team_id").asInt();
                String uuid = spawnNode.get("uuid").asText();

                Location location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);

                KOTHSpawn kothSpawn = new KOTHSpawn();
                kothSpawn.setID(uuid);
                kothSpawn.setTeamID(teamID);
                kothSpawn.setPos(location);

                kothSpawns.add(kothSpawn);
            }
        }

        return kothSpawns;
    }
}
