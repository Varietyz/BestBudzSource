package com.bestbudz.core.util;

import com.bestbudz.rs2.entity.World;
import com.bestbudz.rs2.entity.mob.Mob;

/**
 * A updateable mob
 * 
 * @author Jaybane
 * 
 */
public class UpdateableMob {
	/**
	 * The amount of stoners viewing this mob
	 */
	protected int viewed = 1;

	/**
	 * The mob
	 */
	protected final short mob;

	/**
	 * Creates a new (@UpdateableMob)
	 * 
	 * @param mob
	 */
	public UpdateableMob(Mob mob) {
	this.mob = (short) mob.getIndex();
	}

	/**
	 * WARNING: unchecked cast
	 */
	@Override
	public boolean equals(Object o) {
	return this.mob == ((UpdateableMob) o).mob;
	}

	/**
	 * Gets the mob
	 */
	public Mob getMob() {
	return World.getNpcs()[mob];
	}
}
