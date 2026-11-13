package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.mixin.MixinStatus;
import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.util.Notifications;
import net.minecraft.client.CameraType;

/**
 * Свободный обзор: пока модуль включён, мышь крутит только камеру, а игрок продолжает смотреть и идти
 * в прежнюю сторону. Удобнее всего назначить клавишу (средняя кнопка мыши по модулю в ClickGUI).
 */
public final class FreeLookModule extends Module {
    private static FreeLookModule instance;
    private static float yaw, pitch;

    private final BooleanSetting thirdPerson = add(new BooleanSetting("Вид от третьего лица", true));
    private CameraType saved = CameraType.FIRST_PERSON;
    private int ticks;
    private boolean warned;

    public FreeLookModule() {
        super("FreeLook", "Свободный обзор камерой без поворота игрока", Category.VISUALS);
        instance = this;
    }

    public static boolean active() {
        return instance != null && instance.isEnabled() && mc.player != null;
    }

    public static float yaw() {
        return yaw;
    }

    public static float pitch() {
        return pitch;
    }

    public static void turn(double dYaw, double dPitch) {
        yaw += (float) dYaw * 0.15f;
        pitch = Math.max(-90f, Math.min(90f, pitch + (float) dPitch * 0.15f));
    }

    @Override
    public void onEnable() {
        ticks = 0;
        warned = false;
        if (mc.player != null) {
            yaw = mc.player.getYRot();
            pitch = mc.player.getXRot();
        }
        saved = mc.options.getCameraType();
        if (thirdPerson.isOn()) mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
    }

    @Override
    public void onDisable() {
        mc.options.setCameraType(saved);
    }

    @Override
    public void onTick() {
        if (warned || mc.player == null) return;
        if (++ticks >= 200 && !(MixinStatus.cameraApplied && MixinStatus.turnApplied)) {
            warned = true;
            Notifications.warn("FreeLook: не поддерживается в этой версии");
        }
    }
}
