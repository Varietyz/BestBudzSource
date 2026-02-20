package com.bestbudz.rs2.content.io.sqlite;

import com.bestbudz.core.security.PasswordEncryption;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class StonerSaveUtil {

	private static final Gson gson = new Gson();

	private StonerSaveUtil() {}

	public static void saveToDatabase(Stoner stoner) {
		final String username = stoner.getUsername();

		Connection conn = SQLiteDB.getConnection();
		try {
			conn.setAutoCommit(false);

			// Encrypt password if it's not already encrypted
			String passwordToSave = stoner.getPassword();
			boolean isEncrypted = true; // Assume we're saving encrypted

			// Check if the current password is plaintext (during session it might be)
			if (passwordToSave != null && !isPasswordLikelyEncrypted(passwordToSave) && username != "BestBud") {
				String encryptedPassword = PasswordEncryption.encrypt(passwordToSave);
				if (encryptedPassword != null) {
					passwordToSave = encryptedPassword;
				} else {
					System.err.println("[StonerSaveUtil] Failed to encrypt password for user: " + username);
					// Keep original password and mark as not encrypted
					isEncrypted = false;
				}
			}

			// 1. Save player core state (all fields/arrays as JSON, 1 row, upsert)
			final String playerUpsert =
				"REPLACE INTO player (" +
					"username, password, password_encrypted, x, y, z, rights, lastKnownUID, pin, credits, host, yellTitle, banned, banLength, moneyPouch, jailLength, shopCollection, lastClanChat, muted, isMember, jailed, muteLength, weaponPoints, fightCavesWave, mageBook, necromanceBook, retaliate, expLock, gwkc, poisoned, pouchPayment, poisonDmg, mercenaryTask, mercenaryAmount, mercenaryDifficulty, professionsGrade, experience, gender, appearance, colours, chatEffects, transparentPanel, transparentChatbox, sideStones, left, skullIcon, specialAssault, assaultStyle, assaultType, chillPoints, teleblockTime, familiarId, logStoner, pestPoints, arenaPoints, musicVolume, soundVolume, deaths, kills, rogueKills, rogueRecord, hunterKills, hunterRecord, bountyPoints, blackMarks, rareDropEP, rareDropsReceived, professionGoals, lastKilledStoners, stonerAchievements, achievementsPoints, unlockedCredits, quickNecromances, stonerProperties, counterExp, advancePoints, professionAdvances, totalAdvances, toxicBlowpipe, seasTrident, swampTrident, serpentineHelment, unlockedTitles, stonerTitle" +
					") VALUES (" +
					"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
					")";

			try (PreparedStatement ps = conn.prepareStatement(playerUpsert)) {
				int idx = 1;
				ps.setString(idx++, stoner.getUsername());
				ps.setString(idx++, passwordToSave);
				ps.setInt(idx++, isEncrypted ? 1 : 0); // Add the password_encrypted flag
				ps.setInt(idx++, stoner.getLocation().getX());
				ps.setInt(idx++, stoner.getLocation().getY());
				ps.setInt(idx++, stoner.getLocation().getZ());
				ps.setInt(idx++, stoner.getRights());
				ps.setString(idx++, stoner.getUid());  // or stoner.getLastKnownUID()
				ps.setString(idx++, stoner.getPin());
				ps.setInt(idx++, stoner.getCredits());
				ps.setString(idx++, stoner.getClient().getHost());
				ps.setString(idx++, stoner.getYellTitle());
				ps.setBoolean(idx++, stoner.isBanned());
				ps.setLong(idx++, stoner.getBanLength());
				ps.setLong(idx++, stoner.getMoneyPouch());
				ps.setLong(idx++, stoner.getJailLength());
				ps.setLong(idx++, stoner.getShopCollection());
				ps.setString(idx++, stoner.lastClanChat); // Or stoner.getLastClanChat()
				ps.setBoolean(idx++, stoner.isMuted());
				ps.setBoolean(idx++, stoner.isMember());
				ps.setBoolean(idx++, stoner.isJailed());
				ps.setLong(idx++, stoner.getMuteLength());
				ps.setInt(idx++, stoner.getWeaponPoints());
				ps.setInt(idx++, stoner.getBloodTrialDetails().getStage());
				ps.setInt(idx++, stoner.getMage().getMageBook());
				ps.setInt(idx++, stoner.getResonanceInterface());
				ps.setBoolean(idx++, stoner.isRetaliate());
				ps.setBoolean(idx++, stoner.getProfession().isExpLocked());
				ps.setString(idx++, gson.toJson(stoner.getMinigames().getGWKC()));
				ps.setBoolean(idx++, stoner.isPoisoned());
				ps.setBoolean(idx++, stoner.isPouchPayment());
				ps.setInt(idx++, stoner.getPoisonDamage());
				ps.setString(idx++, stoner.getMercenary().getTask());
				ps.setByte(idx++, stoner.getMercenary().getAmount());
				ps.setString(idx++, (stoner.getMercenary().getCurrent() != null ? stoner.getMercenary().getCurrent().name() : null));
				ps.setString(idx++, gson.toJson(stoner.getProfession().getGrades()));
				ps.setString(idx++, gson.toJson(stoner.getProfession().getExperience()));
				ps.setByte(idx++, (byte)stoner.getGender());
				ps.setString(idx++, gson.toJson(stoner.getAppearance()));
				ps.setString(idx++, gson.toJson(stoner.getColors()));
				ps.setInt     (idx++, stoner.getChatEffects());
				ps.setInt     (idx++, stoner.getTransparentPanel());
				ps.setInt     (idx++, stoner.getTransparentChatbox());
				ps.setInt     (idx++, stoner.getSideStones());
				ps.setLong(idx++, 0); // Used to be skulling now FREE
				ps.setInt(idx++, 0); // Used to be skulling now FREE
				ps.setInt(idx++, stoner.getSpecialAssault().getAmount());
				ps.setString(idx++, (stoner.getEquipment().getAssaultStyle() != null ? stoner.getEquipment().getAssaultStyle().name() : null));
				ps.setString(idx++, (stoner.getAssaultType() != null ? stoner.getAssaultType().name() : null));
				ps.setInt(idx++, stoner.getChillPoints());
				ps.setInt(idx++, stoner.getTeleblockTime());
				ps.setInt(idx++, -1); // FREE WAS SUMMONING
				ps.setBoolean(idx++, stoner.getClient().isLogStoner());
				ps.setInt(idx++, stoner.getPestPoints());
				ps.setInt(idx++, stoner.getArenaPoints());
				ps.setInt(idx++, stoner.getMusicVolume());
				ps.setInt(idx++, stoner.getSoundVolume());
				ps.setInt(idx++, stoner.getDeaths());
				ps.setInt(idx++, stoner.getKills());
				ps.setInt(idx++, stoner.getRogueKills());
				ps.setInt(idx++, stoner.getRogueRecord());
				ps.setInt(idx++, stoner.getHunterKills());
				ps.setInt(idx++, stoner.getHunterRecord());
				ps.setInt(idx++, stoner.getBountyPoints());
				ps.setInt(idx++, stoner.getBlackMarks());
				ps.setDouble(idx++, stoner.getRareDropEP().getEp());
				ps.setInt(idx++, stoner.getRareDropEP().getReceived());
				ps.setString(idx++, gson.toJson(stoner.getProfessionGoals()));
				ps.setString(idx++, gson.toJson(stoner.getLastKilledStoners()));
				Map<String, Integer> serializableMap = new HashMap<>();
				for (Map.Entry<AchievementList, Integer> entry : stoner.getStonerAchievements().entrySet()) {
					serializableMap.put(entry.getKey().name(), entry.getValue());
				}
				ps.setString(idx++, gson.toJson(serializableMap));

				ps.setInt(idx++, stoner.getAchievementsPoints());
				ps.setString(idx++, gson.toJson(stoner.getUnlockedCredits()));
				ps.setString(idx++, gson.toJson(stoner.getResonance().getResonanceFromDB()));
				ps.setString(idx++, gson.toJson(stoner.getAttributes().getAttributes())); // stonerProperties
				ps.setDouble(idx++, stoner.getCounterExp());
				ps.setInt(idx++, stoner.getAdvancePoints());
				ps.setString(idx++, gson.toJson(stoner.getProfessionAdvances()));
				ps.setInt(idx++, stoner.getTotalAdvances());
				ps.setString(idx++, gson.toJson(stoner.getToxicBlowpipe()));
				ps.setString(idx++, gson.toJson(stoner.getSeasTrident()));
				ps.setString(idx++, gson.toJson(stoner.getSwampTrident()));
				ps.setString(idx++, gson.toJson(stoner.getSerpentineHelment()));
				ps.setString(idx++, gson.toJson(stoner.unlockedTitles));
				ps.setString(idx++, gson.toJson(stoner.getStonerTitle()));
				ps.executeUpdate();
			}


			// 2. Save containers: inventory, equipment, bank (delete all then batch insert)
			saveItemsBatch(conn, "player_inventory", username, stoner.getBox().getItems());
			saveItemsBatch(conn, "player_equipment", username, stoner.getEquipment().getItems());
			saveBankBatch(conn, "player_bank", username, stoner.getBank().getItems(), stoner.getBank().getTabAmounts());

			conn.commit();

			AntiRollbackManager.markSave(stoner);

		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			System.err.println("[StonerSaveUtil] Failed to save player: " + username);
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Simple check to determine if a password is likely encrypted (Base64 format)
	 */
	private static boolean isPasswordLikelyEncrypted(String password) {
		if (password == null || password.trim().isEmpty()) {
			return false;
		}
		// Check for Base64 characteristics and reasonable length for encrypted data
		return password.matches("^[A-Za-z0-9+/]*={0,2}$") && password.length() > 20;
	}

	private static void saveItemsBatch(Connection conn, String table, String username, Item[] items) throws SQLException {
		String deleteSql = "DELETE FROM " + table + " WHERE username = ?";
		try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
			del.setString(1, username);
			del.executeUpdate();
		}

		String insertSql = "INSERT INTO " + table + " (username, slot, item_id, amount) VALUES (?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
			for (int slot = 0; slot < items.length; slot++) {
				Item item = items[slot];
				ps.setString(1, username);
				ps.setInt(2, slot);
				if (item == null || item.getId() <= 0 || item.getAmount() <= 0) {
					ps.setInt(3, -1); // Sentinel for empty slot
					ps.setInt(4, 0);
				} else {
					ps.setInt(3, item.getId());
					ps.setInt(4, item.getAmount());
				}
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	private static void saveBankBatch(Connection conn, String table, String username, Item[] items, int[] tabAmounts) throws SQLException {
		String deleteSql = "DELETE FROM " + table + " WHERE username = ?";
		try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
			del.setString(1, username);
			del.executeUpdate();
		}

		String insertSql = "INSERT INTO " + table + " (username, slot, item_id, amount, tab_amounts) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
			String tabJson = new Gson().toJson(tabAmounts);

			for (int slot = 0; slot < items.length; slot++) {
				Item item = items[slot];
				ps.setString(1, username);
				ps.setInt(2, slot);

				if (item == null || item.getId() <= 0 || item.getAmount() <= 0) {
					ps.setInt(3, -1);
					ps.setInt(4, 0);
				} else {
					ps.setInt(3, item.getId());
					ps.setInt(4, item.getAmount());
				}

				ps.setString(5, tabJson); // same tab JSON for all rows
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

	public static boolean unJailOfflineStoner(String username) {
		Stoner target = new Stoner();
		target.setUsername(Utility.formatStonerName(username));
		if (!StonerSave.load(target)) return false;

		target.setJailed(false);
		target.getAttributes().set("jailTime", 0);
		StonerSave.save(target);
		return true;
	}

	public static boolean unmuteOfflineStoner(String username) {
		Stoner target = new Stoner();
		target.setUsername(Utility.formatStonerName(username));
		if (!StonerSave.load(target)) return false;

		target.setMuted(false);
		target.setMuteLength(0);
		target.getAttributes().set("muted_ip", false);
		StonerSave.save(target);
		return true;
	}

	public static boolean unbanOfflineStoner(String username) {
		Stoner target = new Stoner();
		target.setUsername(Utility.formatStonerName(username));
		if (!StonerSave.load(target)) return false;

		target.setBanned(false);
		target.getAttributes().set("banned_ip", false);
		StonerSave.save(target);
		return true;
	}

	public static void setReceivedStarter(Stoner stoner) {
		stoner.getAttributes().set("starter_ip_logged", true);
		SaveCache.markDirty(stoner);
	}

}