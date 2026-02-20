package com.bestbudz.rs2.content.profession.consumer.allergies;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.profession.Professions;
import java.util.*;

public class AllergySystem {

	public enum AllergyType {
		SEAFOOD(Arrays.asList(
			315, 319, 325, 329, 333, 339, 347, 351, 355, 361, 365, 373, 379, 385, 391, 397, // Fish
			7946, 14825, 14831, 15266, 11936, 13433, // Special fish
			2297, 2299, 7188, 7190, 7198, 7200, 7068 // Fish-based foods
		), "You feel nauseous from the seafood!", Hit.HitTypes.POISON),

		DAIRY(Arrays.asList(
			6703, 6705, 1891, 1893, 1895, 1897, 1899, 1901, // Cheese/butter items, cakes
			2289, 2291, 2293, 2295, 2301 // Pizzas with cheese
		), "Your stomach churns from the dairy!", Hit.HitTypes.NONE),

		GRAINS(Arrays.asList(
			2309, 2289, 2291, 2293, 2295, 2297, 2299, 2301, // Bread, pizzas
			2323, 2325, 2327, 2331, 2333, 2335, 7178, 7180 // Pies
		), "The grains make you feel bloated and sick!", Hit.HitTypes.DISEASE),

		FRUITS(Arrays.asList(
			1963, 6883, 2323, 2325, 2335, 7178, 7180, 2301, // Fruits and fruit pies
			4561, 10476 // Purple sweets (sugar-based)
		), "You break out in hives from the fruit!", Hit.HitTypes.NONE),

		MEAT(Arrays.asList(
			2140, 2142, 4291, 4293, 2293, 2295, 2327, 2331, // Cooked meats
			7062, 7064 // Meat-based dishes
		), "The meat makes you violently ill!", Hit.HitTypes.POISON),

		HERBS_POTIONS(Arrays.asList(
			2446, 175, 177, 179, 3032, 3034, 3036, 3038, // Cannabis-based potions
			3040, 3042, 3044, 3046, 6685, 6687, 6689, 6691 // Herbal elixirs and ales
		), "Your body rejects the herbal compounds!", Hit.HitTypes.NONE),

		VEGETABLES(Arrays.asList(
			6701, 6703, 6705, 7054, 7056, 7060, // Potatoes
			7062, 7064, 7068, 7072, 7078, 7082, 7084, // Vegetable dishes
			7178, 7180 // Garden pie
		), "The vegetables cause an allergic reaction!", Hit.HitTypes.DISEASE);

		private final List<Integer> affectedItems;
		private final String reactionMessage;
		private final Hit.HitTypes hitType;

		AllergyType(List<Integer> affectedItems, String reactionMessage, Hit.HitTypes hitType) {
			this.affectedItems = affectedItems;
			this.reactionMessage = reactionMessage;
			this.hitType = hitType;
		}

		public boolean affects(int itemId) {
			return affectedItems.contains(itemId);
		}

		public String getReactionMessage() { return reactionMessage; }
		public Hit.HitTypes getHitType() { return hitType; }
	}

	private final Stoner stoner;
	private Set<AllergyType> allergies = new HashSet<>();
	private Map<AllergyType, Integer> resistance = new HashMap<>();
	private boolean allergiesInitialized = false;

	// Constants for the new resistance system
	private static final int MAX_RESISTANCE = 20000; // Changed from 100 to 20,000
	private static final int MAX_LEVEL = 420; // Changed from 99 to 420

	public AllergySystem(Stoner stoner) {
		this.stoner = stoner;
		// Don't generate allergies here - wait until username is available
	}

	public int getConsumerLevel() {
		return (int) stoner.getMaxGrades()[Professions.CONSUMER];
	}

	/**
	 * Call this method when the player logs in to ensure allergies are initialized
	 */
	public void onLogin() {
		ensureAllergiesInitialized();
		// Load resistance data from file will be handled by ConsumerSaveManager
	}

	/**
	 * Call this when resistance changes to save progress
	 */
	public void saveProgress() {
		// This will be called by ConsumerSaveManager
		// Just trigger the save system
		if (stoner.getUsername() != null) {
			com.bestbudz.rs2.content.profession.consumer.io.ConsumerSaveManager.saveConsumerData(stoner);
		}
	}

