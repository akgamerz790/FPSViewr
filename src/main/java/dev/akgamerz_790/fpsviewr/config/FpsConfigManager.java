package dev.akgamerz_790.fpsviewr.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

public class FpsConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("fpshud.json");

    public static FpsConfig load() {
        if (!Files.exists(PATH)) {
            FpsConfig cfg = new FpsConfig();
            save(cfg);
            return cfg;
        }
        try {
            String json = Files.readString(PATH);
            FpsConfig cfg = GSON.fromJson(json, FpsConfig.class);
            return (cfg != null) ? cfg : new FpsConfig();
        } catch (IOException e) {
            return new FpsConfig();
        }
    }

    public static void save(FpsConfig cfg) {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(cfg));
        } catch (IOException ignored) {
        }
    }
}
