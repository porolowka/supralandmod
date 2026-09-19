package ru.supraland;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import java.util.List;

public class RedCrystalProjectileEntity extends SnowballEntity {
    private float damage = 5.0f;

    public RedCrystalProjectileEntity(EntityType<? extends SnowballEntity> entityType, World world) {
        super(entityType, world);
    }

    public RedCrystalProjectileEntity(World world, LivingEntity owner) {
        super(world, owner);
    }

    public void setDamage(float damage) { this.damage = damage; }
    public float getDamage() { return damage; }

    @Override
    protected float getGravity() {
        return 0.0f;
    }

    @Override
    public void tick() {
        this.setNoGravity(true);
        Vec3d vel = this.getVelocity();
        super.tick();
        this.setVelocity(vel.x, vel.y, vel.z);
        
        Entity owner = getOwner();
        if (owner != null && this.squaredDistanceTo(owner) > 262144) {
            this.discard();
        }
    }

    public static void register() {
        EntityType<RedCrystalProjectileEntity> type = FabricEntityTypeBuilder
            .<RedCrystalProjectileEntity>create(SpawnGroup.MISC, (EntityType.EntityFactory<RedCrystalProjectileEntity>) RedCrystalProjectileEntity::new)
            .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
            .trackRangeBlocks(64)
            .trackedUpdateRate(1)
            .build();
        Registry.register(Registries.ENTITY_TYPE, new Identifier(SupralandMod.MOD_ID, "red_crystal_ball"), type);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        World world = this.getWorld();
        double x = getX();
        double y = getY();
        double z = getZ();

        // Прямой удар по entity
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (entityHit.getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) entityHit.getEntity();
                target.damage(this.getDamageSources().mobProjectile(this, (LivingEntity) getOwner()), damage);
            }
        }

        // Прямой удар по блоку — активируем приёмник
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            BlockPos pos = blockHit.getBlockPos();
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof LaserReceiverBlock) {
                activateReceiver(world, pos);
            }
        }

        // Красный импульс — частицы во все стороны
        spawnPulse(world, x, y, z);

        // Импульс активирует приёмники в радиусе 2 блоков
        BlockPos center = BlockPos.ofFloored(x, y, z);
        int radius = 2;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    BlockState state = world.getBlockState(pos);
                    if (state.getBlock() instanceof LaserReceiverBlock) {
                        activateReceiver(world, pos);
                    }
                }
            }
        }

        // Импульс ранит мобов в радиусе 2 блоков (блоки НЕ ломает)
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class,
            new Box(x - 2, y - 2, z - 2, x + 2, y + 2, z + 2),
            e -> e != getOwner() && e.isAlive());
        for (LivingEntity entity : entities) {
            entity.damage(this.getDamageSources().mobProjectile(this, (LivingEntity) getOwner()), damage * 0.5f);
        }

        this.discard();
    }

    private static void activateReceiver(World world, BlockPos pos) {
        LaserReceiverBlockEntity be = (LaserReceiverBlockEntity) world.getBlockEntity(pos);
        if (be != null) {
            be.activate();
        }
    }

    private void spawnPulse(World world, double x, double y, double z) {
        DustParticleEffect redDust = new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f), 2.5f);
        
        if (world instanceof ServerWorld serverWorld) {
            // 40 красных частиц во все стороны — импульс
            serverWorld.spawnParticles(redDust, x, y, z, 40, 1.0, 1.0, 1.0, 0.5);
            // Мелкие красные частицы для объёма
            serverWorld.spawnParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 20, 0.8, 0.8, 0.8, 0.3);
        } else {
            for (int i = 0; i < 40; i++) {
                double dx = (random.nextDouble() - 0.5) * 2.0;
                double dy = (random.nextDouble() - 0.5) * 2.0;
                double dz = (random.nextDouble() - 0.5) * 2.0;
                world.addParticle(redDust, x, y, z, dx, dy, dz);
            }
            for (int i = 0; i < 20; i++) {
                double dx = (random.nextDouble() - 0.5) * 1.6;
                double dy = (random.nextDouble() - 0.5) * 1.6;
                double dz = (random.nextDouble() - 0.5) * 1.6;
                world.addParticle(ParticleTypes.CRIMSON_SPORE, x, y, z, dx, dy, dz);
            }
        }
    }
}
