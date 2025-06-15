package com.bestbudz.core.security;

import com.bestbudz.rs2.content.io.sqlite.SQLiteDB;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles password encryption migration during server startup
 */
public class ServerStartupMigration {

	/**
	 * Call this method during server startup to ensure password encryption is properly set up
	 */
	public static void initializePasswordSecurity() {
		System.out.println("[ServerStartup] Initializing password security...");

		// Check encryption key status
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

		// Check if migration is needed
		if (needsPasswordMigration()) {
			System.out.println("[ServerStartup] Password migration needed");

			// Ask for confirmation in development/testing
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

	/**
	 * Checks if password migration is needed
	 */
	private static boolean needsPasswordMigration() {
		Connection conn = SQLiteDB.getConnection();
		try {
			// Check if password_encrypted column exists
			DatabaseMetaData metaData = conn.getMetaData();
			try (ResultSet columns = metaData.getColumns(null, null, "player", "password_encrypted")) {
				if (!columns.next()) {
					return true; // Column doesn't exist, migration needed
				}
			}

			// Check if there are any unencrypted passwords
			String sql = "SELECT COUNT(*) as count FROM player WHERE password_encrypted IS NULL OR password_encrypted = 0";
			try (java.sql.PreparedStatement ps = conn.prepareStatement(sql);
				 java.sql.ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("count") > 0;
				}
			}
		} catch (SQLException e) {
			System.err.println("[ServerStartup] Error checking migration status: " + e.getMessage());
			return true; // Assume migration needed if we can't check
		}
		return false;
	}

	/**
	 * Determines if auto-migration should run
	 * In production, you might want to require manual migration for safety
	 */
	private static boolean shouldAutoMigrate() {
		// Check for environment variable or system property
		String autoMigrate = System.getProperty("bestbudz.auto.migrate.passwords",
			System.getenv("BESTBUDZ_AUTO_MIGRATE_PASSWORDS"));

		if ("true".equalsIgnoreCase(autoMigrate)) {
			return true;
		}

		// Default to auto-migrate in development (when using default key)
		// In production with custom key, require manual migration for safety
		return PasswordEncryption.isUsingDefaultKey();
	}

	/**
	 * Utility method to generate a new encryption key
	 * Call this manually to generate a key for production use
	 */
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