package ru.supraland;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
public class SupralandModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LinkSyncClient.register();
        LinkRenderer.register();
    }
}
