package dev.pvpvisuals.module;

public enum Category {
    VISUALS("Visuals"),
    HUD("HUD"),
    PLAYER("Player"),
    MISC("Misc");

    public final String title;

    Category(String title) {
        this.title = title;
    }
}
