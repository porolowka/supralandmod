package ru.supraland;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class LinkSyncClient {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(LinkSync.LINK_PACKET, (client, handler, buf, responseSender) -> {
            int action = buf.readVarInt();

            // Читаем все данные из буфера СЕЙЧАС, пока он не освобождён
            switch (action) {
                case LinkSync.ACTION_ADD: {
                    BlockPos a = BlockPos.fromLong(buf.readLong());
                    BlockPos b = BlockPos.fromLong(buf.readLong());
                    client.execute(() -> LinkData.clientAddLink(a, b));
                    break;
                }
                case LinkSync.ACTION_REMOVE: {
                    BlockPos pos = BlockPos.fromLong(buf.readLong());
                    client.execute(() -> LinkData.clientRemoveLinks(pos));
                    break;
                }
                case LinkSync.ACTION_CLEAR: {
                    client.execute(LinkData::clientClearAll);
                    break;
                }
                case LinkSync.ACTION_FULL_SYNC: {
                    int count = buf.readVarInt();
                    long[] pairs = new long[count * 2];
                    for (int i = 0; i < count * 2; i++) {
                        pairs[i] = buf.readLong();
                    }
                    client.execute(() -> {
                        LinkData.clientClearAll();
                        for (int i = 0; i < count; i++) {
                            BlockPos a = BlockPos.fromLong(pairs[i * 2]);
                            BlockPos b = BlockPos.fromLong(pairs[i * 2 + 1]);
                            LinkData.clientAddLink(a, b);
                        }
                    });
                    break;
                }
            }
        });
    }
}
