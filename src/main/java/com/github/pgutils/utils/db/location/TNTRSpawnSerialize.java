package com.github.pgutils.utils.db.location;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.pgutils.entities.games.tntradditionals.TNTRSpawn;
import org.bukkit.Location;

import java.io.IOException;
import java.util.List;

public class TNTRSpawnSerialize extends JsonSerializer<List<TNTRSpawn>> {

    @Override
    public void serialize(List<TNTRSpawn> tntSpawns, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartArray();
        for (TNTRSpawn spawn : tntSpawns){
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField("uuid", spawn.getID());
            jsonGenerator.writeStringField("world", spawn.getLocation().getWorld().getName());
            jsonGenerator.writeNumberField("x", spawn.getLocation().getX());
            jsonGenerator.writeNumberField("y", spawn.getLocation().getY());
            jsonGenerator.writeNumberField("z", spawn.getLocation().getZ());
            jsonGenerator.writeNumberField("yaw", (double) spawn.getLocation().getYaw());
            jsonGenerator.writeNumberField("pitch", (double) spawn.getLocation().getPitch());

            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();

    }
}
