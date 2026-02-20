package com.bestbudz.rs2.content.io.sqlite;

import com.bestbudz.rs2.entity.stoner.Stoner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public final class SaveWorker {

	private static final BlockingQueue<Stoner> QUEUE = new LinkedBlockingQueue<>();

	private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
		Thread t = new Thread(r, "StonerSaveWorker");
		t.setDaemon(true);
		return t;
	});

	private static final List<Stoner> BATCH = new ArrayList<>(128);

	static {
		EXECUTOR.execute(SaveWorker::runForever);
	}

	private SaveWorker() {}

	public static void enqueueSave(Stoner stoner) {
		if (stoner != null && stoner.getUsername() != null) {
			QUEUE.offer(stoner);
		}
	}

	public static void shutdownAndFlush() {
		EXECUTOR.shutdownNow();
		try {
			if (!EXECUTOR.awaitTermination(15, TimeUnit.SECONDS)) {
				System.err.println("[SaveWorker] flush timeout – some data may be lost.");
			}
		} catch (InterruptedException ignored) { }
	}

	private static void runForever() {
		while (!Thread.currentThread().isInterrupted()) {
			try {

				Stoner first = QUEUE.take();
				saveOne(first);

				QUEUE.drainTo(BATCH);
				for (Stoner s : BATCH) saveOne(s);
				BATCH.clear();

			} catch (InterruptedException stop) {

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
			AntiRollbackManager.markSave(stoner);
			System.out.println("[SaveWorker] Saved: " + stoner.getUsername());
		} catch (IllegalStateException connClosed) {

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
