package com.github.gpaddons.blockhighlightboundaries.type;

import com.github.gpaddons.blockhighlightboundaries.util.CraftbukkitVersion;
import org.jetbrains.annotations.NotNull;
import java.util.function.BooleanSupplier;

/** Enum representing types of block highlights. */
public enum HighlightType {

  /** A block highlight using the game_test_marker block debug. */
  DEBUG_BLOCK(
      false,
      // Debug block changed in 1.18.2 - prior, first sent was always visible, others were not always.
      // In 1.18.2, all are always visible.
      () -> CraftbukkitVersion.getInstance().atOrAbove(new CraftbukkitVersion(1, 18, 2))),
  /** A block highlight using invisible glowing entities. */
  GLOWING_ENTITY(true, () -> true);

  private final boolean requiresErase;
  private final boolean alwaysVisible;

  HighlightType(boolean requiresErase, @NotNull BooleanSupplier alwaysVisible) {
    this.requiresErase = requiresErase;
    this.alwaysVisible = alwaysVisible.getAsBoolean();
  }

  public boolean requiresErase() {
    return this.requiresErase;
  }

  public boolean isAlwaysVisible() {
    return this.alwaysVisible;
  }

}
