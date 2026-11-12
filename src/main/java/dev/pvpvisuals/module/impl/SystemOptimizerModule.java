package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.util.Notifications;
import dev.pvpvisuals.util.SystemOptimizer;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Оптимизация Windows. При включении повышает приоритет игры до High.
 * Переключатель «Создать файлы» кладёт в config/pvpvisuals/optimizer файлы jvm-args.txt, optimize.bat и restore.bat.
 * Скрипты мод сам не запускает: вы запускаете их вручную.
 */
public final class SystemOptimizerModule extends Module {
    private final BooleanSetting priority = add(new BooleanSetting("Приоритет игры High", true));
    private final BooleanSetting generate = add(new BooleanSetting("Создать файлы (.bat, JVM)", false));

    public SystemOptimizerModule() {
        super("System Optimizer", "Приоритет игры, JVM-аргументы и скрипты для Windows", Category.MISC);
    }

    @Override
    public void onEnable() {
        if (priority.isOn()) SystemOptimizer.raisePriorityAsync();
    }

    @Override
    public void onTick() {
        if (!generate.isOn()) return;
        generate.set(false);
        try {
            Path dir = SystemOptimizer.writeFiles();
            Notifications.info("Файлы сохранены: " + dir);
        } catch (IOException e) {
            Notifications.warn("Не удалось создать файлы оптимизации");
        }
    }
}
