package ru.supraland;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class LaserReceiverBlockEntity extends BlockEntity {
    private List<BlockPos> linkedBlocks = new ArrayList<>();
    private int activeTime = 0;

    public LaserReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(SupralandMod.LASER_RECEIVER_BE, pos, state);
    }

    public void activate() {
        if (world == null) return;
        activeTime = 20; // 1 секунда активации

        // Частицы
        for (int i = 0; i < 10; i++) {
            world.addParticle(ParticleTypes.REDSTONE,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                world.random.nextGaussian() * 0.3, world.random.nextGaussian() * 0.3, world.random.nextGaussian() * 0.3);
        }

        world.playSound(null, pos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);

        // Активируем связанные блоки
        for (BlockPos linked : linkedBlocks) {
            BlockState state = world.getBlockState(linked);
            if (state.getBlock() instanceof SupralandDoorBlock) {
                ((SupralandDoorBlock) state.getBlock()).openDoor(world, linked, state);
            } else if (state.getBlock() instanceof SupralandButtonBlock) {
                ((SupralandButtonBlock) state.getBlock()).press(world, linked);
            }
        }

        markDirty();
    }

    public void linkTo(BlockPos target) {
        if (!linkedBlocks.contains(target)) {
            linkedBlocks.add(target);
            markDirty();
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("activeTime", activeTime);
        nbt.putInt("linkCount", linkedBlocks.size());
        for (int i = 0; i < linkedBlocks.size(); i++) {
            BlockPos p = linkedBlocks.get(i);
            nbt.putInt("link" + i + "x", p.getX());
            nbt.putInt("link" + i + "y", p.getY());
            nbt.putInt("link" + i + "z", p.getZ());
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        activeTime = nbt.getInt("activeTime");
        linkedBlocks.clear();
        int count = nbt.getInt("linkCount");
        for (int i = 0; i < count; i++) {
            linkedBlocks.add(new BlockPos(
                nbt.getInt("link" + i + "x"),
                nbt.getInt("link" + i + "y"),
                nbt.getInt("link" + i + "z")
            ));
        }
    }
}
