package com.bestbudz.rs2.content.io.sqlite;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.List;
import java.util.Map;

public class BulkPlayerImporter {

  private static final String DB_PATH = "data/database/playerdata.db";
  private static final String DETAILS_DIR = "data/characters/details";
  private static final String CONTAINERS_DIR = "data/characters/containers";

  public static void main(String[] args) throws Exception {
    File dir = new File(DETAILS_DIR);
    if (!dir.exists() || !dir.isDirectory()) throw new RuntimeException("Missing: " + DETAILS_DIR);

    Gson gson = new Gson();
    Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    ensureSchema(conn);
    conn.setAutoCommit(false);

    for (File detailsFile : dir.listFiles((d, name) -> name.endsWith(".json"))) {
      String name = detailsFile.getName();
      String username = name.replace(".json", "");
      File containersFile = new File(CONTAINERS_DIR, name);

      Map<String, Object> details = loadJson(detailsFile, gson);
      Map<String, Object> containers =
          containersFile.exists() ? loadJson(containersFile, gson) : null;

      try {
        insertPlayer(conn, gson, username, details);
        if (containers != null) insertContainers(conn, gson, username, containers);
        System.out.println("[✔] Imported: " + username);
      } catch (Exception e) {
        System.err.println("[✖] Failed: " + username + " — " + e.getMessage());
      }
    }

    conn.commit();
    conn.close();
  }

  private static Map<String, Object> loadJson(File file, Gson gson) throws Exception {
    Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
    return gson.fromJson(new FileReader(file), mapType);
  }

