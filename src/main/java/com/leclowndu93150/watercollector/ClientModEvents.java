package com.leclowndu93150.watercollector;


import com.leclowndu93150.watercollector.block.custom.WatercollectorRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = WaterCollector.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        WaterCollector.LOGGER.info("INIT CLIENT SETUP");
        WatercollectorRenderer.register();
    }
}