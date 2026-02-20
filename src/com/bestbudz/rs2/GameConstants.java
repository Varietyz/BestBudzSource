package com.bestbudz.rs2;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.mob.VirtualMobRegion;

public class GameConstants {

  public static final int[][][] SIZES = {
    {{0, 0}},
    {{0, 0}},
    {{0, 1}, {1, 0}, {1, 1}},
    {{2, 0}, {2, 1}, {2, 2}, {1, 2}, {0, 2}},
    {{3, 0}, {3, 1}, {3, 2}, {3, 3}, {2, 3}, {1, 3}, {0, 3}},
    {{4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4}, {3, 4}, {2, 4}, {1, 4}, {0, 4}},
    {{5, 0}, {5, 1}, {5, 2}, {5, 3}, {5, 4}, {5, 5}, {4, 5}, {3, 5}, {2, 5}, {1, 5}, {0, 5}},
  };

  public static final int[][] DIR = {
    {-1, 1}, {0, 1}, {1, 1}, {-1, 0}, {1, 0}, {-1, -1}, {0, -1}, {1, -1}
  };

  public static Location[] getBorder(int x, int y, int size) {
    if (size == 1) {
      return new Location[] {new Location(x, y)};
    }

    Location[] border = new Location[4 * (size - 1)];
    int j = 0;

    border[0] = new Location(x, y);

    for (int i = 0; i < 4; i++) {
      for (int k = 0; k < (i < 3 ? size - 1 : size - 2); k++) {
        if (i == 0) {
          x++;
        } else if (i == 1) {
          y++;
        } else if (i == 2) {
          x--;
        } else if (i == 3) {
          y--;
        }
        border[(++j)] = new Location(x, y);
      }
    }

    return border;
  }

  public static Location[] getBorder(int x, int y, int xsize, int ysize) {
    if (xsize <= 1 && ysize <= 1) {
      return new Location[] {new Location(x, y)};
    }

    Location[] border = new Location[(xsize * 2) + (ysize * 2)];
    int j = 0;

    border[0] = new Location(x, y);

    for (int i = 0; i < 4; i++) {
      for (int k = 0;
          k
              < (i < 3
                  ? (i == 0 || i == 2 ? xsize : ysize) - 1
                  : (i == 0 || i == 2 ? xsize : ysize) - 2);
          k++) {
        if (i == 0) x++;
        else if (i == 1) y++;
        else if (i == 2) x--;
        else if (i == 3) {
          y--;
        }
        border[(++j)] = new Location(x, y);
      }
    }

    return border;
  }

  public static Location getClearAdjacentLocation(Location l, int size) {
    return getClearAdjacentLocation(l, size, null);
  }

  public static Location getClearAdjacentLocation(Location l, int size, VirtualMobRegion virtual) {
    int x = l.getX();
    int y = l.getY();
    int z = l.getZ();

    int lowDist = 99999;
    int lowX = 0;
    int lowY = 0;

    main:
    for (int i = 0; i < DIR.length; i++) {
      int x2 = x + DIR[i][0] * size;
      int y2 = y + DIR[i][1] * size;

      for (int k = 0; k < size - 1; k++) {
        int y3 = y2 + k;

        for (int j = 0; j < size - 1; j++) {
          int x3 = x2 + j;

          Region r = Region.getRegion(x3, y3);

          if (r == null) {
            continue main;
          }

          if (virtual == null) {

            if ((!r.canMove(x3, y3, z, 1))
                || (!r.canMove(x3, y3, z, 4))
                || (!r.canMove(x3, y3, z, 2))
                || (r.isNpcOnTile(x3 + DIR[1][0], y3 + DIR[1][1], z))
                || (r.isNpcOnTile(x3 + DIR[4][0], y3 + DIR[4][1], z))
                || (r.isNpcOnTile(x3 + DIR[2][0], y3 + DIR[2][1], z))) {
              continue main;
            }

          } else {

            if ((!r.canMove(x3, y3, z, 1))
                || (!r.canMove(x3, y3, z, 4))
                || (!r.canMove(x3, y3, z, 2))
                || (virtual.isMobOnTile(x3 + DIR[1][0], y3 + DIR[1][1], z))
                || (virtual.isMobOnTile(x3 + DIR[4][0], y3 + DIR[4][1], z))
                || (virtual.isMobOnTile(x3 + DIR[2][0], y3 + DIR[2][1], z))) {
              continue main;
            }
          }
        }
      }

		int dist = Math.abs(x2 - x) + Math.abs(y2 - y); // inline to avoid function call

		if (dist < lowDist) {
        lowX = x2;
        lowY = y2;
        lowDist = dist;
      }
    }

    return lowX != 0 ? new Location(lowX, lowY, z) : null;
  }

	private static final int[][] DIRECTION_LOOKUP = {
		{5, 6, 7}, // dx: -1, 0, 1 — dy: -1
		{3, -1, 4}, // dx: -1, 0, 1 — dy: 0
		{0, 1, 2}   // dx: -1, 0, 1 — dy: +1
	};

	// Replaces getDirection(xDiff, yDiff)
	public static int getDirection(int dx, int dy) {
		if (dx < -1 || dx > 1 || dy < -1 || dy > 1) {
			return -1;
		}
		return DIRECTION_LOOKUP[dy + 1][dx + 1];
	}

	// Replaces getDirection(x, y, x2, y2)
	public static int getDirection(int x, int y, int x2, int y2) {
		int dx = x2 - x;
		int dy = y2 - y;
		return getDirection(dx, dy);
	}

  public static Location[] getEdges(int x, int y, int size) {
    if (size <= 1) {
      return new Location[] {new Location(x, y)};
    }
    size /= 2;
    return new Location[] {
      new Location(x - size, y + size),
      new Location(x, y + size),
      new Location(x + size, y + size),
      new Location(x - size, y),
      new Location(x, y),
      new Location(x + size, y),
      new Location(x - size, y - size),
      new Location(x, y - size),
      new Location(x + size, y - size)
    };
  }

  public static boolean withinBlock(int blockX, int blockY, int size, int x, int y) {
    return (x - blockX < size) && (x - blockX > -1) && (y - blockY < size) && (y - blockY > -1);
  }

  public static boolean withinBlock(
      int blockX, int blockY, int blockSize, int x, int y, int checkSize) {
    for (int i = 1; i < checkSize + 1; i++) {
      for (int k = 0; k < SIZES[i].length; k++) {
        int x2 = x + SIZES[i][k][0];
        int y2 = y + SIZES[i][k][1];

		  if ((x2 >= blockX && x2 < blockX + blockSize)
			  && (y2 >= blockY && y2 < blockY + blockSize))
		  {
          return true;
        }
      }
    }

    return false;
  }
}
