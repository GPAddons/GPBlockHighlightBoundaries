package com.github.gpaddons.blockhighlightboundaries.style;

import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.griefprevention.util.IntVector;
import java.util.HashMap;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link RealCornerVisualization} abstraction that attempts to snap elements to the nearest
 * surface.
 */
public abstract class SnapToSurface extends RealCornerVisualization {

  private static final int SEARCH_DISTANCE = 10;
  private static final BlockFace[] BLOCK_FACES = new BlockFace[]{ BlockFace.DOWN, BlockFace.UP };

  private int lastLoadedDisplayHeight = Integer.MIN_VALUE;

  protected SnapToSurface(
      @NotNull World world,
      @NotNull IntVector visualizeFrom,
      int height,
      @NotNull HighlightConfiguration config) {
    super(world, visualizeFrom, height, config);
  }

  @Override
  protected @NotNull IntVector findDisplayCoordinate(
      @NotNull IntVector displayCoord,
      int minY) {
    if (!displayCoord.isChunkLoaded(world)) {
      // Don't load chunks to find a display coordinate.
      return getDefaultDisplay(displayCoord, minY);
    }

    Block startBlock = world.getBlockAt(displayCoord.x(), displayCoord.y(), displayCoord.z());
    HashMap<Integer, Boolean> transparency = new HashMap<>();

    for (int dY = 1; dY <= SEARCH_DISTANCE; dY++) {
      // Search down 1 then up 1 - find nearest surface faster, but always prioritize snapping down.
      // This prioritizes the floor for areas with low roofs.
      for (BlockFace direction : BLOCK_FACES) {

        int y = startBlock.getY() + dY * direction.getModY();
        if (y < minY) {
          continue;
        }

        if (isEligible(transparency, startBlock, y, direction)) {
          lastLoadedDisplayHeight = y;
          return new IntVector(displayCoord.x(), y, displayCoord.z());
        }
      }
    }

    // If no surface was found to snap to, default based on visibility.
    return config.getType().isAlwaysVisible()
        // Always visible type displays at real boundary edge because it will be shown.
        ? new IntVector(displayCoord.x(), minY, displayCoord.z())
        // Not always visible displays at default level.
        : getDefaultDisplay(displayCoord, minY);
  }

  private boolean isEligible(HashMap<Integer, Boolean> transparency, Block start, int y, BlockFace direction) {
    return !isTransparent(transparency, start, y) && isTransparent(transparency, start, y - direction.getModY());
  }

  private boolean isTransparent(HashMap<Integer, Boolean> transparency, Block start, int y) {
    return transparency.computeIfAbsent(y, key -> {
      Block keyBlock = start.getWorld().getBlockAt(start.getX(), y, start.getZ());
      return isTransparent(keyBlock);
    });
  }

  private boolean isTransparent(Block block) {
    return block.getType().isTransparent();
  }

  @Override
  protected @NotNull IntVector getDefaultDisplay(@NotNull IntVector vector, int minY) {
    // If an element has been successfully displayed, display in line with it in unloaded chunks.
    if (lastLoadedDisplayHeight != Integer.MIN_VALUE) {
      return new IntVector(vector.x(), Math.max(minY, lastLoadedDisplayHeight), vector.z());
    }

    // Fall through to default.
    return super.getDefaultDisplay(vector, minY);
  }

}
