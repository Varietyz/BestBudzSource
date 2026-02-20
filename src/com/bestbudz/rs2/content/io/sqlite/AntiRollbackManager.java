package com.bestbudz.rs2.content.io.sqlite;

import com.bestbudz.rs2.entity.stoner.Stoner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public final class AntiRollbackManager {

	private static final ConcurrentHashMap<String, Long> saveTimestamps = new ConcurrentHashMap<>();
	private static final File FILE = new File("data/database/save_tokens.db");

	private AntiRollbackManager() {}

	public static void markSave(Stoner stoner) {
		if (stoner == null || stoner.getUsername() == null) return;
		saveTimestamps.put(stoner.getUsername().toLowerCase(), System.currentTimeMillis());
	}

	public static void writeSnapshot() {
		try (FileWriter fw = new FileWriter(FILE, false)) {
			for (var entry : saveTimestamps.entrySet()) {
				fw.write(entry.getKey() + ":" + entry.getValue() + "\n");
			}
		} catch (IOException e) {
			System.err.println("[AntiRollback] Failed to write snapshot:");
			e.printStackTrace();
		}
	}

	public static void readSnapshot() {
		if (!FILE.exists()) return;

		try (Scanner sc = new Scanner(FILE)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (!line.contains(":")) continue;

				String[] parts = line.split(":");
				saveTimestamps.put(parts[0].toLowerCase(), Long.parseLong(parts[1]));
			}
		} catch (Exception e) {
			System.err.println("[AntiRollback] Failed to read snapshot:");
			e.printStackTrace();
		}
	}
}
