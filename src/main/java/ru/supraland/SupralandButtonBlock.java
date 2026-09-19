package ru.supraland;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
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

public class SupralandButtonBlock extends ButtonBlock {

    public SupralandButtonBlock(Settings settings) {
        super(settings, BlockSetType.OAK, 20, false);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(POWERED)) return ActionResult.CONSUME;
        if (world.isClient) return ActionResult.SUCCESS;

        world.setBlockState(pos, state.with(POWERED, true), 3);
        world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3f, 0.6f);
        world.scheduleBlockTick(pos, this, 20);

        LinkingToolItem.activateLinkedButton(world, pos);

        return ActionResult.CONSUME;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, false), 3);
            world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3f, 0.5f);
        }
    }

    public static void pressButton(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;
        if (state.get(POWERED)) return;

        world.setBlockState(pos, state.with(POWERED, true), 3);
        world.playSound(null, pos, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3f, 0.6f);
        world.scheduleBlockTick(pos, state.getBlock(), 20);
    }
}
