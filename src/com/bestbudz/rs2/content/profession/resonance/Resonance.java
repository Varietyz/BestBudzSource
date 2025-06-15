package com.bestbudz.rs2.content.profession.resonance;

import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.HashMap;
import java.util.Map;

/**
 * ENHANCED RESONANCE SKILL SYSTEM
 *
 * A pure passive combat skill that evolves with your fighting prowess.
 * Gains experience through successful combat mechanics, perfect timing,
 * and cross-style mastery without any manual training methods.
 */
public class Resonance {

	// Core resonance data
	private final Stoner stoner;
	private final Map<Combat.CombatTypes, CombatStyleMastery> styleMastery = new HashMap<>();

	// Passive resonance tracking
	private static double currentResonance = 0.0;
	private Combat.CombatTypes lastCombatStyle = null;
	private long lastStyleSwitch = 0;
	private static int perfectTimingStreak = 0;
	private long lastPerfectHit = 0;
	private double harmonicFlow = 1.0;

	// Combat rhythm tracking
	private long lastHitTime = 0;
	private int rhythmChain = 0;
	private double combatMastery = 1.0;

	// Constants for passive progression
	private static final double MAX_RESONANCE = 100.0;
	private static final double RESONANCE_DECAY_RATE = 0.3;
	private static final long PERFECT_TIMING_WINDOW = 1200; // milliseconds
	private static final long STYLE_SWITCH_BONUS_WINDOW = 4000; // 4 seconds
	private static final double CROSS_STYLE_MULTIPLIER = 1.8;
	private static final double MASTERY_GROWTH_RATE = 0.02;
	private boolean wasTranscendentLastCheck = false;

	public Resonance(Stoner stoner) {
		this.stoner = stoner;
		initializeStyleMastery();
	}

	private void initializeStyleMastery() {
		for (Combat.CombatTypes type : Combat.CombatTypes.values()) {
			if (type != Combat.CombatTypes.NONE) {
				styleMastery.put(type, new CombatStyleMastery());
			}
		}
	}

	/**
	 * Main passive resonance update - called from combat systems
	 * This is the core integration point with your existing combat
	 */
	public void updateResonance(boolean hitSuccessful, int damage, Combat.CombatTypes combatType) {
		if (combatType == Combat.CombatTypes.NONE) return;

		long currentTime = System.currentTimeMillis();
		CombatStyleMastery mastery = styleMastery.get(combatType);

		// Analyze combat performance
		CombatPerformance performance = analyzeCombatPerformance(
			hitSuccessful, damage, combatType, currentTime
		);

		// Update style-specific mastery
		updateStyleMastery(mastery, performance, hitSuccessful);

		// Calculate passive experience gain
		double baseExp = calculatePassiveExperience(performance, mastery);

		// Award resonance experience based on combat excellence
		if (baseExp > 0) {
			awardResonanceExperience(baseExp, performance);
		}

		// Update resonance state for bonuses
		updateResonanceState(performance, currentTime);

		// Provide subtle feedback
		sendResonanceFeedback(performance, baseExp);
	}

	/**
	 * Analyze the quality of the combat action for experience calculation
	 */
	private CombatPerformance analyzeCombatPerformance(boolean hit, int damage,
													   Combat.CombatTypes type, long currentTime) {
		CombatPerformance performance = new CombatPerformance();

		// Basic hit assessment
		performance.wasSuccessful = hit;
		performance.damage = damage;
		performance.combatType = type;

		// Perfect timing detection (based on weapon speed and rhythm)
		performance.perfectTiming = checkPerfectTiming(type, currentTime);

		// Cross-style fluidity bonus
		performance.crossStyleBonus = checkCrossStyleFluidity(type, currentTime);

		// Combat rhythm analysis
		performance.rhythmQuality = analyzeRhythm(currentTime);

		// High damage execution bonus
		performance.executionQuality = analyzeExecution(damage, type);

		// Combat flow state
		performance.flowState = analyzeFlowState();

		return performance;
	}

