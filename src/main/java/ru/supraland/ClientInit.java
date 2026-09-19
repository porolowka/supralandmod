package ru.supraland;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import ru.supraland.client.SupralandNpcRenderer;

public class ClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            CoinHud.render(drawContext);
        });

        EntityRendererRegistry.register(SupralandMod.RED_NPC, SupralandNpcRenderer::new);
        EntityRendererRegistry.register(SupralandMod.BLUE_NPC, SupralandNpcRenderer::new);
    }
}
