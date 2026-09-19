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
            client.execute(() -> {
                switch (action) {
                    case LinkSync.ACTION_ADD: {
                        BlockPos a = BlockPos.fromLong(buf.readLong());
                        BlockPos b = BlockPos.fromLong(buf.readLong());
                        LinkData.clientAddLink(a, b);
                        break;
                    }
                    case LinkSync.ACTION_REMOVE: {
                        BlockPos pos = BlockPos.fromLong(buf.readLong());
                        LinkData.clientRemoveLinks(pos);
                        break;
                    }
                    case LinkSync.ACTION_CLEAR: {
                        LinkData.clientClearAll();
                        break;
                    }
                    case LinkSync.ACTION_FULL_SYNC: {
                        LinkData.clientClearAll();
                        int count = buf.readVarInt();
                        for (int i = 0; i < count; i++) {
                            BlockPos a = BlockPos.fromLong(buf.readLong());
                            BlockPos b = BlockPos.fromLong(buf.readLong());
                            LinkData.clientAddLink(a, b);
                        }
                        break;
                    }
                }
            });
        });
    }
}