	private boolean checkPerfectTiming(Combat.CombatTypes combatType, long currentTime) {
		if (lastHitTime == 0) return false;

		long timeDelta = currentTime - lastHitTime;
		int expectedInterval = getOptimalInterval(combatType);

		// Perfect timing window - varies by weapon type
		boolean perfect = Math.abs(timeDelta - expectedInterval) <= PERFECT_TIMING_WINDOW;

		if (perfect) {
			perfectTimingStreak++;
		} else {
			perfectTimingStreak = Math.max(0, perfectTimingStreak - 1);
		}

		lastHitTime = currentTime;
		return perfect;
	}

	private int getOptimalInterval(Combat.CombatTypes combatType) {
		// Base intervals optimized for each combat style
		switch (combatType) {
			case MELEE: return 2200;   // 3.67 ticks
			case SAGITTARIUS: return 2800; // 4.67 ticks
			case MAGE: return 3400;    // 5.67 ticks
			default: return 2800;
		}
	}

	private boolean checkCrossStyleFluidity(Combat.CombatTypes combatType, long currentTime) {
		if (lastCombatStyle == null || lastCombatStyle == combatType) {
			lastCombatStyle = combatType;
			return false;
		}

		boolean withinWindow = (currentTime - lastStyleSwitch) <= STYLE_SWITCH_BONUS_WINDOW;

		if (lastCombatStyle != combatType) {
			lastCombatStyle = combatType;
			lastStyleSwitch = currentTime;
		}

		return withinWindow;
	}

	private double analyzeRhythm(long currentTime) {
		if (lastHitTime > 0) {
			long interval = currentTime - lastHitTime;

			// Consistent rhythm builds chain
			if (interval >= 1500 && interval <= 4000) {
				rhythmChain = Math.min(rhythmChain + 1, 20);
				return 1.0 + (rhythmChain * 0.05); // Up to 2.0x for perfect rhythm
			} else {
				rhythmChain = Math.max(0, rhythmChain - 2);
			}
		}

		return 1.0 + (rhythmChain * 0.02);
	}

	private double analyzeExecution(int damage, Combat.CombatTypes type) {
		if (damage <= 0) return 0.5;

		// Get theoretical max hit for comparison
		int maxPossible = stoner.getMaxHit(type);
		if (maxPossible <= 0) return 1.0;

		double damageRatio = (double) damage / maxPossible;

		// High damage execution gets bonus
		if (damageRatio >= 0.8) return 2.0;      // Excellent execution
		if (damageRatio >= 0.6) return 1.5;      // Good execution
		if (damageRatio >= 0.4) return 1.2;      // Average execution

		return 1.0;
	}

	private double analyzeFlowState() {
		// Flow state based on current performance metrics
		double flow = 1.0;

		if (perfectTimingStreak >= 10) flow += 0.5;  // In the zone
		if (rhythmChain >= 8) flow += 0.3;           // Rhythmic mastery
		if (currentResonance >= 75.0) flow += 0.2;   // High resonance state

		return Math.min(flow, 2.5);
	}

	private void updateStyleMastery(CombatStyleMastery mastery, CombatPerformance performance, boolean hit) {
		if (hit) {
			mastery.successfulHits++;
			mastery.totalDamage += performance.damage;

			if (performance.perfectTiming) {
				mastery.perfectHits++;
			}

			// Gradual mastery improvement
			mastery.masteryLevel = Math.min(2.5,
				mastery.masteryLevel + MASTERY_GROWTH_RATE * performance.rhythmQuality);
		}

		mastery.totalAttempts++;
	}

