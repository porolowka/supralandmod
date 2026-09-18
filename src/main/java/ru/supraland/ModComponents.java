package ru.supraland;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class ModComponents implements EntityComponentInitializer {
    public static final ComponentKey<PlayerUpgradeData> UPGRADE_DATA =
        ComponentRegistry.getOrCreate(new Identifier(SupralandMod.MOD_ID, "upgrade_data"), PlayerUpgradeData.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(UPGRADE_DATA, player -> new PlayerUpgradeData());
    }
}
