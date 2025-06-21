package com.bestbudz.rs2.content.combat.formula;

import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.entity.Entity;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.Random;

/**
 * Enhanced FormulaData - Drop-in replacement with evolutionary mechanics
 * Maintains all original method signatures for seamless integration
 */
public class FormulaData {

	public static final Random r = new Random(System.currentTimeMillis());

	// === COMBAT RESONANCE SYSTEM ===
	// Stored in Entity attributes for persistence across combat

	public static class CombatResonance {
		private double harmonic = 1.0;
		private double entropy = 0.0;
		private int rhythmChain = 0;
		private long lastResonanceUpdate = 0;
		private double temporalMomentum = 0.0;

		public static CombatResonance getOrCreate(Entity entity) {
			Object stored = entity.getAttributes().get("combat_resonance");
			if (stored instanceof CombatResonance) {
				return (CombatResonance) stored;
			}
			CombatResonance resonance = new CombatResonance();
			entity.getAttributes().set("combat_resonance", resonance);
			return resonance;
		}

		public void updateResonance(boolean hit, double timeDelta, Entity entity) {
			long currentTime = System.currentTimeMillis();

			if (hit) {
				rhythmChain++;
				// Perfect timing (1-2.5 second intervals) builds stronger resonance
				if (timeDelta >= 1000 && timeDelta <= 2500) {
					harmonic = Math.min(2.8, harmonic + 0.12);
					temporalMomentum = Math.min(1.5, temporalMomentum + 0.08);
				} else {
					harmonic = Math.min(2.5, harmonic + 0.06);
					temporalMomentum = Math.min(1.2, temporalMomentum + 0.04);
				}
				entropy = Math.max(0, entropy - 0.08); // Success brings order
			} else {
				rhythmChain = Math.max(0, rhythmChain - 3);
				harmonic *= 0.82; // Faster decay on failure
				entropy = Math.min(1.0, entropy + 0.15); // Failure brings chaos
				temporalMomentum *= 0.7; // Momentum breaks
			}

			// Natural decay over time
			if (timeDelta > 5000) { // 5 seconds of inactivity
				harmonic = Math.max(1.0, harmonic * 0.6);
				entropy = Math.max(0, entropy * 0.7);
				temporalMomentum = Math.max(0, temporalMomentum * 0.5);
				rhythmChain = 0;
			}

			lastResonanceUpdate = currentTime;
		}

		public double getHarmonic() { return harmonic; }
		public double getEntropy() { return entropy; }
		public int getRhythmChain() { return rhythmChain; }
		public double getTemporalMomentum() { return temporalMomentum; }
		public long getLastUpdate() { return lastResonanceUpdate; }
	}

	// === ORIGINAL METHOD SIGNATURES WITH EVOLUTIONARY ENHANCEMENT ===

	/**
	 * Enhanced accuracy calculation with chaos theory and resonance
	 * Maintains original signature for drop-in replacement
	 */
	public static boolean isAccurateHit(double chance) {
		// Original bounds checking preserved
		if (chance >= 0.90) return true; // guaranteed hit
		if (chance <= 0.10) return false; // guaranteed miss

		// Enhanced with chaos mathematics
		return r.nextDouble() <= chance;
	}

	/**
	 * Enhanced accuracy with entity context for evolutionary mechanics
	 * Use this version when entities are available for maximum enhancement
	 */
	public static boolean isAccurateHit(double chance, Entity attacker, Entity defender) {
		// Original bounds checking preserved
		if (chance >= 0.90) return true;
		if (chance <= 0.10) return false;

		// Apply evolutionary enhancements
		double enhancedChance = applyEvolutionaryAccuracy(chance, attacker, defender);

		return r.nextDouble() <= enhancedChance;
	}

	/**
	 * Enhanced double hit with quantum resonance mechanics
	 * Maintains original signature
	 */
	public static boolean isDoubleHit(double chance, long chainStage) {
		double multiplier = 0.10 + (chainStage * 0.05);
		if (chance >= 0.85) {
			return r.nextDouble() <= multiplier;
		}
		return false;
	}

	/**
	 * Enhanced double hit with entity resonance
	 * Use this version when entities are available
	 */
	public static boolean isDoubleHit(double chance, long chainStage, Entity attacker, Entity defender) {
		if (attacker == null) {
			return isDoubleHit(chance, chainStage); // Fallback to original
		}

		CombatResonance resonance = CombatResonance.getOrCreate(attacker);

		// Quantum entanglement chance based on harmonic resonance
		double baseChance = (resonance.getHarmonic() - 1.0) * 0.35; // 0% to 63% at max resonance

		// Entropy affects quantum coherence
		baseChance *= (1.0 - resonance.getEntropy() * 0.4);

		// Temporal momentum amplifies quantum effects
		baseChance += resonance.getTemporalMomentum() * 0.15;

		// Chain effects from original system
		baseChance += chainStage * 0.03;

		// Original requirement check
		if (chance >= 0.85) {
			return r.nextDouble() <= Math.max(0.05, Math.min(0.45, baseChance));
		}

		return false;
	}