	  private static void insertPlayer(Connection conn, Gson gson, String username, Map<String, Object> data) throws SQLException {
		  final String sql =
			  "REPLACE INTO player (" +
				  "username, password, x, y, z, rights, lastKnownUID, pin, credits, host, yellTitle, banned, banLength, moneyPouch, jailLength, shopCollection, " +
				  "lastClanChat, muted, isMember, jailed, muteLength, weaponPoints, fightCavesWave, mageBook, necromanceBook, retaliate, " +
				  "expLock, gwkc, poisoned, pouchPayment, poisonDmg, mercenaryTask, mercenaryAmount, mercenaryDifficulty, professionsGrade, experience, gender, " +
				  "appearance, colours, chatEffects, transparentPanel, transparentChatbox, sideStones, left, skullIcon, specialAssault, assaultStyle, assaultType, " +
				  "chillPoints, teleblockTime, familiarId, logStoner, pestPoints, arenaPoints, musicVolume, soundVolume, deaths, kills, rogueKills, rogueRecord, " +
				  "hunterKills, hunterRecord, bountyPoints, blackMarks, rareDropEP, rareDropsReceived, professionGoals, lastKilledStoners, stonerAchievements, " +
				  "achievementsPoints, unlockedCredits, quickNecromances, stonerProperties, counterExp, advancePoints, professionAdvances, totalAdvances, " +
				  "toxicBlowpipe, seasTrident, swampTrident, serpentineHelment, unlockedTitles, stonerTitle" +
				  ") VALUES (" +
				  "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
				  ")";

		  try (PreparedStatement ps = conn.prepareStatement(sql)) {
			  int i = 1;
			  ps.setString(i++, username);
			  ps.setString(i++, str(data, "password"));
			  ps.setInt(i++, num(data, "x"));
			  ps.setInt(i++, num(data, "y"));
			  ps.setInt(i++, num(data, "z"));
			  ps.setInt(i++, num(data, "rights"));
			  ps.setString(i++, str(data, "lastKnownUID"));
			  ps.setString(i++, str(data, "pin").isEmpty() ? null : str(data, "pin"));
			  ps.setInt(i++, num(data, "credits"));
			  ps.setString(i++, str(data, "host"));
			  ps.setString(i++, str(data, "yellTitle"));
			  ps.setBoolean(i++, bool(data, "banned"));
			  ps.setLong(i++, num(data, "banLength"));
			  ps.setLong(i++, longNum(data, "moneyPouch"));
			  ps.setLong(i++, num(data, "jailLength"));
			  ps.setLong(i++, num(data, "shopCollection"));
			  ps.setString(i++, str(data, "lastClanChat"));
			  ps.setBoolean(i++, bool(data, "muted"));
			  ps.setBoolean(i++, bool(data, "isMember"));
			  ps.setBoolean(i++, bool(data, "jailed"));
			  ps.setLong(i++, num(data, "muteLength"));
			  ps.setInt(i++, num(data, "weaponPoints"));
			  ps.setInt(i++, num(data, "fightCavesWave"));
			  ps.setInt(i++, num(data, "mageBook"));
			  ps.setInt(i++, num(data, "necromanceBook"));
			  ps.setBoolean(i++, bool(data, "retaliate"));
			  ps.setBoolean(i++, bool(data, "expLock"));
			  ps.setString(i++, json(gson, data, "gwkc"));
			  ps.setBoolean(i++, bool(data, "poisoned"));
			  ps.setBoolean(i++, bool(data, "pouchPayment"));
			  ps.setInt(i++, num(data, "poisonDmg"));
			  ps.setString(i++, str(data, "mercenaryTask"));
			  ps.setInt(i++, num(data, "mercenaryAmount"));
			  ps.setString(i++, str(data, "mercenaryDifficulty"));
			  ps.setString(i++, json(gson, data, "professionsGrade"));
			  ps.setString(i++, json(gson, data, "experience"));
			  ps.setByte(i++, (byte) num(data, "gender"));
			  ps.setString(i++, json(gson, data, "appearance"));
			  ps.setString(i++, json(gson, data, "colours"));
			  ps.setInt(i++, num(data, "chatEffects"));
			  ps.setInt(i++, num(data, "transparentPanel"));
			  ps.setInt(i++, num(data, "transparentChatbox"));
			  ps.setInt(i++, num(data, "sideStones"));
			  ps.setLong(i++, num(data, "left"));
			  ps.setInt(i++, num(data, "skullIcon"));
			  ps.setInt(i++, num(data, "specialAssault"));
			  ps.setString(i++, str(data, "assaultStyle"));
			  ps.setString(i++, str(data, "assaultType"));
			  ps.setInt(i++, num(data, "chillPoints"));
			  ps.setInt(i++, num(data, "teleblockTime"));
			  ps.setInt(i++, num(data, "familiarId"));
			  ps.setBoolean(i++, bool(data, "logStoner"));
			  ps.setInt(i++, num(data, "pestPoints"));
			  ps.setInt(i++, num(data, "arenaPoints"));
			  ps.setInt(i++, num(data, "musicVolume"));
			  ps.setInt(i++, num(data, "soundVolume"));
			  ps.setInt(i++, num(data, "deaths"));
			  ps.setInt(i++, num(data, "kills"));
			  ps.setInt(i++, num(data, "rogueKills"));
			  ps.setInt(i++, num(data, "rogueRecord"));
			  ps.setInt(i++, num(data, "hunterKills"));
			  ps.setInt(i++, num(data, "hunterRecord"));
			  ps.setInt(i++, num(data, "bountyPoints"));
			  ps.setInt(i++, num(data, "blackMarks"));
			  ps.setDouble(i++, dbl(data, "rareDropEP"));
			  ps.setInt(i++, num(data, "rareDropsReceived"));
			  ps.setString(i++, json(gson, data, "professionGoals"));
			  ps.setString(i++, json(gson, data, "lastKilledStoners"));
			  ps.setString(i++, json(gson, data, "stonerAchievements"));
			  ps.setInt(i++, num(data, "achievementsPoints"));
			  ps.setString(i++, json(gson, data, "unlockedCredits"));
			  ps.setString(i++, json(gson, data, "quickNecromances"));
			  ps.setString(i++, json(gson, data, "stonerProperties"));
			  ps.setDouble(i++, dbl(data, "expCounter"));
			  ps.setInt(i++, num(data, "advancePoints"));
			  ps.setString(i++, normalizeProfessionAdvancesFlat(gson, data));
			  ps.setInt(i++, num(data, "totalAdvances"));
			  ps.setString(i++, json(gson, data, "blowpipe"));
			  ps.setString(i++, json(gson, data, "seasTrident"));
			  ps.setString(i++, json(gson, data, "swampTrident"));
			  ps.setString(i++, json(gson, data, "serpentineHelmet"));
			  ps.setString(i++, json(gson, data, "unlockedTitles"));
			  ps.setString(i++, json(gson, data, "stonerTitle"));

			  ps.executeUpdate();
		  }
	  }

