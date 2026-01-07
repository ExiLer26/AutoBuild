package com.autobuild.common.data;

import com.autobuild.AutoBuild;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class StructureManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path autobuildFolder;
    private static final Map<String, BuildStructure> loadedStructures = new HashMap<>();

    public static void ensureDirectoryExists() {
        try {
            // Using a safer way to get the game directory that works on both client and server if needed
            // But since this is a client mod, we'll keep it simple but add null checks
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.gameDirectory == null) return;
            
            autobuildFolder = mc.gameDirectory.toPath().resolve("autobuild");
            if (!Files.exists(autobuildFolder)) {
                Files.createDirectories(autobuildFolder);
                AutoBuild.LOGGER.info("Created autobuild directory at: " + autobuildFolder);
                
                createExampleStructure();
            }
        } catch (IOException e) {
            AutoBuild.LOGGER.error("Failed to create autobuild directory", e);
        }
    }

    private static void createExampleStructure() {
        BuildStructure example = new BuildStructure("example");
        example.addBlock(new BlockPos(0, 0, 0), "minecraft:stone");
        example.addBlock(new BlockPos(1, 0, 0), "minecraft:stone");
        example.addBlock(new BlockPos(0, 1, 0), "minecraft:stone");
        example.addBlock(new BlockPos(0, 0, 1), "minecraft:stone");
        saveStructure(example);
    }

    public static void loadStructures() {
        loadedStructures.clear();
        
        if (autobuildFolder == null || !Files.exists(autobuildFolder)) {
            return;
        }

        try {
            Files.list(autobuildFolder)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(path -> {
                        try {
                            String name = path.getFileName().toString().replace(".json", "");
                            BuildStructure structure = loadStructureFromFile(path, name);
                            if (structure != null) {
                                loadedStructures.put(name, structure);
                                AutoBuild.LOGGER.info("Loaded structure: " + name);
                            }
                        } catch (Exception e) {
                            AutoBuild.LOGGER.error("Failed to load structure: " + path, e);
                        }
                    });
        } catch (IOException e) {
            AutoBuild.LOGGER.error("Failed to list autobuild directory", e);
        }
    }

    private static BuildStructure loadStructureFromFile(Path path, String name) {
        try (Reader reader = Files.newBufferedReader(path)) {
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            BuildStructure structure = new BuildStructure(name);
            
            for (JsonElement element : jsonArray) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    JsonArray posArray = obj.getAsJsonArray("pos");
                    String blockId = obj.has("block") ? obj.get("block").getAsString() : "minecraft:stone";
                    
                    if (posArray.size() >= 3) {
                        int x = posArray.get(0).getAsInt();
                        int y = posArray.get(1).getAsInt();
                        int z = posArray.get(2).getAsInt();
                        structure.addBlock(new BlockPos(x, y, z), blockId);
                    }
                } else if (element.isJsonArray()) {
                    JsonArray posArray = element.getAsJsonArray();
                    if (posArray.size() >= 3) {
                        int x = posArray.get(0).getAsInt();
                        int y = posArray.get(1).getAsInt();
                        int z = posArray.get(2).getAsInt();
                        structure.addBlock(new BlockPos(x, y, z), "minecraft:stone");
                    }
                }
            }
            
            return structure;
        } catch (Exception e) {
            AutoBuild.LOGGER.error("Error reading structure file: " + path, e);
            return null;
        }
    }

    public static void saveStructure(BuildStructure structure) {
        if (autobuildFolder == null) {
            ensureDirectoryExists();
        }

        Path filePath = autobuildFolder.resolve(structure.getName() + ".json");
        
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            JsonArray jsonArray = new JsonArray();
            
            for (Map.Entry<BlockPos, String> entry : structure.getBlocks().entrySet()) {
                BlockPos pos = entry.getKey();
                String blockId = entry.getValue();
                
                JsonObject blockObj = new JsonObject();
                JsonArray posArray = new JsonArray();
                posArray.add(pos.getX());
                posArray.add(pos.getY());
                posArray.add(pos.getZ());
                blockObj.add("pos", posArray);
                blockObj.addProperty("block", blockId);
                jsonArray.add(blockObj);
            }
            
            GSON.toJson(jsonArray, writer);
            loadedStructures.put(structure.getName(), structure);
            AutoBuild.LOGGER.info("Saved structure: " + structure.getName());
        } catch (IOException e) {
            AutoBuild.LOGGER.error("Failed to save structure: " + structure.getName(), e);
        }
    }

    public static BuildStructure createNewStructure(String name) {
        BuildStructure structure = new BuildStructure(name);
        saveStructure(structure);
        return structure;
    }
    
    public static BuildStructure createNewStructure(String name, Map<BlockPos, String> blocks) {
        BuildStructure structure = new BuildStructure(name);
        for (Map.Entry<BlockPos, String> entry : blocks.entrySet()) {
            structure.addBlock(entry.getKey(), entry.getValue());
        }
        saveStructure(structure);
        return structure;
    }

    public static BuildStructure getStructure(String name) {
        return loadedStructures.get(name);
    }

    public static Collection<BuildStructure> getAllStructures() {
        return loadedStructures.values();
    }

    public static List<String> getStructureNames() {
        return new ArrayList<>(loadedStructures.keySet());
    }

    public static boolean deleteStructure(String name) {
        if (autobuildFolder == null) {
            return false;
        }

        Path filePath = autobuildFolder.resolve(name + ".json");
        try {
            Files.deleteIfExists(filePath);
            loadedStructures.remove(name);
            return true;
        } catch (IOException e) {
            AutoBuild.LOGGER.error("Failed to delete structure: " + name, e);
            return false;
        }
    }

    public static Path getAutobuildFolder() {
        return autobuildFolder;
    }
}
