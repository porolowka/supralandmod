package ru.supraland.mixin;

import ru.supraland.PlayerUpgradeData;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerLandMixin {

    // Сброс счётчика прыжков при касании земли
    @Inject(method = "onLanding", at = @At("HEAD"))
    private void onLanding(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) return;

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data != null && data.maxJumps > 1) {
            data.jumpsRemaining = data.maxJumps;
        }
    }
}
