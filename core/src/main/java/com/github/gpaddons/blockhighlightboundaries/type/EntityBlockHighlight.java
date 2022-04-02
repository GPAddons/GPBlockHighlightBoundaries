package com.github.gpaddons.blockhighlightboundaries.type;

import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.Problem;
import com.github.gpaddons.blockhighlightboundaries.TeamManager;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

/**
 * A {@link BlockHighlightElement} abstraction for using glowing entities to highlight blocks.
 *
 * <p>Internally this uses magma cubes of size 1. Size 2 highlights blocks very nicely, but prevents
 * interaction. As the entities are clientside and we do not do additional packet handling, they are
 * not removed on interaction.
 */
public abstract class EntityBlockHighlight extends BlockHighlightElement {

  private static final double[] HORIZONTALS = { 0.31, 0.69 };
  private static final double[] VERTICALS = { 0.06, .43 };

  private final TeamManager teamManager;
  private final @Unmodifiable Collection<FakeEntity> entities;

  /**
   * Construct a new {@code EntityBlockHighlight} with the given coordinate.
   *
   * @param coordinate the in-world coordinate of the element
   * @param configuration the configuration for highlights
   * @param teamManager the scoreboard team manager
   * @param boundary the boundary the element belongs to
   * @param visualizationElementType the type of element being visualized
   */
  public EntityBlockHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    super(coordinate, configuration, boundary, visualizationElementType);
    this.teamManager = teamManager;
    this.entities = getLocalEntities();
  }

  /**
   * Get the slime size set for the associated magma cube entities.
   *
   * @return the slime size
   */
  protected int getSlimeSize() {
    return 1;
  }

  @Override
  protected void draw(@NotNull Player player, @NotNull World world) {
    try {
      for (FakeEntity entity : entities) {
        spawn(player, entity);
      }
      this.teamManager.addTeamEntries(
          player,
          boundary.type(),
          visualizationElementType,
          entities.stream().map(entity -> entity.uuid().toString()).collect(Collectors.toList()));
    } catch (InvocationTargetException e) {
      Problem.sneaky(e);
    }
  }

  /**
   * Send the {@link Player} the packets required to display an entity with the correct data.
   *
   * @param player the recipient
   * @param fakeEntity the {@link FakeEntity} to spawn
   * @throws InvocationTargetException if an error occurs creating or sending the packets
   */
  protected abstract void spawn(@NotNull Player player, @NotNull FakeEntity fakeEntity)
      throws InvocationTargetException;

  @Override
  protected void erase(@NotNull Player player, @NotNull World world) {
    this.teamManager.removeTeamEntries(
        player,
        boundary.type(),
        visualizationElementType,
        entities.stream().map(FakeEntity::uuid).map(UUID::toString).collect(Collectors.toList()));
    try {
      remove(player, entities);
    } catch (InvocationTargetException e) {
      Problem.sneaky(e);
    }
  }

  /**
   * Send the {@link Player} the packet required to remove entities.
   *
   * @param player the recipient
   * @param entities the {@link FakeEntity FakeEntities} to despawn
   * @throws InvocationTargetException if an error occurs creating or sending the packets
   */
  protected abstract void remove(
      @NotNull Player player,
      @NotNull @Unmodifiable Collection<@NotNull FakeEntity> entities)
      throws InvocationTargetException;

  private Collection<FakeEntity> getLocalEntities() {
    if (visualizationElementType == VisualizationElementType.SIDE) {
      return List.of(new FakeEntity(configuration.getNextEntityId(), new Vector(0.5, 0.25, 0.5)));
    }

    List<FakeEntity> localEntities = new ArrayList<>();

    for (double x : HORIZONTALS) {
      for (double z : HORIZONTALS) {
        for (double y: VERTICALS) {
          localEntities.add(new FakeEntity(configuration.getNextEntityId(), new Vector(x, y, z)));
        }
      }
    }

    return localEntities;
  }

  /** Container for fake entity data. */
  protected record FakeEntity(int entityId, UUID uuid, Vector localPosition) {

    private FakeEntity(int entityId, Vector localPosition) {
      this(entityId, UUID.randomUUID(), localPosition);
    }

  }

}
