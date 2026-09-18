package ru.supraland;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SupralandButtonBlock extends Block {
    public static final BooleanProperty PRESSED = BooleanProperty.of("pressed");

    protected SupralandButtonBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(PRESSED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PRESSED);
    }

    public void press(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(PRESSED, true));
        world.updateNeighbors(pos, this);
        // Автовозврат через 2 секунды
        world.createAndScheduleBlockTick(pos, this, 40);
    }

    @Override
    public void scheduledTick(BlockState state, net.minecraft.server.world.ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (state.get(PRESSED)) {
            world.setBlockState(pos, state.with(PRESSED, false));
            world.updateNeighbors(pos, this);
        }
    }
}
