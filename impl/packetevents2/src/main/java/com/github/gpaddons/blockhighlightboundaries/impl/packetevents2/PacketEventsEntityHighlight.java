package com.github.gpaddons.blockhighlightboundaries.impl.packetevents2;

import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.TeamManager;
import com.github.gpaddons.blockhighlightboundaries.type.EntityBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

class PacketEventsEntityHighlight extends EntityBlockHighlight {

  /**
   * Construct a new {@code EntityBlockHighlight} with the given coordinate.
   *
   * @param coordinate               the in-world coordinate of the element
   * @param configuration            the configuration for highlights
   * @param teamManager              the scoreboard team manager
   * @param boundary                 the boundary the element belongs to
   * @param visualizationElementType the type of element being visualized
   */
  public PacketEventsEntityHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    super(coordinate, configuration, teamManager, boundary, visualizationElementType);
  }

  @Override
  protected void spawn(@NotNull Player player, @NotNull FakeEntity entity) {

    String name = getName();
    Optional<Component> nameOptional = name.isBlank()
        ? Optional.empty()
        : Optional.of(LegacyComponentSerializer.legacyAmpersand().deserialize(name));

    List<EntityData<?>> entityData = Arrays.asList(
        // O: Byte: Invisible (0x20) and glowing (0x40)
        new EntityData<>(0, EntityDataTypes.BYTE, (byte) (0x20 | 0x40)),
        // 2: Optional TextComponent: Name
        new EntityData<>(2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, nameOptional),
        // 3: Boolean: Name always visible
        new EntityData<>(3, EntityDataTypes.BOOLEAN, nameOptional.isPresent()),
        // 4: Boolean: Silent
        new EntityData<>(4, EntityDataTypes.BOOLEAN, true),
        // 5: Boolean: No gravity
        new EntityData<>(5, EntityDataTypes.BOOLEAN, true),
        // 15: Byte: No AI (0x01)
        new EntityData<>(15, EntityDataTypes.BYTE, (byte) (0x01)),
        // 16: Integer: Slime size
        new EntityData<>(16, EntityDataTypes.INT, getSlimeSize())
    );

    WrapperPlayServerSpawnLivingEntity spawn = new WrapperPlayServerSpawnLivingEntity(
        entity.entityId(),
        entity.uuid(),
        SpigotConversionUtil.fromBukkitEntityType(EntityType.MAGMA_CUBE),
        new Vector3d(getCoordinate().x() + 0.5, getCoordinate().y(), getCoordinate().z() + 0.5),
        0F,
        0F,
        0F,
        new Vector3d(),
        entityData);

    PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawn);
  }

  @Override
  protected void remove(
      @NotNull Player player,
      @NotNull @Unmodifiable Collection<@NotNull FakeEntity> entities) {
    WrapperPlayServerDestroyEntities destroy = new WrapperPlayServerDestroyEntities(
        entities.stream().mapToInt(FakeEntity::entityId).toArray());
    PacketEvents.getAPI().getPlayerManager().sendPacket(player, destroy);
  }

}
