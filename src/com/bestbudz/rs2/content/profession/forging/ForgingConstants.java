package com.bestbudz.rs2.content.profession.forging;

public class ForgingConstants {
  public static final int[] BARS = {2349, 2351, 2353, 2359, 2361, 2363};

  public static int getBarAmount(int interfaceId, int slot) {
    switch (interfaceId) {
      case 1119:
        switch (slot) {
          case 0:
            return 1;
          case 1:
            return 1;
          case 2:
            return 2;
          case 3:
            return 2;
          case 4:
            return 3;
        }
        break;
      case 1120:
        switch (slot) {
          case 0:
            return 1;
          case 1:
            return 1;
          case 2:
            return 3;
          case 3:
            return 3;
          case 4:
            return 2;
        }
        break;
      case 1121:
        switch (slot) {
          case 0:
            return 3;
          case 1:
            return 3;
          case 2:
            return 3;
          case 3:
            return 5;
          case 4:
            return 1;
        }
        break;
      case 1122:
        switch (slot) {
          case 0:
            return 1;
          case 1:
            return 2;
          case 2:
            return 2;
          case 3:
            return 3;
          case 4:
            return 1;
        }
        break;
      case 1123:
        switch (slot) {
          case 0:
            return 1;
          case 1:
            return 1;
          case 2:
            return 1;
          case 3:
            return 1;
          case 4:
            return 1;
        }
        break;
    }

    return 1;
  }

}
