
package com.autobuild.common.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class StructureDataManager {
    private static StructureDataManager instance;
    private final Map<String, StructureData> structures = new HashMap<>();
    private StructureData selectedStructure;
    private final Path structuresDir;
    private final Gson gson = new Gson();
    
    private StructureDataManager(Path structuresDir) {
        this.structuresDir = structuresDir;
        loadAllStructures();
    }
    
    public static void initialize(Path structuresDir) {
        if (instance == null) {
            instance = new StructureDataManager(structuresDir);
        }
    }
    
    public static StructureDataManager getInstance() {
        return instance;
    }
    
    public void loadAllStructures() {
        structures.clear();
        
        if (!Files.exists(structuresDir)) {
            try {
                Files.createDirectories(structuresDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        try {
            Files.list(structuresDir)
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(this::loadStructure);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadStructure(Path path) {
        try {
            String name = path.getFileName().toString().replace(".json", "");
            Reader reader = Files.newBufferedReader(path);
            List<List<Integer>> coords = gson.fromJson(reader, new TypeToken<List<List<Integer>>>(){}.getType());
            reader.close();
            
            List<BlockPos> blocks = new ArrayList<>();
            for (List<Integer> coord : coords) {
                if (coord.size() >= 3) {
                    blocks.add(new BlockPos(coord.get(0), coord.get(1), coord.get(2)));
                }
            }
            
            structures.put(name, new StructureData(name, blocks));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean createStructure(String name) {
        Path file = structuresDir.resolve(name + ".json");
        if (Files.exists(file)) {
            return false;
        }
        
        try {
            Writer writer = Files.newBufferedWriter(file);
            gson.toJson(new ArrayList<List<Integer>>(), writer);
            writer.close();
            structures.put(name, new StructureData(name, new ArrayList<>()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteStructure(String name) {
        Path file = structuresDir.resolve(name + ".json");
        if (!Files.exists(file)) {
            return false;
        }
        
        try {
            Files.delete(file);
            structures.remove(name);
            if (selectedStructure != null && selectedStructure.getName().equals(name)) {
                selectedStructure = null;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public StructureData getStructure(String name) {
        return structures.get(name);
    }
    
    public StructureData getSelectedStructure() {
        return selectedStructure;
    }
    
    public void setSelectedStructure(String name) {
        this.selectedStructure = structures.get(name);
    }
    
    public Set<String> getStructureNames() {
        return structures.keySet();
    }
}
