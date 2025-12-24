package com.skyzap.legendarylocator.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class for Minecraft-related operations.
 */
@UtilityClass
public class MinecraftUtils {

  /**
   * Formats a dimension resource location into a display-friendly string.
   *
   * @param dimension The dimension resource location string (e.g.,
   *                  "minecraft:overworld")
   * @return Formatted display string with color codes
   */
  public String formatDimensionName(String dimension) {
    return switch (dimension) {
      case "minecraft:overworld" -> "";
      case "minecraft:the_nether" -> " in §7the Nether";
      case "minecraft:the_end" -> " in §7the End";
      case "pixelmon:ultra_space" -> " in §dUltra Space";
      default -> " §7in " + dimension;
    };
  }
}
