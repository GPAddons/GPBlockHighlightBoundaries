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

/**
 * An interface defining behavior for a {@link VisualizationProvider} implementation wrapper.
 */
public interface BoundaryProvider {

  /**
   * Get whether the {@link VisualizationProvider} is capable of functioning properly.
   *
   * @param server the active server implementation
   * @param configuration the highlight configuration
   * @return whether the provider is capable of functioning
   */
  boolean isCapable(@NotNull Server server, @NotNull HighlightConfiguration configuration);

  /**
   * Get a {@link VisualizationProvider} implemenation.
   *
   * @param configuration the highlight configuration
   * @param teamManager the team manager implementation
   * @return the visualization provider implementation
   */
  default @NotNull VisualizationProvider getProvider(
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager) {

    TriFunction<Boundary, IntVector, VisualizationElementType, BlockElement> getElement
        = (boundary, vector, elementType) ->
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

  /**
   * Method for obtaining a
   * {@link com.github.gpaddons.blockhighlightboundaries.type.HighlightType#DEBUG_BLOCK DEBUG_BLOCK}
   * highlight implementation for the given parameters.
   *
   * @param coordinate the location of the element
   * @param configuration the highlight configuration
   * @param boundary the boundary being visualized
   * @param visualizationElementType the type of element in the boundary being visualized
   * @return the {@link DebugBlockHighlight} created
   */
  @NotNull DebugBlockHighlight getDebugHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType);

  /**
   * Method for obtaining a
   * {@link com.github.gpaddons.blockhighlightboundaries.type.HighlightType#GLOWING_ENTITY GLOWING_ENTITY}
   * highlight implementation for the given parameters.
   *
   * @param coordinate the location of the element
   * @param configuration the highlight configuration
   * @param teamManager the {@link TeamManager} implementation
   * @param boundary the boundary being visualized
   * @param visualizationElementType the type of element in the boundary being visualized
   * @return the {@link EntityBlockHighlight} created
   */
  @NotNull EntityBlockHighlight getEntityHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType);

}
