package ru.supraland;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

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

    public static void register() {
        EntityType<RedCrystalProjectileEntity> type = EntityType.Builder
            .<RedCrystalProjectileEntity>create(RedCrystalProjectileEntity::new, net.minecraft.entity.SpawnGroup.MISC)
            .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
            .build();
        Registry.register(Registries.ENTITY_TYPE, new Identifier(SupralandMod.MOD_ID, "red_crystal_ball"), type);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (entityHit.getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) entityHit.getEntity();
                target.damage(this.getDamageSources().mobProjectile(this, (LivingEntity) getOwner()), damage);
            }
        }

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            BlockPos pos = blockHit.getBlockPos();
            BlockState state = getWorld().getBlockState(pos);
            if (state.getBlock() instanceof LaserReceiverBlock) {
                LaserReceiverBlockEntity be = (LaserReceiverBlockEntity) getWorld().getBlockEntity(pos);
                if (be != null) {
                    be.activate();
                }
            }
        }

        for (int i = 0; i < 8; i++) {
            getWorld().addParticle(ParticleTypes.CRIMSON_SPORE,
                getX(), getY(), getZ(),
                random.nextGaussian() * 0.1, random.nextGaussian() * 0.1, random.nextGaussian() * 0.1);
        }

        this.discard();
    }
}
