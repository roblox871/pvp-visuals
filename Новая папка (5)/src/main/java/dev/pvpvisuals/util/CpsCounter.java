package dev.pvpvisuals.util;

import net.minecraft.client.Minecraft;

import java.util.ArrayDeque;

/** Считает клики мыши за последнюю секунду. Тикается из ModuleManager независимо от модулей. */
public final class CpsCounter {
    private CpsCounter() {}

    private static final ArrayDeque<Long> LEFT = new ArrayDeque<>();
    private static final ArrayDeque<Long> RIGHT = new ArrayDeque<>();
    private static boolean wasLeft, wasRight;

    public static void tick(Minecraft mc) {
        long now = System.currentTimeMillis();
        boolean l = mc.mouseHandler.isLeftPressed();
        boolean r = mc.mouseHandler.isRightPressed();
        if (l && !wasLeft) LEFT.add(now);
        if (r && !wasRight) RIGHT.add(now);
        wasLeft = l;
        wasRight = r;
        while (!LEFT.isEmpty() && now - LEFT.peek() > 1000) LEFT.poll();
        while (!RIGHT.isEmpty() && now - RIGHT.peek() > 1000) RIGHT.poll();
    }

    public static int left() {
        return LEFT.size();
    }

    public static int right() {
        return RIGHT.size();
    }
}
