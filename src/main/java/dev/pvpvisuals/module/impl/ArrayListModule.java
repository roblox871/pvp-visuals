package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.PvpVisualsClient;
import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.util.Anim;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Список включённых модулей с плавным выездом справа и градиентом между двумя цветами интерфейса. */
public final class ArrayListModule extends HudModule {
    private static final int ROW = 12;

    private final BooleanSetting showHud = add(new BooleanSetting("HUD-модули", false));
    private final Map<Module, Float> progress = new HashMap<>();

    public ArrayListModule() {
        super("ArrayList", "Список включённых модулей", -1, -1);
        width = 110;
        height = ROW;
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw - width - 6;
        y = 6;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        List<Module> shown = new ArrayList<>();
        for (Module m : PvpVisualsClient.modules().all()) {
            if (m == this || m instanceof InterfaceModule) continue;
            if (!showHud.isOn() && m instanceof HudModule) continue;
            float p = progress.getOrDefault(m, 0f);
            p = Anim.approach(p, m.isEnabled() ? 1f : 0f, 14f);
            if (p < 0.02f && !m.isEnabled()) {
                progress.remove(m);
                continue;
            }
            progress.put(m, p);
            shown.add(m);
        }
        shown.sort(Comparator.comparingInt((Module m) -> RenderUtils.textWidth(m.getName())).reversed());

        height = Math.max(ROW, shown.size() * ROW);
        int right = x + width;
        int bg = InterfaceModule.background();
        int bgAlpha = bg >>> 24;
        int n = shown.size();

        for (int i = 0; i < n; i++) {
            Module m = shown.get(i);
            float p = progress.get(m);
            int tw = RenderUtils.textWidth(m.getName());
            int rowW = tw + 10;
            int rx = right - rowW + (int) ((1f - p) * (rowW + 6));
            int ry = y + i * ROW;
            int alpha = Math.max(8, (int) (255 * p));
            int color = RenderUtils.mix(InterfaceModule.accent(), InterfaceModule.accent2(), n <= 1 ? 0f : i / (float) (n - 1));

            RenderUtils.rect(g, rx, ry, rowW, ROW, RenderUtils.withAlpha(bg, Math.max(8, (int) (bgAlpha * p))));
            RenderUtils.rect(g, rx + rowW - 2, ry, 2, ROW, RenderUtils.withAlpha(color, alpha));
            RenderUtils.text(g, m.getName(), rx + 4, ry + 2, RenderUtils.withAlpha(color, alpha));
        }
    }
}
