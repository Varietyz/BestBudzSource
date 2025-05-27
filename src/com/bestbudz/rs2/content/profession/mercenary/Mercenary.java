package com.bestbudz.rs2.content.profession.mercenary;

import com.bestbudz.core.definitions.NpcDefinition;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.achievements.AchievementHandler;
import com.bestbudz.rs2.content.achievements.AchievementList;
import com.bestbudz.rs2.content.dialogue.DialogueManager;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;

public class Mercenary {

	private final Stoner p;
	private String task = null;
	private byte amount = 0;
	private MercenaryDifficulty current = null;
	private Stoner partner = null;

	public Mercenary(Stoner p) {
	this.p = p;
	}

	public static boolean isMercenaryTask(Stoner p, Mob mob) {
	return (p.getMercenary().getTask() != null) && (mob.getDefinition().getName().toLowerCase().contains(p.getMercenary().getTask().toLowerCase()));
	}

	public static boolean isMercenaryTask(Stoner p, String other) {
	return (p.getMercenary().getTask() != null) && (other.toLowerCase().contains(p.getMercenary().getTask().toLowerCase()));
	}

	public void addMercenaryExperience(double am) {
	p.getProfession().addExperience(18, am);
	}

	public void assign(MercenaryDifficulty diff) {
	switch (diff) {
	case LOW:
		MercenaryTasks.LowGrade[] lval = MercenaryTasks.LowGrade.values();

		MercenaryTasks.LowGrade set = lval[Utility.randomNumber(lval.length)];

		while (p.getMaxGrades()[18] < set.lvl) {
			set = lval[Utility.randomNumber(lval.length)];
		}

		task = set.name;

		amount = ((byte) (30 + Utility.randomNumber(25)));
		current = MercenaryDifficulty.LOW;
		break;
	case MEDIUM:
		MercenaryTasks.MediumGrade[] mval = MercenaryTasks.MediumGrade.values();

		MercenaryTasks.MediumGrade set2 = mval[Utility.randomNumber(mval.length)];

		while (p.getMaxGrades()[18] < set2.lvl) {
			set2 = mval[Utility.randomNumber(mval.length)];
		}

		task = set2.name;

		amount = ((byte) (30 + Utility.randomNumber(25)));
		current = MercenaryDifficulty.MEDIUM;
		break;
	case HIGH:
		MercenaryTasks.HighGrade[] hval = MercenaryTasks.HighGrade.values();

		MercenaryTasks.HighGrade set3 = hval[Utility.randomNumber(hval.length)];

		while (p.getMaxGrades()[18] < set3.lvl) {
			set3 = hval[Utility.randomNumber(hval.length)];
		}

		task = set3.name;

		amount = ((byte) (20 + Utility.randomNumber(25)));
		current = MercenaryDifficulty.HIGH;
		break;
	case BOSS:
		MercenaryTasks.BossGrade[] bval = MercenaryTasks.BossGrade.values();

		MercenaryTasks.BossGrade set4 = bval[Utility.randomNumber(bval.length)];

		while (p.getMaxGrades()[18] < set4.lvl) {
			set4 = bval[Utility.randomNumber(bval.length)];
		}

		task = set4.name;

		amount = ((byte) (20 + Utility.randomNumber(25)));
		current = MercenaryDifficulty.BOSS;
		break;
	default:
		throw new IllegalArgumentException("(Mercenary.java) The world is going to end");
	}
	}

	public void checkForMercenary(Mob killed) {
	if ((partner != null) && ((task == null) || ((partner.getMercenary().hasTask()) && (!isMercenaryTask(partner, task)))) && (partner.getLocation().isViewableFrom(p.getLocation())) && (partner.getMercenary().hasTask()) && (isMercenaryTask(partner, killed.getDefinition().getName()))) {
		partner.getProfession().addExperience(18, killed.getDefinition().getGrade() * 2 / 4);
	}

	if (task == null) {
		return;
	}

	NpcDefinition def = killed.getDefinition();

	if (isMercenaryTask(p, killed)) {
		amount = ((byte) (amount - 1));
		double exp = def.getGrade() * 2;

		addMercenaryExperience(exp);
		doSocialMercenaryExperience(killed, exp);

		if (amount == 0) {

			task = null;
			addMercenaryExperience(def.getGrade() * 35);
			p.getClient().queueOutgoingPacket(new SendMessage("<col=075D78>You have completed your Mercenary task; contact mercenary master for another."));
			p.addMercenaryPoints(amount);
			AchievementHandler.activateAchievement(p, AchievementList.COMPLETE_10_MERCENARY_TASKS, 1);
			AchievementHandler.activateAchievement(p, AchievementList.COMPLETE_100_MERCENARY_TASKS, 1);
			if (current != null) {
				p.addMercenaryPoints(current == MercenaryDifficulty.BOSS ? 20 : current == MercenaryDifficulty.LOW ? 5 : current == MercenaryDifficulty.MEDIUM ? 8 : current == MercenaryDifficulty.HIGH ? 10 : 0);
			}

		} else {
		}
	}
	}

	public void doSocialMercenaryExperience(Mob killed, double am) {
	if (partner != null) {
		Stoner other = partner;

		if ((other.getMercenary().getPartner() == null) || (!other.getMercenary().getPartner().equals(p))) {
			return;
		}

		if (!other.isActive()) {
			DialogueManager.sendStatement(p, "Your social mercenary partner is not online.");
			partner = null;
		} else if ((other.getLocation().isViewableFrom(p.getLocation())) && ((other.getMercenary().hasTask()) || (isMercenaryTask(other, task)))) {
			other.getProfession().addExperience(18, am / 2.0D);
		}
	}
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

	public Stoner getPartner() {
	return partner;
	}

	public String getPartnerName() {
	return partner != null ? partner.getUsername() : null;
	}

	public String getTask() {
	return task;
	}

	public void setTask(String task) {
	this.task = task;
	}

	public boolean hasMercenaryPartner() {
	return partner != null;
	}

	public boolean hasTask() {
	return (amount > 0) && (task != null);
	}

	public void reset() {
	task = null;
	amount = 0;
	current = null;
	}

	public void setSocialMercenaryPartner(String name) {
	if (name.equalsIgnoreCase(p.getUsername())) {
		DialogueManager.sendStatement(p, "You may not set your partner as yourself!");
		return;
	}

	Stoner other = World.getStonerByName(name);

	if (other == null) {
		DialogueManager.sendStatement(p, "It seems '" + name + "' doesn't exist or isn't online.");
		return;
	}

	if (other.getMercenary().hasMercenaryPartner()) {
		DialogueManager.sendStatement(p, name + " already has a mercenary partner!");
		return;
	}

	DialogueManager.sendStatement(p, "You have successfully set " + name + " as your partner.");
	partner = other;
	DialogueManager.sendStatement(other, "You have been set as " + p.getUsername() + "'s mercenary partner.");
	}

	public enum MercenaryDifficulty {
		LOW,
		MEDIUM,
		HIGH,
		BOSS
	}
}
