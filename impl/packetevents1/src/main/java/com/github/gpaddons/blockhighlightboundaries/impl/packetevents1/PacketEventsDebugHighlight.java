package com.github.gpaddons.blockhighlightboundaries.impl.packetevents1;

import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.type.DebugBlockHighlight;
import com.github.gpaddons.blockhighlightboundaries.type.VisualizationElementType;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.out.custompayload.WrappedPacketOutCustomPayload;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.function.UnaryOperator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class PacketEventsDebugHighlight extends DebugBlockHighlight {

  /**
   * Construct a new {@code DebugBlockHighlight} with the given coordinate.
   *
   * @param coordinate               the in-world coordinate of the element
   * @param configuration            the configuration for highlights
   * @param boundary                 the boundary the element belongs to
   * @param visualizationElementType the type of element being visualized
   */
  public PacketEventsDebugHighlight(@NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    super(coordinate, configuration, boundary, visualizationElementType);
  }

  @Override
  protected void sendPacket(@NotNull Player player,
      @NotNull UnaryOperator<@NotNull ByteBuf> write) {
    byte[] data = write.apply(Unpooled.buffer()).array();
    WrappedPacketOutCustomPayload payload = new WrappedPacketOutCustomPayload(getChannel(), data);
    PacketEvents.get().getPlayerUtils().sendPacket(player, payload);
  }

}
