package com.github.gpaddons.blockhighlightboundaries;

import com.github.gpaddons.blockhighlightboundaries.style.HighlightStyle;
import com.github.gpaddons.blockhighlightboundaries.type.HighlightType;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.visualization.Boundary;
import java.awt.Color;
import com.griefprevention.visualization.VisualizationType;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public interface HighlightConfiguration {

  @Range(from = 1, to = 500) int getSpacing();

  @Range(from = 25, to = 500) int getViewDistance();

  @Range(from = 5_000, to = 300_000) int getDisplayMillis();

  int getNextEntityId();

  @NotNull HighlightType getType();

  @NotNull HighlightStyle getStyle();

  @NotNull String getName(@NotNull Boundary boundary, @NotNull VisualizationElementType element);

  @NotNull ChatColor getClosestChatColor(@NotNull VisualizationType type, @NotNull VisualizationElementType element);

  @NotNull Color getColor(@NotNull VisualizationType type, @NotNull VisualizationElementType element);

}
