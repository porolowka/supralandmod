package ru.supraland;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RedCrystalGunItem extends Item {
    private static final Map<UUID, Long> lastShotTimes = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> lastLaserTimes = new ConcurrentHashMap<>();

    private static final long LASER_COOLDOWN = 3;
    private static final float LASER_DAMAGE = 1.0f;

    public RedCrystalGunItem(Settings settings) {
        super(settings);
    }

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

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack);

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data == null) return TypedActionResult.fail(stack);

        if (!data.hasLaser) {
            return TypedActionResult.pass(stack);
        }

        long currentTime = world.getTime();
        long lastLaser = lastLaserTimes.getOrDefault(player.getUuid(), 0L);
        if (currentTime - lastLaser < LASER_COOLDOWN) {
            return TypedActionResult.pass(stack);
        }
        lastLaserTimes.put(player.getUuid(), currentTime);

        Vec3d start = player.getEyePos();
        HitResult hit = player.raycast(64.0, 1.0f, false);

        Vec3d actualEnd;
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            actualEnd = hit.getPos();

            BlockPos pos = blockHit.getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof LaserReceiverBlock) {
                LaserReceiverBlockEntity be = (LaserReceiverBlockEntity) world.getBlockEntity(pos);
                if (be != null) {
                    be.activate();
                }
            }
        } else {
            Vec3d look = player.getRotationVec(1.0f);
            actualEnd = start.add(look.multiply(64));
        }

        spawnLaserParticles(world, start, actualEnd);
        LaserBeamEntity.shootLaser(world, player, LASER_DAMAGE);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.3f, 2.0f);

        return TypedActionResult.success(stack);
    }

    private static void spawnLaserParticles(World world, Vec3d start, Vec3d actualEnd) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        double distance = start.distanceTo(actualEnd);
        int particleCount = Math.max(1, (int) (distance * 4));

        DustParticleEffect redDust = new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f), 1.0f);

        for (int i = 0; i <= particleCount; i++) {
            double t = (double) i / particleCount;
            double x = start.x + (actualEnd.x - start.x) * t;
            double y = start.y + (actualEnd.y - start.y) * t;
            double z = start.z + (actualEnd.z - start.z) * t;
            serverWorld.spawnParticles(redDust, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
