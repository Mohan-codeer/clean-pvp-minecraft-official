package com.cleanpvp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class HudConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(Minecraft.getMinecraft().mcDataDir, "config/cleanpvp-hud.json");

    private HudConfigManager() {
    }

    public static HudConfig load() {
        if (!CONFIG_FILE.exists()) {
            HudConfig config = HudConfig.createDefault();
            save(config);
            return config;
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
            HudConfig loaded = GSON.fromJson(reader, HudConfig.class);
            if (loaded == null) {
                return HudConfig.createDefault();
            }
            for (HudWidget widget : HudWidget.values()) {
                loaded.getWidgetSettings(widget);
            }
            return loaded;
        } catch (IOException | JsonParseException exception) {
            System.err.println("[CleanPVP] Failed to load HUD config, using defaults.");
            exception.printStackTrace();
            return HudConfig.createDefault();
        }
    }

    public static void save(HudConfig config) {
        try {
            File parent = CONFIG_FILE.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException exception) {
            System.err.println("[CleanPVP] Failed to save HUD config");
            exception.printStackTrace();
        }
    }
}
