package com.github.gpaddons.blockhighlightboundaries.compat;

import com.github.jikoo.planarwrappers.service.ProvidedService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

public class FloodgateCompat extends ProvidedService<FloodgateApi> {

  public FloodgateCompat(@NotNull Plugin plugin) {
    super(plugin);
  }

  protected @NotNull FloodgateApi getRegistration(@NotNull Class<FloodgateApi> clazz) {
    return FloodgateApi.getInstance();
  }

  public boolean isBedrock(@NotNull Player player) {
    Wrapper<FloodgateApi> service = getService();
    return service != null && service.unwrap().isFloodgatePlayer(player.getUniqueId());
  }

}
