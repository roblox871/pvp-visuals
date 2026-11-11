package dev.pvpvisuals.module;

import com.google.gson.JsonObject;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Модуль с элементом на экране, который можно перетаскивать в HUD Editor. */
public abstract class HudModule extends Module {
    protected int x, y;
    protected int width = 60, height = 20;
    private boolean positioned;

    /** Передайте отрицательные координаты, чтобы выставить позицию в initPosition по размеру экрана. */
    protected HudModule(String name, String description, int x, int y) {
        super(name, description, Category.HUD);
        this.x = x;
        this.y = y;
        this.positioned = x >= 0 && y >= 0;
    }

    public abstract void render(GuiGraphics g, DeltaTracker delta);

    protected void initPosition(int screenW, int screenH) {}

    public final void ensurePositioned(int screenW, int screenH) {
        if (positioned) return;
        initPosition(screenW, screenH);
        positioned = true;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setPosition(int nx, int ny, int screenW, int screenH) {
        x = Math.max(0, Math.min(screenW - width, nx));
        y = Math.max(0, Math.min(screenH - height, ny));
        positioned = true;
    }

    @Override
    public JsonObject save() {
        JsonObject o = super.save();
        o.addProperty("x", x);
        o.addProperty("y", y);
        return o;
    }

    @Override
    public void load(JsonObject o) {
        super.load(o);
        if (o.has("x") && o.has("y")) {
            x = o.get("x").getAsInt();
            y = o.get("y").getAsInt();
            positioned = true;
        }
    }
}
