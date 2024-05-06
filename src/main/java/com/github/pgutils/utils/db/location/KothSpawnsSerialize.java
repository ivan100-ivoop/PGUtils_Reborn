package com.github.pgutils.utils.db.location;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.pgutils.entities.games.kothadditionals.KOTHSpawn;

import java.io.IOException;
import java.util.List;

public class KothSpawnsSerialize extends JsonSerializer<List<KOTHSpawn>> {

    @Override
    public void serialize(List<KOTHSpawn> kothSpawns, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartArray();
        for (KOTHSpawn spawn : kothSpawns) {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField("uuid", spawn.getID());
            jsonGenerator.writeNumberField("team_id", spawn.getTeamID());
            jsonGenerator.writeStringField("world", spawn.getPos().getWorld().getName());
            jsonGenerator.writeNumberField("x", spawn.getPos().getX());
            jsonGenerator.writeNumberField("y", spawn.getPos().getY());
            jsonGenerator.writeNumberField("z", spawn.getPos().getZ());
            jsonGenerator.writeNumberField("yaw", spawn.getPos().getYaw());
            jsonGenerator.writeNumberField("pitch", spawn.getPos().getPitch());

            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }
}
