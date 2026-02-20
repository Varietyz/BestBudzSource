package com.bestbudz.rs2.entity;

import static com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults.DEFAULT_USERNAME;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles debugging and monitoring operations for the World
 * Extracted from World.java with 1:1 logic preservation
 */
public class WorldDebugManager {

	/**
	 * Debug method to see what's happening during updates
	 */
	public void debugPlayerVisibility(Stoner[] stoners) {
		System.out.println("=== PLAYER VISIBILITY DEBUG ===");

		Stoner discordBot = null;
		List<Stoner> pets = new ArrayList<>();
		List<Stoner> regularPlayers = new ArrayList<>();

		for (Stoner stoner : stoners) {
			if (stoner != null && stoner.isActive()) {
				if (isDiscordBot(stoner)) {
					discordBot = stoner;
				} else if (isPet(stoner)) {
					pets.add(stoner);
				} else {
					regularPlayers.add(stoner);
				}
			}
		}

		System.out.println("Discord Bot: " + (discordBot != null ?
			("Index " + discordBot.getIndex() + ", Username: " + discordBot.getUsername() +
				", UsernameToLong: " + discordBot.getUsernameToLong() +
				", Location: " + discordBot.getLocation()) : "NONE"));

		System.out.println("Pets Count: " + pets.size());
		for (Stoner pet : pets) {
			System.out.println("  Pet - Index: " + pet.getIndex() +
				", Username: " + pet.getUsername() +
				", UsernameToLong: " + pet.getUsernameToLong() +
				", Display: " + pet.getDisplay() +
				", Location: " + pet.getLocation());
		}

		System.out.println("Regular Players Count: " + regularPlayers.size());
		for (Stoner player : regularPlayers) {
			System.out.println("  Player - Index: " + player.getIndex() +
				", Username: " + player.getUsername() +
				", UsernameToLong: " + player.getUsernameToLong() +
				", Location: " + player.getLocation());

			// Check what this player can see
			System.out.println("    Can see:");
			for (Stoner other : player.getStoners()) {
				if (other != null) {
					String type = isDiscordBot(other) ? "DISCORD_BOT" :
						(isPet(other) ? "PET" : "REGULAR");
					System.out.println("      " + type + " - " + other.getUsername() +
						" (Index: " + other.getIndex() + ")");
				}
			}
		}

		System.out.println("=== END DEBUG ===");
	}

	/**
	 * Check for username collision issues
	 */
	public void checkUsernameCollisions(Stoner[] stoners) {
		Map<Long, List<String>> usernameToLongMap = new HashMap<>();

		for (Stoner stoner : stoners) {
			if (stoner != null && stoner.isActive()) {
				long usernameToLong = stoner.getUsernameToLong();
				usernameToLongMap.computeIfAbsent(usernameToLong, k -> new ArrayList<>())
					.add(stoner.getUsername() + " (Index: " + stoner.getIndex() + ")");
			}
		}

		System.out.println("=== USERNAME COLLISION CHECK ===");
		for (Map.Entry<Long, List<String>> entry : usernameToLongMap.entrySet()) {
			if (entry.getValue().size() > 1) {
				System.out.println("COLLISION DETECTED for usernameToLong " + entry.getKey() + ":");
				for (String username : entry.getValue()) {
					System.out.println("  " + username);
				}
			}
		}
		System.out.println("=== END COLLISION CHECK ===");
	}

	// Helper methods for entity type checking
	private boolean isDiscordBot(Stoner stoner) {
		return stoner != null && DEFAULT_USERNAME.equals(stoner.getUsername());
	}

	private boolean isPet(Stoner stoner) {
		return stoner != null && stoner.isPetStoner();
	}
}