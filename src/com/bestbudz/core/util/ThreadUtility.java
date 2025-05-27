package com.bestbudz.core.util;

public class ThreadUtility {

  public static boolean PRINT_AVERAGE = false;
  public static boolean PRINT_CURRENT = true;
  private final long[] total;
  private final int[] amount;
  private final long[] last;
  private final long[] average;

  public ThreadUtility(int count) {
    average = new long[count];
    total = new long[count];
    amount = new int[count];
    last = new long[count];
  }

  public void benchmark(int id, long start, long end) {
    total[id] += (end - start);
    amount[id]++;
    last[id] = (end - start);
    average[id] = total[id] / amount[id];
  }

  public void print() {
    System.out.println("packet thread average: " + average[1] + "ms");
  }

  public boolean ready() {
    synchronized (average) {
      for (int i = 0; i < amount.length; i++) if (amount[i] == 0) return false;
    }
    return true;
  }
}
