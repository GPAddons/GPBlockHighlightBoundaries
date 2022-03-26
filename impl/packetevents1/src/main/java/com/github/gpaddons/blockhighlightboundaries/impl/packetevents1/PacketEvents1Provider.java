package com.github.gpaddons.blockhighlightboundaries.impl.packetevents1;

import com.github.gpaddons.blockhighlightboundaries.BoundaryProvider;
import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.TeamManager;
import com.github.gpaddons.blockhighlightboundaries.type.DebugBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.EntityBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.HighlightType;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.version.PEVersion;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.util.logging.Logger;

public class PacketEvents1Provider implements BoundaryProvider {

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

    // PacketEvents 2 removed PacketEvents#get, have to use deprecated option for compatibility.
    if (PacketEvents.getAPI().getVersion().isNewerThan(new PEVersion(2, -1))) {
      return false;
    }

    if (configuration.getType() == HighlightType.GLOWING_ENTITY) {
      Plugin plugin = server.getPluginManager().getPlugin("GPBlockHighlightBoundaries");
      Logger logger = plugin != null ? plugin.getLogger() : server.getLogger();
      logger.warning("PacketEvents 1 does not support entity metadata.");
      logger.warning("This means that GPBHB cannot display GLOWING_ENTITY type boundaries.");
      logger.warning("Please edit your configuration to use DEBUG_BLOCK instead.");
      return false;
    }

    return true;
  }

  @Override
  public @NotNull DebugBlockHighlight getDebugHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    return new PacketEventsDebugHighlight(coordinate, configuration, boundary, visualizationElementType);
  }

  @Override
  public @NotNull EntityBlockHighlight getEntityHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    throw new UnsupportedOperationException("PacketEvents 1 does not support writing entity metadata.");
  }

}
