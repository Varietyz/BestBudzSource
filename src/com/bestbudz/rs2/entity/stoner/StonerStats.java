package com.bestbudz.rs2.entity.stoner;

import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.cannacredits.CannaCreditUnlocks;
import com.bestbudz.rs2.content.profession.Professions;
import com.bestbudz.rs2.content.interfaces.InterfaceHandler;
import com.bestbudz.rs2.content.interfaces.impl.QuestTab;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages all player statistics, achievements, points, and progression data
 */
public class StonerStats {
	private final Stoner stoner;

	// Experience and levels
	private double expCounter;
	private int[][] professionGoals = new int[Professions.PROFESSION_COUNT + 1][3];
	private int[] professionAdvances = new int[Professions.PROFESSION_COUNT];
	private int totalAdvances;
	private int advancePoints;

	// Achievement system
	private final HashMap<AchievementList, Integer> stonerAchievements =
		new HashMap<AchievementList, Integer>(AchievementList.values().length) {
			private static final long serialVersionUID = -4629357800141530574L;
			{
				for (AchievementList achievement : AchievementList.values()) {
					put(achievement, 0);
				}
			}
		};
	private int achievementsPoints;

	// Kill tracking
	private ArrayList<String> lastKilledStoners = new ArrayList<String>();
	private int kills = 0;
	private int deaths = 0;
	private int rogueKills = 0;
	private int rogueRecord = 0;
	private int hunterKills = 0;
	private int hunterRecord = 0;

	// Points systems
	private int chillPoints = 50;
	private int mercenaryPoints = 0;
	private int pestPoints = 0;
	private int blackMarks = 0;
	private int bountyPoints;
	private int arenaPoints;
	private int weaponKills;
	private int weaponPoints;

	// Currency and spending
	private int cannacredits;
	private int moneySpent;
	private long moneyPouch;
	private boolean pouchPayment;
	private long shopCollection;

	// Credit system
	private Set<CannaCreditUnlocks> unlockedCredits =
		new HashSet<CannaCreditUnlocks>(CannaCreditUnlocks.values().length);

	// Profile stats
	private int likes, dislikes, profileViews;
	private long lastLike;
	private byte likesGiven;
	private boolean profilePrivacy;

	// Clue scrolls
	private int[] cluesCompleted = new int[4];

	// Boss tracking
	private int bossID;

	// Member status
	private boolean isMember = false;

	public StonerStats(Stoner stoner) {
		this.stoner = stoner;
	}

	public void process() {
		// Any periodic stat processing can go here
	}

	// Experience methods
	public void addExperience(int skill, double amount) {
		expCounter += amount;
		// Additional experience processing logic
	}

	public double getCounterExp() {
		return expCounter;
	}

	public void addCounterExp(double exp) {
		expCounter += exp;
	}

	// Achievement methods
	public void updateAchievement(AchievementList achievement, int progress) {
		stonerAchievements.put(achievement, progress);
	}

	public HashMap<AchievementList, Integer> getStonerAchievements() {
		return stonerAchievements;
	}

	public int getAchievementsPoints() {
		return achievementsPoints;
	}

	public void addAchievementPoints(int points) {
		achievementsPoints += points;
	}

	// Kill tracking methods
	public void addKill() {
		kills++;
		InterfaceHandler.writeText(new QuestTab(stoner));
	}

	public void addDeath() {
		deaths++;
		InterfaceHandler.writeText(new QuestTab(stoner));
	}

	public void incrDeaths() {
		deaths = ((short) (deaths + 1));
		InterfaceHandler.writeText(new QuestTab(stoner));
	}

	// Points management
	public void addMercenaryPoints(int amount) {
		mercenaryPoints += amount;
	}

	// Credit system
	public void unlockCredit(CannaCreditUnlocks purchase) {
		unlockedCredits.add(purchase);
	}

	public boolean isCreditUnlocked(CannaCreditUnlocks purchase) {
		return unlockedCredits.contains(purchase);
	}

	// Getters and setters
	public int[][] getProfessionGoals() { return professionGoals; }
	public void setProfessionGoals(int[][] professionGoals) { this.professionGoals = professionGoals; }

	public int[] getProfessionAdvances() { return professionAdvances; }
	public void setProfessionAdvances(int[] professionAdvances) { this.professionAdvances = professionAdvances; }

	public int getTotalAdvances() { return totalAdvances; }
	public void setTotalAdvances(int totalAdvances) { this.totalAdvances = totalAdvances; }

