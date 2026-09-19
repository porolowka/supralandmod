package ru.supraland;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Set;

public class LinkingToolItem extends Item {

    public LinkingToolItem(Settings settings) {
        super(settings);
    }

    private String blockName(Block block) {
        if (block instanceof SupralandButtonBlock) return "Кнопка";
        if (block instanceof LaserReceiverBlock) return "Приёмник";
        if (block instanceof SupralandDoorBlock) return "Дверь";
        return "Блок";
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (world.isClient) {
            if (LinkData.getClientFirstSelection() == null) {
                LinkData.setClientFirstSelection(pos);
            } else {
                LinkData.clearClientFirstSelection();
            }
            return ActionResult.SUCCESS;
        }

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        boolean isButton = block instanceof SupralandButtonBlock;
        boolean isReceiver = block instanceof LaserReceiverBlock;
        boolean isDoor = block instanceof SupralandDoorBlock;

        if (!isButton && !isReceiver && !isDoor) {
            player.sendMessage(Text.literal("§cЭтот блок нельзя связать."), true);
            return ActionResult.FAIL;
        }

        NbtCompound nbt = stack.getOrCreateNbt();

        if (!nbt.contains("first_block")) {
            nbt.putLong("first_block", pos.asLong());
            player.sendMessage(Text.literal("§dВыбран: " + blockName(block) + " (" + pos.toShortString() + ")"), true);
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 1.0f, 1.2f);
        } else {
            BlockPos first = BlockPos.fromLong(nbt.getLong("first_block"));
            nbt.remove("first_block");

            if (first.equals(pos)) {
                player.sendMessage(Text.literal("§eВыбор сброшен."), true);
                return ActionResult.SUCCESS;
            }

            BlockState firstState = world.getBlockState(first);
            Block firstBlock = firstState.getBlock();

            boolean firstIsButton = firstBlock instanceof SupralandButtonBlock;
            boolean firstIsReceiver = firstBlock instanceof LaserReceiverBlock;
            boolean firstIsDoor = firstBlock instanceof SupralandDoorBlock;

            boolean valid = false;
            String error = "";

            if (isButton && firstIsDoor) { valid = true; }
            else if (isDoor && firstIsButton) { valid = true; }
            else if (isReceiver && firstIsDoor) { valid = true; }
            else if (isDoor && firstIsReceiver) { valid = true; }
            else if (isButton && firstIsButton) {
                error = "§cКнопку нельзя связать с кнопкой. Только: Кнопка→Дверь или Приёмник→Дверь.";
            } else if (isReceiver && firstIsReceiver) {
                error = "§cПриёмник нельзя связать с приёмником.";
            } else if (isDoor && firstIsDoor) {
                error = "§cДверь нельзя связать с дверью.";
            } else if ((isButton && firstIsReceiver) || (isReceiver && firstIsButton)) {
                error = "§cКнопку нельзя связать с приёмником.";
            } else {
                error = "§cНеподдерживаемая комбинация.";
            }

            if (valid) {
                LinkData.addLink(first, pos);
                LinkSync.sendAdd((ServerPlayerEntity) player, first, pos);
                player.sendMessage(Text.literal("§aСвязано: " + blockName(firstBlock) + " → " + blockName(block)), true);
                player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.0f, 1.5f);
            } else {
                player.sendMessage(Text.literal(error), true);
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.PLAYERS, 0.5f, 0.5f);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) {
            LinkData.clearClientFirstSelection();
            return TypedActionResult.success(stack);
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.contains("first_block")) {
            nbt.remove("first_block");
            player.sendMessage(Text.literal("§eВыбор сброшен."), true);
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 0.5f, 0.8f);
        }

        return TypedActionResult.success(stack);
    }

    public static void toggleDoor(World world, BlockPos receiverPos) {
        Set<BlockPos> targets = LinkData.getLinks(receiverPos);
        for (BlockPos doorPos : targets) {
            BlockState doorState = world.getBlockState(doorPos);
            if (doorState.getBlock() instanceof SupralandDoorBlock) {
                toggleDoorState(world, doorPos, doorState);
            }
        }
    }

    public static void toggleDoorFromButton(World world, BlockPos buttonPos) {
        Set<BlockPos> targets = LinkData.getLinks(buttonPos);
        for (BlockPos doorPos : targets) {
            BlockState doorState = world.getBlockState(doorPos);
            if (doorState.getBlock() instanceof SupralandDoorBlock) {
                toggleDoorState(world, doorPos, doorState);
            }
        }
    }

    private static void toggleDoorState(World world, BlockPos doorPos, BlockState doorState) {
        boolean isOpen = doorState.get(DoorBlock.OPEN);
        world.setBlockState(doorPos, doorState.with(DoorBlock.OPEN, !isOpen), 3);
        BlockState upperState = world.getBlockState(doorPos.up());
        if (upperState.getBlock() instanceof SupralandDoorBlock) {
            world.setBlockState(doorPos.up(), upperState.with(DoorBlock.OPEN, !isOpen), 3);
        }
        world.playSound(null, doorPos,
            isOpen ? SoundEvents.BLOCK_IRON_DOOR_CLOSE : SoundEvents.BLOCK_IRON_DOOR_OPEN,
            SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}