	private static void insertContainers(
		Connection conn, Gson gson, String username, Map<String, Object> data) throws SQLException {

		insertItems(conn, gson, username, "player_inventory", "box", null, data);
		insertItems(conn, gson, username, "player_equipment", "equipment", null, data);

		Object rawTabAmounts = data.get("tabAmounts");
		String tabJson = "[0,0,0,0,0,0,0,0,0,0]";

		if (rawTabAmounts instanceof List<?>) {
			List<?> list = (List<?>) rawTabAmounts;
			int[] ints = new int[list.size()];
			for (int i = 0; i < list.size(); i++) {
				Object val = list.get(i);
				ints[i] = (val instanceof Number) ? ((Number) val).intValue() : 0;
			}
			tabJson = gson.toJson(ints);
		}

		insertItems(conn, gson, username, "player_bank", "bank", tabJson, data);
	}

  private static void insertItems(
      Connection conn,
      Gson gson,
      String username,
      String table,
      String key,
      Object extra,
      Map<String, Object> data)
      throws SQLException {
    List<Object> items = (List<Object>) dataOrEmpty(data, key);
    String sql =
        table.equals("player_bank")
            ? "REPLACE INTO "
                + table
                + " (username, slot, item_id, amount, tab_amounts) VALUES (?, ?, ?, ?, ?)"
            : "REPLACE INTO " + table + " (username, slot, item_id, amount) VALUES (?, ?, ?, ?)";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      for (int i = 0; i < items.size(); i++) {
        Object obj = items.get(i);
        if (obj instanceof Map) {
          Map<?, ?> item = (Map<?, ?>) obj;

          ps.setString(1, username);
          ps.setInt(2, i);
          ps.setInt(3, ((Number) item.get("id")).intValue());
          ps.setInt(4, ((Number) item.get("amount")).intValue());
          if (table.equals("player_bank")) ps.setString(5, extra.toString());
          ps.addBatch();
        }
      }
      ps.executeBatch();
    }
  }

  private static void ensureSchema(Connection conn) throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      stmt.execute("PRAGMA foreign_keys = ON;");
      stmt.execute("PRAGMA defer_foreign_keys = OFF;");

      // Player core data - All primitive and JSON fields in one table
      stmt.execute(
          "CREATE TABLE IF NOT EXISTS player ("
              + "username TEXT PRIMARY KEY COLLATE NOCASE,"
              + "password TEXT,"
              + "x INTEGER,"
              + "y INTEGER,"
              + "z INTEGER,"
              + "rights INTEGER,"
              + "lastKnownUID TEXT,"
              + "pin TEXT,"
              + "credits INTEGER,"
              + "host TEXT,"
              + "yellTitle TEXT,"
              + "banned BOOLEAN,"
              + "banLength INTEGER,"
              + "moneyPouch BIGINT,"
              + "jailLength INTEGER,"
              + "shopCollection INTEGER,"
              + "lastClanChat TEXT,"
              + "muted BOOLEAN,"
              + "isMember BOOLEAN,"
              + "jailed BOOLEAN,"
              + "muteLength INTEGER,"
              + "weaponPoints INTEGER,"
              + "fightCavesWave INTEGER,"
              + "mageBook INTEGER,"
              + "necromanceBook INTEGER,"
              + "retaliate BOOLEAN,"
              + "expLock BOOLEAN,"
              + "gwkc TEXT,"
              + "poisoned BOOLEAN,"
              + "pouchPayment BOOLEAN,"
              + "poisonDmg INTEGER,"
              + "mercenaryTask TEXT,"
              + "mercenaryAmount INTEGER,"
              + "mercenaryDifficulty TEXT,"
              + "professionsGrade TEXT,"
              + "experience TEXT,"
              + "gender INTEGER,"
              + "appearance TEXT,"
              + "colours TEXT,"
              + "chatEffects INTEGER,"
              + "transparentPanel INTEGER,"
              + "transparentChatbox INTEGER,"
              + "sideStones INTEGER,"
              + "left INTEGER,"
              + "skullIcon INTEGER,"
              + "specialAssault INTEGER,"
              + "assaultStyle TEXT,"
              + "assaultType TEXT,"
              + "chillPoints INTEGER,"
              + "teleblockTime INTEGER,"
              + "familiarId INTEGER,"
              + "logStoner BOOLEAN,"
              + "pestPoints INTEGER,"
              + "arenaPoints INTEGER,"
              + "musicVolume INTEGER,"
              + "soundVolume INTEGER,"
              + "deaths INTEGER,"
              + "kills INTEGER,"
              + "rogueKills INTEGER,"
              + "rogueRecord INTEGER,"
              + "hunterKills INTEGER,"
              + "hunterRecord INTEGER,"
              + "bountyPoints INTEGER,"
              + "blackMarks INTEGER,"
              + "rareDropEP REAL,"
              + "rareDropsReceived INTEGER,"
              + "professionGoals TEXT,"
              + "lastKilledStoners TEXT,"
              + "stonerAchievements TEXT,"
              + "achievementsPoints INTEGER,"
              + "unlockedCredits TEXT,"
              + "quickNecromances TEXT,"
              + "stonerProperties TEXT,"
              + "counterExp REAL,"
              + "advancePoints INTEGER,"
              + "professionAdvances TEXT,"
              + "totalAdvances INTEGER,"
              + "toxicBlowpipe TEXT,"
              + "seasTrident TEXT,"
              + "swampTrident TEXT,"
              + "serpentineHelment TEXT,"
              + "unlockedTitles TEXT,"
              + "stonerTitle TEXT,"
              + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP"
              + ");");

      // Inventory
      stmt.execute(
          "CREATE TABLE IF NOT EXISTS player_inventory ("
              + "username TEXT COLLATE NOCASE,"
              + "slot INTEGER,"
              + "item_id INTEGER,"
              + "amount INTEGER,"
              + "PRIMARY KEY (username, slot),"
              + "FOREIGN KEY (username) REFERENCES player(username) ON DELETE CASCADE"
              + ");");

      // Equipment
      stmt.execute(
          "CREATE TABLE IF NOT EXISTS player_equipment ("
              + "username TEXT COLLATE NOCASE,"
              + "slot INTEGER,"
              + "item_id INTEGER,"
              + "amount INTEGER,"
              + "PRIMARY KEY (username, slot),"
              + "FOREIGN KEY (username) REFERENCES player(username) ON DELETE CASCADE"
              + ");");

      // Bank
      stmt.execute(
          "CREATE TABLE IF NOT EXISTS player_bank ("
              + "username TEXT COLLATE NOCASE,"
              + "slot INTEGER,"
              + "item_id INTEGER,"
              + "amount INTEGER,"
              + "tab_amounts TEXT,"
              + "PRIMARY KEY (username, slot),"
              + "FOREIGN KEY (username) REFERENCES player(username) ON DELETE CASCADE"
              + ");");
    }
  }

  // Utilities

  private static Object dataOrEmpty(Map<String, Object> map, String key) {
    return map.getOrDefault(key, List.of());
  }

  private static String str(Map<String, Object> m, String k) {
    Object val = m.get(k);
    return val == null ? "" : String.valueOf(val);
  }

  private static int num(Map<String, Object> m, String k) {
    Object val = m.get(k);
    return val == null ? 0 : ((Number) val).intValue();
  }

  private static double dbl(Map<String, Object> m, String k) {
    Object val = m.get(k);
    return val == null ? 0.0 : ((Number) val).doubleValue();
  }

  private static boolean bool(Map<String, Object> m, String k) {
    Object val = m.get(k);
    return val != null && Boolean.parseBoolean(val.toString());
  }

  private static String json(Gson gson, Map<String, Object> m, String k) {
    Object val = m.get(k);
    return val == null ? "[]" : gson.toJson(val);
  }

	private static long longNum(Map<String, Object> m, String k) {
		Object val = m.get(k);
		return val == null ? 0L : ((Number) val).longValue();
	}


	private static String normalizeProfessionAdvancesFlat(Gson gson, Map<String, Object> data) {
    Object raw = data.get("professionAdvances");

    if (raw instanceof List<?>) {
      List<?> list = (List<?>) raw;
      if (list.size() == 25) {
        long[] flat = new long[50]; // 25 * 2
        for (int i = 0; i < 25; i++) {
          Object pair = list.get(i);
          if (pair instanceof List<?>) {
            List<?> p = (List<?>) pair;
            if (p.size() == 2) {
              flat[i * 2] = ((Number) p.get(0)).longValue(); // grade
              flat[i * 2 + 1] = ((Number) p.get(1)).longValue(); // exp
              continue;
            }
          }
          // fallback if corrupted entry
          return gson.toJson(new long[50]);
        }
        return gson.toJson(flat);
      }
    }

    // fallback
    return gson.toJson(new long[50]);
  }

}
