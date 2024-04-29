package com.github.pgutils.utils.db.location;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.pgutils.entities.games.tntradditionals.TNTRJump;
import org.bukkit.Location;

import java.io.IOException;
import java.util.List;

public class TNTRJumpSerialize extends JsonSerializer<List<TNTRJump>> {

    @Override
    public void serialize(List<TNTRJump> tntJump, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartArray();
        for (TNTRJump jump : tntJump){
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField("uuid", jump.getID());
            jsonGenerator.writeNumberField("radius", jump.getRadius());
            jsonGenerator.writeNumberField("strength", jump.getStrength());
            jsonGenerator.writeNumberField("cooldown", jump.getCooldown());
            jsonGenerator.writeStringField("world", jump.getPos().getWorld().getName());
            jsonGenerator.writeNumberField("x", jump.getPos().getX());
            jsonGenerator.writeNumberField("y", jump.getPos().getY());
            jsonGenerator.writeNumberField("z", jump.getPos().getZ());
            jsonGenerator.writeNumberField("yaw", (double) jump.getPos().getYaw());
            jsonGenerator.writeNumberField("pitch", (double) jump.getPos().getPitch());

            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();

    }
}
