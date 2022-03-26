package com.github.gpaddons.blockhighlightboundaries;

import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.visualization.VisualizationType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface TeamManager {

  void addTeamEntries(
      @NotNull Player player,
      @NotNull VisualizationType type,
      @NotNull VisualizationElementType element,
      @NotNull Iterable<String> values);

  void removeTeamEntries(
      @NotNull Player player,
      @NotNull VisualizationType type,
      @NotNull VisualizationElementType element,
      @NotNull Iterable<String> values);

}
