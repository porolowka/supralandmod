package ru.supraland;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class LinkRenderer {

    private static final int R = 153, G = 51, B = 204, A = 128;

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            if (player == null) return;

            ItemStack mainHand = player.getMainHandStack();
            ItemStack offHand = player.getOffHandStack();

            boolean hasLinker = mainHand.getItem() instanceof LinkingToolItem
                             || offHand.getItem() instanceof LinkingToolItem;
            boolean hasUnlinker = mainHand.getItem() instanceof UnlinkingToolItem
                               || offHand.getItem() instanceof UnlinkingToolItem;

            if (!hasLinker && !hasUnlinker) {
                LinkData.clearClientFirstSelection();
                return;
            }

            Map<BlockPos, Set<BlockPos>> links = LinkData.getAllClientLinks();

            MatrixStack matrices = context.matrixStack();
            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null) return;

            Vec3d cam = context.camera().getPos();
            VertexConsumer consumer = consumers.getBuffer(RenderLayer.getLines());
            Matrix4f matrix = matrices.peek().getPositionMatrix();

            Set<Long> drawn = new HashSet<>();
            for (var entry : links.entrySet()) {
                BlockPos a = entry.getKey();
                for (BlockPos b : entry.getValue()) {
                    long lo = Math.min(a.asLong(), b.asLong());
                    long hi = Math.max(a.asLong(), b.asLong());
                    if (drawn.add(lo * 73856093L ^ hi)) {
                        drawLine(consumer, matrix,
                            a.getX() + 0.5f - (float) cam.x, a.getY() + 0.5f - (float) cam.y, a.getZ() + 0.5f - (float) cam.z,
                            b.getX() + 0.5f - (float) cam.x, b.getY() + 0.5f - (float) cam.y, b.getZ() + 0.5f - (float) cam.z);
                    }
                }
            }

            if (hasLinker) {
                BlockPos first = LinkData.getClientFirstSelection();
                if (first != null) {
                    Vec3d eye = player.getEyePos();
                    drawLine(consumer, matrix,
                        first.getX() + 0.5f - (float) cam.x, first.getY() + 0.5f - (float) cam.y, first.getZ() + 0.5f - (float) cam.z,
                        (float) (eye.x - cam.x), (float) (eye.y - cam.y), (float) (eye.z - cam.z));
                }
            }
        });
    }

    private static void drawLine(VertexConsumer consumer, Matrix4f matrix,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2) {
        float nx = x2 - x1;
        float ny = y2 - y1;
        float nz = z2 - z1;
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 0) { nx /= len; ny /= len; nz /= len; }

        consumer.vertex(matrix, x1, y1, z1).color(R, G, B, A).normal(nx, ny, nz).next();
        consumer.vertex(matrix, x2, y2, z2).color(R, G, B, A).normal(nx, ny, nz).next();
    }
}
