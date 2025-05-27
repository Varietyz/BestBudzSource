package com.bestbudz.core.util.chance;

import java.util.ArrayList;
import java.util.List;

public class Chance<T> {

  private final List<WeightedObject<T>> objects;

  private double sum;

  public Chance(List<WeightedObject<T>> objects) {
    this.objects = objects;
    sum = objects.stream().mapToDouble(WeightedObject::getWeight).sum();
    sort();
  }

  public Chance() {
    this.objects = new ArrayList<>();
    sum = 0;
  }

  public final void add(double weight, T t) {
    objects.add(new WeightedChance<T>(weight, t));
    sum += weight;
  }

  public final void sort() {
    objects.sort(WeightedObject::compareTo);
  }

  public WeightedObject<T> nextObject() {
    double rnd = Math.random() * sum;
    double hit = 0;

    for (WeightedObject<T> obj : objects) {
      hit += obj.getWeight();

      if (hit > rnd) {
        return obj;
      }
    }

    throw new AssertionError("The random number [" + rnd + "] is too large!");
  }
}
