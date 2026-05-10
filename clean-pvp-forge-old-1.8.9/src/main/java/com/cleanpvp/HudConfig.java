package com.cleanpvp;

import java.util.EnumMap;
import java.util.Map;

public class HudConfig {
    public enum ColorMode {
        SOLID,
        RAINBOW
    }

    public enum FillMode {
        OUTLINE_ONLY,
        SOLID_BOX
    }

    public enum WidgetScale {
        SMALL(0.85f),
        MEDIUM(1.0f),
        LARGE(1.2f);

        private final float factor;

        WidgetScale(float factor) {
            this.factor = factor;
        }

        public float factor() {
            return factor;
        }

        public WidgetScale next() {
            switch (this) {
                case SMALL: return MEDIUM;
                case MEDIUM: return LARGE;
                case LARGE: return SMALL;
                default: return MEDIUM;
            }
        }
    }

    public static class WidgetSettings {
        public double xNorm;
        public double yNorm;
        public boolean enabled;

        public WidgetSettings() {
        }

        public WidgetSettings(double xNorm, double yNorm, boolean enabled) {
            this.xNorm = xNorm;
            this.yNorm = yNorm;
            this.enabled = enabled;
        }
    }

    public Map<HudWidget, WidgetSettings> widgets = new EnumMap<HudWidget, WidgetSettings>(HudWidget.class);
    public ColorMode colorMode = ColorMode.SOLID;
    public FillMode fillMode = FillMode.OUTLINE_ONLY;
    public WidgetScale keystrokesScale = WidgetScale.SMALL;
    public int solidColor = 0xFF00B7FF;

    public static HudConfig createDefault() {
        HudConfig config = new HudConfig();
        config.widgets.put(HudWidget.KEYSTROKES, new WidgetSettings(0.02, 0.02, true));
        config.widgets.put(HudWidget.FPS, new WidgetSettings(0.47, 0.02, true));
        config.widgets.put(HudWidget.CPS, new WidgetSettings(0.02, 0.08, false));
        config.widgets.put(HudWidget.ARMOR, new WidgetSettings(0.80, 0.02, false));
        config.widgets.put(HudWidget.POTIONS, new WidgetSettings(0.80, 0.28, false));
        return config;
    }

    public WidgetSettings getWidgetSettings(HudWidget widget) {
        WidgetSettings settings = widgets.get(widget);
        if (settings == null) {
            settings = new WidgetSettings(0.05, 0.05, true);
            widgets.put(widget, settings);
        }
        return settings;
    }
}
