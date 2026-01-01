package com.github.gpaddons.blockhighlightboundaries.type;

import java.util.function.BooleanSupplier;
import org.jetbrains.annotations.NotNull;

/** Enum representing types of block highlights. */
public enum HighlightType {

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
