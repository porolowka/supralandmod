package ru.supraland;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.HashMap;
import java.util.Map;

public class LinkingToolItem extends Item {
    private static final Map<BlockPos, BlockPos> links = new HashMap<>();

    public LinkingToolItem(Settings settings) {
        super(settings);
    }

    public static BlockPos getLinkedTarget(BlockPos from) {
        return links.get(from);
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

        if (world.isClient) return ActionResult.SUCCESS;

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // Проверяем, что это наш блок
        boolean isButton = block instanceof SupralandButtonBlock;
        boolean isReceiver = block instanceof LaserReceiverBlock;
        boolean isDoor = block instanceof SupralandDoorBlock;

        if (!isButton && !isReceiver && !isDoor) {
            player.sendMessage(Text.literal("§cЭтот блок нельзя связать."), true);
            return ActionResult.FAIL;
        }

        NbtCompound nbt = stack.getOrCreateNbt();

        if (!nbt.contains("first_block")) {
            // Первый клик — запоминаем
            nbt.putLong("first_block", pos.asLong());
            player.sendMessage(Text.literal("§dВыбран: " + blockName(block) + " (" + pos.toShortString() + ")"), true);
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 1.0f, 1.2f);
        } else {
            // Второй клик — связываем
            BlockPos first = BlockPos.fromLong(nbt.getLong("first_block"));
            nbt.remove("first_block");

            BlockState firstState = world.getBlockState(first);
            Block firstBlock = firstState.getBlock();

            boolean firstIsButton = firstBlock instanceof SupralandButtonBlock;
            boolean firstIsReceiver = firstBlock instanceof LaserReceiverBlock;
            boolean firstIsDoor = firstBlock instanceof SupralandDoorBlock;

            // Проверяем допустимые комбинации
            boolean valid = false;
            String error = "";

            // Кнопка ↔ Кнопка
            if (isButton && firstIsButton) {
                valid = true;
            }
            // Приёмник → Дверь (в любом порядке)
            else if (isReceiver && firstIsDoor) {
                valid = true;
            } else if (isDoor && firstIsReceiver) {
                valid = true;
            }
            // Нельзя: дверь↔дверь
            else if (isDoor && firstIsDoor) {
                error = "§cДверь нельзя связать с дверью.";
            }
            // Нельзя: кнопка↔приёмник
            else if ((isButton && firstIsReceiver) || (isReceiver && firstIsButton)) {
                error = "§cКнопку нельзя связать с приёмником. Кнопка→Кнопка или Приёмник→Дверь.";
            }
            // Нельзя: кнопка↔дверь
            else if ((isButton && firstIsDoor) || (isDoor && firstIsButton)) {
                error = "§cКнопку нельзя связать с дверью. Используй Приёмник→Дверь.";
            } else {
                error = "§cНеподдерживаемая комбинация.";
            }

            if (valid) {
                links.put(first, pos);
                links.put(pos, first);
                player.sendMessage(Text.literal("§aСвязано: " + blockName(firstBlock) + " → " + blockName(block)), true);
                player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.0f, 1.5f);
            } else {
                player.sendMessage(Text.literal(error), true);
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS, SoundCategory.PLAYERS, 0.5f, 0.5f);
            }
        }

        return ActionResult.SUCCESS;
    }

    // ЛКМ по воздуху — сброс
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack);

        NbtCompound nbt = stack.getOrCreateNbt();
        if (nbt.contains("first_block")) {
            nbt.remove("first_block");
            player.sendMessage(Text.literal("§eВыбор сброшен."), true);
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 0.5f, 0.8f);
        }

        return TypedActionResult.success(stack);
    }

    // Открыть дверь по связи (вызывается из приёмника)
    public static void toggleDoor(World world, BlockPos receiverPos) {
        BlockPos doorPos = links.get(receiverPos);
        if (doorPos == null) return;

        BlockState doorState = world.getBlockState(doorPos);
        if (doorState.getBlock() instanceof SupralandDoorBlock) {
            boolean isOpen = doorState.get(DoorBlock.OPEN);
            // Открываем/закрываем обе половины двери
            toggleDoorHalf(world, doorPos, doorState);
            BlockState upperState = world.getBlockState(doorPos.up());
            if (upperState.getBlock() instanceof SupralandDoorBlock) {
                toggleDoorHalf(world, doorPos.up(), upperState);
            }
            world.playSound(null, doorPos,
                isOpen ? SoundEvents.BLOCK_IRON_DOOR_CLOSE : SoundEvents.BLOCK_IRON_DOOR_OPEN,
                SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private static void toggleDoorHalf(World world, BlockPos pos, BlockState state) {
        boolean wasOpen = state.get(DoorBlock.OPEN);
        world.setBlockState(pos, state.with(DoorBlock.OPEN, !wasOpen), 3);
    }

    // Активировать связанную кнопку (вызывается из кнопки)
    public static void activateLinkedButton(World world, BlockPos buttonPos) {
        BlockPos linkedPos = links.get(buttonPos);
        if (linkedPos == null) return;

        BlockState linkedState = world.getBlockState(linkedPos);
        if (linkedState.getBlock() instanceof SupralandButtonBlock) {
            // Нажимаем связанную кнопку
            SupralandButtonBlock.pressButton(world, linkedPos, linkedState);
        }
    }
}