	/**
	 * Calculate passive experience based on combat performance
	 */
	private double calculatePassiveExperience(CombatPerformance performance, CombatStyleMastery mastery) {
		if (!performance.wasSuccessful) {
			// Small experience for learning from failures
			return 2.0 * performance.rhythmQuality;
		}

		// Base experience from damage dealt (logarithmic scaling)
		double baseExp = Math.log(performance.damage + 1) * 8.0;

		// Performance multipliers
		double multiplier = 1.0;

		// Perfect timing mastery
		if (performance.perfectTiming) {
			multiplier *= 2.2;
			if (perfectTimingStreak >= 5) {
				multiplier *= 1.0 + (perfectTimingStreak * 0.08); // Streak bonus
			}
		}

		// Cross-style fluidity bonus
		if (performance.crossStyleBonus) {
			multiplier *= CROSS_STYLE_MULTIPLIER;
		}

		// Combat rhythm bonus
		multiplier *= performance.rhythmQuality;

		// Execution quality bonus
		multiplier *= performance.executionQuality;

		// Flow state amplification
		multiplier *= performance.flowState;

		// Style mastery bonus
		multiplier *= mastery.masteryLevel;

		// High-level progression scaling
		long resonanceLevel = stoner.getGrades()[Professions.RESONANCE];
		if (resonanceLevel >= 80) {
			multiplier *= 0.7; // Slower progression at high levels
		} else if (resonanceLevel >= 50) {
			multiplier *= 0.85;
		}

		return Math.min(baseExp * multiplier, 150.0); // Cap per-hit experience
	}

	private void awardResonanceExperience(double baseExp, CombatPerformance performance) {
		if (baseExp <= 0) return;

		// Award to resonance skill
		double finalExp = stoner.getProfession().addExperience(Professions.RESONANCE, baseExp);

		// Update current resonance energy
		double resonanceGain = baseExp / 20.0;
		currentResonance = Math.min(currentResonance + resonanceGain, MAX_RESONANCE);

		// Improve harmonic flow with excellent performance
		if (performance.perfectTiming && performance.crossStyleBonus) {
			harmonicFlow = Math.min(harmonicFlow + 0.02, 2.0);
		}

		// Improve combat mastery gradually
		combatMastery = Math.min(combatMastery + 0.001, 1.5);
	}

	private void updateResonanceState(CombatPerformance performance, long currentTime) {
		// Track last perfect hit for streak management
		if (performance.perfectTiming) {
			lastPerfectHit = currentTime;
		}

		// Natural resonance decay
		if (currentTime - lastPerfectHit > 8000) { // 8 seconds without perfect hit
			currentResonance = Math.max(0, currentResonance - RESONANCE_DECAY_RATE);
			harmonicFlow = Math.max(1.0, harmonicFlow - 0.01);
		}
	}

	// ============================================================================
	// SUPER SIMPLE MESSAGE SYSTEM - Easy to modify!
	// ============================================================================

	// Message consolidation to prevent spam
	private static String lastMessageType = null;
	private static double consolidatedExp = 0.0;
	private static int messageCount = 0;
	private long lastMessageTime = 0;
	private static final long MESSAGE_CONSOLIDATION_WINDOW = 500; // 0.5 seconds

	/**
	 * Clean, maintainable feedback system with spam prevention
	 */
	private void sendResonanceFeedback(CombatPerformance performance, double expGained) {
		if (expGained <= 1.0) return;

		// Get the message type
		String messageType = getResonanceMessageType(performance);
		if (messageType != null) {
			consolidateAndSendMessage(messageType, expGained);
		}

		// Send milestone notifications
		checkAndSendMilestones();
		updateResonanceDisplay();
	}

	/**
	 * Consolidate identical messages within a time window
	 */
	private void consolidateAndSendMessage(String messageType, double expGained) {
		long currentTime = System.currentTimeMillis();

		// If same message type within consolidation window, accumulate
		if (messageType.equals(lastMessageType) &&
			(currentTime - lastMessageTime) <= MESSAGE_CONSOLIDATION_WINDOW) {

			consolidatedExp += expGained;
			messageCount++;

		} else {
			// Send any pending consolidated message first
			if (lastMessageType != null && consolidatedExp > 0) {
				sendConsolidatedMessage(lastMessageType, consolidatedExp, messageCount);
			}

			// Start new consolidation
			lastMessageType = messageType;
			consolidatedExp = expGained;
			messageCount = 1;
			lastMessageTime = currentTime;
		}
	}

