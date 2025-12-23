package com.skyzap.legendarylocator.listeners.pixelmon;

import com.pixelmonmod.pixelmon.api.events.raids.RandomizeRaidEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.DenEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PixelmonListener {
    private static final Map<BlockPos, UUID> announcedDens = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @SubscribeEvent
    public static void onRaidRandomized(RandomizeRaidEvent.ChooseStarLevel event) {
        if (event.isCanceled()) return;

        DenEntity den = event.getDen();
        var level = den.level();

        if (!(level instanceof ServerLevel serverLevel)) return;

        scheduler.schedule(() -> {
            serverLevel.getServer().execute(() -> {
                checkDenForLegendary(den, serverLevel);
            });
        }, 100, TimeUnit.MILLISECONDS);
    }

    private static void checkDenForLegendary(DenEntity den, ServerLevel serverLevel) {
        den.getData().ifPresent(raidData -> {
            Pokemon pokemon = raidData.getPokemon();

            if (pokemon == null || !pokemon.isLegendary()) return;

            BlockPos pos = den.blockPosition();
            UUID pokemonUUID = pokemon.getUUID();

            UUID previousUUID = announcedDens.get(pos);
            if (pokemonUUID.equals(previousUUID)) {
                return;
            }

            announcedDens.put(pos, pokemonUUID);

            broadcastLegendary(
                    serverLevel,
                    pokemon.getSpecies().getName(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ()
            );
        });
    }
    @SubscribeEvent
    public static void onLegendarySpawn(LegendarySpawnEvent.DoSpawn event) {
        if (event.isCanceled()) return;

        var legendarySpawnLocation = event.action.spawnLocation;
        var legendaryEntity = event.getLegendary();

        if (legendaryEntity == null) return;

        BlockPos pos = legendarySpawnLocation.location.pos;
        var level = legendarySpawnLocation.location.world;
        if (!(level instanceof ServerLevel serverLevel)) return;

        serverLevel.getServer().execute(() -> {
            broadcastLegendary(
                    serverLevel,
                    legendaryEntity.getName(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ()
            );
        });
    }

    private static void broadcastLegendary(ServerLevel level, String species, int x, int y, int z) {
        String messageText = String.format("§f[§6LegendaryLocator§f] §e%s §aspawned at x:%s, y:%s, z:%s! §bClick to teleport", species, x, y, z);

        Component message = Component.literal(messageText)
                .withStyle(style -> style.withClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/tp @s " + x + " " + y + " " + z
                        )
                )
        );

        level.getServer()
                .getPlayerList()
                .getPlayers()
                .forEach(player -> player.sendSystemMessage(message, false));
    }


}
