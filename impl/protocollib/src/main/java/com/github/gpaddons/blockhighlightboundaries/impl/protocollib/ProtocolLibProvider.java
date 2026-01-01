package com.github.gpaddons.blockhighlightboundaries.impl.protocollib;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.github.gpaddons.blockhighlightboundaries.BoundaryProvider;
import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.TeamManager;
import com.github.gpaddons.blockhighlightboundaries.type.EntityBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public class ProtocolLibProvider implements BoundaryProvider {

  private boolean useDataValueList;

  @Override
  public boolean isCapable(@NotNull Server server, @NotNull HighlightConfiguration configuration) {
    if (!server.getPluginManager().isPluginEnabled("ProtocolLib")) {
      return false;
    }

    try {
      Class.forName("com.comphenix.protocol.ProtocolLibrary");
      useDataValueList = MinecraftVersion.FEATURE_PREVIEW_UPDATE.atOrAbove();
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  @Override
  public @NotNull EntityBlockHighlight getEntityHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    if (useDataValueList) {
      return new EntityHighlightDataValueList(
          coordinate,
          configuration,
          teamManager,
          boundary,
          visualizationElementType);
    }

    return new EntityHighlightDataWatcher(
        coordinate,
        configuration,
        teamManager,
        boundary,
        visualizationElementType);
  }

}
