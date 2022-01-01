package com.github.gpaddons.blockhighlightboundaries;

import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.BlockBoundaryVisualization;
import com.griefprevention.visualization.Boundary;
import com.griefprevention.visualization.VisualizationType;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// TODO might actually want to use GP techique of hugging ground by default within
//  a range - while display in sky is fine, ground may be easier for players to see.
public class BlockHighlightVisualization extends BlockBoundaryVisualization {

  private final GPBlockHighlightBoundaries plugin;
  private final Map<ColorPath, Color> colorCache = new HashMap<>();

  protected BlockHighlightVisualization(
      @NotNull GPBlockHighlightBoundaries plugin,
      @NotNull World world,
      @NotNull IntVector visualizeFrom,
      int height) {
    super(
        world,
        visualizeFrom,
        height,
        limit(1, 500, plugin.getConfig().getInt("spacing")),
        limit(25, 500, plugin.getConfig().getInt("viewDistance")));
    this.plugin = plugin;
  }

  @Override
  protected void scheduleRevert(@NotNull Player player, @NotNull PlayerData playerData) {
    // Do nothing - elements automatically revert after configured time.
  }

  // Note: while it's possible to batch revert via minecraft:debug/game_test_clear,
  // that clears all active debug blocks, not just ones we've added.

  @Override
  protected @NotNull Consumer<@NotNull IntVector> addCornerElements(@NotNull Boundary boundary) {
    return addElement(boundary, "corner");
  }

  @Override
  protected @NotNull Consumer<@NotNull IntVector> addSideElements(@NotNull Boundary boundary) {
    return addElement(boundary, "side");
  }

  private @NotNull Consumer<@NotNull IntVector> addElement(
      @NotNull Boundary boundary,
      @NotNull String detailPath) {
    Color color = getColor(boundary, detailPath);
    String name = getName(boundary, detailPath);
    int displayMillis = limit(5, 300, plugin.getConfig().getInt("displaySeconds", 60)) * 1000;

    // TODO other adapter support
    return vector -> {
      elements.add(new ProtocolLibElement(
          new IntVector(vector.x(), boundary.bounds().getMinY(), vector.z()),
          color,
          name,
          displayMillis));
      // If element is over 5 blocks below, also display at visualization level
      if (boundary.bounds().getMinY() < vector.y() - 7) {
        elements.add(new ProtocolLibElement(vector.add(0, -2, 0), color, name, displayMillis));
      }
    };
  }

  protected String getName(@NotNull Boundary boundary, @NotNull String detailPath) {
    // TODO support ClaimslistClassifier?
    return "";
  }

  private Color getColor(@NotNull Boundary boundary, @NotNull String detailPath) {
    ColorPath colorPath = new ColorPath(boundary.type(), detailPath);

    return colorCache.computeIfAbsent(colorPath, key -> new Color(
        sanitizeColor(key.path("red")),
        sanitizeColor(key.path("green")),
        sanitizeColor(key.path("blue")),
        sanitizeColor(key.path("alpha"))));
  }

  private int sanitizeColor(String path) {
    return limit(0, 255, plugin.getConfig().getInt(path, 255));
  }

  private record ColorPath(@NotNull VisualizationType type, @NotNull String detailPath) {
    private String path(@NotNull String element) {
      return String.format("markers.%s.%s.%s", type().name(), detailPath(), element);
    }
  }

  private static int limit(int min, int max, int actual) {
    return Math.max(min, Math.min(max, actual));
  }

}
