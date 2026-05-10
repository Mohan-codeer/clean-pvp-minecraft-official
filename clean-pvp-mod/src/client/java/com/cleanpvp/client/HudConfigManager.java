package com.cleanpvp.client;

import com.cleanpvp.CleanPVP;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HudConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("cleanpvp-hud.json");

	private HudConfigManager() {
	}

	public static HudConfig load() {
		if (!Files.exists(CONFIG_PATH)) {
			HudConfig config = HudConfig.createDefault();
			save(config);
			return config;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
			HudConfig loaded = GSON.fromJson(reader, HudConfig.class);
			if (loaded == null) {
				return HudConfig.createDefault();
			}
			for (HudWidget widget : HudWidget.values()) {
				loaded.getWidgetSettings(widget);
			}
			return loaded;
		} catch (IOException | JsonParseException exception) {
			CleanPVP.LOGGER.warn("Failed to load HUD config, using defaults.", exception);
			return HudConfig.createDefault();
		}
	}

	public static void save(HudConfig config) {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(config, writer);
			}
		} catch (IOException exception) {
			CleanPVP.LOGGER.error("Failed to save HUD config", exception);
		}
	}
}
