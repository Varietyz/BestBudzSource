package com.bestbudz.core.security;

import com.bestbudz.rs2.content.io.sqlite.SQLiteDB;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerStartupMigration {

	public static void initializePasswordSecurity() {
		System.out.println("[ServerStartup] Initializing password security...");

		if (PasswordEncryption.isUsingDefaultKey()) {
			System.err.println("╔══════════════════════════════════════════════════════════════╗");
			System.err.println("║                    🔐 SECURITY WARNING 🔐                    ║");
			System.err.println("║                                                              ║");
			System.err.println("║  Your server is using the DEFAULT encryption key!           ║");
			System.err.println("║  This is NOT secure for production use.                     ║");
			System.err.println("║                                                              ║");
			System.err.println("║  To fix this:                                                ║");
			System.err.println("║  1. Generate a new key: PasswordEncryption.generateKey()    ║");
			System.err.println("║  2. Set environment variable: BESTBUDZ_ENCRYPTION_KEY       ║");
			System.err.println("║  3. Restart the server                                       ║");
			System.err.println("║                                                              ║");
			System.err.println("╚══════════════════════════════════════════════════════════════╝");
		} else {
			System.out.println("[ServerStartup] ✅ Using custom encryption key");
		}

		if (needsPasswordMigration()) {
			System.out.println("[ServerStartup] Password migration needed");

			if (shouldAutoMigrate()) {
				System.out.println("[ServerStartup] Starting automatic password migration...");
				PasswordMigrationUtil.migrateAllPasswords();
				PasswordMigrationUtil.verifyMigration();
			} else {
				System.out.println("[ServerStartup] ⚠️  Manual migration required!");
				System.out.println("[ServerStartup] Run: PasswordMigrationUtil.migrateAllPasswords()");
			}
		} else {
			System.out.println("[ServerStartup] ✅ Password encryption already configured");
		}
	}

	private static boolean needsPasswordMigration() {
		Connection conn = SQLiteDB.getConnection();
		try {

			DatabaseMetaData metaData = conn.getMetaData();
			try (ResultSet columns = metaData.getColumns(null, null, "player", "password_encrypted")) {
				if (!columns.next()) {
					return true;
				}
			}

			String sql = "SELECT COUNT(*) as count FROM player WHERE password_encrypted IS NULL OR password_encrypted = 0";
			try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
				 java.sql.ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("count") > 0;
				}
			}
		} catch (SQLException e) {
			System.err.println("[ServerStartup] Error checking migration status: " + e.getMessage());
			return true;
		}
		return false;
	}

	private static boolean shouldAutoMigrate() {

		String autoMigrate = System.getProperty("bestbudz.auto.migrate.passwords",
			System.getenv("BESTBUDZ_AUTO_MIGRATE_PASSWORDS"));

		if ("true".equalsIgnoreCase(autoMigrate)) {
			return true;
		}

		return PasswordEncryption.isUsingDefaultKey();
	}

	public static void generateNewEncryptionKey() {
		String newKey = PasswordEncryption.generateKey();
		if (newKey != null) {
			System.out.println("╔══════════════════════════════════════════════════════════════╗");
			System.out.println("║                    🔑 NEW ENCRYPTION KEY 🔑                  ║");
			System.out.println("║                                                              ║");
			System.out.println("║  Set this as your BESTBUDZ_ENCRYPTION_KEY environment var:  ║");
			System.out.println("║                                                              ║");
			System.out.println("║  " + newKey + "  ║");
			System.out.println("║                                                              ║");
			System.out.println("║  ⚠️  IMPORTANT: Save this key securely!                     ║");
			System.out.println("║  ⚠️  You'll need it to decrypt existing passwords!          ║");
			System.out.println("║                                                              ║");
			System.out.println("╚══════════════════════════════════════════════════════════════╝");
		} else {
			System.err.println("[ServerStartup] Failed to generate encryption key!");
		}
	}

}
