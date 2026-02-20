package com.bestbudz.rs2.content.profession.bankstanding;

import com.bestbudz.core.cache.map.Region;
import com.bestbudz.core.cache.map.RSObject;
import com.bestbudz.rs2.entity.Location;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class BankStanding {

	public static final int BANKSTANDING_SKILL_ID = 19;

	private final Stoner stoner;

	private boolean isActive = false;
	private long sessionStartTime = 0;
	private Location startLocation = null;
	private long lastXPTime = 0;
	private long lastActivityTime = 0;
	private int standingMinutes = 0;
	private int totalMovement = 0;
	private int ticksStanding = 0;
	private int processTicks = 0;
	private int sessionXPGained = 0;
	private int sessionBonusXPGained = 0;

	private static final int BASE_XP = 850;
	private static final int MAX_STANDING_BONUS = 35;
	private static final int ACTIVITY_BONUS = 10;
	private static final double BASE_MULTIPLIER = 1.2;
	private static final int XP_INTERVAL_TICKS = 25;
	private static final int PROCESS_INTERVAL = 5;
	public static int BANKSTANDING_RANGE = 2;

	public BankStanding(Stoner stoner) {
		this.stoner = stoner;
	}

	public void process() {
		try {
			if (!stoner.isActive()) {
				return;
			}

			processTicks++;

			if (processTicks % PROCESS_INTERVAL == 0) {
				boolean nearBank = isNearBank();

				if (nearBank && !isActive) {

					startSession();
				} else if (!nearBank && isActive) {

					endSession();
				}
			}

			if (isActive) {
				processTick();
			}
		} catch (Exception e) {

			System.err.println("BankStanding error for " + stoner.getUsername() + ": " + e.getMessage());
			e.printStackTrace();
			forceStop();
		}
	}

	private void startSession() {
		isActive = true;
		sessionStartTime = System.currentTimeMillis();
		startLocation = new Location(stoner.getLocation());
		lastXPTime = sessionStartTime;
		lastActivityTime = sessionStartTime;
		standingMinutes = 0;
		totalMovement = 0;
		ticksStanding = 0;
		sessionXPGained = 0;
		sessionBonusXPGained = 0;

		stoner.send(new SendMessage("@gre@You begin bank standing training..."));

	}

	private void endSession() {
		if (isActive) {
			isActive = false;
			long duration = System.currentTimeMillis() - sessionStartTime;
			int minutes = (int) (duration / 60000);
			int seconds = (int) ((duration % 60000) / 1000);

			if (sessionXPGained > 0) {
				stoner.send(new SendMessage("@red@You stop bank standing training after " + minutes + "m " + seconds + "s."));
				stoner.send(new SendMessage("@gre@Total bank standing experience gained: " + sessionXPGained));
			} else {
				stoner.send(new SendMessage("@red@You stop bank standing training after " + minutes + "m " + seconds + "s."));
			}

		}
	}

	private void processTick() {
		ticksStanding++;
		long currentTime = System.currentTimeMillis();

		updateMovementTracking();

		if (ticksStanding % XP_INTERVAL_TICKS == 0) {
			givePassiveXP();
			updateStandingBonus(currentTime);
		}
	}

	private void updateMovementTracking() {
		if (startLocation != null) {
			Location currentLoc = stoner.getLocation();
			int distance = Math.max(
				Math.abs(currentLoc.getX() - startLocation.getX()),
				Math.abs(currentLoc.getY() - startLocation.getY())
			);

			if (distance > totalMovement) {
				totalMovement = distance;

				if (totalMovement > 5) {
					stoner.send(new SendMessage("@red@You've moved too far from your starting position!"));
				}
			}
		}
	}

	private void givePassiveXP() {
		int baseXP = calculatePassiveXP();

		double finalXP = stoner.getProfession().addExperience(BANKSTANDING_SKILL_ID, baseXP);

		sessionXPGained += (int)finalXP;

		lastXPTime = System.currentTimeMillis();
	}

	private int calculatePassiveXP() {
		int xp = BASE_XP;

		int timeBonus = Math.min(standingMinutes, MAX_STANDING_BONUS);
		xp += timeBonus;

		long timeSinceActivity = System.currentTimeMillis() - lastActivityTime;
		if (timeSinceActivity < 60000) {
			xp += ACTIVITY_BONUS;
		}

		if (totalMovement > 3) {
			xp = Math.max(xp / 2, BASE_XP);
		}
		sessionBonusXPGained += xp - BASE_XP;

		return xp;
	}

	private void updateStandingBonus(long currentTime) {
		if (sessionStartTime > 0) {
			int newMinutes = (int) ((currentTime - sessionStartTime) / 60000);
			if (newMinutes > standingMinutes && standingMinutes < MAX_STANDING_BONUS) {
				standingMinutes = newMinutes;

				double currentBonus = (Math.min(standingMinutes, MAX_STANDING_BONUS) * 100.0) / BASE_XP;
				stoner.send(new SendMessage("@blu@Bank standing bonus increased! (+" + String.format("%.1f", currentBonus) + "%)"));
			}
		}
	}

	public void recordActivity() {
		if (isActive) {
			lastActivityTime = System.currentTimeMillis();
		}
	}

	public double getXPMultiplier() {
		if (!isActive) {
			return 1.0;
		}

		double bonus = Math.min(standingMinutes * 0.02, 0.3);
		return BASE_MULTIPLIER + bonus;
	}

	public int applyXPBonus(int skillId, int baseXP) {

		if (skillId == BANKSTANDING_SKILL_ID || !isActive) {
			return baseXP;
		}

		recordActivity();

		double multiplier = getXPMultiplier();

		return (int) (baseXP * multiplier);
	}

	public String getSessionInfo() {
		if (isActive) {
			long duration = System.currentTimeMillis() - sessionStartTime;
			int minutes = (int) (duration / 60000);
			int seconds = (int) ((duration % 60000) / 1000);

			double standingBonus = (Math.min(standingMinutes, MAX_STANDING_BONUS) * 100.0) / BASE_XP;

			long timeSinceActivity = System.currentTimeMillis() - lastActivityTime;
			double activityBonus = 0;
			if (timeSinceActivity < 60000) {
				activityBonus = (ACTIVITY_BONUS * 100.0) / BASE_XP;
			}

			double totalBonusPercent = standingBonus + activityBonus;

			return String.format("Bank Standing: %dm %ds | Bonus: +%.1f%% XP | Bonus XP Gained: %d | Multiplier: %.2fx | Movement: %d tiles",
				minutes, seconds, totalBonusPercent, sessionBonusXPGained, getXPMultiplier(), totalMovement);
		} else if (isNearBank()) {
			return "You are near a bank. Stand still to begin bank standing training!";
		} else {
			return "You are not near a bank. Stand within 2 tiles of a bank booth or banker!";
		}
	}

	private boolean isNearBank() {
		Location playerLoc = stoner.getLocation();

		if (hasNearbyBankObjects(playerLoc)) {
			return true;
		}

		return hasNearbyBankNPCs(playerLoc);
	}

	private boolean hasNearbyBankObjects(Location playerLoc) {
		Region region = Region.getRegion(playerLoc);
		if (region == null) return false;

		RSObject[][][] objects = region.getObjects();
		if (objects == null) return false;

		int playerX = playerLoc.getX();
		int playerY = playerLoc.getY();
		int playerZ = playerLoc.getZ();

		if (playerZ < 0 || playerZ >= objects.length || objects[playerZ] == null) {
			return false;
		}

		int regionAbsX = (region.getId() >> 8) << 6;
		int regionAbsY = (region.getId() & 0xff) << 6;

		int playerLocalX = playerX - regionAbsX;
		int playerLocalY = playerY - regionAbsY;

		int startX = Math.max(0, playerLocalX - BANKSTANDING_RANGE);
		int endX = Math.min(63, playerLocalX + BANKSTANDING_RANGE);
		int startY = Math.max(0, playerLocalY - BANKSTANDING_RANGE);
		int endY = Math.min(63, playerLocalY + BANKSTANDING_RANGE);

		for (int localX = startX; localX <= endX; localX++) {

			if (localX >= 0 && localX < objects[playerZ].length && objects[playerZ][localX] != null) {
				for (int localY = startY; localY <= endY; localY++) {

					if (localY >= 0 && localY < objects[playerZ][localX].length) {
						RSObject obj = objects[playerZ][localX][localY];
						if (obj != null && isBankObject(obj.getId())) {

							int deltaX = Math.abs(playerX - obj.getX());
							int deltaY = Math.abs(playerY - obj.getY());

							if (deltaX <= 2 && deltaY <= 2) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	private boolean hasNearbyBankNPCs(Location playerLoc) {

		Mob[] npcs = World.getNpcs();
		if (npcs == null) {
			return false;
		}

		for (int i = 0; i < npcs.length; i++) {
			Mob npc = npcs[i];
			if (npc != null && npc.isActive() && isBankNPC(npc.getId())) {
				Location npcLoc = npc.getLocation();

				if (npcLoc == null) continue;

				if (npcLoc.getZ() != playerLoc.getZ()) {
					continue;
				}

				int deltaX = Math.abs(playerLoc.getX() - npcLoc.getX());
				int deltaY = Math.abs(playerLoc.getY() - npcLoc.getY());

				if (deltaX <= 2 && deltaY <= 2) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isBankObject(int objectId) {

		for (int bankObjectId : BankStandingConstants.BANK_OBJECT_IDS) {
			if (objectId == bankObjectId) {
				return true;
			}
		}
		return false;
	}

	private static boolean isBankNPC(int npcId) {

		for (int bankNpcId : BankStandingConstants.BANK_NPC_IDS) {
			if (npcId == bankNpcId) {
				return true;
			}
		}
		return false;
	}

	public boolean isActive() {
		return isActive;
	}

	public void forceStop() {
		if (isActive) {
			isActive = false;

			if (sessionXPGained > 0) {
				stoner.send(new SendMessage("@red@Bank standing training interrupted."));
				stoner.send(new SendMessage("@gre@Total bank standing experience gained: " + sessionXPGained));

			} else {
				stoner.send(new SendMessage("@red@Bank standing training interrupted."));
			}
		}
	}

	private void resetSession() {
		isActive = false;
		sessionStartTime = 0;
		startLocation = null;
		lastXPTime = 0;
		lastActivityTime = 0;
		standingMinutes = 0;
		totalMovement = 0;
		ticksStanding = 0;
		sessionXPGained = 0;
		processTicks = 0;
	}

	public void cleanup() {
		if (isActive) {
			forceStop();
		}
		resetSession();

		startLocation = null;
	}

	public static void declare() {

	}
}
