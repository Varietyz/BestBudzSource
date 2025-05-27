package com.bestbudz.rs2.content.dialogue;

import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class Dialogue {
	protected int next = 0;
	protected int option;
	protected Stoner stoner;

	public abstract boolean clickButton(int id);

	public void end() {
	next = -1;
	}

	public abstract void execute();

	public int getNext() {
	return next;
	}

	public int getOption() {
	return option;
	}

	public Stoner getStoner() {
	return stoner;
	}

	public void setNext(int next) {
	this.next = next;
	}

	public void setOption(int option) {
	this.option = option;
	}

	public void setStoner(Stoner stoner) {
	this.stoner = stoner;
	}
}
