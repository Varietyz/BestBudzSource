package com.bestbudz.rs2.entity.pets;

import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.GameDefinitionLoader;
import com.bestbudz.rs2.entity.stoner.StonerAnimations;

public class PetDefinition {

	public static StonerAnimations createPetAnimations(int npcId) {
		NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcId);

		if (npcDef == null) {

			return new StonerAnimations();
		}

		StonerAnimations animations = new StonerAnimations();

		int standAnim = npcDef.getStandAnimation() != 65535 ? npcDef.getStandAnimation() : 808;
		int walkAnim = npcDef.getWalkAnimation() != 65535 ? npcDef.getWalkAnimation() : 819;
		int turn180Anim = npcDef.getTurn180Animation() != 65535 ? npcDef.getTurn180Animation() : 820;
		int turn90CWAnim = npcDef.getTurn90CWAnimation() != 65535 ? npcDef.getTurn90CWAnimation() : 821;
		int turn90CCWAnim = npcDef.getTurn90CCWAnimation() != 65535 ? npcDef.getTurn90CCWAnimation() : 822;
		int runAnim = walkAnim;

		animations.set(
			standAnim,
			standAnim,
			walkAnim,
			turn180Anim,
			turn90CWAnim,
			turn90CCWAnim,
			runAnim
		);

		return animations;
	}

	public static int getPetHP(int npcId) {
		NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcId);

		if (npcDef == null) {
			return 1000;
		}

		return Math.max(100, npcDef.getGrade() * 10);
	}

	public static int getPetSize(int npcId) {
		NpcDefinition npcDef = GameDefinitionLoader.getNpcDefinition(npcId);

		if (npcDef == null) {
			return 1;
		}

		int originalSize = npcDef.getSize();

		switch (npcId) {
			case 4000:
			case 4001:
			case 4003:
			case 4004:
			case 4009:
			case 6640:
			case 497:
			case 495:
			case 5559:
			case 5560:
				return Math.max(2, 2);

			case 4006:
			case 4007:
			case 4008:
			case 318:
			case 6655:
			case 6637:
			case 6638:
				return Math.max(2, originalSize);

			default:

				return Math.max(1, originalSize);
		}
	}
}
