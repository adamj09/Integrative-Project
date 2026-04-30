package oms.Presets;

import java.lang.reflect.Type;

import org.joml.Vector3d;
import org.joml.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Class used to create JSON files using Google's API.
 * 
 * @author Ryan Lau
 */
public class GsonFactory {

    /**
     * Creates a new Gson object using the Vector3f and Vector3d adapters.
     * 
     * @return the new Gson object.
     */
    public static Gson create() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Vector3f.class, new Vector3fAdapter())
                .registerTypeAdapter(Vector3d.class, new Vector3dAdapter())
                .create();
    }

    /**
     * Json serializer for Vector3f types.
     */
    private static class Vector3fAdapter implements JsonSerializer<Vector3f>, JsonDeserializer<Vector3f> {
        @Override
        public JsonElement serialize(Vector3f src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray array = new JsonArray();
            array.add(src.x);
            array.add(src.y);
            array.add(src.z);
            return array;
        }

        @Override
        public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
        }
    }

    /**
     * Json serializer for Vector3d types.
     */
    private static class Vector3dAdapter implements JsonSerializer<Vector3d>, JsonDeserializer<Vector3d> {
        @Override
        public JsonElement serialize(Vector3d src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray array = new JsonArray();
            array.add(src.x);
            array.add(src.y);
            array.add(src.z);
            return array;
        }

        @Override
        public Vector3d deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            return new Vector3d(array.get(0).getAsDouble(), array.get(1).getAsDouble(), array.get(2).getAsDouble());
        }
    }
}
