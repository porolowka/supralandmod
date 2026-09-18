package ru.supraland;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class PlayerUpgradeData implements Component {
    public double speedMultiplier = 1.0;
    public int maxJumps = 1;
    public double jumpHeightMultiplier = 1.0;
    public int swordBonusDamage = 0;
    public double gunDamageMultiplier = 1.0;
    public double fireRateMultiplier = 1.0;
    public int maxCoins = 30;
    public int coins = 0;
    public boolean hasLaser = false;
    public int jumpsRemaining = 1;

    public static PlayerUpgradeData get(PlayerEntity player) {
        return ModComponents.UPGRADE_DATA.get(player);
    }

    public void applyUpgrade(UpgradeType type) {
        switch (type) {
            case SPEED_X2: speedMultiplier *= 2.0; break;
            case SPEED_X15: speedMultiplier *= 1.5; break;
            case DOUBLE_JUMP: maxJumps = Math.max(maxJumps, 2); jumpsRemaining = maxJumps; break;
            case TRIPLE_JUMP: maxJumps = Math.max(maxJumps, 3); jumpsRemaining = maxJumps; break;
            case HAPPINESS: jumpHeightMultiplier *= 3.0; break;
            case SWORD_DAMAGE: swordBonusDamage += 1; break;
            case GUN_DAMAGE: gunDamageMultiplier += 0.10; break;
            case FIRE_RATE: fireRateMultiplier += 0.10; break;
            case COIN_CAPACITY: maxCoins += 30; break;
            case COINS: coins = Math.min(coins + 30, maxCoins); break;
            case LASER: hasLaser = true; break;
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putDouble("speedMul", speedMultiplier);
        tag.putInt("maxJumps", maxJumps);
        tag.putDouble("jumpHeight", jumpHeightMultiplier);
        tag.putInt("swordBonus", swordBonusDamage);
        tag.putDouble("gunDamage", gunDamageMultiplier);
        tag.putDouble("fireRate", fireRateMultiplier);
        tag.putInt("maxCoins", maxCoins);
        tag.putInt("coins", coins);
        tag.putBoolean("hasLaser", hasLaser);
        tag.putInt("jumpsRem", jumpsRemaining);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        speedMultiplier = tag.getDouble("speedMul");
        maxJumps = tag.getInt("maxJumps");
        jumpHeightMultiplier = tag.getDouble("jumpHeight");
        swordBonusDamage = tag.getInt("swordBonus");
        gunDamageMultiplier = tag.getDouble("gunDamage");
        fireRateMultiplier = tag.getDouble("fireRate");
        maxCoins = tag.getInt("maxCoins");
        coins = tag.getInt("coins");
        hasLaser = tag.getBoolean("hasLaser");
        jumpsRemaining = tag.getInt("jumpsRem");
    }
}
