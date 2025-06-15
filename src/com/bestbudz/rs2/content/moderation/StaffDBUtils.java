package com.bestbudz.rs2.content.moderation;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.content.io.sqlite.SQLiteDB;
import com.bestbudz.rs2.content.io.sqlite.StonerSave;
import com.bestbudz.core.util.Utility;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

/**
 * StaffDBUtils - Database modification utilities for staff
 *
 * Handles all database operations for staff commands.
 * Each method reads parameters from stoner attributes.
 */
public class StaffDBUtils {

	/**
	 * Change player password
	 */
	public static boolean changePassword(Stoner stoner) {
		String targetName = DockStaff.getTargetPlayer(stoner);
		String newPassword = (String) stoner.getAttributes().get("staff_new_password");

		if (targetName == null || newPassword == null || newPassword.trim().isEmpty()) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Missing target or new password."));
			return false;
		}

		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET password = ? WHERE username = ? COLLATE NOCASE";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, newPassword.trim());
				ps.setString(2, targetName);

				int rows = ps.executeUpdate();
				if (rows > 0) {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Changed password for " + targetName));
					DockStaff.logAction(stoner, "CHANGE_PASSWORD", targetName, "Password updated");
					return true;
				} else {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Player not found: " + targetName));
					return false;
				}
			}
		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Change player username
	 */
	public static boolean changeUsername(Stoner stoner) {
		String oldName = DockStaff.getTargetPlayer(stoner);
		String newName = (String) stoner.getAttributes().get("staff_new_username");

		if (oldName == null || newName == null || newName.trim().isEmpty()) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Missing old or new username."));
			return false;
		}

		try (Connection conn = SQLiteDB.getConnection()) {
			conn.setAutoCommit(false);

			// Update all tables with foreign key references
			String[] tables = {"player", "player_inventory", "player_equipment", "player_bank"};

			for (String table : tables) {
				String sql = "UPDATE " + table + " SET username = ? WHERE username = ? COLLATE NOCASE";
				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					ps.setString(1, newName.trim());
					ps.setString(2, oldName);
					ps.executeUpdate();
				}
			}

			conn.commit();
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Renamed " + oldName + " to " + newName));
			DockStaff.logAction(stoner, "CHANGE_USERNAME", oldName + " -> " + newName, "Username changed");
			return true;

		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Change player rights level
	 */
	public static boolean changeRights(Stoner stoner) {
		String targetName = DockStaff.getTargetPlayer(stoner);
		Integer newRights = (Integer) stoner.getAttributes().get("staff_new_rights");

		if (targetName == null || newRights == null) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Missing target or rights level."));
			return false;
		}

		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET rights = ? WHERE username = ? COLLATE NOCASE";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, newRights);
				ps.setString(2, targetName);

				int rows = ps.executeUpdate();
				if (rows > 0) {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Set " + targetName + " rights to " + newRights));
					DockStaff.logAction(stoner, "CHANGE_RIGHTS", targetName, "Rights: " + newRights);
					return true;
				} else {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Player not found: " + targetName));
					return false;
				}
			}
		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Change player location
	 */
	public static boolean changeLocation(Stoner stoner) {
		String targetName = DockStaff.getTargetPlayer(stoner);
		Integer x = (Integer) stoner.getAttributes().get("staff_new_x");
		Integer y = (Integer) stoner.getAttributes().get("staff_new_y");
		Integer z = (Integer) stoner.getAttributes().get("staff_new_z");

		if (targetName == null || x == null || y == null || z == null) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Missing target or coordinates (x,y,z)."));
			return false;
		}

		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET x = ?, y = ?, z = ? WHERE username = ? COLLATE NOCASE";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, x);
				ps.setInt(2, y);
				ps.setInt(3, z);
				ps.setString(4, targetName);

				int rows = ps.executeUpdate();
				if (rows > 0) {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Moved " + targetName + " to " + x + "," + y + "," + z));
					DockStaff.logAction(stoner, "CHANGE_LOCATION", targetName, x + "," + y + "," + z);
					return true;
				} else {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Player not found: " + targetName));
					return false;
				}
			}
		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Change player money pouch
	 */
	public static boolean changeMoney(Stoner stoner) {
		String targetName = DockStaff.getTargetPlayer(stoner);
		Long newMoney = (Long) stoner.getAttributes().get("staff_new_money");

		if (targetName == null || newMoney == null) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Missing target or money amount."));
			return false;
		}

		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET moneyPouch = ? WHERE username = ? COLLATE NOCASE";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setLong(1, newMoney);
				ps.setString(2, targetName);

				int rows = ps.executeUpdate();
				if (rows > 0) {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Set " + targetName + " money to " + Utility.format(newMoney)));
					DockStaff.logAction(stoner, "CHANGE_MONEY", targetName, "Money: " + Utility.format(newMoney));
					return true;
				} else {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Player not found: " + targetName));
					return false;
				}
			}
		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Change player credits
	 */
	public static boolean changeCredits(Stoner stoner) {
		String targetName = DockStaff.getTargetPlayer(stoner);
		Integer newCredits = (Integer) stoner.getAttributes().get("staff_new_credits");

		if (targetName == null || newCredits == null) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Missing target or credits amount."));
			return false;
		}

		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET credits = ? WHERE username = ? COLLATE NOCASE";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, newCredits);
				ps.setString(2, targetName);

				int rows = ps.executeUpdate();
				if (rows > 0) {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Set " + targetName + " credits to " + newCredits));
					DockStaff.logAction(stoner, "CHANGE_CREDITS", targetName, "Credits: " + newCredits);
					return true;
				} else {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Player not found: " + targetName));
					return false;
				}
			}
		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Change player kills
	 */
	public static boolean changeKills(Stoner stoner) {
		String targetName = DockStaff.getTargetPlayer(stoner);
		Integer newKills = (Integer) stoner.getAttributes().get("staff_new_kills");

		if (targetName == null || newKills == null) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Missing target or kills amount."));
			return false;
		}

		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET kills = ? WHERE username = ? COLLATE NOCASE";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, newKills);
				ps.setString(2, targetName);

				int rows = ps.executeUpdate();
				if (rows > 0) {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Set " + targetName + " kills to " + newKills));
					DockStaff.logAction(stoner, "CHANGE_KILLS", targetName, "Kills: " + newKills);
					return true;
				} else {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Player not found: " + targetName));
					return false;
				}
			}
		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Change player deaths
	 */
	public static boolean changeDeaths(Stoner stoner) {
		String targetName = DockStaff.getTargetPlayer(stoner);
		Integer newDeaths = (Integer) stoner.getAttributes().get("staff_new_deaths");

		if (targetName == null || newDeaths == null) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Missing target or deaths amount."));
			return false;
		}

		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET deaths = ? WHERE username = ? COLLATE NOCASE";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, newDeaths);
				ps.setString(2, targetName);

				int rows = ps.executeUpdate();
				if (rows > 0) {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Set " + targetName + " deaths to " + newDeaths));
					DockStaff.logAction(stoner, "CHANGE_DEATHS", targetName, "Deaths: " + newDeaths);
					return true;
				} else {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Player not found: " + targetName));
					return false;
				}
			}
		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Change player host/IP
	 */
	public static boolean changeHost(Stoner stoner) {
		String targetName = DockStaff.getTargetPlayer(stoner);
		String newHost = (String) stoner.getAttributes().get("staff_new_host");

		if (targetName == null || newHost == null || newHost.trim().isEmpty()) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Missing target or host."));
			return false;
		}

		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET host = ? WHERE username = ? COLLATE NOCASE";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, newHost.trim());
				ps.setString(2, targetName);

				int rows = ps.executeUpdate();
				if (rows > 0) {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Set " + targetName + " host to " + newHost));
					DockStaff.logAction(stoner, "CHANGE_HOST", targetName, "Host: " + newHost);
					return true;
				} else {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Player not found: " + targetName));
					return false;
				}
			}
		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Reset player stats (kills, deaths, experience, etc.)
	 */
	public static boolean resetStats(Stoner stoner) {
		String targetName = DockStaff.getTargetPlayer(stoner);
		if (targetName == null) return false;

		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET kills = 0, deaths = 0, rogueKills = 0, rogueRecord = 0, " +
				"hunterKills = 0, hunterRecord = 0, bountyPoints = 0, blackMarks = 0, " +
				"rareDropEP = 0.0, rareDropsReceived = 0 WHERE username = ? COLLATE NOCASE";

			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, targetName);

				int rows = ps.executeUpdate();
				if (rows > 0) {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Reset stats for " + targetName));
					DockStaff.logAction(stoner, "RESET_STATS", targetName, "All stats reset to 0");
					return true;
				} else {
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Player not found: " + targetName));
					return false;
				}
			}
		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Search for players in database
	 */
	public static boolean searchPlayer(Stoner stoner) {
		String searchTerm = (String) stoner.getAttributes().get("staff_search_term");
		if (searchTerm == null || searchTerm.trim().isEmpty()) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] No search term provided."));
			return false;
		}

		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "SELECT username, rights, banned, muted, jailed, host, moneyPouch " +
				"FROM player WHERE username LIKE ? OR host LIKE ? " +
				"ORDER BY username LIMIT 20";

			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				String pattern = "%" + searchTerm.trim() + "%";
				ps.setString(1, pattern);
				ps.setString(2, pattern);

				try (ResultSet rs = ps.executeQuery()) {
					int count = 0;
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Search results for: " + searchTerm));

					while (rs.next()) {
						count++;
						String username = rs.getString("username");
						int rights = rs.getInt("rights");
						boolean banned = rs.getBoolean("banned");
						boolean muted = rs.getBoolean("muted");
						boolean jailed = rs.getBoolean("jailed");
						String host = rs.getString("host");
						long money = rs.getLong("moneyPouch");

						StringBuilder status = new StringBuilder();
						if (banned) status.append("BANNED ");
						if (muted) status.append("MUTED ");
						if (jailed) status.append("JAILED ");
						if (status.length() == 0) status.append("Clean");

						stoner.send(new SendMessage("[ <col=255>DB</col> ] " + count + ". " + username +
							" (Rights:" + rights + ", Money:" + Utility.format(money) + ", Status:" + status.toString().trim() + ")"));
					}

					if (count == 0) {
						stoner.send(new SendMessage("[ <col=255>DB</col> ] No players found."));
					} else {
						stoner.send(new SendMessage("[ <col=255>DB</col> ] Found " + count + " player(s)."));
					}

					return true;
				}
			}
		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Search error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * View detailed player information
	 */
	public static boolean viewPlayerInfo(Stoner stoner) {
		String targetName = DockStaff.getTargetPlayer(stoner);
		if (targetName == null) return false;

		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "SELECT * FROM player WHERE username = ? COLLATE NOCASE";

			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, targetName);

				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next()) {
						stoner.send(new SendMessage("[ <col=255>DB</col> ] Player not found: " + targetName));
						return false;
					}

					// Display key information
					stoner.send(new SendMessage("[ <col=255>DB</col> ] === Player Info: " + rs.getString("username") + " ==="));
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Rights: " + rs.getInt("rights")));
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Location: " + rs.getInt("x") + "," + rs.getInt("y") + "," + rs.getInt("z")));
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Money: " + Utility.format(rs.getLong("moneyPouch"))));
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Credits: " + rs.getInt("credits")));
					stoner.send(new SendMessage("[ <col=255>DB</col> ] K/D: " + rs.getInt("kills") + "/" + rs.getInt("deaths")));
					stoner.send(new SendMessage("[ <col=255>DB</col> ] Host: " + rs.getString("host")));

					// Status information
					boolean banned = rs.getBoolean("banned");
					boolean muted = rs.getBoolean("muted");
					boolean jailed = rs.getBoolean("jailed");

					if (banned || muted || jailed) {
						stoner.send(new SendMessage("[ <col=255>DB</col> ] === Punishments ==="));
						if (banned) stoner.send(new SendMessage("[ <col=255>DB</col> ] BANNED (expires: " +
							new java.util.Date(rs.getLong("banLength")) + ")"));
						if (muted) stoner.send(new SendMessage("[ <col=255>DB</col> ] MUTED (expires: " +
							new java.util.Date(rs.getLong("muteLength")) + ")"));
						if (jailed) stoner.send(new SendMessage("[ <col=255>DB</col> ] JAILED (expires: " +
							new java.util.Date(rs.getLong("jailLength")) + ")"));
					} else {
						stoner.send(new SendMessage("[ <col=255>DB</col> ] Status: Clean"));
					}

					return true;
				}
			}
		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Info error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Get database statistics
	 */
	public static boolean getDatabaseStats(Stoner stoner) {
		try (Connection conn = SQLiteDB.getConnection()) {
			StringBuilder stats = new StringBuilder("[ <col=255>DB</col> ] Database Stats: ");

			// Total players
			try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM player")) {
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) stats.append("Total: ").append(rs.getInt(1)).append(" | ");
				}
			}

			// Banned players
			try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM player WHERE banned = 1")) {
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) stats.append("Banned: ").append(rs.getInt(1)).append(" | ");
				}
			}

			// Muted players
			try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM player WHERE muted = 1")) {
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) stats.append("Muted: ").append(rs.getInt(1)).append(" | ");
				}
			}

			// Jailed players
			try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM player WHERE jailed = 1")) {
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) stats.append("Jailed: ").append(rs.getInt(1));
				}
			}

			stoner.send(new SendMessage(stats.toString()));
			return true;

		} catch (SQLException e) {
			stoner.send(new SendMessage("[ <col=255>DB</col> ] Stats error: " + e.getMessage()));
			return false;
		}
	}

	/**
	 * Ban offline player via database
	 */
	public static boolean banOfflinePlayer(String targetName, String reason) {
		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET banned = 1, banLength = ? WHERE username = ? COLLATE NOCASE";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setLong(1, System.currentTimeMillis() + (24 * 60 * 60 * 1000L)); // 24 hours
				ps.setString(2, targetName);

				return ps.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			System.err.println("Offline ban error: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Unban offline player via database
	 */
	public static boolean unbanOfflinePlayer(String targetName) {
		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET banned = 0, banLength = 0 WHERE username = ? COLLATE NOCASE";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setString(1, targetName);
				return ps.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			System.err.println("Offline unban error: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Generic field update method for future expansion
	 */
	public static boolean updatePlayerField(String targetName, String fieldName, Object value) {
		try (Connection conn = SQLiteDB.getConnection()) {
			String sql = "UPDATE player SET " + fieldName + " = ? WHERE username = ? COLLATE NOCASE";
			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setObject(1, value);
				ps.setString(2, targetName);
				return ps.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			System.err.println("Field update error: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Backup player data before major changes
	 */
	public static boolean backupPlayerData(String targetName) {
		// Could implement backup to separate table if needed
		System.out.println("Backup requested for player: " + targetName);
		return true;
	}
}