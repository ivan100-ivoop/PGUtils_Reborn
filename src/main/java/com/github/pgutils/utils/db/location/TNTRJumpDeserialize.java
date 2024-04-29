package com.github.pgutils.utils.db.location;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.pgutils.entities.games.tntradditionals.TNTRJump;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TNTRJumpDeserialize extends JsonDeserializer<List<TNTRJump>> {
    @Override
    public List<TNTRJump> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        List<TNTRJump> jumps = new ArrayList<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        if (node.isArray()) {
            for (JsonNode spawnNode : node) {

                String worldName = spawnNode.get("world").asText();
                String key = spawnNode.get("uuid").asText();

                double x = spawnNode.get("x").asDouble();
                double y = spawnNode.get("y").asDouble();
                double z = spawnNode.get("z").asDouble();

                double radius = spawnNode.get("radius").asDouble();
                int strength = spawnNode.get("strength").asInt();
                int cooldown = spawnNode.get("cooldown").asInt();


                float yaw = (float) spawnNode.get("yaw").asDouble();
                float pitch = (float) spawnNode.get("pitch").asDouble();

                Location loc = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);

                TNTRJump jump = new TNTRJump(loc, radius, strength);
                jump.setID(key);
                jump.setCooldown(cooldown);

                jumps.add(jump);

            }
        }

        return jumps;
    }
}