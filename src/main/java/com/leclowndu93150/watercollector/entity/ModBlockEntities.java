package com.leclowndu93150.watercollector.entity;

import com.leclowndu93150.watercollector.WaterCollector;
import com.leclowndu93150.watercollector.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WaterCollector.MODID);

    public static final RegistryObject<BlockEntityType<WatercollectorBlockEntity>> WATERCOLLECTOR_ENTITY =
            BLOCK_ENTITIES.register("watercollector_entity", () ->
                    BlockEntityType.Builder.of(WatercollectorBlockEntity::new,
                            ModBlocks.WATERCOLLECTOR.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
