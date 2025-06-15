package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Manages all player settings, preferences, and administrative flags
 */
public class StonerSettings {
	private final Stoner stoner;

	// Display settings
	private byte screenBrightness = 3;
	private byte multipleMouseButtons = 0;
	private byte chatEffectsEnabled = 0;
	private byte splitPrivateChat = 0;
	private byte transparentPanel = 0;
	private byte transparentChatbox = 0;
	private byte sideStones = 0;
	private byte acceptAid = 0;
	private byte musicVolume = 0;
	private byte soundVolume = 0;

	// Administrative flags
	private boolean jailed = false;
	private long jailLength = 0;
	private long banLength = 0;
	private long muteLength = 0;
	private boolean banned = false;
	private boolean muted = false;
	private boolean yellMuted = false;

	// Pin system
	private String pin;
	public boolean enteredPin = false;

	// Interface settings
	private int resonanceInterface;
	private int currentSongId = -1;

	// Advanced settings
	private boolean advanceColors;

	// Pouches for runecrafting
	private byte[] pouches = new byte[4];

	public StonerSettings(Stoner stoner) {
		this.stoner = stoner;
	}

	// Getters and setters for display settings
	public byte getScreenBrightness() { return screenBrightness; }
	public void setScreenBrightness(byte screenBrightness) { this.screenBrightness = screenBrightness; }

	public byte getMultipleMouseButtons() { return multipleMouseButtons; }
	public void setMultipleMouseButtons(byte multipleMouseButtons) { this.multipleMouseButtons = multipleMouseButtons; }

	public byte getChatEffectsEnabled() { return chatEffectsEnabled; }
	public void setChatEffectsEnabled(byte chatEffectsEnabled) { this.chatEffectsEnabled = chatEffectsEnabled; }

	public byte getSplitPrivateChat() { return splitPrivateChat; }
	public void setSplitPrivateChat(byte splitPrivateChat) { this.splitPrivateChat = splitPrivateChat; }

	public byte getTransparentPanel() { return transparentPanel; }
	public void setTransparentPanel(byte transparentPanel) { this.transparentPanel = transparentPanel; }

	public byte getTransparentChatbox() { return transparentChatbox; }
	public void setTransparentChatbox(byte transparentChatbox) { this.transparentChatbox = transparentChatbox; }

	public byte getSideStones() { return sideStones; }
	public void setSideStones(byte sideStones) { this.sideStones = sideStones; }

	public byte getAcceptAid() { return acceptAid; }
	public void setAcceptAid(byte acceptAid) { this.acceptAid = acceptAid; }

	public byte getMusicVolume() { return musicVolume; }
	public void setMusicVolume(byte musicVolume) { this.musicVolume = musicVolume; }

	public byte getSoundVolume() { return soundVolume; }
	public void setSoundVolume(byte soundVolume) { this.soundVolume = soundVolume; }

	// Administrative flags getters and setters
	public boolean isJailed() { return jailed; }
	public void setJailed(boolean jailed) { this.jailed = jailed; }

	public long getJailLength() { return jailLength; }
	public void setJailLength(long jailLength) { this.jailLength = jailLength; }

	public long getBanLength() { return banLength; }
	public void setBanLength(long banLength) { this.banLength = banLength; }

	public long getMuteLength() { return muteLength; }
	public void setMuteLength(long muteLength) { this.muteLength = muteLength; }

	public boolean isBanned() { return banned; }
	public void setBanned(boolean banned) { this.banned = banned; }

	public boolean isMuted() { return muted; }
	public void setMuted(boolean muted) { this.muted = muted; }

	public boolean isYellMuted() { return yellMuted; }
	public void setYellMuted(boolean yellMuted) { this.yellMuted = yellMuted; }

	// Pin system getters and setters
	public String getPin() { return pin; }
	public void setPin(String pin) { this.pin = pin; }

	public boolean isEnteredPin() { return enteredPin; }
	public void setEnteredPin(boolean enteredPin) { this.enteredPin = enteredPin; }

	// Interface settings getters and setters
	public int getResonanceInterface() { return resonanceInterface; }
	public void setResonanceInterface(int resonanceInterface) { this.resonanceInterface = resonanceInterface; }

	public int getCurrentSongId() { return currentSongId; }
	public void setCurrentSongId(int currentSongId) { this.currentSongId = currentSongId; }

	// Advanced settings getters and setters
	public boolean isAdvanceColors() { return advanceColors; }
	public void setAdvanceColors(boolean advanceColors) { this.advanceColors = advanceColors; }

	// Pouches getters and setters
	public byte[] getPouches() { return pouches; }
	public void setPouches(byte[] pouches) { this.pouches = pouches; }
}