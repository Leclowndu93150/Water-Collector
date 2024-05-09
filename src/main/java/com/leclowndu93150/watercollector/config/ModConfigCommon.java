package com.leclowndu93150.watercollector.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfigCommon {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final String COLLECTOR_FLUID_DEFAULT = "minecraft:water";
    public static final ForgeConfigSpec.ConfigValue<Integer> COLLECTOR_CAPACITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> COLLECTOR_TICKS_PER_CYCLE;
    public static final ForgeConfigSpec.ConfigValue<Integer> COLLECTOR_MB_PER_CYCLE;
    public static final ForgeConfigSpec.ConfigValue<Float> COLLECTOR_MB_MULTI_MIN;
    public static final ForgeConfigSpec.ConfigValue<Float> COLLECTOR_MB_MULTI_MAX;
    public static final ForgeConfigSpec.ConfigValue<Integer> COLLECTOR_BOTTLE_MB_CONSUMPTION;

    static {
        BUILDER.push("Configs for WaterCollector");

        COLLECTOR_CAPACITY = BUILDER.comment("Tank capacity in mB")
                .define("Condenser capacity", 1000);
        COLLECTOR_TICKS_PER_CYCLE = BUILDER.comment("The length of a fill cycle, in ticks")
                .define("Ticks between cycles", 1);
        COLLECTOR_MB_PER_CYCLE = BUILDER.comment("How much mB to generate per fill cycle")
                .define("Fluid per cycle", 2);
        COLLECTOR_MB_MULTI_MIN = BUILDER.comment("For random variance, the minimum multiplier for each fill cycle")
                .define("Fluid multiplier chance min", 0.0f);
        COLLECTOR_MB_MULTI_MAX = BUILDER.comment("For random variance, the maximum multiplier for each fill cycle")
                .define("Fluid multiplier chance max", 1.0f);

        COLLECTOR_BOTTLE_MB_CONSUMPTION = BUILDER.comment("Bottle consumption per bottle, in mB")
                .define("Fluid amount in mB", 250);


        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
