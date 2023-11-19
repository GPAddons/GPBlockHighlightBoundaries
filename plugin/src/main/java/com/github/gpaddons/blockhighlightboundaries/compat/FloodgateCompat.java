package com.github.gpaddons.blockhighlightboundaries.compat;

import com.github.jikoo.planarwrappers.event.Event;
import com.github.jikoo.planarwrappers.service.ProvidedService;
import org.bukkit.entity.Player;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.plugin.Plugin;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

public class FloodgateCompat extends ProvidedService<FloodgateApi> {

  public FloodgateCompat(@NotNull Plugin plugin) {
    super(plugin);

    // Directly register events - Bukkit's Listener system will fail due to missing dependencies.
    Event.register(PluginEnableEvent.class, this::handlePlugin, plugin);
    Event.register(PluginDisableEvent.class, this::handlePlugin, plugin);
  }

  private void handlePlugin(@NotNull PluginEvent event) {
    // Floodgate may or may not be capitalized.
    if (event.getPlugin().getName().equalsIgnoreCase("floodgate")) {
      // If already set up, ensure that provider is present.
      wrapClass(true);
    }
  }

  protected @NotNull FloodgateApi getRegistration(@NotNull Class<FloodgateApi> clazz) {
    return FloodgateApi.getInstance();
  }

  public boolean isBedrock(@NotNull Player player) {
    Wrapper<FloodgateApi> service = getService();
    return service != null && service.unwrap().isFloodgatePlayer(player.getUniqueId());
  }

  @Override
  protected @Nullable Supplier<@NotNull String> logServiceClassNotLoaded() {
    // Don't bother logging Floodgate not present.
    return null;
  }
}
