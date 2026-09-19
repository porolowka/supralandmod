package ru.supraland.mixin;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.supraland.RedCrystalGunItem;
import ru.supraland.SupralandMod;

@Mixin(MinecraftClient.class)
public class ClientAttackMixin {
    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void supraland$onAttack(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        if (client.player != null && client.player.getMainHandStack().getItem() instanceof RedCrystalGunItem) {
            ClientPlayNetworking.send(new Identifier(SupralandMod.MOD_ID, "gun_attack"), PacketByteBufs.create());
            cir.setReturnValue(false);
        }
    }
}
