package com.bestbudz.rs2.content.io.sqlite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class SQLiteDB {

	private static Connection connection;
	private static boolean SCHEMA_READY = false;
	private static final String DB_URL = "jdbc:sqlite:data/database/playerdata.db";
	private static boolean closed = false;

	private SQLiteDB() {}

	public static void init() throws IOException {
		Files.createDirectories(Paths.get("data/database"));

		if (connection != null) {
			try {
				if (!connection.isClosed()) return;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		try {
			System.out.println("[SQLiteDB] Connecting...");
			connection = DriverManager.getConnection(DB_URL);
			System.out.println("[SQLiteDB] Connected.");
			configurePragmas();
			initializeSchema();
		} catch (Exception e) {
			System.err.println("[SQLiteDB] Initialization failed.");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void configurePragmas() throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			stmt.execute("PRAGMA journal_mode = WAL;");
			stmt.execute("PRAGMA synchronous = NORMAL;");
			stmt.execute("PRAGMA temp_store = MEMORY;");
			stmt.execute("PRAGMA foreign_keys = ON;");
			stmt.execute("PRAGMA cache_size = 10000;");
			stmt.execute("PRAGMA busy_timeout=5000;");
		}
	}

	private static void initializeSchema() throws SQLException {
		if (SCHEMA_READY) return;
		try (Statement stmt = connection.createStatement()) {
			stmt.execute("PRAGMA foreign_keys = ON;");
			stmt.execute("PRAGMA defer_foreign_keys = OFF;");

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

			stmt.execute(
				"CREATE TABLE IF NOT EXISTS player_inventory (" +
					"username TEXT COLLATE NOCASE," +
					"slot INTEGER," +
					"item_id INTEGER," +
					"amount INTEGER," +
					"PRIMARY KEY (username, slot)," +
					"FOREIGN KEY (username) REFERENCES player(username) ON DELETE CASCADE" +
					");"
			);

			stmt.execute(
				"CREATE TABLE IF NOT EXISTS player_equipment (" +
					"username TEXT COLLATE NOCASE," +
					"slot INTEGER," +
					"item_id INTEGER," +
					"amount INTEGER," +
					"PRIMARY KEY (username, slot)," +
					"FOREIGN KEY (username) REFERENCES player(username) ON DELETE CASCADE" +
					");"
			);

			stmt.execute(
				"CREATE TABLE IF NOT EXISTS player_bank (" +
					"username TEXT COLLATE NOCASE," +
					"slot INTEGER," +
					"item_id INTEGER," +
					"amount INTEGER," +
					"tab_amounts TEXT," +
					"PRIMARY KEY (username, slot)," +
					"FOREIGN KEY (username) REFERENCES player(username) ON DELETE CASCADE" +
					");"
			);
		}
		SCHEMA_READY = true;
	}

	public static boolean isClosed() {
		return closed;
	}

	public static synchronized Connection getConnection() {
		try {
			if (connection == null || connection.isClosed() || closed) {
				init();
				closed = false;
			}
		} catch (SQLException | IOException e) {
			throw new IllegalStateException("[SQLiteDB] Connection state check failed.", e);
		}
		return connection;
	}

	public static synchronized void close() {
		if (closed) return;
		try {
			if (connection != null && !connection.isClosed()) {
				System.out.println("[SQLiteDB] Closing SQLite connection...");
				connection.close();
				closed = true;
			}
		} catch (SQLException ex) {
			System.err.println("[SQLiteDB] Failed to close connection:");
			ex.printStackTrace();
		}
	}

	public static List<String> getAllPlayerUsernames() throws SQLException {
		List<String> names = new ArrayList<>();
		String sql = "SELECT username FROM player";
		try (PreparedStatement ps = getConnection().prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				names.add(rs.getString(1).toLowerCase());
			}
		}
		return names;
	}

}
