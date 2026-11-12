package dev.pvpvisuals.module;

import dev.pvpvisuals.module.impl.ArmorHudModule;
import dev.pvpvisuals.module.impl.AspectRatioModule;
import dev.pvpvisuals.module.impl.AlertsModule;
import dev.pvpvisuals.module.impl.AutoRespawnModule;
import dev.pvpvisuals.module.impl.CrosshairModule;
import dev.pvpvisuals.module.impl.DamageIndicatorsModule;
import dev.pvpvisuals.module.impl.DirectionModule;
import dev.pvpvisuals.module.impl.FogColorModule;
import dev.pvpvisuals.module.impl.FullBrightModule;
import dev.pvpvisuals.module.impl.HeldItemModule;
import dev.pvpvisuals.module.impl.HitParticlesModule;
import dev.pvpvisuals.module.impl.HitSoundModule;
import dev.pvpvisuals.module.impl.ItemCounterModule;
import dev.pvpvisuals.module.impl.MemoryHudModule;
import dev.pvpvisuals.module.impl.MovementStatusModule;
import dev.pvpvisuals.module.impl.NoRenderModule;
import dev.pvpvisuals.module.impl.ServerInfoModule;
import dev.pvpvisuals.module.impl.SessionModule;
import dev.pvpvisuals.module.impl.SystemOptimizerModule;
import dev.pvpvisuals.module.impl.ToggleKeysModule;
import dev.pvpvisuals.util.HitTracker;
import dev.pvpvisuals.module.impl.ArrayListModule;
import dev.pvpvisuals.module.impl.AutoSprintModule;
import dev.pvpvisuals.module.impl.ComboModule;
import dev.pvpvisuals.module.impl.FpsBoostModule;
import dev.pvpvisuals.module.impl.InfoHudModule;
import dev.pvpvisuals.module.impl.InterfaceModule;
import dev.pvpvisuals.module.impl.KeystrokesModule;
import dev.pvpvisuals.module.impl.NameTagsModule;
import dev.pvpvisuals.module.impl.PotionsHudModule;
import dev.pvpvisuals.module.impl.SmoothCameraModule;
import dev.pvpvisuals.module.impl.TargetHudModule;
import dev.pvpvisuals.module.impl.WatermarkModule;
import dev.pvpvisuals.module.impl.WeatherModule;
import dev.pvpvisuals.module.impl.ZoomModule;
import dev.pvpvisuals.util.Anim;
import dev.pvpvisuals.util.CpsCounter;
import dev.pvpvisuals.util.Notifications;
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
        register(new SmoothCameraModule());
        register(new WeatherModule());
        register(new NameTagsModule());
        register(new AspectRatioModule());
        register(new FogColorModule());
        register(new FullBrightModule());
        register(new NoRenderModule());
        register(new CrosshairModule());
        register(new HitParticlesModule());
        register(new DamageIndicatorsModule());
        // HUD
        register(new WatermarkModule());
        register(new ArrayListModule());
        register(new InfoHudModule());
        register(new TargetHudModule());
        register(new ComboModule());
        register(new PotionsHudModule());
        register(new KeystrokesModule());
        register(new ArmorHudModule());
        register(new HeldItemModule());
        register(new MovementStatusModule());
        register(new ItemCounterModule());
        register(new AlertsModule());
        register(new DirectionModule());
        register(new SessionModule());
        register(new ServerInfoModule());
        register(new MemoryHudModule());
        // Player
        register(new AutoSprintModule());
        register(new ToggleKeysModule());
        register(new AutoRespawnModule());
        register(new HitSoundModule());
        // Misc
        register(new FpsBoostModule());
        register(new SystemOptimizerModule());
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
        HitTracker.tick(mc);
        for (Module m : modules) if (m.isEnabled()) m.onTick();
    }

    public void renderHud(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        Anim.frame();
        int sw = g.guiWidth(), sh = g.guiHeight();
        for (Module m : modules) {
            if (!m.isEnabled()) continue;
            if (m instanceof HudModule h) {
                h.ensurePositioned(sw, sh);
                h.render(g, delta);
                h.setPosition(h.getX(), h.getY(), sw, sh);
            } else {
                m.renderOverlay(g, delta);
            }
        }
        Notifications.render(g);
    }

    /** Вызывается при закрытии игры: возвращает изменённые настройки клиента. */
    public void shutdown() {
        for (Module m : modules) {
            if (m.isEnabled()) m.onDisable();
        }
    }
}
