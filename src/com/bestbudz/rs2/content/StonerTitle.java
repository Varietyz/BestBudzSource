package com.bestbudz.rs2.content;

import java.util.Objects;

public final class StonerTitle {

  private final String title;

  private final int color;

  private final boolean suffix;

  public StonerTitle(String title, int color, boolean suffix) {
    this.title = title;
    this.color = color;
    this.suffix = suffix;
  }

  public static StonerTitle create(String title, int color, boolean suffix) {
    return new StonerTitle(title, color, suffix);
  }

  public String getTitle() {
    return title;
  }

  public int getColor() {
    return color;
  }

  public boolean isSuffix() {
    return suffix;
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, color, suffix);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof StonerTitle)) {
      return false;
    }

    StonerTitle title = (StonerTitle) obj;

    return title.getTitle().equals(this.title)
        && title.getColor() == getColor()
        && title.isSuffix() == isSuffix();
  }
}
