package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.LineHud;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.util.HitTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Набор маленьких информационных плашек. Каждая включается отдельно в ClickGUI. */
public final class SimpleHuds {
    private SimpleHuds() {}

    private static final Minecraft MC = Minecraft.getInstance();
    private static final int WHITE = 0xFFFFFFFF, GREEN = 0xFF55FF55, YELLOW = 0xFFFFD54D, RED = 0xFFFF4D4D;

    private static double lastAliveX, lastAliveY, lastAliveZ;
    private static boolean wasAlive = true;
    private static String deathText = "нет";

    public static List<Module> create() {
        List<Module> list = new ArrayList<>();
        int s = 0;

        list.add(new LineHud("Ping", "Задержка до сервера", s++, "Пинг", () -> {
            if (MC.getConnection() == null) return "-";
            PlayerInfo pi = MC.getConnection().getPlayerInfo(MC.player.getUUID());
            return pi == null ? "-" : pi.getLatency() + " мс";
        }, () -> {
            if (MC.getConnection() == null) return WHITE;
            PlayerInfo pi = MC.getConnection().getPlayerInfo(MC.player.getUUID());
            int l = pi == null ? 0 : pi.getLatency();
            return l < 80 ? GREEN : l < 160 ? YELLOW : RED;
        }));

        list.add(new LineHud("FPS Counter", "Количество кадров в секунду", s++, "FPS",
                () -> String.valueOf(MC.getFps()),
                () -> MC.getFps() >= 120 ? GREEN : MC.getFps() >= 60 ? YELLOW : RED));

        list.add(new LineHud("Light Level", "Уровень света в вашей точке", s++, "Свет", () -> {
            BlockPos pos = MC.player.blockPosition();
            int b = MC.level.getBrightness(LightLayer.BLOCK, pos);
            int sky = MC.level.getBrightness(LightLayer.SKY, pos);
            return "блок " + b + " | небо " + sky;
        }, () -> MC.level.getBrightness(LightLayer.BLOCK, MC.player.blockPosition()) < 8 ? YELLOW : WHITE));

        list.add(new LineHud("Totem Counter", "Количество тотемов в инвентаре", s++, "Тотемы", () -> {
            int n = 0;
            LocalPlayer p = MC.player;
            for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
                ItemStack st = p.getInventory().getItem(i);
                if (st.is(Items.TOTEM_OF_UNDYING)) n += st.getCount();
            }
            return String.valueOf(n);
        }, () -> {
            int n = 0;
            LocalPlayer p = MC.player;
            for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
                ItemStack st = p.getInventory().getItem(i);
                if (st.is(Items.TOTEM_OF_UNDYING)) n += st.getCount();
            }
            return n == 0 ? RED : n == 1 ? YELLOW : GREEN;
        }));

        list.add(new LineHud("World Time", "Время и день в мире", s++, "Мир", () -> {
            long t = MC.level.getDayTime();
            long h = (t / 1000 + 6) % 24, m = (t % 1000) * 60 / 1000;
            return String.format(Locale.ROOT, "%02d:%02d | день %d", h, m, t / 24000 + 1);
        }, null));

        list.add(new LineHud("Players Count", "Игроки в зоне прорисовки", s++, "Игроки",
                () -> String.valueOf(MC.level.players().size()), null));

        list.add(new LineHud("Entities Count", "Количество существ вокруг", s++, "Существа", () -> {
            int n = 0;
            for (Entity ignored : MC.level.entitiesForRendering()) n++;
            return String.valueOf(n);
        }, null));

        list.add(new LineHud("Looking At", "Блок, на который вы смотрите, и расстояние", s++, "Блок", () -> {
            HitResult h = MC.hitResult;
            if (!(h instanceof BlockHitResult bhr) || h.getType() != HitResult.Type.BLOCK) return "-";
            String name = MC.level.getBlockState(bhr.getBlockPos()).getBlock().getName().getString();
            double d = MC.player.getEyePosition().distanceTo(bhr.getLocation());
            return name + " " + String.format(Locale.ROOT, "%.1f", d) + " м";
        }, null));

        list.add(new LineHud("Free Slots", "Свободные слоты инвентаря", s++, "Слоты", () -> {
            int free = 0;
            for (int i = 0; i < 36; i++) if (MC.player.getInventory().getItem(i).isEmpty()) free++;
            return free + " / 36";
        }, () -> {
            int free = 0;
            for (int i = 0; i < 36; i++) if (MC.player.getInventory().getItem(i).isEmpty()) free++;
            return free == 0 ? RED : free < 5 ? YELLOW : GREEN;
        }));

        list.add(new LineHud("Hit Counter", "Количество ударов по текущей цели", s++, "Удары",
                () -> String.valueOf(HitTracker.hits()), null));

        list.add(new LineHud("Death Point", "Координаты последней смерти", s++, "Смерть", () -> {
            LocalPlayer p = MC.player;
            boolean alive = p.isAlive() && p.getHealth() > 0;
            if (alive) {
                lastAliveX = p.getX();
                lastAliveY = p.getY();
                lastAliveZ = p.getZ();
            } else if (wasAlive) {
                deathText = String.format(Locale.ROOT, "%.0f %.0f %.0f", lastAliveX, lastAliveY, lastAliveZ);
            }
            wasAlive = alive;
            return deathText;
        }, null));

        list.add(new LineHud("Angles", "Горизонтальный и вертикальный угол взгляда", s++, "Углы", () -> {
            LocalPlayer p = MC.player;
            float yaw = ((p.getYRot() % 360f) + 540f) % 360f - 180f;
            return String.format(Locale.ROOT, "%.1f / %.1f", yaw, p.getXRot());
        }, null));

        list.add(new LineHud("Clock", "Время на часах", s++, "Время",
                () -> java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")), null));

        return list;
    }
}
