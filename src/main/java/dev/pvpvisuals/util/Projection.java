package dev.pvpvisuals.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Переводит точку мира в координаты экрана для камеры от первого лица.
 * Позиция приблизительная: изменения FOV от бега и эффектов не учитываются.
 */
public final class Projection {
    private final double ex, ey, ez;
    private final double fx, fy, fz;
    private final double rx, ry, rz;
    private final double ux, uy, uz;
    private final double tanHalf;
    private final double aspect;
    private final int sw, sh;

    private Projection(LocalPlayer me, float pt, int sw, int sh) {
        Vec3 eye = me.getEyePosition(pt);
        ex = eye.x;
        ey = eye.y;
        ez = eye.z;

        double yaw = Math.toRadians(me.getViewYRot(pt));
        double pitch = Math.toRadians(me.getViewXRot(pt));
        double sinYaw = Math.sin(yaw), cosYaw = Math.cos(yaw);
        double sinPitch = Math.sin(pitch), cosPitch = Math.cos(pitch);

        fx = -sinYaw * cosPitch;
        fy = -sinPitch;
        fz = cosYaw * cosPitch;
        rx = -cosYaw;
        ry = 0;
        rz = -sinYaw;
        ux = ry * fz - rz * fy;
        uy = rz * fx - rx * fz;
        uz = rx * fy - ry * fx;

        tanHalf = Math.tan(Math.toRadians(Minecraft.getInstance().options.fov().get()) / 2.0);
        aspect = (double) sw / sh;
        this.sw = sw;
        this.sh = sh;
    }

    public static Projection of(LocalPlayer me, float partialTick, int screenW, int screenH) {
        return new Projection(me, partialTick, screenW, screenH);
    }

    /** Возвращает {x, y} на экране или null, если точка позади камеры или далеко за краем экрана. */
    public int[] toScreen(double x, double y, double z) {
        double dx = x - ex, dy = y - ey, dz = z - ez;
        double depth = dx * fx + dy * fy + dz * fz;
        if (depth < 0.5) return null;
        double nx = (dx * rx + dy * ry + dz * rz) / depth / (tanHalf * aspect);
        double ny = (dx * ux + dy * uy + dz * uz) / depth / tanHalf;
        if (Math.abs(nx) > 1.1 || Math.abs(ny) > 1.1) return null;
        return new int[]{(int) (sw / 2.0 + nx * sw / 2.0), (int) (sh / 2.0 - ny * sh / 2.0)};
    }
}
