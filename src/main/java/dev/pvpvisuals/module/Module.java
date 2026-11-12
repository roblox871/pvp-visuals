package dev.pvpvisuals.module;

import com.google.gson.JsonObject;
import dev.pvpvisuals.module.impl.InterfaceModule;
import dev.pvpvisuals.setting.Setting;
import dev.pvpvisuals.util.Notifications;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Module {
    protected static final Minecraft mc = Minecraft.getInstance();

    /** Становится true после первого тика клиента: до этого onEnable/onDisable не вызываем (настройки игры ещё не готовы). */
    private static boolean ready;

    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting<?>> settings = new ArrayList<>();

    protected boolean enabled;
    private int key = -1;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public static void markReady() {
        ready = true;
    }

    protected <S extends Setting<?>> S add(S setting) {
        settings.add(setting);
        return setting;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public List<Setting<?>> getSettings() {
        return Collections.unmodifiableList(settings);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        enabled = value;
        if (!ready) return;
        if (value) onEnable();
        else onDisable();
        if (!isHoldMode() && !(this instanceof InterfaceModule)) Notifications.push(name, value);
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    /** Если true, модуль включён только пока зажата привязанная клавиша. */
    public boolean isHoldMode() {
        return false;
    }

    public void onEnable() {}

    public void onDisable() {}

    public void onTick() {}

    /** Рисование поверх экрана для модулей, которые не являются перетаскиваемыми HUD-элементами. */
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {}

    public JsonObject save() {
        JsonObject o = new JsonObject();
        o.addProperty("enabled", enabled);
        o.addProperty("key", key);
        JsonObject s = new JsonObject();
        for (Setting<?> st : settings) s.add(st.getName(), st.toJson());
        o.add("settings", s);
        return o;
    }

    public void load(JsonObject o) {
        if (o.has("key")) key = o.get("key").getAsInt();
        if (o.has("settings")) {
            JsonObject s = o.getAsJsonObject("settings");
            for (Setting<?> st : settings) {
                if (!s.has(st.getName())) continue;
                try {
                    st.fromJson(s.get(st.getName()));
                } catch (RuntimeException ignored) {
                    // битое значение в конфиге: оставляем текущее
                }
            }
        }
        if (o.has("enabled")) setEnabled(o.get("enabled").getAsBoolean());
    }
}
