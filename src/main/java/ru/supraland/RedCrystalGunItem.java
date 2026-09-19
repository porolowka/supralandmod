package ru.supraland;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RedCrystalGunItem extends Item {
    private static final Map<UUID, Long> lastShotTimes = new ConcurrentHashMap<>();

    public RedCrystalGunItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack);

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data == null) return TypedActionResult.fail(stack);

        long currentTime = world.getTime();
        long cooldown = (long) (20 / data.fireRateMultiplier);

        long lastShot = lastShotTimes.getOrDefault(player.getUuid(), 0L);
        if (currentTime - lastShot < cooldown) {
            return TypedActionResult.pass(stack);
        }
        lastShotTimes.put(player.getUuid(), currentTime);

        float damage = 5.0f * (float) data.gunDamageMultiplier;

        // Стреляем шариком (без гравитации, ровный полёт)
        RedCrystalProjectileEntity projectile = new RedCrystalProjectileEntity(world, player);
        projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, 3.0f, 0.0f);
        projectile.setDamage(damage);
        world.spawnEntity(projectile);

        // Если есть лазер — стреляем лазером с красными частицами
        if (data.hasLaser) {
            spawnLaserParticles(world, player);
            LaserBeamEntity.shootLaser(world, player, damage * 0.2f);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.5f);

        return TypedActionResult.success(stack);
    }

    private void spawnLaserParticles(World world, PlayerEntity player) {
        Vec3d look = player.getRotationVec(1.0f);
        for (int i = 1; i <= 30; i++) {
            double x = player.getX() + look.x * i;
            double y = player.getEyeY() + look.y * i;
            double z = player.getZ() + look.z * i;
            world.addParticle(ParticleTypes.CRIMSON_SPORE, x, y, z, 0, 0, 0);
        }
    }
}
