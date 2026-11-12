package dev.pvpvisuals.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

public final class SliderSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;

    public SliderSetting(String name, double defaultValue, double min, double max, double step) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        set(defaultValue);
    }

    @Override
    public void set(Double v) {
        double c = Math.max(min, Math.min(max, v));
        if (step > 0) c = Math.round(c / step) * step;
        this.value = Math.max(min, Math.min(max, c));
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getInt() {
        return (int) Math.round(value);
    }

    public float getFloat() {
        return value.floatValue();
    }

    public double ratio() {
        return (value - min) / (max - min);
    }

    public String display() {
        if (step >= 1) return String.valueOf(getInt());
        return String.format(Locale.ROOT, step < 0.1 ? "%.2f" : "%.1f", value);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromJson(JsonElement element) {
        set(element.getAsDouble());
    }
}
