package ru.supraland;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SupralandButtonBlock extends AbstractButtonBlock {

    public SupralandButtonBlock(Settings settings) {
        super(BlockSetType.OAK, settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Если уже нажата — ничего не делаем
        if (state.get(POWERED)) {
            return ActionResult.CONSUME;
        }

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        // Нажимаем кнопку (визуально + звук)
        world.setBlockState(pos, state.with(POWERED, true), 3);
        world.playSound(null, pos,
            SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON,
            SoundCategory.BLOCKS, 0.3f, 0.6f);
        world.scheduleBlockTick(pos, this, 20);

        // Активируем связанную кнопку
        LinkingToolItem.activateLinkedButton(world, pos);

        return ActionResult.CONSUME;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, false), 3);
            world.playSound(null, pos,
                SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF,
                SoundCategory.BLOCKS, 0.3f, 0.5f);
        }
    }

    // Программное нажатие кнопки (вызывается из LinkingToolItem)
    public static void pressButton(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        if (state.get(POWERED)) return;

        world.setBlockState(pos, state.with(POWERED, true), 3);
        world.playSound(null, pos,
            SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON,
            SoundCategory.BLOCKS, 0.3f, 0.6f);
        world.scheduleBlockTick(pos, state.getBlock(), 20);
    }
}
