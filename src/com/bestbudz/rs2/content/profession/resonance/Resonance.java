package com.bestbudz.rs2.content.profession.resonance;

import com.bestbudz.rs2.content.combat.Combat;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;
import java.util.HashMap;
import java.util.Map;

public class Resonance {

	private final Stoner stoner;
	private final Map<Combat.CombatTypes, CombatStyleMastery> styleMastery = new HashMap<>();

	private static double currentResonance = 0.0;
	private Combat.CombatTypes lastCombatStyle = null;
	private long lastStyleSwitch = 0;
	private static int perfectTimingStreak = 0;
	private long lastPerfectHit = 0;
	private double harmonicFlow = 1.0;

	private long lastHitTime = 0;
	private int rhythmChain = 0;
	private double combatMastery = 1.0;

	private static final double MAX_RESONANCE = 100.0;
	private static final double RESONANCE_DECAY_RATE = 0.3;
	private static final long PERFECT_TIMING_WINDOW = 1200;
	private static final long STYLE_SWITCH_BONUS_WINDOW = 4000;
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

	public void updateResonance(boolean hitSuccessful, int damage, Combat.CombatTypes combatType) {
		if (combatType == Combat.CombatTypes.NONE) return;

		long currentTime = System.currentTimeMillis();
		CombatStyleMastery mastery = styleMastery.get(combatType);

		CombatPerformance performance = analyzeCombatPerformance(
			hitSuccessful, damage, combatType, currentTime
		);

		updateStyleMastery(mastery, performance, hitSuccessful);

		double baseExp = calculatePassiveExperience(performance, mastery);

		if (baseExp > 0) {
			awardResonanceExperience(baseExp, performance);
		}

		updateResonanceState(performance, currentTime);

		sendResonanceFeedback(performance, baseExp);
	}

	private CombatPerformance analyzeCombatPerformance(boolean hit, int damage,
													   Combat.CombatTypes type, long currentTime) {
		CombatPerformance performance = new CombatPerformance();

		performance.wasSuccessful = hit;
		performance.damage = damage;
		performance.combatType = type;

		performance.perfectTiming = checkPerfectTiming(type, currentTime);

		performance.crossStyleBonus = checkCrossStyleFluidity(type, currentTime);

		performance.rhythmQuality = analyzeRhythm(currentTime);

		performance.executionQuality = analyzeExecution(damage, type);

		performance.flowState = analyzeFlowState();

		return performance;
	}

	private boolean checkPerfectTiming(Combat.CombatTypes combatType, long currentTime) {
		if (lastHitTime == 0) return false;

		long timeDelta = currentTime - lastHitTime;
		int expectedInterval = getOptimalInterval(combatType);

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

		switch (combatType) {
			case MELEE: return 2200;
			case SAGITTARIUS: return 2800;
			case MAGE: return 3400;
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

			if (interval >= 1500 && interval <= 4000) {
				rhythmChain = Math.min(rhythmChain + 1, 20);
				return 1.0 + (rhythmChain * 0.05);
			} else {
				rhythmChain = Math.max(0, rhythmChain - 2);
			}
		}

		return 1.0 + (rhythmChain * 0.02);
	}

	private double analyzeExecution(int damage, Combat.CombatTypes type) {
		if (damage <= 0) return 0.5;

		int maxPossible = stoner.getMaxHit(type);
		if (maxPossible <= 0) return 1.0;

		double damageRatio = (double) damage / maxPossible;

		if (damageRatio >= 0.8) return 2.0;
		if (damageRatio >= 0.6) return 1.5;
		if (damageRatio >= 0.4) return 1.2;

		return 1.0;
	}

	private double analyzeFlowState() {

		double flow = 1.0;

		if (perfectTimingStreak >= 10) flow += 0.5;
		if (rhythmChain >= 8) flow += 0.3;
		if (currentResonance >= 75.0) flow += 0.2;

		return Math.min(flow, 2.5);
	}

	private void updateStyleMastery(CombatStyleMastery mastery, CombatPerformance performance, boolean hit) {
		if (hit) {
			mastery.successfulHits++;
			mastery.totalDamage += performance.damage;

			if (performance.perfectTiming) {
				mastery.perfectHits++;
			}

			mastery.masteryLevel = Math.min(2.5,
				mastery.masteryLevel + MASTERY_GROWTH_RATE * performance.rhythmQuality);
		}

		mastery.totalAttempts++;
	}

