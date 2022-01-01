package com.github.gpaddons.blockhighlightboundaries;

import com.griefprevention.events.BoundaryVisualizationEvent;
import com.griefprevention.visualization.VisualizationProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class GPBlockHighlightBoundaries extends JavaPlugin implements Listener
{

  private final VisualizationProvider provider = (world, visualizeFrom, height) ->
      new BlockHighlightVisualization(GPBlockHighlightBoundaries.this, world, visualizeFrom, height);

  @Override
  public void onEnable() {
    saveDefaultConfig();
    getServer().getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  private void onVisualize(@NotNull BoundaryVisualizationEvent event)
  {
    event.setProvider(provider);
  }

}
