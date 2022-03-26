package com.github.gpaddons.blockhighlightboundaries.impl.protocollib;

import com.github.gpaddons.blockhighlightboundaries.BoundaryProvider;
import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.TeamManager;
import com.github.gpaddons.blockhighlightboundaries.type.DebugBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.EntityBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public class ProtocolLibProvider implements BoundaryProvider {

  @Override
  public boolean isCapable(@NotNull Server server, @NotNull HighlightConfiguration configuration) {
    if (!server.getPluginManager().isPluginEnabled("ProtocolLib")) {
      return false;
    }

    try {
      Class.forName("com.comphenix.protocol.ProtocolLibrary");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  @Override
  public @NotNull DebugBlockHighlight getDebugHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    return new ProtocolLibDebugHighlight(
        coordinate,
        configuration,
        boundary,
        visualizationElementType);
  }

  @Override
  public @NotNull EntityBlockHighlight getEntityHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    return new ProtocolLibEntityHighlight(
        coordinate,
        configuration,
        teamManager,
        boundary,
        visualizationElementType);
  }

}
