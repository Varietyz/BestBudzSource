package com.bestbudz.rs2.content.profession.petmaster.db;

import com.bestbudz.rs2.content.profession.petmaster.bond.PetBond;
import com.bestbudz.rs2.entity.pets.PetData;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.ArrayList;

/**
 * MINIMAL OPTIMAL SQLite for Java 17 + Game Server
 *
 * Key optimizations:
 * - Single connection with WAL mode for maximum concurrency
 * - Async write queue to never block game thread
 * - Minimal prepared statements (only what's needed)
 * - Batch processing for writes
 * - Simple, fast, reliable
 */
public class PetMasterDatabase {

	private static final String DB_URL = "jdbc:sqlite:data/petmaster.db";
	private static PetMasterDatabase instance;
	private static boolean initialized = false;

	// SINGLE connection with WAL mode - this is optimal for SQLite
	private Connection connection;

	// Minimal prepared statements - only the essentials
	private PreparedStatement selectBond;
	private PreparedStatement upsertBond;
	private PreparedStatement selectStoner;
	private PreparedStatement insertStoner;

	// Simple async write queue
	private final ConcurrentLinkedQueue<WriteOperation> writeQueue = new ConcurrentLinkedQueue<>();
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(r -> {
		Thread t = new Thread(r, "PetMaster-DB");
		t.setDaemon(true);
		return t;
	});
	private final AtomicBoolean processingWrites = new AtomicBoolean(false);

