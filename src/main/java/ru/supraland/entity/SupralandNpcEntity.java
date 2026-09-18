package ru.supraland.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SupralandNpcEntity extends Entity {

    private final float[] color;

    public SupralandNpcEntity(EntityType<SupralandNpcEntity> type, World world, float r, float g, float b) {
        super(type, world);
        this.color = new float[]{r, g, b};
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_SLIME_ATTACK, SoundCategory.NEUTRAL, 1.0f, 1.5f);
        }
        return false;
    }

    @Override
    public void tick() {
        // NPC стоит на месте
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
    public boolean collides() {
        return true;
    }

    public float[] getColor() {
        return color;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }
}
