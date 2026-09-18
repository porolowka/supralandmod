package ru.supraland;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.supraland.entity.SupralandNpcEntity;

public class SupralandMod implements ModInitializer {
    public static final String MOD_ID = "supralandmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Item SUPRALAND_SWORD;
    public static Item RED_CRYSTAL_GUN;
    public static Item LINKING_TOOL;

    public static Block UPGRADE_CHEST;
    public static Block LASER_RECEIVER;
    public static Block SUPRALAND_DOOR;
    public static Block SUPRALAND_BUTTON;

    public static BlockEntityType<UpgradeChestBlockEntity> UPGRADE_CHEST_BE;
    public static BlockEntityType<LaserReceiverBlockEntity> LASER_RECEIVER_BE;
    public static EntityType<SupralandNpcEntity> RED_NPC;
    public static EntityType<SupralandNpcEntity> BLUE_NPC;

    @Override
    public void onInitialize() {
        LOGGER.info("[Supraland Mod] Initializing...");

        RED_NPC = Registry.register(Registries.ENTITY_TYPE, id("red_npc"),
            EntityType.Builder.<SupralandNpcEntity>create(
                (type, world) -> new SupralandNpcEntity(type, world, 1.0f, 0.0f, 0.0f),
                SpawnGroup.MISC)
                .setDimensions(0.6f, 1.8f)
                .build("red_npc"));

        BLUE_NPC = Registry.register(Registries.ENTITY_TYPE, id("blue_npc"),
            EntityType.Builder.<SupralandNpcEntity>create(
                (type, world) -> new SupralandNpcEntity(type, world, 0.0f, 0.3f, 1.0f),
                SpawnGroup.MISC)
                .setDimensions(0.6f, 1.8f)
                .build("blue_npc"));

        SUPRALAND_SWORD = new SupralandSwordItem(new Item.Settings().maxCount(1));
        Registry.register(Registries.ITEM, id("supraland_sword"), SUPRALAND_SWORD);

        RED_CRYSTAL_GUN = new RedCrystalGunItem(new Item.Settings().maxCount(1));
        Registry.register(Registries.ITEM, id("red_crystal_gun"), RED_CRYSTAL_GUN);

        LINKING_TOOL = new LinkingToolItem(new Item.Settings().maxCount(1));
        Registry.register(Registries.ITEM, id("linking_tool"), LINKING_TOOL);

        UPGRADE_CHEST = new UpgradeChestBlock(AbstractBlock.Settings.create().strength(2.0f).sounds(BlockSoundGroup.WOOD));
        Registry.register(Registries.BLOCK, id("upgrade_chest"), UPGRADE_CHEST);
        Registry.register(Registries.ITEM, id("upgrade_chest"), new BlockItem(UPGRADE_CHEST, new Item.Settings()));

        LASER_RECEIVER = new LaserReceiverBlock(AbstractBlock.Settings.create().strength(3.0f).sounds(BlockSoundGroup.METAL).luminance(s -> 7));
        Registry.register(Registries.BLOCK, id("laser_receiver"), LASER_RECEIVER);
        Registry.register(Registries.ITEM, id("laser_receiver"), new BlockItem(LASER_RECEIVER, new Item.Settings()));

        SUPRALAND_DOOR = new SupralandDoorBlock(AbstractBlock.Settings.create().strength(5.0f).sounds(BlockSoundGroup.METAL).nonOpaque());
        Registry.register(Registries.BLOCK, id("supraland_door"), SUPRALAND_DOOR);
        Registry.register(Registries.ITEM, id("supraland_door"), new BlockItem(SUPRALAND_DOOR, new Item.Settings()));

        SUPRALAND_BUTTON = new SupralandButtonBlock(AbstractBlock.Settings.create().strength(3.0f).sounds(BlockSoundGroup.METAL));
        Registry.register(Registries.BLOCK, id("supraland_button"), SUPRALAND_BUTTON);
        Registry.register(Registries.ITEM, id("supraland_button"), new BlockItem(SUPRALAND_BUTTON, new Item.Settings()));

        UPGRADE_CHEST_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("upgrade_chest"),
            FabricBlockEntityTypeBuilder.create(UpgradeChestBlockEntity::new, UPGRADE_CHEST).build());
        LASER_RECEIVER_BE = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("laser_receiver"),
            FabricBlockEntityTypeBuilder.create(LaserReceiverBlockEntity::new, LASER_RECEIVER).build());

        RedCrystalProjectileEntity.register();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SUPRALAND_SWORD);
            entries.add(RED_CRYSTAL_GUN);
            entries.add(LINKING_TOOL);
            entries.add(UPGRADE_CHEST.asItem());
            entries.add(LASER_RECEIVER.asItem());
            entries.add(SUPRALAND_DOOR.asItem());
            entries.add(SUPRALAND_BUTTON.asItem());
        });

        for (UpgradeType type : UpgradeType.values()) {
            Item chestItem = new UpgradeChestItem(new Item.Settings(), type);
            Registry.register(Registries.ITEM, id("chest_" + type.name().toLowerCase()), chestItem);
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(e -> e.add(chestItem));
        }

        LOGGER.info("[Supraland Mod] Done!");
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
