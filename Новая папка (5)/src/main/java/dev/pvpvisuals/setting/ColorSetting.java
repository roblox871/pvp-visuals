package dev.pvpvisuals.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.awt.Color;

/** Цвет хранится как ARGB. В интерфейсе меняется только оттенок (hue), насыщенность и яркость берутся из значения по умолчанию. */
public final class ColorSetting extends Setting<Integer> {
    private float hue, sat, bri;

    public ColorSetting(String name, int argb) {
        super(name, argb | 0xFF000000);
        recompute();
    }

    private void recompute() {
        float[] hsb = Color.RGBtoHSB((value >> 16) & 255, (value >> 8) & 255, value & 255, null);
        hue = hsb[0];
        sat = hsb[1];
        bri = hsb[2];
    }

    @Override
    public void set(Integer v) {
        value = v | 0xFF000000;
        recompute();
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float h) {
        hue = Math.max(0f, Math.min(1f, h));
        value = 0xFF000000 | (Color.HSBtoRGB(hue, sat, bri) & 0xFFFFFF);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public void fromJson(JsonElement element) {
        set(element.getAsInt());
    }
}
