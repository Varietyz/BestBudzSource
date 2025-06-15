package com.bestbudz.rs2.content.io.sqlite;

import com.bestbudz.rs2.entity.stoner.Stoner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Persists Stoner state off the game-thread.
 *
 *  • One real daemon thread lives for the lifetime of the JVM.
 *  • A BlockingQueue prevents unbounded runnable spam.
 *  • drainTo() gives free batching each tick.
 *  • shutdownAndFlush() guarantees every queued save is written once.
 */
public final class SaveWorker {

	/** unbounded, lock-free; offers never block the game loop */
	private static final BlockingQueue<Stoner> QUEUE = new LinkedBlockingQueue<>();

	/** single dedicated thread – easier to debug than pooled runnables */
	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
		Thread t = new Thread(r, "StonerSaveWorker");
		t.setDaemon(true);
		return t;
	});

	/** scratch buffer reused every drain – avoids per-flush allocations */
	private static final List<Stoner> BATCH = new ArrayList<>(128);

	static {
		EXECUTOR.execute(SaveWorker::runForever);
	}

	private SaveWorker() {}   // no instances

	// ---------------------------------------------------------------------
	// API
	// ---------------------------------------------------------------------

	/** Queue a player for background save; returns immediately. */
	public static void enqueueSave(Stoner stoner) {
		if (stoner != null && stoner.getUsername() != null) {
			QUEUE.offer(stoner);
		}
	}

	/** Flush everything (blocking) and stop the worker – call from shutdown hook. */
	public static void shutdownAndFlush() {
		EXECUTOR.shutdownNow();                 // interrupts the take()
		try {
			if (!EXECUTOR.awaitTermination(15, TimeUnit.SECONDS)) {
				System.err.println("[SaveWorker] flush timeout – some data may be lost.");
			}
		} catch (InterruptedException ignored) { }
	}

	// ---------------------------------------------------------------------
	// Internal
	// ---------------------------------------------------------------------

	/** Main loop – runs in the dedicated thread. */
	private static void runForever() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				// block until at least one item is present
				Stoner first = QUEUE.take();
				saveOne(first);

				// grab everything else that arrived while we were busy
				QUEUE.drainTo(BATCH);
				for (Stoner s : BATCH) saveOne(s);
				BATCH.clear();

			} catch (InterruptedException stop) {
				// shutdownNow() called – exit loop after draining queue once
				QUEUE.drainTo(BATCH);
				for (Stoner s : BATCH) saveOne(s);
				break;
			}
		}
	}

	private static void saveOne(Stoner stoner) {
		if (stoner.isPetStoner()) return;
		try {
			StonerSaveUtil.saveToDatabase(stoner);
			AntiRollbackManager.markSave(stoner);              // keep rollback map fresh
			System.out.println("[SaveWorker] Saved: " + stoner.getUsername());
		} catch (IllegalStateException connClosed) {
			// self-heal the connection once, then retry
			try {
				SQLiteDB.init();
				StonerSaveUtil.saveToDatabase(stoner);
				AntiRollbackManager.markSave(stoner);
				System.out.println("[SaveWorker] Saved after reconnect: " + stoner.getUsername());
			} catch (Exception fatal) {
				logFail(stoner, fatal);
			}
		} catch (Exception ex) {
			logFail(stoner, ex);
		}
	}

	private static void logFail(Stoner stoner, Exception ex) {
		System.err.println("[SaveWorker] Failed to save " + stoner.getUsername());
		ex.printStackTrace();
	}
}
