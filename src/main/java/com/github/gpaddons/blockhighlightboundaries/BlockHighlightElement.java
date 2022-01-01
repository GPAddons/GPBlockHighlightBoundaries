package com.github.gpaddons.blockhighlightboundaries;

import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.BlockElement;
import io.netty.buffer.ByteBuf;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.function.UnaryOperator;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class BlockHighlightElement extends BlockElement {

  private final Color color;
  private final String name;
  private final int durationMillis;

  /**
   * Construct a new {@code BlockElement} with the given coordinate.
   *
   * @param coordinate the in-world coordinate of the element
   */
  public BlockHighlightElement(
      @NotNull IntVector coordinate,
      @NotNull Color color,
      @NotNull String name,
      int durationMillis) {
    super(coordinate);
    this.color = color;
    this.name = name;
    this.durationMillis = durationMillis;
  }

  @Override
  protected void draw(@NotNull Player player, @NotNull World world) {
    try {
      sendPacket(player, this::writeData);
    } catch (InvocationTargetException e) {
      // TODO proper logging
      e.printStackTrace();
    }
  }

  private ByteBuf writeData(ByteBuf buffer) {
    buffer.writeLong(getBlockPosition());
    buffer.writeInt(getColor());
    writeString(buffer, name);
    buffer.writeInt(durationMillis);
    return buffer;
  }

  private void writeString(ByteBuf buffer, String value) {
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

  private long getBlockPosition() {
    // See https://wiki.vg/Protocol#Position
    return ((getCoordinate().x() & 0x3FFFFFFL) << 38) | ((getCoordinate().z() & 0x3FFFFFFL) << 12) | (getCoordinate().y() & 0xFFFL);
  }

  private int getColor() {
    // See https://wiki.vg/Plugin_channels#minecraft:debug.2Fgame_test_add_marker
    return color.getRGB();
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
      e.printStackTrace();
    }
  }

  private ByteBuf writeErase(@NotNull ByteBuf buffer) {
    buffer.writeLong(getBlockPosition());
    buffer.writeInt(getColor());
    writeString(buffer, name);
    buffer.writeInt(durationMillis);
    return buffer;
  }

}
