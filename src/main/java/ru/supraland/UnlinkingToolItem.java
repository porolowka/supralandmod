package ru.supraland;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UnlinkingToolItem extends Item {

    public UnlinkingToolItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        if (world.isClient) return ActionResult.SUCCESS;

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        boolean isButton = block instanceof SupralandButtonBlock;
        boolean isReceiver = block instanceof LaserReceiverBlock;
        boolean isDoor = block instanceof SupralandDoorBlock;

        if (!isButton && !isReceiver && !isDoor) {
            player.sendMessage(Text.literal("§cЭтот блок не может иметь связей."), true);
            return ActionResult.FAIL;
        }

        if (!LinkData.hasLinks(pos)) {
            player.sendMessage(Text.literal("§cУ этого блока нет связей."), true);
            return ActionResult.FAIL;
        }

        LinkData.removeLinks(pos);
        LinkSync.sendRemove((ServerPlayerEntity) player, pos);

        player.sendMessage(Text.literal("§aВсе связи удалены."), true);
        player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.PLAYERS, 0.5f, 1.5f);

        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack);

        player.sendMessage(Text.literal("§eКликни по связанному блоку, чтобы отвязать его."), true);
        return TypedActionResult.success(stack);
    }
}
