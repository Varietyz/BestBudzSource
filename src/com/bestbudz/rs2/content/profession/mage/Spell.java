package com.bestbudz.rs2.content.profession.mage;

//import com.bestbudz.rs2.entity.item.Item;
import com.bestbudz.rs2.entity.stoner.Stoner;

public abstract class Spell {

	public abstract boolean execute(Stoner paramStoner);

	public abstract double getExperience();

	public abstract int getGrade();

	public abstract String getName();

	// public abstract Item[] getRunes();
}
