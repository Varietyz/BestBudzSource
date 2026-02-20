package com.bestbudz.core.discord.stonerbot.state;

import com.bestbudz.core.discord.stonerbot.config.DiscordBotDefaults;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.impl.ChangeAppearancePacket;

public class DiscordBotAppearance {

	private final Stoner stoner;

	public DiscordBotAppearance(Stoner stoner) {
		this.stoner = stoner;
	}

	public void setupMinimalAppearance() {

		stoner.setGender(DiscordBotDefaults.DEFAULT_GENDER);

		stoner.setAppearance(DiscordBotDefaults.DEFAULT_APPEARANCE.clone());

		stoner.setColors(DiscordBotDefaults.DEFAULT_COLORS.clone());

		if (!ChangeAppearancePacket.validate(stoner)) {

			System.out.println("Discord bot appearance validation failed, setting to safe defaults");

			stoner.setGender((byte) 1);
			stoner.getAppearance()[0] = 48;
			stoner.getAppearance()[1] = 57;
			stoner.getAppearance()[2] = 61;
			stoner.getAppearance()[3] = 68;
			stoner.getAppearance()[4] = 76;
			stoner.getAppearance()[5] = 79;
			stoner.getAppearance()[6] = -1;

			stoner.getColors()[0] = 0;
			stoner.getColors()[1] = 0;
			stoner.getColors()[2] = 0;
			stoner.getColors()[3] = 0;
			stoner.getColors()[4] = 0;
		}

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

		if (stoner instanceof com.bestbudz.core.discord.stonerbot.DiscordBotStoner) {
			((com.bestbudz.core.discord.stonerbot.DiscordBotStoner) stoner).getBotPersistence().onBotAppearanceChange();
		}
	}
}
