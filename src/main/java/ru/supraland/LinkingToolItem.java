package ru.supraland;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LinkingToolItem extends Item {
    private static BlockPos selectedReceiver = null;

    public LinkingToolItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (world.isClient) return ActionResult.SUCCESS;

        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof LaserReceiverBlock) {
            selectedReceiver = pos.toImmutable();
            if (player != null) {
                player.sendMessage(Text.literal("Приёмник выбран. Теперь кликните по двери или кнопке."), true);
            }
            drawLineParticles(world, pos, player);
            return ActionResult.SUCCESS;
        }

        if (selectedReceiver != null) {
            if (state.getBlock() instanceof SupralandDoorBlock || state.getBlock() instanceof SupralandButtonBlock) {
                BlockEntity be = world.getBlockEntity(selectedReceiver);
                if (be instanceof LaserReceiverBlockEntity) {
                    ((LaserReceiverBlockEntity) be).linkTo(pos.toImmutable());
                    if (player != null) {
                        player.sendMessage(Text.literal("Связано! Приёмник -> " + pos.getX() + "," + pos.getY() + "," + pos.getZ()), true);
                    }
                    drawLineParticles(world, selectedReceiver, player);
                }
                selectedReceiver = null;
                return ActionResult.SUCCESS;
            }
        }

        if (player != null) {
            player.sendMessage(Text.literal("Сначала выберите приёмник (правый клик по приёмнику), потом по двери/кнопке."), true);
        }

        return ActionResult.PASS;
    }

    private void drawLineParticles(World world, BlockPos from, PlayerEntity player) {
        if (player == null) return;
        BlockPos to = player.getBlockPos();
        double dx = (to.getX() - from.getX());
        double dy = (to.getY() - from.getY());
        double dz = (to.getZ() - from.getZ());
        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
        int steps = (int) (dist * 4);
        for (int i = 0; i < steps; i++) {
            double t = (double) i / steps;
            world.addParticle(ParticleTypes.END_ROD,
                from.getX() + 0.5 + dx * t,
                from.getY() + 0.5 + dy * t,
                from.getZ() + 0.5 + dz * t,
                0, 0, 0);
        }
    }
}
