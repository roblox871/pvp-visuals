package dev.pvpvisuals.input;

import com.mojang.blaze3d.platform.InputConstants;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.module.ModuleManager;
import net.minecraft.client.Minecraft;

/**
 * Привязки клавиш модулей. Клавиши опрашиваются напрямую, поэтому привязку можно менять в ClickGUI
 * (средняя кнопка мыши по модулю), не регистрируя KeyMapping на каждый модуль.
 */
public final class KeybindManager {
    private final boolean[] down = new boolean[GLFW_LAST + 1];

    private static final int GLFW_LAST = 348;

    public void tick(Minecraft mc, ModuleManager modules) {
        var window = mc.getWindow();
        boolean inGame = mc.screen == null;

        for (Module m : modules.all()) {
            int k = m.getKey();
            if (k < 0 || k > GLFW_LAST) continue;
            boolean now = InputConstants.isKeyDown(window, k);
            if (m.isHoldMode()) {
                m.setEnabled(inGame && now);
            } else if (inGame && now && !down[k]) {
                m.toggle();
            }
        }
        for (Module m : modules.all()) {
            int k = m.getKey();
            if (k < 0 || k > GLFW_LAST) continue;
            down[k] = InputConstants.isKeyDown(window, k);
        }
    }
}
