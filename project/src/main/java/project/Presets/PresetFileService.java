package project.Presets;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;

/**
 * @author Ryan Lau
 */
public class PresetFileService {

    private final Gson gson = GsonFactory.create();

    public void save(Path filePath, WorldConfiguration configuration) throws IOException {
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(configuration, writer);
        }
    }

    public WorldConfiguration load(Path filePath) throws IOException {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            WorldConfiguration config = gson.fromJson(reader, WorldConfiguration.class);
            if (config == null) {
                throw new IOException("Empty or invalid preset file.");
            }
            return config;
        }
    }
}
