package com.github.pgutils.utils.db.location;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.pgutils.entities.games.kothadditionals.KOTHPoint;

import java.io.IOException;
import java.util.List;

public class KothPointsSerialize extends JsonSerializer<List<KOTHPoint>> {

    @Override
    public void serialize(List<KOTHPoint> kothPoints, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartArray();
        for (KOTHPoint point : kothPoints){
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField("uuid", point.getID());
            jsonGenerator.writeNumberField("radius", point.getRadius());
            jsonGenerator.writeNumberField("captureTime", point.getCaptureTime());
            jsonGenerator.writeNumberField("pointsAwarding", point.getPointsAwarding());
            jsonGenerator.writeObjectFieldStart("position");
            jsonGenerator.writeStringField("world", point.getPosition().getWorld().getName());
            jsonGenerator.writeNumberField("x", point.getPosition().getX());
            jsonGenerator.writeNumberField("y", point.getPosition().getY());
            jsonGenerator.writeNumberField("z", point.getPosition().getZ());
            jsonGenerator.writeNumberField("yaw", point.getPosition().getYaw());
            jsonGenerator.writeNumberField("pitch", point.getPosition().getPitch());
            jsonGenerator.writeEndObject();

            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }
}
