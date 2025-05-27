package com.bestbudz.rs2.content.io.sqlite;

import com.bestbudz.rs2.entity.stoner.Stoner;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks which players have modified state and need saving.
 * Avoids redundant writes every tick or event.
 */
public final class SaveCache {

	private static final Set<String> dirtyUsernames = ConcurrentHashMap.newKeySet();

	private SaveCache() {}

	/**
	 * Marks a player as dirty (modified).
	 */
	public static void markDirty(Stoner stoner) {
		if (stoner != null && stoner.getUsername() != null) {
			dirtyUsernames.add(stoner.getUsername());
		}
	}

	/**
	 * Checks if this player needs to be saved.
	 */
	public static boolean isDirty(Stoner stoner) {
		return stoner != null && dirtyUsernames.contains(stoner.getUsername());
	}

	/**
	 * Clears the dirty flag once the save has occurred.
	 */
	public static void clear(Stoner stoner) {
		if (stoner != null) {
			dirtyUsernames.remove(stoner.getUsername());
		}
	}
}
