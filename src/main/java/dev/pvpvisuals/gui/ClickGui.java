package dev.pvpvisuals.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.pvpvisuals.PvpVisualsClient;
import dev.pvpvisuals.config.ConfigManager;
import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.module.impl.InterfaceModule;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.setting.ColorSetting;
import dev.pvpvisuals.setting.ModeSetting;
import dev.pvpvisuals.setting.Setting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleConsumer;

/**
 * ClickGUI в режиме immediate-mode: при каждой отрисовке строится список кликабельных областей (Region),
 * по нему же обрабатываются клики.
 * ЛКМ по модулю: включить. ПКМ: открыть настройки. СКМ: назначить клавишу (Delete - снять, Esc - отмена).
 */
public class ClickGui extends Screen {
    private static final Set<Module> EXPANDED = new HashSet<>();
    private static final Map<Category, Float> SCROLL = new EnumMap<>(Category.class);

    private static final int ROW = 16;
    private static final int GAP = 8;
    private static final int TOP = 36;

    private static final class Region {
        int x, y, w, h;
        Runnable left, right, middle;
        DoubleConsumer drag;

        boolean contains(double mx, double my) {
            return mx >= x && mx < x + w && my >= y && my < y + h;
        }
    }

    private final List<Region> regions = new ArrayList<>();
    private final Map<Category, int[]> bounds = new EnumMap<>(Category.class);
    private Region active;
    private Module binding;
    private Module hoverTip;
    private String query = "";
    private int mouseX, mouseY;

    public ClickGui() {
        super(Component.literal("PvP Visuals"));
    }

    @Override
    public void renderBackground(GuiGraphics g, int mx, int my, float delta) {
        g.fill(0, 0, this.width, this.height, 0x70000000);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float delta) {
        super.render(g, mx, my, delta);
        this.mouseX = mx;
        this.mouseY = my;
        regions.clear();
        bounds.clear();
        hoverTip = null;

        int accent = InterfaceModule.accent();
        int panelW = Math.min(112, (this.width - 24 - 3 * GAP) / 4);
        int total = 4 * panelW + 3 * GAP;
        int startX = (this.width - total) / 2;

        RenderUtils.text(g, "PvP Visuals", startX, 14, accent);
        int bx = startX + total;
        bx = topButton(g, "Профиль: минимум", bx, this.height - 38, () -> {
            PvpVisualsClient.modules().applyPreset(dev.pvpvisuals.module.ModuleManager.PRESET_MINIMAL);
            PvpVisualsClient.modules().autoLayout(this.width, this.height);
        });
        bx = topButton(g, "Профиль: ПвП", bx, this.height - 38, () -> {
            PvpVisualsClient.modules().applyPreset(dev.pvpvisuals.module.ModuleManager.PRESET_PVP);
            PvpVisualsClient.modules().autoLayout(this.width, this.height);
        });
        bx = topButton(g, "Авто-HUD", bx, this.height - 38, () -> PvpVisualsClient.modules().autoLayout(this.width, this.height));
        bx = topButton(g, "HUD Editor", bx, this.height - 38, () -> minecraft.setScreen(new HudEditorScreen()));
        bx = topButton(g, "Load", bx, this.height - 38, () -> ConfigManager.load(PvpVisualsClient.modules()));
        topButton(g, "Save", bx, this.height - 38, () -> ConfigManager.save(PvpVisualsClient.modules()));

        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; i++) {
            drawPanel(g, cats[i], startX + i * (panelW + GAP), TOP, panelW, accent);
        }

        drawSearch(g, startX, total);
        drawTooltip(g);

