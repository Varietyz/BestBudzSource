package com.bestbudz.core.discord.stonerbot.handling.decisions;

public class DecisionState {

	private volatile long lastQuarryingSession = 0;
	private volatile long lastLumberingSession = 0;
	private volatile long lastEmoteTime = 0;
	private volatile long lastStatusTime = 0;
	private volatile long lastCombatSession = 0;
	private volatile long lastCombatAreaMoveTime = 0;
	private volatile long lastHomeReturnTime = 0;
	private volatile long lastExploreTime = 0;

	private volatile String lastActivity = "idle";
	private volatile boolean combatSessionActive = false;
	private volatile int combatSessionsWithoutReturn = 0;
	private volatile boolean forceReturnHome = false;

	private volatile boolean isEmoting = false;
	private volatile long emoteStartTime = 0;

	public long getLastQuarryingSession() { return lastQuarryingSession; }
	public void setLastQuarryingSession(long time) { this.lastQuarryingSession = time; }

	public long getLastLumberingSession() { return lastLumberingSession; }
	public void setLastLumberingSession(long time) { this.lastLumberingSession = time; }

	public long getLastEmoteTime() { return lastEmoteTime; }
	public void setLastEmoteTime(long time) { this.lastEmoteTime = time; }

	public long getLastStatusTime() { return lastStatusTime; }
	public void setLastStatusTime(long time) { this.lastStatusTime = time; }

	public long getLastCombatSession() { return lastCombatSession; }
	public void setLastCombatSession(long time) { this.lastCombatSession = time; }

	public long getLastCombatAreaMoveTime() { return lastCombatAreaMoveTime; }
	public void setLastCombatAreaMoveTime(long time) { this.lastCombatAreaMoveTime = time; }

	public long getLastHomeReturnTime() { return lastHomeReturnTime; }
	public void setLastHomeReturnTime(long time) { this.lastHomeReturnTime = time; }

	public long getLastExploreTime() { return lastExploreTime; }
	public void setLastExploreTime(long time) { this.lastExploreTime = time; }

	public String getLastActivity() { return lastActivity; }
	public void setLastActivity(String activity) { this.lastActivity = activity; }

	public boolean isCombatSessionActive() { return combatSessionActive; }
	public void setCombatSessionActive(boolean active) { this.combatSessionActive = active; }

	public int getCombatSessionsWithoutReturn() { return combatSessionsWithoutReturn; }
	public void setCombatSessionsWithoutReturn(int count) { this.combatSessionsWithoutReturn = count; }
	public void incrementCombatSessionsWithoutReturn() { this.combatSessionsWithoutReturn++; }
	public void resetCombatSessionsWithoutReturn() { this.combatSessionsWithoutReturn = 0; }

	public boolean isForceReturnHome() { return forceReturnHome; }
	public void setForceReturnHome(boolean force) { this.forceReturnHome = force; }

	public boolean isEmoting() { return isEmoting; }
	public void setEmoting(boolean emoting) { this.isEmoting = emoting; }

	public long getEmoteStartTime() { return emoteStartTime; }
	public void setEmoteStartTime(long time) { this.emoteStartTime = time; }

	public void resetHomeTracking() {
		lastHomeReturnTime = System.currentTimeMillis();
		combatSessionsWithoutReturn = 0;
		forceReturnHome = false;
	}

	public String getStats() {
		long currentTime = System.currentTimeMillis();
		return String.format(
			"Last quarrying: %ds ago, Last lumbering: %ds ago, Last combat: %ds ago, " +
				"Combat sessions without return: %d, Last activity: %s",
			(currentTime - lastQuarryingSession) / 1000,
			(currentTime - lastLumberingSession) / 1000,
			(currentTime - lastCombatSession) / 1000,
			combatSessionsWithoutReturn,
			lastActivity
		);
	}
}
