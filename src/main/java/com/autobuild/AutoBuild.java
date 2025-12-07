package com.autobuild;

import com.autobuild.client.handler.KeyBindingHandler;
import com.autobuild.common.command.AutoBuildCommand;
import com.autobuild.common.config.AutoBuildConfig;
import com.autobuild.common.data.StructureManager;
import com.autobuild.common.network.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AutoBuild.MOD_ID)
public class AutoBuild {
    public static final String MOD_ID = "autobuild";
    public static final Logger LOGGER = LogManager.getLogger();

    public AutoBuild() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        modEventBus.addListener(this::commonSetup);
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AutoBuildConfig.CLIENT_SPEC);
        
        MinecraftForge.EVENT_BUS.register(this);
        
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(this::clientSetup);
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("AutoBuild mod initializing...");
        NetworkHandler.register();
        
        event.enqueueWork(() -> {
            StructureManager.ensureDirectoryExists();
            StructureManager.loadStructures();
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("AutoBuild client setup...");
        KeyBindingHandler.register();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        AutoBuildCommand.register(event.getDispatcher());
    }
}
