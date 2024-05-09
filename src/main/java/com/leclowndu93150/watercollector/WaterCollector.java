package com.leclowndu93150.watercollector;

import com.leclowndu93150.watercollector.config.ModConfigCommon;
import com.leclowndu93150.watercollector.entity.ModBlockEntities;
import com.leclowndu93150.watercollector.entity.WatercollectorBlockEntity;
import com.leclowndu93150.watercollector.item.ModItems;
import com.leclowndu93150.watercollector.network.ModMessages;
import com.mojang.logging.LogUtils;
import com.leclowndu93150.watercollector.block.ModBlocks;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(WaterCollector.MODID)
public class WaterCollector
{
    public static final String MODID = "watercollector";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public WaterCollector()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(eventBus);
        ModItems.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMessages.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigCommon.SPEC, "watercollector-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
        eventBus.addListener(this::addCreative);

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.WATERCOLLECTOR);
        }
    }

    @SubscribeEvent
    public void onServerAboutToStart(final ServerAboutToStartEvent event) {
        WatercollectorBlockEntity.verifyConfig(LOGGER);
    }
    //@SubscribeEvent
    //public void onBottleClick(PlayerInteractEvent.RightClickItem e) {
    //    Player player = e.getEntity();
    //    Level world = e.getLevel();
    //    InteractionHand hand = e.getHand();

    //    ItemStack stack = player.getItemInHand(hand);
    //    Potion waterPotion = Potion.byName(Potions.WATER.toString());
        //player.getInventory().placeItemBackInInventory(new ItemStack(waterPotion));
        //player.getInventory().placeItemBackInInventory(meshInv.getStackInSlot(0));


        //if (BottleEvent.onBottleClick(e.getEntity(), e.getLevel(), e.getHand()).getResult().equals(InteractionResult.FAIL)) {
        //    e.setCanceled(true);
        //}
   // }
}
