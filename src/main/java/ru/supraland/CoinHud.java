package ru.supraland;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class CoinHud {
    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data == null) return;

        public void sync(PlayerEntity player) {
            ModComponents.UPGRADE_DATA.sync(player);
        }

        TextRenderer textRenderer = client.textRenderer;
        Window window = client.getWindow();

        // Слева по центру экрана
        int x = 10;
        int y = window.getScaledHeight() / 2 - 10;

        String text = "Монеты: " + data.coins + " / " + data.maxCoins;
        context.drawTextWithShadow(textRenderer, Text.literal(text), x, y, 0xFFD700);

        // Доп. инфо об улучшениях
        int yOffset = 14;
        if (data.speedMultiplier > 1.0) {
            context.drawTextWithShadow(textRenderer, Text.literal("Скорость: x" + String.format("%.1f", data.speedMultiplier)), x, y + yOffset, 0x00FF00);
            yOffset += 12;
        }
        if (data.maxJumps > 1) {
            context.drawTextWithShadow(textRenderer, Text.literal("Прыжков: " + data.maxJumps), x, y + yOffset, 0x00FF00);
            yOffset += 12;
        }
        if (data.jumpHeightMultiplier > 1.0) {
            context.drawTextWithShadow(textRenderer, Text.literal("Счастье: x" + String.format("%.1f", data.jumpHeightMultiplier)), x, y + yOffset, 0xFFAA00);
            yOffset += 12;
        }
        if (data.hasLaser) {
            context.drawTextWithShadow(textRenderer, Text.literal("Лазер: есть"), x, y + yOffset, 0xFF0000);
        }
    }
}
