package com.bestbudz.rs2.content.wilderness;

import com.bestbudz.rs2.entity.stoner.Stoner;

public class StonerKilling {

	/**
	 * Adds the host of the killed stoner.
	 *
	 * @param stoner
	 *                   Stoner that saves the host.
	 * @param host
	 *                   Host address of the killed stoner.
	 * @return True if the host is added to the stoners array.
	 */

	public static boolean addHostToList(Stoner stoner, String host) {
	if (stoner != null && stoner.getLastKilledStoners() != null) {
		return stoner.getLastKilledStoners().add(host);
	}
	return false;
	}

	/**
	 * Checks if the host is already on the stoners array.
	 * 
	 * @param stoner
	 *                   Stoner that is adding the killed stoners host.
	 * @param host
	 *                   Host address of the killed stoner.
	 * @return True if the host is on the stoners array.
	 */

	public static boolean hostOnList(Stoner stoner, String host) {
	if (stoner != null && stoner.getLastKilledStoners() != null) {
		if (stoner.getLastKilledStoners().lastIndexOf(host) >= 3) {
			removeHostFromList(stoner, host);
			return false;
		}
		return stoner.getLastKilledStoners().contains(host);
	}
	return false;
	}

	/**
	 * Removes the host from the stoners array.
	 * 
	 * @param stoner
	 *                   Stoner that is removing the host.
	 * @param host
	 *                   Host that is being removed.
	 * @return True if host is successfully removed.
	 */

	public static boolean removeHostFromList(Stoner stoner, String host) {
	if (stoner != null && stoner.getLastKilledStoners() != null) {
		return stoner.getLastKilledStoners().remove(host);
	}
	return false;
	}

}