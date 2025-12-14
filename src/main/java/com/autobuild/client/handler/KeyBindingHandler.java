package com.autobuild.client.handler;

import com.autobuild.AutoBuild;
import com.autobuild.client.gui.AutoBuildScreen;
import com.autobuild.common.config.AutoBuildConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = AutoBuild.MOD_ID, value = Dist.CLIENT)
public class KeyBindingHandler {
    public static final String KEY_CATEGORY = "key.categories.autobuild";
    public static final String KEY_OPEN_MENU = "key.autobuild.openmenu";
    public static final String KEY_TOGGLE = "key.autobuild.toggle";
    public static final String KEY_TOGGLE_HITBOX = "key.autobuild.togglehitbox";
public static final String KEY_HITBOX_FORWARD = "key.autobuild.hitbox.forward";
public static final String KEY_HITBOX_BACK = "key.autobuild.hitbox.back";
public static final String KEY_HITBOX_LEFT = "key.autobuild.hitbox.left";
public static final String KEY_HITBOX_RIGHT = "key.autobuild.hitbox.right";
public static final String KEY_HITBOX_UP = "key.autobuild.hitbox.up";
public static final String KEY_HITBOX_DOWN = "key.autobuild.hitbox.down";

public static KeyMapping hitboxForwardKey;
public static KeyMapping hitboxBackKey;
public static KeyMapping hitboxLeftKey;
public static KeyMapping hitboxRightKey;
public static KeyMapping hitboxUpKey;
public static KeyMapping hitboxDownKey;

    public static KeyMapping openMenuKey;
    public static KeyMapping toggleKey;
    public static KeyMapping toggleHitboxKey;

    static {
        openMenuKey = new KeyMapping(
                KEY_OPEN_MENU,
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                KEY_CATEGORY
        );
        
        toggleKey = new KeyMapping(
                KEY_TOGGLE,
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                KEY_CATEGORY
        );
        
        toggleHitboxKey = new KeyMapping(
                KEY_TOGGLE_HITBOX,
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KEY_CATEGORY
        );

hitboxForwardKey = new KeyMapping(
        KEY_HITBOX_FORWARD,
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_KP_8,
        KEY_CATEGORY
);

hitboxBackKey = new KeyMapping(
        KEY_HITBOX_BACK,
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_KP_5,
        KEY_CATEGORY
);

hitboxLeftKey = new KeyMapping(
        KEY_HITBOX_LEFT,
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_LEFT,
        KEY_CATEGORY
);

hitboxRightKey = new KeyMapping(
        KEY_HITBOX_RIGHT,
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_RIGHT,
        KEY_CATEGORY
);

hitboxUpKey = new KeyMapping(
        KEY_HITBOX_UP,
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_UP,
        KEY_CATEGORY
);

hitboxDownKey = new KeyMapping(
        KEY_HITBOX_DOWN,
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_DOWN,
        KEY_CATEGORY
);
    }

    
    public static void register() {
        MinecraftForge.EVENT_BUS.register(new KeyBindingHandler());
        AutoBuild.LOGGER.info("KeyBindingHandler registered");
    }

    @Mod.EventBusSubscriber(modid = AutoBuild.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        
            event.register(openMenuKey);
event.register(hitboxForwardKey);
event.register(hitboxBackKey);
event.register(hitboxLeftKey);
event.register(hitboxRightKey);
event.register(hitboxUpKey);
event.register(hitboxDownKey);
            AutoBuild.LOGGER.info("Registered AutoBuild menu keybinding (J)");
            event.register(toggleKey);
            AutoBuild.LOGGER.info("Registered AutoBuild toggle keybinding (L)");
            event.register(toggleHitboxKey);
            AutoBuild.LOGGER.info("Registered AutoBuild hitbox toggle keybinding (H)");
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.screen != null) {
            return;
        }

        if (openMenuKey != null && openMenuKey.consumeClick()) {
            mc.setScreen(new AutoBuildScreen());
        }
        
        if (toggleKey != null && toggleKey.consumeClick()) {
            boolean newState = !AutoBuildConfig.isEnabled();
            AutoBuildConfig.setEnabled(newState);
            if (mc.player != null) {
                mc.player.displayClientMessage(
                        Component.translatable(newState ? 
                                "message.autobuild.toggle.on" : 
                                "message.autobuild.toggle.off"), true);
            }
            if (!newState) {
                SelectionManager.clearFixedHitboxOrigin();
            }
        }
        
        if (toggleHitboxKey != null && toggleHitboxKey.consumeClick()) {
            boolean currentVisible = AutoBuildConfig.isHitboxVisible();
            AutoBuildConfig.setHitboxVisible(!currentVisible);
            if (mc.player != null) {
                mc.player.displayClientMessage(
                        Component.translatable(!currentVisible ? 
                                "message.autobuild.hitbox.on" : 
                                "message.autobuild.hitbox.off"), true);
            }
        }
    }
}
