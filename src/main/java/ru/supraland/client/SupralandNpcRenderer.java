package ru.supraland.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.VertexConsumer;
import ru.supraland.entity.SupralandNpcEntity;

public class SupralandNpcRenderer extends EntityRenderer<SupralandNpcEntity> {

    public SupralandNpcRenderer(Context ctx) {
        super(ctx);
    }

    @Override
    public void render(SupralandNpcEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // Тело — красный/синий куб
        float[] color = entity.getColor();
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(getTexture(entity)));

        // Простое тело — куб 0.6 x 1.8
        // Глаза — два белых пикселя
        // (полная геометрия будет через модель, это заготовка)

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SupralandNpcEntity entity) {
        float[] c = entity.getColor();
        if (c[0] > 0.5f) {
            return new Identifier("supralandmod", "textures/entity/red_npc.png");
        }
        return new Identifier("supralandmod", "textures/entity/blue_npc.png");
    }
}
