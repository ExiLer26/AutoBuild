package com.autobuild.client.handler;
import static com.autobuild.client.handler.KeyBindingHandler.*;

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


if (hitboxUpKey.consumeClick()) {
    BlockPos newPos = SelectionManager.getFixedHitboxOrigin().above();
    SelectionManager.setFixedHitboxOrigin(newPos);
    mc.player.displayClientMessage(
        Component.literal("Hitbox yukarı taşındı: " + newPos.toShortString()), true);
    return;
}

if (hitboxDownKey.consumeClick()) {
    BlockPos newPos = SelectionManager.getFixedHitboxOrigin().below();
    SelectionManager.setFixedHitboxOrigin(newPos);
    mc.player.displayClientMessage(
        Component.literal("Hitbox aşağı taşındı: " + newPos.toShortString()), true);
    return;
}

if (hitboxLeftKey.consumeClick()) {
    BlockPos newPos = SelectionManager.getFixedHitboxOrigin().west();
    SelectionManager.setFixedHitboxOrigin(newPos);
    mc.player.displayClientMessage(
        Component.literal("Hitbox sola taşındı: " + newPos.toShortString()), true);
    return;
}

if (hitboxRightKey.consumeClick()) {
    BlockPos newPos = SelectionManager.getFixedHitboxOrigin().east();
    SelectionManager.setFixedHitboxOrigin(newPos);
    mc.player.displayClientMessage(
        Component.literal("Hitbox sağa taşındı: " + newPos.toShortString()), true);
    return;
}

if (hitboxForwardKey.consumeClick()) {
    BlockPos newPos = SelectionManager.getFixedHitboxOrigin().north();
    SelectionManager.setFixedHitboxOrigin(newPos);
    mc.player.displayClientMessage(
        Component.literal("Hitbox ileri taşındı: " + newPos.toShortString()), true);
    return;
}

if (hitboxBackKey.consumeClick()) {
    BlockPos newPos = SelectionManager.getFixedHitboxOrigin().south();
    SelectionManager.setFixedHitboxOrigin(newPos);
    mc.player.displayClientMessage(
        Component.literal("Hitbox geri taşındı: " + newPos.toShortString()), true);
    return;
}

            }
        }
