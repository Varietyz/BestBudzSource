package com.bestbudz.rs2.entity.pets;

import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.entity.stoner.StonerAnimations;

/**
 * Pet definition system that uses existing GameDefinitionLoader data
 */
public class PetDefinition {

	/**
	 * Create StonerAnimations for this pet using existing NPC definitions
	 */
	public static StonerAnimations createPetAnimations(int npcId) {
		NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcId);

		if (npcDef == null) {
			// Return default animations if no definition found
			return new StonerAnimations();
		}

		StonerAnimations animations = new StonerAnimations();

		// Use the NPC's actual animations with proper fallbacks
		int standAnim = npcDef.getStandAnimation() != 65535 ? npcDef.getStandAnimation() : 808;
		int walkAnim = npcDef.getWalkAnimation() != 65535 ? npcDef.getWalkAnimation() : 819;
		int turn180Anim = npcDef.getTurn180Animation() != 65535 ? npcDef.getTurn180Animation() : 820;
		int turn90CWAnim = npcDef.getTurn90CWAnimation() != 65535 ? npcDef.getTurn90CWAnimation() : 821;
		int turn90CCWAnim = npcDef.getTurn90CCWAnimation() != 65535 ? npcDef.getTurn90CCWAnimation() : 822;
		int runAnim = walkAnim; // Use walk animation for running

		animations.set(
			standAnim,     // Stand
			standAnim,     // Stand turn (use stand animation)
			walkAnim,      // Walk
			turn180Anim,   // 180 turn
			turn90CWAnim,  // 90CW turn
			turn90CCWAnim, // 90CCW turn
			runAnim        // Run
		);

		return animations;
	}

	/**
	 * Get pet HP based on NPC definition
	 */
	public static int getPetHP(int npcId) {
		NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcId);

		if (npcDef == null) {
			return 1000; // Default HP
		}

		// Scale HP appropriately for pets (original grade * 10, minimum 100)
		return Math.max(100, npcDef.getGrade() * 10);
	}

	/**
	 * Get pet size from NPC definition with proper scaling
	 */
	public static int getPetSize(int npcId) {
		NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcId);

		if (npcDef == null) {
			return 1; // Default size (1x1 tile)
		}

		int originalSize = npcDef.getSize();

		// Handle special cases for known large pets
		switch (npcId) {
			case 4000: // Prince Black Dragon
			case 4001: // General Graardor
			case 4003: // Kree'arra
			case 4004: // K'ril Tsutsaroth
			case 4009: // Commander Zilyana
			case 6640: // Kraken
			case 497:  // Callisto
			case 495:  // Venenatis
			case 5559: // Vet'ion Purple
			case 5560: // Vet'ion Orange
				return Math.max(2, 2); // Ensure minimum 2x2 for boss pets

			case 4006: // Dagannoth Supreme
			case 4007: // Dagannoth Prime
			case 4008: // Dagannoth Rex
			case 318:  // Dark Core
			case 6655: // Smoke Devil
			case 6637: // kq bug
			case 6638: // kq fly
				return Math.max(2, originalSize); // 2x2 for medium boss pets

			default:
				// For regular pets, use original size but ensure minimum 1x1
				return Math.max(1, originalSize);
		}
	}
}