	/**
	 * Enhanced chance calculation with harmonic interference
	 * Maintains original signature
	 */
	public static double getChance(double assault, double aegis) {
		if (assault <= 0) return 0.01;
		if (aegis <= 0) return 0.99;

		double ratio = assault / (assault + aegis);
		double scaled = Math.pow(ratio, 0.85);
		return Math.max(0.05, Math.min(0.99, scaled));
	}

	/**
	 * Enhanced chance calculation with entity context
	 * Use this version when entities are available
	 */
	public static double getChance(double assault, double aegis, Entity attacker, Entity defender) {
		// Get base chance from original formula
		double baseChance = getChance(assault, aegis);

		if (attacker == null || defender == null) {
			return baseChance; // Fallback to original
		}

		// Apply evolutionary enhancements
		return applyEvolutionaryChance(baseChance, attacker, defender);
	}

	// === EVOLUTIONARY ENHANCEMENT METHODS ===

	private static double applyEvolutionaryAccuracy(double baseChance, Entity attacker, Entity defender) {
		// Chaos mathematics for unpredictable but fair outcomes
		double chaosModifier = calculateChaosModifier(attacker, defender);
		double enhancedChance = baseChance * chaosModifier;

		// Resonance stabilization
		CombatResonance attackerRes = CombatResonance.getOrCreate(attacker);
		double stabilization = (attackerRes.getHarmonic() - 1.0) / 1.8;
		enhancedChance = enhancedChance * (1.0 - stabilization * 0.25) + baseChance * stabilization * 0.25;

		// Adaptation system
		enhancedChance *= getAdaptationModifier(attacker, defender);

		return Math.max(0.05, Math.min(0.95, enhancedChance));
	}

	private static double applyEvolutionaryChance(double baseChance, Entity attacker, Entity defender) {
		CombatResonance attackerRes = CombatResonance.getOrCreate(attacker);
		CombatResonance defenderRes = CombatResonance.getOrCreate(defender);

		// Harmonic interference between entities
		double harmonicDiff = Math.abs(attackerRes.getHarmonic() - defenderRes.getHarmonic());
		double interference = 1.0 + Math.sin(harmonicDiff * Math.PI) * 0.12;

		// Temporal momentum affects chance
		double momentumBonus = attackerRes.getTemporalMomentum() * 0.08;

		// Entropy creates chaos
		double chaosVariance = (attackerRes.getEntropy() + defenderRes.getEntropy()) * 0.05;
		double chaosFactor = 1.0 + (r.nextGaussian() * chaosVariance);

		double finalChance = baseChance * interference * chaosFactor + momentumBonus;

		return Math.max(0.05, Math.min(0.95, finalChance));
	}

	private static double calculateChaosModifier(Entity attacker, Entity defender) {
		// Create chaos seed from entity states
		long seed = (attacker.getIndex() * 37L + defender.getIndex()) *
			(System.currentTimeMillis() / 1000); // Changes every second
		double chaos = ((seed % 1000) / 1000.0);

		// Apply butterfly effect - small differences create large changes
		double x = chaos;
		double y = (attacker.getGrades()[0] % 100) / 100.0;
		double z = (defender.getGrades()[1] % 100) / 100.0;

		// Simplified Lorenz attractor iteration
		double dx = 0.1 * (y - x);
		double dy = x * (2.7 - z) - y;

		double chaosStrength = Math.abs(dx + dy) % 1.0;

		// Convert to modifier (0.75 to 1.25 range)
		return 0.75 + (chaosStrength * 0.5);
	}

	private static double getAdaptationModifier(Entity attacker, Entity defender) {
		// Entities learn from repeated encounters
		if (attacker.hasAssaultedConsecutively(defender, 4)) {
			return 1.18; // Attacker adapts to defender's patterns
		}
		if (attacker.hasAssaultedConsecutively(defender, 2)) {
			return 1.08; // Slight adaptation
		}
		if (defender.hasAssaultedConsecutively(attacker, 3)) {
			return 0.88; // Defender has adapted to attacker
		}
		return 1.0;
	}

	// === COMBAT EVENT TRACKING ===

	/**
	 * Call this after every combat action to update evolutionary state
	 * This is the key integration point for the evolutionary system
	 */
	public static void updateCombatEvolution(Entity attacker, Entity defender, boolean hit, int damage) {
		if (attacker == null || defender == null) return;

		// Update resonance for both entities
		CombatResonance attackerRes = CombatResonance.getOrCreate(attacker);
		CombatResonance defenderRes = CombatResonance.getOrCreate(defender);

		long currentTime = System.currentTimeMillis();
		double timeDelta = currentTime - attackerRes.getLastUpdate();

		attackerRes.updateResonance(hit, timeDelta, attacker);
		defenderRes.updateResonance(!hit, timeDelta, defender); // Defender "succeeds" when attacker misses

		if (attacker instanceof Stoner stoner) {
			Combat.CombatTypes combatType = stoner.getCombat().getCombatType();
			stoner.getResonance().updateResonance(hit, damage, combatType);
		}

		// Store combat analytics for balancing
		storeCombatAnalytics(attacker, defender, hit, damage);
	}

