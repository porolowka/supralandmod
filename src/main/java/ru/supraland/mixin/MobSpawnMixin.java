package ru.supraland.mixin;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntityAccessor;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobSpawnMixin {

    @Inject(method = "canMobSpawn", at = @At("HEAD"), cancellable = true)
    private static void onMobSpawn(net.minecraft.entity.EntityType<?> type, WorldAccess world,
                                   net.minecraft.block.BlockState state, net.minecraft.util.math.BlockPos pos,
                                   SpawnReason spawnReason, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        // Отменяем спавн ВСЕХ ванильных мобов
        cir.setReturnValue(false);
    }
}
