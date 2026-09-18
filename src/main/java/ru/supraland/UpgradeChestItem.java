package ru.supraland;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class UpgradeChestItem extends Item {
    private final UpgradeType upgradeType;

    public UpgradeChestItem(Settings settings, UpgradeType type) {
        super(settings.maxCount(64));
        this.upgradeType = type;
    }

    public UpgradeType getUpgradeType() { return upgradeType; }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(stack);

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data == null) return TypedActionResult.fail(stack);

        // Для монет проверяем вместимость
        if (upgradeType == UpgradeType.COINS) {
            if (data.coins >= data.maxCoins) {
                player.sendMessage(Text.literal("Нужно увеличить вместимость монет!"), true);
                return TypedActionResult.fail(stack);
            }
        }

        // Круг частиц вокруг игрока
        spawnCircle(world, player);

        // Применяем улучшение навсегда
        data.applyUpgrade(upgradeType);

        // Синхронизация с клиентом — чтобы HUD обновился
        ModComponents.UPGRADE_DATA.sync(player);

        // Звук
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.5f);

        player.sendMessage(Text.literal("Получено: " + upgradeType.getDisplayName() + (навсегда)"), true);

        stack.decrement(1);
        return TypedActionResult.success(stack);
    }

    private void spawnCircle(World world, PlayerEntity player) {
        for (int i = 0; i < 30; i++) {
            double angle = (Math.PI * 2 * i) / 30;
            double dx = Math.cos(angle) * 1.5;
            double dz = Math.sin(angle) * 1.5;
            world.addParticle(ParticleTypes.END_ROD,
                player.getX() + dx, player.getY() + 1.0, player.getZ() + dz,
                -dx * 0.1, 0.3, -dz * 0.1);
        }
    }
}
