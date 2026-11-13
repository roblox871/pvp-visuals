package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.Notifications;
import net.minecraft.client.multiplayer.PlayerInfo;

/** Предупреждение, когда пинг выше заданного значения. */
public final class PingAlertModule extends Module {
    private final SliderSetting limit = add(new SliderSetting("Порог, мс", 200, 50, 500, 10));
    private long lastWarn;

    public PingAlertModule() {
        super("Ping Alert", "Уведомление при высоком пинге", Category.MISC);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.getConnection() == null) return;
        PlayerInfo pi = mc.getConnection().getPlayerInfo(mc.player.getUUID());
        if (pi == null || pi.getLatency() < limit.getInt()) return;
        long now = System.currentTimeMillis();
        if (now - lastWarn < 15000) return;
        lastWarn = now;
        Notifications.warn("Высокий пинг: " + pi.getLatency() + " мс");
    }
}