	private void ensureAllergiesInitialized() {
		if (!allergiesInitialized && stoner.getUsername() != null) {
			generateAllergies();
			allergiesInitialized = true;
		}
	}

	private void generateAllergies() {
		// Generate 2-4 random allergies based on username hash for consistency
		Random rand = new Random(stoner.getUsername().hashCode());
		int allergyCount = 2; // 2-4 allergies

		List<AllergyType> allAllergies = Arrays.asList(AllergyType.values());
		Collections.shuffle(allAllergies, rand);

		for (int i = 0; i < allergyCount && i < allAllergies.size(); i++) {
			AllergyType allergy = allAllergies.get(i);
			allergies.add(allergy);
			resistance.put(allergy, 0);
		}

		// Notify player of their allergies on first discovery
		if (!allergies.isEmpty()) {
			stoner.send(new SendMessage("@red@You discover you have food allergies! Check what affects you as you consume items."));
		}
	}

	public AllergyType getAllergyFor(int itemId) {
		ensureAllergiesInitialized();
		for (AllergyType allergy : allergies) {
			if (allergy.affects(itemId)) {
				return allergy;
			}
		}
		return null;
	}

	public boolean shouldTriggerAllergy(AllergyType allergy) {
		ensureAllergiesInitialized();
		int consumerLevel = getConsumerLevel();
		int currentResistance = resistance.getOrDefault(allergy, 0);

		// Base trigger chance starts at 100% and decreases with level and resistance
		double triggerChance = 1.0;

		// Consumer level reduces chance (max 50% reduction at level 420)
		triggerChance -= (consumerLevel / (MAX_LEVEL * 2.0)) * 0.5;

		// Resistance reduces chance (each point = 0.005% reduction, so 20k = 100%)
		triggerChance -= (currentResistance / (double) MAX_RESISTANCE);

		// Minimum 10% chance unless fully immune
		if (currentResistance < MAX_RESISTANCE) {
			triggerChance = Math.max(0.1, triggerChance);
		} else {
			triggerChance = 0; // Full immunity
		}

		return Math.random() < triggerChance;
	}

	public void applyAllergyReaction(AllergyType allergy, int originalHeal) {
		// Schedule the allergy reaction for the next tick to avoid override
		com.bestbudz.core.task.TaskQueue.queue(
			new com.bestbudz.core.task.Task(
				stoner,
				1, // 1 tick delay
				false,
				com.bestbudz.core.task.Task.StackType.STACK,
				com.bestbudz.core.task.Task.BreakType.NEVER,
				com.bestbudz.core.task.impl.TaskIdentifier.CURRENT_ACTION) {

				@Override
				public void execute() {
					performAllergyReaction(allergy, originalHeal);
					stop();
				}

				@Override
				public void onStop() {}
			});
	}

	/**
	 * Perform the actual allergy reaction on a delayed tick
	 */
	private void performAllergyReaction(AllergyType allergy, int originalHeal) {
		int consumerLevel = getConsumerLevel();
		int severity = calculateSeverity(allergy, consumerLevel);

		// Send reaction message
		stoner.send(new SendMessage("@red@" + allergy.getReactionMessage()));

		// Calculate damage based on severity and original heal amount
		int damage = calculateAllergyDamage(severity, originalHeal);

		// Apply the hit with proper visual effects
		Hit allergyHit = new Hit(damage, allergy.getHitType());
		stoner.hit(allergyHit);

		// Apply animation after the eating animation has finished
		int animationId = getReactionAnimation(severity);
		if (animationId > 0) {
			stoner.getUpdateFlags().sendAnimation(animationId, 0);
		}

		// Force the update flags to be set for hit display
		stoner.getUpdateFlags().setUpdateRequired(true);
		stoner.getUpdateFlags().setHitUpdate(true);
		stoner.getUpdateFlags().setDamage(damage);
		stoner.getUpdateFlags().setHitType(allergyHit.getHitType());
		stoner.getUpdateFlags().setHitUpdateCombatType(allergyHit.getCombatHitType());

		// Apply negative effects based on severity
		switch (severity) {
			case 1: // Mild
				stoner.send(new SendMessage("@yel@You feel slightly unwell..."));
				break;

			case 2: // Moderate
				stoner.send(new SendMessage("@ora@Your reaction is getting worse!"));
				// Temporary stat debuff
				stoner.getProfession().deductFromGrade(0, 5); // Attack
				stoner.getProfession().deductFromGrade(2, 5); // Strength
				break;

			case 3: // Severe
				stoner.send(new SendMessage("@red@You're tweaking due to a severe allergic reaction!"));
				// Major stat debuffs
				stoner.getProfession().deductFromGrade(0, 10);
				stoner.getProfession().deductFromGrade(1, 10); // Defence
				stoner.getProfession().deductFromGrade(2, 10);
				break;
		}

		// Apply additional status effects based on allergy type
		applyStatusEffects(allergy, severity);

		// Ensure profession stats and visual updates are processed
		stoner.getProfession().update();
		stoner.setAppearanceUpdateRequired(true);
	}

