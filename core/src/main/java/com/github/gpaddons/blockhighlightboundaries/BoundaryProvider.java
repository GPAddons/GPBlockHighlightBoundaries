package com.github.gpaddons.blockhighlightboundaries;

import com.github.gpaddons.blockhighlightboundaries.style.RealCornerVisualization;
import com.github.gpaddons.blockhighlightboundaries.style.SnapToSurface;
import com.github.gpaddons.blockhighlightboundaries.type.DebugBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.EntityBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.github.gpaddons.blockhighlightboundaries.util.TriFunction;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.BlockElement;
import com.griefprevention.visualization.Boundary;
import com.griefprevention.visualization.VisualizationProvider;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public interface BoundaryProvider {

  boolean isCapable(@NotNull Server server, @NotNull HighlightConfiguration configuration);

  default @NotNull VisualizationProvider getProvider(
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager) {

    TriFunction<Boundary, IntVector, VisualizationElementType, BlockElement> getElement = (boundary, vector, elementType) ->
        switch (configuration.getType()) {
      case DEBUG_BLOCK -> getDebugHighlight(vector, configuration, boundary, elementType);
      case GLOWING_ENTITY -> getEntityHighlight(vector, configuration, teamManager, boundary, elementType);
    };

    return (world, visualizeFrom, height) -> switch (configuration.getStyle()) {
      case FLAT -> new RealCornerVisualization(world, visualizeFrom, height, configuration) {
        @Override
        protected @NotNull IntVector findDisplayCoordinate(
            @NotNull IntVector displayCoord, int minY) {
          return getDefaultDisplay(displayCoord, minY);
        }

        @Override
        protected @NotNull BlockElement getElement(
            @NotNull Boundary boundary, @NotNull IntVector location,
            @NotNull VisualizationElementType visualizationElementType) {
          return getElement.apply(boundary, location, visualizationElementType);
        }
      };

      case SNAP_TO_SURFACE -> new SnapToSurface(world, visualizeFrom, height, configuration) {
        @Override
        protected @NotNull BlockElement getElement(
            @NotNull Boundary boundary, @NotNull IntVector location,
            @NotNull VisualizationElementType visualizationElementType) {
          return getElement.apply(boundary, location, visualizationElementType);
        }
      };

    };
  }

  @NotNull DebugBlockHighlight getDebugHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType);

  @NotNull EntityBlockHighlight getEntityHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType);

}
