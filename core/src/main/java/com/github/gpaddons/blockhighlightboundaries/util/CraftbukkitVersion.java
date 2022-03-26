package com.github.gpaddons.blockhighlightboundaries.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * A utility for fetching Craftbukkit version information. Note that CB revision may not be in sync
 * with MC patch version!
 */
public record CraftbukkitVersion(int major, int minor, int revision)
    implements Comparable<CraftbukkitVersion> {

  private static CraftbukkitVersion craftbukkitVersion;

  public static @NotNull CraftbukkitVersion getInstance() {
    if (craftbukkitVersion == null) {
      craftbukkitVersion = parseServerVersion();
    }

    return craftbukkitVersion;
  }

  private static @NotNull CraftbukkitVersion parseServerVersion() {
    String versionString = Bukkit.getServer().getClass().getPackageName();
    int lastDivider = versionString.lastIndexOf('.');
    versionString = versionString.substring(lastDivider + 1);

    // Early CB: 1_4_5 etc; modern CB: v1_8_R1
    Matcher matcher = Pattern.compile("v?(\\d+)_(\\d+)_R?(\\d+)").matcher(versionString);

    if (!matcher.matches()) {
      // Even earlier CB did not version packages, no way to know for sure what the version is.
      return new CraftbukkitVersion(0, 0, 0);
    }

    try {
      return new CraftbukkitVersion(
          Integer.parseInt(matcher.group(1)),
          Integer.parseInt(matcher.group(2)),
          Integer.parseInt(matcher.group(3)));
    } catch (NumberFormatException exception) {
      // Shouldn't be possible based on regex.
      return new CraftbukkitVersion(0, 0, 0);
    }
  }

  public boolean atOrAbove(@NotNull CraftbukkitVersion other) {
    return compareTo(other) > -1;
  }

  @Override
  public int compareTo(@NotNull CraftbukkitVersion other) {

    List<ToIntFunction<CraftbukkitVersion>> toIntFunctions = Arrays.asList(
        CraftbukkitVersion::major,
        CraftbukkitVersion::minor,
        CraftbukkitVersion::revision);

    for (ToIntFunction<CraftbukkitVersion> version : toIntFunctions) {
      int otherVersion = version.applyAsInt(other);
      int thisVersion = version.applyAsInt(this);

      if (otherVersion > thisVersion) {
        return -1;
      } else if (otherVersion < thisVersion) {
        return 1;
      }
    }

    return 0;
  }

}
