package dev.pvpvisuals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/** Конфиги лежат в config/pvpvisuals/<имя>.json. Основной конфиг называется default. */
public final class ConfigManager {
    private ConfigManager() {}

    public static final String DEFAULT = "default";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path dir() {
        return FabricLoader.getInstance().getConfigDir().resolve("pvpvisuals");
    }

    private static Path file(String name) {
        String safe = name.replaceAll("[^a-zA-Z0-9_-]", "");
        if (safe.isEmpty()) safe = DEFAULT;
        return dir().resolve(safe + ".json");
    }

    public static boolean save(ModuleManager modules) {
        return save(modules, DEFAULT);
    }

    public static boolean save(ModuleManager modules, String name) {
        JsonObject root = new JsonObject();
        for (Module m : modules.all()) root.add(m.getName(), m.save());
        try {
            Files.createDirectories(dir());
            Files.writeString(file(name), GSON.toJson(root), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean load(ModuleManager modules) {
        return load(modules, DEFAULT);
    }

    public static boolean load(ModuleManager modules, String name) {
        Path f = file(name);
        if (!Files.exists(f)) return false;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(f, StandardCharsets.UTF_8)).getAsJsonObject();
            for (Module m : modules.all()) {
                if (root.has(m.getName()) && root.get(m.getName()).isJsonObject()) {
                    m.load(root.getAsJsonObject(m.getName()));
                }
            }
            return true;
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    public static List<String> list() {
        List<String> names = new ArrayList<>();
        Path d = dir();
        if (!Files.isDirectory(d)) return names;
        try (Stream<Path> s = Files.list(d)) {
            s.map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith(".json"))
                    .map(n -> n.substring(0, n.length() - 5))
                    .sorted()
                    .forEach(names::add);
        } catch (IOException ignored) {
        }
        return names;
    }
}
