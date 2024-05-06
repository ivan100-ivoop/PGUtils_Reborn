package com.github.pgutils.utils.db.location;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.pgutils.entities.games.kothadditionals.KOTHPoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KothPointsDeserialize extends JsonDeserializer<List<KOTHPoint>> {

    @Override
    public List<KOTHPoint> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        List<KOTHPoint> kothPoints = new ArrayList<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        if (node.isArray()) {
            for (JsonNode pointNode : node) {
                String worldName = pointNode.get("position").get("world").asText();
                double x = pointNode.get("position").get("x").asDouble();
                double y = pointNode.get("position").get("y").asDouble();
                double z = pointNode.get("position").get("z").asDouble();
                float yaw = pointNode.get("position").get("yaw").floatValue();
                float pitch = pointNode.get("position").get("pitch").floatValue();

                Location location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);

                double radius = pointNode.get("radius").asDouble();
                int captureTime = pointNode.get("captureTime").asInt();
                int pointsAwarding = pointNode.get("pointsAwarding").asInt();
                String uuid = pointNode.get("uuid").asText();

                KOTHPoint kothPoint = new KOTHPoint();
                kothPoint.setID(uuid);
                kothPoint.setRadius(radius);
                kothPoint.setCaptureTime(captureTime);
                kothPoint.setPointsAwarding(pointsAwarding);
                kothPoint.setLocation(location);

                kothPoints.add(kothPoint);
            }
        }

        return kothPoints;
    }
}
