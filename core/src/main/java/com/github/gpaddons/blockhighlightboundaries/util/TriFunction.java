package com.github.gpaddons.blockhighlightboundaries.util;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {

  R apply(T t, U u, V v);

}
