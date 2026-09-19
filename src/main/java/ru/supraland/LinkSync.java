package ru.supraland;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import java.util.*;

public class LinkSync {
    public static final Identifier LINK_PACKET = new Identifier(SupralandMod.MOD_ID, "link_sync");

    public static final int ACTION_ADD = 0;
    public static final int ACTION_REMOVE = 1;
    public static final int ACTION_CLEAR = 2;
    public static final int ACTION_FULL_SYNC = 3;

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(LINK_PACKET, (server, player, handler, buf, responseSender) -> {
            int action = buf.readVarInt();
            if (action == ACTION_FULL_SYNC) {
                server.execute(() -> sendFullSync(player));
            }
        });
    }

    public static void sendAdd(ServerPlayerEntity player, BlockPos a, BlockPos b) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(ACTION_ADD);
        buf.writeLong(a.asLong());
        buf.writeLong(b.asLong());
        ServerPlayNetworking.send(player, LINK_PACKET, buf);
    }

    public static void sendRemove(ServerPlayerEntity player, BlockPos pos) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(ACTION_REMOVE);
        buf.writeLong(pos.asLong());
        ServerPlayNetworking.send(player, LINK_PACKET, buf);
    }

    public static void sendFullSync(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(ACTION_FULL_SYNC);

        List<long[]> pairs = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        for (var entry : LinkData.getAllServerLinks().entrySet()) {
            for (BlockPos target : entry.getValue()) {
                long lo = Math.min(entry.getKey().asLong(), target.asLong());
                long hi = Math.max(entry.getKey().asLong(), target.asLong());
                long hash = lo * 73856093L ^ hi;
                if (seen.add(hash)) {
                    pairs.add(new long[]{entry.getKey().asLong(), target.asLong()});
                }
            }
        }

        buf.writeVarInt(pairs.size());
        for (long[] pair : pairs) {
            buf.writeLong(pair[0]);
            buf.writeLong(pair[1]);
        }

        ServerPlayNetworking.send(player, LINK_PACKET, buf);
    }
}
