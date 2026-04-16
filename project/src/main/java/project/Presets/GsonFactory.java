package project.Presets;

import java.lang.reflect.Type;

import org.joml.Vector3d;
import org.joml.Vector3f;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonFactory {

    public static GsonBuilder createBuilder() {
        return new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Vector3f.class, new Vector3fAdapter())
            .registerTypeAdapter(Vector3d.class, new Vector3dAdapter());
    }

    private static class Vector3fAdapter implements JsonSerializer<Vector3f>, JsonDeserializer<Vector3f> {
        @Override
        public JsonElement serialize(Vector3f src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray arr = new JsonArray();
            arr.add(src.x);
            arr.add(src.y);
            arr.add(src.z);
            return arr;
        }

        @Override
        public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonArray arr = json.getAsJsonArray();
            return new Vector3f(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat());
        }
    }

    private static class Vector3dAdapter implements JsonSerializer<Vector3d>, JsonDeserializer<Vector3d> {
        @Override
        public JsonElement serialize(Vector3d src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray arr = new JsonArray();
            arr.add(src.x);
            arr.add(src.y);
            arr.add(src.z);
            return arr;
        }

        @Override
        public Vector3d deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonArray arr = json.getAsJsonArray();
            return new Vector3d(arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), arr.get(2).getAsDouble());
        }
    }
}
