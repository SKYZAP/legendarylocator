package com.skyzap.legendarylocator.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.skyzap.legendarylocator.LegendaryLocator;
import com.skyzap.legendarylocator.util.MinecraftUtils;
import lombok.experimental.UtilityClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

/**
 * Custom teleport command for LegendaryLocator.
 * Uses Minecraft's ServerPlayer.teleportTo() API for cross-dimension
 * teleportation.
 */
@UtilityClass
public class TeleportCommand {

  /**
   * Registers the /lltp command with the command dispatcher.
   */
  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(
        Commands.literal("lltp")
            .then(Commands.argument("dimension", ResourceLocationArgument.id())
                .then(Commands.argument("x", IntegerArgumentType.integer())
                    .then(Commands.argument("y", IntegerArgumentType.integer())
                        .then(Commands.argument("z", IntegerArgumentType.integer())
                            .executes(context -> executeTeleport(
                                context.getSource(),
                                ResourceLocationArgument.getId(context, "dimension"),
                                IntegerArgumentType.getInteger(context, "x"),
                                IntegerArgumentType.getInteger(context, "y"),
                                IntegerArgumentType.getInteger(context, "z"))))))));
  }

  private static int executeTeleport(CommandSourceStack source, ResourceLocation dimension, int x, int y, int z) {
    if (!(source.getEntity() instanceof ServerPlayer player)) {
      source.sendFailure(Component.literal("This command can only be executed by a player"));
      return 0;
    }

    MinecraftServer server = source.getServer();
    ServerLevel targetLevel = findTargetLevel(server, dimension);

    if (targetLevel == null) {
      source.sendFailure(Component.literal("Unknown dimension: " + dimension));
      return 0;
    }

    return teleportPlayer(player, targetLevel, x, y, z);
  }

  private static ServerLevel findTargetLevel(MinecraftServer server, ResourceLocation dimension) {
    for (ServerLevel level : server.getAllLevels()) {
      if (level.dimension().location().equals(dimension)) {
        return level;
      }
    }

    return null;
  }

  private static int teleportPlayer(ServerPlayer player, ServerLevel targetLevel, int x, int y, int z) {
    boolean crossDimension = player.level() != targetLevel;

    double targetX = x + 0.5;
    double targetY = y;
    double targetZ = z + 0.5;

    float yRot = player.getYRot();
    float xRot = player.getXRot();

    if (crossDimension) {
      player.teleportTo(
          targetLevel,
          targetX,
          targetY,
          targetZ,
          Set.of(),
          yRot,
          xRot);
    } else {
      player.teleportTo(targetX, targetY, targetZ);
    }

    String dimensionName = MinecraftUtils.formatDimensionName(targetLevel.dimension().location().toString());
    player.sendSystemMessage(Component.literal(
        String.format("§a[LegendaryLocator] Teleported to x:%d, y:%d, z:%d%s", x, y, z, dimensionName)));

    LegendaryLocator.LOGGER.info("Player {} teleported to {} at ({}, {}, {})",
        player.getName().getString(),
        targetLevel.dimension().location(),
        x, y, z);

    return 1;
  }
}
