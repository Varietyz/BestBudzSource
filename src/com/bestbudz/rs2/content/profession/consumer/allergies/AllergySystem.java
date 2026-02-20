package com.bestbudz.rs2.content.profession.consumer.allergies;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.content.combat.Hit;
import com.bestbudz.rs2.content.profession.Professions;
import java.util.*;

public class AllergySystem {

	public enum AllergyType {
		SEAFOOD(Arrays.asList(
			315, 319, 325, 329, 333, 339, 347, 351, 355, 361, 365, 373, 379, 385, 391, 397,
			7946, 14825, 14831, 15266, 11936, 13433,
			2297, 2299, 7188, 7190, 7198, 7200, 7068
		), "You feel nauseous from the seafood!", Hit.HitTypes.POISON),

		DAIRY(Arrays.asList(
			6703, 6705, 1891, 1893, 1895, 1897, 1899, 1901,
			2289, 2291, 2293, 2295, 2301
		), "Your stomach churns from the dairy!", Hit.HitTypes.NONE),

		GRAINS(Arrays.asList(
			2309, 2289, 2291, 2293, 2295, 2297, 2299, 2301,
			2323, 2325, 2327, 2331, 2333, 2335, 7178, 7180
		), "The grains make you feel bloated and sick!", Hit.HitTypes.DISEASE),

		FRUITS(Arrays.asList(
			1963, 6883, 2323, 2325, 2335, 7178, 7180, 2301,
			4561, 10476
		), "You break out in hives from the fruit!", Hit.HitTypes.NONE),

		MEAT(Arrays.asList(
			2140, 2142, 4291, 4293, 2293, 2295, 2327, 2331,
			7062, 7064
		), "The meat makes you violently ill!", Hit.HitTypes.POISON),

		HERBS_POTIONS(Arrays.asList(
			2446, 175, 177, 179, 3032, 3034, 3036, 3038,
			3040, 3042, 3044, 3046, 6685, 6687, 6689, 6691
		), "Your body rejects the herbal compounds!", Hit.HitTypes.NONE),

