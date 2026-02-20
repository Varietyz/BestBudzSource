package com.bestbudz.rs2.content.io.sqlite;

import com.bestbudz.rs2.entity.stoner.Stoner;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SaveCache {

	private static final Set<String> dirtyUsernames = ConcurrentHashMap.newKeySet();

	private SaveCache() {}

	public static void markDirty(Stoner stoner) {
		if (stoner != null && stoner.getUsername() != null) {
			dirtyUsernames.add(stoner.getUsername());
		}
	}

	public static boolean isDirty(Stoner stoner) {
		return stoner != null && dirtyUsernames.contains(stoner.getUsername());
	}

	public static void clear(Stoner stoner) {
		if (stoner != null) {
			dirtyUsernames.remove(stoner.getUsername());
		}
	}
}
