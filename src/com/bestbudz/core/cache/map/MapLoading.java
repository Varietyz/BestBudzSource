package com.bestbudz.core.cache.map;

import com.bestbudz.core.util.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

public class MapLoading {

  private static final Logger logger = Logger.getLogger(MapLoading.class.getSimpleName());

  private static List<QueuedDoor> doorQueue = new LinkedList<QueuedDoor>();

  private static int doors = 0;

  @SuppressWarnings("unchecked")
  public static void load() {
    try {
      Gson gson = new Gson();

      // Read region index
      Type indexType = new TypeToken<Map<String, Object>>() {}.getType();
      Map<String, Object> indexData;
      try (FileReader reader = new FileReader("./data/map/maps_json/region_index.json")) {
        indexData = gson.fromJson(reader, indexType);
      }

      List<Map<String, Object>> regionEntries = (List<Map<String, Object>>) indexData.get("regions");
      int size = regionEntries.size();
      Region.setRegions(new Region[size]);

      // Create regions first
      int[] regionIds = new int[size];
      for (int i = 0; i < size; i++) {
        regionIds[i] = ((Number) regionEntries.get(i).get("regionId")).intValue();
        Region.getRegions()[i] = new Region(regionIds[i]);
      }

      // Load each region from its JSON file
      Type regionType = new TypeToken<Map<String, Object>>() {}.getType();
      for (int i = 0; i < size; i++) {
        String regionFile = "./data/map/maps_json/" + regionIds[i] + ".json";
        java.io.File f = new java.io.File(regionFile);
        if (!f.exists()) continue;

        try {
          Map<String, Object> regionData;
          try (FileReader reader = new FileReader(regionFile)) {
            regionData = gson.fromJson(reader, regionType);
          }

          int absX = ((Number) regionData.get("absX")).intValue();
          int absY = ((Number) regionData.get("absY")).intValue();

          // Process ground flags
          List<Map<String, Object>> groundFlags =
              (List<Map<String, Object>>) regionData.get("groundFlags");
          int[][][] someArray = new int[4][64][64];
          if (groundFlags != null) {
            for (Map<String, Object> gf : groundFlags) {
              int level = ((Number) gf.get("level")).intValue();
              int x = ((Number) gf.get("x")).intValue();
              int y = ((Number) gf.get("y")).intValue();
              int flag = ((Number) gf.get("flag")).intValue();
              someArray[level][x][y] = flag;
            }
          }

          // Apply ground clipping
          for (int lvl = 0; lvl < 4; lvl++) {
            for (int x = 0; x < 64; x++) {
              for (int y = 0; y < 64; y++) {
                if ((someArray[lvl][x][y] & 1) == 1) {
                  int height = lvl;
                  if ((someArray[1][x][y] & 2) == 2) {
                    height--;
                  }
                  if (height >= 0 && height <= 3) {
                    addClipping(true, absX + x, absY + y, height, 0x200000);
                  }
                }
              }
            }
          }

          // Process objects
          List<Map<String, Object>> objects =
              (List<Map<String, Object>>) regionData.get("objects");
          if (objects != null) {
            for (Map<String, Object> obj : objects) {
              int objectId = ((Number) obj.get("id")).intValue();
              int localX = ((Number) obj.get("x")).intValue();
              int localY = ((Number) obj.get("y")).intValue();
              int height = ((Number) obj.get("level")).intValue();
              int type = ((Number) obj.get("type")).intValue();
              int direction = ((Number) obj.get("direction")).intValue();

              if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64) continue;
              if ((someArray[1][localX][localY] & 2) == 2) {
                height--;
              }
              if (height >= 0 && height <= 3) {
                addObject(true, objectId, absX + localX, absY + localY, height, type, direction);
              }
            }
          }
        } catch (Exception e) {
          System.out.println("Error loading map region: " + regionIds[i] + ": " + e.getMessage());
        }
      }

      logger.info(Utility.format(size) + " Maps have been loaded from JSON successfully.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void processDoors() {
    List<QueuedDoor> ignore = new LinkedList<QueuedDoor>();
    for (Iterator<QueuedDoor> k = doorQueue.iterator(); k.hasNext(); ) {
      QueuedDoor i = k.next();

      if (ignore.contains(i)) {
        continue;
      }

      RSObject o = MapConstants.getDoubleDoor(i.getId(), i.getX(), i.getY(), i.getZ(), i.getFace());

      if (o != null) {
        if (MapConstants.isOpen(o.getId())) {
          continue;
        }

        ignore.add(new QueuedDoor(-1, o.getX(), o.getY(), o.getZ(), -1, -1));
        Region.getRegion(i.getX(), i.getY()).addDoubleDoor(new DoubleDoor(i, o));
      } else {
        Region r = Region.getRegion(i.getX(), i.getY());
        if (r != null) {
          r.addDoor(i.getId(), i.getX(), i.getY(), i.getZ(), i.getType(), i.getFace());
        }
      }
    }

    doorQueue = null;
    logger.info(Utility.format(doors) + " Doors have been loaded successfully.");
  }

  public static void addObject(
      boolean beforeLoad, int objectId, int x, int y, int height, int type, int direction) {
    ObjectDef def = ObjectDef.getObjectDef(objectId);

    if (def == null) {
      return;
    }

    int xLength;
    int yLength;
    if (direction != 1 && direction != 3) {
      xLength = def.xLength();
      yLength = def.yLength();
    } else {
      xLength = def.yLength();
      yLength = def.xLength();
    }

    if (type == 22) {
      if (def.hasActions() && def.aBoolean779) {
        addClipping(beforeLoad, x, y, height, 0x200000);
      }
    } else if (type >= 9) {
      if (def.aBoolean779) {
        addClippingForSolidObject(beforeLoad, x, y, height, xLength, yLength, def.aBoolean757);
      }
    } else if (type >= 0 && type <= 3) {
      if (def.aBoolean779) {
        addClippingForVariableObject(beforeLoad, x, y, height, type, direction, def.aBoolean757);
      }
    }

    if (def.hasActions()) {
      (beforeLoad ? Region.getUnsortedRegion(x, y) : Region.getRegion(x, y))
          .addObject(new RSObject(x, y, height, objectId, type, direction));
      if (beforeLoad) {
        if (def.actions != null && def.name != null) {
          if (objectId == 1586
              || objectId == 23156
              || objectId == 15516
              || def.name.toLowerCase().contains("door")
                  && !def.name.toLowerCase().contains("trapdoor")
              || def.name.toLowerCase().contains("gate")) {
            doorQueue.add(new QueuedDoor(objectId, x, y, height, type, direction));
            doors++;
          }
        }
      }
    }
  }

  private static void addClipping(boolean before, int x, int y, int height, int shift) {
    int regionX = x >> 3;
    int regionY = y >> 3;
    int regionId = ((regionX / 8) << 8) + (regionY / 8);
    if (before) {
      for (Region r : Region.getRegions()) {
        if (r.id() == regionId) {
          r.addClip(x, y, height, shift);
          break;
        }
      }
    } else {
      Region.getRegion(x, y).addClip(x, y, height, shift);
    }
  }

  private static void addClippingAlternate(boolean before, int shift, int x, int y, int height) {
    int regionX = x >> 3;
    int regionY = y >> 3;
    int regionId = ((regionX / 8) << 8) + (regionY / 8);
    if (before) {
      for (Region r : Region.getRegions()) {
        if (r.id() == regionId) {
          r.addClip(x, y, height, shift);
          break;
        }
      }
    } else {
      Region.getRegion(x, y).addClip(x, y, height, shift);
    }
  }

  private static void addProjectileClipping(boolean before, int x, int y, int height, int flag) {
    int regionX = x >> 3;
    int regionY = y >> 3;
    int regionId = ((regionX / 8) << 8) + (regionY / 8);
    if (before) {
      for (Region r : Region.getRegions()) {
        if (r.id() == regionId) {
          r.addShootable(x, y, height, flag);
          break;
        }
      }
    } else {
      Region.getRegion(x, y).addShootable(x, y, height, flag);
    }
  }

  private static void addClippingForSolidObject(
      boolean before, int x, int y, int height, int xLength, int yLength, boolean flag) {
    int clipping = 256;
    for (int i = x; i < x + xLength; i++) {
      for (int i2 = y; i2 < y + yLength; i2++) {
        if (flag) {
          addProjectileClipping(before, i, i2, height, clipping);
        }
        addClipping(before, i, i2, height, clipping);
      }
    }
  }

  public static void removeObject(int objectId, int x, int y, int height, int type, int direction) {
    ObjectDef def = ObjectDef.getObjectDef(objectId);

    if (def == null) {
      System.out.println("null object def: " + objectId);
      return;
    }

    int xLength;
    int yLength;
    if (direction != 1 && direction != 3) {
      xLength = def.xLength();
      yLength = def.yLength();
    } else {
      xLength = def.yLength();
      yLength = def.xLength();
    }

    if (type == 22) {
      if (def.hasActions() && def.aBoolean779) {
        addClipping(false, x, y, height, -0x200000);
      }
    } else if (type >= 9) {
      if (def.aBoolean779) {
        removeClippingForSolidObject(x, y, height, xLength, yLength, def.aBoolean757);
      }
    } else if (type >= 0 && type <= 3) {
      if (def.aBoolean779) {
        removeClippingForVariableObject(x, y, height, type, direction, def.aBoolean757);
      }
    }

    if (def.hasActions()) {
      Region.getRegion(x, y).removeObject(new RSObject(x, y, height, objectId, type, direction));
    }
  }

  public static void removeClippingForVariableObject(
      int x, int y, int height, int type, int direction, boolean flag) {
    boolean before = false;
    if (type == 0) {
      if (direction == 0) {
        addClipping(before, x, y, height, -128);
        addClipping(before, x - 1, y, height, -8);
      } else if (direction == 1) {
        addClipping(before, x, y, height, -2);
        addClipping(before, x, y + 1, height, -32);
      } else if (direction == 2) {
        addClipping(before, x, y, height, -8);
        addClipping(before, x + 1, y, height, -128);
      } else if (direction == 3) {
        addClipping(before, x, y, height, -32);
        addClipping(before, x, y - 1, height, -2);
      }
    } else if (type == 1 || type == 3) {
      if (direction == 0) {
        addClipping(before, x, y, height, -1);
        addClipping(before, x - 1, y, height, -16);
      } else if (direction == 1) {
        addClipping(before, x, y, height, -4);
        addClipping(before, x + 1, y + 1, height, -64);
      } else if (direction == 2) {
        addClipping(before, x, y, height, -16);
        addClipping(before, x + 1, y - 1, height, -1);
      } else if (direction == 3) {
        addClipping(before, x, y, height, -64);
        addClipping(before, x - 1, y - 1, height, -4);
      }
    } else if (type == 2) {
      if (direction == 0) {
        addClipping(before, x, y, height, -130);
        addClipping(before, x - 1, y, height, -8);
        addClipping(before, x, y + 1, height, -32);
      } else if (direction == 1) {
        addClipping(before, x, y, height, -10);
        addClipping(before, x, y + 1, height, -32);
        addClipping(before, x + 1, y, height, -128);
      } else if (direction == 2) {
        addClipping(before, x, y, height, -40);
        addClipping(before, x + 1, y, height, -128);
        addClipping(before, x, y - 1, height, -2);
      } else if (direction == 3) {
        addClipping(before, x, y, height, -160);
        addClipping(before, x, y - 1, height, -2);
        addClipping(before, x - 1, y, height, -8);
      }
    }
  }

  private static void removeClippingForSolidObject(
      int x, int y, int height, int xLength, int yLength, boolean flag) {
    int clipping = -256;
    for (int i = x; i < x + xLength; i++) {
      for (int i2 = y; i2 < y + yLength; i2++) {
        if (flag) {
          addProjectileClipping(false, i, i2, height, -clipping);
        }
        addClipping(false, i, i2, height, -clipping);
      }
    }
  }

  public static void addClippingForVariableObject(
      boolean before, int x, int y, int height, int type, int direction, boolean flag) {

    addProjectileClippingForVariableObject(before, x, y, height, type, direction, flag);

    if (type == 0) {
      if (direction == 0) {
        addClippingAlternate(before, 128, x, y, height);
        addClippingAlternate(before, 8, x - 1, y, height);
      }
      if (direction == 1) {
        addClippingAlternate(before, 2, x, y, height);
        addClippingAlternate(before, 32, x, y + 1, height);
      }
      if (direction == 2) {
        addClippingAlternate(before, 8, x, y, height);
        addClippingAlternate(before, 128, x + 1, y, height);
      }
      if (direction == 3) {
        addClippingAlternate(before, 32, x, y, height);
        addClippingAlternate(before, 2, x, y - 1, height);
      }
    }
    if (type == 1 || type == 3) {
      if (direction == 0) {
        addClippingAlternate(before, 1, x, y, height);
        addClippingAlternate(before, 16, x - 1, y + 1, height);
      }
      if (direction == 1) {
        addClippingAlternate(before, 4, x, y, height);
        addClippingAlternate(before, 64, x + 1, y + 1, height);
      }
      if (direction == 2) {
        addClippingAlternate(before, 16, x, y, height);
        addClippingAlternate(before, 1, x + 1, y - 1, height);
      }
      if (direction == 3) {
        addClippingAlternate(before, 64, x, y, height);
        addClippingAlternate(before, 4, x - 1, y - 1, height);
      }
    }
    if (type == 2) {
      if (direction == 0) {
        addClippingAlternate(before, 130, x, y, height);
        addClippingAlternate(before, 8, x - 1, y, height);
        addClippingAlternate(before, 32, x, y + 1, height);
      }
      if (direction == 1) {
        addClippingAlternate(before, 10, x, y, height);
        addClippingAlternate(before, 32, x, y + 1, height);
        addClippingAlternate(before, 128, x + 1, y, height);
      }
      if (direction == 2) {
        addClippingAlternate(before, 40, x, y, height);
        addClippingAlternate(before, 128, x + 1, y, height);
        addClippingAlternate(before, 2, x, y - 1, height);
      }
      if (direction == 3) {
        addClippingAlternate(before, 160, x, y, height);
        addClippingAlternate(before, 2, x, y - 1, height);
        addClippingAlternate(before, 8, x - 1, y, height);
      }
    }
  }

  public static void addProjectileClippingForVariableObject(
      boolean before, int x, int y, int height, int type, int direction, boolean flag) {
    if (flag) {
      if (type == 0) {
        if (direction == 0) {
          addProjectileClipping(before, x, y, height, 128);
          addProjectileClipping(before, x - 1, y, height, 8);
        }
        if (direction == 1) {
          addProjectileClipping(before, x, y, height, 2);
          addProjectileClipping(before, x, y + 1, height, 32);
        }
        if (direction == 2) {
          addProjectileClipping(before, x, y, height, 8);
          addProjectileClipping(before, x + 1, y, height, 128);
        }
        if (direction == 3) {
          addProjectileClipping(before, x, y, height, 32);
          addProjectileClipping(before, x, y - 1, height, 2);
        }
      }
      if (type == 1 || type == 3) {
        if (direction == 0) {
          addProjectileClipping(before, x, y, height, 1);
          addProjectileClipping(before, x - 1, y + 1, height, 16);
        }
        if (direction == 1) {
          addProjectileClipping(before, x, y, height, 4);
          addProjectileClipping(before, x + 1, y + 1, height, 64);
        }
        if (direction == 2) {
          addProjectileClipping(before, x, y, height, 16);
          addProjectileClipping(before, x + 1, y - 1, height, 1);
        }
        if (direction == 3) {
          addProjectileClipping(before, x, y, height, 64);
          addProjectileClipping(before, x - 1, y - 1, height, 4);
        }
      }
      if (type == 2) {
        if (direction == 0) {
          addProjectileClipping(before, x, y, height, 130);
          addProjectileClipping(before, x - 1, y, height, 8);
          addProjectileClipping(before, x, y + 1, height, 32);
        }
        if (direction == 1) {
          addProjectileClipping(before, x, y, height, 10);
          addProjectileClipping(before, x, y + 1, height, 32);
          addProjectileClipping(before, x + 1, y, height, 128);
        }
        if (direction == 2) {
          addProjectileClipping(before, x, y, height, 40);
          addProjectileClipping(before, x + 1, y, height, 128);
          addProjectileClipping(before, x, y - 1, height, 2);
        }
        if (direction == 3) {
          addProjectileClipping(before, x, y, height, 160);
          addProjectileClipping(before, x, y - 1, height, 2);
          addProjectileClipping(before, x - 1, y, height, 8);
        }
      }
    }
  }
}
