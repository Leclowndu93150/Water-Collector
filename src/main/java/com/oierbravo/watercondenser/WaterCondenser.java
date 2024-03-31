package com.oierbravo.watercondenser;

import com.mojang.logging.LogUtils;
import com.oierbravo.watercondenser.block.ModBlocks;
import com.oierbravo.watercondenser.config.ModConfigCommon;
import com.oierbravo.watercondenser.entity.ModBlockEntities;
import com.oierbravo.watercondenser.entity.WatercondenserBlockEntity;
import com.oierbravo.watercondenser.item.ModItems;
import com.oierbravo.watercondenser.network.ModMessages;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import net.minecraft.world.item.alchemy.PotionUtils;
@Mod(WaterCondenser.MODID)
public class WaterCondenser
{
    public static final String MODID = "watercondenser";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public WaterCondenser()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(eventBus);
        ModItems.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModMessages.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigCommon.SPEC, "watercondenser-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
        eventBus.addListener(this::addCreative);

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.WATERCONDENSER);
        }
    }

    @SubscribeEvent
    public void onServerAboutToStart(final ServerAboutToStartEvent event) {
        WatercondenserBlockEntity.verifyConfig(LOGGER);
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
