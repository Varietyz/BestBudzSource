package com.bestbudz.rs2.content.profession.mercenary;

import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import java.util.HashMap;
import java.util.Map;

public class Mercenary {

	private final Stoner p;

	private String task = null;
	private byte amount = 0;
	private MercenaryDifficulty current = null;

	private final Map<String, Integer> killTracker = new HashMap<>();
	private int totalKills = 0;
	private String bossTask = null;
	private int bossTaskKills = 0;
	private static final int NORMAL_TASK_TARGET = 50;
	private static final int BOSS_TASK_TARGET = 10;
	private static final int KILLS_PER_POINT_REWARD = 25;
	private static final int KILLS_PER_EXP_BONUS = 10;

	public Mercenary(Stoner p) {
		this.p = p;
		assignAutoTasks();
	}

	public static boolean isMercenaryTask(Stoner p, Mob mob) {
		String mobName = mob.getDefinition().getName().toLowerCase();
		Mercenary merc = p.getMercenary();

		return (merc.task != null && mobName.contains(merc.task.toLowerCase())) ||
			(merc.bossTask != null && mobName.contains(merc.bossTask.toLowerCase()));
	}

	public void addMercenaryExperience(double am) {
		p.getProfession().addExperience(18, am);
	}

	public void assign(MercenaryDifficulty diff) {
		assignAutoTasks();
	}

	public void checkForMercenary(Mob killed) {
		if (!isValidTarget(killed)) {
			return;
		}

		String mobName = killed.getDefinition().getName().toLowerCase();
		NpcDefinition def = killed.getDefinition();

		killTracker.put(mobName, killTracker.getOrDefault(mobName, 0) + 1);
		totalKills++;

		double baseExp = def.getGrade() * 2;
		addMercenaryExperience(baseExp);

		processMilestoneRewards();

		processAutoTasks(killed, baseExp);

	}

	private void processMilestoneRewards() {
		if (totalKills % KILLS_PER_POINT_REWARD == 0) {
			int points = calculatePointReward();
			p.addMercenaryPoints(points);
			p.getClient().queueOutgoingPacket(new SendMessage("<col=075D78>Milestone! +" + points + " points (" + totalKills + " kills)"));
		}

		if (totalKills % KILLS_PER_EXP_BONUS == 0) {
			addMercenaryExperience(500);
			p.getClient().queueOutgoingPacket(new SendMessage("<col=32CD32>Kill streak bonus: +50 exp!"));
		}
	}

	private void processAutoTasks(Mob killed, double baseExp) {
		String mobName = killed.getDefinition().getName().toLowerCase();

		if (task != null && mobName.contains(task.toLowerCase())) {
			amount++;
			addMercenaryExperience(baseExp * 1.5);

			if (amount >= NORMAL_TASK_TARGET) {
				completeNormalTask();
				AchievementHandler.activateAchievement(p, AchievementList.COMPLETE_10_MERCENARY_TASKS, 1);
				AchievementHandler.activateAchievement(p, AchievementList.COMPLETE_100_MERCENARY_TASKS, 1);
			} else {
				p.getClient().queueOutgoingPacket(new SendMessage("<col=FFD700>Normal task: " + amount + "/" + NORMAL_TASK_TARGET + " " + task));
			}
		}

		if (bossTask != null && mobName.contains(bossTask.toLowerCase())) {
			bossTaskKills++;
			addMercenaryExperience(baseExp * 4.0);

			if (bossTaskKills >= BOSS_TASK_TARGET) {
				completeBossTask();
			} else {
				p.getClient().queueOutgoingPacket(new SendMessage("<col=FF6347>Boss task: " + bossTaskKills + "/" + BOSS_TASK_TARGET + " " + bossTask));
			}
		}
	}

	private void assignAutoTasks() {
		task = MercenaryTasks.getRandomNormalTask();
		amount = 0;
		current = MercenaryDifficulty.LOW;

		bossTask = MercenaryTasks.getRandomBossTask();
		bossTaskKills = 0;

		p.getClient().queueOutgoingPacket(new SendMessage("<col=87CEEB>Auto-mercenary tasks assigned!"));
		p.getClient().queueOutgoingPacket(new SendMessage("<col=FFD700>Normal: Kill " + NORMAL_TASK_TARGET + " " + task + "s"));
		p.getClient().queueOutgoingPacket(new SendMessage("<col=FF6347>Boss: Kill " + BOSS_TASK_TARGET + " " + bossTask + "s"));
	}

	private void completeNormalTask() {
		addMercenaryExperience(200);
		p.addMercenaryPoints(3);
		p.getClient().queueOutgoingPacket(new SendMessage("<col=00FF00>Normal task completed! +200 exp, +3 points"));

		task = MercenaryTasks.getRandomNormalTask();
		amount = 0;
		p.getClient().queueOutgoingPacket(new SendMessage("<col=FFD700>New normal task: Kill " + NORMAL_TASK_TARGET + " " + task + "s"));
	}

	private void completeBossTask() {
		addMercenaryExperience(500);
		p.addMercenaryPoints(10);
		p.getClient().queueOutgoingPacket(new SendMessage("<col=00FF00>Boss task completed! +500 exp, +10 points"));

		bossTask = MercenaryTasks.getRandomBossTask();
		bossTaskKills = 0;
		p.getClient().queueOutgoingPacket(new SendMessage("<col=FF6347>New boss task: Kill " + BOSS_TASK_TARGET + " " + bossTask + "s"));
	}

	private int calculatePointReward() {
		if (totalKills < 100) return 1;
		if (totalKills < 500) return 2;
		if (totalKills < 1000) return 3;
		return 5;
	}

	private boolean isValidTarget(Mob mob) {
		return mob != null &&
			mob.getDefinition() != null &&
			mob.getDefinition().isAssaultable() &&
			mob.getDefinition().getName() != null;
	}

	public byte getAmount() {
		return amount;
	}

	public void setAmount(byte amount) {
		this.amount = amount;
	}

	public MercenaryDifficulty getCurrent() {
		return current;
	}

	public void setCurrent(MercenaryDifficulty current) {
		this.current = current;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public boolean hasTask() {
		return task != null;
	}

	public void reset() {
		assignAutoTasks();
	}

	public enum MercenaryDifficulty {
		LOW,
		BOSS
	}
}