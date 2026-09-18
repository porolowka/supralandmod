package ru.supraland.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SupralandNpcEntity extends PathAwareEntity {

    private final float[] color;

    public SupralandNpcEntity(EntityType<? extends PathAwareEntity> entityType, World world, float r, float g, float b) {
        super(entityType, world);
        this.color = new float[]{r, g, b};
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    public static DefaultAttributeContainer.Builder createNpcAttributes() {
        return PathAwareEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 1.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.world.isClient) {
            // Звук удара мячом
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_SLIME_ATTACK, net.minecraft.sound.SoundCategory.NEUTRAL, 1.0f, 1.5f);
            // Вздрёг — небольшая отдача
            this.setVelocity(0, 0.05, 0);
            this.velocityModified = true;
        }
        return false;
    }

    @Override
    public void tick() {
        // Не двигается вообще, не падает
        this.setVelocity(0, 0, 0);
        super.tick();
        this.setVelocity(0, 0, 0);
    }

    @Override
    public boolean canMoveVoluntarily() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldRenderName() {
        return false;
    }

    @Override
    public void lookAt(net.minecraft.entity.Entity target, float maxYawChange, float maxPitchChange) {
        // Не поворачивается — смотрит в одну точку
    }

    @Override
    public void tickMovement() {
        // Ничего не делаем — стоит на месте
    }

    public float[] getColor() {
        return color;
    }
}
