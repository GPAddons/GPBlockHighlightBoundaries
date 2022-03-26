package com.github.gpaddons.blockhighlightboundaries;

import org.jetbrains.annotations.NotNull;

public class Problem extends RuntimeException {

  private Problem(@NotNull Exception exception) {
    // Don't fill in trace, etc.
    // Exists only to pass the root problem to a runtime catch.
    super(null, exception, true, false);
  }

  @Override
  public synchronized @NotNull Throwable getCause() {
    return super.getCause();
  }

  public static void sneaky(@NotNull Exception exception) {
    throw new Problem(exception);
  }

}
