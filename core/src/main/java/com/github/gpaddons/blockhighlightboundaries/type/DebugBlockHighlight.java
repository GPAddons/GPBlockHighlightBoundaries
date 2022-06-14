package com.github.gpaddons.blockhighlightboundaries.type;

import com.github.gpaddons.blockhighlightboundaries.HighlightConfiguration;
import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.Boundary;
import io.netty.buffer.ByteBuf;
import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.function.UnaryOperator;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link BlockHighlightElement} that is drawn using the block debug functionality.
 */
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

  /**
   * Get the {@link Color} the block highlight will be.
   *
   * @return the RGBA color
   */
  public Color getColor() {
    return configuration.getColor(boundary.type(), visualizationElementType);
  }

  /**
   * Get the number of milliseconds the highlight should be displayed for.
   *
   * @return the display duration in milliseconds
   */
  public int getDisplayMillis() {
    return configuration.getDisplayMillis();
  }

  /**
   * Get the name of the channel of the custom payload.
   *
   * @return the name of the payload channel
   */
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
    sendPacket(player, this::writeData);
  }

  @Contract("_ -> param1")
  private @NotNull ByteBuf writeData(@NotNull ByteBuf buffer) {
    buffer.writeLong(getBlockPositionLong());
    buffer.writeInt(getColorInt());
    writeString(buffer, getName());
    buffer.writeInt(getDisplayMillis());
    return buffer;
  }

  /**
   * Send the payload containing the specified data to the {@link Player} visualizing the element.
   *
   * @param player the recipient
   * @param write the {@link ByteBuf} modification
   */
  protected abstract void sendPacket(
      @NotNull Player player,
      @NotNull UnaryOperator<@NotNull ByteBuf> write);

  @Override
  protected void erase(@NotNull Player player, @NotNull World world) {
    sendPacket(player, this::writeErase);
  }

  @Contract("_ -> param1")
  private @NotNull ByteBuf writeErase(@NotNull ByteBuf buffer) {
    buffer.writeLong(getBlockPositionLong());
    buffer.writeInt(getColorInt());
    writeString(buffer, "");
    buffer.writeInt(0);
    return buffer;
  }

}
