package com.autobuild.client.handler;

import com.autobuild.common.config.AutoBuildConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "autobuild", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) {
            return;
        }

        if (event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }

        if (SelectionManager.hasFixedHitbox()) {
            if (event.getKey() == GLFW.GLFW_KEY_UP) {
                BlockPos current = SelectionManager.getFixedHitboxOrigin();
                BlockPos newPos = current.above();
                SelectionManager.setFixedHitboxOrigin(newPos);

                mc.player.displayClientMessage(
                    Component.literal("Hitbox yukarı taşındı: " + newPos.toShortString()),
                    true
                );
                return;
            } else if (event.getKey() == GLFW.GLFW_KEY_DOWN) {
                BlockPos current = SelectionManager.getFixedHitboxOrigin();
                BlockPos newPos = current.below();
                SelectionManager.setFixedHitboxOrigin(newPos);

                mc.player.displayClientMessage(
                    Component.literal("Hitbox aşağı taşındı: " + newPos.toShortString()),
                    true
                );
                return;
            }
        }
    }
}
