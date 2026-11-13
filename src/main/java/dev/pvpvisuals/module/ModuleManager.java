package dev.pvpvisuals.module;

import dev.pvpvisuals.module.impl.ArmorHudModule;
import dev.pvpvisuals.module.impl.HitMarkerModule;
import dev.pvpvisuals.module.impl.AttackBarModule;
import dev.pvpvisuals.module.impl.RainbowBarModule;
import dev.pvpvisuals.module.impl.ScreenFrameModule;
import dev.pvpvisuals.module.impl.FovChangerModule;
import dev.pvpvisuals.module.impl.SensitivityModule;
import dev.pvpvisuals.module.impl.RawInputModule;
import dev.pvpvisuals.module.impl.NoAutoJumpModule;
import dev.pvpvisuals.module.impl.NoPauseModule;
import dev.pvpvisuals.module.impl.HideHudModule;
import dev.pvpvisuals.module.impl.PingAlertModule;
import dev.pvpvisuals.module.impl.WelcomeModule;
import dev.pvpvisuals.module.impl.SimpleHuds2;
import dev.pvpvisuals.module.impl.TimeChangerModule;
import dev.pvpvisuals.module.impl.SkyColorModule;
import dev.pvpvisuals.module.impl.FreeLookModule;
import dev.pvpvisuals.module.impl.TotemPopsModule;
import dev.pvpvisuals.module.impl.NoFireModule;
import dev.pvpvisuals.module.impl.ScreenTintModule;
import dev.pvpvisuals.module.impl.HurtFlashModule;
import dev.pvpvisuals.module.impl.CinematicBarsModule;
import dev.pvpvisuals.module.impl.ColoredEdgesModule;
import dev.pvpvisuals.module.impl.HealthBarModule;
import dev.pvpvisuals.module.impl.CompassModule;
import dev.pvpvisuals.module.impl.CooldownsModule;
import dev.pvpvisuals.module.impl.FpsGraphModule;
import dev.pvpvisuals.module.impl.SimpleHuds;
import dev.pvpvisuals.module.impl.CoordinatesModule;
import dev.pvpvisuals.module.impl.LowHealthVignetteModule;
import dev.pvpvisuals.module.impl.ViewModelModule;
import dev.pvpvisuals.module.impl.ShieldIndicatorModule;
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
        register(new NoFireModule());
        register(new TimeChangerModule());
        register(new SkyColorModule());
        register(new FreeLookModule());
        register(new TotemPopsModule());
        register(new ScreenTintModule());
        register(new HurtFlashModule());
        register(new CinematicBarsModule());
        register(new ColoredEdgesModule());
        register(new LowHealthVignetteModule());
        register(new ViewModelModule());
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
        register(new ShieldIndicatorModule());
        register(new CoordinatesModule());
        register(new HealthBarModule());
        register(new CompassModule());
        register(new CooldownsModule());
        register(new FpsGraphModule());
        for (Module m : SimpleHuds.create()) register(m);
        register(new HitMarkerModule());
        register(new AttackBarModule());
        register(new RainbowBarModule());
        register(new ScreenFrameModule());
        register(new FovChangerModule());
        register(new SensitivityModule());
        register(new RawInputModule());
        register(new NoAutoJumpModule());
        register(new NoPauseModule());
        register(new HideHudModule());
        register(new PingAlertModule());
        register(new WelcomeModule());
        for (Module m : SimpleHuds2.create()) register(m);
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


    /** Модули с собственным удачным местом на экране: авто-расстановка их не трогает. */
    private static final java.util.Set<String> FIXED = java.util.Set.of(
            "Watermark", "ArrayList", "Keystrokes", "ArmorHUD", "Held Item", "TargetHUD", "Combo", "Direction",
            "Movement Status", "Shield Indicator", "Health Bar", "Cooldowns", "FPS Graph");

    /** Раскладывает включённые плашки колонками слева без наложений. */
    public void autoLayout(int sw, int sh) {
        int x = 6, y = 30, colW = 0;
        for (HudModule h : hudModules()) {
            if (!h.isEnabled() || FIXED.contains(h.getName())) continue;
            if (y + h.getHeight() > sh - 40 && y > 30) {
                x += colW + 6;
                y = 30;
                colW = 0;
            }
            h.setPosition(x, y, sw, sh);
            y += h.getHeight() + 6;
            colW = Math.max(colW, h.getWidth());
        }
    }

    /** Включает только перечисленные модули (по внутренним именам), остальные выключает. Интерфейс не трогает. */
    public void applyPreset(java.util.Collection<String> names) {
        for (Module m : modules) {
            if (m instanceof dev.pvpvisuals.module.impl.InterfaceModule) continue;
            m.setEnabled(names.contains(m.getName()));
        }
    }

    public static final java.util.List<String> PRESET_PVP = java.util.List.of(
            "Watermark", "Info", "Keystrokes", "ArmorHUD", "Potions HUD", "TargetHUD", "Combo", "Shield Indicator",
            "Alerts", "Hit Marker", "Totem Pops", "FullBright", "Welcome");
    public static final java.util.List<String> PRESET_MINIMAL = java.util.List.of("Watermark", "Info", "Alerts", "Welcome");

    /** Вызывается при закрытии игры: возвращает изменённые настройки клиента. */
    public void shutdown() {
        for (Module m : modules) {
            if (m.isEnabled()) m.onDisable();
        }
    }
}
