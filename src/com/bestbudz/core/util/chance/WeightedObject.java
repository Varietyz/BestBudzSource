package com.bestbudz.core.util.chance;

public interface WeightedObject<T> extends Comparable<WeightedObject<T>> {

  double getWeight();

  T get();

  @Override
  String toString();
}
