package dev.pvpvisuals.util;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Оптимизация под Windows без прав администратора.
 * 1. Повышает приоритет процесса игры до High (только своего процесса).
 * 2. Подбирает JVM-аргументы под объём памяти компьютера.
 * 3. Создаёт optimize.bat и restore.bat: их вы запускаете сами, мод их не выполняет.
 */
public final class SystemOptimizer {
    private SystemOptimizer() {}

    public static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    /** Повышает приоритет текущего процесса. Выполняется в фоне, чтобы не вызвать фриз. */
    public static void raisePriorityAsync() {
        if (!isWindows()) return;
        long pid = ProcessHandle.current().pid();
        Thread t = new Thread(() -> {
            try {
                Process p = new ProcessBuilder("powershell", "-NoProfile", "-NonInteractive", "-Command",
                        "(Get-Process -Id " + pid + ").PriorityClass = 'High'")
                        .redirectErrorStream(true)
                        .start();
                if (!p.waitFor(15, TimeUnit.SECONDS)) p.destroyForcibly();
            } catch (IOException | InterruptedException ignored) {
                // PowerShell недоступен: приоритет остаётся прежним
            }
        }, "pvpvisuals-priority");
        t.setDaemon(true);
        t.start();
    }

    private static long totalMemoryGb() {
        try {
            // через рефлексию, чтобы сборка не зависела от внутренних пакетов JDK
            Object os = ManagementFactory.getOperatingSystemMXBean();
            Object bytes = Class.forName("com.sun.management.OperatingSystemMXBean")
                    .getMethod("getTotalMemorySize")
                    .invoke(os);
            if (bytes instanceof Long total) return Math.max(1, total / (1024L * 1024L * 1024L));
        } catch (ReflectiveOperationException | RuntimeException | LinkageError ignored) {
            // нет доступа к данным о памяти: берём значение по умолчанию
        }
        return 8;
    }

    /** Размер кучи: половина оперативной памяти, но не меньше 2 и не больше 8 ГБ. */
    public static int recommendedHeapGb() {
        return (int) Math.max(2, Math.min(8, totalMemoryGb() / 2));
    }

    public static String jvmArgsText() {
        int gb = recommendedHeapGb();
        String heap = "-Xms" + gb + "G -Xmx" + gb + "G";
        return "PvP Visuals - JVM arguments for Java 21\n"
                + "Paste ONE variant into your launcher (JVM arguments field).\n\n"
                + "Variant 1 (recommended, 8 GB RAM or more): Generational ZGC\n"
                + heap + " -XX:+UseZGC -XX:+ZGenerational -XX:+AlwaysPreTouch -XX:+DisableExplicitGC\n\n"
                + "Variant 2 (less than 8 GB RAM): G1GC\n"
                + heap + " -XX:+UseG1GC -XX:MaxGCPauseMillis=40 -XX:+UnlockExperimentalVMOptions "
                + "-XX:G1NewSizePercent=30 -XX:G1ReservePercent=20 -XX:+DisableExplicitGC\n\n"
                + "Detected RAM: about " + totalMemoryGb() + " GB, recommended heap: " + gb + " GB\n";
    }

    private static final String OPTIMIZE_BAT = String.join("\r\n",
            "@echo off",
            "echo PvP Visuals - Windows optimizer (current user, reversible with restore.bat)",
            "echo.",
            "echo [1/5] High performance power plan",
            "powercfg /setactive SCHEME_MIN",
            "echo [2/5] Turn off transparency effects",
            "reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\" /v EnableTransparency /t REG_DWORD /d 0 /f >nul",
            "echo [3/5] Turn off background game capture (Game DVR)",
            "reg add \"HKCU\\System\\GameConfigStore\" /v GameDVR_Enabled /t REG_DWORD /d 0 /f >nul",
            "reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\GameDVR\" /v AppCaptureEnabled /t REG_DWORD /d 0 /f >nul",
            "echo [4/5] Flush DNS cache",
            "ipconfig /flushdns >nul",
            "echo [5/5] Delete temporary files of the current user",
            "del /q /f /s \"%TEMP%\\*\" >nul 2>&1",
            "echo.",
            "echo Done. Run restore.bat to undo points 1-3.",
            "pause",
            "");

    private static final String RESTORE_BAT = String.join("\r\n",
            "@echo off",
            "echo PvP Visuals - restoring default Windows settings",
            "powercfg /setactive SCHEME_BALANCED",
            "reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\" /v EnableTransparency /t REG_DWORD /d 1 /f >nul",
            "reg add \"HKCU\\System\\GameConfigStore\" /v GameDVR_Enabled /t REG_DWORD /d 1 /f >nul",
            "reg add \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\GameDVR\" /v AppCaptureEnabled /t REG_DWORD /d 1 /f >nul",
            "echo Done.",
            "pause",
            "");

    /** Создаёт файлы в config/pvpvisuals/optimizer и возвращает путь к папке. */
    public static Path writeFiles() throws IOException {
        Path dir = FabricLoader.getInstance().getConfigDir().resolve("pvpvisuals").resolve("optimizer");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("jvm-args.txt"), jvmArgsText(), StandardCharsets.UTF_8);
        Files.writeString(dir.resolve("optimize.bat"), OPTIMIZE_BAT, StandardCharsets.US_ASCII);
        Files.writeString(dir.resolve("restore.bat"), RESTORE_BAT, StandardCharsets.US_ASCII);
        return dir;
    }
}