	/**
	 * Get appropriate animation ID based on reaction severity
	 */
	private int getReactionAnimation(int severity) {
		switch (severity) {
			case 1: // Mild - use a subtle animation
				return 404; // Sneeze/cough animation
			case 2: // Moderate - more noticeable
				return 420; // Hit/damage animation
			case 3: // Severe - dramatic reaction
				return 4965; // Could be death animation or dramatic reaction
			default:
				return 0; // No animation
		}
	}

	/**
	 * Calculate damage amount based on severity and original heal
	 */
	private int calculateAllergyDamage(int severity, int originalHeal) {
		switch (severity) {
			case 1: // Mild - 25% of heal as damage, minimum 3
				return Math.max(3, originalHeal / 4);
			case 2: // Moderate - 50% of heal as damage, minimum 7
				return Math.max(7, originalHeal / 2);
			case 3: // Severe - 100% of heal as damage, minimum 15
				return Math.max(15, originalHeal);
			default:
				return 1;
		}
	}

	/**
	 * Apply status effects (poison/disease) based on allergy type and severity
	 */
	private void applyStatusEffects(AllergyType allergy, int severity) {
		switch (allergy.getHitType()) {
			case POISON:
				// Try to apply poison if the method exists
				try {
					// Check if stoner has a poison method - adjust based on your actual poison system
					if (severity >= 2) { // Only for moderate/severe reactions
						// You may need to adjust this based on your actual poison implementation
						// stoner.getPoison().inflict(severity * 30); // Example
						stoner.send(new SendMessage("@gre@The " + allergy.name().toLowerCase().replace("_", " ") +
							" has poisoned you!"));
					}
				} catch (Exception e) {
					// Poison system might not be available, just continue
				}
				break;
			case DISEASE:
				// Apply additional disease effects for severe reactions
				if (severity >= 2) {
					applyDiseaseEffect(severity);
				}
				break;
			case DEFLECT:
				// Just direct damage, no additional effects
				break;
		}
	}

	/**
	 * Apply disease-like stat drain effects
	 */
	private void applyDiseaseEffect(int severity) {
		// Additional stat reduction for disease effects
		int statDrain = severity; // 1-3 additional stat points

		// Drain additional stats for disease
		stoner.getProfession().deductFromGrade(3, statDrain); // Life/HP
		stoner.getProfession().deductFromGrade(4, statDrain); // Sagittarius/Ranged

		stoner.send(new SendMessage("@red@The allergic reaction saps your vitality!"));
	}

	private int calculateSeverity(AllergyType allergy, int consumerLevel) {
		int currentResistance = resistance.getOrDefault(allergy, 0);
		int advancements = stoner.getProfessionAdvances()[Professions.CONSUMER];

		// Base severity decreases with consumer level and resistance
		int severity = 3;

		// Advancement-based permanent unlocks (override level requirements)
		if (advancements >= 2 || consumerLevel >= 252 || currentResistance >= 10000) severity = 1; // Tier 2: Mild reactions only
		else if (advancements >= 1 || consumerLevel >= 126 || currentResistance >= 5000) severity = 2; // Tier 1: No severe reactions

		return severity;
	}

