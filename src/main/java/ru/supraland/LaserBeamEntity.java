package ru.supraland;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.block.BlockState;

public class LaserBeamEntity {

    public static void shootLaser(World world, PlayerEntity player, float damage) {
        Vec3d start = player.getCameraPosVec(1.0f);
        Vec3d dir = player.getRotationVec(1.0f);

        // Raycast — лазер проходит сквозь стены к приёмникам, но мы ищем ближайший приёмник
        Vec3d end = start.add(dir.multiply(64));

        // Сначала ищем приёмник по лучу (игнорируем стены)
        BlockPos hitReceiver = findReceiverAlongRay(world, start, end);

        if (hitReceiver != null) {
            LaserReceiverBlockEntity be = (LaserReceiverBlockEntity) world.getBlockEntity(hitReceiver);
            if (be != null) {
                be.activate();
            }
        }

        // Также наносим урон сущностям на пути
        // Проверяем попадание в шарики (RedCrystalProjectileEntity) — взрыв
        world.getEntitiesByClass(RedCrystalProjectileEntity.class,
            player.getBoundingBox().expand(64), e -> true).forEach(proj -> {
            Vec3d projPos = proj.getPos();
            if (isOnRay(start, dir, projPos, 1.5)) {
                world.createExplosion(null, projPos.x, projPos.y, projPos.z, 3.0f,
                    World.ExplosionSourceType.TNT);
                proj.discard();
            }
        });

        // Частицы лазера
        for (double t = 0; t < 64; t += 0.5) {
            Vec3d pos = start.add(dir.multiply(t));
            world.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }

    private static BlockPos findReceiverAlongRay(World world, Vec3d start, Vec3d end) {
        Vec3d dir = end.subtract(start).normalize();
        for (double t = 0; t < 64; t += 0.5) {
            Vec3d pos = start.add(dir.multiply(t));
            BlockPos bPos = new BlockPos((int) pos.x, (int) pos.y, (int) pos.z);
            BlockState state = world.getBlockState(bPos);
            if (state.getBlock() instanceof LaserReceiverBlock) {
                return bPos;
            }
        }
        return null;
    }

    private static boolean isOnRay(Vec3d start, Vec3d dir, Vec3d point, double tolerance) {
        Vec3d toPoint = point.subtract(start);
        double projLen = toPoint.dotProduct(dir);
        if (projLen < 0 || projLen > 64) return false;
        Vec3d closest = start.add(dir.multiply(projLen));
        return closest.distanceTo(point) < tolerance;
    }
}
