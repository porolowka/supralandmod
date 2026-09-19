package ru.supraland;

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

public class LinkingToolItem extends Item {
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

        if (!(state.getBlock() instanceof LaserReceiverBlock) &&
            !(state.getBlock() instanceof SupralandButtonBlock) &&
            !(state.getBlock() instanceof SupralandDoorBlock)) {
            player.sendMessage(Text.literal("§cЭтот блок нельзя связать."), true);
            return ActionResult.FAIL;
        }

        NbtCompound nbt = stack.getOrCreateNbt();

        if (!nbt.contains("first_block")) {
            nbt.putLong("first_block", pos.asLong());
            player.sendMessage(Text.literal("§dПервый блок выбран: " + pos.toShortString()), true);
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 1.0f, 1.2f);
        } else {
            BlockPos first = BlockPos.fromLong(nbt.getLong("first_block"));
            nbt.remove("first_block");

            if (world.getBlockEntity(pos) instanceof LaserReceiverBlockEntity receiverBe) {
                NbtCompound beNbt = receiverBe.writeNbt(new NbtCompound());
                beNbt.putLong("linked_door", first.asLong());
                receiverBe.readNbt(beNbt);
                receiverBe.markDirty();
            } else if (world.getBlockEntity(first) instanceof LaserReceiverBlockEntity receiverBe) {
                NbtCompound beNbt = receiverBe.writeNbt(new NbtCompound());
                beNbt.putLong("linked_door", pos.asLong());
                receiverBe.readNbt(beNbt);
                receiverBe.markDirty();
            }

            player.sendMessage(Text.literal("§aСвязано: " + first.toShortString() + " -> " + pos.toShortString()), true);
            player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME.value(), SoundCategory.PLAYERS, 1.0f, 1.5f);
        }

        return ActionResult.SUCCESS;
    }

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
}
