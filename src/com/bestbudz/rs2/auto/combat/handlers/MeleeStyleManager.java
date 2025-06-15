package com.bestbudz.rs2.auto.combat.handlers;

import com.bestbudz.rs2.entity.item.Equipment;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.content.combat.Combat.CombatTypes;

/**
 * Manages melee combat style rotation for balanced profession training
 */
public class MeleeStyleManager {

	private final Stoner stoner;
	private long lastStyleChangeTime = 0;
	private static final long STYLE_CHANGE_INTERVAL = 120000; // 2 minutes per style

	// Style rotation order for balanced training
	private final Equipment.AssaultStyles[] STYLE_ROTATION = {
		Equipment.AssaultStyles.ACCURATE,    // Assault XP
		Equipment.AssaultStyles.AGGRESSIVE,  // Vigour XP
		Equipment.AssaultStyles.DEFENSIVE,   // Aegis XP
		Equipment.AssaultStyles.CONTROLLED   // Balanced XP (all three)
	};

	private int currentStyleIndex = 0;

	public MeleeStyleManager(Stoner stoner) {
		this.stoner = stoner;
		this.lastStyleChangeTime = System.currentTimeMillis();
	}

	/**
	 * Check if melee style should be rotated and do it if needed
	 */
	public void processStyleRotation() {
		if (!shouldRotateStyle()) {
			return;
		}

		rotateToNextStyle();
	}

	/**
	 * Check if it's time to rotate the melee style
	 */
	private boolean shouldRotateStyle() {
		long currentTime = System.currentTimeMillis();
		return (currentTime - lastStyleChangeTime) >= STYLE_CHANGE_INTERVAL;
	}

	/**
	 * Rotate to the next style in the sequence
	 */
	private void rotateToNextStyle() {
		currentStyleIndex = (currentStyleIndex + 1) % STYLE_ROTATION.length;
		Equipment.AssaultStyles newStyle = STYLE_ROTATION[currentStyleIndex];

		// Apply the new style
		stoner.getEquipment().setAssaultStyle(newStyle);
		lastStyleChangeTime = System.currentTimeMillis();

		String message = "Auto-combat: Switched to " + getStyleName(newStyle) +
			" style for " + getStyleBenefit(newStyle);
		stoner.send(new SendMessage(message));
	}

	/**
	 * Force a specific style (for manual control or special situations)
	 */
	public void forceStyle(Equipment.AssaultStyles style) {
		for (int i = 0; i < STYLE_ROTATION.length; i++) {
			if (STYLE_ROTATION[i] == style) {
				currentStyleIndex = i;
				break;
			}
		}

		stoner.getEquipment().setAssaultStyle(style);
		lastStyleChangeTime = System.currentTimeMillis();

		String message = "Manually set melee style to " + getStyleName(style);
		stoner.send(new SendMessage(message));
	}

	/**
	 * Get current melee style
	 */
	public Equipment.AssaultStyles getCurrentStyle() {
		return stoner.getEquipment().getAssaultStyle();
	}

	/**
	 * Get user-friendly style name
	 */
	private String getStyleName(Equipment.AssaultStyles style) {
		switch (style) {
			case ACCURATE: return "Accurate";
			case AGGRESSIVE: return "Aggressive";
			case CONTROLLED: return "Controlled";
			case DEFENSIVE: return "Defensive";
			default: return "Unknown";
		}
	}

	/**
	 * Get the training benefit description
	 */
	private String getStyleBenefit(Equipment.AssaultStyles style) {
		switch (style) {
			case ACCURATE: return "Assault training";
			case AGGRESSIVE: return "Vigour training";
			case CONTROLLED: return "balanced training (Assault/Vigour/Aegis)";
			case DEFENSIVE: return "Aegis training";
			default: return "unknown benefit";
		}
	}

	/**
	 * Reset style rotation timing
	 */
	public void resetTiming() {
		lastStyleChangeTime = System.currentTimeMillis();
	}

	/**
	 * Get timing info for debugging
	 */
	public String getTimingInfo() {
		long timeInCurrentStyle = System.currentTimeMillis() - lastStyleChangeTime;
		long timeUntilNext = Math.max(0, STYLE_CHANGE_INTERVAL - timeInCurrentStyle);

		return String.format("Melee Style: %s, Time in style: %ds, Next change in: %ds",
			getStyleName(getCurrentStyle()),
			timeInCurrentStyle / 1000,
			timeUntilNext / 1000);
	}
}