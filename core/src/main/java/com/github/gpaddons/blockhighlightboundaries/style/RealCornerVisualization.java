package com.github.gpaddons.blockhighlightboundaries.style;

import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.BlockBoundaryVisualization;
import com.griefprevention.visualization.BlockElement;
import com.griefprevention.visualization.Boundary;
import java.util.function.Consumer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link BlockBoundaryVisualization} that always displays the actual depth of the boundary in
 * addition to the visualized depth.
 */
public abstract class RealCornerVisualization extends BlockBoundaryVisualization {

  protected final @NotNull HighlightConfiguration config;

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
