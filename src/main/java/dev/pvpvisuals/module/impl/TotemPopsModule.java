package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.LineHud;
import dev.pvpvisuals.util.Notifications;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Считает, сколько тотемов потерял каждый игрок, и показывает счёт цели в прицеле и ваш. */
public final class TotemPopsModule extends LineHud {
    private static final Map<UUID, Integer> POPS = new HashMap<>();
    private static TotemPopsModule instance;
    private static Minecraft mcRef;

    public TotemPopsModule() {
        super("Totem Pops", "Сколько тотемов потеряли вы и игрок в прицеле", 33, "Попы тотемов", TotemPopsModule::text, null);
        instance = this;
    }

    public static boolean enabledNow() {
        return instance != null && instance.isEnabled();
    }

    private static Minecraft mc() {
        if (mcRef == null) mcRef = Minecraft.getInstance();
        return mcRef;
    }

    public static void pop(Entity e) {
        if (!(e instanceof Player p)) return;
        int n = POPS.merge(p.getUUID(), 1, Integer::sum);
        if (p == mc().player) Notifications.info("Ваш тотем сработал (" + n + ")");
        else Notifications.info(p.getName().getString() + " потерял тотем (" + n + ")");
    }

    private static String text() {
        Minecraft m = mc();
        if (m.player == null) return null;
        int own = POPS.getOrDefault(m.player.getUUID(), 0);
        Entity t = m.crosshairPickEntity;
        if (t instanceof Player tp && tp != m.player) {
            return tp.getName().getString() + " " + POPS.getOrDefault(tp.getUUID(), 0) + " | вы " + own;
        }
        return "вы " + own;
    }

    @Override
    public void onTick() {
        if (mc().level == null && !POPS.isEmpty()) POPS.clear();
    }
}
