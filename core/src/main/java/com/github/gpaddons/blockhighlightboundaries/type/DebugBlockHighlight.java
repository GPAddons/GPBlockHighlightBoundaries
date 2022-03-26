package com.github.gpaddons.blockhighlightboundaries.type;

import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.github.gpaddons.blockhighlightboundaries.Problem;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import io.netty.buffer.ByteBuf;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.function.UnaryOperator;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class DebugBlockHighlight extends BlockHighlightElement {

  /**
   * Construct a new {@code DebugBlockHighlight} with the given coordinate.
   *
   * @param coordinate the in-world coordinate of the element
   * @param configuration the configuration for highlights
   * @param boundary the boundary the element belongs to
   * @param visualizationElementType the type of element being visualized
   */
  public DebugBlockHighlight(
      @NotNull IntVector coordinate,
      @NotNull HighlightConfiguration configuration,
      @NotNull Boundary boundary,
      @NotNull VisualizationElementType visualizationElementType) {
    super(coordinate, configuration, boundary, visualizationElementType);
  }

  public Color getColor() {
    return configuration.getColor(boundary.type(), visualizationElementType);
  }

  public int getDurationMillis() {
    return configuration.getDisplayMillis();
  }

  protected String getChannel() {
    return "debug/game_test_add_marker";
  }

  private long getBlockPositionLong() {
    // See https://wiki.vg/Protocol#Position
    return ((getCoordinate().x() & 0x3FFFFFFL) << 38) | ((getCoordinate().z() & 0x3FFFFFFL) << 12) | (getCoordinate().y() & 0xFFFL);
  }

  private int getColorInt() {
    // See https://wiki.vg/Plugin_channels#minecraft:debug.2Fgame_test_add_marker
    return getColor().getRGB();
  }

  private void writeString(ByteBuf buffer, @NotNull String value) {
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
    // See FriendlyByteBuf#writeByteArray(byte[])
    writeVarInt(buffer, bytes.length);
    buffer.writeBytes(bytes);
  }

  private void writeVarInt(ByteBuf buffer, int value) {
    // See FriendlyByteBuf#writeVarInt(int)
    while((value & -128) != 0) {
      buffer.writeByte(value & 127 | 128);
      value >>>= 7;
    }

    buffer.writeByte(value);
  }

  @Override
  protected void draw(@NotNull Player player, @NotNull World world) {
    try {
      sendPacket(player, this::writeData);
    } catch (InvocationTargetException e) {
      Problem.sneaky(e);
    }
  }

  @Contract("_ -> param1")
  private @NotNull ByteBuf writeData(@NotNull ByteBuf buffer) {
    buffer.writeLong(getBlockPositionLong());
    buffer.writeInt(getColorInt());
    writeString(buffer, getName());
    buffer.writeInt(getDurationMillis());
    return buffer;
  }

  protected abstract void sendPacket(
      @NotNull Player player,
      @NotNull UnaryOperator<@NotNull ByteBuf> write)
      throws InvocationTargetException;

  @Override
  protected void erase(@NotNull Player player, @NotNull World world) {
    try {
      sendPacket(player, this::writeErase);
    } catch (InvocationTargetException e) {
      Problem.sneaky(e);
    }
  }

  @Contract("_ -> param1")
  private @NotNull ByteBuf writeErase(@NotNull ByteBuf buffer) {
    buffer.writeLong(getBlockPositionLong());
    buffer.writeInt(getColorInt());
    writeString(buffer, "");
    buffer.writeInt(0);
    return buffer;
  }

  @Override
  public boolean requiresErase() {
    return false;
  }

}