	/**
	 * Format a consolidated message with hit count
	 */
	private static String formatConsolidatedMessage(String template, double expGained, int hitCount) {
		String streakInfo = (perfectTimingStreak >= 3) ?
			String.format(" (%dx streak!)", perfectTimingStreak) : "";

		String resonanceInfo = (currentResonance >= 25.0) ?
			String.format(" (%.0f%% resonance)", currentResonance) : "";

		return String.format(template, expGained, hitCount, streakInfo, resonanceInfo);
	}

	/**
	 * Send consolidated message with proper formatting
	 */
	private void sendConsolidatedMessage(String messageType, double totalExp, int count) {
		String message;

		if (count > 1) {
			// Multiple hits - show total exp and hit count
			message = formatConsolidatedMessage(messageType + " %.1f total resonance experience (%dx hits)%s%s", totalExp, count);
		} else {
			// Single hit - normal format
			message = formatMessage(messageType + " %.1f resonance experience%s%s", totalExp);
		}

		sendMessage(message);
	}

	/**
	 * Flush any pending consolidated message (call this when combat ends)
	 */
	public void flushPendingMessage() {
		if (lastMessageType != null && consolidatedExp > 0) {
			sendConsolidatedMessage(lastMessageType, consolidatedExp, messageCount);
			lastMessageType = null;
			consolidatedExp = 0.0;
			messageCount = 0;
		}
	}

	/**
	 * Get the message type (without formatting) - super easy to modify!
	 */
	private String getResonanceMessageType(CombatPerformance performance) {
		// Transcendent state (highest priority)
		if (performance.perfectTiming && performance.crossStyleBonus && performance.flowState > 2.0) {
			return "TRANSCENDENT RESONANCE!";
		}

		// Harmonic convergence
		if (performance.perfectTiming && performance.crossStyleBonus) {
			return "Harmonic Convergence!";
		}

		// Perfect flow (long streak)
		if (perfectTimingStreak >= 8) {
			return "Perfect Flow!";
		}

		// Cross-style mastery
		if (performance.crossStyleBonus) {
			return "Cross-Style Mastery!";
		}

		// Perfect timing
		if (performance.perfectTiming) {
			return "Perfect Timing!";
		}

		// Good rhythm
		if (performance.rhythmQuality > 1.5) {
			return "Combat Rhythm!";
		}

		// High damage execution
		if (performance.executionQuality >= 2.0) {
			return "Devastating Strike!";
		}

		return null; // No special message needed
	}

	/**
	 * Get the complete message string - super easy to modify!
	 */
	private String getResonanceMessage(CombatPerformance performance, double expGained) {
		// Transcendent state (highest priority)
		if (performance.perfectTiming && performance.crossStyleBonus && performance.flowState > 2.0) {
			return formatMessage("TRANSCENDENT RESONANCE! %.1f resonance experience%s%s", expGained);
		}

		// Harmonic convergence
		if (performance.perfectTiming && performance.crossStyleBonus) {
			return formatMessage("Harmonic Convergence! %.1f resonance experience%s%s", expGained);
		}

		// Perfect flow (long streak)
		if (perfectTimingStreak >= 8) {
			return formatMessage("Perfect Flow! %.1f resonance experience%s%s", expGained);
		}

		// Cross-style mastery
		if (performance.crossStyleBonus) {
			return formatMessage("Cross-Style Mastery! %.1f resonance experience%s%s", expGained);
		}

		// Perfect timing
		if (performance.perfectTiming) {
			return formatMessage("Perfect Timing! %.1f resonance experience%s%s", expGained);
		}

		// Good rhythm
		if (performance.rhythmQuality > 1.5) {
			return formatMessage("Combat Rhythm! %.1f resonance experience%s%s", expGained);
		}

		// High damage execution
		if (performance.executionQuality >= 2.0) {
			return formatMessage("Devastating Strike! %.1f resonance experience%s%s", expGained);
		}

		return null; // No special message needed
	}

