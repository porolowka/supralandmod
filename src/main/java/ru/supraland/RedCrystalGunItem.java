package ru.supraland;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class RedCrystalGunItem extends Item {
    private long lastShotTime = 0;

    public RedCrystalGunItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack);

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        long currentTime = world.getTime();
        long cooldown = (long) (20 / data.fireRateMultiplier);

        if (currentTime - lastShotTime < cooldown) {
            return TypedActionResult.pass(stack);
        }

        lastShotTime = currentTime;

        // Стреляем шариком
        RedCrystalProjectileEntity projectile = new RedCrystalProjectileEntity(world, player);
        projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, 1.5f, 1.0f);
        float damage = 5.0f * (float) data.gunDamageMultiplier;
        projectile.setDamage(damage);
        world.spawnEntity(projectile);

        // Если есть лазер — стреляем лазером
        if (data.hasLaser) {
            LaserBeamEntity.shootLaser(world, player, damage * 0.2f);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.5f);

        return TypedActionResult.success(stack);
    }
}