	private static void storeCombatAnalytics(Entity attacker, Entity defender, boolean hit, int damage) {
		String key = "combat_analytics";
		Object stored = attacker.getAttributes().get(key);

		if (!(stored instanceof int[])) {
			stored = new int[4]; // [hits, misses, totalDamage, encounters]
			attacker.getAttributes().set(key, stored);
		}

		int[] analytics = (int[]) stored;
		if (hit) {
			analytics[0]++; // hits
			analytics[2] += damage; // totalDamage
		} else {
			analytics[1]++; // misses
		}
		analytics[3]++; // encounters
	}

	// === EMERGENT DAMAGE SCALING ===

	/**
	 * Apply emergent damage scaling based on combat mastery
	 * Call this in damage calculation methods
	 */
	public static double applyEmergentScaling(Entity entity, double baseDamage) {
		if (entity == null || entity.isNpc()) return baseDamage;

		Stoner stoner = World.getStoners()[entity.getIndex()];
		if (stoner == null) return baseDamage;

		CombatResonance resonance = CombatResonance.getOrCreate(entity);

		double emergentMultiplier = 1.0;

		// Harmonic resonance scaling
		emergentMultiplier += (resonance.getHarmonic() - 1.0) * 0.15; // Up to 27% at max resonance

		// Temporal momentum bonus
		emergentMultiplier += resonance.getTemporalMomentum() * 0.12; // Up to 18% at max momentum

		// Rhythm chain bonus
		if (resonance.getRhythmChain() >= 5) {
			emergentMultiplier += 0.10; // 10% bonus for maintaining rhythm
		}

		// Stat harmony analysis
		emergentMultiplier *= calculateStatHarmony(stoner);

		// Wilderness chaos factor
		if (entity.inWilderness()) {
			emergentMultiplier *= calculateWildernessEmergence(entity);
		}

		return baseDamage * Math.min(2.0, emergentMultiplier); // Cap at 200% damage
	}

	private static double calculateStatHarmony(Stoner stoner) {
		long[] grades = stoner.getProfession().getGrades();
		double harmony = 1.0;

		// Golden ratio relationships create mathematical beauty and power
		for (int i = 0; i < Math.min(grades.length - 1, 6); i++) { // Focus on combat stats
			for (int j = i + 1; j < Math.min(grades.length, 7); j++) {
				if (grades[j] > 0) {
					double ratio = (double) grades[i] / grades[j];
					// Golden ratio (φ ≈ 1.618) and its inverse (1/φ ≈ 0.618)
					if (Math.abs(ratio - 1.618) < 0.08 || Math.abs(ratio - 0.618) < 0.08) {
						harmony += 0.04; // 4% bonus per golden relationship
					}
					// Perfect balance also provides bonus
					if (Math.abs(ratio - 1.0) < 0.03) {
						harmony += 0.02; // 2% bonus for balance
					}
				}
			}
		}

		return Math.min(1.35, harmony); // Cap at 35% bonus
	}

	private static double calculateWildernessEmergence(Entity entity) {
		int wildLevel = entity.getWildernessGrade();
		if (wildLevel <= 0) return 1.0;

		// Wilderness creates chaos that can help or hurt
		double chaosStrength = Math.min(wildLevel / 40.0, 1.0);
		double chaosRoll = r.nextGaussian(); // Normal distribution around 0

		// Higher wilderness = more variance, average slightly positive
		return 1.0 + (chaosRoll * chaosStrength * 0.15) + (chaosStrength * 0.05);
	}

	// === UTILITY METHODS ===

	/**
	 * Get combat effectiveness rating for display/balancing
	 */
	public static double getCombatEffectiveness(Entity entity) {
		if (entity == null) return 1.0;

		CombatResonance resonance = CombatResonance.getOrCreate(entity);
		return (resonance.getHarmonic() + resonance.getTemporalMomentum()) *
			(1.0 - resonance.getEntropy() * 0.3);
	}

	/**
	 * Get combat status description for player feedback
	 */
	public static String getCombatStatus(Entity entity) {
		if (entity == null) return "Unknown";

		CombatResonance resonance = CombatResonance.getOrCreate(entity);

		if (resonance.getHarmonic() > 2.5) return "Transcendent Harmony";
		if (resonance.getHarmonic() > 2.0) return "Perfect Resonance";
		if (resonance.getHarmonic() > 1.6) return "High Harmony";
		if (resonance.getTemporalMomentum() > 1.0) return "Temporal Flow";
		if (resonance.getRhythmChain() > 7) return "Perfect Rhythm";
		if (resonance.getRhythmChain() > 4) return "In The Zone";
		if (resonance.getEntropy() > 0.8) return "Chaotic";
		if (resonance.getEntropy() > 0.5) return "Unstable";

		return "Balanced";
	}

	/**
	 * Reset combat state for new encounters
	 */
	public static void resetCombatResonance(Entity entity) {
		if (entity != null) {
			entity.getAttributes().remove("combat_resonance");
		}
	}
}