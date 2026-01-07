package com.autobuild.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class AutoBuildConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLED;
    public static final ForgeConfigSpec.IntValue BUILD_SPEED;
    public static final ForgeConfigSpec.IntValue PROXIMITY_DISTANCE;
    public static final ForgeConfigSpec.ConfigValue<String> SELECTED_STRUCTURE;
    public static final ForgeConfigSpec.BooleanValue HITBOX_VISIBLE;
    public static final ForgeConfigSpec.IntValue HITBOX_Y_OFFSET;
    public static final ForgeConfigSpec.BooleanValue ANTI_CHEAT_PROTECTION;

    static {
        BUILDER.push("autobuild");

        ENABLED = BUILDER
                .comment("Enable or disable AutoBuild functionality")
                .define("enabled", false);

        BUILD_SPEED = BUILDER
                .comment("Build speed (1-10, where 1 is slowest and 10 is fastest)")
                .defineInRange("buildSpeed", 5, 1, 10);

        PROXIMITY_DISTANCE = BUILDER
                .comment("Proximity distance for auto-building (1-10 blocks)")
                .defineInRange("proximityDistance", 3, 1, 10);

        SELECTED_STRUCTURE = BUILDER
                .comment("Currently selected structure JSON file")
                .define("selectedStructure", "");

        HITBOX_VISIBLE = BUILDER
                .comment("Enable or disable hitbox visibility")
                .define("hitboxVisible", true);

        HITBOX_Y_OFFSET = BUILDER
                .comment("Offset for hitbox vertical position")
                .defineInRange("hitboxYOffset", 0, -10, 10);

        ANTI_CHEAT_PROTECTION = BUILDER
                .comment("Enable anti-cheat protection (jitter, fake rotation, arm swing)")
                .define("antiCheatProtection", true);

        BUILDER.pop();

        CLIENT_SPEC = BUILDER.build();
    }

    public static boolean isAntiCheatProtection() {
        return ANTI_CHEAT_PROTECTION.get();
    }

    public static void setAntiCheatProtection(boolean protection) {
        ANTI_CHEAT_PROTECTION.set(protection);
    }

    public static boolean isEnabled() {
        return ENABLED.get();
    }

    public static void setEnabled(boolean enabled) {
        ENABLED.set(enabled);
    }

    public static int getBuildSpeed() {
        return BUILD_SPEED.get();
    }

    public static void setBuildSpeed(int speed) {
        BUILD_SPEED.set(Math.max(1, Math.min(10, speed)));
    }

    public static int getProximityDistance() {
        return PROXIMITY_DISTANCE.get();
    }

    public static void setProximityDistance(int distance) {
        PROXIMITY_DISTANCE.set(Math.max(1, Math.min(10, distance)));
    }

    public static String getSelectedStructure() {
        return SELECTED_STRUCTURE.get();
    }

    public static void setSelectedStructure(String structure) {
        SELECTED_STRUCTURE.set(structure);
    }

    public static boolean isHitboxVisible() {
        return HITBOX_VISIBLE.get();
    }

    public static void setHitboxVisible(boolean visible) {
        HITBOX_VISIBLE.set(visible);
    }

    public static int getHitboxYOffset() {
        return HITBOX_Y_OFFSET.get();
    }

    public static void setHitboxYOffset(int offset) {
        HITBOX_Y_OFFSET.set(Math.max(-10, Math.min(10, offset)));
    }
}
