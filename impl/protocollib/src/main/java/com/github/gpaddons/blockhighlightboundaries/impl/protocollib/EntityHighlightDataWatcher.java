package com.github.gpaddons.blockhighlightboundaries.impl.protocollib;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.TeamManager;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import org.jetbrains.annotations.NotNull;

class EntityHighlightDataWatcher extends EntityHighlight<WrappedDataWatcher> {

  /**
   * Construct a new {@code EntityBlockHighlight} with the given coordinate.
   *
   * @param coordinate the in-world coordinate of the element
   * @param configuration the configuration for highlights
   * @param teamManager the scoreboard team manager
   * @param boundary the boundary the element belongs to
   * @param visualizationElementType the type of element being visualized
   */
  public EntityHighlightDataWatcher(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    super(coordinate, configuration, teamManager, boundary, visualizationElementType);
  }

  @Override
  protected @NotNull WrappedDataWatcher create() {
    return new WrappedDataWatcher();
  }

  @Override
  protected void addData(
      @NotNull WrappedDataWatcher values,
      int index,
      @NotNull Serializer serializer,
      @NotNull Object value) {
    values.setObject(index, serializer, value);
  }

  @Override
  protected void write(@NotNull PacketContainer container, @NotNull WrappedDataWatcher data) {
    container.getWatchableCollectionModifier().write(0, data.getWatchableObjects());
  }

}
