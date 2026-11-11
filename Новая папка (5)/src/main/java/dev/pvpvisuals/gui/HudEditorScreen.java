package dev.pvpvisuals.gui;

import dev.pvpvisuals.PvpVisualsClient;
import dev.pvpvisuals.config.ConfigManager;
import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.module.impl.InterfaceModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Режим перетаскивания HUD-элементов. Сами элементы рисует HUD под экраном, здесь только рамки и перенос. */
public class HudEditorScreen extends Screen {
    private HudModule dragging;
    private int offX, offY;

    public HudEditorScreen() {
        super(Component.literal("HUD Editor"));
    }

    @Override
    public void renderBackground(GuiGraphics g, int mx, int my, float delta) {
        g.fill(0, 0, this.width, this.height, 0x50000000);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float delta) {
        super.render(g, mx, my, delta);
        int accent = InterfaceModule.accent();
        for (HudModule h : PvpVisualsClient.modules().hudModules()) {
            if (!h.isEnabled()) continue;
            h.ensurePositioned(this.width, this.height);
            boolean hov = h == dragging || RenderUtils.hover(mx, my, h.getX(), h.getY(), h.getWidth(), h.getHeight());
            RenderUtils.outline(g, h.getX() - 1, h.getY() - 1, h.getWidth() + 2, h.getHeight() + 2,
                    hov ? accent : RenderUtils.withAlpha(accent, 110));
            RenderUtils.text(g, h.getName(), h.getX(), h.getY() - 10, hov ? RenderUtils.WHITE : RenderUtils.GRAY);
        }
        RenderUtils.textCentered(g, "Перетаскивайте элементы мышью. Esc - сохранить и выйти", this.width / 2, 8, RenderUtils.WHITE);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            List<HudModule> all = PvpVisualsClient.modules().hudModules();
            for (int i = all.size() - 1; i >= 0; i--) {
                HudModule h = all.get(i);
                if (h.isEnabled() && RenderUtils.hover(event.x(), event.y(), h.getX(), h.getY(), h.getWidth(), h.getHeight())) {
                    dragging = h;
                    offX = (int) event.x() - h.getX();
                    offY = (int) event.y() - h.getY();
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging != null) {
            dragging.setPosition((int) event.x() - offX, (int) event.y() - offY, this.width, this.height);
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        dragging = null;
        return super.mouseReleased(event);
    }

    @Override
    public void removed() {
        ConfigManager.save(PvpVisualsClient.modules());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
