package com.autobuild.common.command;

import com.autobuild.client.handler.SelectionManager;
import com.autobuild.common.config.AutoBuildConfig;
import com.autobuild.common.data.BuildStructure;
import com.autobuild.common.data.StructureManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class AutoBuildCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("autobuild")
                .then(Commands.literal("new")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    if (name.endsWith(".json")) {
                                        name = name.substring(0, name.length() - 5);
                                    }
                                    final String finalName = name;
                                    
                                    if (SelectionManager.hasSelection()) {
                                        Map<BlockPos, String> blocks = SelectionManager.getSelectedBlocksWithTypes();
                                        BuildStructure structure = StructureManager.createNewStructure(finalName, blocks);
                                        int blockCount = blocks.size();
                                        SelectionManager.clearSelection();
                                        context.getSource().sendSuccess(() -> 
                                                Component.translatable("command.autobuild.new.success.selection", finalName, blockCount), true);
                                    } else {
                                        BuildStructure structure = StructureManager.createNewStructure(finalName);
                                        context.getSource().sendSuccess(() -> 
                                                Component.translatable("command.autobuild.new.success.empty", finalName), true);
                                    }
                                    return 1;
                                })))
                .then(Commands.literal("select")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    if (name.endsWith(".json")) {
                                        name = name.substring(0, name.length() - 5);
                                    }
                                    final String finalName = name;
                                    
                                    BuildStructure structure = StructureManager.getStructure(finalName);
                                    if (structure != null) {
                                        AutoBuildConfig.setSelectedStructure(finalName);
                                        SelectionManager.clearFixedHitboxOrigin();
                                        context.getSource().sendSuccess(() -> 
                                                Component.translatable("command.autobuild.select.success", finalName), true);
                                        return 1;
                                    } else {
                                        context.getSource().sendFailure(
                                                Component.translatable("command.autobuild.select.notfound", finalName));
                                        return 0;
                                    }
                                })))
                .then(Commands.literal("list")
                        .executes(context -> {
                            java.util.List<String> structures = StructureManager.getStructureNames();
                            if (structures.isEmpty()) {
                                context.getSource().sendSuccess(() -> 
                                        Component.translatable("command.autobuild.list.empty"), false);
                            } else {
                                context.getSource().sendSuccess(() -> 
                                        Component.translatable("command.autobuild.list.header"), false);
                                for (String name : structures) {
                                    String finalName = name;
                                    context.getSource().sendSuccess(() -> 
                                            Component.literal("- " + finalName + ".json"), false);
                                }
                            }
                            return 1;
                        }))
                .then(Commands.literal("reload")
                        .executes(context -> {
                            StructureManager.loadStructures();
                            context.getSource().sendSuccess(() -> 
                                    Component.translatable("command.autobuild.reload.success"), true);
                            return 1;
                        }))
                .then(Commands.literal("toggle")
                        .executes(context -> {
                            boolean newState = !AutoBuildConfig.isEnabled();
                            AutoBuildConfig.setEnabled(newState);
                            if (!newState) {
                                SelectionManager.clearFixedHitboxOrigin();
                            }
                            context.getSource().sendSuccess(() -> 
                                    Component.translatable(newState ? 
                                            "command.autobuild.toggle.on" : 
                                            "command.autobuild.toggle.off"), true);
                            return 1;
                        }))
                .then(Commands.literal("speed")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 10))
                                .executes(context -> {
                                    int speed = IntegerArgumentType.getInteger(context, "value");
                                    AutoBuildConfig.setBuildSpeed(speed);
                                    context.getSource().sendSuccess(() -> 
                                            Component.translatable("command.autobuild.speed.set", speed), true);
                                    return 1;
                                })))
                .then(Commands.literal("proximity")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 10))
                                .executes(context -> {
                                    int proximity = IntegerArgumentType.getInteger(context, "value");
                                    AutoBuildConfig.setProximityDistance(proximity);
                                    context.getSource().sendSuccess(() -> 
                                            Component.translatable("command.autobuild.proximity.set", proximity), true);
                                    return 1;
                                })))
                .then(Commands.literal("delete")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    if (name.endsWith(".json")) {
                                        name = name.substring(0, name.length() - 5);
                                    }
                                    
                                    if (StructureManager.deleteStructure(name)) {
                                        String finalName = name;
                                        context.getSource().sendSuccess(() -> 
                                                Component.translatable("command.autobuild.delete.success", finalName), true);
                                        return 1;
                                    } else {
                                        String finalName = name;
                                        context.getSource().sendFailure(
                                                Component.translatable("command.autobuild.delete.notfound", finalName));
                                        return 0;
                                    }
                                })))
                .then(Commands.literal("clear")
                        .executes(context -> {
                            SelectionManager.clearSelection();
                            SelectionManager.clearFixedHitboxOrigin();
                            context.getSource().sendSuccess(() -> 
                                    Component.translatable("command.autobuild.clear.success"), true);
                            return 1;
                        }))
        );
    }
}
