package ru.supraland;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import ru.supraland.client.SupralandNpcRenderer;

public class ClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            CoinHud.render(drawContext);
        });

        EntityRendererRegistry.register(SupralandMod.RED_NPC, SupralandNpcRenderer::new);
        EntityRendererRegistry.register(SupralandMod.BLUE_NPC, SupralandNpcRenderer::new);

        // Левый клик по блоку с пушкой — стреляем шариком
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (player.getMainHandStack().getItem() instanceof RedCrystalGunItem) {
                if (!world.isClient) {
                    RedCrystalGunItem.shootBall(player);
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });

        // Левый клик по entity с пушкой — стреляем шариком
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player.getMainHandStack().getItem() instanceof RedCrystalGunItem) {
                if (!world.isClient) {
                    RedCrystalGunItem.shootBall(player);
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
}
