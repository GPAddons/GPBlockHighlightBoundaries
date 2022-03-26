package com.github.gpaddons.blockhighlightboundaries;

import com.github.gpaddons.blockhighlightboundaries.style.HighlightStyle;
import com.github.gpaddons.blockhighlightboundaries.type.HighlightType;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.visualization.Boundary;
import com.griefprevention.visualization.VisualizationType;
import java.awt.Color;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class PluginHighlightConfiguration implements HighlightConfiguration {

  private final Plugin plugin;
  private @Nullable Integer spacing;
  private @Nullable Integer viewDistance;
  private @Nullable Integer displayMillis;
  private AtomicInteger nextEntityId;
  private @Nullable HighlightType highlightType;
  private @Nullable HighlightStyle highlightStyle;
  private final Map<ColorPath, Color> colorCache = new HashMap<>();
  private final Map<ColorPath, ChatColor> chatColorCache = new HashMap<>();

  PluginHighlightConfiguration(Plugin plugin) {
    this.plugin = plugin;
  }

  void reload() {
    spacing = null;
    viewDistance = null;
    displayMillis = null;
    highlightType = null;
    highlightStyle = null;
    colorCache.clear();
    chatColorCache.clear();
  }

  @Override
  public @Range(from = 1, to = 500) int getSpacing() {
    if (spacing == null) {
      spacing = limit(1, 500, plugin.getConfig().getInt("spacing", 10));
    }

    return spacing;
  }

  @Override
  public @Range(from = 25, to = 500) int getViewDistance() {
    if (viewDistance == null) {
      viewDistance = limit(25, 500, plugin.getConfig().getInt("viewDistance", 150));
    }

    return viewDistance;
  }

  @Override
  public @Range(from = 5_000, to = 300_000) int getDisplayMillis() {
    if (displayMillis == null) {
      displayMillis = limit(5_000, 300_000, plugin.getConfig().getInt("displaySeconds", 60) * 1_000);
    }

    return displayMillis;
  }

  @Override
  public int getNextEntityId() {
    if (nextEntityId == null) {
      nextEntityId = new AtomicInteger(plugin.getConfig().getInt("advanced.startEid", -1));
    }

    return nextEntityId.getAndDecrement();
  }

  @Override
  public @NotNull HighlightType getType() {
    if (highlightType == null) {
      highlightType = parseEnum(plugin.getConfig().getString("type", "GLOWING_ENTITY"), HighlightType.GLOWING_ENTITY);
    }

    return highlightType;
  }

  @Override
  public @NotNull HighlightStyle getStyle() {
    if (highlightStyle == null) {
      highlightStyle = parseEnum(plugin.getConfig().getString("style", "FLAT"), HighlightStyle.FLAT);
    }

    return highlightStyle;
  }

  @Override
  public @NotNull String getName(
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType element) {
    // FEATURE support ClaimslistClassifier
    return "";
  }

  @Override
  public @NotNull ChatColor getClosestChatColor(
      @NotNull VisualizationType type,
      @NotNull VisualizationElementType element) {
    ColorPath colorPath = new ColorPath(type, element);

    return chatColorCache.computeIfAbsent(colorPath, key -> asChatColor(getColor(colorPath)));
  }

  @Override
  public @NotNull Color getColor(
      @NotNull VisualizationType type,
      @NotNull VisualizationElementType element) {
    ColorPath colorPath = new ColorPath(type, element);

    return getColor(colorPath);
  }

  private @NotNull Color getColor(@NotNull ColorPath colorPath) {

    return colorCache.computeIfAbsent(colorPath, key -> new Color(
        sanitizeColor(key.path("red")),
        sanitizeColor(key.path("green")),
        sanitizeColor(key.path("blue")),
        sanitizeColor(key.path("alpha"))));
  }

  private int sanitizeColor(String path) {
    return limit(0, 255, plugin.getConfig().getInt(path, 255));
  }

  private static int limit(int min, int max, int actual) {
    return Math.max(min, Math.min(max, actual));
  }

  private static <T extends Enum<T>> @NotNull T parseEnum(
      @NotNull String value,
      @NotNull T defaultValue) {
    for (T enumConstant : defaultValue.getDeclaringClass().getEnumConstants()) {
      if (enumConstant.name().equalsIgnoreCase(value)) {
        return enumConstant;
      }
    }

    return defaultValue;
  }

  private static final Map<ChatColor, Vector> COLOR_VECTORS = new EnumMap<>(ChatColor.class);

  static {
    Arrays.stream(ChatColor.values()).filter(ChatColor::isColor).forEach(chatColor -> {
      Color color = chatColor.asBungee().getColor();
      Vector vector = new Vector(color.getRed(), color.getGreen(), color.getBlue());
      COLOR_VECTORS.put(chatColor, vector);
    });
  }

  private static @NotNull ChatColor asChatColor(@NotNull Color color) {
    Vector colorVector = new Vector(color.getRed(), color.getGreen(), color.getBlue());
    // Alpha -> greyscaling, reduce all colors by percentage.
    colorVector.multiply(color.getAlpha() / 255.0D);

    ChatColor bestMatch = ChatColor.WHITE;
    double bestDistance = Double.MAX_VALUE;

    for (Entry<ChatColor, Vector> chatVectorEntry : COLOR_VECTORS.entrySet()) {
      Vector chatVector = chatVectorEntry.getValue().clone();

      if (colorVector.equals(chatVector)) {
        return chatVectorEntry.getKey();
      }

      // No need to get square root, effectively the same result either way.
      double distance = colorVector.distanceSquared(chatVector);
      if (distance < bestDistance) {
        bestDistance = distance;
        bestMatch = chatVectorEntry.getKey();
      }
    }

    return bestMatch;
  }

  private record ColorPath(@NotNull VisualizationType visualization, @NotNull VisualizationElementType element) {
    public String path(@NotNull String element) {
      return String.format("colors.%s.%s.%s", visualization().name(), element().name(), element);
    }
  }

}
