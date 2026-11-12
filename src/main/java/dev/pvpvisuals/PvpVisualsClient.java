package dev.pvpvisuals;

import com.mojang.blaze3d.platform.InputConstants;
import dev.pvpvisuals.config.ConfigManager;
import dev.pvpvisuals.gui.ClickGui;
import dev.pvpvisuals.input.KeybindManager;
import dev.pvpvisuals.module.ModuleManager;
import dev.pvpvisuals.module.impl.CrosshairModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class PvpVisualsClient implements ClientModInitializer {
    public static final String MOD_ID = "pvpvisuals";

    private static ModuleManager modules;
    private static final KeybindManager KEYBINDS = new KeybindManager();
    private static KeyMapping openGui;

    public static ModuleManager modules() {
        return modules;
    }

    @Override
    public void onInitializeClient() {
        modules = new ModuleManager();
        ConfigManager.load(modules);

        KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));
        openGui = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.pvpvisuals.gui", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            modules.tick();
            KEYBINDS.tick(mc, modules);
            while (openGui.consumeClick()) {
                mc.setScreen(new ClickGui());
            }
        });

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                Identifier.fromNamespaceAndPath(MOD_ID, "hud"),
                modules::renderHud
        );

        // Свой прицел заменяет ванильный, пока модуль Crosshair включён
        HudElementRegistry.replaceElement(
                VanillaHudElements.CROSSHAIR,
                original -> (g, d) -> {
                    if (!CrosshairModule.isActive()) original.render(g, d);
                }
        );

        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> {
            ConfigManager.save(modules);
            modules.shutdown();
        });
    }
}
