package dev.pvpvisuals.module;

public enum Category {
    VISUALS("Визуал"),
    HUD("Интерфейс"),
    PLAYER("Игрок"),
    MISC("Прочее");

    public final String title;

    Category(String title) {
        this.title = title;
    }
}