        String hint = binding != null
                ? "Нажмите клавишу для " + binding.getTitle() + " (Delete - снять, Esc - отмена)"
                : "ЛКМ - вкл/выкл   ПКМ - настройки   СКМ - клавиша   Печатайте для поиска (англ.)";
        RenderUtils.textCentered(g, hint, this.width / 2, this.height - 12, RenderUtils.GRAY);
    }

    private void drawSearch(GuiGraphics g, int startX, int total) {
        int w = 130, x = this.width / 2 - w / 2, y = 10;
        RenderUtils.roundRect(g, x, y, w, 16, 3, 0xFF1A1A20);
        boolean blink = (System.currentTimeMillis() / 500) % 2 == 0;
        String shown = query.isEmpty() ? "Поиск..." : query + (blink ? "_" : "");
        RenderUtils.text(g, shown, x + 6, y + 4, query.isEmpty() ? 0xFF6A6A78 : RenderUtils.WHITE);
    }

    private void drawTooltip(GuiGraphics g) {
        if (hoverTip == null || binding != null) return;
        String text = hoverTip.getDescription();
        int w = RenderUtils.textWidth(text) + 12;
        int x = Math.min(mouseX + 12, this.width - w - 4), y = Math.min(mouseY + 12, this.height - 22);
        RenderUtils.panel(g, x, y, w, 16, 0xF0101014, InterfaceModule.accent());
        RenderUtils.text(g, text, x + 6, y + 4, RenderUtils.WHITE);
    }

    private int topButton(GuiGraphics g, String label, int right, int y, Runnable action) {
        int w = RenderUtils.textWidth(label) + 12;
        int x = right - w;
        boolean hov = RenderUtils.hover(mouseX, mouseY, x, y, w, 16);
        RenderUtils.roundRect(g, x, y, w, 16, 3, hov ? 0xFF3A3A44 : 0xFF24242C);
        RenderUtils.textCentered(g, label, x + w / 2, y + 4, RenderUtils.WHITE);
        Region r = new Region();
        r.x = x; r.y = y; r.w = w; r.h = 16; r.left = action;
        regions.add(r);
        return x - 6;
    }

    private void drawPanel(GuiGraphics g, Category cat, int x, int top, int pw, int accent) {
        List<Module> all = PvpVisualsClient.modules().byCategory(cat);
        List<Module> mods = new ArrayList<>();
        int enabledCount = 0;
        for (Module m : all) {
            if (m.isEnabled()) enabledCount++;
            if (query.isEmpty() || m.getName().toLowerCase().contains(query) || m.getTitle().toLowerCase().contains(query)) mods.add(m);
        }

        int content = 0;
        for (Module m : mods) {
            content += ROW;
            if (EXPANDED.contains(m)) for (Setting<?> s : m.getSettings()) content += settingHeight(s);
        }

        int bodyTop = top + 20;
        int bodyH = Math.min(content, this.height - bodyTop - 46);
        bodyH = Math.max(bodyH, ROW);
        float maxScroll = Math.max(0, content - bodyH);
        float scroll = Math.max(0, Math.min(maxScroll, SCROLL.getOrDefault(cat, 0f)));
        SCROLL.put(cat, scroll);
        bounds.put(cat, new int[]{x, top, pw, bodyTop + bodyH - top});

        RenderUtils.roundRect(g, x, top, pw, 20, 3, 0xFF1A1A20);
        RenderUtils.gradientH(g, x + 2, top, pw - 4, 1, accent, InterfaceModule.accent2());
        RenderUtils.text(g, cat.title, x + 6, top + 6, RenderUtils.WHITE);
        RenderUtils.textRight(g, enabledCount + "/" + all.size(), x + pw - 6, top + 6, RenderUtils.GRAY);
        RenderUtils.rect(g, x, bodyTop, pw, bodyH, 0xD0101014);

        int clipTop = bodyTop, clipBottom = bodyTop + bodyH;
        g.enableScissor(x, clipTop, x + pw, clipBottom);
        int cy = bodyTop - (int) scroll;
        for (Module m : mods) {
            boolean hov = RenderUtils.hover(mouseX, mouseY, x, cy, pw, ROW)
                    && mouseY >= clipTop && mouseY < clipBottom;
            if (hov) hoverTip = m;
            if (m.isEnabled()) {
                RenderUtils.rect(g, x, cy, 2, ROW, accent);
                RenderUtils.gradientH(g, x, cy, pw, ROW,
                        RenderUtils.withAlpha(accent, 120), RenderUtils.withAlpha(InterfaceModule.accent2(), 50));
            }
            else if (hov) RenderUtils.rect(g, x, cy, pw, ROW, 0x30FFFFFF);
            RenderUtils.text(g, m.getTitle(), x + 6, cy + 4, m.isEnabled() ? RenderUtils.WHITE : RenderUtils.GRAY);

            String tag;
            if (binding == m) tag = "...";
            else if (m.getKey() >= 0) tag = "[" + keyName(m.getKey()) + "]";
            else tag = m.getSettings().isEmpty() ? "" : (EXPANDED.contains(m) ? "-" : "+");
            if (!tag.isEmpty()) RenderUtils.textRight(g, tag, x + pw - 5, cy + 4, RenderUtils.GRAY);

            Region r = region(x, cy, pw, ROW, clipTop, clipBottom);
            if (r != null) {
                r.left = m::toggle;
                r.right = () -> {
                    if (!EXPANDED.remove(m)) EXPANDED.add(m);
                };
                r.middle = () -> binding = m;
            }
            cy += ROW;

            if (EXPANDED.contains(m)) {
                for (Setting<?> s : m.getSettings()) cy += drawSetting(g, s, x, cy, pw, clipTop, clipBottom, accent);
            }
        }
        g.disableScissor();
    }

    private static int settingHeight(Setting<?> s) {
        return (s instanceof SliderSetting || s instanceof ColorSetting) ? 22 : 15;
    }

    private int drawSetting(GuiGraphics g, Setting<?> s, int x, int y, int pw, int clipTop, int clipBottom, int accent) {
        int h = settingHeight(s);
        RenderUtils.rect(g, x, y, pw, h, 0x40000000);
        RenderUtils.text(g, s.getName(), x + 8, y + 3, RenderUtils.GRAY);

        Region r = region(x, y, pw, h, clipTop, clipBottom);
        int bx = x + 8, bw = pw - 16;

        if (s instanceof BooleanSetting b) {
            RenderUtils.roundRect(g, x + pw - 17, y + 3, 9, 9, 2, b.isOn() ? accent : RenderUtils.DARK);
            if (r != null) r.left = b::toggle;
        } else if (s instanceof ModeSetting m) {
            RenderUtils.textRight(g, m.get(), x + pw - 8, y + 3, accent);
            if (r != null) {
                r.left = () -> m.cycle(1);
                r.right = () -> m.cycle(-1);
            }
        } else if (s instanceof SliderSetting sl) {
            RenderUtils.textRight(g, sl.display(), x + pw - 8, y + 3, RenderUtils.WHITE);
            RenderUtils.rect(g, bx, y + 15, bw, 3, RenderUtils.DARK);
            RenderUtils.rect(g, bx, y + 15, (int) Math.round(bw * sl.ratio()), 3, accent);
            if (r != null) {
                r.drag = mx -> sl.set(sl.getMin() + (sl.getMax() - sl.getMin()) * Math.max(0, Math.min(1, (mx - bx) / bw)));
            }
        } else if (s instanceof ColorSetting c) {
            RenderUtils.roundRect(g, x + pw - 17, y + 3, 9, 9, 2, c.get());
            int steps = Math.max(1, bw);
            for (int i = 0; i < steps; i++) {
                RenderUtils.rect(g, bx + i, y + 15, 1, 4, RenderUtils.hsb(i / (float) steps, 0.65f, 1f));
            }
            RenderUtils.rect(g, bx + Math.round(c.getHue() * (bw - 1)), y + 13, 1, 8, RenderUtils.WHITE);
            if (r != null) r.drag = mx -> c.setHue((float) ((mx - bx) / bw));
        }
        return h;
    }

    /** Создаёт область клика, обрезанную по видимой части панели. */
    private Region region(int x, int y, int w, int h, int clipTop, int clipBottom) {
        int y1 = Math.max(y, clipTop);
        int y2 = Math.min(y + h, clipBottom);
        if (y2 <= y1) return null;
        Region r = new Region();
        r.x = x; r.y = y1; r.w = w; r.h = y2 - y1;
        regions.add(r);
        return r;
    }

    private static String keyName(int key) {
        return InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x(), my = event.y();
        int button = event.button();
        for (Region r : regions) {
            if (!r.contains(mx, my)) continue;
            if (button == 0) {
                if (r.left != null) r.left.run();
                if (r.drag != null) {
                    active = r;
                    r.drag.accept(mx);
                }
            } else if (button == 1 && r.right != null) {
                r.right.run();
            } else if (button == 2 && r.middle != null) {
                r.middle.run();
            }
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (active != null && active.drag != null) {
            active.drag.accept(event.x());
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        active = null;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
        for (Map.Entry<Category, int[]> e : bounds.entrySet()) {
            int[] b = e.getValue();
            if (RenderUtils.hover(mx, my, b[0], b[1], b[2], b[3])) {
                SCROLL.merge(e.getKey(), (float) (-vertical * 14), Float::sum);
                return true;
            }
        }
        return super.mouseScrolled(mx, my, horizontal, vertical);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int key = event.key();
        if (binding != null) {
            if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) binding.setKey(-1);
            else if (key != GLFW.GLFW_KEY_ESCAPE) binding.setKey(key);
            binding = null;
            return true;
        }
        if (key == GLFW.GLFW_KEY_BACKSPACE && !query.isEmpty()) {
            query = query.substring(0, query.length() - 1);
            return true;
        }
        if ((key >= GLFW.GLFW_KEY_A && key <= GLFW.GLFW_KEY_Z) || (key >= GLFW.GLFW_KEY_0 && key <= GLFW.GLFW_KEY_9)
                || key == GLFW.GLFW_KEY_SPACE) {
            if (query.length() < 16) query += (char) (key >= GLFW.GLFW_KEY_A && key <= GLFW.GLFW_KEY_Z ? key + 32 : key);
            return true;
        }
        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
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
