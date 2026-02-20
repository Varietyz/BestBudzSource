package com.bestbudz.rs2.content.io.sqlite;

import com.bestbudz.rs2.entity.stoner.Stoner;

public final class StonerSave {

	private StonerSave() {}

	public static void save(Stoner stoner) {
		if (stoner == null || stoner.getUsername() == null) return;

		if (!SaveCache.isDirty(stoner)) return;

		if (stoner.getProfessions() != null) {
			stoner.getProfessions().save();
		}

		SaveCache.clear(stoner);
		SaveWorker.enqueueSave(stoner);
	}

	public static boolean load(Stoner stoner) {
		try {
			boolean result = StonerLoadUtil.loadFromDatabase(stoner);
			System.out.println("[StonerSave.load] For user " + stoner.getUsername() + ": " + result);
			return result;
		} catch (Exception e) {
			System.err.println("[StonerSave] Failed to load " + (stoner != null ? stoner.getUsername() : "null") + ":");
			e.printStackTrace();
			return false;
		}
	}

}