	private double calculatePassiveExperience(CombatPerformance performance, CombatStyleMastery mastery) {
		if (!performance.wasSuccessful) {

			return 2.0 * performance.rhythmQuality;
		}

		double baseExp = Math.log(performance.damage + 1) * 8.0;

		double multiplier = 1.0;

		if (performance.perfectTiming) {
			multiplier *= 2.2;
			if (perfectTimingStreak >= 5) {
				multiplier *= 1.0 + (perfectTimingStreak * 0.08);
			}
		}

		if (performance.crossStyleBonus) {
			multiplier *= CROSS_STYLE_MULTIPLIER;
		}

		multiplier *= performance.rhythmQuality;

		multiplier *= performance.executionQuality;

		multiplier *= performance.flowState;

		multiplier *= mastery.masteryLevel;

		long resonanceLevel = stoner.getGrades()[Professions.RESONANCE];
		if (resonanceLevel >= 80) {
			multiplier *= 0.7;
		} else if (resonanceLevel >= 50) {
			multiplier *= 0.85;
		}

		return Math.min(baseExp * multiplier, 150.0);
	}

	private void awardResonanceExperience(double baseExp, CombatPerformance performance) {
		if (baseExp <= 0) return;

		double finalExp = stoner.getProfession().addExperience(Professions.RESONANCE, baseExp);

		double resonanceGain = baseExp / 20.0;
		currentResonance = Math.min(currentResonance + resonanceGain, MAX_RESONANCE);

		if (performance.perfectTiming && performance.crossStyleBonus) {
			harmonicFlow = Math.min(harmonicFlow + 0.02, 2.0);
		}

		combatMastery = Math.min(combatMastery + 0.001, 1.5);
	}

	private void updateResonanceState(CombatPerformance performance, long currentTime) {

		if (performance.perfectTiming) {
			lastPerfectHit = currentTime;
		}

		if (currentTime - lastPerfectHit > 8000) {
			currentResonance = Math.max(0, currentResonance - RESONANCE_DECAY_RATE);
			harmonicFlow = Math.max(1.0, harmonicFlow - 0.01);
		}
	}

	private static String lastMessageType = null;
	private static double consolidatedExp = 0.0;
	private static int messageCount = 0;
	private long lastMessageTime = 0;
	private static final long MESSAGE_CONSOLIDATION_WINDOW = 500;

	private void sendResonanceFeedback(CombatPerformance performance, double expGained) {
		if (expGained <= 1.0) return;

		String messageType = getResonanceMessageType(performance);
		if (messageType != null) {
			consolidateAndSendMessage(messageType, expGained);
		}

		checkAndSendMilestones();
		updateResonanceDisplay();
	}

	private void consolidateAndSendMessage(String messageType, double expGained) {
		long currentTime = System.currentTimeMillis();

		if (messageType.equals(lastMessageType) &&
			(currentTime - lastMessageTime) <= MESSAGE_CONSOLIDATION_WINDOW) {

			consolidatedExp += expGained;
			messageCount++;

		} else {

			if (lastMessageType != null && consolidatedExp > 0) {
				sendConsolidatedMessage(lastMessageType, consolidatedExp, messageCount);
			}

			lastMessageType = messageType;
			consolidatedExp = expGained;
			messageCount = 1;
			lastMessageTime = currentTime;
		}
	}

	private static String formatConsolidatedMessage(String template, double expGained, int hitCount) {
		String streakInfo = (perfectTimingStreak >= 3) ?
			String.format(" (%dx streak!)", perfectTimingStreak) : "";

		String resonanceInfo = (currentResonance >= 25.0) ?
			String.format(" (%.0f%% resonance)", currentResonance) : "";

		return String.format(template, expGained, hitCount, streakInfo, resonanceInfo);
	}

	private void sendConsolidatedMessage(String messageType, double totalExp, int count) {
		String message;

		if (count > 1) {

			message = formatConsolidatedMessage(messageType + " %.1f total resonance experience (%dx hits)%s%s", totalExp, count);
		} else {

			message = formatMessage(messageType + " %.1f resonance experience%s%s", totalExp);
		}

		sendMessage(message);
	}

	public void flushPendingMessage() {
		if (lastMessageType != null && consolidatedExp > 0) {
			sendConsolidatedMessage(lastMessageType, consolidatedExp, messageCount);
			lastMessageType = null;
			consolidatedExp = 0.0;
			messageCount = 0;
		}
	}

	private String getResonanceMessageType(CombatPerformance performance) {

		if (performance.perfectTiming && performance.crossStyleBonus && performance.flowState > 2.0) {
			return "TRANSCENDENT RESONANCE!";
		}

		if (performance.perfectTiming && performance.crossStyleBonus) {
			return "Harmonic Convergence!";
		}

		if (perfectTimingStreak >= 8) {
			return "Perfect Flow!";
		}

		if (performance.crossStyleBonus) {
			return "Cross-Style Mastery!";
		}

		if (performance.perfectTiming) {
			return "Perfect Timing!";
		}

		if (performance.rhythmQuality > 1.5) {
			return "Combat Rhythm!";
		}

		if (performance.executionQuality >= 2.0) {
			return "Devastating Strike!";
		}

		return null;
	}

