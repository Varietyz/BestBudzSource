package com.bestbudz.rs2.entity.pets;

import com.bestbudz.rs2.entity.pets.abilities.ChaosElementAbility;
import com.bestbudz.rs2.entity.pets.abilities.DarkCoreAbility;
import com.bestbudz.rs2.entity.pets.abilities.DragonBreathAbility;
import com.bestbudz.rs2.entity.pets.abilities.ExplosiveAbility;
import com.bestbudz.rs2.entity.pets.abilities.JadPetAbility;
import com.bestbudz.rs2.entity.pets.abilities.KalphiteQueenAbility;
import com.bestbudz.rs2.entity.pets.abilities.PetAbility;
import com.bestbudz.rs2.entity.pets.abilities.PoisonStingAbility;
import com.bestbudz.rs2.entity.pets.abilities.SaradominHealAbility;
import com.bestbudz.rs2.entity.pets.abilities.TeleportAbility;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for mapping pets to their special abilities
 */
public class PetAbilityRegistry {

	private static final Map<PetData, PetAbility> abilities = new HashMap<>();

	static {
		// Initialize all pet abilities
		initializeAbilities();
	}

	/**
	 * Register all pet abilities - mapping pets to their implemented abilities
	 */
	private static void initializeAbilities() {
		// Boss pets with implemented abilities
		abilities.put(PetData.KALPHITE_PRINCESS_FLY, new KalphiteQueenAbility());
		abilities.put(PetData.KALPHITE_PRINCESS_BUG, new KalphiteQueenAbility());
		abilities.put(PetData.DARK_CORE, new DarkCoreAbility());
		abilities.put(PetData.PRINCE_BLACK_DRAGON, new DragonBreathAbility());
		abilities.put(PetData.CHAOS_ELEMENT, new ChaosElementAbility());
		abilities.put(PetData.SCORPIAS_OFFSPRING, new PoisonStingAbility());
		abilities.put(PetData.COMMANDER_ZILYANA, new SaradominHealAbility());

		// Small pets with implemented abilities
		abilities.put(PetData.IMP, new TeleportAbility());
		abilities.put(PetData.BLACK_CHINCHOMPA, new ExplosiveAbility());
		abilities.put(PetData.BABY_DRAGON, new DragonBreathAbility());

		// Snakelings with poison abilities
		abilities.put(PetData.GREEN_SNAKELING, new PoisonStingAbility());
		abilities.put(PetData.RED_SNAKELING, new PoisonStingAbility());
		abilities.put(PetData.BLUE_SNAKELING, new PoisonStingAbility());

    // Jad
    abilities.put(PetData.JAD, new JadPetAbility());
		abilities.put(PetData.TOY_JAD, new JadPetAbility());
		abilities.put(PetData.INFANT_JAD, new JadPetAbility());
		abilities.put(PetData.HATCHLING_JAD, new JadPetAbility());
		abilities.put(PetData.CUB_JAD, new JadPetAbility());
		abilities.put(PetData.YOUTH_JAD, new JadPetAbility());
		abilities.put(PetData.TEEN_JAD, new JadPetAbility());
		abilities.put(PetData.YOUNG_ADULT_JAD, new JadPetAbility());
		abilities.put(PetData.ADULT_JAD, new JadPetAbility());
		abilities.put(PetData.PRIME_JAD, new JadPetAbility());

	}

	/**
	 * Get the ability for a specific pet type
	 */
	public static PetAbility getAbility(PetData petData) {
		return abilities.get(petData);
	}

}