package com.github.gpaddons.blockhighlightboundaries.impl.packetevents2;

import com.github.gpaddons.blockhighlightboundaries.BoundaryProvider;
import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.TeamManager;
import com.github.gpaddons.blockhighlightboundaries.type.EntityBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.PEVersion;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public class PacketEvents2Provider implements BoundaryProvider {

  @Override
  public boolean isCapable(@NotNull Server server, @NotNull HighlightConfiguration configuration) {
    if (!server.getPluginManager().isPluginEnabled("PacketEvents")) {
      return false;
    }

    try {
      Class.forName("io.github.retrooper.packetevents.PacketEvents");
    } catch (ClassNotFoundException e) {
      return false;
    }

    return !PacketEvents.getAPI().getVersion().isOlderThan(new PEVersion(2, 11, 1));
  }

  @Override
  public @NotNull EntityBlockHighlight getEntityHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    return new PacketEventsEntityHighlight(coordinate, configuration, teamManager, boundary, visualizationElementType);
  }

}