	/**
	 * Format a message with streak and resonance info - all in one place!
	 */
	private static String formatMessage(String template, double expGained) {
		String streakInfo = (perfectTimingStreak >= 3) ?
			String.format(" (%dx streak!@whi@)", perfectTimingStreak) : "";

		String resonanceInfo = (currentResonance >= 25.0) ?
			String.format(" (%.0f%% resonance)", currentResonance) : "";

		return String.format(template, expGained, streakInfo, resonanceInfo);
	}

	/**
	 * Send any message string
	 */
	private void sendMessage(String message) {
		stoner.getClient().queueOutgoingPacket(new SendMessage(message));
	}

	/**
	 * Check for and send milestone messages
	 */
	private void checkAndSendMilestones() {
		// Resonance level milestones
		if (currentResonance == 50.0) {
			sendMessage("RESONANCE MILESTONE: 50% - Combat efficiency increased!");
		} else if (currentResonance == 75.0) {
			sendMessage("RESONANCE MILESTONE: 75% - Approaching mastery!");
		} else if (currentResonance >= 95.0 && !wasTranscendentLastCheck) {
			sendMessage("TRANSCENDENT STATE ACHIEVED! Maximum combat resonance!");
			wasTranscendentLastCheck = true;
		}

		// Perfect timing streak milestones
		if (perfectTimingStreak == 5) {
			sendMessage("Perfect timing mastery developing! (+5 streak)");
		} else if (perfectTimingStreak == 10) {
			sendMessage("Incredible timing precision! (+10 streak)");
		} else if (perfectTimingStreak == 15) {
			sendMessage("LEGENDARY timing control! (+15 streak)");
		}

		// Rhythm chain milestones
		if (rhythmChain == 10) {
			sendMessage("Combat rhythm established! Flow bonus active!");
		} else if (rhythmChain == 20) {
			sendMessage("PERFECT COMBAT RHYTHM! Maximum flow achieved!");
		}
	}

	private void updateResonanceDisplay() {
		String resonanceText = String.format("Resonance: %.1f%%", currentResonance);
		stoner.getClient().queueOutgoingPacket(new SendString(resonanceText, 5608));
	}

	// ============================================================================
	// REST OF THE CLASS REMAINS THE SAME
	// ============================================================================

	/**
	 * Get current resonance percentage (0-100)
	 */
	public double getCurrentResonance() {
		return currentResonance;
	}

	/**
	 * Get passive damage bonus from resonance mastery
	 */
	public double getResonanceBonus() {
		double base = (currentResonance / MAX_RESONANCE) * 0.15; // Max 15% base bonus
		double mastery = (combatMastery - 1.0) * 0.1; // Up to 5% from mastery
		double flow = (harmonicFlow - 1.0) * 0.08; // Up to 8% from flow

		return base + mastery + flow; // Max ~28% damage bonus
	}

	/**
	 * Get accuracy bonus from combat resonance
	 */
	public double getAccuracyBonus() {
		double base = (currentResonance / MAX_RESONANCE) * 0.12; // Max 12% base
		double rhythm = (rhythmChain / 20.0) * 0.08; // Up to 8% from rhythm

		return base + rhythm; // Max ~20% accuracy bonus
	}

	/**
	 * Check if player is in transcendent state
	 */
	public boolean isTranscendent() {
		return currentResonance >= 95.0 &&
			perfectTimingStreak >= 10 &&
			harmonicFlow >= 1.8;
	}

