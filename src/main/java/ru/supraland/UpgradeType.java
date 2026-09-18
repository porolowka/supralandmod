package ru.supraland;

public enum UpgradeType {
    SPEED_X2("Скорость x2", "speed_x2"),
    DOUBLE_JUMP("Двойной прыжок", "double_jump"),
    TRIPLE_JUMP("Тройной прыжок", "triple_jump"),
    HAPPINESS("Счастье (прыжок x3)", "happiness"),
    SWORD_DAMAGE("Урон меча +1", "sword_damage"),
    GUN_DAMAGE("Урон пушки +10%", "gun_damage"),
    COIN_CAPACITY("Вместимость монет x2", "coin_capacity"),
    COINS("Монеты (30 шт)", "coins"),
    FIRE_RATE("Скорострельность +10%", "fire_rate"),
    LASER("Лазер для пушки", "laser");

    private final String displayName;
    private final String id;

    UpgradeType(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() { return displayName; }
    public String getId() { return id; }
}
