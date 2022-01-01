package com.github.gpaddons.blockhighlightboundaries;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.griefprevention.util.IntVector;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.function.UnaryOperator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProtocolLibElement extends BlockHighlightElement {

  /**
   * Construct a new {@code ProtocolLibElement}.
   *
   * @param coordinate     the in-world coordinate of the element
   * @param color the color of the element
   * @param durationMillis the
   */
  public ProtocolLibElement(
      @NotNull IntVector coordinate,
      @NotNull Color color,
      @NotNull String name,
      int durationMillis) {
    super(coordinate, color, name, durationMillis);
  }

  @Override
  protected void sendPacket(
      @NotNull Player player,
      @NotNull UnaryOperator<@NotNull ByteBuf> write)
      throws InvocationTargetException {
    PacketContainer packet = new PacketContainer(Server.CUSTOM_PAYLOAD);
    packet.getMinecraftKeys().write(0, new MinecraftKey("debug/game_test_add_marker"));
    Object packetDataSerializer = MinecraftReflection.getPacketDataSerializer(write.apply(Unpooled.buffer()));
    packet.getModifier().withType(ByteBuf.class).write(0, packetDataSerializer);
    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
  }

}
