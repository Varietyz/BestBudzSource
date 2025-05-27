package com.bestbudz.core.util;

import java.util.concurrent.TimeUnit;

public class Stopwatch {

  private long time = Stopwatch.currentTime();

  public static long currentTime() {
    return TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
  }

  public Stopwatch reset(long start) {
    time = Stopwatch.currentTime() - start;
    return this;
  }

  public Stopwatch reset() {
    time = Stopwatch.currentTime();
    return this;
  }

  public long elapsed() {
    return Stopwatch.currentTime() - time;
  }

  public long elapsed(TimeUnit unit) {
    if (unit == TimeUnit.MILLISECONDS)
      throw new IllegalArgumentException("Time is already in milliseconds!");
    return unit.convert(elapsed(), TimeUnit.MILLISECONDS);
  }

  public boolean elapsed(int time2) {
    return false;
  }
}
