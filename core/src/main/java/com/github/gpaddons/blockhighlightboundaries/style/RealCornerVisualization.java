package com.github.gpaddons.blockhighlightboundaries.style;

import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.type.BlockHighlightElement;
import com.github.gpaddons.blockhighlightboundaries.type.FallThroughElement;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.BlockBoundaryVisualization;
import com.griefprevention.visualization.BlockElement;
import com.griefprevention.visualization.Boundary;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link BlockBoundaryVisualization} that always displays the actual depth of the boundary in
 * addition to the visualized depth.
 */
public abstract class RealCornerVisualization extends BlockBoundaryVisualization {

  protected final @NotNull HighlightConfiguration config;
  private long lastSend = 0;

  /**
   * Construct a new {@code RealCornerVisualization} with the given configuration.
   *
   * @param world the {@link World} being visualized in
   * @param visualizeFrom the {@link IntVector} representing the world coordinate being visualized from
   * @param height the height of the visualization
   * @param config the {@link HighlightConfiguration} containing additional settings
   */
  protected RealCornerVisualization(
      @NotNull World world,
      @NotNull IntVector visualizeFrom,
      int height,
      @NotNull HighlightConfiguration config) {
    super(world, visualizeFrom, height, config.getSpacing(), config.getViewDistance());
    this.config = config;
  }

  @Override
  protected @NotNull Consumer<@NotNull IntVector> addCornerElements(@NotNull Boundary boundary) {
    return vector -> {
      int minY = boundary.bounds().getMinY();
      // Display at visualization level.
      IntVector coordinate = findDisplayCoordinate(vector, minY);
      if (minY != coordinate.y()) {
        elements.add(getElement(boundary, coordinate, VisualizationElementType.CORNER));
      }
      // Always display actual bottom corners as well.
      elements.add(
          getElement(
              boundary,
              new IntVector(vector.x(), minY, vector.z()),
              VisualizationElementType.CORNER));
    };
  }

  @Override
  protected @NotNull Consumer<@NotNull IntVector> addSideElements(@NotNull Boundary boundary) {
    return vector -> {
      IntVector coordinate = findDisplayCoordinate(vector,boundary.bounds().getMinY());
      elements.add(getElement(boundary, coordinate, VisualizationElementType.SIDE));

      // If elements are always visible, display side bottoms as well.
      if (config.getType().isAlwaysVisible() && boundary.bounds().getMinY() < coordinate.y()) {
        elements.add(
            getElement(
                boundary,
                new IntVector(vector.x(), boundary.bounds().getMinY(), vector.z()),
                VisualizationElementType.SIDE));
      }
    };
  }

  @Override
  protected void draw(@NotNull Player player,
      @NotNull Boundary boundary) {
    this.lastSend = System.currentTimeMillis();
    try {
      super.draw(player, boundary);
    } catch (Exception e) {
      Collection<BlockElement> fallthroughElements = new ArrayList<>();
      for (BlockElement element : this.elements) {
        if (element instanceof BlockHighlightElement highlightElement) {
          ChatColor color = config.getClosestChatColor(boundary.type(), highlightElement.getElementType());
          fallthroughElements.add(new FallThroughElement(element.getCoordinate(), color));
        } else {
          // Somehow not our element? Someone else's problem if it fails again.
          fallthroughElements.add(element);
        }
      }

      this.elements.clear();
      this.elements.addAll(fallthroughElements);

      // Re-try the draw.
      super.draw(player, boundary);

      // TODO get logger instance in a better way (via config? Seems hacky, but it's an impl dao)
      Logger logger = Logger.getLogger("GPBlockHighlightBoundaries");
      logger.log(Level.SEVERE, "Caught exception while visualizing a claim! Please report this:");
      logger.log(Level.SEVERE, e, () -> "Visualization error");
    }
  }

  @Override
  protected void scheduleRevert(@NotNull Player player, @NotNull PlayerData playerData) {
    // Use GP to schedule the revert - we don't have a concept of a managing plugin here, the plugin
    // is the implementation. Basically the same as super method but with a configurable delay.
    GriefPrevention.instance.getServer().getScheduler().scheduleSyncDelayedTask(
        GriefPrevention.instance,
        () -> {
          // Only revert if this is the active visualization.
          if (playerData.getVisibleBoundaries() == this) revert(player);
        },
        config.getDisplayMillis() / 50);
  }

  @Override
  public void revert(@Nullable Player player) {
    if (!config.getType().requiresErase()
        && (lastSend + config.getDisplayMillis()) < System.currentTimeMillis()) {
      // If already erased, do not send more packets.
      return;
    }

    super.revert(player);
  }

  /**
   * Find an eligible display coordinate in the same column as the given coordinate.
   *
   * @param displayCoord the provided coordinate
   * @param minY the minimum Y value of the resulting coordinate
   * @return the display coordinate
   */
  protected abstract @NotNull IntVector findDisplayCoordinate(
      @NotNull IntVector displayCoord,
      int minY);

  /**
   * Create a {@link BlockElement} with the given parameters.
   *
   * @param boundary the {@link Boundary} the element represents
   * @param location the coordinate to display at
   * @param visualizationElementType the type of element being drawn
   * @return the element created
   */
  protected abstract @NotNull BlockElement getElement(
      @NotNull Boundary boundary,
      @NotNull IntVector location,
      @NotNull VisualizationElementType visualizationElementType);

  /**
   * Get the default display coordinate for a given coordinate.
   *
   * @param vector the provided coordinate
   * @param minY the minimum Y value of the resulting coordinate
   * @return the default display coordinate
   */
  protected @NotNull IntVector getDefaultDisplay(@NotNull IntVector vector, int minY) {
    // As most displays are triggered at eye level, moving 2 blocks deeper by default
    // makes the display far less obstructive while still being clearly visible.
    return new IntVector(vector.x(), Math.max(minY, vector.y() - 2), vector.z());
  }

}
