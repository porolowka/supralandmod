package ru.supraland.mixin;

import ru.supraland.PlayerUpgradeData;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerMovementMixin {

    @Inject(method = "getMovementSpeed", at = @At("RETURN"), cancellable = true)
    private void onGetMovementSpeed(CallbackInfoReturnable<Float> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) return;

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data != null && data.speedMultiplier > 1.0) {
            cir.setReturnValue((float) (cir.getReturnValue() * data.speedMultiplier));
        }
    }
}
