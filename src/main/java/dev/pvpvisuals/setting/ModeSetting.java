package dev.pvpvisuals.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public final class ModeSetting extends Setting<String> {
    private final String[] modes;

    public ModeSetting(String name, String defaultMode, String... modes) {
        super(name, defaultMode);
        this.modes = modes;
    }

    public boolean is(String mode) {
        return value.equalsIgnoreCase(mode);
    }

    public int index() {
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equalsIgnoreCase(value)) return i;
        }
        return 0;
    }

    public void cycle(int direction) {
        int i = Math.floorMod(index() + direction, modes.length);
        value = modes[i];
    }

    @Override
    public void set(String v) {
        for (String m : modes) {
            if (m.equalsIgnoreCase(v)) {
                value = m;
                return;
            }
        }
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromJson(JsonElement element) {
        set(element.getAsString());
    }
}
