package com.bestbudz.rs2.content.io.sqlite;

import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Public entry point for saving a player.
 * Preserves compatibility with legacy StonerSave.save(Stoner) structure.
 */
public final class StonerSave {

	private StonerSave() {} // No instantiation

	/**
	 * Schedules a save operation for the given player.
	 * This is non-blocking and tick-safe.
	 */
	public static void save(Stoner stoner) {
		if (stoner == null || stoner.getUsername() == null) return;

		if (!SaveCache.isDirty(stoner)) return; // 🚫 Skip unnecessary saves

		SaveCache.clear(stoner);                // ✅ Clear dirty after queue
		SaveWorker.enqueueSave(stoner);         // 🧵 Async write
	}

	public static boolean load(Stoner stoner) {
		try {
			boolean result = StonerLoadUtil.loadFromDatabase(stoner);
			System.out.println("[StonerSave.load] For user " + stoner.getUsername() + ": " + result);
			return result;
		} catch (Exception e) {
			System.err.println("[StonerSave] Failed to load " + (stoner != null ? stoner.getUsername() : "null") + ":");
			e.printStackTrace();
			return false; // fallback triggers starter=true
		}
	}


}
