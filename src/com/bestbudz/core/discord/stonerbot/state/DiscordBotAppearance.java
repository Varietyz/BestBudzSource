package com.bestbudz.core.discord.stonerbot.state;

import com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChangeAppearancePacket;

/**
 * Handles Discord bot appearance modifications
 */
public class DiscordBotAppearance {

	private final Stoner stoner;

	public DiscordBotAppearance(Stoner stoner) {
		this.stoner = stoner;
	}

	public void setupMinimalAppearance() {
		// Set gender first
		stoner.setGender(DiscordBotDefaults.DEFAULT_GENDER);

		// Set appearance array correctly
		stoner.setAppearance(DiscordBotDefaults.DEFAULT_APPEARANCE.clone());

		// Set colors
		stoner.setColors(DiscordBotDefaults.DEFAULT_COLORS.clone());

		// Validate the appearance using the same validation as ChangeAppearancePacket
		if (!ChangeAppearancePacket.validate(stoner)) {
			// If validation fails, set to safe default
			System.out.println("Discord bot appearance validation failed, setting to safe defaults");

			// Use safe female defaults
			stoner.setGender((byte) 1); // Female
			stoner.getAppearance()[0] = 48; // HEAD
			stoner.getAppearance()[1] = 57; // TORSO
			stoner.getAppearance()[2] = 61; // ARMS
			stoner.getAppearance()[3] = 68; // HANDS
			stoner.getAppearance()[4] = 76; // LEGS
			stoner.getAppearance()[5] = 79; // FEET
			stoner.getAppearance()[6] = -1; // JAW (no jaw for female)

			// Safe color defaults (all 0 to be safe)
			stoner.getColors()[0] = 0; // HAIR
			stoner.getColors()[1] = 0; // TORSO
			stoner.getColors()[2] = 0; // LEGS
			stoner.getColors()[3] = 0; // FEET
			stoner.getColors()[4] = 0; // SKIN
		}

		// Force appearance update
		stoner.setAppearanceUpdateRequired(true);
		System.out.println("Discord bot appearance set: Gender=" + stoner.getGender() +
			", Head=" + stoner.getAppearance()[0] +
			", Torso=" + stoner.getAppearance()[1] +
			", Valid=" + ChangeAppearancePacket.validate(stoner));
	}

	public void setNeedsVisualUpdate() {
		stoner.setChatUpdateRequired(true);
		stoner.setAppearanceUpdateRequired(true);
		stoner.getUpdateFlags().setUpdateRequired(true);

		// Trigger save when appearance changes
		if (stoner instanceof com.bestbudz.core.discord.stonerbot.DiscordBotStoner) {
			((com.bestbudz.core.discord.stonerbot.DiscordBotStoner) stoner).getBotPersistence().onBotAppearanceChange();
		}
	}
}