package com.bestbudz.rs2.auto.combat.handlers;

import com.bestbudz.rs2.entity.item.Equipment;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class MeleeStyleManager {

	private final Stoner stoner;
	private long lastStyleChangeTime = 0;
	private static final long STYLE_CHANGE_INTERVAL = 120000;

	private final Equipment.AssaultStyles[] STYLE_ROTATION = {
		Equipment.AssaultStyles.ACCURATE,
		Equipment.AssaultStyles.AGGRESSIVE,
		Equipment.AssaultStyles.DEFENSIVE,
		Equipment.AssaultStyles.CONTROLLED
	};

	private int currentStyleIndex = 0;

	public MeleeStyleManager(Stoner stoner) {
		this.stoner = stoner;
		this.lastStyleChangeTime = System.currentTimeMillis();
	}

	public void processStyleRotation() {
		if (!shouldRotateStyle()) {
			return;
		}

		rotateToNextStyle();
	}

	private boolean shouldRotateStyle() {
		long currentTime = System.currentTimeMillis();
		return (currentTime - lastStyleChangeTime) >= STYLE_CHANGE_INTERVAL;
	}

	private void rotateToNextStyle() {
		currentStyleIndex = (currentStyleIndex + 1) % STYLE_ROTATION.length;
		Equipment.AssaultStyles newStyle = STYLE_ROTATION[currentStyleIndex];

		stoner.getEquipment().setAssaultStyle(newStyle);
		lastStyleChangeTime = System.currentTimeMillis();

		String message = "Auto-combat: Switched to " + getStyleName(newStyle) +
			" style for " + getStyleBenefit(newStyle);
		stoner.send(new SendMessage(message));
	}

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

	public Equipment.AssaultStyles getCurrentStyle() {
		return stoner.getEquipment().getAssaultStyle();
	}

	private String getStyleName(Equipment.AssaultStyles style) {
		switch (style) {
			case ACCURATE: return "Accurate";
			case AGGRESSIVE: return "Aggressive";
			case CONTROLLED: return "Controlled";
			case DEFENSIVE: return "Defensive";
			default: return "Unknown";
		}
	}

	private String getStyleBenefit(Equipment.AssaultStyles style) {
		switch (style) {
			case ACCURATE: return "Assault training";
			case AGGRESSIVE: return "Vigour training";
			case CONTROLLED: return "balanced training (Assault/Vigour/Aegis)";
			case DEFENSIVE: return "Aegis training";
			default: return "unknown benefit";
		}
	}

	public void resetTiming() {
		lastStyleChangeTime = System.currentTimeMillis();
	}

	public String getTimingInfo() {
		long timeInCurrentStyle = System.currentTimeMillis() - lastStyleChangeTime;
		long timeUntilNext = Math.max(0, STYLE_CHANGE_INTERVAL - timeInCurrentStyle);

		return String.format("Melee Style: %s, Time in style: %ds, Next change in: %ds",
			getStyleName(getCurrentStyle()),
			timeInCurrentStyle / 1000,
			timeUntilNext / 1000);
	}
}
