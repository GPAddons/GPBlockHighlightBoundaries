package com.github.gpaddons.blockhighlightboundaries.impl.packetevents2;

import com.github.gpaddons.blockhighlightboundaries.BoundaryProvider;
import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.TeamManager;
import com.github.gpaddons.blockhighlightboundaries.type.DebugBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.EntityBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.HighlightType;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.PEVersion;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
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

    if (PacketEvents.getAPI().getVersion().isOlderThan(new PEVersion(2, 11, 1))) {
      return false;
    }

    if (configuration.getType() == HighlightType.DEBUG_BLOCK) {
      Plugin plugin = server.getPluginManager().getPlugin("GPBlockHighlightBoundaries");
      Logger logger = plugin != null ? plugin.getLogger() : server.getLogger();
      logger.warning("PacketEvents 2 does not (or did not yet) support custom payloads.");
      logger.warning("This means that GPBHB cannot display DEBUG_BLOCK type boundaries.");
      logger.warning("Please edit your configuration to use GLOWING_ENTITY instead.");
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
    throw new UnsupportedOperationException("PacketEvents 2.0-SNAPSHOT does not support custom payloads.");
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
