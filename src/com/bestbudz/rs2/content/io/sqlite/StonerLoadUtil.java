package com.bestbudz.rs2.content.io.sqlite;

import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.content.StonerTitle;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSeas;
import com.bestbudz.rs2.content.profession.mage.weapons.TridentOfTheSwamp;
import com.bestbudz.rs2.content.profession.melee.SerpentineHelmet;
import com.bestbudz.rs2.content.profession.sagittarius.ToxicBlowpipe;
import com.bestbudz.rs2.entity.Location;
import com.google.gson.Gson;
import java.sql.*;
import java.util.Map;

public final class StonerLoadUtil {

	private static final Gson gson = new Gson();

	private StonerLoadUtil() {}

	public static boolean loadFromDatabase(Stoner stoner) throws SQLException {
		final String username = stoner.getUsername();
		Connection conn = SQLiteDB.getConnection();

		String sql = "SELECT * FROM player WHERE username = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					System.out.println("[DB] No player found for '" + username + "'");
					return false; // player not found!
				}
				System.out.println("[DB] Loaded player '" + username + "'");
				int idx = 1;
				stoner.setUsername(rs.getString(idx++));
				stoner.setPassword(rs.getString(idx++));
				int x = rs.getInt(idx++);
				int y = rs.getInt(idx++);
				int z = rs.getInt(idx++);
				stoner.getLocation().setAs(new Location(x, y, z));
				stoner.setRights(rs.getInt(idx++));
				stoner.setUid(rs.getString(idx++));
				stoner.setPin(rs.getString(idx++));
				stoner.setCredits(rs.getInt(idx++));
				stoner.getClient().setHost(rs.getString(idx++));
				stoner.setYellTitle(rs.getString(idx++));
				stoner.setBanned(rs.getBoolean(idx++));
				stoner.setBanLength(rs.getLong(idx++));
				stoner.setMoneyPouch(rs.getLong(idx++));
				stoner.setJailLength(rs.getLong(idx++));
				stoner.setShopCollection(rs.getLong(idx++));
				stoner.lastClanChat = rs.getString(idx++);
				stoner.setMuted(rs.getBoolean(idx++));
				stoner.setMember(rs.getBoolean(idx++));
				stoner.setJailed(rs.getBoolean(idx++));
				stoner.setMuteLength(rs.getLong(idx++));
				stoner.setWeaponPoints(rs.getInt(idx++));
				stoner.getJadDetails().setStage(rs.getInt(idx++));
				stoner.getMage().setMageBook(rs.getInt(idx++));
				stoner.setNecromanceInterface(rs.getInt(idx++));
				stoner.setRetaliate(rs.getBoolean(idx++));
				stoner.getProfession().setExpLock(rs.getBoolean(idx++));
				stoner.getMinigames().setGWKC(gson.fromJson(rs.getString(idx++), short[].class));
				boolean isPoisoned = rs.getBoolean(idx++);
				stoner.setPouchPayment(rs.getBoolean(idx++));
				int poisonDmg = rs.getInt(idx++);
				if (isPoisoned && poisonDmg > 0) {
					stoner.poison(poisonDmg);
				}
				stoner.setPoisonDamage(poisonDmg); // ✅ use existing read
				stoner.getMercenary().setTask(rs.getString(idx++));
				stoner.getMercenary().setAmount(rs.getByte(idx++));
				String mercDiff = rs.getString(idx++);
				stoner.getMercenary().setCurrent(mercDiff != null ? com.bestbudz.rs2.content.profession.mercenary.Mercenary.MercenaryDifficulty.valueOf(mercDiff) : null);
				long[] loadedGrades = gson.fromJson(rs.getString(idx++), long[].class);
				long[] grades = stoner.getGrades();
				if (loadedGrades != null && grades != null) {
					System.arraycopy(loadedGrades, 0, grades, 0, Math.min(loadedGrades.length, grades.length));
				}
				stoner.getProfession().setExperience(gson.fromJson(rs.getString(idx++), double[].class));
				stoner.setGender(rs.getByte(idx++));
				stoner.setAppearance(gson.fromJson(rs.getString(idx++), int[].class));
				stoner.setColors(gson.fromJson(rs.getString(idx++), byte[].class));
				stoner.setChatEffects(rs.getInt(idx++));
				stoner.setTransparentPanel((byte) rs.getInt(idx++));
				stoner.setTransparentChatbox((byte) rs.getInt(idx++));
				stoner.setSideStones((byte) rs.getInt(idx++));
				stoner.getSkulling().setLeft(rs.getLong(idx++));
				stoner.getSkulling().setSkullIcon(stoner, rs.getInt(idx++));
				stoner.getSpecialAssault().setSpecialAmount(rs.getInt(idx++));
				String assaultStyle = rs.getString(idx++);
				if (assaultStyle != null) stoner.getEquipment().setAssaultStyle(com.bestbudz.rs2.entity.item.Equipment.AssaultStyles.valueOf(assaultStyle));
				String assaultType = rs.getString(idx++);
				if (assaultType != null) stoner.setAssaultType(com.bestbudz.rs2.entity.Entity.AssaultType.valueOf(assaultType));
				stoner.setChillPoints(rs.getInt(idx++));
				stoner.setTeleblockTime(rs.getInt(idx++));
				int familiarData = rs.getInt(idx++);
				if (familiarData != -1) stoner.getAttributes().set("summoningfamsave", familiarData);
				stoner.getClient().setLogStoner(rs.getBoolean(idx++));
				stoner.setPestPoints(rs.getInt(idx++));
				stoner.setArenaPoints(rs.getInt(idx++));
				stoner.setMusicVolume((byte) rs.getInt(idx++));
				stoner.setSoundVolume((byte) rs.getInt(idx++));
				stoner.setDeaths(rs.getInt(idx++));
				stoner.setKills(rs.getInt(idx++));
				stoner.setRogueKills(rs.getInt(idx++));
				stoner.setRogueRecord(rs.getInt(idx++));
				stoner.setHunterKills(rs.getInt(idx++));
				stoner.setHunterRecord(rs.getInt(idx++));
				stoner.setBountyPoints(rs.getInt(idx++));
				stoner.setBlackMarks(rs.getInt(idx++));
				stoner.getRareDropEP().setEp(rs.getDouble(idx++));
				stoner.getRareDropEP().setReceived(rs.getInt(idx++));
				stoner.setProfessionGoals(gson.fromJson(rs.getString(idx++), int[][].class));
				stoner.setLastKilledStoners(gson.fromJson(rs.getString(idx++), java.util.ArrayList.class));
				Map<?, ?> rawMap = gson.fromJson(rs.getString(idx++), java.util.HashMap.class);
				if (rawMap != null) {
					for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
						Object key = entry.getKey();
						Object value = entry.getValue();

						if (key instanceof String && value instanceof Number) {
							try {
								AchievementList enumKey = AchievementList.valueOf((String) key);
								stoner.getStonerAchievements().put(enumKey, ((Number) value).intValue());
							} catch (IllegalArgumentException ignored) {
								// Ignore unknown enum entries
							}
						}
					}
				}

				stoner.addAchievementPoints(rs.getInt(idx++));
				stoner.getUnlockedCredits().addAll(gson.fromJson(rs.getString(idx++), java.util.HashSet.class));
				stoner.getNecromance().setQuickNecromances(gson.fromJson(rs.getString(idx++), boolean[].class));
				java.util.HashMap<?,?> properties = gson.fromJson(rs.getString(idx++), java.util.HashMap.class);
				if (properties != null) stoner.getAttributes().getAttributes().putAll(properties);
				stoner.addCounterExp(rs.getDouble(idx++));
				stoner.setAdvancePoints(rs.getInt(idx++));
				stoner.setProfessionAdvances(gson.fromJson(rs.getString(idx++), int[].class));

				stoner.setTotalAdvances(rs.getInt(idx++));
				stoner.setToxicBlowpipe(gson.fromJson(rs.getString(idx++), ToxicBlowpipe.class));
				stoner.setSeasTrident(gson.fromJson(rs.getString(idx++), TridentOfTheSeas.class));
				stoner.setSwampTrident(gson.fromJson(rs.getString(idx++), TridentOfTheSwamp.class));
				stoner.setSerpentineHelment(gson.fromJson(rs.getString(idx++), SerpentineHelmet.class));
				stoner.unlockedTitles = gson.fromJson(rs.getString(idx++), java.util.ArrayList.class);
				stoner.setStonerTitle(gson.fromJson(rs.getString(idx++), StonerTitle.class));

			}
		}

		// === Load containers as before ===
		loadItems(conn, SaveConstants.TABLE_INVENTORY, username, stoner.getBox().getItems(), stoner);
		loadItems(conn, SaveConstants.TABLE_EQUIPMENT, username, stoner.getEquipment().getItems(), stoner);
		loadItems(conn, SaveConstants.TABLE_BANK, username, stoner.getBank().getItems(), stoner);

		return true;
	}

	private static void loadItems(Connection conn, String table, String username, Item[] items, Stoner stoner)
	throws SQLException {
		String sql = "SELECT slot, item_id, amount FROM " + table + " WHERE username = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int slot = rs.getInt("slot");
					int id = rs.getInt("item_id");
					int amount = rs.getInt("amount");
					if (table.equals("player_bank")) {
						PreparedStatement tabStmt = conn.prepareStatement("SELECT tab_amounts FROM player_bank WHERE username = ? LIMIT 1");
						tabStmt.setString(1, username);
						ResultSet tabRs = tabStmt.executeQuery();
						if (tabRs.next()) {
							String tabJson = tabRs.getString("tab_amounts");
							int[] tabAmounts = new Gson().fromJson(tabJson, int[].class);
							stoner.getBank().setTabAmounts(tabAmounts);
						}
						tabRs.close();
						tabStmt.close();
					}

					if (slot >= 0 && slot < items.length) {
						items[slot] = (id > 0 && amount > 0) ? new Item(id, amount) : null;
					}
				}
			}
		}
	}
}
