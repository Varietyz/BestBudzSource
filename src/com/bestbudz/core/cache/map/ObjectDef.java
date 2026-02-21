package com.bestbudz.core.cache.map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.bestbudz.core.util.Utility;

public final class ObjectDef {

  private static final Logger logger = Logger.getLogger(ObjectDef.class.getSimpleName());
  private static ObjectDef[] defs;
  private static int objects = 0;

  public String name;
  public byte[] description;
  public int type;
  public int objectSizeX;
  public int objectSizeY;
  public boolean aBoolean779;
  public boolean aBoolean757;
  public boolean aBoolean767;
  public boolean hasActions;
  public String[] actions;
  public boolean aBoolean762;
  public boolean aBoolean764;
  public int anInt781;
  public int anInt775;
  public int anInt746;
  public int anInt758;
  public int anInt768;
  public boolean aBoolean736;
  public int anInt774;
  public int anInt749;
  public int[] childrenIDs;
  int[] modifiedModelColors;
  int[] originalModelColors;
  int[] anIntArray773;
  int[] anIntArray776;

  private ObjectDef() {
    type = -1;
  }

  public static int getObjects() {
    return objects;
  }

  public static ObjectDef getObjectDef(int i) {
    if (i < 0 || i >= objects) return null;
    return defs[i];
  }

  @SuppressWarnings("unchecked")
  public static void loadConfig() {
    try {
      Gson gson = new Gson();
      Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
      List<Map<String, Object>> entries;

      try (FileReader reader = new FileReader("./data/map/objectdata/object_definitions.json")) {
        entries = gson.fromJson(reader, listType);
      }

      // Find max ID to size array
      int maxId = 0;
      for (Map<String, Object> entry : entries) {
        int id = ((Number) entry.get("id")).intValue();
        if (id > maxId) maxId = id;
      }

      defs = new ObjectDef[maxId + 1];
      objects = maxId + 1;

      // Initialize all entries with defaults
      for (int i = 0; i < defs.length; i++) {
        defs[i] = new ObjectDef();
        defs[i].type = i;
        defs[i].setDefaults();
      }

      // Populate from JSON
      for (Map<String, Object> entry : entries) {
        int id = ((Number) entry.get("id")).intValue();
        ObjectDef def = defs[id];
        def.type = id;

        def.name = (String) entry.get("name");

        if (entry.containsKey("description") && entry.get("description") != null) {
          def.description = ((String) entry.get("description")).getBytes();
        }

        def.objectSizeX = getInt(entry, "sizeX", 1);
        def.objectSizeY = getInt(entry, "sizeY", 1);
        def.aBoolean779 = getBool(entry, "blocksMovement", true);
        def.aBoolean757 = getBool(entry, "blocksProjectile", true);
        def.aBoolean767 = getBool(entry, "isWalkable", true);
        def.hasActions = getBool(entry, "hasActions", false);
        def.aBoolean762 = getBool(entry, "adjustToTerrain", false);
        def.aBoolean764 = getBool(entry, "nonFlatShading", false);
        def.anInt781 = getInt(entry, "animation", -1);
        def.anInt775 = getInt(entry, "decorDisplacement", 16);
        def.anInt746 = getInt(entry, "mapScene", -1);
        def.anInt758 = getInt(entry, "mapFunction", -1);
        def.anInt768 = getInt(entry, "face", 0);
        def.aBoolean736 = getBool(entry, "obstructsGround", false);
        def.anInt774 = getInt(entry, "varbitId", -1);
        def.anInt749 = getInt(entry, "configId", -1);

        if (entry.containsKey("actions") && entry.get("actions") != null) {
          List<String> actionList = (List<String>) entry.get("actions");
          def.actions = new String[actionList.size()];
          for (int j = 0; j < actionList.size(); j++) {
            def.actions[j] = actionList.get(j);
          }
        }

        if (entry.containsKey("childrenIDs") && entry.get("childrenIDs") != null) {
          def.childrenIDs = toIntArray((List<Double>) entry.get("childrenIDs"));
        }

        if (entry.containsKey("modifiedModelColors") && entry.get("modifiedModelColors") != null) {
          def.modifiedModelColors = toIntArray((List<Double>) entry.get("modifiedModelColors"));
        }

        if (entry.containsKey("originalModelColors") && entry.get("originalModelColors") != null) {
          def.originalModelColors = toIntArray((List<Double>) entry.get("originalModelColors"));
        }

        if (entry.containsKey("modelIds") && entry.get("modelIds") != null) {
          def.anIntArray773 = toIntArray((List<Double>) entry.get("modelIds"));
        }

        if (entry.containsKey("modelTypes") && entry.get("modelTypes") != null) {
          def.anIntArray776 = toIntArray((List<Double>) entry.get("modelTypes"));
        }
      }

      logger.info(Utility.format(objects) + " Objects have been loaded from JSON successfully.");
    } catch (Exception e) {
      logger.severe("Failed to load object definitions from JSON: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static int getInt(Map<String, Object> map, String key, int defaultVal) {
    Object v = map.get(key);
    if (v == null) return defaultVal;
    return ((Number) v).intValue();
  }

  private static boolean getBool(Map<String, Object> map, String key, boolean defaultVal) {
    Object v = map.get(key);
    if (v == null) return defaultVal;
    return (Boolean) v;
  }

  private static int[] toIntArray(List<Double> list) {
    if (list == null) return null;
    int[] arr = new int[list.size()];
    for (int i = 0; i < list.size(); i++) {
      arr[i] = list.get(i).intValue();
    }
    return arr;
  }

  private void setDefaults() {
    anIntArray773 = null;
    anIntArray776 = null;
    name = null;
    description = null;
    modifiedModelColors = null;
    originalModelColors = null;
    objectSizeX = 1;
    objectSizeY = 1;
    aBoolean767 = true;
    aBoolean757 = true;
    hasActions = false;
    aBoolean762 = false;
    aBoolean764 = false;
    anInt781 = -1;
    anInt775 = 16;
    actions = null;
    anInt746 = -1;
    anInt758 = -1;
    aBoolean779 = true;
    anInt768 = 0;
    aBoolean736 = false;
    anInt774 = -1;
    anInt749 = -1;
    childrenIDs = null;
  }

  public boolean hasActions() {
    return hasActions || actions != null;
  }

  public boolean hasName() {
    return name != null && name.length() > 1;
  }

  public int xLength() {
    return objectSizeX;
  }

  public int yLength() {
    return objectSizeY;
  }
}