		VEGETABLES(Arrays.asList(
			6701, 6703, 6705, 7054, 7056, 7060,
			7062, 7064, 7068, 7072, 7078, 7082, 7084,
			7178, 7180
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

	private static final int MAX_RESISTANCE = 20000;
	private static final int MAX_LEVEL = 420;

	public AllergySystem(Stoner stoner) {
		this.stoner = stoner;

	}

	public int getConsumerLevel() {
		return (int) stoner.getMaxGrades()[Professions.CONSUMER];
	}

	public void onLogin() {
		ensureAllergiesInitialized();

	}

	public void saveProgress() {

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

		Random rand = new Random(stoner.getUsername().hashCode());
		int allergyCount = 2;

		List<AllergyType> allAllergies = Arrays.asList(AllergyType.values());
		Collections.shuffle(allAllergies, rand);

		for (int i = 0; i < allergyCount && i < allAllergies.size(); i++) {
			AllergyType allergy = allAllergies.get(i);
			allergies.add(allergy);
			resistance.put(allergy, 0);
		}

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

		double triggerChance = 1.0;

		triggerChance -= (consumerLevel / (MAX_LEVEL * 2.0)) * 0.5;

		triggerChance -= (currentResistance / (double) MAX_RESISTANCE);

		if (currentResistance < MAX_RESISTANCE) {
			triggerChance = Math.max(0.1, triggerChance);
		} else {
			triggerChance = 0;
		}

		return Math.random() < triggerChance;
	}

	public void applyAllergyReaction(AllergyType allergy, int originalHeal) {

		com.bestbudz.core.task.TaskQueue.queue(
			new com.bestbudz.core.task.Task(
				stoner,
				1,
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

	private void performAllergyReaction(AllergyType allergy, int originalHeal) {
		int consumerLevel = getConsumerLevel();
		int severity = calculateSeverity(allergy, consumerLevel);

		stoner.send(new SendMessage("@red@" + allergy.getReactionMessage()));

		int damage = calculateAllergyDamage(severity, originalHeal);

		Hit allergyHit = new Hit(damage, allergy.getHitType());
		stoner.hit(allergyHit);

		int animationId = getReactionAnimation(severity);
		if (animationId > 0) {
			stoner.getUpdateFlags().sendAnimation(animationId, 0);
		}

		stoner.getUpdateFlags().setUpdateRequired(true);
		stoner.getUpdateFlags().setHitUpdate(true);
		stoner.getUpdateFlags().setDamage(damage);
		stoner.getUpdateFlags().setHitType(allergyHit.getHitType());
		stoner.getUpdateFlags().setHitUpdateCombatType(allergyHit.getCombatHitType());

		switch (severity) {
			case 1:
				stoner.send(new SendMessage("@yel@You feel slightly unwell..."));
				break;

			case 2:
				stoner.send(new SendMessage("@ora@Your reaction is getting worse!"));

				stoner.getProfession().deductFromGrade(0, 5);
				stoner.getProfession().deductFromGrade(2, 5);
				break;

			case 3:
				stoner.send(new SendMessage("@red@You're tweaking due to a severe allergic reaction!"));

				stoner.getProfession().deductFromGrade(0, 10);
				stoner.getProfession().deductFromGrade(1, 10);
				stoner.getProfession().deductFromGrade(2, 10);
				break;
		}

		applyStatusEffects(allergy, severity);

		stoner.getProfession().update();
		stoner.setAppearanceUpdateRequired(true);
	}

	private int getReactionAnimation(int severity) {
		switch (severity) {
			case 1:
				return 404;
			case 2:
				return 420;
			case 3:
				return 4965;
			default:
				return 0;
		}
	}

	private int calculateAllergyDamage(int severity, int originalHeal) {
		switch (severity) {
			case 1:
				return Math.max(3, originalHeal / 4);
			case 2:
				return Math.max(7, originalHeal / 2);
			case 3:
				return Math.max(15, originalHeal);
			default:
				return 1;
		}
	}

	private void applyStatusEffects(AllergyType allergy, int severity) {
		switch (allergy.getHitType()) {
			case POISON:

				try {

					if (severity >= 2) {

						stoner.send(new SendMessage("@gre@The " + allergy.name().toLowerCase().replace("_", " ") +
							" has poisoned you!"));
					}
				} catch (Exception e) {

				}
				break;
			case DISEASE:

				if (severity >= 2) {
					applyDiseaseEffect(severity);
				}
				break;
			case DEFLECT:

				break;
		}
	}

	private void applyDiseaseEffect(int severity) {

		int statDrain = severity;

		stoner.getProfession().deductFromGrade(3, statDrain);
		stoner.getProfession().deductFromGrade(4, statDrain);

		stoner.send(new SendMessage("@red@The allergic reaction saps your vitality!"));
	}

	private int calculateSeverity(AllergyType allergy, int consumerLevel) {
		int currentResistance = resistance.getOrDefault(allergy, 0);
		int advancements = stoner.getProfessionAdvances()[Professions.CONSUMER];

		int severity = 3;

		if (advancements >= 2 || consumerLevel >= 252 || currentResistance >= 10000) severity = 1;
		else if (advancements >= 1 || consumerLevel >= 126 || currentResistance >= 5000) severity = 2;

		return severity;
	}

	public void handleAllergyExposure(AllergyType allergy) {
		int consumerLevel = getConsumerLevel();
		int advancements = stoner.getProfessionAdvances()[Professions.CONSUMER];

		if (!canBuildResistance(consumerLevel, advancements)) {
			stoner.send(new SendMessage("@yel@Your Consumer level is too low to build resistance. (Need level 84 or 1+ advancement)"));
			return;
		}

		int currentResistance = resistance.getOrDefault(allergy, 0);

		if (currentResistance < MAX_RESISTANCE) {
			int resistanceGain = calculateResistanceGain(consumerLevel, advancements);

			resistance.put(allergy, Math.min(MAX_RESISTANCE, currentResistance + resistanceGain));

			stoner.send(new SendMessage("@gre@You feel slightly more resistant to " +
				allergy.name().toLowerCase().replace("_", " ") + ". (" +
				resistance.get(allergy) + "/" + MAX_RESISTANCE + ")"));

			saveProgress();

			if (resistance.get(allergy) >= MAX_RESISTANCE &&
				stoner.getProfessionAdvances()[Professions.CONSUMER] >= 1) {
				allergies.remove(allergy);
				stoner.send(new SendMessage("@gre@You have completely overcome your " +
					allergy.name().toLowerCase().replace("_", " ") + " allergy!"));
				stoner.getUpdateFlags().sendGraphic(Professions.UPGRADE_GRAPHIC);

				saveProgress();
			}
		}
	}

	public boolean applyConsumerMastery(int originalHeal, int consumerLevel) {
		int advancements = stoner.getProfessionAdvances()[Professions.CONSUMER];

		if (advancements < 2 && consumerLevel < 168) return false;

		double masteryChance = 0.0;

		if (advancements >= 2) {

			masteryChance = 0.10 + (consumerLevel / 420.0) * 0.20;
		} else if (consumerLevel >= 168) {

			masteryChance = (consumerLevel - 168) / (420.0 - 168.0) * 0.3;
		}

		if (Math.random() < masteryChance) {
			int bonusHeal = (int) (originalHeal * 0.5);
			long currentHealth = stoner.getProfession().getGrades()[3];
			long maxHealth = stoner.getMaxGrades()[3];
			long newHealth = Math.min(maxHealth, currentHealth + bonusHeal);
			stoner.getProfession().setGrade(3, newHealth);
			stoner.send(new SendMessage("@gre@Your Consumer mastery enhances the effect!"));
			return true;
		}

		return false;
	}

	public boolean shouldPreserveItem(int consumerLevel) {
		int advancements = stoner.getProfessionAdvances()[Professions.CONSUMER];

		if (advancements < 1 && consumerLevel < 252) return false;

		double preserveChance = 0.0;

		if (advancements >= 1) {

			double advancementBonus = Math.min(0.65, advancements * 0.03);

			double levelBonus = (consumerLevel / 420.0) * 0.05;

			preserveChance = advancementBonus + levelBonus;
		} else if (consumerLevel >= 252) {

			preserveChance = (consumerLevel - 252) / (420.0 - 252.0) * 0.1;
		}

		preserveChance = Math.min(0.70, preserveChance);

		if (Math.random() < preserveChance) {
			stoner.send(new SendMessage("@gre@Your efficient consumption preserves the item! (" +
				String.format("%.1f", preserveChance * 100) + "% chance)"));
			return true;
		}

		return false;
	}

	public Set<AllergyType> getAllergies() {
		ensureAllergiesInitialized();
		return new HashSet<>(allergies);
	}

	public Map<AllergyType, Integer> getResistance() {
		ensureAllergiesInitialized();
		return new HashMap<>(resistance);
	}

	public void setResistance(Map<AllergyType, Integer> resistance) {
		this.resistance = resistance;
	}

	public static int getMaxResistance() {
		return MAX_RESISTANCE;
	}

	private boolean canBuildResistance(int consumerLevel, int advancements) {
		return advancements >= 1 || consumerLevel >= 84;
	}

	private int calculateResistanceGain(int consumerLevel, int advancements) {

		int baseGain = 1;

		if (advancements >= 1) baseGain += 1;
		if (advancements >= 2) baseGain += 1;
		if (advancements >= 3) baseGain += 1;
		if (advancements >= 4) baseGain += 1;
		if (advancements >= 5) baseGain += 2;

		int levelBonus = consumerLevel / 105;

		return baseGain + levelBonus;
	}

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

		for (int i = 4; i <= Math.min(advancements, 21); i++) {
			benefits.append("@gre@Tier " + i + ": +1 more resistance gain\n");
			benefits.append("@gre@       Item preservation: " + (i * 3) + "% chance\n");
		}

		if (advancements >= 22) {
			benefits.append("@gre@Tier 22+: +1 more resistance gain\n");
			benefits.append("@gre@        Item preservation: 65% chance (MAX)\n");
		}

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