	private String getResonanceMessage(CombatPerformance performance, double expGained) {

		if (performance.perfectTiming && performance.crossStyleBonus && performance.flowState > 2.0) {
			return formatMessage("TRANSCENDENT RESONANCE! %.1f resonance experience%s%s", expGained);
		}

		if (performance.perfectTiming && performance.crossStyleBonus) {
			return formatMessage("Harmonic Convergence! %.1f resonance experience%s%s", expGained);
		}

		if (perfectTimingStreak >= 8) {
			return formatMessage("Perfect Flow! %.1f resonance experience%s%s", expGained);
		}

		if (performance.crossStyleBonus) {
			return formatMessage("Cross-Style Mastery! %.1f resonance experience%s%s", expGained);
		}

		if (performance.perfectTiming) {
			return formatMessage("Perfect Timing! %.1f resonance experience%s%s", expGained);
		}

		if (performance.rhythmQuality > 1.5) {
			return formatMessage("Combat Rhythm! %.1f resonance experience%s%s", expGained);
		}

		if (performance.executionQuality >= 2.0) {
			return formatMessage("Devastating Strike! %.1f resonance experience%s%s", expGained);
		}

		return null;
	}

	private static String formatMessage(String template, double expGained) {
		String streakInfo = (perfectTimingStreak >= 3) ?
			String.format(" (%dx streak!@whi@)", perfectTimingStreak) : "";

		String resonanceInfo = (currentResonance >= 25.0) ?
			String.format(" (%.0f%% resonance)", currentResonance) : "";

		return String.format(template, expGained, streakInfo, resonanceInfo);
	}

	private void sendMessage(String message) {
		stoner.getClient().queueOutgoingPacket(new SendMessage(message));
	}

	private void checkAndSendMilestones() {

		if (currentResonance == 50.0) {
			sendMessage("RESONANCE MILESTONE: 50% - Combat efficiency increased!");
		} else if (currentResonance == 75.0) {
			sendMessage("RESONANCE MILESTONE: 75% - Approaching mastery!");
		} else if (currentResonance >= 95.0 && !wasTranscendentLastCheck) {
			sendMessage("TRANSCENDENT STATE ACHIEVED! Maximum combat resonance!");
			wasTranscendentLastCheck = true;
		}

		if (perfectTimingStreak == 5) {
			sendMessage("Perfect timing mastery developing! (+5 streak)");
		} else if (perfectTimingStreak == 10) {
			sendMessage("Incredible timing precision! (+10 streak)");
		} else if (perfectTimingStreak == 15) {
			sendMessage("LEGENDARY timing control! (+15 streak)");
		}

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

	public double getCurrentResonance() {
		return currentResonance;
	}

	public double getResonanceBonus() {
		double base = (currentResonance / MAX_RESONANCE) * 0.15;
		double mastery = (combatMastery - 1.0) * 0.1;
		double flow = (harmonicFlow - 1.0) * 0.08;

		return base + mastery + flow;
	}

	public double getAccuracyBonus() {
		double base = (currentResonance / MAX_RESONANCE) * 0.12;
		double rhythm = (rhythmChain / 20.0) * 0.08;

		return base + rhythm;
	}

	public boolean isTranscendent() {
		return currentResonance >= 95.0 &&
			perfectTimingStreak >= 10 &&
			harmonicFlow >= 1.8;
	}

	public void drain() {
		if (currentResonance > 0) {
			currentResonance = Math.max(0, currentResonance - (RESONANCE_DECAY_RATE * 0.5));

			if (System.currentTimeMillis() - lastPerfectHit > 15000) {
				perfectTimingStreak = Math.max(0, perfectTimingStreak - 1);
				rhythmChain = Math.max(0, rhythmChain - 1);
			}

			updateResonanceDisplay();
		}
	}

	public void disable() {
		currentResonance = 0.0;
		perfectTimingStreak = 0;
		rhythmChain = 0;
		harmonicFlow = 1.0;
		lastCombatStyle = null;
		updateResonanceDisplay();
	}

	public double getStyleResonanceBonus(Combat.CombatTypes combatType) {
		CombatStyleMastery mastery = styleMastery.get(combatType);
		if (mastery == null) return getResonanceBonus();

		double baseBonus = getResonanceBonus();
		double masteryBonus = (mastery.masteryLevel - 1.0) * 0.12;
		double accuracyBonus = mastery.getAccuracyRatio() * 0.06;

		return baseBonus + masteryBonus + accuracyBonus;
	}

	public double applyResonanceEffects(double baseValue, Combat.CombatTypes combatType) {
		if (currentResonance <= 0) return baseValue;

		double multiplier = 1.0 + getStyleResonanceBonus(combatType);

		if (isTranscendent()) {
			multiplier += 0.08;
		}

		return baseValue * multiplier;
	}

	public int getResonanceLevel() {
		return (int) stoner.getGrades()[Professions.RESONANCE];
	}

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

	public void setResonanceOnBD(boolean[] booleans) {
	}

	public Object getResonanceFromDB() {
		return null;
	}

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
