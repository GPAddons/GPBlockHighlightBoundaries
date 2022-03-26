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

public abstract class RealCornerVisualization extends BlockBoundaryVisualization {

  protected final @NotNull HighlightConfiguration config;

  protected RealCornerVisualization(
      @NotNull World world,
      @NotNull IntVector visualizeFrom,
      int height,
      @NotNull HighlightConfiguration config) {
    super(world, visualizeFrom, height, config.getSpacing(), config.getViewDistance());
    this.config = config;
  }

  @Override
  protected @NotNull Consumer<@NotNull IntVector> addCornerElements(
      @NotNull Boundary boundary) {
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
  protected @NotNull Consumer<@NotNull IntVector> addSideElements(
      @NotNull Boundary boundary) {
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

  protected abstract @NotNull BlockElement getElement(
      @NotNull Boundary boundary,
      @NotNull IntVector location,
      @NotNull VisualizationElementType visualizationElementType);

  protected @NotNull IntVector getDefaultDisplay(@NotNull IntVector vector, int minY) {
    // As most displays are triggered at eye level, moving 2 blocks deeper by default
    // makes the display far less obstructive while still being clearly visible.
    // This is later normalized to boundary max depth.
    return new IntVector(vector.x(), Math.max(minY, vector.y() - 2), vector.z());
  }

}
