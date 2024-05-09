package com.leclowndu93150.watercollector.item;

import com.leclowndu93150.watercollector.WaterCollector;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, WaterCollector.MODID);


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
