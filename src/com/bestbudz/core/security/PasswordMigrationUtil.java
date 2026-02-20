package com.bestbudz.core.security;

import com.bestbudz.rs2.content.io.sqlite.SQLiteDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordMigrationUtil {

	private static class PlayerPassword {
		String username;
		String password;
		boolean isEncrypted;

		PlayerPassword(String username, String password, boolean isEncrypted) {
			this.username = username;
			this.password = password;
			this.isEncrypted = isEncrypted;
		}
	}

	public static void migrateAllPasswords() {
		System.out.println("[PasswordMigration] Starting password migration...");

		Connection conn = SQLiteDB.getConnection();
		List<PlayerPassword> playersToUpdate = new ArrayList<>();

		try {

			addEncryptedColumnIfNeeded(conn);

			String selectSql = "SELECT username, password, password_encrypted FROM player WHERE password_encrypted IS NULL OR password_encrypted = 0";
			try (PreparedStatement ps = conn.prepareStatement(selectSql);
				 ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					String username = rs.getString("username");
					String password = rs.getString("password");
					playersToUpdate.add(new PlayerPassword(username, password, false));
				}
			}

			System.out.println("[PasswordMigration] Found " + playersToUpdate.size() + " passwords to encrypt");

			String updateSql = "UPDATE player SET password = ?, password_encrypted = 1 WHERE username = ?";
			try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
				conn.setAutoCommit(false);

				int updateCount = 0;
				for (PlayerPassword player : playersToUpdate) {
					String encryptedPassword = PasswordEncryption.encrypt(player.password);
					if (encryptedPassword != null) {
						updatePs.setString(1, encryptedPassword);
						updatePs.setString(2, player.username);
						updatePs.addBatch();
						updateCount++;

						if (updateCount % 100 == 0) {
							updatePs.executeBatch();
							System.out.println("[PasswordMigration] Encrypted " + updateCount + " passwords so far...");
						}
					} else {
						System.err.println("[PasswordMigration] Failed to encrypt password for user: " + player.username);
					}
				}

				updatePs.executeBatch();
				conn.commit();
				System.out.println("[PasswordMigration] Successfully encrypted " + updateCount + " passwords");

			} catch (SQLException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(true);
			}

		} catch (SQLException e) {
			System.err.println("[PasswordMigration] Migration failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void addEncryptedColumnIfNeeded(Connection conn) throws SQLException {

		DatabaseMetaData metaData = conn.getMetaData();
		try (ResultSet columns = metaData.getColumns(null, null, "player", "password_encrypted")) {
			if (!columns.next()) {

				String alterSql = "ALTER TABLE player ADD COLUMN password_encrypted INTEGER DEFAULT 0";
				try (PreparedStatement ps = conn.prepareStatement(alterSql)) {
					ps.executeUpdate();
					System.out.println("[PasswordMigration] Added password_encrypted column to player table");
				}
			}
		}
	}

	public static boolean isPasswordEncrypted(String password) {
		if (password == null || password.trim().isEmpty()) {
			return false;
		}

		return password.matches("^[A-Za-z0-9+/]*={0,2}$") && password.length() > 20;
	}

	public static void verifyMigration() {
		Connection conn = SQLiteDB.getConnection();
		try {
			String sql = "SELECT COUNT(*) as total, " +
				"SUM(CASE WHEN password_encrypted = 1 THEN 1 ELSE 0 END) as encrypted " +
				"FROM player " +
				"WHERE username != 'BestBud'";
			try (PreparedStatement ps = conn.prepareStatement(sql);
				 ResultSet rs = ps.executeQuery()) {

				if (rs.next()) {
					int total = rs.getInt("total");
					int encrypted = rs.getInt("encrypted");
					System.out.println("[PasswordMigration] Migration Status: " + encrypted + "/" + total + " passwords encrypted");

					if (encrypted == total) {
						System.out.println("[PasswordMigration] ✅ All passwords successfully encrypted!");
					} else {
						System.out.println("[PasswordMigration] ⚠️  " + (total - encrypted) + " passwords still need encryption");
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("[PasswordMigration] Failed to verify migration: " + e.getMessage());
		}
	}
}
