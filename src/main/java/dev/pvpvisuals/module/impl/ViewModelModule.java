package dev.pvpvisuals.module.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.pvpvisuals.mixin.MixinStatus;
import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ModeSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.Notifications;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Предметы в руках: положение, поворот и размер, отдельный размер щита и тотема, скорость взмаха
 * и размер анимации тотема при спасении. Работает через миксины ItemInHandRenderer, ScreenEffectRenderer и LivingEntity.
 */
public final class ViewModelModule extends Module {
    private static ViewModelModule instance;

    /** Флаги парных push/pop в миксинах (рендер идёт в одном потоке). */
    public static boolean handPushed;
    public static boolean popPushed;

    private final SliderSetting all = add(new SliderSetting("Размер всех предметов, %", 100, 20, 150, 5));
    private final SliderSetting shield = add(new SliderSetting("Щит, %", 100, 20, 150, 5));
    private final SliderSetting totem = add(new SliderSetting("Тотем, %", 100, 20, 150, 5));
    private final SliderSetting x = add(new SliderSetting("Сдвиг X (вбок)", 0, -1, 1, 0.05));
    private final SliderSetting y = add(new SliderSetting("Сдвиг Y (вверх)", 0, -1, 1, 0.05));
    private final SliderSetting z = add(new SliderSetting("Сдвиг Z (вперёд)", 0, -1, 1, 0.05));
    private final SliderSetting rx = add(new SliderSetting("Поворот X, °", 0, -90, 90, 1));
    private final SliderSetting ry = add(new SliderSetting("Поворот Y, °", 0, -90, 90, 1));
    private final SliderSetting rz = add(new SliderSetting("Поворот Z, °", 0, -90, 90, 1));
    private final SliderSetting swing = add(new SliderSetting("Скорость взмаха, %", 100, 40, 250, 5));
    private final ModeSetting pop = add(new ModeSetting("Анимация тотема", "Normal", "Normal", "Small", "Hidden"));
    private final SliderSetting popScale = add(new SliderSetting("Размер анимации тотема, %", 50, 20, 100, 5));

    private int ticks;
    private boolean warned;

    public ViewModelModule() {
        super("ViewModel", "Положение, размер и поворот предметов в руках, скорость взмаха", Category.VISUALS);
        instance = this;
    }

    private static boolean on() {
        return instance != null && instance.isEnabled();
    }

    private static float scale(ItemStack stack) {
        float k = instance.all.getFloat() / 100f;
        if (stack.is(Items.SHIELD)) k *= instance.shield.getFloat() / 100f;
        else if (stack.is(Items.TOTEM_OF_UNDYING)) k *= instance.totem.getFloat() / 100f;
        return k;
    }

    /** Есть ли для этого предмета хоть какое-то изменение. */
    public static boolean affects(ItemStack stack) {
        if (!on()) return false;
        ViewModelModule v = instance;
        return Math.abs(scale(stack) - 1f) > 0.001f || v.x.getFloat() != 0 || v.y.getFloat() != 0 || v.z.getFloat() != 0
                || v.rx.getFloat() != 0 || v.ry.getFloat() != 0 || v.rz.getFloat() != 0;
    }

    /** Применяет сдвиг, поворот и размер вокруг точки, где в кадре находится предмет. side: 1 - правая рука, -1 - левая. */
    public static void apply(PoseStack pose, ItemStack stack, float side) {
        ViewModelModule v = instance;
        pose.translate(side * v.x.getFloat(), v.y.getFloat(), v.z.getFloat());
        float px = side * 0.56f, py = -0.52f, pz = -0.72f;
        pose.translate(px, py, pz);
        if (v.rx.getFloat() != 0) pose.mulPose(Axis.XP.rotationDegrees(v.rx.getFloat()));
        if (v.ry.getFloat() != 0) pose.mulPose(Axis.YP.rotationDegrees(side * v.ry.getFloat()));
        if (v.rz.getFloat() != 0) pose.mulPose(Axis.ZP.rotationDegrees(side * v.rz.getFloat()));
        float s = scale(stack);
        pose.scale(s, s, s);
        pose.translate(-px, -py, -pz);
    }

    /** Множитель длительности взмаха (меньше 1 - быстрее). */
    public static float swingFactor() {
        return on() ? 100f / instance.swing.getFloat() : 1f;
    }

    public static boolean totemPopHidden() {
        return on() && instance.pop.is("Hidden");
    }

    public static float totemPopScale() {
        return on() && instance.pop.is("Small") ? instance.popScale.getFloat() / 100f : 1f;
    }

    @Override
    public void onEnable() {
        ticks = 0;
        warned = false;
    }

    @Override
    public void onTick() {
        if (warned || mc.player == null) return;
        if (!affects(mc.player.getMainHandItem()) && !affects(mc.player.getOffhandItem())) return;
        if (++ticks >= 100 && !MixinStatus.itemApplied) {
            warned = true;
            Notifications.warn("ViewModel: руки не поддерживаются в этой версии");
        }
    }
}