	public int getAdvancePoints() { return advancePoints; }
	public void setAdvancePoints(int advancePoints) { this.advancePoints = advancePoints; }

	public ArrayList<String> getLastKilledStoners() { return lastKilledStoners; }
	public void setLastKilledStoners(ArrayList<String> lastKilledStoners) { this.lastKilledStoners = lastKilledStoners; }

	public int getKills() { return kills; }
	public void setKills(int kills) { this.kills = kills; }

	public int getDeaths() { return deaths; }
	public void setDeaths(int deaths) { this.deaths = deaths; }

	public int getRogueKills() { return rogueKills; }
	public void setRogueKills(int rogueKills) { this.rogueKills = rogueKills; }

	public int getRogueRecord() { return rogueRecord; }
	public void setRogueRecord(int rogueRecord) { this.rogueRecord = rogueRecord; }

	public int getHunterKills() { return hunterKills; }
	public void setHunterKills(int hunterKills) { this.hunterKills = hunterKills; }

	public int getHunterRecord() { return hunterRecord; }
	public void setHunterRecord(int hunterRecord) { this.hunterRecord = hunterRecord; }

	public int getChillPoints() { return chillPoints; }
	public void setChillPoints(int chillPoints) { this.chillPoints = chillPoints; }

	public int getMercenaryPoints() { return mercenaryPoints; }
	public void setMercenaryPoints(int mercenaryPoints) { this.mercenaryPoints = mercenaryPoints; }

	public int getPestPoints() { return pestPoints; }
	public void setPestPoints(int pestPoints) { this.pestPoints = pestPoints; }

	public int getBlackMarks() { return blackMarks; }
	public void setBlackMarks(int blackMarks) { this.blackMarks = blackMarks; }

	public int getBountyPoints() { return bountyPoints; }
	public int setBountyPoints(int amount) { return bountyPoints = amount; }

	public int getArenaPoints() { return arenaPoints; }
	public void setArenaPoints(int arenaPoints) { this.arenaPoints = arenaPoints; }

	public int getWeaponKills() { return weaponKills; }
	public void setWeaponKills(int weaponKills) { this.weaponKills = weaponKills; }

	public int getWeaponPoints() { return weaponPoints; }
	public void setWeaponPoints(int weaponPoints) { this.weaponPoints = weaponPoints; }

	public int getCredits() { return cannacredits; }
	public void setCredits(int cannacredits) { this.cannacredits = cannacredits; }

	public int getMoneySpent() { return moneySpent; }
	public void setMoneySpent(int moneySpent) { this.moneySpent = moneySpent; }

	public long getMoneyPouch() { return moneyPouch; }
	public void setMoneyPouch(long moneyPouch) { this.moneyPouch = moneyPouch; }

	public boolean isPouchPayment() { return pouchPayment; }
	public void setPouchPayment(boolean pouchPayment) { this.pouchPayment = pouchPayment; }

	public long getShopCollection() { return shopCollection; }
	public void setShopCollection(long shopCollection) { this.shopCollection = shopCollection; }

	public Set<CannaCreditUnlocks> getUnlockedCredits() { return unlockedCredits; }
	public void setUnlockedCredits(Set<CannaCreditUnlocks> unlockedCredits) { this.unlockedCredits = unlockedCredits; }

	public int getLikes() { return likes; }
	public void setLikes(int likes) { this.likes = likes; }

	public int getDislikes() { return dislikes; }
	public void setDislikes(int dislikes) { this.dislikes = dislikes; }

	public int getProfileViews() { return profileViews; }
	public void setProfileViews(int profileViews) { this.profileViews = profileViews; }

	public long getLastLike() { return lastLike; }
	public void setLastLike(long lastLike) { this.lastLike = lastLike; }

	public byte getLikesGiven() { return likesGiven; }
	public void setLikesGiven(byte likesGiven) { this.likesGiven = likesGiven; }

	public boolean isProfilePrivacy() { return profilePrivacy; }
	public void setProfilePrivacy(boolean profilePrivacy) { this.profilePrivacy = profilePrivacy; }

	public int[] getCluesCompleted() { return cluesCompleted; }
	public void setCluesCompleted(int[] cluesCompleted) { this.cluesCompleted = cluesCompleted; }
	public void setCluesCompleted(int index, int value) { cluesCompleted[index] = value; }

	public int getBossID() { return bossID; }
	public void setBossID(int bossID) { this.bossID = bossID; }

	public boolean isMember() { return isMember; }
	public void setMember(boolean isMember) { this.isMember = isMember; }
}