package ru.supraland.mixin;

import ru.supraland.PlayerUpgradeData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class PlayerLandMixin {

    @Inject(method = "onLanding", at = @At("HEAD"))
    private void onLanding(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) self;
        if (player.getWorld().isClient) return;

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data != null && data.maxJumps > 1) {
            data.jumpsRemaining = data.maxJumps;
        }
    }
}

