package com.oierbravo.watercondenser.entity;

import com.oierbravo.watercondenser.config.ModConfigCommon;
import com.oierbravo.watercondenser.network.ModMessages;
import com.oierbravo.watercondenser.network.packets.FluidStackSyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;


import java.util.Random;
import java.util.random.RandomGenerator;

/**
 *  Code adapted from https://github.com/EwyBoy/ITank/blob/1.18.2/src/main/java/com/ewyboy/itank/common/content/tank/TankTile.java
 *
 */
public class WatercondenserBlockEntity extends BlockEntity {
//public class WatercondenserBlockEntity extends BlockEntity implements IFluidHandler{
    private static final RandomGenerator sharedRandom = new Random();
    private static Fluid fluidOutput = null;
    private static long lastCycleTime = -1;
    private static int cycleCounter = 0;
    private static boolean resetCycle = false;
    private CompoundTag updateTag;
    private final FluidTank fluidTankHandler = createFluidTank();
    //private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> fluidTankHandler);
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public WatercondenserBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.WATERCONDENSER_ENTITY.get(), pWorldPosition, pBlockState);
        updateTag = getPersistentData();
    }
    public static void verifyConfig(final Logger logger) {
        if (fluidOutput == null) {
            // verify and set the configured fluid
            final String fluidResourceRaw = ModConfigCommon.CONDENSER_FLUID.get();
            final ResourceLocation desiredFluid = new ResourceLocation(fluidResourceRaw);
            if (ForgeRegistries.FLUIDS.containsKey(desiredFluid)) {
                fluidOutput = ForgeRegistries.FLUIDS.getValue(desiredFluid);
            } else {
                logger.error("Unknown fluid '{}' in config, using default '{}' instead", fluidResourceRaw, ModConfigCommon.CONDENSER_FLUID_DEFAULT);
                fluidOutput = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(ModConfigCommon.CONDENSER_FLUID_DEFAULT));
            }
        }
    }
    private FluidTank createFluidTank() {
        return new FluidTank(ModConfigCommon.CONDENSER_CAPACITY.get(), ((FluidStack fluid) -> fluid.getFluid().isSame(fluidOutput))) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                assert level != null;
                if(!level.isClientSide()) {
                    ModMessages.sendToClients(new FluidStackSyncS2CPacket(this.fluid, worldPosition));
                }
            }

        };
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }
    public FluidStack getFluidStack() {

        if (!fluidTankHandler.isEmpty()) {
            return fluidTankHandler.getFluid();
        }

        return new FluidStack(fluidOutput, 1);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyFluidHandler = LazyOptional.of(() -> fluidTankHandler);
    }


    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }


    @Override
    protected void saveAdditional(CompoundTag nbt) {
        fluidTankHandler.writeToNBT(nbt);
        //nbt.putInt("fluid", fluidTankHandler.getCapacity());
        //updateTag = nbt;
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        fluidTankHandler.readFromNBT(nbt);
        //fluidTankHandler.setCapacity(nbt.getInt("fluid"));

        if (!fluidTankHandler.getFluid().getFluid().isSame(fluidOutput)) {
            // fluid in config differs from saved NBT, override it
            final FluidStack changedFluidStack = new FluidStack(fluidOutput, fluidTankHandler.getFluid().getAmount());
            fluidTankHandler.setFluid(changedFluidStack);
        }
    }


    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, WatercondenserBlockEntity pBlockEntity) {

        if(pLevel.isClientSide()) {
            return;
        }

        for(Direction direction : Direction.values()) {
            BlockPos offsetPos = pPos.relative(direction);
            BlockEntity blockEntity = pLevel.getBlockEntity(offsetPos);
            if (blockEntity != null){
                LazyOptional<IFluidHandler> FluidTank = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite());
                FluidTank.ifPresent(tank -> {
                    if(pBlockEntity.fluidTankHandler.getFluidAmount() >= ModConfigCommon.CONDENSER_MB_PER_CYCLE.get()){
                        tank.fill(new FluidStack(Fluids.WATER,ModConfigCommon.CONDENSER_MB_PER_CYCLE.get()), FluidAction.EXECUTE);
                        pBlockEntity.fluidTankHandler.drain(ModConfigCommon.CONDENSER_MB_PER_CYCLE.get(),FluidAction.EXECUTE);
                    }
                });
            }
        }

        final long timeNow = pLevel.getDayTime();
        if (timeNow != lastCycleTime) {
            lastCycleTime = timeNow;
            if (resetCycle) {
                // A "lazy" counter reset; allows all TE's to actually get a chance to tick their cycle
                resetCycle = false;
                cycleCounter = 0;
            }

            cycleCounter++;
        }

        if (cycleCounter >= ModConfigCommon.CONDENSER_TICKS_PER_CYCLE.get()) {
            resetCycle = true;

            final float amountMultiMin = ModConfigCommon.CONDENSER_MB_MULTI_MIN.get();
            int amount = ModConfigCommon.CONDENSER_MB_PER_CYCLE.get();
            if (amountMultiMin < 1.0f) {
                final float randomMultiplier = amountMultiMin + (sharedRandom.nextFloat() * (ModConfigCommon.CONDENSER_MB_MULTI_MAX.get() - amountMultiMin));
                amount = Math.round(ModConfigCommon.CONDENSER_MB_PER_CYCLE.get() * randomMultiplier);
            }

            pBlockEntity.fluidTankHandler.fill( new FluidStack(fluidOutput, amount), FluidAction.EXECUTE);
        }
    }
    public IFluidHandler getFluidHandler() {
        return this.fluidTankHandler;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    public void setFluid(FluidStack fluidStack) {
        this.fluidTankHandler.setFluid(fluidStack);
    }

    public boolean consumeWaterBottle() {
        int consumption = ModConfigCommon.CONDENSER_BOTTLE_MB_CONSUMPTION.get();
        if( consumption > fluidTankHandler.getFluidAmount()){
            return false;
        }
        fluidTankHandler.drain(consumption,FluidAction.EXECUTE);
        return true;
    }
}
