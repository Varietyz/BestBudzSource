package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.content.StonerProperties;
import com.bestbudz.rs2.content.minigames.StonerMinigames;
import com.bestbudz.rs2.content.minigames.barrows.Barrows.Brother;
import com.bestbudz.rs2.content.minigames.duelarena.Dueling;
import com.bestbudz.rs2.content.minigames.fightcave.TzharrDetails;
import com.bestbudz.rs2.content.minigames.fightcave.TzharrGame;
import com.bestbudz.rs2.entity.mob.Mob;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all minigame-related functionality
 */
public class StonerMinigame {
	private final Stoner stoner;
	private final StonerMinigames stonerMinigamesContent; // Renamed to avoid conflict
	private final Dueling dueling;
	private final TzharrDetails jadDetails;
	private final StonerProperties properties;

	// Minigame state
	public int monsterSelected = 0;
	public boolean playingMB = false;

	// Zulrah-specific data
	public int whirlpoolsHit = 0;
	public List<Mob> tentacles = new ArrayList<>();

	// Barrows data
	private boolean[] killRecord = new boolean[Brother.values().length];
	private int killCount;
	private boolean chestClicked;

	public StonerMinigame(Stoner stoner) {
		this.stoner = stoner;
		this.stonerMinigamesContent = new StonerMinigames(stoner); // Use content class
		this.dueling = new Dueling(stoner);
		this.jadDetails = new TzharrDetails();
		this.properties = new StonerProperties(stoner);
	}

	public void process() {
		if (stoner.isPetStoner()) {
			return; // Pets don't need minigame processing
		}
		// Any periodic minigame processing can go here
	}

	/**
	 * Starts the player in minigames that need initialization
	 */
	public void start() {
		if (jadDetails.getStage() != 0) {
			TzharrGame.loadGame(stoner);
		}
	}

	// Getters and setters
	public StonerMinigames getStonerMinigames() { return stonerMinigamesContent; }
	public Dueling getDueling() { return dueling; }
	public TzharrDetails getJadDetails() { return jadDetails; }
	public StonerProperties getProperties() { return properties; }

	public int getMonsterSelected() { return monsterSelected; }
	public void setMonsterSelected(int monsterSelected) { this.monsterSelected = monsterSelected; }

	public boolean isPlayingMB() { return playingMB; }
	public void setPlayingMB(boolean playingMB) { this.playingMB = playingMB; }

	public int getWhirlpoolsHit() { return whirlpoolsHit; }
	public void setWhirlpoolsHit(int whirlpoolsHit) { this.whirlpoolsHit = whirlpoolsHit; }

	public List<Mob> getTentacles() { return tentacles; }
	public void setTentacles(List<Mob> tentacles) { this.tentacles = tentacles; }

	public boolean[] getKillRecord() { return killRecord; }
	public void setKillRecord(boolean[] killRecord) { this.killRecord = killRecord; }

	public int getKillCount() { return killCount; }
	public void setKillCount(int killCount) { this.killCount = killCount; }

	public boolean isChestClicked() { return chestClicked; }
	public void setChestClicked(boolean chestClicked) { this.chestClicked = chestClicked; }

	// Convenience methods for barrows
	public int getBarrowsKC() { return killCount; }
	public void setBarrowsKC(int killCount) { this.killCount = killCount; }
}