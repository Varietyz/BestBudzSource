package com.bestbudz.core.cache.map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class RSInterface {

  private static final Logger logger = Logger.getLogger(RSInterface.class.getSimpleName());

  public static RSInterface[] interfaceCache;

  public String popupString;
  public String hoverText;
  public boolean drawsTransparent;
  public int anInt208;
  public int[] requiredValues;
  public int contentType;
  public int[] spritesX;
  public int textHoverColour;
  public int atActionType;
  public String spellName;
  public int anInt219;
  public int width;
  public String tooltip;
  public String selectedActionName;
  public boolean centerText;
  public int scrollLocation;
  public String[] itemActions;
  public int[][] valueIndexArray;
  public boolean aBoolean227;
  public String disabledText;
  public int mouseOverPopupInterface;
  public int invSpritePadX;
  public int textColor;
  public int anInt233;
  public int mediaID;
  public boolean aBoolean235;
  public int parentID;
  public int spellUsableOn;
  public int anInt239;
  public int[] children;
  public int[] childX;
  public boolean usableItemInterface;
  public int invSpritePadY;
  public int[] valueCompareType;
  public int anInt246;
  public int[] spritesY;
  public String message;
  public boolean isBoxInterface;
  public int id;
  public int[] invStackSizes;
  public int[] inv;
  public byte opacity;
  public int anInt255;
  public int anInt256;
  public int anInt257;
  public int anInt258;
  public boolean aBoolean259;
  public int scrollMax;
  public int type;
  public int anInt263;
  public int anInt265;
  public boolean isMouseoverTriggered;
  public int height;
  public boolean textShadow;
  public int modelZoom;
  public int modelRotation1;
  public int modelRotation2;
  public int[] childY;
  public int itemSpriteId1;
  public int itemSpriteId2;
  public int itemSpriteZoom1;
  public int itemSpriteZoom2;
  public int itemSpriteIndex;
  public boolean greyScale;

  public RSInterface() {
    itemSpriteId1 = -1;
    itemSpriteId2 = -1;
    itemSpriteZoom1 = -1;
    itemSpriteZoom2 = -1;
    itemSpriteIndex = 0;
  }

  @SuppressWarnings("unchecked")
  public static void unpack() {
    try {
      Gson gson = new Gson();
      Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
      List<Map<String, Object>> entries;

      try (FileReader reader = new FileReader("./data/def/interfaces/interfaces.json")) {
        entries = gson.fromJson(reader, listType);
      }

      interfaceCache = new RSInterface[60000];

      for (Map<String, Object> entry : entries) {
        int id = getInt(entry, "id", 0);
        if (id < 0 || id >= interfaceCache.length) continue;

        RSInterface rsi = new RSInterface();
        interfaceCache[id] = rsi;

        rsi.id = id;
        rsi.parentID = getInt(entry, "parentID", 0);
        rsi.type = getInt(entry, "type", 0);
        rsi.atActionType = getInt(entry, "atActionType", 0);
        rsi.contentType = getInt(entry, "contentType", 0);
        rsi.width = getInt(entry, "width", 0);
        rsi.height = getInt(entry, "height", 0);
        rsi.opacity = (byte) getInt(entry, "opacity", 0);
        rsi.mouseOverPopupInterface = getInt(entry, "mouseOverPopupInterface", -1);

        rsi.scrollMax = getInt(entry, "scrollMax", 0);
        rsi.scrollLocation = getInt(entry, "scrollLocation", 0);
        rsi.isMouseoverTriggered = getBool(entry, "isMouseoverTriggered");
        rsi.drawsTransparent = getBool(entry, "drawsTransparent");

        rsi.valueCompareType = getIntArray(entry, "valueCompareType");
        rsi.requiredValues = getIntArray(entry, "requiredValues");
        if (entry.containsKey("valueIndexArray") && entry.get("valueIndexArray") != null) {
          List<List<Double>> outer = (List<List<Double>>) entry.get("valueIndexArray");
          rsi.valueIndexArray = new int[outer.size()][];
          for (int i = 0; i < outer.size(); i++) {
            if (outer.get(i) != null) {
              List<Double> inner = outer.get(i);
              rsi.valueIndexArray[i] = new int[inner.size()];
              for (int j = 0; j < inner.size(); j++) {
                rsi.valueIndexArray[i][j] = inner.get(j).intValue();
              }
            }
          }
        }

        rsi.children = getIntArray(entry, "children");
        rsi.childX = getIntArray(entry, "childX");
        rsi.childY = getIntArray(entry, "childY");

        // Inventory
        rsi.inv = getIntArray(entry, "inv");
        rsi.invStackSizes = getIntArray(entry, "invStackSizes");
        if (entry.containsKey("itemActions") && entry.get("itemActions") != null) {
          List<String> actions = (List<String>) entry.get("itemActions");
          rsi.itemActions = actions.toArray(new String[0]);
        }
        rsi.invSpritePadX = getInt(entry, "invSpritePadX", 0);
        rsi.invSpritePadY = getInt(entry, "invSpritePadY", 0);
        rsi.aBoolean259 = getBool(entry, "aBoolean259");
        rsi.isBoxInterface = getBool(entry, "isBoxInterface");
        rsi.usableItemInterface = getBool(entry, "usableItemInterface");
        rsi.aBoolean235 = getBool(entry, "aBoolean235");

        // Text
        rsi.message = getString(entry, "message");
        rsi.disabledText = getString(entry, "disabledText");
        rsi.textColor = getInt(entry, "textColor", 0);
        rsi.textHoverColour = getInt(entry, "textHoverColour", 0);
        rsi.centerText = getBool(entry, "centerText");
        rsi.textShadow = getBool(entry, "textShadow");
        rsi.anInt219 = getInt(entry, "anInt219", 0);
        rsi.anInt239 = getInt(entry, "anInt239", 0);

        // Sprite
        rsi.spritesX = getIntArray(entry, "spritesX");
        rsi.spritesY = getIntArray(entry, "spritesY");

        // Model
        rsi.mediaID = getInt(entry, "mediaID", 0);
        rsi.modelZoom = getInt(entry, "modelZoom", 0);
        rsi.modelRotation1 = getInt(entry, "modelRotation1", 0);
        rsi.modelRotation2 = getInt(entry, "modelRotation2", 0);
        rsi.anInt233 = getInt(entry, "anInt233", 0);
        rsi.anInt255 = getInt(entry, "anInt255", 0);
        rsi.anInt256 = getInt(entry, "anInt256", 0);
        rsi.anInt257 = getInt(entry, "anInt257", 0);
        rsi.anInt258 = getInt(entry, "anInt258", 0);

        // Misc
        rsi.tooltip = getString(entry, "tooltip");
        rsi.selectedActionName = getString(entry, "selectedActionName");
        rsi.spellName = getString(entry, "spellName");
        rsi.spellUsableOn = getInt(entry, "spellUsableOn", 0);
        rsi.popupString = getString(entry, "popupString");
        rsi.hoverText = getString(entry, "hoverText");
        rsi.aBoolean227 = getBool(entry, "aBoolean227");
        rsi.greyScale = getBool(entry, "greyScale");

        rsi.anInt208 = getInt(entry, "anInt208", 0);
        rsi.anInt246 = getInt(entry, "anInt246", 0);
        rsi.anInt263 = getInt(entry, "anInt263", 0);
        rsi.anInt265 = getInt(entry, "anInt265", 0);

        rsi.itemSpriteId1 = getInt(entry, "itemSpriteId1", -1);
        rsi.itemSpriteId2 = getInt(entry, "itemSpriteId2", -1);
        rsi.itemSpriteZoom1 = getInt(entry, "itemSpriteZoom1", -1);
        rsi.itemSpriteZoom2 = getInt(entry, "itemSpriteZoom2", -1);
        rsi.itemSpriteIndex = getInt(entry, "itemSpriteIndex", 0);
      }

      logger.info(entries.size() + " interfaces have been loaded from JSON successfully.");
    } catch (Exception e) {
      logger.severe("Failed to load interfaces from JSON: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static int getInt(Map<String, Object> map, String key, int defaultVal) {
    Object v = map.get(key);
    if (v == null) return defaultVal;
    return ((Number) v).intValue();
  }

  private static boolean getBool(Map<String, Object> map, String key) {
    Object v = map.get(key);
    if (v == null) return false;
    return (Boolean) v;
  }

  private static String getString(Map<String, Object> map, String key) {
    return (String) map.get(key);
  }

  @SuppressWarnings("unchecked")
  private static int[] getIntArray(Map<String, Object> map, String key) {
    Object v = map.get(key);
    if (v == null) return null;
    List<Double> list = (List<Double>) v;
    int[] arr = new int[list.size()];
    for (int i = 0; i < list.size(); i++) {
      arr[i] = list.get(i).intValue();
    }
    return arr;
  }
}