	public void handleAllergyExposure(AllergyType allergy) {
		int consumerLevel = getConsumerLevel();
		int advancements = stoner.getProfessionAdvances()[Professions.CONSUMER];

		// Check if player can build resistance based on level OR advancements
		if (!canBuildResistance(consumerLevel, advancements)) {
			stoner.send(new SendMessage("@yel@Your Consumer level is too low to build resistance. (Need level 84 or 1+ advancement)"));
			return;
		}

		int currentResistance = resistance.getOrDefault(allergy, 0);

		// Gradual resistance building - much slower progression
		if (currentResistance < MAX_RESISTANCE) {
			int resistanceGain = calculateResistanceGain(consumerLevel, advancements);

			resistance.put(allergy, Math.min(MAX_RESISTANCE, currentResistance + resistanceGain));

			stoner.send(new SendMessage("@gre@You feel slightly more resistant to " +
				allergy.name().toLowerCase().replace("_", " ") + ". (" +
				resistance.get(allergy) + "/" + MAX_RESISTANCE + ")"));

			// Save progress when resistance changes
			saveProgress();

			// Check for full immunity
			if (resistance.get(allergy) >= MAX_RESISTANCE &&
				stoner.getProfessionAdvances()[Professions.CONSUMER] >= 1) {
				allergies.remove(allergy);
				stoner.send(new SendMessage("@gre@You have completely overcome your " +
					allergy.name().toLowerCase().replace("_", " ") + " allergy!"));
				stoner.getUpdateFlags().sendGraphic(Professions.UPGRADE_GRAPHIC);

				// Save progress after overcoming allergy
				saveProgress();
			}
		}
	}

	// Enhanced consumption mechanic for Consumer skill mastery
	public boolean applyConsumerMastery(int originalHeal, int consumerLevel) {
		int advancements = stoner.getProfessionAdvances()[Professions.CONSUMER];

		// Advancement-based permanent unlock or level requirement
		if (advancements < 2 && consumerLevel < 168) return false; // Tier 2 advancement or 40% of 420

		// Absorption Mastery: chance to get bonus healing
		double masteryChance = 0.0;

		if (advancements >= 2) {
			// Tier 2 advancement: 10% base chance + level scaling
			masteryChance = 0.10 + (consumerLevel / 420.0) * 0.20; // 10-30% total
		} else if (consumerLevel >= 168) {
			// Level-based: 0-30% at levels 168-420
			masteryChance = (consumerLevel - 168) / (420.0 - 168.0) * 0.3;
		}

		if (Math.random() < masteryChance) {
			int bonusHeal = (int) (originalHeal * 0.5); // 50% bonus
			long currentHealth = stoner.getProfession().getGrades()[3];
			long maxHealth = stoner.getMaxGrades()[3];
			long newHealth = Math.min(maxHealth, currentHealth + bonusHeal);
			stoner.getProfession().setGrade(3, newHealth);
			stoner.send(new SendMessage("@gre@Your Consumer mastery enhances the effect!"));
			return true;
		}

		return false;
	}

	// Efficiency: chance to not consume the item
	public boolean shouldPreserveItem(int consumerLevel) {
		int advancements = stoner.getProfessionAdvances()[Professions.CONSUMER];

		// Advancement-based permanent unlock or level requirement
		if (advancements < 1 && consumerLevel < 252) return false; // Tier 1 advancement or 60% of 420

		double preserveChance = 0.0;

		if (advancements >= 1) {
			// Advancement-based: 3% per advancement, max 65% at advancement 21+ (but max advances is 420)
			double advancementBonus = Math.min(0.65, advancements * 0.03); // 3% per advancement, cap at 65%

			// Level-based bonus (small additional scaling)
			double levelBonus = (consumerLevel / 420.0) * 0.05; // Up to 5% bonus from level

			preserveChance = advancementBonus + levelBonus;
		} else if (consumerLevel >= 252) {
			// Level-based only: 0-10% at levels 252-420 (for non-advanced players)
			preserveChance = (consumerLevel - 252) / (420.0 - 252.0) * 0.1;
		}

		// Cap at 70% total (65% from advancements + 5% from level)
		preserveChance = Math.min(0.70, preserveChance);

		if (Math.random() < preserveChance) {
			stoner.send(new SendMessage("@gre@Your efficient consumption preserves the item! (" +
				String.format("%.1f", preserveChance * 100) + "% chance)"));
			return true;
		}

		return false;
	}

