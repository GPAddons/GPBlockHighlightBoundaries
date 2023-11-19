package com.github.gpaddons.blockhighlightboundaries.impl.protocollib;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.TeamManager;
import com.github.gpaddons.blockhighlightboundaries.type.EntityBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

abstract class EntityHighlight<T> extends EntityBlockHighlight {

  public EntityHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull TeamManager teamManager,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    super(coordinate, configuration, teamManager, boundary, visualizationElementType);
  }

  @Override
  protected void spawn(@NotNull Player player, @NotNull FakeEntity fakeEntity) {
    PacketContainer spawn = new PacketContainer(Server.SPAWN_ENTITY);
    spawn.getIntegers().write(0, fakeEntity.entityId());
    spawn.getEntityTypeModifier().write(0, EntityType.MAGMA_CUBE);
    spawn.getUUIDs().write(0, fakeEntity.uuid());
    spawn.getDoubles()
        .write(0, getCoordinate().x() + fakeEntity.localPosition().getX())
        .write(1, getCoordinate().y() + fakeEntity.localPosition().getY())
        .write(2, getCoordinate().z() + fakeEntity.localPosition().getZ());

    ProtocolLibrary.getProtocolManager().sendServerPacket(player, spawn);

    PacketContainer meta = new PacketContainer(Server.ENTITY_METADATA);
    meta.getIntegers().write(0, fakeEntity.entityId());

    T data = create();
    // Invisible (0x20) and glowing (0x40).
    Serializer byteSerializer = Registry.get(Byte.class);
    addData(data, 0, byteSerializer, (byte) (0x20 | 0x40));
    // Set name.
    String name = getName();
    Optional<WrappedChatComponent> nameOptional = name.isBlank()
        ? Optional.empty()
        : Optional.of(WrappedChatComponent.fromLegacyText(name));
    addData(data, 2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), nameOptional);
    // Set name always visible.
    Serializer booleanSerializer = Registry.get(Boolean.class);
    addData(data, 3, booleanSerializer, Boolean.TRUE);
    // Set silent.
    addData(data, 4, booleanSerializer, Boolean.TRUE);
    // Set no gravity.
    addData(data, 5, booleanSerializer, Boolean.TRUE);
    // Set no AI.
    addData(data, 15, byteSerializer, (byte) (0x01));
    // Set slime size.
    addData(data, 16, WrappedDataWatcher.Registry.get(Integer.class), getSlimeSize());

    write(meta, data);
    ProtocolLibrary.getProtocolManager().sendServerPacket(player, meta);
  }

  protected abstract @NotNull T create();

  protected abstract void addData(
      @NotNull T written,
      int index,
      @NotNull Serializer serializer,
      @NotNull Object value);

  protected abstract void write(@NotNull PacketContainer container, @NotNull T data);

  @Override
  protected void remove(
      @NotNull Player player,
      @NotNull @Unmodifiable Collection<@NotNull FakeEntity> entities) {
    PacketContainer destroy = new PacketContainer(Server.ENTITY_DESTROY);
    destroy
        .getIntLists()
        .write(0, entities.stream().map(FakeEntity::entityId).collect(Collectors.toList()));
    ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroy);
  }

}
