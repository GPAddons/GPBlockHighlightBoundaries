package com.github.gpaddons.blockhighlightboundaries.type;

import com.github.gpaddons.blockhighlightboundaries.util.CraftbukkitVersion;
import java.util.function.BooleanSupplier;

public enum HighlightType {
  // Debug block changed in 1.18.2 - prior, first sent was always visible, others were not always.
  // In 1.18.2, all are always visible.
  DEBUG_BLOCK(() -> CraftbukkitVersion.getInstance().atOrAbove(new CraftbukkitVersion(1, 18, 2))),
  // Glowing entities are always visible provided entities are rendered.
  GLOWING_ENTITY(() -> true);

  private boolean alwaysVisible;

  HighlightType(BooleanSupplier alwaysVisible) {
    this.alwaysVisible = alwaysVisible.getAsBoolean();
  }

  public boolean isAlwaysVisible() {
    return this.alwaysVisible;
  }

}
