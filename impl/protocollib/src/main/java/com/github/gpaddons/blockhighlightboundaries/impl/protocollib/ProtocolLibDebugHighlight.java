package com.github.gpaddons.blockhighlightboundaries.impl.protocollib;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.type.DebugBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.function.UnaryOperator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class ProtocolLibDebugHighlight extends DebugBlockHighlight {

  /**
   * Construct a new {@code ProtocolLibDebugHighlight}.
   *
   * @param coordinate the in-world coordinate of the element
   * @param configuration the configuration for highlights
   * @param boundary the boundary the element belongs to
   * @param visualizationElementType the type of element being visualized
   */
  public ProtocolLibDebugHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    super(coordinate, configuration, boundary, visualizationElementType);
  }

  @Override
  protected void sendPacket(
      @NotNull Player player,
      @NotNull UnaryOperator<@NotNull ByteBuf> write) {
    PacketContainer packet = new PacketContainer(Server.CUSTOM_PAYLOAD);
    packet.getMinecraftKeys().write(0, new MinecraftKey(getChannel()));
    Object packetDataSerializer = MinecraftReflection.getPacketDataSerializer(write.apply(Unpooled.buffer()));
    packet.getModifier().withType(ByteBuf.class).write(0, packetDataSerializer);
    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
  }

}