	// Getters for integration
	public Set<AllergyType> getAllergies() {
		ensureAllergiesInitialized();
		return new HashSet<>(allergies);
	}

	public Map<AllergyType, Integer> getResistance() {
		ensureAllergiesInitialized();
		return new HashMap<>(resistance);
	}

	// For save/load - only resistance needs to be saved, allergies regenerate from username hash
	public void setResistance(Map<AllergyType, Integer> resistance) {
		this.resistance = resistance;
	}

	// Utility method to get max resistance constant
	public static int getMaxResistance() {
		return MAX_RESISTANCE;
	}

	/**
	 * Check if player can build resistance based on level OR advancements
	 */
	private boolean canBuildResistance(int consumerLevel, int advancements) {
		return advancements >= 1 || consumerLevel >= 84; // Tier 1 advancement OR 20% of 420
	}

	/**
	 * Calculate resistance gain based on level and advancements
	 */
	private int calculateResistanceGain(int consumerLevel, int advancements) {
		// Base resistance gain from advancements
		int baseGain = 1;

		// Advancement bonuses (permanent improvements)
		if (advancements >= 1) baseGain += 1; // Tier 1: +1 base gain
		if (advancements >= 2) baseGain += 1; // Tier 2: +1 more base gain
		if (advancements >= 3) baseGain += 1; // Tier 3: +1 more base gain
		if (advancements >= 4) baseGain += 1; // Tier 4: +1 more base gain
		if (advancements >= 5) baseGain += 2; // Tier 5: +2 more base gain (max tier)

		// Level-based bonus (still scales with current level)
		int levelBonus = consumerLevel / 105; // 0-4 bonus points based on level (420/105 = 4)

		return baseGain + levelBonus;
	}

	/**
	 * Get advancement tier benefits description for UI/info purposes
	 */
	public String getAdvancementBenefits(int advancements) {
		StringBuilder benefits = new StringBuilder("Consumer Advancement Benefits:\n");

		if (advancements >= 1) {
			benefits.append("@gre@Tier 1: Can build allergy resistance at any level, +1 resistance gain, moderate reactions max\n");
			benefits.append("@gre@       Item preservation: 3% chance\n");
		}
		if (advancements >= 2) {
			benefits.append("@gre@Tier 2: +1 more resistance gain, mild reactions only, 10% base mastery chance\n");
			benefits.append("@gre@       Item preservation: 6% chance\n");
		}
		if (advancements >= 3) {
			benefits.append("@gre@Tier 3: +1 more resistance gain\n");
			benefits.append("@gre@       Item preservation: 9% chance\n");
		}

		// Show progression for higher tiers
		for (int i = 4; i <= Math.min(advancements, 21); i++) {
			benefits.append("@gre@Tier " + i + ": +1 more resistance gain\n");
			benefits.append("@gre@       Item preservation: " + (i * 3) + "% chance\n");
		}

		// Show when 65% cap is reached
		if (advancements >= 22) {
			benefits.append("@gre@Tier 22+: +1 more resistance gain\n");
			benefits.append("@gre@        Item preservation: 65% chance (MAX)\n");
		}

		// Show current preservation chance
		if (advancements > 0) {
			int preservationPercent = Math.min(65, advancements * 3);
			benefits.append("\n@yel@Current Item Preservation: " + preservationPercent + "%");
			if (preservationPercent < 65) {
				int advancementsToMax = (65 - preservationPercent) / 3;
				benefits.append(" (+" + advancementsToMax + " advancements to reach 65% max)");
			}
		}

		if (advancements == 0) {
			benefits.append("@red@No advancement benefits yet. Advance at level 420 to unlock permanent perks!");
		}

		return benefits.toString();
	}
}