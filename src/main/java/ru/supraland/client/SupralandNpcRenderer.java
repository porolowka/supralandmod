package ru.supraland.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import ru.supraland.SupralandMod;
import ru.supraland.entity.SupralandNpcEntity;

public class SupralandNpcRenderer extends EntityRenderer<SupralandNpcEntity> {
    public SupralandNpcRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(SupralandNpcEntity entity) {
        return new Identifier(SupralandMod.MOD_ID, "textures/entity/red_npc.png");
    }

    @Override
    public void render(SupralandNpcEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        float[] color = entity.getColor();
        Box box = new Box(-0.3, 0, -0.3, 0.3, 1.8, 0.3);
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        WorldRenderer.drawBox(matrices, consumer, box, color[0], color[1], color[2], 1.0f);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
