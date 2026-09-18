package ru.supraland;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UpgradeChestBlockEntity extends BlockEntity {
    private UpgradeType upgradeType = UpgradeType.SPEED_X2;
    private boolean opened = false;

    public UpgradeChestBlockEntity(BlockPos pos, BlockState state) {
        super(SupralandMod.UPGRADE_CHEST_BE, pos, state);
    }

    public void setUpgradeType(UpgradeType type) { this.upgradeType = type; markDirty(); }
    public UpgradeType getUpgradeType() { return upgradeType; }

    public void open(PlayerEntity player) {
        if (opened) {
            player.sendMessage(Text.literal("Этот сундук уже открыт"), true);
            return;
        }

        PlayerUpgradeData data = PlayerUpgradeData.get(player);
        if (data == null) return;

        // Проверка монет
        if (upgradeType == UpgradeType.COINS && data.coins >= data.maxCoins) {
            player.sendMessage(Text.literal("Нужно увеличить вместимость монет!"), true);
            return;
        }

        // Круг частиц
        spawnCircle(world, pos);

        // Применяем улучшение
        data.applyUpgrade(upgradeType);

        // Синхронизация с клиентом — чтобы HUD обновился
        ModComponents.UPGRADE_DATA.sync(player);

        world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0f, 1.5f);

        player.sendMessage(Text.literal("Получено: " + upgradeType.getDisplayName() + " (навсегда)"), true);

        opened = true;
        markDirty();
    }

    private void spawnCircle(World world, BlockPos pos) {
        if (world == null) return;
        for (int i = 0; i < 40; i++) {
            double angle = (Math.PI * 2 * i) / 40;
            double dx = Math.cos(angle) * 2.0;
            double dz = Math.sin(angle) * 2.0;
            world.addParticle(ParticleTypes.END_ROD,
                pos.getX() + 0.5 + dx, pos.getY() + 1.0, pos.getZ() + 0.5 + dz,
                -dx * 0.05, 0.4, -dz * 0.05);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putString("upgradeType", upgradeType.name());
        nbt.putBoolean("opened", opened);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        try { upgradeType = UpgradeType.valueOf(nbt.getString("upgradeType")); }
        catch (Exception e) { upgradeType = UpgradeType.SPEED_X2; }
        opened = nbt.getBoolean("opened");
    }
}
