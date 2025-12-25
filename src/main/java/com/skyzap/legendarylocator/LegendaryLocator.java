package com.skyzap.legendarylocator;

import com.mojang.logging.LogUtils;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.skyzap.legendarylocator.listeners.pixelmon.PixelmonListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(value = LegendaryLocator.MODID, dist = Dist.DEDICATED_SERVER)
public class LegendaryLocator {
    public static final String MODID = "legendarylocator";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LegendaryLocator(IEventBus modEventBus, ModContainer modContainer) {
        if (!ModList.get().isLoaded("pixelmon")) {
            throw new IllegalStateException(
                    "LegendaryLocator requires Pixelmon to be installed on the server");
        }

        Pixelmon.EVENT_BUS.register(PixelmonListener.class);
        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        LOGGER.info("[{}] Pixelmon Legendary Locator loaded", MODID);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[{}] Started", MODID);
    }
}
