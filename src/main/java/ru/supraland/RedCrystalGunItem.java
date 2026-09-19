package ru.supraland;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RaycastContext;
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

    // Левый клик — шарик (вызывается сервером через пакет)
    public static void shootBall(PlayerEntity player) {
        World world = player.getWorld();
        if (world.isClient) return;

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data == null) return;

        long currentTime = world.getTime();
        long cooldown = (long) (20 / data.fireRateMultiplier);
        long lastShot = lastShotTimes.getOrDefault(player.getUuid(), 0L);
        if (currentTime - lastShot < cooldown) return;
        lastShotTimes.put(player.getUuid(), currentTime);

        float damage = 5.0f * (float) data.gunDamageMultiplier;

        RedCrystalProjectileEntity projectile = new RedCrystalProjectileEntity(world, player);
        projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, 1.3f, 0.0f);
        projectile.setDamage(damage);
        world.spawnEntity(projectile);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.5f);
    }

    // Правый клик — ТОЛЬКО лазер
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack);

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data == null) return TypedActionResult.fail(stack);

        // Нет лазера — правый клик ничего не делает
        if (!data.hasLaser) {
            return TypedActionResult.pass(stack);
        }

        long currentTime = world.getTime();
        long cooldown = (long) (20 / data.fireRateMultiplier);
        long lastShot = lastShotTimes.getOrDefault(player.getUuid(), 0L);
        if (currentTime - lastShot < cooldown) {
            return TypedActionResult.pass(stack);
        }
        lastShotTimes.put(player.getUuid(), currentTime);

        float damage = 5.0f * (float) data.gunDamageMultiplier;

        // Видимый лазер — raycast + частицы вдоль луча
        spawnLaserParticles(world, player);
        LaserBeamEntity.shootLaser(world, player, damage * 0.2f);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0f, 2.0f);

        return TypedActionResult.success(stack);
    }

    private static void spawnLaserParticles(World world, PlayerEntity player) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Vec3d start = player.getEyePos();
        Vec3d look = player.getRotationVec(1.0f);
        Vec3d end = start.add(look.multiply(64));

        // Raycast — находим точку попадания
        BlockHitResult hit = serverWorld.raycast(new RaycastContext(start, end,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            player));

        Vec3d actualEnd = hit.getType() == HitResult.Type.BLOCK ? hit.getPos() : end;
        double distance = start.distanceTo(actualEnd);
        int particleCount = (int) (distance * 4); // 4 частицы на блок

        for (int i = 0; i <= particleCount; i++) {
            double t = (double) i / particleCount;
            double x = start.x + (actualEnd.x - start.x) * t;
            double y = start.y + (actualEnd.y - start.y) * t;
            double z = start.z + (actualEnd.z - start.z) * t;
            serverWorld.spawnParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
