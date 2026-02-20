package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.StonerAnimations;

public class StonerAppearance {
	private final Stoner stoner;
	private final StonerAnimations stonerAnimations;

	private byte gender = 0;
	private int[] appearance = new int[7];
	private byte[] colors = new byte[5];
	private short npcAppearanceId = -1;

	private int chatColor;
	private int chatEffects;
	private byte[] chatText;

	private boolean appearanceUpdateRequired = false;
	private boolean chatUpdateRequired = false;

	public StonerAppearance(Stoner stoner) {
		this.stoner = stoner;
		this.stonerAnimations = new StonerAnimations();
	}

	public void reset() {
		appearanceUpdateRequired = false;
		chatUpdateRequired = false;
	}

	public byte getGender() { return gender; }
	public void setGender(byte gender) { this.gender = gender; }

	public int[] getAppearance() { return appearance; }
	public void setAppearance(int[] appearance) { this.appearance = appearance; }

	public byte[] getColors() { return colors; }
	public void setColors(byte[] colors) { this.colors = colors; }

	public short getNpcAppearanceId() { return npcAppearanceId; }
	public void setNpcAppearanceId(short npcAppearanceId) { this.npcAppearanceId = npcAppearanceId; }

	public int getChatColor() { return chatColor; }
	public void setChatColor(int chatColor) { this.chatColor = chatColor; }

	public int getChatEffects() { return chatEffects; }
	public void setChatEffects(int chatEffects) { this.chatEffects = chatEffects; }

	public byte[] getChatText() { return chatText; }
	public void setChatText(byte[] chatText) { this.chatText = chatText; }

	public boolean isAppearanceUpdateRequired() { return appearanceUpdateRequired; }
	public void setAppearanceUpdateRequired(boolean appearanceUpdateRequired) {
		if (appearanceUpdateRequired) {
			stoner.getUpdateFlags().setUpdateRequired(true);
		}
		this.appearanceUpdateRequired = appearanceUpdateRequired;
	}

	public boolean isChatUpdateRequired() { return chatUpdateRequired; }
	public void setChatUpdateRequired(boolean chatUpdateRequired) {
		if (chatUpdateRequired) {
			stoner.getUpdateFlags().setUpdateRequired(true);
		}
		this.chatUpdateRequired = chatUpdateRequired;
	}

	public StonerAnimations getAnimations() { return stonerAnimations; }
}
