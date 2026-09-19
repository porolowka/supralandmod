package ru.supraland;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LaserReceiverBlockEntity extends BlockEntity {
    private int activeTicks = 0;

    public LaserReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(SupralandMod.LASER_RECEIVER_BE, pos, state);
    }

    // Вызывается когда шар или лазер попадает в приёмник
    public void activate() {
        if (world == null || world.isClient) return;

        // Открываем/закрываем связанную дверь
        LinkingToolItem.toggleDoor(world, pos);

        // Визуальная обратная связь — загорается на 1 секунду
        world.setBlockState(pos, getCachedState().with(LaserReceiverBlock.ACTIVE, true), 3);
        activeTicks = 20;
        markDirty();

        world.playSound(null, pos,
            SoundEvents.BLOCK_BEEHIVE_ENTER,
            SoundCategory.BLOCKS, 0.5f, 1.5f);
    }

    public static void tick(World world, BlockPos pos, BlockState state, LaserReceiverBlockEntity be) {
        if (be.activeTicks > 0) {
            be.activeTicks--;
            if (be.activeTicks == 0) {
                world.setBlockState(pos, state.with(LaserReceiverBlock.ACTIVE, false), 3);
                be.markDirty();
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("activeTicks", activeTicks);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        activeTicks = nbt.getInt("activeTicks");
    }
}
