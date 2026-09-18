package ru.supraland;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class SupralandSwordItem extends SwordItem {
    private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("a3b1c2d3-e4f5-6789-0abc-def012345678");

    public SupralandSwordItem(Settings settings) {
        super(ToolMaterials.IRON, 3, -2.4f, settings);
    }

    @Override
    public float getAttackDamage() {
        return 3.0f; // Слабый меч, как в начале Supraland
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PlayerUpgradeData data = null;
        if (attacker instanceof PlayerEntity) {
            data = PlayerUpgradeData.get((PlayerEntity) attacker);
        }
        // Бонусный урон от улучшений
        if (data != null && data.swordBonusDamage > 0) {
            target.damage(attacker.getWorld().getDamageSources().mobAttack(attacker), data.swordBonusDamage);
        }
        return super.postHit(stack, target, attacker);
    }
}
