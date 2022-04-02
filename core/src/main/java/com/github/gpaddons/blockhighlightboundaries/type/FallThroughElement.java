package com.github.gpaddons.blockhighlightboundaries.type;

import com.griefprevention.util.IntVector;
import com.griefprevention.visualization.BlockElement;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A very simple BlockElement very similar to GP's FakeBlockElement used to fall through during
 * failures.
 */
public final class FallThroughElement extends BlockElement
{

  private final @NotNull BlockData visualizedBlock;
  private @Nullable BlockData realBlock;

  public FallThroughElement(
      @NotNull IntVector intVector,
      @NotNull ChatColor color)
  {
    super(intVector);
    Material material = Material.getMaterial(color.name() + "_ WOOL");
    if (material == null) {
      material = Material.WHITE_WOOL;
    }
    this.visualizedBlock = material.createBlockData();
  }

  @Override
  protected void draw(@NotNull Player player, @NotNull World world)
  {
    // Send the player a fake block change event only if the chunk is loaded.
    if (!getCoordinate().isChunkLoaded(world)) return;

    // Grab real block from world to revert later.
    Location coordinate = getCoordinate().toLocation(world);
    realBlock = coordinate.getBlock().getBlockData();
    // Send change.
    player.sendBlockChange(coordinate, visualizedBlock);
  }

  @Override
  protected void erase(@NotNull Player player, @NotNull World world)
  {
    if (realBlock == null) {
      // Real block was not loaded when drawn, so change was never sent.
      return;
    }

    player.sendBlockChange(getCoordinate().toLocation(world), realBlock);
  }

}
