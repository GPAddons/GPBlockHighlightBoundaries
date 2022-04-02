package com.github.gpaddons.blockhighlightboundaries;

import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.visualization.VisualizationType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for managing team entries for fake glowing entities.
 */
public interface TeamManager {

  /**
   * Add entries to a team for a {@link Player}.
   *
   * @param player the recipient
   * @param type the type of visualization
   * @param element the type of element in the visualization
   * @param values the entries to add
   */
  void addTeamEntries(
      @NotNull Player player,
      @NotNull VisualizationType type,
      @NotNull VisualizationElementType element,
      @NotNull Iterable<String> values);

  /**
   * Remove entried from a team for a {@link Player}.
   *
   * @param player the recipient
   * @param type the type of visualization
   * @param element the type of element in the visualization
   * @param values the entries to remove
   */
  void removeTeamEntries(
      @NotNull Player player,
      @NotNull VisualizationType type,
      @NotNull VisualizationElementType element,
      @NotNull Iterable<String> values);

}