	/**
	 * Decay resonance over time (called from Stoner.process())
	 */
	public void drain() {
		if (currentResonance > 0) {
			currentResonance = Math.max(0, currentResonance - (RESONANCE_DECAY_RATE * 0.5));

			// Reset streaks if no activity
			if (System.currentTimeMillis() - lastPerfectHit > 15000) {
				perfectTimingStreak = Math.max(0, perfectTimingStreak - 1);
				rhythmChain = Math.max(0, rhythmChain - 1);
			}

			updateResonanceDisplay();
		}
	}

	/**
	 * Reset resonance state
	 */
	public void disable() {
		currentResonance = 0.0;
		perfectTimingStreak = 0;
		rhythmChain = 0;
		harmonicFlow = 1.0;
		lastCombatStyle = null;
		updateResonanceDisplay();
	}

	/**
	 * Get style-specific resonance bonus
	 */
	public double getStyleResonanceBonus(Combat.CombatTypes combatType) {
		CombatStyleMastery mastery = styleMastery.get(combatType);
		if (mastery == null) return getResonanceBonus();

		double baseBonus = getResonanceBonus();
		double masteryBonus = (mastery.masteryLevel - 1.0) * 0.12; // Up to 18% for mastery
		double accuracyBonus = mastery.getAccuracyRatio() * 0.06; // Up to 6% for accuracy

		return baseBonus + masteryBonus + accuracyBonus;
	}

	/**
	 * Apply all resonance effects to combat values
	 */
	public double applyResonanceEffects(double baseValue, Combat.CombatTypes combatType) {
		if (currentResonance <= 0) return baseValue;

		double multiplier = 1.0 + getStyleResonanceBonus(combatType);

		// Transcendent state bonus
		if (isTranscendent()) {
			multiplier += 0.08; // Additional 8% in transcendent state
		}

		return baseValue * multiplier;
	}

	/**
	 * Get resonance mastery level for the current skill level
	 */
	public int getResonanceLevel() {
		return (int) stoner.getGrades()[Professions.RESONANCE];
	}

	/**
	 * Get detailed resonance statistics for display
	 */
	public String getResonanceStats() {
		StringBuilder stats = new StringBuilder();
		stats.append("=== RESONANCE MASTERY ===\n");
		stats.append(String.format("Current Resonance: %.1f%%\n", currentResonance));
		stats.append(String.format("Combat Flow: x%.1f\n", harmonicFlow));
		stats.append(String.format("Perfect Timing Streak: %d\n", perfectTimingStreak));
		stats.append(String.format("Rhythm Chain: %d\n", rhythmChain));
		stats.append(String.format("Damage Bonus: +%.1f%%\n", getResonanceBonus() * 100));
		stats.append(String.format("Accuracy Bonus: +%.1f%%\n", getAccuracyBonus() * 100));

		if (isTranscendent()) {
			stats.append("STATUS: TRANSCENDENT MASTERY!\n");
		}

		return stats.toString();
	}

	//NO-OP SAVE TO DB METHOD
	public void setResonanceOnBD(boolean[] booleans) {
	}

	//NO-OP LOAD TO DB METHOD
	public Object getResonanceFromDB() {
		return null;
	}

	/**
	 * Inner class to track performance of a combat action
	 */
	private static class CombatPerformance {
		boolean wasSuccessful = false;
		int damage = 0;
		Combat.CombatTypes combatType = Combat.CombatTypes.NONE;
		boolean perfectTiming = false;
		boolean crossStyleBonus = false;
		double rhythmQuality = 1.0;
		double executionQuality = 1.0;
		double flowState = 1.0;
	}

	/**
	 * Inner class to track mastery in each combat style
	 */
	private static class CombatStyleMastery {
		double masteryLevel = 1.0;
		int successfulHits = 0;
		int perfectHits = 0;
		int totalAttempts = 0;
		long totalDamage = 0;

		public double getAccuracyRatio() {
			return totalAttempts > 0 ? (double) successfulHits / totalAttempts : 0.0;
		}

		public double getPerfectRatio() {
			return successfulHits > 0 ? (double) perfectHits / successfulHits : 0.0;
		}
	}
}