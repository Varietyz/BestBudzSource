package com.bestbudz.rs2.content.wilderness;

import java.math.BigInteger;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendConfig;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendStonerHint;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class TargetSystem {

	/**
	 * The class instance.
	 */
	private static final TargetSystem instance = new TargetSystem();

	/**
	 * Returns a visible encapsulation of the class instance.
	 * 
	 * @return The returned encapsulated instance.
	 */
	public final static TargetSystem getInstance() {
	return instance;
	}

	/**
	 * Manages Assigning the Target to the user if they meet the requirements to
	 * receive one.
	 * 
	 * @param stoner
	 */
	public void assignTarget(Stoner stoner) {
	for (Stoner stoners : World.getStoners()) {
		if (stoners != null && stoners.isActive()) {
			Stoner p = stoners;
			if (p.inWilderness() && stoner.inWilderness() && p.isActive() && stoner.isActive() && p != stoner) {
				if (p.getClient().getHostId() == stoner.getClient().getHostId()) {
					return;
				}
				if (p.isDead() || stoner.isDead()) {
					return;
				}
				if (stonerHasTarget(p) || stonerHasTarget(stoner)) {
					return;
				}
				if (StonerKilling.hostOnList(p, stoner.getClient().getHost()) || StonerKilling.hostOnList(stoner, p.getClient().getHost())) {
					return;
				}
				if (!inCombatRange(stoner, p)) {
					return;
				}
				setTarget(stoner, p.getIndex(), p.getUsername());
				setTarget(p, stoner.getIndex(), stoner.getUsername());
			}
		}
	}
	}

	/**
	 * Deterquarrys if the chosen user is applicable combat grade for the other user
	 * to be assigned a target to.
	 * 
	 * @param stoner
	 * @param target
	 * @return
	 */
	public boolean inCombatRange(Stoner stoner, Stoner target) {
	if (Math.abs(stoner.getProfession().getCombatGrade() - target.getProfession().getCombatGrade()) > 10) {
		return false;
	}
	return true;
	}

	/**
	 * Deterquarrys if the users target is null.
	 * 
	 * @param targetName
	 * @return
	 */
	public boolean isNull(String targetName) {
	for (Stoner p : World.getStoners())
		if (p != null && p.getUsername().equalsIgnoreCase(targetName))
			return false;
	return true;
	}

	/**
	 * Returns whether or not the user has a target.
	 * 
	 * @param stoner
	 * @return
	 */
	public boolean stonerHasTarget(Stoner stoner) {
	return stoner.targetIndex != 0 && (stoner.targetName != "None" || stoner.targetName != null);
	}

	/**
	 * Manages resetting the users target for whatever reason may be called for.
	 * 
	 * Ex: Logging out, Dying etc.
	 * 
	 * @param stoner
	 */
	public void resetTarget(Stoner stoner, boolean logout) {
	Stoner target = World.getStoners()[stoner.targetIndex];
	if (target == null || stoner == null) {
		return;
	}
	target.targetIndex = 0;
	target.targetName = "None";
	if (logout) {
		target.getClient().queueOutgoingPacket(new SendMessage("@dre@Your target has left the wilderness. You will be assigned a new one shortly."));
		if (target.inWilderness()) {
			if (stoner.getAttributes().get("gainTarget") == null) {
				Task task = new GainTarget(stoner, (byte) 1);
				stoner.getAttributes().set("gainTarget", task);
				TaskQueue.queue(task);
			}
		}
	}
	target.getClient().queueOutgoingPacket(new SendStonerHint(true, -1));
	target.getClient().queueOutgoingPacket(new SendString("None", 23307));
	stoner.targetIndex = 0;
	stoner.targetName = "None";
	stoner.getClient().queueOutgoingPacket(new SendStonerHint(true, -1));
	}

	/**
	 * Manages setting the target to the stoner.
	 * 
	 * @param stoner
	 * @param targetStonerId
	 * @param targetName
	 */
	public void setTarget(Stoner stoner, int targetStonerId, String targetName) {
	stoner.targetIndex = targetStonerId;
	stoner.targetName = targetName;
	stoner.getClient().queueOutgoingPacket(new SendMessage("@dre@You have been assigned the user " + "'" + targetName + "'" + " as your target!"));
	if (World.getStoners()[targetStonerId] != null) {
		stoner.getClient().queueOutgoingPacket(new SendStonerHint(true, stoner.targetIndex));
	}
	}

	/**
	 * Updates the interfaces
	 * 
	 * @param stoner
	 */
	public void update(Stoner stoner) {
	if (!stoner.inWilderness()) {
		return;
	}
	stoner.send(new SendString(stoner.getRogueKills() + "", 23310));
	stoner.send(new SendString(stoner.getRogueRecord() + "", 23311));
	stoner.send(new SendString(stoner.getHunterKills() + "", 23312));
	stoner.send(new SendString(stoner.getHunterRecord() + "", 23313));
	stoner.send(new SendString(stoner.targetIndex == 0 ? "None" : stoner.targetName, 23307));
	stoner.send(new SendString(calculateTargetWealth(stoner), 23305));
	stoner.send(new SendString(getTargetInformation(stoner), 23308));
	stoner.send(new SendString("@yel@Grade: " + stoner.getWildernessGrade(), 199));
	}

	public String calculateTargetWealth(Stoner stoner) {
	if (stoner.targetIndex == 0) {
		return "";
	}

	Stoner target = World.getStoners()[stoner.targetIndex];
	BigInteger carried_wealth = target.getBox().getContainerNet().add(target.getEquipment().getContainerNet());

	if (carried_wealth.intValue() > 10_000_000) {
		stoner.send(new SendConfig(881, 1));
		return "Wealth: V. High";
	} else if ((carried_wealth.intValue() >= 1_000_000) && (carried_wealth.intValue() < 10_000_000)) {
		stoner.send(new SendConfig(880, 1));
		return "Wealth: High";
	} else if ((carried_wealth.intValue() >= 250_000) && (carried_wealth.intValue() < 1_000_000)) {
		stoner.send(new SendConfig(879, 1));
		return "Wealth: Medium";
	} else if ((carried_wealth.intValue() >= 50_000) && (carried_wealth.intValue() < 250_000)) {
		stoner.send(new SendConfig(878, 1));
		return "Wealth: Low";
	} else {
		stoner.send(new SendConfig(877, 1));
		return "Wealth: V. Low";
	}
	}

	public String getTargetInformation(Stoner stoner) {
	if (stoner.targetIndex == 0) {
		return "Grade: -----";
	}

	Stoner target = World.getStoners()[stoner.targetIndex];

	String location = "Safe";
	String color = "@gr2@";

	if (target.inWilderness()) {
		int grade = target.getWildernessGrade();
		location = "Lvl " + (grade <= 3 ? "1" : grade - 3) + "-" + (grade + 3);

		if ((target.getProfession().getCombatGrade() > (stoner.getProfession().getCombatGrade() + target.getWildernessGrade())) || (stoner.getProfession().getCombatGrade() > (target.getProfession().getCombatGrade() + target.getWildernessGrade()))) {
			color = "@red@";
		}
	}

	location += ", Cmb " + target.getProfession().getCombatGrade();
	return color + location;
	}

}