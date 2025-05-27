package com.bestbudz.rs2.content.combat.impl;

import java.util.LinkedList;
import java.util.List;

import com.bestbudz.core.task.Task;
import com.bestbudz.core.task.TaskQueue;
import com.bestbudz.rs2.entity.stoner.Stoner;

public class Skulling {

	public static final short SKULL_TIME = 500;

	private int icon = -1;
	private short left = 0;

	private List<Stoner> assaulted = new LinkedList<Stoner>();

	public void checkForSkulling(Stoner stoner, Stoner assault) {
	if (isSkull(stoner, assault))
		skull(stoner, assault);
	}

	public long getLeft() {
	return left;
	}

	public int getSkullIcon() {
	return icon;
	}

	public boolean hasAssaulted(Stoner p) {
	return assaulted.contains(p);
	}

	public boolean isSkull(Stoner stoner, Stoner assaulting) {
	return (!assaulting.isNpc()) && (stoner.inWilderness()) && (!assaulting.getSkulling().hasAssaulted(stoner));
	}

	public boolean isSkulled() {
	return left > 0;
	}

	public void setLeft(long left) {
	if (left < 0) {
		return;
	}

	if (left > Short.MAX_VALUE) {
		left = SKULL_TIME;
	}

	this.left = (short) left;
	}

	public void setSkullIcon(Stoner stoner, int skullIcon) {
	this.icon = skullIcon;
	stoner.setAppearanceUpdateRequired(true);
	}

	public void skull(Stoner stoner, Stoner assaulting) {
	if (assaulting != null) {
		assaulted.add(assaulting);
	}

	if (left <= 0) {
		left = SKULL_TIME;
		stoner.setAppearanceUpdateRequired(true);
		icon = 0;
	}
	}

	public void tick(final Stoner stoner) {
	TaskQueue.queue(new Task(stoner, 25) {
		@Override
		public void execute() {
		if (!isSkulled()) {
			return;
		}

		if ((left -= 25) <= 0) {
			unskull(stoner);
		}
		}

		@Override
		public void onStop() {
		}
	});
	}

	public void unskull(Stoner stoner) {
	assaulted.clear();
	left = 0;
	icon = -1;
	stoner.setAppearanceUpdateRequired(true);
	}
}
