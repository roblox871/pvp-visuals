package dev.pvpvisuals.module;

import dev.pvpvisuals.module.impl.ArmorHudModule;
import dev.pvpvisuals.module.impl.FpsBoostModule;
import dev.pvpvisuals.module.impl.InfoHudModule;
import dev.pvpvisuals.module.impl.InterfaceModule;
import dev.pvpvisuals.module.impl.KeystrokesModule;
import dev.pvpvisuals.module.impl.PotionsHudModule;
import dev.pvpvisuals.module.impl.TargetHudModule;
import dev.pvpvisuals.module.impl.WatermarkModule;
import dev.pvpvisuals.module.impl.ZoomModule;
import dev.pvpvisuals.util.CpsCounter;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private final List<Module> modules = new ArrayList<>();
    private boolean started;

    public ModuleManager() {
        // Visuals
        register(new InterfaceModule());
        register(new ZoomModule());
        // HUD
        register(new WatermarkModule());
        register(new InfoHudModule());
        register(new TargetHudModule());
        register(new PotionsHudModule());
        register(new KeystrokesModule());
        register(new ArmorHudModule());
        // Misc
        register(new FpsBoostModule());
    }

    private void register(Module m) {
        modules.add(m);
    }

    public List<Module> all() {
        return Collections.unmodifiableList(modules);
    }

    public List<Module> byCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : modules) if (m.getCategory() == c) out.add(m);
        return out;
    }

    public List<HudModule> hudModules() {
        List<HudModule> out = new ArrayList<>();
        for (Module m : modules) if (m instanceof HudModule h) out.add(h);
        return out;
    }

    public <T extends Module> T get(Class<T> type) {
        for (Module m : modules) if (type.isInstance(m)) return type.cast(m);
        throw new IllegalArgumentException("Модуль не зарегистрирован: " + type.getSimpleName());
    }

    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (!started) {
            started = true;
            Module.markReady();
            // Модули, включённые конфигом, получают onEnable уже при готовом клиенте
            for (Module m : modules) if (m.isEnabled()) m.onEnable();
        }
        CpsCounter.tick(mc);
        for (Module m : modules) if (m.isEnabled()) m.onTick();
    }

    public void renderHud(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        int sw = g.guiWidth(), sh = g.guiHeight();
        for (Module m : modules) {
            if (m instanceof HudModule h && h.isEnabled()) {
                h.ensurePositioned(sw, sh);
                h.render(g, delta);
                h.setPosition(h.getX(), h.getY(), sw, sh);
            }
        }
    }

    /** Вызывается при закрытии игры: возвращает изменённые настройки клиента. */
    public void shutdown() {
        for (Module m : modules) {
            if (m.isEnabled()) m.onDisable();
        }
    }
}
