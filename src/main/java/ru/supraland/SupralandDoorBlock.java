package ru.supraland;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SupralandDoorBlock extends Block {
    public static final BooleanProperty OPEN = BooleanProperty.of("open");

    protected SupralandDoorBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(OPEN, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
    }

    public void openDoor(World world, BlockPos pos, BlockState state) {
        if (state.get(OPEN)) return;
        world.setBlockState(pos, state.with(OPEN, true));
        world.updateNeighbors(pos, this);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            player.sendMessage(net.minecraft.text.Text.literal("Эта дверь открывается только через приёмник!"), true);
        }
        return ActionResult.SUCCESS;
    }
}
