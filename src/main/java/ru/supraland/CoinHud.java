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
        if (player == null) {
            return;
        }

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data == null) {
            return;
        }

        TextRenderer textRenderer = client.textRenderer;
        Window window = client.getWindow();

        int x = 10;
        int y = window.getScaledHeight() / 2 - 10;

        String text = "Coins: " + data.coins + " / " + data.maxCoins;
        context.drawTextWithShadow(textRenderer, Text.literal(text), x, y, 0xFFD700);

        int yOffset = 14;
        if (data.speedMultiplier > 1.0) {
            context.drawTextWithShadow(textRenderer,
                Text.literal("Speed: x" + String.format("%.1f", data.speedMultiplier)),
                x, y + yOffset, 0x00FF00);
            yOffset += 12;
        }
        if (data.maxJumps > 1) {
            context.drawTextWithShadow(textRenderer,
                Text.literal("Jumps: " + data.maxJumps),
                x, y + yOffset, 0x00FF00);
            yOffset += 12;
        }
        if (data.jumpHeightMultiplier > 1.0) {
            // Исправлено: было «Счастье», теперь логичнее «Jump Height»
            context.drawTextWithShadow(textRenderer,
                Text.literal("Jump Height: x" + String.format("%.1f", data.jumpHeightMultiplier)),
                x, y + yOffset, 0xFFAA00);
            yOffset += 12;
        }
        if (data.hasLaser) {
            context.drawTextWithShadow(textRenderer,
                Text.literal("Laser: yes"),
                x, y + yOffset, 0xFF0000);
        }
    }
}
