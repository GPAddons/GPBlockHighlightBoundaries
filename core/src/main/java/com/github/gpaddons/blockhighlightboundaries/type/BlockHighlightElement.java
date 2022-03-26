package com.github.gpaddons.blockhighlightboundaries.type;

import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.BlockElement;
import com.griefprevention.visualization.Boundary;
import org.jetbrains.annotations.NotNull;

public abstract class BlockHighlightElement extends BlockElement {

  protected final HighlightConfiguration configuration;
  protected final Boundary boundary;
  protected final VisualizationElementType visualizationElementType;

  /**
   * Construct a new {@code BlockElement} with the given coordinate.
   *
   * @param coordinate the in-world coordinate of the element
   * @param configuration the configuration for highlights
   * @param boundary the boundary the element belongs to
   * @param visualizationElementType the type of element being visualized
   */
  public BlockHighlightElement(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    super(coordinate);
    this.configuration = configuration;
    this.boundary = boundary;
    this.visualizationElementType = visualizationElementType;
  }

  public String getName() {
    return configuration.getName(boundary, visualizationElementType);
  }

  public abstract boolean requiresErase();

}