	private PetMasterDatabase() {
		try {
			initializeDatabase();
			startAsyncProcessor();
			System.out.println("PetMaster database initialized (minimal optimal)");
		} catch (SQLException e) {
			System.err.println("Failed to initialize PetMaster database: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static synchronized PetMasterDatabase getInstance() {
		if (instance == null) {
			instance = new PetMasterDatabase();
		}
		return instance;
	}

	/**
	 * Initialize with OPTIMAL SQLite settings for Java 17
	 */
	private void initializeDatabase() throws SQLException {
		// Ensure directory exists
		java.io.File dbFile = new java.io.File("data/petmaster.db");
		dbFile.getParentFile().mkdirs();

		// Create single optimized connection
		connection = DriverManager.getConnection(DB_URL);

		// Apply OPTIMAL settings from research
		try (Statement stmt = connection.createStatement()) {
			// WAL mode - allows concurrent reads during writes
			stmt.execute("PRAGMA journal_mode = WAL");

			// NORMAL sync - safe + fast (recommended for WAL)
			stmt.execute("PRAGMA synchronous = NORMAL");

			// Memory for temporary storage
			stmt.execute("PRAGMA temp_store = MEMORY");

			// Large cache (100MB) for better performance
			stmt.execute("PRAGMA cache_size = -102400");

			// Memory mapping for faster I/O
			stmt.execute("PRAGMA mmap_size = 268435456");

			// Longer timeout for high concurrency
			stmt.execute("PRAGMA busy_timeout = 30000");

			// Foreign keys
			stmt.execute("PRAGMA foreign_keys = ON");
		}

		// Create simple, optimized tables
		createTables();

		// Prepare ONLY the statements we actually need
		prepareStatements();

		initialized = true;
	}

	/**
	 * Simple, optimal table design
	 */
	private void createTables() throws SQLException {
		try (Statement stmt = connection.createStatement()) {
			// Minimal stoners table
			stmt.execute("""
                CREATE TABLE IF NOT EXISTS stoners (
                    stoner_id INTEGER PRIMARY KEY,
                    username TEXT UNIQUE NOT NULL COLLATE NOCASE
                )
            """);

			// Minimal pet_bonds table with optimal indexing
			stmt.execute("""
                CREATE TABLE IF NOT EXISTS pet_bonds (
                    stoner_id INTEGER NOT NULL,
                    pet_data_id INTEGER NOT NULL,
                    bond_grade INTEGER DEFAULT 1,
                    bond_experience REAL DEFAULT 0.0,
                    first_summoned INTEGER DEFAULT 0,
                    active_time INTEGER DEFAULT 0,
                    PRIMARY KEY (stoner_id, pet_data_id),
                    FOREIGN KEY (stoner_id) REFERENCES stoners (stoner_id)
                )
            """);

			// Essential indexes only
			stmt.execute("CREATE INDEX IF NOT EXISTS idx_stoners_username ON stoners(username)");
		}
	}

	/**
	 * Prepare ONLY the statements we actually use
	 */
	private void prepareStatements() throws SQLException {
		selectStoner = connection.prepareStatement(
			"SELECT stoner_id FROM stoners WHERE username = ?"
		);

		insertStoner = connection.prepareStatement(
			"INSERT INTO stoners (username) VALUES (?)",
			Statement.RETURN_GENERATED_KEYS
		);

		selectBond = connection.prepareStatement(
			"SELECT bond_grade, bond_experience, first_summoned, active_time " +
				"FROM pet_bonds WHERE stoner_id = ? AND pet_data_id = ?"
		);

		upsertBond = connection.prepareStatement(
			"INSERT INTO pet_bonds (stoner_id, pet_data_id, bond_grade, bond_experience, first_summoned, active_time) " +
				"VALUES (?, ?, ?, ?, ?, ?) " +
				"ON CONFLICT(stoner_id, pet_data_id) DO UPDATE SET " +
				"bond_grade = excluded.bond_grade, " +
				"bond_experience = excluded.bond_experience, " +
				"active_time = excluded.active_time"
		);
	}

	/**
	 * Get stoner ID - fast and simple
	 */
	public synchronized int getStonerId(String username) {
		try {
			// Try to find existing
			selectStoner.setString(1, username.toLowerCase());
			try (ResultSet rs = selectStoner.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("stoner_id");
				}
			}

			// Create new
			insertStoner.setString(1, username.toLowerCase());
			insertStoner.executeUpdate();

			try (ResultSet keys = insertStoner.getGeneratedKeys()) {
				if (keys.next()) {
					return keys.getInt(1);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error getting stoner ID: " + e.getMessage());
		}
		return -1;
	}

	/**
	 * Load pet bond - fast read
	 */
	public synchronized PetBond loadPetBond(int stonerId, PetData petData) {
		try {
			selectBond.setInt(1, stonerId);
			selectBond.setInt(2, petData.ordinal());

			try (ResultSet rs = selectBond.executeQuery()) {
				if (rs.next()) {
					PetBond bond = new PetBond();
					bond.setBondGrade(rs.getInt("bond_grade"));
					bond.setExperience(rs.getDouble("bond_experience"));
					bond.setFirstSummoned(rs.getLong("first_summoned"));
					bond.setActiveTime(rs.getLong("active_time"));
					return bond;
				}
			}
		} catch (SQLException e) {
			System.err.println("Error loading pet bond: " + e.getMessage());
		}
		return new PetBond();
	}

	/**
	 * Save pet bond - async, non-blocking
	 */
	public void savePetBond(int stonerId, PetData petData, PetBond bond) {
		writeQueue.offer(new SaveBondOperation(stonerId, petData, bond));
		tryProcessWrites();
	}

	/**
	 * Batch save - async, non-blocking
	 */
	public void batchSave(int stonerId, Map<PetData, PetBond> bonds, Map<?, ?> stats) {
		for (Map.Entry<PetData, PetBond> entry : bonds.entrySet()) {
			writeQueue.offer(new SaveBondOperation(stonerId, entry.getKey(), entry.getValue()));
		}
		tryProcessWrites();
	}

	/**
	 * Simple async write processor
	 */
	private void tryProcessWrites() {
		if (processingWrites.compareAndSet(false, true)) {
			executor.submit(this::processWrites);
		}
	}

	/**
	 * Process writes in batches - runs on background thread
	 */
	private void processWrites() {
		try {
			List<WriteOperation> batch = new ArrayList<>();

			// Collect up to 50 operations for batching
			WriteOperation op;
			while ((op = writeQueue.poll()) != null && batch.size() < 50) {
				batch.add(op);
			}

			if (!batch.isEmpty()) {
				executeBatch(batch);
			}

		} finally {
			processingWrites.set(false);

			// Check if more writes came in while we were processing
			if (!writeQueue.isEmpty()) {
				tryProcessWrites();
			}
		}
	}

	/**
	 * Execute batch with transaction
	 */
	private synchronized void executeBatch(List<WriteOperation> operations) {
		try {
			connection.setAutoCommit(false);

			for (WriteOperation op : operations) {
				if (op instanceof SaveBondOperation) {
					SaveBondOperation save = (SaveBondOperation) op;

					upsertBond.setInt(1, save.stonerId);
					upsertBond.setInt(2, save.petData.ordinal());
					upsertBond.setInt(3, save.bond.getBondGrade());
					upsertBond.setDouble(4, save.bond.getExperience());
					upsertBond.setLong(5, save.bond.getFirstSummoned());
					upsertBond.setLong(6, save.bond.getActiveTime());
					upsertBond.addBatch();
				}
			}

			upsertBond.executeBatch();
			connection.commit();

		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException rollbackEx) {
				e.addSuppressed(rollbackEx);
			}
			System.err.println("Batch save failed: " + e.getMessage());
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				System.err.println("Error resetting auto-commit: " + e.getMessage());
			}
		}
	}

	/**
	 * Start background processor
	 */
	private void startAsyncProcessor() {
		// Process writes every 5 seconds if there are any
		executor.scheduleWithFixedDelay(() -> {
			if (!writeQueue.isEmpty()) {
				tryProcessWrites();
			}
		}, 5, 5, TimeUnit.SECONDS);

		// Run PRAGMA optimize every hour for query plan optimization
		executor.scheduleWithFixedDelay(() -> {
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("PRAGMA optimize");
			} catch (SQLException e) {
				System.err.println("Optimize failed: " + e.getMessage());
			}
		}, 1, 1, TimeUnit.HOURS);
	}

	/**
	 * Compatibility methods for existing code
	 */
	public boolean logGrowth(int stonerId, PetData fromPet, PetData toPet, int bondGrade, int stonerGrade) {
		// Optional: implement if needed, or remove if not used
		return true;
	}

	public void logActivity(int stonerId, PetData petData, String activityType, String activityData, double expGained) {
		// Optional: implement if needed, or remove if not used
	}

	public Map<String, Object> getStonerStats(int stonerId) {
		// Optional: implement if needed, or return empty map
		return new HashMap<>();
	}

	/**
	 * Graceful shutdown
	 */
	public void shutdown() {
		try {
			executor.shutdown();
			if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}

			if (connection != null && !connection.isClosed()) {
				connection.close();
			}

			System.out.println("PetMaster database shutdown complete");
		} catch (Exception e) {
			System.err.println("Error during shutdown: " + e.getMessage());
		}
	}

	// Simple operation classes
	private abstract static class WriteOperation {}

	private static class SaveBondOperation extends WriteOperation {
		final int stonerId;
		final PetData petData;
		final PetBond bond;

		SaveBondOperation(int stonerId, PetData petData, PetBond bond) {
			this.stonerId = stonerId;
			this.petData = petData;
			this.bond = bond;
		}
	}
}
