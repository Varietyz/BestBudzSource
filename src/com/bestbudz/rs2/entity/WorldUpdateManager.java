package com.bestbudz.rs2.entity;

import static com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults.DEFAULT_USERNAME;
import com.bestbudz.rs2.content.dwarfcannon.DwarfCannon;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.mob.MobUpdateFlags;
import com.bestbudz.rs2.entity.pets.Pet;
import com.bestbudz.rs2.entity.pets.PetCombat;
import com.bestbudz.rs2.entity.pets.PetManager;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerUpdateFlags;
import com.bestbudz.rs2.entity.stoner.net.Client;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendNPCUpdate;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerUpdate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the main update processing cycle for World
 * Extracted from World.java with 1:1 logic preservation
 */
public class WorldUpdateManager {

	/**
	 * Process all entities (stoners and mobs) through their update cycles
	 */
	public void processEntities(Stoner[] stoners, Mob[] mobs, List<DwarfCannon> cannons,
								StonerUpdateFlags[] pFlags, MobUpdateFlags[] nFlags) {

		// Pre-pass: process all stoners (FIXED FOR DISCORD BOT AND PETS)
		for (int i = 1; i < stoners.length; i++) {
			var stoner = stoners[i];
			if (stoner == null) continue;

			try {
				if (!stoner.isActive()) {
					if (stoner.getClient().getStage() == Client.Stages.LOGGED_IN) {
						stoner.setActive(true);
						stoner.start();
						stoner.getClient().resetLastPacketReceived();
					} else if (World.getCycles() - stoner.getClient().getLastPacketTime() > 30) {
						stoner.logout(true);
						continue;
					}
				}

				// FIXED: Process Discord bot properly but efficiently
				if (isDiscordBot(stoner)) {
					processDiscordBotEfficiently(stoner);
				} else if (isPet(stoner)) {
					// CRITICAL FIX: Full processing for pets including combat
					processPetWithFullCombat(stoner);
				} else {
					// Full processing for real players
					stoner.getClient().processIncomingPackets();
					stoner.process();
					if (!isDiscordBot(stoner) && !isPet(stoner) &&
						stoner.getActivePets() != null && !stoner.getActivePets().isEmpty()) {
						processPetCombat(stoner);
					}
					stoner.getClient().reset();

					// Cannon rotation only for real players
					for (var cannon : cannons) {
						if (cannon.getLoc().isViewableFrom(stoner.getLocation())) {
							cannon.rotate(stoner);
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				stoner.logout(true);
			}
		}

		// Pre-pass: process all mobs (unchanged)
		for (int i = 0; i < mobs.length; i++) {
			var mob = mobs[i];
			if (mob == null) continue;

			try {
				mob.process();
			} catch (Exception e) {
				e.printStackTrace();
				mob.remove();
			}
		}

		// Movement + flags for stoners (FIXED FOR DISCORD BOT)
		for (int i = 1; i < stoners.length; i++) {
			var stoner = stoners[i];
			if (stoner == null || !stoner.isActive()) continue;

			try {
				// CRITICAL FIX: Process movement for ALL stoners including Discord bot and pets
				stoner.getMovementHandler().process();

				// Always create flags for visibility
				pFlags[i] = new StonerUpdateFlags(stoner);

			} catch (Exception e) {
				e.printStackTrace();
				stoner.logout(true);
			}
		}

		// Movement + flags for mobs (unchanged)
		for (var mob : mobs) {
			if (mob == null) continue;
			try {
				mob.processMovement();
				nFlags[mob.getIndex()] = new MobUpdateFlags(mob);
			} catch (Exception e) {
				e.printStackTrace();
				mob.remove();
			}
		}

		// Send updates (FIXED FOR DISCORD BOT VISIBILITY)
		for (int i = 1; i < stoners.length; i++) {
			var stoner = stoners[i];
			if (stoner == null || !stoner.isActive() || pFlags[i] == null) continue;

			try {
				// FIXED: Discord bot and pets don't need to RECEIVE updates, but others need to see them
				if (!isPet(stoner) && !isDiscordBot(stoner)) {
					stoner.getClient().queueOutgoingPacket(new SendStonerUpdate(pFlags));
					stoner.getClient().queueOutgoingPacket(new SendNPCUpdate(nFlags, pFlags[i]));
				}
			} catch (Exception e) {
				e.printStackTrace();
				stoner.logout(true);
			}
		}

		// Reset stoners (FIXED FOR DISCORD BOT AND PETS)
		for (int i = 1; i < stoners.length; i++) {
			var stoner = stoners[i];
			if (stoner == null || !stoner.isActive()) continue;

			try {
				// FIXED: Proper reset for Discord bot and pets
				if (isDiscordBot(stoner)) {
					resetDiscordBotProperly(stoner);
				} else if (isPet(stoner)) {
					resetPetProperly(stoner);
				} else {
					stoner.reset();
				}
			} catch (Exception e) {
				e.printStackTrace();
				stoner.logout(true);
			}
		}

		// Reset mobs (unchanged)
		for (var mob : mobs) {
			if (mob == null) continue;
			try {
				mob.reset();
			} catch (Exception e) {
				e.printStackTrace();
				mob.remove();
			}
		}
	}

	/**
	 * Process pet combat for all active pets of an owner
	 */
	public void processPetCombat(Stoner owner) {
		List<Pet> activePets = PetManager.getActivePets(owner);
		for (Pet pet : activePets) {
			if (pet != null && pet.getPetStoner().isActive()) {
				PetCombat.processPetCombat(pet);
			}
		}
	}

	/**
	 * NEW: Full combat processing for pets
	 */
	private void processPetWithFullCombat(Stoner pet) {
		try {
			pet.getClient().resetLastPacketReceived();

			// CRITICAL: Process packets (minimal for pets)
			pet.getClient().processIncomingPackets();

			// CRITICAL: Full process() call for pets to get proper combat
			pet.process(); // This will call pet.getStonerCombat().process()

			// Reset client state
			pet.getClient().reset();

			// DEBUG: Check if pet is trying to attack
			if (pet.getCombat().getAssaulting() != null) {
				System.out.println("DEBUG: Pet NPC " + pet.getUsername() +
					" has target: " + (pet.getCombat().getAssaulting().isNpc() ? "NPC" : "Player") +
					", assault timer: " + pet.getCombat().getAssaultTimer() +
					", in combat: " + pet.getCombat().inCombat() +
					", NPC appearance ID: " + pet.getNpcAppearanceId());
			}

		} catch (Exception e) {
			System.err.println("Pet full combat processing error for " + pet.getUsername() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * NEW: Proper reset for pets
	 */
	private void resetPetProperly(Stoner pet) {
		try {
			// Full reset to ensure proper state management
			pet.reset();
		} catch (Exception e) {
			System.err.println("Pet reset error for " + pet.getUsername() + ": " + e.getMessage());
		}
	}

	/**
	 * FIXED: Proper but efficient processing for Discord bot
	 */
	private void processDiscordBotEfficiently(Stoner bot) {
		try {
			// CRITICAL FIX: Full processing for Discord bot to ensure proper combat
			bot.getClient().resetLastPacketReceived();

			// Process packets (minimal for bot)
			bot.getClient().processIncomingPackets();

			// CRITICAL: Full process() call for proper aggression and combat
			bot.process();

			// Reset client state
			bot.getClient().reset();

			// Debug output every 10 seconds for Discord bot status
			if (World.getCycles() % 100 == 0) { // Every ~30 seconds
				System.out.println("Discord bot status - " +
					"Active: " + bot.isActive() +
					", Location: " + bot.getLocation() +
					", In Combat: " + bot.getCombat().inCombat() +
					", Current Target: " + (bot.getCombat().getAssaulting() != null ?
					("NPC " + bot.getCombat().getAssaulting().getIndex()) : "none"));
			}

		} catch (Exception e) {
			System.err.println("Discord bot processing error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * FIXED: Ensure Discord bot is properly reset
	 */
	private void resetDiscordBotProperly(Stoner bot) {
		try {
			// CRITICAL: Full reset to ensure visibility to other players
			bot.reset();
		} catch (Exception e) {
			System.err.println("Discord bot reset error: " + e.getMessage());
		}
	}

	// Helper methods for entity type checking
	private boolean isDiscordBot(Stoner stoner) {
		return stoner != null && DEFAULT_USERNAME.equals(stoner.getUsername());
	}

	private boolean isPet(Stoner stoner) {
		return stoner != null && stoner.isPetStoner();
	}
}