package com.github.gpaddons.blockhighlightboundaries;

import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.visualization.VisualizationType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginTeamManager implements TeamManager {

  private final Plugin plugin;
  private final HighlightConfiguration configuration;

  public PluginTeamManager(@NotNull Plugin plugin, @NotNull HighlightConfiguration configuration) {
    this.plugin = plugin;
    this.configuration = configuration;
  }

  void reload() {
    ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();

    if (scoreboardManager == null) {
      // If worlds aren't loaded, players can't be online to have per-player scoreboards anyway.
      return;
    }

    Scoreboard mainScoreboard = scoreboardManager.getMainScoreboard();

    for (Player player : plugin.getServer().getOnlinePlayers()) {
      Scoreboard scoreboard = player.getScoreboard();
      if (scoreboard != mainScoreboard) {
        reloadScoreboard(scoreboard);
      }
    }

    reloadScoreboard(mainScoreboard);
  }

  private void reloadScoreboard(Scoreboard scoreboard) {
    for (Team team : scoreboard.getTeams()) {
      if (!team.getName().startsWith("gpbhb__")) {
        continue;
      }

      String[] nameElements = team.getName().split("__");
      if (nameElements.length < 3) {
        continue;
      }

      VisualizationType type = parseEnum(nameElements[1], VisualizationType.class);
      VisualizationElementType element = parseEnum(nameElements[2], VisualizationElementType.class);

      if (type == null || element == null) {
        continue;
      }

      team.setColor(configuration.getClosestChatColor(type, element));
    }
  }

  private static <T extends Enum<T>> @Nullable T parseEnum(
      @NotNull String value,
      @NotNull Class<T> enumClass) {
    for (T enumConstant : enumClass.getEnumConstants()) {
      if (enumConstant.name().equalsIgnoreCase(value)) {
        return enumConstant;
      }
    }

    return null;
  }

  @Override
  public void addTeamEntries(
      @NotNull Player player,
      @NotNull VisualizationType type,
      @NotNull VisualizationElementType element,
      @NotNull Iterable<String> values) {
    Team team = getOrSetUpTeam(player.getScoreboard(), type, element);
    values.forEach(team::addEntry);

    // Also set up main scoreboard if player is on player-specific scoreboard.
    // This prevents issues with plugins like mcMMO where players frequently see per-user details.
    ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
    if (scoreboardManager != null && player.getScoreboard() != scoreboardManager.getMainScoreboard()) {
      team = getOrSetUpTeam(scoreboardManager.getMainScoreboard(), type, element);
      values.forEach(team::addEntry);
    }

  }

  private @NotNull Team getOrSetUpTeam(
      @NotNull Scoreboard scoreboard,
      @NotNull VisualizationType type,
      @NotNull VisualizationElementType element) {
    String name = getTeamName(type, element);

    Team team = scoreboard.getTeam(name);
    if (team != null) {
      return team;
    }

    team = scoreboard.registerNewTeam(name);
    team.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
    team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
    team.setColor(configuration.getClosestChatColor(type, element));

    return team;
  }

  @Override
  public void removeTeamEntries(
      @NotNull Player player,
      @NotNull VisualizationType type,
      @NotNull VisualizationElementType element,
      @NotNull Iterable<String> values) {
    String name = getTeamName(type, element);

    Scoreboard scoreboard = player.getScoreboard();
    Team team = scoreboard.getTeam(name);
    if (team != null) {
      values.forEach(team::removeEntry);
    }

    ScoreboardManager manager = plugin.getServer().getScoreboardManager();
    if (manager == null) {
      return;
    }

    if (manager.getMainScoreboard() != scoreboard) {
      scoreboard = manager.getMainScoreboard();
      team = scoreboard.getTeam(name);
      if (team != null) {
        values.forEach(team::removeEntry);
      }
    }
  }

  private @NotNull String getTeamName(
      @NotNull VisualizationType type,
      @NotNull VisualizationElementType element) {
    return String.format("gpbhb__%s__%s", type, element);
  }

  void cleanUp() {
    ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
    if (scoreboardManager != null) {
      scoreboardManager.getMainScoreboard().getTeams().stream()
          .filter(team -> team.getName().startsWith("gpbhb__"))
          .forEach(Team::unregister);
    }
  }

}
