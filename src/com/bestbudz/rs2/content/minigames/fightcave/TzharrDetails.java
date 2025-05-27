package com.bestbudz.rs2.content.minigames.fightcave;

import com.bestbudz.rs2.entity.mob.Mob;
import com.bestbudz.rs2.entity.stoner.Stoner;
import java.util.ArrayList;
import java.util.List;

public final class TzharrDetails {

	private final List<Mob> mobs = new ArrayList<Mob>();
	private int stage = 0;
	private int z;

	public void addNpc(Mob mob) {
	mobs.add(mob);
	}

	public int getKillAmount() {
	return mobs.size();
	}

	public List<Mob> getMobs() {
	return mobs;
	}

	public int getStage() {
	return stage;
	}

	public void setStage(int stage) {
	this.stage = stage;
	}

	public int getZ() {
	return z;
	}

	public void setZ(Stoner p) {
	z = (p.getIndex() * 4);
	}

	public void increaseStage() {
	stage += 1;
	}

	public boolean removeNpc(Mob mob) {
	int index = mobs.indexOf(mob);

	if (index == -1) {
		return false;
	}

	mobs.remove(mob);
	return true;
	}

	public void reset() {
	stage = 0;
	}

}
