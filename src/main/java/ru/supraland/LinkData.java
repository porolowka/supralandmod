package ru.supraland;

import net.minecraft.util.math.BlockPos;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LinkData {
    private static final Map<BlockPos, Set<BlockPos>> serverLinks = new ConcurrentHashMap<>();
    private static final Map<BlockPos, Set<BlockPos>> clientLinks = new ConcurrentHashMap<>();
    private static BlockPos clientFirstSelection = null;

    public static void addLink(BlockPos a, BlockPos b) {
        serverLinks.computeIfAbsent(a, k -> ConcurrentHashMap.newKeySet()).add(b);
        serverLinks.computeIfAbsent(b, k -> ConcurrentHashMap.newKeySet()).add(a);
    }

    public static void removeLinks(BlockPos pos) {
        Set<BlockPos> targets = serverLinks.remove(pos);
        if (targets != null) {
            for (BlockPos t : targets) {
                Set<BlockPos> reverse = serverLinks.get(t);
                if (reverse != null) {
                    reverse.remove(pos);
                    if (reverse.isEmpty()) serverLinks.remove(t);
                }
            }
        }
    }

    public static Set<BlockPos> getLinks(BlockPos pos) {
        Set<BlockPos> s = serverLinks.get(pos);
        return s != null ? new HashSet<>(s) : Collections.emptySet();
    }

    public static boolean hasLinks(BlockPos pos) {
        return serverLinks.containsKey(pos) && !serverLinks.get(pos).isEmpty();
    }

    public static Map<BlockPos, Set<BlockPos>> getAllServerLinks() {
        return serverLinks;
    }

    public static void clientAddLink(BlockPos a, BlockPos b) {
        clientLinks.computeIfAbsent(a, k -> ConcurrentHashMap.newKeySet()).add(b);
        clientLinks.computeIfAbsent(b, k -> ConcurrentHashMap.newKeySet()).add(a);
    }

    public static void clientRemoveLinks(BlockPos pos) {
        Set<BlockPos> targets = clientLinks.remove(pos);
        if (targets != null) {
            for (BlockPos t : targets) {
                Set<BlockPos> reverse = clientLinks.get(t);
                if (reverse != null) {
                    reverse.remove(pos);
                    if (reverse.isEmpty()) clientLinks.remove(t);
                }
            }
        }
    }

    public static void clientClearAll() {
        clientLinks.clear();
    }

    public static Map<BlockPos, Set<BlockPos>> getAllClientLinks() {
        return clientLinks;
    }

    public static void setClientFirstSelection(BlockPos pos) { clientFirstSelection = pos; }
    public static BlockPos getClientFirstSelection() { return clientFirstSelection; }
    public static void clearClientFirstSelection() { clientFirstSelection = null; }
}
