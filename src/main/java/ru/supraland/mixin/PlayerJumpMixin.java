package ru.supraland.mixin;

import ru.supraland.PlayerUpgradeData;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerJumpMixin {

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) return;

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data == null) return;

        // Высота прыжка (счастье)
        if (data.jumpHeightMultiplier > 1.0) {
            // Minecraft: jump() устанавливает velocity.y = 0.42
            // Мы умножаем на множитель счастья
            player.setVelocity(player.getVelocity().x, player.getVelocity().y * data.jumpHeightMultiplier, player.getVelocity().z);
            player.velocityModified = true;
        }

        // Множественные прыжки
        if (data.maxJumps > 1 && data.jumpsRemaining > 0) {
            data.jumpsRemaining--;
            if (data.jumpsRemaining > 0) {
                // Не отменяем стандартный прыжок, позволяем дополнительный
                player.setVelocity(player.getVelocity().x, 0.42, player.getVelocity().z);
                player.velocityModified = true;
            }
        }
    }
}
