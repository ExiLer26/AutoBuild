
package com.autobuild.client.handler;

import com.autobuild.AutoBuild;
import com.autobuild.common.config.AutoBuildConfig;
import com.autobuild.common.data.BuildStructure;
import com.autobuild.common.data.StructureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = AutoBuild.MOD_ID, value = Dist.CLIENT)
public class StickInteractionHandler {

    @SubscribeEvent
    public static void onMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.level == null) {
            return;
        }

        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        HitResult hitResult = mc.hitResult;

        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos hitPos = blockHit.getBlockPos();

        // Wooden Axe ile pos1/pos2 seçimi
        if (heldItem.is(Items.WOODEN_AXE)) {
            if (event.isUseItem()) {
                // Sağ tık - Pos2
                SelectionManager.setPos2(hitPos);
                player.displayClientMessage(
                        Component.literal("§aPos2 ayarlandı: " + hitPos.getX() + ", " + hitPos.getY() + ", " + hitPos.getZ()), true);
                event.setCanceled(true);
                event.setSwingHand(false);
            } else if (event.isAttack()) {
                // Sol tık - Pos1
                SelectionManager.setPos1(hitPos);
                player.displayClientMessage(
                        Component.literal("§aPos1 ayarlandı: " + hitPos.getX() + ", " + hitPos.getY() + ", " + hitPos.getZ()), true);
                event.setCanceled(true);
                event.setSwingHand(false);
            }
            return;
        }

        // Stick ile hitbox sabitleme
        if (heldItem.is(Items.STICK)) {
            if (event.isUseItem()) {
                // Sağ tık - Hitbox sabitini temizle
                SelectionManager.clearFixedHitboxOrigin();
                player.displayClientMessage(
                        Component.literal("§eSabit hitbox temizlendi"), true);
                event.setCanceled(true);
                event.setSwingHand(false);
            } else if (event.isAttack()) {
                // Sol tık - Hitbox sabitini ayarla
                SelectionManager.setFixedHitboxOrigin(hitPos);
                player.displayClientMessage(
                        Component.literal("§aSabit hitbox noktası ayarlandı: " + hitPos.getX() + ", " + hitPos.getY() + ", " + hitPos.getZ()), true);
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }
}
