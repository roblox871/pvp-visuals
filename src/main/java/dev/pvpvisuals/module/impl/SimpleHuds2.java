package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.LineHud;
import dev.pvpvisuals.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Ещё один набор информационных плашек. Плашка с пустым значением (null) не рисуется. */
public final class SimpleHuds2 {
    private SimpleHuds2() {}

    private static final Minecraft MC = Minecraft.getInstance();
    private static final int GREEN = 0xFF55FF55, YELLOW = 0xFFFFD54D, RED = 0xFFFF4D4D;
    private static double maxSpeed;

    private static int count(LocalPlayer p, net.minecraft.world.item.Item item) {
        int n = 0;
        for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
            ItemStack st = p.getInventory().getItem(i);
            if (st.is(item)) n += st.getCount();
        }
        return n;
    }

    private static double fall(LocalPlayer p) {
        return (double) p.fallDistance;
    }

    public static List<Module> create() {
        List<Module> list = new ArrayList<>();
        int s = 14;

        list.add(new LineHud("Saturation", "Насыщение (скрытая сытость)", s++, "Насыщение",
                () -> String.format(Locale.ROOT, "%.1f", MC.player.getFoodData().getSaturationLevel()), null));
        list.add(new LineHud("XP Level", "Уровень опыта", s++, "Опыт",
                () -> String.valueOf(MC.player.experienceLevel), () -> 0xFF8CFF4D));
        list.add(new LineHud("Air Supply", "Запас воздуха под водой (скрыт, пока не нужен)", s++, "Воздух", () -> {
            int air = MC.player.getAirSupply(), max = MC.player.getMaxAirSupply();
            if (air >= max) return null;
            return String.format(Locale.ROOT, "%.1f c", Math.max(0, air) / 20.0);
        }, () -> MC.player.getAirSupply() < 100 ? RED : YELLOW));
        list.add(new LineHud("Chunk", "Координаты чанка", s++, "Чанк",
                () -> (MC.player.blockPosition().getX() >> 4) + " " + (MC.player.blockPosition().getZ() >> 4), null));
        list.add(new LineHud("Nether Coords", "Координаты в соседнем измерении (Незер или Верхний мир)", s++, "Незер", () -> {
            boolean nether = MC.level.dimension() == Level.NETHER;
            double k = nether ? 8.0 : 1.0 / 8.0;
            return String.format(Locale.ROOT, "%.0f %.0f", MC.player.getX() * k, MC.player.getZ() * k);
        }, null));
        list.add(new LineHud("Date", "Сегодняшняя дата", s++, "Дата",
                () -> java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")), null));
        list.add(new LineHud("Gamemode", "Текущий игровой режим", s++, "Режим",
                () -> MC.gameMode == null ? "-" : MC.gameMode.getPlayerMode().getName(), null));
        list.add(new LineHud("Max Speed", "Максимальная скорость за сессию", s++, "Макс. скорость", () -> {
            maxSpeed = Math.max(maxSpeed, MC.player.getDeltaMovement().length() * 20.0);
            return String.format(Locale.ROOT, "%.1f б/с", maxSpeed);
        }, null));
        list.add(new LineHud("Weather HUD", "Погода в мире", s++, "Погода",
                () -> MC.level.isThundering() ? "Гроза" : MC.level.isRaining() ? "Дождь" : "Ясно", null));
        list.add(new LineHud("Held Name", "Название предмета в руке", s++, "В руке", () -> {
            ItemStack st = MC.player.getMainHandItem();
            return st.isEmpty() ? null : st.getHoverName().getString();
        }, null));
        list.add(new LineHud("Potion Count", "Количество взрывных зелий", s++, "Зелья",
                () -> String.valueOf(count(MC.player, Items.SPLASH_POTION)), null));
        list.add(new LineHud("Arrow Count", "Количество стрел", s++, "Стрелы",
                () -> String.valueOf(count(MC.player, Items.ARROW)), null));
        list.add(new LineHud("Target Distance", "Расстояние до цели в прицеле", s++, "Цель", () -> {
            Entity e = MC.crosshairPickEntity;
            if (e == null) return null;
            return e.getName().getString() + " " + String.format(Locale.ROOT, "%.1f м", MC.player.distanceTo(e));
        }, null));
        list.add(new LineHud("Block Count", "Сколько блоков осталось в руке", s++, "Блоки", () -> {
            ItemStack st = MC.player.getMainHandItem();
            return st.getItem() instanceof BlockItem ? String.valueOf(st.getCount()) : null;
        }, () -> MC.player.getMainHandItem().getCount() < 8 ? RED : GREEN));
        list.add(new LineHud("Burning", "Показывает, что вы горите", s++, "Огонь",
                () -> MC.player.isOnFire() ? "Вы горите!" : null, () -> RED));
        list.add(new LineHud("Elytra Speed", "Скорость полёта на элитрах", s++, "Элитры",
                () -> MC.player.isFallFlying()
                        ? String.format(Locale.ROOT, "%.1f б/с", MC.player.getDeltaMovement().length() * 20.0) : null, null));
        list.add(new LineHud("Fall Distance", "Высота падения (скрыта, если падение мало)", s++, "Падение", () -> {
            double f = fall(MC.player);
            return f < 1.0 ? null : String.format(Locale.ROOT, "%.1f м", f);
        }, () -> fall(MC.player) >= 3.0 ? RED : YELLOW));
        list.add(new LineHud("Crit Indicator", "Показывает, что падение сейчас даст критический удар", s++, "Крит", () -> {
            LocalPlayer p = MC.player;
            boolean ok = fall(p) > 0.0 && !p.onGround() && !p.isInWater() && !p.onClimbable() && !p.isPassenger();
            return ok ? "Готов" : null;
        }, () -> GREEN));
        return list;
    }
}
