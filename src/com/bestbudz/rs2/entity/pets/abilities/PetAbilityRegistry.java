package com.bestbudz.rs2.entity.pets.abilities;

import com.bestbudz.rs2.entity.pets.PetData;
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

		// TODO: Implement these abilities when ready
		/*
		abilities.put(PetData.KREE_ARRA, new WindstormAbility());
		abilities.put(PetData.CALLISTO, new BearRoarAbility());
		abilities.put(PetData.VENENATIS, new WebSpinAbility());
		abilities.put(PetData.VETION_PURPLE, new UndeadStrengthAbility());
		abilities.put(PetData.VETION_ORANGE, new UndeadStrengthAbility());
		abilities.put(PetData.BABY_MOLE, new DigAbility());
		abilities.put(PetData.KRAKEN, new InkSprayAbility());
		abilities.put(PetData.DAGANNOTH_SUPRIME, new ElementalResistanceAbility());
		abilities.put(PetData.DAGANNOTH_RIME, new ElementalResistanceAbility());
		abilities.put(PetData.DAGANNOTH_REX, new ElementalResistanceAbility());
		abilities.put(PetData.GENERAL_GRAARDOR, new BandosStrengthAbility());
		abilities.put(PetData.KRIL_TSUTSAROTH, new ZamorakCurseAbility());
		abilities.put(PetData.SMOKE_DEVIL, new SmokeCloudAbility());
		abilities.put(PetData.KEBBIT, new QuickStrikeAbility());
		abilities.put(PetData.BUTTERFLY, new DistractAbility());
		abilities.put(PetData.GIANT_EAGLE, new DiveAbility());
		abilities.put(PetData.GNOME, new LuckyDodgeAbility());
		abilities.put(PetData.CHICKEN, new PeckAbility());
		abilities.put(PetData.HELLHOUND, new HellFireAbility());
		abilities.put(PetData.DEMON, new DemonicCurseAbility());
		abilities.put(PetData.ROCNAR, new RockThrowAbility());
		abilities.put(PetData.FLAMBEED, new FlameAuraAbility());
		abilities.put(PetData.TENTACLE, new GrappleAbility());
		abilities.put(PetData.DEATH, new DeathTouchAbility());
		*/
	}

	/**
	 * Get the ability for a specific pet type
	 */
	public static PetAbility getAbility(PetData petData) {
		return abilities.get(petData);
	}

	/**
	 * Check if a pet has a special ability
	 */
	public static boolean hasAbility(PetData petData) {
		return abilities.containsKey(petData);
	}
}